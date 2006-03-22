/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2005.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;

/**
 * The tabel model for the annotation specific table
 *
 */
public class AnnotationTableModel extends AbstractTableModel{

	public static final String ANNOTATE_COLNAME_AUTHOR ="Author";
	public static final String ANNOTATE_COLNAME_CREATED ="Created";
	public static final String ANNOTATE_COLNAME_TITLE ="Title";
	public static final String ANNOTATE_COLNAME_COMMENTS ="Comments";
	
	private String[] colNames;
	private ArrayList data;
	private KnowledgeBase changeKB;
	
	public AnnotationTableModel(KnowledgeBase changeKB) {
		this.changeKB = changeKB;
		
		// Setup the table column size/names
		colNames = new String[4];
		colNames[0] = ANNOTATE_COLNAME_AUTHOR;
		colNames[1] = ANNOTATE_COLNAME_CREATED;
		colNames[2] = ANNOTATE_COLNAME_TITLE;
		colNames[3] = ANNOTATE_COLNAME_COMMENTS;
		
		data = new ArrayList();
	}
	
	public void update() {
		fireTableDataChanged();
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return data.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return colNames.length;
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return colNames[0];
		case 1:
			return colNames[1];
		case 2:
			return colNames[2];
		case 3:
			return colNames[3];
		}
		
		return "";
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {
		
		// This is the instance object,
		// get the particular piece of info out of it.
		Instance aInst = (Instance)data.get(row);
		
		switch(col) {
		
		case 0:
			return ChangeCreateUtil.getAuthor(changeKB, aInst);
		case 1: 
			return ChangeCreateUtil.getCreated(changeKB, aInst);
		case 2:
			return ChangeCreateUtil.getTitle(changeKB, aInst);
		case 3: 
			return ChangeCreateUtil.getBody(changeKB, aInst);
		}
		
		return "";
	}
	
	public Object getObjInRow(int row) {
		Instance aInst = (Instance) data.get(row);
		return aInst;
	}
	
	/**
	 * @param index
	 * @return Returns the instance name associated with the given index in the table
	 */
	public String getInstanceName(int index) {
		String name;
		
		Instance annotate = (Instance)data.get(index);
		name = annotate.getName();
		
		return name;
	}
	
	/**
	 * @param annotate
	 * Add the given annotation to the internal data structure of instances
	 */
	public void addAnnotationData(Instance annotate) {
		data.add(annotate);
		fireTableRowsInserted(data.size()-1, data.size()-1);
		
	}
	
	/**
	 * @param index
	 * Remove the given annotation with the associated index
	 */
	public void removeAnnotationData(int index) {
		data.remove(index);
		fireTableRowsDeleted(data.size()-1, data.size()-1);
		
	}
	
	/**
	 * @param indicies
	 * Remove the given annotations associated with the given indicies
	 */
	public void removeAnnotationData(int[] indicies) {
		for (int i = 0; i < indicies.length; i++) {
			data.remove(indicies[i]-i);
		}
		fireTableRowsDeleted(data.size()-1, data.size()-1);
	}
	
	/**
	 * @param annotate
	 * @param index
	 * Edit the given annotation with the new annotation at the position specified by the index
	 */
	public void editAnnotationData(Instance annotate, int index) {
		data.remove(index);
		data.add(index, annotate);
		fireTableDataChanged();
	}
}
