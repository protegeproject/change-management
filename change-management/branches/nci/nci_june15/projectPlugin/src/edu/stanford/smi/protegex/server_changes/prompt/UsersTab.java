package edu.stanford.smi.protegex.server_changes.prompt;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.AbstractTabWidget;

public class UsersTab extends AbstractTabWidget {

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

}
