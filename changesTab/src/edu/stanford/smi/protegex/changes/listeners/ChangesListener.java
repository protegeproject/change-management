package edu.stanford.smi.protegex.changes.listeners;



import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.server_changes.model.AbstractChangeListener;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;


public class ChangesListener extends AbstractChangeListener {
    private final static Logger log = Log.getLogger(ChangesListener.class);
    
    public ChangesListener(ChangeModel model) {
        super(model);
    }
    
    public void addChange(Change change) {
        ChangeModel.logAnnotatableThing("ChangeTab listener received change", log, Level.FINE, change);
        ChangesTab.createChange(change);
    }
    
    @Override
    public void modifyChange(Change change) {
        ChangeModel.logAnnotatableThing("ChangeTab listener received modification to change", log, Level.FINE, change);
        ChangesTab.modifyChange(change);
    }
    
    public void addAnnotation(Annotation a) { }


    
}

