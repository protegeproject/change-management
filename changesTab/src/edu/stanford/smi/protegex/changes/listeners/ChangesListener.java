package edu.stanford.smi.protegex.changes.listeners;



import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.changes.ChangeTreeTableModel;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.changes.ui.ChangeMenu;
import edu.stanford.smi.protegex.server_changes.model.AbstractChangeListener;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;


public class ChangesListener extends AbstractChangeListener {
    private final static Logger log = Log.getLogger(ChangesListener.class);
    
    private ChangesTab changesTab;
    
    public ChangesListener(ChangeModel model, ChangesTab changesTab) {
        super(model);
        
        this.changesTab = changesTab;
    }
    
    public void addChange(Change change) {
        ChangeModel.logAnnotatableThing("ChangeTab listener received change", log, Level.FINE, change);
        changesTab.createChange(change);
    }
    
    @Override
    public void modifyChange(Change change, Slot slot, List oldValues) {
        ChangeModel.logAnnotatableThing("ChangeTab listener received modification to change", log, Level.FINE, change);
        changesTab.modifyChange(change, slot, oldValues);
    }
    
    public void addAnnotation(Annotation a) { }


    
}

