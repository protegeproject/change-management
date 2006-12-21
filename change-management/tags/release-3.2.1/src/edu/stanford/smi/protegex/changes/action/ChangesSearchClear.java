package edu.stanford.smi.protegex.changes.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.stanford.smi.protegex.changes.ChangeTreeTableModel;

public class ChangesSearchClear implements ActionListener{

	private ChangeTreeTableModel changeTableModel;
	
	public ChangesSearchClear(ChangeTreeTableModel ctableModel) {
		changeTableModel = ctableModel;
	}
	
	public void actionPerformed(ActionEvent arg0) {
	
		// Clear the existing search query
		changeTableModel.cancelQuery();
	}
}
