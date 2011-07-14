package edu.stanford.smi.protegex.changes.action;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.ArchiveManager;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protegex.changes.ChangeProjectUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;

public class CleanUpChangesOntAction extends AbstractAction {
    private static final long serialVersionUID = 5399527829679938005L;

    private static final String EXTRACT_FROM_CHG_ONT = "Clean Up Changes Ontology ...";

	private KnowledgeBase kb;

	public CleanUpChangesOntAction(KnowledgeBase kb) {
		super(EXTRACT_FROM_CHG_ONT);
		this.kb = kb;
	}

	public void actionPerformed(ActionEvent arg0) {

		CleanupPanel cleanupPanel = new CleanupPanel();
		KnowledgeBase chaoKb = ChAOKbManager.getChAOKb(kb);

		int result = ModalDialog.showDialog(ProjectManager.getProjectManager().getMainPanel(), cleanupPanel, EXTRACT_FROM_CHG_ONT, ModalDialog.MODE_OK_CANCEL);

		if (result == ModalDialog.OPTION_OK) {
			if (cleanupPanel.isArchiveCurrentVersion()) {
				ArchiveManager manager = ArchiveManager.getArchiveManager();
				try {
					manager.archive(chaoKb.getProject(), new Date().toString() + ": Archiving changes ontology before clean-up");
				} catch (Exception e) {
					Log.getLogger().log(Level.WARNING, "Archive of changes ontology failed. ", e);
					ModalDialog.showMessageDialog(ProjectManager.getProjectManager().getMainPanel(),
					"Archiving of changes ontology failed. Operation aborted.\nSee console and logs for more details.");
					return;
				}
			}

			boolean success = true;
			boolean  continueCleanUp = true;

			if (continueCleanUp && cleanupPanel.isWipeOut()) {
			    int ret = ModalDialog.showMessageDialog(ProjectManager.getProjectManager().getCurrentProjectView(), "The entire Changes and Annotation Ontology will be wiped out.\n" +
			    		"You will loose all the change tracking and notes. Are you sure you want to continue?",
			            "Wipe out!", ModalDialog.MODE_OK_CANCEL);
			    if (ret == ModalDialog.OPTION_OK) {
			        try {
			            ChAOKbManager.wipeOutChAO(kb);
			            continueCleanUp = false;
			        } catch (Exception e) {
			            Log.getLogger().log(Level.WARNING, "Wipe out of ChAO failed.", e);
			            ModalDialog.showMessageDialog(ProjectManager.getProjectManager().getCurrentProjectView(), "Wipe out of the Change and Annotation ontology failed.\n" +
			            		"See console and logs for more details.");
			        }
			    }
			}

			if (continueCleanUp && cleanupPanel.isExtractAnnotations()) {
			    boolean generateEvents = chaoKb.getGenerateEventsEnabled();
				try {
				    chaoKb.setGenerateEventsEnabled(false);
					ChangeProjectUtil.deleteAllAnnotations(chaoKb);
				} catch (Exception e) {
					Log.getLogger().log(Level.WARNING, "Delete of annotation instances failed. ", e);
					success = false;

					ModalDialog.showMessageDialog(ProjectManager.getProjectManager().getMainPanel(),
					"Delete of annotation instances failed.\nSee console and logs for more details.");
				} finally {
				    chaoKb.setGenerateEventsEnabled(generateEvents);
				}
			}

			if (continueCleanUp && cleanupPanel.isExtractChanges()) {
			    boolean generateEvents = chaoKb.getGenerateEventsEnabled();
				try {
				    chaoKb.setGenerateEventsEnabled(false);
					ChangeProjectUtil.deleteAllChanges(chaoKb);
				} catch (Exception e) {
					Log.getLogger().log(Level.WARNING, "Delete of change instances failed. ", e);
					success = false;

					ModalDialog.showMessageDialog(ProjectManager.getProjectManager().getMainPanel(),
					"Delete of change instances failed.\nSee console and logs for more details.");
				} finally {
                    chaoKb.setGenerateEventsEnabled(generateEvents);
                }
			}

			if (continueCleanUp && cleanupPanel.isExtractSubChanges()) {
			    boolean generateEvents = chaoKb.getGenerateEventsEnabled();
                try {
                    chaoKb.setGenerateEventsEnabled(false);
                    ChangeProjectUtil.deleteAllSubChanges(chaoKb);
                } catch (Exception e) {
                    Log.getLogger().log(Level.WARNING, "Delete of change instances failed. ", e);
                    success = false;

                    ModalDialog.showMessageDialog(ProjectManager.getProjectManager().getMainPanel(),
                    "Delete of change instances failed.\nSee console and logs for more details.");
                } finally {
                    chaoKb.setGenerateEventsEnabled(generateEvents);
                }
            }

			/*
			 * Reinitialize ChangesTab to refresh the display of changes
			 */

			try	{
				ProjectView projectView = ProjectManager.getProjectManager().getCurrentProjectView();
				ChangesTab changesTab = (ChangesTab) projectView.getTabByClassName(ChangesTab.class.getName());

				if (changesTab != null) {
					projectView.reload(changesTab);
				}

			} catch (Exception e) {
				Log.getLogger().log(Level.WARNING, "Errors at reinitializing ChangesTab after ontology clean-up", e);
			}


			if (success == true) {
				ModalDialog.showMessageDialog(ProjectManager.getProjectManager().getMainPanel(),
				"Clean-up of changes ontology successful!");
			}

		}
	}

