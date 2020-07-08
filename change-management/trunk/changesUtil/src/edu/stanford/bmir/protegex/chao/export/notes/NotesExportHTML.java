package edu.stanford.bmir.protegex.chao.export.notes;

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

import edu.stanford.bmir.protegex.chao.annotation.api.AnnotatableThing;
import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.annotation.api.OntologyJavaMapping;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyComponentFactory;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultTimestamp;
import edu.stanford.bmir.protegex.chao.util.NoteDateComparator;
import edu.stanford.smi.protege.code.generator.wrapping.AbstractWrappedInstance;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Transaction;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

/**
 * Exports the notes from the Changes and Annotation ontology (ChAO) to HTML and CSV
 * file. 
 * The script will generate in the output folder:
 * - (1) The HTML files containing the notes after the max date (one HTML file per ontology entity)
 * - (2) A file "exportedNotes.csv" that contains a tabular format of all the exported notes
 * - (3) A file "entitiesToDelete" prefixed with "yyyy-MM-dd.HHmmss" that contains a list of entity ids (notes and timestamps) that should be 
 *       deleted from ChAO. They include notes and their timestamps that have been included in this export, but also notes and their
 *       timestamps that are before the max date 
 * - (4) An "export.metadata" file that contains the max date and the time of the export.
 * 
 * Notes: 
 * This script is supposed to be run on the same folder at different dates (e.g., once yearly). The script assumes
 * that the notes that have been exported, have also been deleted from ChAO. Even if not, a future run of the script will not include
 * again in the HTML a note instance that already exists in the file. The CSV file will not make this distinction and may 
 * include duplicates. If exported notes are deleted after each run of the script, as intended, this situation will not
 * occur.
 * 
 * @author ttania
 *
 */
public class NotesExportHTML {

	public static final String FILE_NAME_REPLACE_REGEX = "[\\/:\"*?<>|#]+";
	public static final String FILE_NAME_REPLACE_WITH = "_";
	
	public static final String CSV_SEPARATOR = "\t";
	public static final String CSV_QUOTE_CHAR = "\"";
	public static final SimpleDateFormat CSV_FILE_PREFIX = new SimpleDateFormat("yyyy-MM-dd.HHmmss");
	
	public static final String METADATA_FILE = "export.metadata";
	public static final String ENTITIES_TO_DELETE_FILE = "entitiesToDelete.csv";
	public static final String EXPORTED_NOTES_CSV_FILE = "exportedNotes.csv";
	
	public static final String STYLE_CSS = "style.css";

