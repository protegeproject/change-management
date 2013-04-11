package edu.stanford.bmir.protegex.chao.export.changes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import edu.stanford.bmir.protegex.chao.annotation.api.OntologyJavaMapping;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;


public class NCIChangesAggregator {

    private static Logger log = Logger.getLogger(NCIChangesAggregator.class.getName());

    public static void main(String[] args) throws IOException {

        String exportFilePath = "/home/ttania/Desktop/ChAOExports/nci/nci_changes_export_2010.csv";

        OntologyJavaMapping.initMap();
        edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyJavaMapping.initMap();
        edu.stanford.bmir.protegex.chao.change.api.OntologyJavaMapping.initMap();

        ChangesExport exporter = new ChangesExport();
        exporter.setChangeFilter(new NCIChangesFilter());

        log.info("Started ChAO to CSV export on " + new Date());

        Writer w = new FileWriter(new File(exportFilePath), false); //second arg: append or not
        exporter.printHeader(w);

        for (int i = 0; i < 30; i++) {
            String table = "annotation_Thesaurus_Baseline" + i;
            log.info("--------------------------------- Started export of " + table + " ------------------------------ on " + new Date() );
            exporter.setDbTable(table);
            KnowledgeBase chAOKb = getChAOKb(table);
            exporter.exportChanges(chAOKb, w);
            chAOKb.getProject().dispose();
        }

        w.close();

        log.info("Ended ChAO to CSV export on " + new Date());
    }

    private static KnowledgeBase getChAOKb(String tableName) {
        DatabaseKnowledgeBaseFactory factory = new DatabaseKnowledgeBaseFactory();
        ArrayList errors = new ArrayList();
        Project prj = Project.createNewProject(factory, errors);
        DatabaseKnowledgeBaseFactory.setSources(prj.getSources(), "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/nci_201004", tableName, "protege", "protege");
        prj.createDomainKnowledgeBase(factory, errors, true);
        return prj.getKnowledgeBase();
    }

}
