 /*
  * Contributor(s): Natasha Noy noy@smi.stanford.edu
 */

package edu.stanford.smi.protegex.server_changes.prompt;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.DefaultRenderer;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.server_changes.time.ChangingFrame;
import edu.stanford.smi.protegex.server_changes.time.ChangingFrameManager;

public class DiffUserView extends JPanel {
  private static final long serialVersionUID = 3771686927172752103L;
  private DiffUserView _this;
  private JTable _userTable = ComponentFactory.createTable (null);
  private JSplitPane _contentPane = null;
  private UserConceptList _concepts = new UserConceptList();
  private AuthorManagement authorManagement;
  private ChangingFrameManager frameManager;
  
  public DiffUserView () {

   }
   
   public void setAuthorManagement(AuthorManagement authorManagement) {
       this.authorManagement = authorManagement;
       this.frameManager = authorManagement.getFrameManager();
       _concepts.setAuthorManagement(authorManagement);
       initialize();
   }
   
   private void initialize () {
       _this = this;
       initializeUserTable ();
       _contentPane = createContentPane();

       setLayout (new BorderLayout ());
       add (_contentPane, BorderLayout.CENTER);
   }
   
   private static final String [] COLUMN_NAMES = 
   	{"User", "Changed", "Conflicts", "Conflicts with"};
   
   private void initializeUserTable () {
   	_userTable.setModel(createTableModel ());
   	DefaultRenderer renderer = new DefaultRenderer ();
   	for (int i = 0; i < COLUMN_NAMES.length; i++)
   		ComponentUtilities.addColumn(_userTable, renderer);

   }

   private TableModel createTableModel () {
       DefaultTableModel model = new DefaultTableModel() {
           public boolean isCellEditable(int row, int col) {
               return false;
           }
       };
       for (int c = 0; c < COLUMN_NAMES.length; c++) {
           model.addColumn (COLUMN_NAMES[c]);
       }

       for (String user : frameManager.getUsers()) {
           Collection<ChangingFrame> frames = frameManager.getFramesTouchedByUser(user);
           int conceptsChanged = frames.size();
           int conceptsInConflict = authorManagement.getConflictedFrames(user).size();
           String conflictsWith = authorManagement.getUsersInConflictWith(user).toString();
           model.addRow(new Object []{user, new Integer (conceptsChanged), new Integer (conceptsInConflict), conflictsWith});
       }
       return model;
   }
   
   public Collection<String> getSelection () {
   	int[] indices = _userTable.getSelectedRows();
   	Collection<String> selection = new ArrayList<String>();
   	if (indices == null) return selection;
   	TableModel model = _userTable.getModel();
   	for (int i = 0; i < indices.length; i++) {
   		selection.add((String) model.getValueAt(indices[i], 0));
   	}
   	return selection;
   }
   
   private JSplitPane createContentPane () {
   	JSplitPane result = ComponentFactory.createLeftRightSplitPane();
   	result.setLeftComponent (new LabeledComponent ("Users with changes (select multiple rows to see changes from several users on the rigt)",
   	                                               ComponentFactory.createScrollPane(_userTable), true));
   	result.setRightComponent (_concepts);
   	addSelectionListener (null);
   	return result;
   }
   
	public void addSelectionListener (SelectionListener listener) {
		_userTable.getSelectionModel().addListSelectionListener(new ListSelectionListener () {
			public void valueChanged(ListSelectionEvent e) {
				_concepts.setUserList(_this.getSelection());
  	        }
	});
}
  public String toString () {
   	return "DiffUserView";
  }


}

