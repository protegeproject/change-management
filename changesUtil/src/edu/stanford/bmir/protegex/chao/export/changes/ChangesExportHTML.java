package edu.stanford.bmir.protegex.chao.export.changes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.bmir.protegex.chao.annotation.api.OntologyJavaMapping;
import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyComponentFactory;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultTimestamp;
import edu.stanford.bmir.protegex.chao.util.ChangeDateComparator;
import edu.stanford.smi.protege.code.generator.wrapping.AbstractWrappedInstance;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;

/**
 * Exports the changes from the Changes and Annotation ontology (ChAO) to HTML and CSV
 * file. 
 * The script will generate in the output folder:
 * - (1) The HTML files containing the changes after the max date (one HTML file per ontology entity)
 * - (2) A file "exportedChanges.csv" that contains a tabular format of all the exported changes (change desc, author, timestamp, change inst name)
 * - (3) A file "entitiesToDelete" prefixed with "yyyy-MM-dd.HHmmss" that contains a list of entity ids (changes and timestamps) that should be 
 *       deleted from ChAO. They include changes and their timestamps that have been included in this export, but also changes and their
 *       timestamps that are before the max date (E.g. subchanges, or invalid changes that are not part of the HTML and CSV export)
 * - (4) An "export.metadata" file that contains the max date and the time of the export.
 * 
 * Notes: 
 * This script is supposed to be run on the same folder at different dates (e.g., once yearly). The script assumes
 * that the changes that have been exported, have also been deleted from ChAO. Even if not, a future run of the script will not include
 * again in the HTML a change instance that already exists in the file. The CSV file will not make this distinction and may 
 * include duplicates. If exported changes are deleted after each run of the script, as intended, this situation will not
 * occur.
 * 
 * @author ttania
 *
 */
public class ChangesExportHTML {

	public static final String FILE_NAME_REPLACE_REGEX = "[\\/:\"*?<>|#]+";
	public static final String FILE_NAME_REPLACE_WITH = "_";
	
	public static final String CSV_SEPARATOR = "\t";
	public static final String CSV_QUOTE_CHAR = "\"";
	public static final SimpleDateFormat CSV_FILE_PREFIX = new SimpleDateFormat("yyyy-MM-dd.HHmmss");
	
	public static final String METADATA_FILE = "export.metadata";
	public static final String ENTITIES_TO_DELETE_FILE = "entitiesToDelete.csv";
	public static final String EXPORTED_CHANGES_CSV_FILE = "exportedChanges.csv";
	
	public static final String STYLE_CSS = "style.css";

	private static Logger log = Logger.getLogger(ChangesExportHTML.class.getName());

	private ProjectChangeFilter changeFilter = new DefaultChangesFilter();

	private KnowledgeBase kb;
	private KnowledgeBase chaoKb;

	private File exportHMTLDir;
	private boolean append;
	private Date maxDate;

	private BufferedWriter toDeleteWriter;
	private BufferedWriter csvWriter;

	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
		   	 log.severe("(1) Path to ChAO file or project URI, " + 
						"(2) Path to export HTML folder, "
					  + "(3) Append to existing HTML files [true|false],"
					  + "(4) Date up to which to export changes, date format: MM/dd/yyyy HH:mm:ss zzz"
					  + "(5) Optional: Path to main pprj file to export entity browser text.");

