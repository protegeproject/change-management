package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.changes.ui.AbstractTreeTableModel;
import edu.stanford.smi.protegex.changes.ui.TreeTableModel;
import edu.stanford.smi.protegex.server_changes.model.Model;

public class ChangeTreeTableModel extends AbstractTreeTableModel implements TreeTableModel{


	
	public static final String CHANGE_COLNAME_AUTHOR ="Author";
	public static final String CHANGE_COLNAME_CREATED ="Created";
	public static final String CHANGE_COLNAME_ACTION ="Action";
	public static final String CHANGE_COLNAME_DESCRIPTION ="Description";
	//static protected Class[]  cTypes = {TreeTableModel.class, String.class, String.class, String.class};
	
	private String[] colNames;
	private ArrayList<Instance> completeData;
	private KnowledgeBase changeKB;
    
    private TreeTableNode root;
    private Map<Instance, TreeTableNode> treeMap = new HashMap<Instance, TreeTableNode>();
	
	
	public ChangeTreeTableModel(TreeTableNode rootOfTree, KnowledgeBase changeKb) {
		super(rootOfTree);
		this.changeKB = changeKb;
		init();
	}

	// init the column names, data structures
	private void init() {
		colNames = new String[4];
	
		colNames[2] = CHANGE_COLNAME_AUTHOR;
		colNames[3] = CHANGE_COLNAME_CREATED;
		colNames[0] = CHANGE_COLNAME_ACTION;
		colNames[1] = CHANGE_COLNAME_DESCRIPTION;
		completeData = new ArrayList();
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

	
   public void addChangeData(Instance changeInst) {
		
		addChangeData(changeInst, true);
		
		
	
	}

	

	
	private void addChangeData(Instance changeInst,  boolean completeUpdate) {
        TreeTableNode newNode = null;
		String actionType = Model.getType(changeInst);   
		if (actionType != null){
			if (!actionType.equals(Model.CHANGE_LEVEL_ROOT)){
			    newNode = insertIntoModel(root, changeInst);
			    if (completeUpdate) {
			        completeData.add(changeInst);
			    }
			}
		}
		// If we have a transaction change, add the list of changes
		Cls changeInstType = changeInst.getDirectType();
		if (changeInstType.getName().equals(Model.CHANGETYPE_TRANS_CHANGE)) {

		    Collection relChanges = Model.getChanges(changeInst);
            for (Object o : relChanges) {
                Instance aInst = (Instance) o;
                insertIntoModel(newNode, aInst);
            }
		} 
			
			
		
		}
	
	
	private void addChangeData(Collection changeInsts) {
		
		for (Iterator iter = changeInsts.iterator(); iter.hasNext();) {
			Instance aInst = (Instance) iter.next();
			addChangeData(aInst,false);
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
		Slot author = changeKB.getSlot(Model.SLOT_NAME_AUTHOR);
		Slot created = changeKB.getSlot(Model.SLOT_NAME_CREATED);
		Slot action = changeKB.getSlot(Model.SLOT_NAME_ACTION);
		Slot desc = changeKB.getSlot(Model.SLOT_NAME_CONTEXT);
		
		Slot sltToSearch = null;
		if (field.equals(CHANGE_COLNAME_AUTHOR)) {
			sltToSearch = author;
		} else if (field.equals(CHANGE_COLNAME_CREATED)) {
			sltToSearch = created;
		} else if (field.equals(CHANGE_COLNAME_ACTION)) {
			sltToSearch = action;
		} else if (field.equals(CHANGE_COLNAME_DESCRIPTION)) {
			sltToSearch = desc;
		}
		Collection results = changeKB.getMatchingFrames(sltToSearch, null, false, text, 1000);
		
		for (Iterator iter = results.iterator(); iter.hasNext();) {
			Object element = (Object) iter.next();
			if (element instanceof Instance) {
				Instance someInst = (Instance) element;
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
		for (Iterator iter = completeData.iterator(); iter.hasNext();) {
			Instance aInst = (Instance) iter.next();
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
    
    private TreeTableNode insertIntoModel(TreeTableNode parent, Instance changeInst) {
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
