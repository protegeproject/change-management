package edu.stanford.smi.protegex.changes.changesKBViewTab;

import java.awt.Container;
import java.util.Collection;

import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.server.RemoteProjectManager;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.ui.DirectInstancesList;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.SelectableList;
import edu.stanford.smi.protege.widget.AbstractTabWidget;
import edu.stanford.smi.protege.widget.ClsesAndInstancesTab;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.GetAnnotationProjectName;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

/**
 * @author Tania Tudorache <tudorache@stanford.edu>
 *
 */
public class ChangesKBViewTab extends AbstractTabWidget {
	//TODO: Clean up this class
	
	ClsesAndInstancesTab ciTab;
	Project changesProject;

	public void initialize() {	
		setLabel("ChangesKBViewTab");
		
		changesProject = getChangesProject();
		buildGUI();		
	}

	public void buildGUI() {	
		if (changesProject == null) {
			return;
		}
		
		ciTab = new ClsesAndInstancesTab();
		ciTab.setup(getDescriptor(), changesProject);
		ciTab.initialize();
		
		//getClsTree().setCellRenderer(new FrameIDRenderer());
		//adjustDirectInstanceRenderer();	
		
		add(ciTab);
	};
	
	
	public Project getChangesProject() {
		if (changesProject != null) {
			return changesProject;
		}
		
		KnowledgeBase changesKB = null;
		
		if (getProject() != null) {
			changesKB = getChangesKb(getKnowledgeBase());		  
		}
		
		if (changesKB != null) {
			changesProject = changesKB.getProject();
		}
		
		return changesProject;
	}	
	
	
	public static boolean isSuitable(Project p, Collection errors) {
		try {
			KnowledgeBase kb = ChangesProject.getChangesKB(p.getKnowledgeBase());
			if (kb == null) {
				errors.add("Needs Changes ontology");
				return false;
			}
			
			return true;
		} catch (Exception e) {
			errors.add("Needs Changes ontology");
			return false;
		}
	}
	   
	
	private void adjustDirectInstanceRenderer() {	
		try {
			//ugly way of getting the instance list, because there are no getter methods in the superclass
			DirectInstancesList dirList = (DirectInstancesList)((Container)((Container)((Container)getComponent(0)).getComponent(2)).getComponent(1)).getComponent(1);
			((SelectableList)((DirectInstancesList) dirList).getSelectable()).setCellRenderer(new FrameIDRenderer());
			
		} catch (Exception e) {
			Log.getLogger().warning("Error at setting browser slot " + e.getMessage());			
		}
	}
	
	private final class FrameIDRenderer extends FrameRenderer {		
		@Override
		public void load(Object value) {
			super.load(value);
			if (value instanceof Frame)
				appendText(" id=" + ((Frame)value).getFrameID().getLocalPart());
		}
	}
	
	
	//copied from ChangesTab. This should be unified at some point with the ChangesTab method!!!
	private  KnowledgeBase getChangesKb(KnowledgeBase kb) {
				
		if (changesProject != null) {
			return changesProject.getKnowledgeBase();
		}
		
		KnowledgeBase changesKb = null;
			
		try {
			// NEED TO ADD IMPLEMENTATION FOR SERVER MODE
			// But this project must "essentially" be the same as the project that the project plugin is using
			// same events, contents etc.
			// it also runs after the changes project plugin has initialized.
			if (kb.getProject().isMultiUserClient()) {
				changesKb = getServerSideChangeKb(kb);
			}
			else {
				changesKb = ChangesProject.getChangesKB(kb);		
			}
						
		} catch(Throwable e) {
			Log.getLogger().warning("There were errors at getting the Changes project attached to " + kb + ". Error message: " + e.getMessage());
		}

		return changesKb;
	}
	
	private KnowledgeBase getServerSideChangeKb(KnowledgeBase kb) {
		String annotationName = (String) new GetAnnotationProjectName(kb).execute();
		if (annotationName == null) {
			Log.getLogger().warning("annotation project not configured (use " +
					GetAnnotationProjectName.METAPROJECT_ANNOTATION_PROJECT_SLOT +
			" slot)");
		}
		RemoteProjectManager manager = RemoteProjectManager.getInstance();
		FrameStoreManager fsmanager = ((DefaultKnowledgeBase) kb).getFrameStoreManager();
		RemoteClientFrameStore rcfs = (RemoteClientFrameStore) fsmanager.getFrameStoreFromClass(RemoteClientFrameStore.class);
		Project changes_project = manager.connectToProject(rcfs.getRemoteServer(), rcfs.getSession(), annotationName);
		return changes_project.getKnowledgeBase();	
	}
	
	@Override
	public void dispose() {
		if (getProject().isMultiUserClient() && changesProject != null) {			
			try {
				changesProject.dispose();
			} catch (Exception e) {
				Log.getLogger().warning("Errors at disposing changes project " + changesProject + " of project " + changesProject);
			}
		}	
		
		//TODO: remove the listeners
		super.dispose();
	}
	
	
}
