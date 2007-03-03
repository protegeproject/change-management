/*
 * Author(s): Natasha Noy (noy@smi.stanford.edu)
 *            Abhita Chugh (abhita@stanford.edu)
  * 
*/
package edu.stanford.smi.protegex.server_changes.prompt;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SimpleListModel;
import edu.stanford.smi.protegex.server_changes.time.ChangingFrame;

public class UserConceptList extends JPanel {
	private Collection<String> _userList = new ArrayList<String>(); 
	private JTextArea textArea = new JTextArea ();
	private JList _noConflictList, _conflictList;
	private Set<ChangingFrame> _noConflictConcepts = new HashSet<ChangingFrame> ();
	private Set<ChangingFrame> _conflictConcepts = new HashSet<ChangingFrame> ();
    
    private AuthorManagement authorManagement;

    
    public UserConceptList () {
        super ();
        setLayout (new BorderLayout ());
        add (createConceptLists (), BorderLayout.CENTER);
    } 
    
    public void setUserList (Collection<String> newUsers) {
        _userList = newUsers;
        setConceptList ();
    }
    
    public void setAuthorManagement(AuthorManagement authorManagement) {
        this.authorManagement = authorManagement;
    }
	
	private JPanel createConceptLists () {
		JPanel result = new JPanel ();
		result.setLayout (new GridLayout (0, 2, 10, 0));

		_noConflictList = createConceptList (_noConflictConcepts);
		_conflictList = createConceptList (_conflictConcepts);		
		result.add( new LabeledComponent ("Changes with NO conflict ", ComponentFactory.createScrollPane (_noConflictList)));
		result.add(new LabeledComponent ("Changes WITH conflicts", ComponentFactory.createScrollPane (_conflictList)));
		
		return result;
	}
	
	private void setConceptList () {
	    _noConflictConcepts.clear();
	    _conflictConcepts.clear();
	    for (String user : _userList) {
	        _noConflictConcepts.addAll(authorManagement.getUnConlictedFrames(user));
	        _conflictConcepts.addAll(authorManagement.getConflictedFrames(user));
	    }
	    ((SimpleListModel)_noConflictList.getModel()).setValues(_noConflictConcepts);
	    ((SimpleListModel)_conflictList.getModel()).setValues (_conflictConcepts);
	}
	
	private JList createConceptList (Collection concepts) {
		JList list = ComponentFactory.createList(null);

		list = ComponentFactory.createList(null);
		list.setCellRenderer(new ChangeTabRenderer());
		((SimpleListModel)list.getModel()).setValues(concepts);
		
		return list;
	}
	

	
}
