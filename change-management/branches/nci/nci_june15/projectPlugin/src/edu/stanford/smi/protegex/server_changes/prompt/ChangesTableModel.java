package edu.stanford.smi.protegex.server_changes.prompt;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import edu.stanford.smi.protege.model.Instance;

import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;

public class ChangesTableModel extends DefaultTableModel {
	private static final String[] COLUMN_NAMES =
	   	{"Context", "Applied to", "Author", "Timestamp"};

	private Ontology_Component ontologyComp;
	
	private List<Instance> changesList;
	
	private boolean showAllChanges = false;
	
		
	public ChangesTableModel(Ontology_Component ontologyComp) {
		super();
		this.ontologyComp = ontologyComp;
		//this.changesList = (ontologyComp == null ? new ArrayList<Instance>() : (ontologyComp).getSortedTopLevelChanges());
		refillTableValues();
		
		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			addColumn(COLUMN_NAMES[i]);
		}		
	}
	
	@Override
	public Class getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return Instance.class;
		case 2:
			return String.class;
		case 3:
			return Timestamp.class;
		default:
			return String.class;
		}		
	}
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}
	
	@Override
	public int getRowCount() {
		return (changesList == null ? 0 : changesList.size());
	}
	
	public void setOntologyComponent(Ontology_Component ontComp) {
		this.ontologyComp = ontComp;
		
		refillTableValues();
		fireTableDataChanged();
	}

	@Override
	public Object getValueAt(int row, int column) {	
		switch (column) {
		case 0:
			return ((Change)changesList.get(row)).getContext();
		case 1:
			return ((Change)changesList.get(row)).getApplyTo();
		case 2:
			return ((Change)changesList.get(row)).getAuthor();
		case 3:
			return ((Change)changesList.get(row)).getTimestamp();
		default:
			return null;
		}				
	}
	
	
	private void refillTableValues() {
		changesList = (ontologyComp == null ? new ArrayList<Instance>() : 
			(showAllChanges ? (ontologyComp).getSortedChanges() :(ontologyComp).getSortedTopLevelChanges()));		
	}
	
	public Change getChange(int row) {
		if (row >= 0 && row < changesList.size()) {
			return (Change) changesList.get(row);
		}
		
		return null;		
	}
	
	public void setShowAllChanges(boolean showAllChanges) {
		this.showAllChanges = showAllChanges;
		
		refillTableValues();
		fireTableDataChanged();		
	}
	
}
