package edu.stanford.smi.protegex.server_changes.prompt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class AuthorManagement {
    private KnowledgeBase kb;
    private ChangesDb changes_db;
    private ChangeModel model;
    
    private Map<String, Set<String>> userConflictsMap = new HashMap<String, Set<String>>();
    private Map<String, Set<Ontology_Component>> conflictingFrames = new HashMap<String, Set<Ontology_Component>>();
    private Map<String, Set<Ontology_Component>> unconflictedFrames = new HashMap<String, Set<Ontology_Component>>();
    
    private Set<String> active_users  = new HashSet<String>();
    
    private boolean showAnonymousOntologyComponents = false;
    
    private AuthorManagement(KnowledgeBase kb1, KnowledgeBase kb2) {
        this.kb = kb2;
        changes_db = ChangesProject.getChangesDb(kb);
        model = changes_db.getModel();
        evaluateConflicts();
    }
    
    public static AuthorManagement getAuthorManagement(KnowledgeBase kb1, KnowledgeBase kb2) {
        if (ChangesProject.getChangesDb(kb2) != null) {
            return new AuthorManagement(kb1, kb2);
        }
        else {
            return null;
        }
    }
    
    private void evaluateConflicts() {
        
        Map<Ontology_Component, Set<String>> whoChangedMeMap = new HashMap<Ontology_Component, Set<String>>();
        
        for (Object o : model.getSortedChanges()) {
            Change change = (Change) o;
            Ontology_Component frame = (Ontology_Component) change.getApplyTo();
            Set<String> users = whoChangedMeMap.get(frame);
            if (users == null) {
                users = new HashSet<String>();
                whoChangedMeMap.put(frame, users);
            }
            String user = change.getAuthor();
            users.add(user);
            active_users.add(user);
        }
        for (Entry<Ontology_Component, Set<String>> entry : whoChangedMeMap.entrySet()) {
            Ontology_Component frame = entry.getKey();
            Set<String> users = entry.getValue();
            if (users.size() > 1) {
                for (String user : users) {
                    Set<Ontology_Component> frames = getConflictedFrames(user);
                    frames.add(frame);
                    
                    Set<String> conflictingUsers = getUsersInConflictWith(user);
                    conflictingUsers.addAll(users);
                }
            }
            else {
                for (String user : users) {
                    Set<Ontology_Component> frames = getUnConlictedFrames(user);
                    frames.add(frame);
                }
            }
        }
        for (Entry<String, Set<String>> entry : userConflictsMap.entrySet()) {
            String user = entry.getKey();
            Set<String> conflictingUsers = entry.getValue();
            conflictingUsers.remove(user);
        }   
    }

    public Set<String> getUsersInConflictWith(String user) {
        Set<String> conflictingUsers = userConflictsMap.get(user);
        if (conflictingUsers == null) {
            conflictingUsers = new HashSet<String>();
            userConflictsMap.put(user, conflictingUsers);
        }
        return conflictingUsers;
    }
    
    public Set<Ontology_Component> getConflictedFrames(String user) {
        Set<Ontology_Component> myConflictingFrames = conflictingFrames.get(user);
        if (myConflictingFrames == null) {
            myConflictingFrames = new HashSet<Ontology_Component>();
            conflictingFrames.put(user, myConflictingFrames);
        }
        
        return myConflictingFrames;
    }
   

	public Set<Ontology_Component> getUnConlictedFrames(String user) {
        Set<Ontology_Component> myUnconflictedFrames = unconflictedFrames.get(user);
        if (myUnconflictedFrames == null) {
            myUnconflictedFrames = new HashSet<Ontology_Component>();
            unconflictedFrames.put(user, myUnconflictedFrames);
        }
        
        return myUnconflictedFrames;
    }

	
	public Set<Ontology_Component> getFilteredConflictedFrames(String user) {
		  Set<Ontology_Component> myConflictingFrames = new HashSet<Ontology_Component>(getConflictedFrames(user));
		  
		  if (!showAnonymousOntologyComponents) {
	        	filterAnonymousOntologyComponent(myConflictingFrames);
	      } 
		  
		  return myConflictingFrames;
	  }

	
	public Set<Ontology_Component> getFilteredUnConflictedFrames(String user) {
		  Set<Ontology_Component> myUnConflictingFrames = new HashSet<Ontology_Component>(getUnConlictedFrames(user));
		  
		  if (!showAnonymousOntologyComponents) {
	        	filterAnonymousOntologyComponent(myUnConflictingFrames);
	      } 
		  
		  return myUnConflictingFrames;
	  }

	
	
    private void filterAnonymousOntologyComponent(Set<Ontology_Component> myConflictingFrames) {
    	for (Iterator iter = myConflictingFrames.iterator(); iter.hasNext();) {
			Ontology_Component ontoComp = (Ontology_Component) iter.next();
			if (ontoComp.isAnonymous()) {
				iter.remove();
			}
		}		
	}

	
    public Set<String> getUsers() {
        return active_users;
    }

	public boolean isShowAnonymousOntologyComponents() {
		return showAnonymousOntologyComponents;
	}

	public void setShowAnonymousOntologyComponents(
			boolean showAnonymousOntologyComponents) {
		this.showAnonymousOntologyComponents = showAnonymousOntologyComponents;
	}
    
}
