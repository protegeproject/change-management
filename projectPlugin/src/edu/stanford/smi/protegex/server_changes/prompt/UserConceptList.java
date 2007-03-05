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

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.SimpleListModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class UserConceptList extends JPanel {
    private KnowledgeBase old_kb, new_kb;
    
	private Collection<String> _userList = new ArrayList<String>(); 
	private JList _noConflictList, _conflictList;
	private Set<Ontology_Component> _noConflictConcepts = new HashSet<Ontology_Component> ();
	private Set<Ontology_Component> _conflictConcepts = new HashSet<Ontology_Component> ();
    
    private AuthorManagement authorManagement;

    
    public UserConceptList (KnowledgeBase old_kb, KnowledgeBase new_kb) {
        super ();
        this.old_kb = old_kb;
        this.new_kb = new_kb;
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
		list.setCellRenderer(new ChangeTabRenderer(old_kb, new_kb));
		((SimpleListModel)list.getModel()).setValues(concepts);
		
		return list;
	}
	

	
}