	private static Logger log = Logger.getLogger(NotesExportHTML.class.getName());

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
					  + "(4) Date up to which to export notes, date format: MM/dd/yyyy HH:mm:ss zzz"
					  + "(5) Optional: Path to main pprj file to export entity browser text.");

			System.exit(1);
		}

		initJavaMappings();

		log.info("Loading ChAO from: " + args[0]);
		KnowledgeBase chaoKB = loadKB(args[0]);
		
		if (chaoKB == null) {
			log.severe("Could not load project from: " + args[0]);
			System.exit(1);
		}
		
		KnowledgeBase kb = null;
		if (args.length == 5) {
			kb = loadKB(args[4]);
		}
		
		Date maxDate = DefaultTimestamp.getDateParsed(args[3]);
		log.info("Exporting changes before date: " + maxDate);
		
		NotesExportHTML exporter = new NotesExportHTML(chaoKB, kb);
		exporter.setExportHMTLDir(new File(args[1]));
		exporter.setAppend(Boolean.parseBoolean(args[2]));
		exporter.setMaxDate(maxDate);

		log.info("Started ChAO to HTML export on " + new Date());

		exporter.exportNotes();
		exporter.exportMetadata(args[1]);

		log.info("Ended ChAO to HTML export on " + new Date());
	}

	private static void initJavaMappings() {
		OntologyJavaMapping.initMap();
		edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyJavaMapping.initMap();
		//edu.stanford.bmir.protegex.chao.change.api.OntologyJavaMapping.initMap();
		edu.stanford.bmir.protegex.chao.annotation.api.OntologyJavaMapping.initMap();
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

	public NotesExportHTML(KnowledgeBase chaoKb, KnowledgeBase kb) {
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
			URI sourceURL = NotesExportHTML.class.getClassLoader().
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

	public void exportNotes() throws IOException {
		initWriters();
		
		Collection<Ontology_Component> ocs = new OntologyComponentFactory(chaoKb).getAllOntology_ComponentObjects(true);

		int i = 0;
		for (Ontology_Component oc : ocs) {
			try {
				exportNotes(oc);
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
	private void exportNotes(Ontology_Component oc) throws IOException {
		
		//Collection<Annotation> notes = oc.getAssociatedAnnotations();
		Collection<Annotation> notes = ServerChangesUtil.getAnnotatations(oc);
		List<Annotation> filteredNotes = filterNotes(notes);
		
		Collections.sort(filteredNotes, new NoteDateComparator(chaoKb));
		Collections.reverse(filteredNotes);
		
		printToDeleteEntities(notes);
		
		if (filteredNotes.size() == 0) { //don't export entities with no changes
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
		
		for (Annotation note : filteredNotes) {
			String changeInstName = ((AbstractWrappedInstance)note).getName();
			
			if (existingOCFileContent.contains(changeInstName)) {
				log.warning(ocFile + " already contains change: " + changeInstName );
			} else {
				tmpWriter.write("<tr> " + "<!-- " + changeInstName + " -->\n");
				tmpWriter.write(getNoteHtmlRow(note));
				tmpWriter.write("</tr>\n");
			}
			
			exportCSVRow(note);
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

	private List<Annotation> filterNotes(Collection<Annotation> notes) {
		List<Annotation> filteredNotes = new ArrayList<>();
		for (Annotation note : notes) {
			if (shouldExportNote(note) == true) {
				filteredNotes.add(note);
			}
		}
		return filteredNotes;
	}

	private String getNoteHtmlRow(Annotation note) {
		StringBuffer text = new StringBuffer();

		text.append("<td>");
		text.append(note.getSubject());
		text.append("</td> ");
		
		text.append("<td>");
		text.append(note.getBody());
		text.append("</td> ");

		text.append("<td>");
		text.append(note.getAuthor());
		text.append("</td> ");
		
		text.append("<td>");
		Timestamp timestamp = note.getCreated();
		text.append(timestamp == null ? "(no info)" : timestamp.getDate());
		text.append("</td>");
		
		text.append("<td>");
		text.append(getNoteType(note));
		text.append("</td> ");

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
	
	
	private boolean shouldExportNote(Annotation note) {
		Timestamp timestamp = note.getCreated();
		if (timestamp == null) {
			return true;
		}
		Date date = timestamp.getDateParsed();
			
		if (date == null) {
			return true;
		}
		
		return date.before(maxDate);
	}

	private void exportHeader(Writer w, Ontology_Component oc) throws IOException {
		w.write("<html>\n");
		w.write("<head>\n" + 
				"    <link href=\"style.css\" rel=\"stylesheet\" type=\"text/css\">\n" + 
				"</head>\n\n");
		w.write("<body>\n");
		w.write("<p>\n");
		w.write("<h1>Notes for <i>" + getOCDisplayName(oc) + "</i></h1>");
		w.write("<p>that occured before " + DefaultTimestamp.DATE_FORMAT.format(maxDate) + "</p>\n\n");
		w.write("<table>\n");
		w.write("<thead> <tr> <th>Subject</th> <th>Body</th> <th>Author</th> <th>Timestamp</th> <th>Type</th> </tr></thead>\n");
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
		File metadataFile = new File(exportFolder, METADATA_FILE);
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
	

	private void printToDeleteEntities(Collection<Annotation> notes) throws IOException {
		for (Annotation note : notes) {
			if (shouldExportNote(note) == true) {
				toDeleteWriter.write(((AbstractWrappedInstance)note).getName() + "\n");
				Timestamp ts = note.getCreated();
				
				if (ts != null) {
					toDeleteWriter.write(((AbstractWrappedInstance)ts).getName() + "\n");
				} 
			}
		}
	}
	
    // ************** CSV export ****************
	
	private void initCSVWriter() throws IOException {
		csvWriter = new BufferedWriter(new FileWriter(new File(exportHMTLDir, EXPORTED_NOTES_CSV_FILE), append));
	}
    
    private String getNoteCSVRow(Annotation note) {
        StringBuffer text = new StringBuffer();

        text.append(quote(note.getSubject()));
        text.append(CSV_SEPARATOR);
        
        text.append(quote(note.getBody()));
        text.append(CSV_SEPARATOR);

        text.append(getNoteType(note));
        text.append(CSV_SEPARATOR);

        text.append(getNoteAttachedToOC(note));
        text.append(CSV_SEPARATOR);

        text.append(getNoteDirectAnnotatesId(note));
        text.append(CSV_SEPARATOR);

        text.append(note.getAuthor());
        text.append(CSV_SEPARATOR);

        Timestamp timestamp = note.getCreated();
        text.append(timestamp == null ? "" : timestamp.getDate());
        text.append(CSV_SEPARATOR);

        text.append(((AbstractWrappedInstance)note).getName());
        text.append(CSV_SEPARATOR);
        
        return text.toString();
    }
    
    
    private String getNoteType(Annotation note) {
        String name = note.getClass().getSimpleName();
        return name.replace("Default", "");
    } 
    
    private String getNoteAttachedToOC(Annotation note) {
        Collection<Ontology_Component> ocs = ServerChangesUtil.getAnnotatedOntologyComponents(note);
        if (ocs.size() > 0) {
            Ontology_Component oc = CollectionUtilities.getFirstItem(ocs);
            return oc == null ? "" : oc.getCurrentName();
        }
        return "";
    }

    private String getNoteDirectAnnotatesId(Annotation note) {
        Collection<AnnotatableThing> anns = note.getAnnotates();
        AnnotatableThing ann = CollectionUtilities.getFirstItem(anns);
        if (ann == null) {
            return "";
        }
       return ((AbstractWrappedInstance)ann).getName();
    }
    
    private void exportCSVRow(Annotation note) throws IOException {
    	csvWriter.write(getNoteCSVRow(note));
    }
    
    private String quote(String s) {
    	if (s == null) {
    		return "";
    	}
        return CSV_QUOTE_CHAR + s.replaceAll("\\" + CSV_QUOTE_CHAR, CSV_QUOTE_CHAR + CSV_QUOTE_CHAR) + CSV_QUOTE_CHAR;
    }
    
    private String getChangeDescription(String text) {
        if (text == null) {
            return "No details";
        }
        int index = text.indexOf(Transaction.APPLY_TO_TRAILER_STRING);
        if (index > 0) {
            return text.substring(0, index);
        }
        return text;
    }
}
