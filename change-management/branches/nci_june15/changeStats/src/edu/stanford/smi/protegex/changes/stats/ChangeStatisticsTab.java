package edu.stanford.smi.protegex.changes.stats;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.server.RemoteProjectManager;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.DefaultRenderer;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.SelectableTable;
import edu.stanford.smi.protege.widget.AbstractTabWidget;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.GetAnnotationProjectName;

public class ChangeStatisticsTab extends AbstractTabWidget {
	
	private KnowledgeBase changesKb;
	private StatsTableModel statsTableModel;
	private SelectableTable statsTable;

	public void initialize() {
		setLabel("Change Statistics");
		
		changesKb = getChangesKb(getKnowledgeBase());
		
		statsTableModel = new StatsTableModel(changesKb);
		statsTableModel.generateStatsTable();
			
		buildGUI();
	}

	private void buildGUI() {
		
		statsTable = ComponentFactory.createSelectableTable(null);
		//statsTable = new SelectableTable();
		statsTable.setModel(statsTableModel);
			
		DefaultRenderer renderer = new FrameRenderer();
		for (int i = 0; i < StatsTableModel.columnNames.length; i++) {
			ComponentUtilities.addColumn(statsTable, renderer);
		}
		
		statsTable.setShowGrid(true);
		
		LabeledComponent labeledComp = new LabeledComponent("Change Statistics", new JScrollPane(statsTable), true);
				
		JButton generateStatsButton = ComponentFactory.createButton(getGenerateStatsButton());
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.add(generateStatsButton);
		
		labeledComp.setFooterComponent(buttonPanel);
		
		add(labeledComp);		
	}

	private AllowableAction getGenerateStatsButton() {
		return new AllowableAction(new ResourceKey("Generate change statistics")) {

			public void actionPerformed(ActionEvent arg0) {
				statsTableModel.generateStatsTable();				
			}
			
		};
	}
	
	//copied from ChangesTab. This should be unified at some point with the ChangesTab method!!!
	private KnowledgeBase getChangesKb(KnowledgeBase kb) {
				
		if (changesKb != null) {
			return changesKb;
		}
			
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
		Project changesProject = null;
		
		if (changesKb != null) {
			changesProject = changesKb.getProject();
		}
		
		if (changesProject != null) {			
			try {
				changesProject.dispose();
			} catch (Exception e) {
				Log.getLogger().warning("Errors at disposing changes project " + changesProject + " of project " + changesProject);
			}
		}	

		super.dispose();
	}

}