			System.exit(1);
		}

		initJavaMappings();

		log.info("Loading ChAO from: " + args[0]);
		//workaround for pprj loading bug! Params are hard coded
		//KnowledgeBase chaoKB = loadKB(args[0]);
		KnowledgeBase chaoKB = loadChAOKb("icd_ann");
		
		if (chaoKB == null) {
			log.severe("Could not load project from: " + args[0]);
			System.exit(1);
		}
		
		KnowledgeBase kb = null;
		if (args.length == 5) {
			kb = loadKB(args[4]);
		}
		
		ChangesExportHTML exporter = new ChangesExportHTML(chaoKB, kb);
		exporter.setExportHMTLDir(new File(args[1]));
		exporter.setAppend(Boolean.parseBoolean(args[2]));
		exporter.setMaxDate(DefaultTimestamp.getDateParsed(args[3]));

		log.info("Started ChAO to HTML export on " + new Date());

		exporter.exportChanges();
		exporter.exportMetadata(args[1]);

		log.info("Ended ChAO to HTML export on " + new Date());
	}

	private static void initJavaMappings() {
		OntologyJavaMapping.initMap();
		edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyJavaMapping.initMap();
		edu.stanford.bmir.protegex.chao.change.api.OntologyJavaMapping.initMap();
	}

	@SuppressWarnings("rawtypes")
	private static KnowledgeBase loadKB(String pprjPath) {
		ArrayList errors = new ArrayList();

		Project prj = Project.loadProjectFromFile(pprjPath, errors);
		KnowledgeBase kb = prj.getKnowledgeBase();

		if (errors.size() > 0) {
			log.warning("There were errors at loading project " + pprjPath);
			for (Iterator iterator = errors.iterator(); iterator.hasNext();) {
				Object object = iterator.next();
				log.warning(object.toString());
			}
		}

		return kb;
	}

    private static KnowledgeBase loadChAOKb(String tableName) {
        DatabaseKnowledgeBaseFactory factory = new DatabaseKnowledgeBaseFactory();
        ArrayList errors = new ArrayList();
        Project prj = Project.createNewProject(factory, errors);
        DatabaseKnowledgeBaseFactory.setSources(prj.getSources(), "com.mysql.jdbc.Driver",
        		"jdbc:mysql://localhost:3306/protege", tableName, "protege", "protege");
        prj.createDomainKnowledgeBase(factory, errors, true);
        return prj.getKnowledgeBase();
    }
	
	public ChangesExportHTML(KnowledgeBase chaoKb, KnowledgeBase kb) {
		this.chaoKb = chaoKb;
		this.kb = kb;
	}

	public void setExportHMTLDir(File exportHMTLDir) throws IOException {
		if (exportHMTLDir.exists() == false) {
			exportHMTLDir.mkdir();
		}
		this.exportHMTLDir = exportHMTLDir;
		
		File styleFile = new File(exportHMTLDir.getAbsolutePath() + "/style.css");
		if (styleFile.exists()) {
			styleFile.delete();
		}
		
		//copy the styles.css file
		try {
			URI sourceURL = ChangesExportHTML.class.getClassLoader().
				getResource("edu/stanford/bmir/protegex/chao/export/changes/style.css").toURI();
			Files.copy(new File(sourceURL).toPath(), styleFile.toPath());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not copy styles.css file.", e);
		}
	}


	private void initWriters() throws IOException {
		initToDeleteWriter();
		initCSVWriter();
	}

	public void setAppend(boolean append) {
		this.append = append;
	}
	
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public void exportChanges() throws IOException {
		initWriters();
		
		Collection<Ontology_Component> ocs = new OntologyComponentFactory(chaoKb).getAllOntology_ComponentObjects(true);

		int i = 0;
		for (Ontology_Component oc : ocs) {
			try {
				exportChanges(oc);
				i++;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exceptions at export" , e);
			}

			if (i % 100 == 0) {
				log.info("Exported changes for " + i + " entities.");
			}
		}
		
		closeWriters();
		deleteTmpFile();
	}




	/**
	 * @param oc
	 * @throws IOException
	 */
	private void exportChanges(Ontology_Component oc) throws IOException {
		
		Collection<Change> changes = oc.getChanges();
		List<Change> filteredChanges = filterChanges(changes);
		
		Collections.sort(filteredChanges, new ChangeDateComparator(chaoKb));
		Collections.reverse(filteredChanges);
		
		printToDeleteEntities(changes);
		
		if (filteredChanges.size() == 0) { //don't export entities with no changes
			return;
		}
		
		File ocFile = getFile(oc.getCurrentName());
		boolean readExistingOCFile = append && ocFile.exists();
		String existingOCFileContent = readExistingOCFile ? readExistingFile(ocFile) : "";
		
		//write to tmp file, so that we can insert the new changes at the beginning
		//of the old file, and rename the file later.
		
		File tmpFile = new File(exportHMTLDir, "tmp");
		FileWriter tmpWriter = new FileWriter(tmpFile);
		
		exportHeader(tmpWriter, oc);
		printExportDate(tmpWriter);
		
		for (Change change : filteredChanges) {
			String changeInstName = ((AbstractWrappedInstance)change).getName();
			
			if (existingOCFileContent.contains(changeInstName)) {
				log.warning(ocFile + " already contains change: " + changeInstName );
			} else {
				tmpWriter.write("<tr> " + "<!-- " + changeInstName + " -->\n");
				tmpWriter.write(getChangeRow(change));
				tmpWriter.write("</tr>\n");
			}
			
			exportCSVRow(change);
		}
		
		if (readExistingOCFile == true) {
			tmpWriter.write(existingOCFileContent);
		}
		
		exportFooter(tmpWriter);
		
		tmpWriter.close();
		
		renameTmpToOCFile(tmpFile,ocFile);
	}


	private void renameTmpToOCFile(File tmpFile, File ocFile) {
		Path ocPath = new File(ocFile.getAbsolutePath()).toPath();
		
		if (ocFile.exists() == true) {
			try {
				Files.delete(ocFile.toPath());
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not delete file: " + ocFile, e);
			}
		}
		
		try {
			Files.copy(tmpFile.toPath(), ocPath);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Could not copy file: " + tmpFile + " to " + ocPath, e);
		}
	}

	private void printExportDate(FileWriter w) throws IOException {
		w.write("\n<!-- *************** Changes up to date: " + DefaultTimestamp.DATE_FORMAT.format(maxDate) +
				" ***** Exported on " + DefaultTimestamp.DATE_FORMAT.format(new Date()) + 
				" *************** -->\n\n");
	}

	private List<Change> filterChanges(Collection<Change> changes) {
		List<Change> filteredChanges = new ArrayList<>();
		for (Change change : changes) {
			if (isIncluded(change) == true) {
				filteredChanges.add(change);
			}
		}
		return filteredChanges;
	}


	private String getChangeRow(Change change) {
		StringBuffer text = new StringBuffer();

		text.append("<td>");
		text.append(change.getContext());
		text.append("</td> ");

		text.append("<td>");
		text.append(change.getAuthor());
		text.append("</td> ");

		text.append("<td>");
		Timestamp timestamp = change.getTimestamp();
		text.append(timestamp == null ? "(no info)" : timestamp.getDate());
		text.append("</td>");

		return text.toString();
	}

	private File getFile(String ocName) throws IOException {
		ocName = ocName.replaceAll(FILE_NAME_REPLACE_REGEX, FILE_NAME_REPLACE_WITH);
		return new File(exportHMTLDir, ocName + ".html");
	}

	private String readExistingFile(File file) throws IOException {
		if (file.exists() == false) {
			return "";
		}
		
		BufferedReader reader = new BufferedReader(new FileReader(file));

		StringBuffer output = new StringBuffer();
		String row = null;

		while ((row = reader.readLine()) != null) {
			row = row.trim();
			//keep only empty rows, rows with comments, and rows with <tr> or <td>, but not <thead>
			if     ( //row.length() == 0 || 
					row.contains("<!--") ||
					row.contains("td>") ||
					(row.contains("tr>") && row.contains("thead>") == false) ) {
				output.append(row);
				output.append("\n");
			}
		}
		reader.close();
		
		return output.toString();
	}
	
	
	private boolean isIncluded(Change change) {
		Ontology_Component applyTo = change.getApplyTo();
		if (applyTo == null) {
			return false;
		}
		if (applyTo.getCurrentName() == null) {
			return false;
		}

		if (changeFilter.isFilteredOut(change) == true) {
			return false;
		}
		
		Date changeDate = change.getTimestamp().getDateParsed();
		if (changeDate == null) {
			return true;
		}
		
		return changeDate.before(maxDate);
	}

	private void exportHeader(Writer w, Ontology_Component oc) throws IOException {
		w.write("<html>\n");
		w.write("<head>\n" + 
				"    <link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">\n" + 
				"</head>\n\n");
		w.write("<body>\n");
		w.write("<p>\n");
		w.write("<h1>Changes for <i>" + getOCDisplayName(oc) + "</i></h1>");
		w.write("<p>that occured before " + DefaultTimestamp.DATE_FORMAT.format(maxDate) + "</p>\n\n");
		w.write("<table>\n");
		w.write("<thead> <tr> <th>Description</th> <th>Author</th> <th>Timestamp</th> </tr></thead>\n");
	}

	private String getOCDisplayName(Ontology_Component oc) {
		String currentName = oc.getCurrentName();
		if (kb == null) {
			return currentName;
		}
		
		Frame inst = kb.getFrame(currentName);
		return inst == null ? currentName : inst.getBrowserText();
	}

	private void exportFooter(Writer w) throws IOException {
		w.write("</table>\n\n");
		w.write("</body>\n</html>\n");
	}

    private void closeWriters() throws IOException {
    	toDeleteWriter.close();
    	csvWriter.close();
    }
    
	private void deleteTmpFile() {
		File tmpFile = new File(exportHMTLDir, "tmp");
		try {
			Files.delete(tmpFile.toPath());
		} catch (IOException e) {
			//do nothing
		}
	}
	
    
    // ************** Write metadata file **************
    
    private void exportMetadata(String exportFolder) throws IOException {
		File metadataFile = new File(exportFolder + METADATA_FILE);
		FileWriter w = new FileWriter(metadataFile, append);
		w.write("\n");
		w.write("exported-on:" + DefaultTimestamp.DATE_FORMAT.format(new Date()) + "\n");
		w.write("max-date:" + DefaultTimestamp.DATE_FORMAT.format(maxDate) + "\n");
		w.close();
	}
    
    // ************** Entities to delete export **************
    
	private void initToDeleteWriter() throws IOException {
		String prefix = CSV_FILE_PREFIX.format(new Date());
		toDeleteWriter = new BufferedWriter(new FileWriter(new File(exportHMTLDir, prefix + "." + ENTITIES_TO_DELETE_FILE)));
	}
	
	/**
	 * This method will print also changes that might not be included in the
	 * HTML export, e.g., subchanges, or invalid changes 
	 * that have a prior date to max date
	 * @param changes
	 * @throws IOException 
	 */
	private void printToDeleteEntities(Collection<Change> changes) throws IOException {
		for (Change change : changes) {
			if (shouldDeleteChange(change) == true) {
				toDeleteWriter.write(((AbstractWrappedInstance)change).getName() + "\n");
				toDeleteWriter.write(((AbstractWrappedInstance)change.getTimestamp()).getName() + "\n");
				
				//for testing purposes
				//toDeleteWriter.write(((AbstractWrappedInstance)change).getName() + CSV_SEPARATOR + change.getTimestamp().getDate() + "\n");
				//toDeleteWriter.write(((AbstractWrappedInstance)change.getTimestamp()).getName() + CSV_SEPARATOR + change.getTimestamp().getDate() + "\n");
			}
		}
	}
	
	private boolean shouldDeleteChange(Change change) {
		return change.getTimestamp().getDateParsed().before(maxDate);
	}
    
    // ************** CSV export ****************
	
	private void initCSVWriter() throws IOException {
		csvWriter = new BufferedWriter(new FileWriter(new File(exportHMTLDir, EXPORTED_CHANGES_CSV_FILE), append));
	}
    
    private String getCSVChangeRow(Change change) {
        StringBuffer text = new StringBuffer();

        text.append(quote(change.getContext()));
        text.append(CSV_SEPARATOR);

        text.append(change.getAuthor());
        text.append(CSV_SEPARATOR);

        Timestamp timestamp = change.getTimestamp();
        text.append(timestamp == null ? "(no_timestamp)" : timestamp.getDate());
        text.append(CSV_SEPARATOR);
        text.append(change.getApplyTo().getCurrentName());
        text.append(CSV_SEPARATOR);

        text.append(((AbstractWrappedInstance)change).getName());
        text.append(CSV_SEPARATOR);

        text.append("\n");
        return text.toString();
    }
    
    private void exportCSVRow(Change change) throws IOException {
    	csvWriter.write(getCSVChangeRow(change));
    }
    
    private String quote(String s) {
        return CSV_QUOTE_CHAR + s.replaceAll("\\" + CSV_QUOTE_CHAR, CSV_QUOTE_CHAR + CSV_QUOTE_CHAR) + CSV_QUOTE_CHAR;
    }
}
