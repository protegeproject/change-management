package edu.stanford.smi.protegex.changes.stats;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.AnnotationCls;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;

/**
 * @author ttania
 *
 */
public class StatsTableModel extends AbstractTableModel {
	private final static String COL_USER = "User";
	private final static String COL_CHANGES = "Changes";
	private final static String COL_ANNOTATIONS = "Annotations";
	private final static String COL_TOTAL = "Total";
	
	private final static int COLNO_USER = 0;
	private final static int COLNO_CHANGES = 1;
	private final static int COLNO_ANNOTATIONS = 2;
	private final static int COLNO_TOTAL = 3;

	public static String[] columnNames = {COL_USER, COL_CHANGES, COL_ANNOTATIONS, COL_TOTAL};

	private List<String> users = new ArrayList<String>();
	
	private HashMap<String, String> user2Changes = new HashMap<String, String>();
	private HashMap<String, String> user2Annotations = new HashMap<String, String>();
		
	private KnowledgeBase changesKb;
	
	public StatsTableModel(KnowledgeBase changesKb) {
		this.changesKb = changesKb;
			
		fillStatsTable();
	}

	public void generateStatsTable() {
		clearMaps();
		fillStatsTable();
			
		fireTableDataChanged();
	}
	
	private void clearMaps() {
		users.clear();
		user2Annotations.clear();
		user2Changes.clear();			
	}

	private void fillStatsTable() {
		if (changesKb == null) {
			return;
		}
		
		ChangeModel changeModel = new ChangeModel(changesKb);

		Cls changeCls = changeModel.getCls(ChangeCls.Change);
		
		for (Iterator iter = changeCls.getInstances().iterator(); iter.hasNext();) {
			Change change = (Change) iter.next();
			
			String user = change.getAuthor();
			
			if (user != null) {
				if (!users.contains(user)) {
					users.add(user);
				}
				increaseItemValueByOne(user2Changes, user);
			}			
		}
		
		Cls annotationCls = changeModel.getCls(AnnotationCls.Annotation);
		
		for (Iterator iter = annotationCls.getInstances().iterator(); iter.hasNext();) {
			Annotation annotation = (Annotation) iter.next();
			
			String user = annotation.getAuthor();
			
			if (user != null) {
				if (!users.contains(user)) {
					users.add(user);
				}
				increaseItemValueByOne(user2Annotations, user);
			}
		}
		
		Collections.sort(users);
	}
	
	
	private int getItemCount(HashMap<String, String> map, String user) {
		String itemCount = map.get(user);
		
		if (itemCount == null) {
			return 0;
		}
	
		int count = 0;
		
		try {
			count = Integer.parseInt(itemCount); 
		} catch (Exception e) {
			Log.getLogger().warning("Error at parsing interger from string " + itemCount);
		}
		
		return count;
	}
	
	
	private void increaseItemValueByOne(HashMap<String, String> map, String user) {
		int itemCount = getItemCount(map, user);
		
		itemCount++;
		
		map.put(user, itemCount + "");		
	}
	
	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public int getRowCount() {
		return users.size();
		//return 3;
	}

	public Class getColumnClass(int colIndex) {
		return String.class;
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	};
	
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		String user = users.get(rowIndex);
		
		switch (columnIndex) {
		case COLNO_USER:
			return user;
		case COLNO_ANNOTATIONS:
			String value = user2Annotations.get(user);
			return value == null ? "0" : value;
		case COLNO_CHANGES:	 
			String value1 = user2Changes.get(user);
			return value1 == null ? "0" : value1;
		case COLNO_TOTAL:
			int changesCount = getItemCount(user2Changes, user);
			int annotationsCount = getItemCount(user2Annotations, user);
			
			return (changesCount + annotationsCount) + "";
			
		default:
			return new String("??");
		}
	}

}
