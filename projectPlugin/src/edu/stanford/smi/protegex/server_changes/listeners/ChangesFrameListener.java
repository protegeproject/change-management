package edu.stanford.smi.protegex.server_changes.listeners;

import java.util.ArrayList;

import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;


public class ChangesFrameListener implements FrameListener {
    private KnowledgeBase kb;
    private ChangesDb changes_db;
    private KnowledgeBase changesKb;

    public ChangesFrameListener(KnowledgeBase kb) {
        this.kb = kb;
        changes_db = ChangesProject.getChangesDb(kb);
        changesKb = changes_db.getChangesKb();
    }

    public void browserTextChanged(FrameEvent event) {

    }

    public void deleted(FrameEvent event) {

    }

    public void nameChanged(FrameEvent event) {

    }

    public void visibilityChanged(FrameEvent event) {

    }

    public void ownFacetAdded(FrameEvent event) {

    }

    public void ownFacetRemoved(FrameEvent event) {

    }

    public void ownFacetValueChanged(FrameEvent event) {

    }

    public void ownSlotAdded(FrameEvent event) {

    }

    public void ownSlotRemoved(FrameEvent event) {

    }

    public void ownSlotValueChanged(FrameEvent event) {

        Frame f = event.getFrame();

        if (f instanceof Slot) {
            Slot s = (Slot)f;
            String sName = s.getName();
            Slot ownS = event.getSlot();
            String ownSName = ownS.getName();
            String newSlotValue = CollectionUtilities.toString(s.getOwnSlotValues(event.getSlot()));

            StringBuffer context = new StringBuffer();
            if(ownSName.equals(":SLOT-NUMERIC-MAXIMUM")) {
                context.append("Maximum value for: ");
                context.append(sName);
                context.append(" set to: ");
                context.append(newSlotValue);
                
                ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Maximum_Value, s, context.toString());
            }
            if(ownSName.equals(":SLOT-NUMERIC-MINIMUM")){
                context.append("Minimum value for: ");
                context.append(sName);
                context.append(" set to: ");
                context.append(newSlotValue);
                
                ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Minimum_Value, s, context.toString());

            }


            if(ownSName.equals(":SLOT-MINIMUM-CARDINALITY")){
                if(!newSlotValue.equals("")){
                    //should have atleast - value
                    context.append("Minimum cardinality for: ");
                    context.append(sName);
                    context.append(" set to: ");
                    context.append(newSlotValue);
                    
                    ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Minimum_Cardinality, s, context.toString());
                }

            }
            if(ownSName.equals(":SLOT-MAXIMUM-CARDINALITY")){
                if(newSlotValue.equals("")){
                    //slot can take multiple values

                    context.append(sName);
                    context.append(" can take multiple values");
                    
                    ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Maximum_Cardinality, s, context.toString());
                }
                else{
                    //maximum values set to -
                    context.append("Maximum cardinality for: ");
                    context.append(sName);
                    context.append(" set to: ");
                    context.append(newSlotValue);
                    
                    ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Maximum_Cardinality, s, context.toString());
                }

            }
        }

        else if (f instanceof Cls) {

            Cls c = (Cls)f;
            String cName = c.getBrowserText();
            Slot s = event.getSlot();
            String sName = s.getName();
            StringBuffer context = new StringBuffer();



            if(sName.equals(":DOCUMENTATION")) {
                String newSlotValue = CollectionUtilities.toString(c.getOwnSlotValues(event.getSlot()));
                if(newSlotValue.equals("")) {
                    //REMOVED DOCUMENTATION
                    context.append("Removed documentation from ");
                    context.append(cName);
                    
                    ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Documentation_Removed, c, context.toString());
                }
                else {
                    // ADDED DOCUMENTATION
                    context.append("Added documentation: ");
                    context.append(newSlotValue);
                    context.append(" to: ");
                    context.append(cName);
                    
                    ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Documentation_Added, c, context.toString());
                }
            } // Handles documentation slot
        }
        else if (f instanceof Instance) {
            Instance i = (Instance)f;
            String iName = i.getBrowserText();
            Slot ownS = event.getSlot();
            String ownSName = ownS.getName();
            String newSlotValue = CollectionUtilities.toString(i.getOwnSlotValues(event.getSlot()));
            ArrayList oldValue = (ArrayList)event.getArgument2();
            String oldSlotValue = oldValue.toString();

            StringBuffer context = new StringBuffer();
            context.append("Slot: ");
            context.append(ownSName);
            context.append(" for Instance: ");
            context.append(iName);
            context.append(" set to: ");
            context.append(newSlotValue);
            
            ServerChangesUtil.createChangeStd(changes_db, ChangeCls.Slot_Value, i, context.toString());

        }

    }




}
