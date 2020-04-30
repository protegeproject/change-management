package edu.stanford.bmir.protegex.chao.export.changes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
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
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;

/**
 * Exports the changes from the Changes and Annotation ontology (ChAO) to a HTML
 * file. It will also export an "export.metadata" file with the export date and max date for 
 * the export. And will also export a "entitiesToDelete.csv" that contains
 * the change objects and the timestamp objects that should be deleted from ChAO
 * (maybe with a SQL script to be faster).
 * 
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
	
	public static final String STYLES_CSS = "etc/style.css";

	private static Logger log = Logger.getLogger(ChangesExportHTML.class.getName());

	private ProjectChangeFilter changeFilter = new DefaultChangesFilter();

	private KnowledgeBase chaoKb;

	private File exportHMTLDir;
	private boolean append;
	private Date maxDate;

	private BufferedWriter toDeleteWriter;
	private BufferedWriter csvWriter;

	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
		   	 log.severe("(1) ChAO file or project URI, " + 
						"(2) Path to export HTML folder, "
					  + "(3) Append to existing HTML files [true|false],"
					  + "(4) Date up to which to export changes, date format: MM/dd/yyyy HH:mm:ss zzz");

			System.exit(1);
		}

		initJavaMappings();

		ChangesExportHTML exporter = new ChangesExportHTML(loadChaoKB(args[0]));
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
	private static KnowledgeBase loadChaoKB(String pprjPath) {
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

		if (kb == null) {
			log.severe("Could not load ChAO KB from: " + pprjPath);
			System.exit(1);
		}

		return kb;
	}

	public ChangesExportHTML(KnowledgeBase chaoKb) {
		this.chaoKb = chaoKb;
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
			Files.copy(new File(STYLES_CSS).toPath(), styleFile.toPath());
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
	}


	private void exportChanges(Ontology_Component oc) throws IOException {
		
		Collection<Change> changes = oc.getChanges();
		List<Change> filteredChanges = filterChanges(changes);
		
		Collections.sort(filteredChanges, new ChangeDateComparator(chaoKb));
		Collections.reverse(filteredChanges);
		
		printToDeleteEntities(changes);
		
		if (filteredChanges.size() == 0) { //don't export entities with no changes
			return;
		}
		
		File file = getFile(oc.getCurrentName());
		boolean shouldExportHeader = shouldExportHeader(file);
				
		FileWriter ocFileWriter = new FileWriter(file, append);
		
		if (shouldExportHeader == true) {
			exportHeader(ocFileWriter);
		}
		
		printExportDate(ocFileWriter);
		
		for (Change change : filteredChanges) {
			ocFileWriter.write("<tr> " + "<!-- " + ((AbstractWrappedInstance)change).getName() + " -->\n");
			ocFileWriter.write(getChangeRow(change));
			ocFileWriter.write("</tr>\n");
			
			exportCSVRow(change);
		}
		
		exportFooter(ocFileWriter);
		
		ocFileWriter.close();
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

	private boolean shouldExportHeader(File exportFile) {
		return exportFile.exists() == false || append == false;
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
		text.append("</td>\n");

		return text.toString();
	}

	private File getFile(String ocName) throws IOException {
		ocName = ocName.replaceAll(FILE_NAME_REPLACE_REGEX, FILE_NAME_REPLACE_WITH);
		File file = new File(exportHMTLDir, ocName + ".html");
		
		if (file.exists() == true && append == true) {
			truncateFile(file);
		}
		
		return file;
	}

	private void truncateFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		StringBuffer output = new StringBuffer();
		String row = null;

		while ((row = reader.readLine()) != null) {
			row = row.trim();
			if (row.contains("</html>") == false && row.contains("</body>") == false
					&& row.contains("</table>") == false) {
				output.append(row);
				output.append("\n");
			}
		}
		reader.close();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(output.toString());
		writer.close();
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

	private void exportHeader(Writer w) throws IOException {
		w.write("<html>\n");
		w.write("<head>\n" + 
				"    <link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">\n" + 
				"</head>\n\n");
		w.write("<body>\n");
		w.write("<table>\n");
		w.write("<thead> <tr> <th>Description</th> <th>Author</th> <th>Timestamp</th> </tr></thead>\n");
	}

	private void exportFooter(Writer w) throws IOException {
		w.write("\n</table>\n\n");
		w.write("</body>\n</html>\n");
	}

    private void closeWriters() throws IOException {
    	toDeleteWriter.close();
    	csvWriter.close();
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
        text.append(timestamp == null ? "(no timestamp)" : timestamp.getDate());
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
