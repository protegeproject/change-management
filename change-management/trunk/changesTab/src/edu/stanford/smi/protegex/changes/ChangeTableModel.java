package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;


public class ChangeTableModel extends AbstractTableModel {
    public enum FilterMethod {
        FILTER_TRANS, FILTER_TRANS_INFO, SHOW_ALL
    }
	
    //static protected Class[]  cTypes = {String.class, String.class, String.class, Icon.class, String.class};
	private String[] colNames;
	private List<Change> completeData;
	private List<Change> workingData;
	
	// Used for coloring the table
	private ArrayList colorList;
	private Integer currColor = new Integer(-1);
	
	private FilterMethod filterMethod = FilterMethod.FILTER_TRANS;
	private KnowledgeBase changeKB;
    ChangeModel model;
	
	public ChangeTableModel(ChangeModel model) {
        this.model = model;
		this.changeKB = model.getChangeKb();
		init();
	}
	
	public ChangeTableModel(ChangeModel model, FilterMethod filter) {
        this.model = model;
		this.changeKB = model.getChangeKb();
		filterMethod = filter;
		init();
	}
	
	// init the column names, data structures
	private void init() {
        ChangeTableColumn[] cols = ChangeTableColumn.values();
		colNames = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
            colNames[i] = cols[i].getName();
        }
		
		workingData = new ArrayList<Change>();
		completeData = new ArrayList<Change>();
		colorList = new ArrayList();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return workingData.size();
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
        if (column < 0 || column > colNames.length) {
            return "";
        }
        return colNames[column];
	}
	
	/*public Class getColumnClass(int c) {
		return cTypes[c];
        //return getValueAt(0, c).getClass();
    }*/
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {
		
		// This is the instance object,
		// get the particular piece of info out of it.
		Change aInst = workingData.get(row);

		
		Object ctxt = null;
		if (col < 0 || col >= ChangeTableColumn.values().length) {
		    return null;
        }
        
		switch(ChangeTableColumn.values()[col]) {
		case CHANGE_COLNAME_AUTHOR:
			return  aInst.getAuthor();
		case CHANGE_COLNAME_CREATED: 
            return ((Timestamp) aInst.getTimestamp()).getDate();
		case CHANGE_COLNAME_ACTION:
			return ChangeCreateUtil.getActionDisplay(aInst);
		case CHANGE_COLNAME_DESCRIPTION: 
		    return aInst.getContext();
		default:
		    throw new UnsupportedOperationException("Developer missed a case");
		}
	}
	
	public Object getObjInRow(int row) {
		Instance aInst = workingData.get(row);
		return aInst;
	}
	
	/**
	 * @param annotate
	 * Add the given annotation to the internal data structure
	 */
	public void addChangeData(Change changeInst) {
		
		addChangeData(changeInst, true);
		fireTableRowsInserted(workingData.size()-1, workingData.size()-1);
	}
	
	public void update() {
		fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
    private void addChangeData(Change changeInst, boolean completeUpdate) {
		boolean isTrans = false;
		boolean added = false;
			
		if (!ChangeModel.isRoot(changeInst)) {
            switch (filterMethod) {
            case FILTER_TRANS:
                if (!(changeInst instanceof Composite_Change)) {
                    workingData.add(changeInst);
                    colorList.add(currColor);
                    added = true;
                }
                break;
            case FILTER_TRANS_INFO:
                if (changeInst.getPartOfCompositeChange() == null) {
                    workingData.add(changeInst);
                    colorList.add(currColor);
                    added = true;
                }
                break;
            case SHOW_ALL:
                workingData.add(changeInst);
                colorList.add(currColor);
                added = true;
                break;
            default:
                throw new UnsupportedOperationException("Developer missed a case");
            }

            isTrans = changeInst instanceof Composite_Change;
			
			if (!isTrans && added) {
				updateCurrColor();
			}
			
			if (completeUpdate) {
				completeData.add(changeInst);
			}
		}
    }
	
	
	
	/**
	 * @param changeInsts
	 * Set the given data structure to the given collectin of instances
	 */
	public void setChanges(Collection<Instance> changeInsts) {
		workingData.clear();
        completeData.clear();
		colorList.clear();
		for (Instance i : changeInsts) {
			Change cInst = (Change) i;
			workingData.add(cInst);
            completeData.add(cInst);
			colorList.add(currColor);
			updateCurrColor();
		}
		fireTableDataChanged();
	}
	
	/**
	 * Clear all instances in the current data structure
	 */
	public void clearData() {
		workingData.clear();
		colorList.clear();
		completeData.clear();
		
		fireTableDataChanged();
	}
	
	public void cancelQuery() {
		setNewFilter();
	}
	
	public void setSearchQuery(String field, String text) {
		
		setNewSearch(field, text);
	}
	
	public void filterTrans() {
		filterMethod = FilterMethod.FILTER_TRANS;
		setNewFilter();
	}
	
	public void filterTransInfo() {
		filterMethod = FilterMethod.FILTER_TRANS_INFO;
		setNewFilter();
	}

	public void filterAll() {
		filterMethod = FilterMethod.SHOW_ALL;
		setNewFilter();
	}
	
	private void initCurrColor() {
		currColor = new Integer(-1);
	}
	
	private void updateCurrColor() {
		currColor = new Integer(-currColor.intValue());
	}
	
	private void setNewSearch(String field, String text) {
		workingData.clear();
		colorList.clear();
		
        ChangeTableColumn search_column = null;
        for (ChangeTableColumn col : ChangeTableColumn.values()) {
            if (field.equals(col.getName())) {
                search_column = col;
            }
        }
		
		Slot sltToSearch = model.getSlot(search_column.getSearchSlot());
		Collection results = changeKB.getMatchingFrames(sltToSearch, null, false, text, 1000);
		
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			if (element instanceof Change) {
				Change someInst = (Change) element;
				addChangeData(someInst, false);
			}
		}
		
		fireTableDataChanged();
	}
	
	private void setNewFilter() {
		workingData.clear();
		colorList.clear();
		
		//initCurrColor();
		for (Change aInst : completeData) {
			addChangeData(aInst, false);
		}
		
		fireTableDataChanged();
	}
}

