package edu.stanford.smi.protegex.server_changes;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesTransListener;
import edu.stanford.smi.protegex.server_changes.listeners.owl.ChangesOwlKBListener;
import edu.stanford.smi.protegex.server_changes.listeners.owl.OwlChangesClassListener;
import edu.stanford.smi.protegex.server_changes.listeners.owl.OwlChangesFrameListener;
import edu.stanford.smi.protegex.server_changes.listeners.owl.OwlChangesPropertyListener;


public class ChangesProjectOWL {
	private static OwlChangesClassListener owlChangesClassListener;
    private static OwlChangesPropertyListener owlChangesPropertyListener;
    private static OwlChangesFrameListener owlChangesFrameListener;
    private static ChangesTransListener changeTransactionListener;
    private static ChangesOwlKBListener changesOwlKbListener;

    public static void registerOwlListeners(KnowledgeBase knowledgeBase) {
		OWLModel om = (OWLModel) knowledgeBase;
		om.addClassListener(owlChangesClassListener = new OwlChangesClassListener(om));
		om.addPropertyListener(owlChangesPropertyListener = new OwlChangesPropertyListener(om));
		((KnowledgeBase) om).addFrameListener(owlChangesFrameListener = new OwlChangesFrameListener(om));
		om.addTransactionListener(changeTransactionListener = new ChangesTransListener(om));
		((KnowledgeBase) om).addKnowledgeBaseListener(changesOwlKbListener = new ChangesOwlKBListener(om)); // Handles Class Deletes
	}

    public static void deregisterOwlListeners(KnowledgeBase kb) {
        try {
            ((OWLModel)kb).removeClassListener(owlChangesClassListener);
            ((OWLModel)kb).removePropertyListener(owlChangesPropertyListener);
            kb.removeFrameListener(owlChangesFrameListener);
            ((OWLModel)kb).removeTransactionListener(changeTransactionListener);
            kb.removeKnowledgeBaseListener(changesOwlKbListener);
        } catch (Exception e) {
            Log.getLogger().log(Level.WARNING, "Error at removing OWL change listeners", e);
        }

    }

}
