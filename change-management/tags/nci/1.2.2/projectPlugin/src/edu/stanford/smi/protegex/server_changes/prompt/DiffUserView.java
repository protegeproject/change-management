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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
import edu.stanford.smi.protegex.owl.model.OWLModel;

public class DiffUserView extends JPanel {
	private static final long serialVersionUID = 3771686927172752103L;
    
    private boolean isOwl;
    
    protected KnowledgeBase kb1;
    protected KnowledgeBase kb2;

	protected JTable userTable = ComponentFactory.createTable(null);

	private UserConceptList userConceptLists;

	protected AuthorManagement authorManagement;
	
	public enum UserColumn {
	    USER("User"), CHANGED("Changed"), CONFLICTS("Conflicts"), CONFLICTS_WITH("Conflicts with");
	    
	    private String name;
	    private UserColumn(String name) {
	        this.name = name;
	    }
	    
	    public String getName() {
	        return name;
	    }
	}

	public DiffUserView(KnowledgeBase old_kb, KnowledgeBase new_kb) {
	    kb1 = old_kb;
	    kb2 = new_kb;
        isOwl = (new_kb instanceof OWLModel);
	}

	public void setAuthorManagement(AuthorManagement authorManagement) {
		this.authorManagement = authorManagement;
		getUserConceptList().setAuthorManagement(authorManagement);
		initialize();
	}
	
	protected UserConceptList getUserConceptList() {
	    if (userConceptLists == null) {
	        userConceptLists = new UserConceptList(kb1, kb2);
	        userConceptLists.initialize();
	    }
	    return userConceptLists;
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

    @SuppressWarnings("unused")
	private void initializeUserTable() {
		userTable.setModel(createTableModel());
		DefaultRenderer renderer = new DefaultRenderer();
		for (UserColumn col : UserColumn.values()) {
		    ComponentUtilities.addColumn(userTable, renderer);
		}
	}

	private TableModel createTableModel() {
		DefaultTableModel table_model = new DefaultTableModel() {
            private static final long serialVersionUID = 6503930747934822085L;

            public boolean isCellEditable(int row, int col) {
				return false;
			}
            
		};
		for (UserColumn col : UserColumn.values()) {
		    table_model.addColumn(col.getName());
		}

		for (String user : authorManagement.getUsers()) {
			table_model.addRow(createUserTableRow(user));
		}
		return table_model;
	}
	
	private Object[] createUserTableRow(String user) {
	    Object[] row = new Object[UserColumn.values().length];
	    int conceptsNotInConflict = authorManagement.getFilteredUnConflictedFrames(user).size();
	    int conceptsInConflict = authorManagement.getFilteredConflictedFrames(user).size();
	    String conflictsWith = authorManagement.getUsersInConflictWith(user).toString();
	    row[UserColumn.USER.ordinal()] = user;
	    row[UserColumn.CONFLICTS.ordinal()] = conceptsInConflict;
	    row[UserColumn.CHANGED.ordinal()] = conceptsNotInConflict + conceptsInConflict;
	    row[UserColumn.CONFLICTS_WITH.ordinal()] = conflictsWith;
	    return row;
	}

	public Collection<String> getSelectedUsers() {

		Collection<String> selection = new ArrayList<String>();

		int[] indices = userTable.getSelectedRows();
		if (indices == null) {
			return selection;
		}

		TableModel model = userTable.getModel();
		for (int i = 0; i < indices.length; i++) {
			selection.add((String) model.getValueAt(indices[i], UserColumn.USER.ordinal()));
		}

		return selection;
	}
	
	public void setSelectedUsers(Collection<String> users) {
	    TableModel model = userTable.getModel();
	    ListSelectionModel selectionModel = userTable.getSelectionModel();
	    selectionModel.clearSelection();
	    for (int i = 0; i < model.getRowCount(); i++) {
	        if (users.contains(model.getValueAt(i, UserColumn.USER.ordinal()))) {
	            selectionModel.addSelectionInterval(i, i);
	        }
	    }

	}

	private LabeledComponent buildGUI() {
		JSplitPane result = ComponentFactory.createLeftRightSplitPane();
		result.setLeftComponent(new LabeledComponent(
						"Users with changes (select multiple rows to see changes from several users on the rigt)",
						ComponentFactory.createScrollPane(userTable), true));
		result.setRightComponent(getUserConceptList());

		synchronizeUserSelection();
		LabeledComponent listsLabeledComponent = new LabeledComponent(
				"Changed ontology components", result, true);

		listsLabeledComponent.setHeaderComponent(getHeaderComponent(), BorderLayout.EAST);
		return listsLabeledComponent;
	}

	private void synchronizeUserSelection() {
		userTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						getUserConceptList().setUserList(getSelectedUsers());
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
