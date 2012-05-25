package edu.stanford.bmir.protegex.changes.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.bmir.protegex.chao.annotation.api.AnnotatableThing;
import edu.stanford.bmir.protegex.chao.annotation.api.Annotation;
import edu.stanford.bmir.protegex.chao.annotation.api.AnnotationFactory;
import edu.stanford.bmir.protegex.chao.annotation.api.OntologyJavaMapping;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.smi.protege.code.generator.wrapping.AbstractWrappedInstance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;


public class NotesExport {

    private static Logger log = Logger.getLogger(NotesExport.class.getName());

    private static final String SEPARATOR = "\t";
    private static final String QUOTE_CHAR = "\"";

    private KnowledgeBase chAOKb;

    private String dbTable = null;

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            log.severe("First argument should be the file or project URI, " +
                    "second argument should be the path to the exported file");
            System.exit(1);
        }

        String chaoPrjPath = args[0];
        String exportFilePath = args[1];

        //init Java mappings
        OntologyJavaMapping.initMap();
        edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyJavaMapping.initMap();

        NotesExport exporter = new NotesExport();

        log.info("Started notes export from ChAO to CSV on " + new Date());

        Writer w = new FileWriter(new File(exportFilePath));
        exporter.exportNotes(exporter.getKb(chaoPrjPath), w);
        w.close();

        log.info("Ended notes export from ChAO to CSV on " + new Date());
    }


    @SuppressWarnings("rawtypes")
    private KnowledgeBase getKb(String pprjPath) {
        ArrayList errors = new ArrayList();

        Project prj = Project.loadProjectFromFile(pprjPath, errors);
        chAOKb =  prj.getKnowledgeBase();

        if (errors.size() > 0) {
            log.warning("There were errors at loading project " + pprjPath);
            for (Iterator iterator = errors.iterator(); iterator.hasNext();) {
                Object object = iterator.next();
                log.warning(object.toString());
            }
        }

        if (chAOKb.getKnowledgeBaseFactory() instanceof DatabaseKnowledgeBaseFactory) {
            dbTable = DatabaseKnowledgeBaseFactory.getTableName(chAOKb.getProject().getSources());
        }

        return chAOKb;
    }

    public void exportNotes(KnowledgeBase kb, Writer w) throws IOException {
        printHeader(w);

        log.info("Started getting all changes on " + new Date());
        Collection<Annotation> notes = new AnnotationFactory(chAOKb).getAllAnnotationObjects(true);
        log.info("Ended getting " + notes.size() +" notes on " + new Date());

        int i = 0;
        for (Annotation note : notes) {
            try {
                printNote(note, w);
                i++;
                if (i % 1000 == 0) {
                    log.info("Processed " + i + " notes on " + new Date());
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Error at exporting note " + note, e);
            }
        }
    }


    private void printHeader(Writer w) throws IOException {
        w.write("note_subject" + SEPARATOR + "note_type" + SEPARATOR +
                 "top_annotated_entity" + SEPARATOR + "direct_annotated_id" + SEPARATOR +
                 "author" + SEPARATOR + "timestamp" +
                SEPARATOR + "note_id" + SEPARATOR + "db_table" + "\n");
    }


    private void printNote(Annotation note, Writer w) throws IOException {
        StringBuffer text = new StringBuffer();

        text.append(quote(note.getSubject()));
        text.append(SEPARATOR);

        text.append(getNoteType(note));
        text.append(SEPARATOR);

        text.append(getNoteAttachedToOC(note));
        text.append(SEPARATOR);

        text.append(getNoteDirectAnnotatesId(note));
        text.append(SEPARATOR);

        text.append(note.getAuthor());
        text.append(SEPARATOR);

        Timestamp timestamp = note.getCreated();
        text.append(timestamp == null ? "" : timestamp.getDate());
        text.append(SEPARATOR);

        text.append(((AbstractWrappedInstance)note).getName());
        text.append(SEPARATOR);

        text.append(dbTable);
        text.append("\n");

        w.write(text.toString());
    }

    private String getNoteType(Annotation note) {
        String name = note.getClass().getSimpleName();
        return name.replace("Default", "");
    }

    private String quote(String s) {
        if (s == null) { return ""; }
        return QUOTE_CHAR + s.replaceAll("\\" + QUOTE_CHAR, QUOTE_CHAR + QUOTE_CHAR) + QUOTE_CHAR;
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
       return ((AbstractWrappedInstance)ann).getName();
    }

}
