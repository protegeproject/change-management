/*
 * Contributor(s): Natasha Noy noy@smi.stanford.edu
 */

package edu.stanford.smi.protegex.server_changes.prompt;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class DiffUserView extends JPanel {
	private static final long serialVersionUID = 3771686927172752103L;
    

    
    private boolean isOwl;

	private JTable userTable = ComponentFactory.createTable(null);

	private UserConceptList userConceptLists;

	private AuthorManagement authorManagement;

	private static final String[] COLUMN_NAMES = { "User", "Changed",
			"Conflicts", "Conflicts with" };

	public DiffUserView(KnowledgeBase old_kb, KnowledgeBase new_kb) {
		userConceptLists = new UserConceptList(old_kb, new_kb);
        isOwl = (new_kb instanceof OWLModel);
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
    
    private void reinitialize() {
        authorManagement.reinitialize();
        userTable.setModel(createTableModel());
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

		listsLabeledComponent.setHeaderComponent(getHeaderComponent(), BorderLayout.EAST);
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
    
    private JComponent getHeaderComponent() {
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        panel.add(getConfigureButton());
        panel.add(getRefreshButton());
        return panel;
    }

	private JComponent getConfigureButton() {
	    JButton button = new JButton("Set Filters");
        button.setSize(button.getMinimumSize());
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                popupFiltersDialog();
            }
        });
        return button;
    }
    
    private void popupFiltersDialog() {

        final FilterPanel filterPanel = new FilterPanel(isOwl, authorManagement.getFilters());
        ModalDialog.showDialog(this, filterPanel, "Filters Panel", ModalDialog.MODE_OK_CANCEL,
                               new ModalDialog.CloseCallback() {

            public boolean canClose(int result) {
                if (result == ModalDialog.OPTION_OK) {
                    authorManagement.setFilters(filterPanel.getResult());
                    // reinitialize the users table
                    userTable.setModel(createTableModel());
                }
                return true;
            }
        });

	}
    
    private JComponent getRefreshButton() {
        JButton button = new JButton("Recalculate");
        button.setSize(button.getMinimumSize());
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reinitialize();
            }
        });
        return button;
    }

}