	@Override
	public boolean isEnabled() {
		return !kb.getProject().isMultiUserClient();
	}



	protected class CleanupPanel extends JPanel {

		private static final long serialVersionUID = -4885321069512662284L;
        private static final String EXTRACT_CHANGES = "Delete changes";
		private static final String EXTRACT_SUBCHANGES = "Delete sub-changes (keep only top-level changes)";
		private static final String EXTRACT_ANNOTATIONS = "Delete annotations";
		private static final String WIPE_OUT = "Wipe out changes and annotations (replace ChAO with an empty one)";
		private static final String ARCHIVE = "Archive changes ontology before deleting";

		private JCheckBox extractChangesCheckBox;
		private JCheckBox extractSubChangesCheckBox;
		private JCheckBox extractAnnotationsCheckBox;
		private JCheckBox wipeOutCheckBox;
		private JCheckBox archiveCheckBox;


		public CleanupPanel() {
			buildGUI();
		}

		public void buildGUI() {
			extractChangesCheckBox = new JCheckBox(EXTRACT_CHANGES, false);
			extractSubChangesCheckBox = new JCheckBox(EXTRACT_SUBCHANGES, false);
			extractAnnotationsCheckBox = new JCheckBox(EXTRACT_ANNOTATIONS, false);
			extractAnnotationsCheckBox = new JCheckBox(EXTRACT_ANNOTATIONS, false);
			wipeOutCheckBox = new JCheckBox(WIPE_OUT, false);
			archiveCheckBox = new JCheckBox(ARCHIVE, true);


			setLayout(new GridLayout(5,1));

			add(extractAnnotationsCheckBox);
			add(extractChangesCheckBox);
			add(extractSubChangesCheckBox);
			add(wipeOutCheckBox);
			add(archiveCheckBox);
		}

		public boolean isExtractChanges() {
			return extractChangesCheckBox.isSelected();
		}

		public boolean isExtractSubChanges() {
		    return extractSubChangesCheckBox.isSelected();
		}

		public boolean isExtractAnnotations() {
			return extractAnnotationsCheckBox.isSelected();
		}

		public boolean isArchiveCurrentVersion(){
			return archiveCheckBox.isSelected();
		}

		public boolean isWipeOut() {
		    return wipeOutCheckBox.isSelected();
		}

	}


}
