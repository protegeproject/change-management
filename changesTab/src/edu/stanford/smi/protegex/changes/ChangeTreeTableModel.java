package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.changes.ui.AbstractTreeTableModel;
import edu.stanford.smi.protegex.changes.ui.TreeTableModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;

public class ChangeTreeTableModel extends AbstractTreeTableModel implements TreeTableModel{
	
	private String[] colNames;
	private ArrayList<Instance> completeData;
    
	private KnowledgeBase changeKB;
    private ChangeModel model;
    
    private TreeTableNode root;
    private Map<Instance, TreeTableNode> treeMap = new HashMap<Instance, TreeTableNode>();
	
	
	public ChangeTreeTableModel(TreeTableNode rootOfTree, ChangeModel model) {
		super(rootOfTree);
        this.model = model;
		this.changeKB = model.getChangeKb();
		init();
	}

	// init the column names, data structures
	private void init() {
        ChangeTableColumn[] cols = ChangeTableColumn.values();
		colNames = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
            colNames[i] = cols[i].getName();
        }
		completeData = new ArrayList<Instance>();
		root = (TreeTableNode)getRoot();
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return colNames.length;
	}
	
	public Class getColumnClass(int column) {
		return column == 0 ? TreeTableModel.class : Object.class;
    }

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
        if (column < 0 || column >= colNames.length) {
            return "";
        }
        return colNames[column];
	}
	


	public int getChildCount(Object node) {
	    return ((TreeTableNode) node).getChildCount();
	}

	public Object getChild(Object node, int i) {
	    return ((TreeTableNode) node).getChildAt(i);
	}


	public Object getValueAt(Object node, int col) {
	    return ((TreeTableNode) node).getValueAt(col);
	}
	

   public void setValueAt(Object aValue, Object node, int column) {
   	((TreeTableNode) node).setValueAt(aValue, column);
   }

	
   public void addChangeData(Change changeInst) {
		addChangeData(changeInst, true);
	}

	

	
	private void addChangeData(Change changeInst,  boolean completeUpdate) {
        TreeTableNode newNode = null;

        if (!ChangeModel.isRoot(changeInst)) {
            newNode = insertIntoModel(root, changeInst);
            if (completeUpdate) {
                completeData.add(changeInst);
            }
        }
		// If we have a transaction change, add the list of changes
		if (changeInst instanceof Composite_Change) {

		    Collection relChanges = ((Composite_Change) changeInst).getSubChanges();
            for (Object o : relChanges) {
                Change aInst = (Change) o;
                insertIntoModel(newNode, aInst);
            }
		} 
			
			
		
		}


	
	public void cancelQuery() {
		setNewFilter();
	}
	
	public void setSearchQuery(String field, String text) {
		
		setNewSearch(field, text);
	}
	
	
	private void setNewSearch(String field, String text) {
		
	    clearModel();
		
        ChangeTableColumn searchColumn = null;
        for (ChangeTableColumn col : ChangeTableColumn.values()) {
            if (col.getName().equals(field)) {
                searchColumn = col;
                break;
            }
        }
        
		Slot sltToSearch = model.getSlot(searchColumn.getSearchSlot());

		Collection results = changeKB.getMatchingFrames(sltToSearch, null, false, "*" + text + "*", 1000);
		
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			if (element instanceof Change) {
				Change someInst = (Change) element;
				addChangeData(someInst, false);
		
				
			}
		}
	
	
	}
	
	
	public Object getObjInRow(int row) {
		Instance aInst = root.getChildInstanceAt(row);
		return aInst;
	}
	
	private void setNewFilter() {
        clearModel();
		for (Instance i : completeData) {
			Change aInst = (Change) i;
			addChangeData(aInst, false);
		}
	
	}
    
    private void clearModel() {
        int num = root.getChildCount();
        if(num!=0){
          int[] childIndices = new int[num];
          Object[] children = new Object[num];
          for(int i=0;i<num;i++) {
            children[i] = root.getChildAt(i);
          }
          root.removeChildren();
          fireTreeNodesRemoved(getRoot(), rootPath.getPath() , childIndices, children);
          treeMap.clear();
        }
    }
    
    private TreeTableNode insertIntoModel(TreeTableNode parent, Change changeInst) {
        TreeTableNode newNode = treeMap.get(changeInst);
        if (newNode == null) {
            newNode = new TreeTableNode(changeInst,changeKB);
            treeMap.put(changeInst, newNode);
        }
        else {
            removeFromModel(newNode);
        }
        parent.addChild(newNode);
        int[] childIndices = new int[1];
        if(parent.getChildCount()!=0)
            childIndices[0]= root.getChildCount()- 1;
        Object[] children = new Object[1];
        if(parent.getChildCount()!=0)
            children[0] = newNode;
        fireTreeNodesInserted(parent, makePath(parent), childIndices, children);
        return newNode;
    }
    
    private void removeFromModel(TreeTableNode node) {
        TreeTableNode parent = node.getParent();
        Object[] siblings = parent.getChildren();
        for (int i = 0; i < siblings.length; i++) {
            if (node.equals(siblings[i])) {;
                Object[] path = makePath(parent);
                int[] childIndices = { i };
                Object[] children = { node };
                parent.removeChild(node);
                fireTreeNodesRemoved(parent, path, childIndices, children);
                return;
            }
        }
        throw new RuntimeException("Help...");
    }
    
    private Object[] makePath(TreeTableNode node) {
        int len = 1;
        for (TreeTableNode climber = node; !climber.equals(root); climber = climber.getParent()) {
            len++;
        }
        Object[] path = new Object[len];
        TreeTableNode climber = node;
        for(int i = len - 1; i >=0; i--) {
            path[i] = climber;
            climber = climber.getParent();
        }
        return path;
    }

}
