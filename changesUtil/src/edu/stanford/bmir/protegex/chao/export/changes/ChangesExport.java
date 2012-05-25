package edu.stanford.bmir.protegex.chao.export.changes;

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

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.smi.protege.code.generator.wrapping.AbstractWrappedInstance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;


/**
 * Exports the changes from the Changes and Annotation ontology (ChAO) to a CSV file.
 *
 * CSV structure:
 *
 * change description, type of change operation (add, delete, change), type of changed entity (class, property, individual),
 * changed entity name, author, timestamp, change id, ChAO  db table
 *
 * @author ttania
 *
 */
public class ChangesExport {

    private static Logger log = Logger.getLogger(ChangesExport.class.getName());

    private static final String SEPARATOR = "\t";
    private static final String QUOTE_CHAR = "\"";

    //create a new class, property, restriction
    static String OP_TYPE_ADD="ADD";
    //delete or retire a class, property, restriction
    static String OP_TYPE_DELETE="DEL";
    //change of a property value
    static String OP_TYPE_PROP_CHANGE="EDIT";
    //change in class hierarchy
    static String OP_TYPE_MOVE="MOVE";
    //create reference
    static String OP_TYPE_REF="REF";

    static String ENTITY_CLS="CLS";
    static String ENTITY_PROP="PROP";
    static String ENTITY_RESTR="RESTR";
    static String ENTITY_IND="IND";

    private KnowledgeBase chAOKb;
    private ProjectChangeFilter changeFilter;

    private String dbTable = null;

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            log.severe("First argument should be the file or project URI, " +
                    "second argument should be the path to the exported file," +
            "third argument can be the project change filter (NCI | ICD | something else)");
            System.exit(1);
        }

        String chaoPrjPath = args[0];
        String exportFilePath = args[1];

        ChangesExport exporter = new ChangesExport();
        exporter.setChangeFilter(exporter.getChangeFilterFromArg(args[2]));

        log.info("Started ChAO to CSV export on " + new Date());

        Writer w = new FileWriter(new File(exportFilePath));
        exporter.exportChanges(exporter.getKb(chaoPrjPath), w);
        w.close();

        log.info("Ended ChAO to CSV export on " + new Date());
    }

    private ProjectChangeFilter getChangeFilterFromArg(String filter) {
        if (filter == null) {
            return new DefaultChangesFilter();
        } else if (filter.equalsIgnoreCase("NCI") ) {
            return new NCIChangesFilter();
        } else if (filter.equalsIgnoreCase("ICD") ) {
            return new ICDChangesFilter();
        } else {
            return new DefaultChangesFilter();
        }
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

    public void setChangeFilter(ProjectChangeFilter changeFilter) {
        this.changeFilter = changeFilter;
    }

    public void exportChanges(KnowledgeBase kb, Writer w) throws IOException {
        printHeader(w);

        log.info("Started getting all changes on " + new Date());
        Collection<Change> changes = new ChangeFactory(chAOKb).getAllChangeObjects(true);
        log.info("Ended getting " + changes.size() +" (total) changes on " + new Date());

        int i = 0;
        for (Change change : changes) {
            try {
                if (isIncluded(change)) {
                    printChange(change, w);
                }
                i++;
                if (i % 1000 == 0) {
                    log.info("Processed " + i + " changes on " + new Date());
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Error at exporting change " + change, e);
            }
        }
    }


    private boolean isIncluded(Change change) {
        Ontology_Component applyTo = change.getApplyTo();
        if (applyTo == null) {
            return false;
        }
        if (applyTo.getCurrentName() == null) {
            return false;
        }

        return !changeFilter.isFilteredOut(change);
    }


    private void printHeader(Writer w) throws IOException {
        w.write("change_desc" + SEPARATOR + "change_type" + SEPARATOR +
                "entity_type" + SEPARATOR + "entity" + SEPARATOR + "author" + SEPARATOR + "timestamp" +
                SEPARATOR + "change_id" + SEPARATOR + "db_table" + "\n");
    }

    private void printChange(Change change, Writer w) throws IOException {
        w.write(getChangeRow(change));
    }

    private String getChangeRow(Change change) {
        StringBuffer text = new StringBuffer();

        text.append(quote(change.getContext()));
        text.append(SEPARATOR);

        EntityOperationType entityOpType = changeFilter.getEntityAndOperationType(change);

        text.append(entityOpType.getOperationType());
        text.append(SEPARATOR);

        text.append(entityOpType.getEntityType());
        text.append(SEPARATOR);

        text.append(change.getApplyTo().getCurrentName());
        text.append(SEPARATOR);

        text.append(change.getAuthor());
        text.append(SEPARATOR);

        Timestamp timestamp = change.getTimestamp();
        text.append(timestamp == null ? "null" : timestamp.getDate());
        text.append(SEPARATOR);

        text.append(((AbstractWrappedInstance)change).getName());
        text.append(SEPARATOR);

        text.append(dbTable);

        text.append("\n");
        return text.toString();
    }

    private String quote(String s) {
        return QUOTE_CHAR + s.replaceAll("\\" + QUOTE_CHAR, QUOTE_CHAR + QUOTE_CHAR) + QUOTE_CHAR;
    }

}
