package edu.stanford.smi.protegex.changes.action;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.annotation.api.AnnotationFactory;
import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.ModalDialog;

public class ShowChAODetails extends AbstractAction {

	private static final long serialVersionUID = -665490014819502949L;
    private static final String EXTRACT_FROM_CHG_ONT = "Show Changes Ontology Details";
	private KnowledgeBase kb;

	public ShowChAODetails(KnowledgeBase kb) {
		super(EXTRACT_FROM_CHG_ONT);
		this.kb = kb;
	}

	public void actionPerformed(ActionEvent arg0) {
		ChAODetailsPanel detailsPane = new ChAODetailsPanel();

		ModalDialog.showDialog(ProjectManager.getProjectManager().getMainPanel(), detailsPane, "Changes ontology details", ModalDialog.MODE_CLOSE);
	}

	class ChAODetailsPanel extends JPanel {

		private static final long serialVersionUID = 4413637178054085701L;

        public ChAODetailsPanel() {
			buildGUI();
		}

		private void buildGUI() {
			setLayout(new GridLayout(4, 1, 5, 5));

			KnowledgeBase chaoKb = ChAOKbManager.getChAOKb(kb);
			Project chaoPrj = chaoKb.getProject();
			String location = chaoPrj.getProjectURI() == null ? "(unknown)" : chaoPrj.getProjectURI().toString();

			add(new JLabel("<html>Location: <b>" + location + "</b></html>"));
			add(new JLabel("<html>Backend: <b>" + (chaoPrj.isMultiUserClient() ? " Remote project (stored on Protege server)" : chaoPrj.getKnowledgeBaseFactory().getDescription()) + "</html>"));
			add(new JLabel("Number of annotations: " + new AnnotationFactory(chaoKb).getAnnotationClass().getInstanceCount()));
			add(new JLabel("Number of changes: " + new ChangeFactory(chaoKb).getChangeClass().getInstanceCount()));
		}

	}

}
