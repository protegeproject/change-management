package edu.stanford.smi.protegex.changes.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import edu.stanford.smi.protegex.changes.ChangeTableModel;

public class ChangesSearchExecute implements ActionListener{

	JComboBox searchField;
	JTextField searchText;
	ChangeTableModel changeTableModel;
	
	public ChangesSearchExecute(JComboBox field, JTextField text, ChangeTableModel ctableModel) {
		searchField = field;
		searchText = text;
		changeTableModel = ctableModel;
	}
	
	public void actionPerformed(ActionEvent arg0) {
		String field = (String) searchField.getSelectedItem();
		String text = searchText.getText();
		
		// Set new search query in the change table
		changeTableModel.setSearchQuery(field, text);
	}
}
