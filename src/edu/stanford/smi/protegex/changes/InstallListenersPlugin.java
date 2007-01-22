package edu.stanford.smi.protegex.changes;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.plugin.ProjectPluginAdapter;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.changes.listeners.ChangesClsListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesFrameListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesInstanceListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesKBListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesSlotListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesTransListener;
import edu.stanford.smi.protegex.changes.listeners.owl.ChangesOwlKBListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesClassListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesFrameListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesModelListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesPropertyListener;
import edu.stanford.smi.protegex.changes.owl.Util;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

public class InstallListenersPlugin extends ProjectPluginAdapter {
    private static boolean serverAlreadyHasChangesProject = false;
    
    public void afterLoad(Project p) {
        KnowledgeBase kb = p.getKnowledgeBase();
        if (!isChangesTabProject(p) || p.isMultiUserClient()) {
            return;
        }
        if (p.isMultiUserServer() && serverAlreadyHasChangesProject) {
            Log.getLogger().info("Can only have one server side project with the Changes Plugin");
        }
        else if (p.isMultiUserServer()) {
            serverAlreadyHasChangesProject = true;
        }
        
        ChangesTab.initializeChangesKB(kb);
        boolean isOwlProject = Util.kbInOwl(kb);
        // Register listeners
        if (isOwlProject) {
            registerOwlListeners(kb);
        } else {
            registerKBListeners(kb);
        }
    }
    
    private boolean isChangesTabProject(Project p) {
        String changesTabClassName = ChangesTab.class.getName();
        for (Object o : p.getTabWidgetDescriptors()) {
            WidgetDescriptor w = (WidgetDescriptor) o;
            if (changesTabClassName.equals(w.getWidgetClassName())) {
                return true;
            }
        }
        return false;
    }
    
    
    private void registerOwlListeners(KnowledgeBase kb) {
        ((AbstractOWLModel) kb).addClassListener(new OwlChangesClassListener());
        ((AbstractOWLModel) kb).addModelListener(new OwlChangesModelListener());
        ((AbstractOWLModel) kb).addPropertyListener(new OwlChangesPropertyListener());
        ((AbstractOWLModel) kb).addFrameListener(new OwlChangesFrameListener());
        kb.addTransactionListener(new ChangesTransListener());
        kb.addKnowledgeBaseListener(new ChangesOwlKBListener()); // Handles Class Deletes
    }
    
    private void registerKBListeners(KnowledgeBase kb) {
        kb.addKnowledgeBaseListener(new ChangesKBListener());
        kb.addClsListener(new ChangesClsListener());
        kb.addInstanceListener(new ChangesInstanceListener());
        kb.addSlotListener(new ChangesSlotListener());
        kb.addTransactionListener(new ChangesTransListener());
        kb.addFrameListener(new ChangesFrameListener());
    }
}
