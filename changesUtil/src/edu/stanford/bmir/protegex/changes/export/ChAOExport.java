package edu.stanford.bmir.protegex.changes.export;

import edu.stanford.smi.protege.model.KnowledgeBase;


public class ChAOExport {
    private KnowledgeBase chaoKb;
    private String exportFilePath = "chaoExport";


    public ChAOExport(KnowledgeBase chaoKb, String exportPath) {
       this.chaoKb = chaoKb;
       this.exportFilePath = exportPath;
    }

    private void exportChAO() {

    }



}
