package edu.stanford.smi.protegex.changes.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.stanford.smi.protegex.changes.ChangeTableModel;

public class ChangesSearchClear implements ActionListener{

	private ChangeTableModel changeTableModel;
	
	public ChangesSearchClear(ChangeTableModel ctableModel) {
		changeTableModel = ctableModel;
	}
	
	public void actionPerformed(ActionEvent arg0) {
	
		// Clear the existing search query
		changeTableModel.cancelQuery();
	}
}
