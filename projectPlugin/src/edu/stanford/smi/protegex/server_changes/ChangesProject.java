package edu.stanford.smi.protegex.server_changes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.plugin.ProjectPluginAdapter;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesClsListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesFrameListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesInstanceListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesKBListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesSlotListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesTransListener;
import edu.stanford.smi.protegex.server_changes.postprocess.AnnotationCombiner;
import edu.stanford.smi.protegex.server_changes.postprocess.JoinCreateAndNameChange;
import edu.stanford.smi.protegex.server_changes.postprocess.JoinInstanceCreateAndAdd;
import edu.stanford.smi.protegex.server_changes.server.ChangeOntStateMachine;
import edu.stanford.smi.protegex.server_changes.util.Util;

public class ChangesProject extends ProjectPluginAdapter {

    private static Map<KnowledgeBase, PostProcessorManager> changesDbMap = new HashMap<KnowledgeBase, PostProcessorManager>();

    /* ---------------------------- Project Plugin Interfaces ---------------------------- */
	@Override
	public void afterLoad(Project p) {
		if (!isChangeTrackingEnabled(p) || p.isMultiUserClient()) {
			return;
		}
		initialize(p);
	}

    @Override
	public void afterSave(Project p) {
        if (p.isMultiUserClient()) {
            return;
        }
        ChAOKbManager.saveChAOProject(p.getKnowledgeBase());
    }

    @Override
	public void beforeClose(Project p) {
        if (p.isMultiUserClient()) {
            return;
        }
        KnowledgeBase kb = p.getKnowledgeBase();
        PostProcessorManager changesDb  = getChangesDb(kb);
        if (changesDb != null) {
            if (!p.isMultiUserServer()) {
            	//FIXME: don't dispose for now. The ChAOKBManager should handle this.
                //changesDb.getChangesKb().dispose();
            }
            changesDbMap.remove(kb);
        }
    }

    /* ---------------------------- End of Project Plugin Interfaces ---------------------------- */

    public static boolean isChangeTrackingEnabled(Project p) {
    	boolean trackChanges = p.getChangeTrackingActive();

    	if (trackChanges) {
    		return true;
    	}

    	//If the update modification slots is not set, try to find the changes tab

        String changesTabClassName = ChangesTab.class.getName();
        for (Object o : p.getTabWidgetDescriptors()) {
            WidgetDescriptor w = (WidgetDescriptor) o;
            if (w.isVisible() && changesTabClassName.equals(w.getWidgetClassName())) {
                return true;
            }
        }
        return false;
    }


	public void initialize(Project p) {
		Project currentProj = p;
		KnowledgeBase currentKB = currentProj.getKnowledgeBase();

		createChangeProject(currentKB);

        PostProcessorManager changesDb = changesDbMap.get(currentKB);
        KnowledgeBase changesKb = changesDb.getChangesKb();
        Project changesProj = changesDb.getChangesProject();
		if (changesKb == null) {
			Log.getLogger().warning("Could not initialize the annotations ontology. ChangesTab will probably not work");
			return;
		}

		//Check to see if the project is an OWL one
		boolean isOwlProject = Util.kbInOwl(currentKB);

		// Register listeners
		if (isOwlProject) {
			ChangesProjectOWL.registerOwlListeners(currentKB);
		} else {
			registerKBListeners(currentKB);
		}

		if (changesProj.isMultiUserServer()) {
			ServerFrameStore.requestEventDispatch(currentKB);
            ((DefaultKnowledgeBase) changesKb).setCacheMachine(new ChangeOntStateMachine(changesKb));
		}

	}

	private static void registerKBListeners(KnowledgeBase currentKB) {
		currentKB.addKnowledgeBaseListener(new ChangesKBListener(currentKB));
		currentKB.addClsListener(new ChangesClsListener(currentKB));
		currentKB.addInstanceListener(new ChangesInstanceListener(currentKB));
		currentKB.addSlotListener(new ChangesSlotListener(currentKB));
		currentKB.addTransactionListener(new ChangesTransListener(currentKB));
		currentKB.addFrameListener(new ChangesFrameListener(currentKB));
	}


	private static void createChangeProject(KnowledgeBase currentKB) {
        PostProcessorManager changesDb = changesDbMap.get(currentKB);
        if (changesDb == null) {
            changesDb = new PostProcessorManager(currentKB);
            changesDb.addPostProcessor(new AnnotationCombiner());
            changesDb.addPostProcessor(new JoinCreateAndNameChange());
            changesDb.addPostProcessor(new JoinInstanceCreateAndAdd());
            changesDbMap.put(currentKB, changesDb);
        }
	}


	public static String getUserName(KnowledgeBase currentKB) {
		return currentKB.getUserName();
	}

    public static PostProcessorManager getChangesDb(KnowledgeBase kb) {
        return changesDbMap.get(kb);
    }

	public static KnowledgeBase getChangesKB(KnowledgeBase kb) {
        PostProcessorManager changesDb = changesDbMap.get(kb);
		return changesDb == null ? null: changesDb.getChangesKb();
	}

	public static Project getChangesProj(KnowledgeBase kb) {
        PostProcessorManager changesDb = changesDbMap.get(kb);
        if (changesDb == null) {
        		return null;
        }
        return changesDb.getChangesProject();
	}

	@Override
	public String getName() {
		return "Changes Project Plugin";
	}

	public static void displayErrors(Collection errors) {
        Iterator i = errors.iterator();
        while (i.hasNext()) {
            Object elem = i.next();
            if (elem instanceof Throwable) {
                Log.getLogger().log(Level.WARNING, "Warnings at loading changes project", (Throwable)elem);
            } else if (elem instanceof MessageError) {
                Log.getLogger().log(Level.WARNING, ((MessageError)elem).getMessage(), ((MessageError)elem).getException());
            } else {
                Log.getLogger().warning(elem.toString());
            }
        }
    }

    public static void main(String[] args) {
		Application.main(args);
	}

}
