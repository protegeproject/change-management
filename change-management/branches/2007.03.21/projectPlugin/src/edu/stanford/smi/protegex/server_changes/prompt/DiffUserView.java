/*
 * Contributor(s): Natasha Noy noy@smi.stanford.edu
 */

package edu.stanford.smi.protegex.server_changes.prompt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.DefaultRenderer;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectionListener;

public class DiffUserView extends JPanel {
	private static final long serialVersionUID = 3771686927172752103L;

	private JTable userTable = ComponentFactory.createTable(null);

	private UserConceptList userConceptLists;

	private AuthorManagement authorManagement;

	private static final String[] COLUMN_NAMES = { "User", "Changed",
			"Conflicts", "Conflicts with" };

	public DiffUserView(KnowledgeBase old_kb, KnowledgeBase new_kb) {
		userConceptLists = new UserConceptList(old_kb, new_kb);
	}

	public void setAuthorManagement(AuthorManagement authorManagement) {
		this.authorManagement = authorManagement;
		userConceptLists.setAuthorManagement(authorManagement);
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());

		initializeUserTable();
		add(buildGUI(), BorderLayout.CENTER);
	}

	private void initializeUserTable() {
		userTable.setModel(createTableModel());
		DefaultRenderer renderer = new DefaultRenderer();
		for (int i = 0; i < COLUMN_NAMES.length; i++)
			ComponentUtilities.addColumn(userTable, renderer);

	}

	private TableModel createTableModel() {
		DefaultTableModel table_model = new DefaultTableModel() {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		for (int c = 0; c < COLUMN_NAMES.length; c++) {
			table_model.addColumn(COLUMN_NAMES[c]);
		}

		for (String user : authorManagement.getUsers()) {
			int conceptsNotInConflict = authorManagement
					.getFilteredUnConflictedFrames(user).size();
			int conceptsInConflict = authorManagement
					.getFilteredConflictedFrames(user).size();
			String conflictsWith = authorManagement
					.getUsersInConflictWith(user).toString();
			table_model.addRow(new Object[] { user,
					new Integer(conceptsNotInConflict + conceptsInConflict),
					new Integer(conceptsInConflict), conflictsWith });
		}
		return table_model;
	}

	public Collection<String> getSelection() {

		Collection<String> selection = new ArrayList<String>();

		int[] indices = userTable.getSelectedRows();
		if (indices == null) {
			return selection;
		}

		TableModel model = userTable.getModel();
		for (int i = 0; i < indices.length; i++) {
			selection.add((String) model.getValueAt(indices[i], 0));
		}

		return selection;
	}

	private LabeledComponent buildGUI() {
		JSplitPane result = ComponentFactory.createLeftRightSplitPane();
		result.setLeftComponent(new LabeledComponent(
						"Users with changes (select multiple rows to see changes from several users on the rigt)",
						ComponentFactory.createScrollPane(userTable), true));
		result.setRightComponent(userConceptLists);

		addSelectionListener(null);
		LabeledComponent listsLabeledComponent = new LabeledComponent(
				"Changed ontology components", result, true);

		listsLabeledComponent.setHeaderComponent(getHeaderCenterComponent(
				"Show anonymous ontology components",
				((authorManagement == null ? false : authorManagement
						.isShowAnonymousOntologyComponents())),
				new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						onShowAnonymousResources(e.getSource());
					}
				}));

		return listsLabeledComponent;
	}

	public void addSelectionListener(SelectionListener listener) {
		userTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						userConceptLists.setUserList(getSelection());
					}
				});
	}

	private void onShowAnonymousResources(Object source) {
		JCheckBox cb = (JCheckBox) source;

		int[] selectedIndexes = userTable.getSelectedRows();

		authorManagement.setShowAnonymousOntologyComponents(cb.isSelected());

		// reinitialize the users table
		userTable.setModel(createTableModel());

		for (int i = 0; i < selectedIndexes.length; i++) {
			userTable.addRowSelectionInterval(selectedIndexes[i],
					selectedIndexes[i]);
		}
	}

	private JComponent getHeaderCenterComponent(String text, boolean selected,
			ItemListener itemListener) {
		final JCheckBox showAllChanges = ComponentFactory.createCheckBox(text);
		showAllChanges.setSelected(selected);

		Font font = showAllChanges.getFont();
		showAllChanges.setFont(font.deriveFont(Font.BOLD, font.getSize()));
		showAllChanges.setForeground(new Color(140, 140, 140));
		showAllChanges.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(showAllChanges, BorderLayout.EAST);

		if (itemListener != null) {
			showAllChanges.addItemListener(itemListener);
		}

		return panel;
	}

}
