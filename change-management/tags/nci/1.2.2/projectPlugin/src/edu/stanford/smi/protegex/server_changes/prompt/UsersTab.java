package edu.stanford.smi.protegex.server_changes.prompt;

import java.util.Collection;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.AbstractTabWidget;

public class UsersTab extends AbstractTabWidget {
    private static final long serialVersionUID = 1289119067038250399L;

    public void initialize() {
		setLabel("UsersTab");
		
		AuthorManagement authorManagement = AuthorManagement.getAuthorManagement(null, getKnowledgeBase());
		
		if (authorManagement == null) {
			Log.getLogger().warning("Could not initialize AuthorManagement.");
			return;
		}
		
		DiffUserView diffUserView = new DiffUserView(null, getKnowledgeBase());
		diffUserView.setAuthorManagement(authorManagement);
		
		add(diffUserView);
	}
	
	//TT: Until we fix it to work in multi-user client, too.
    @SuppressWarnings("unchecked")
    public static boolean isSuitable(Project p, Collection errors) {
        if (p.isMultiUserClient()) {
        	errors.add("Works only in stand-alone mode");
        	return false;
        }
        return true;
    }

}
