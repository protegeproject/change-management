package edu.stanford.smi.protegex.server_changes.model;

import java.util.List;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeSlot;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;

public abstract class AbstractChangeListener extends FrameAdapter {
    private Slot applyToSlot;
    private Slot annotatesSlot;
    
    public AbstractChangeListener(ChangeModel model) {
        applyToSlot = model.getSlot(ChangeSlot.applyTo);
        annotatesSlot = model.getSlot(ChangeSlot.annotates);
    }
    
    public void ownSlotValueChanged(FrameEvent event) {
        Frame frame = event.getFrame();
        Slot slot = event.getSlot();
        if (frame instanceof Change) {
            Change change = (Change) frame;
            if (slot.equals(applyToSlot)) {
                addChange(change);
            }
            else if (change.getApplyTo() != null) {
                List oldValues = event.getOldValues();
                modifyChange(change, slot, oldValues);
            }
        }
        else if (frame instanceof Annotation && slot.equals(annotatesSlot)) {
            addAnnotation((Annotation) frame);
        }
    }
    
    public abstract void addChange(Change change);
    
    public abstract void modifyChange(Change change, Slot slot, List oldValues);
    
    public abstract void addAnnotation(Annotation annotation);

}
