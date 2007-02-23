package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.changes.ui.AbstractTreeTableModel;
import edu.stanford.smi.protegex.changes.ui.TreeTableModel;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class ChangeTreeTableModel extends AbstractTreeTableModel implements TreeTableModel{


	
	public static final String CHANGE_COLNAME_AUTHOR ="Author";
	public static final String CHANGE_COLNAME_CREATED ="Created";
	public static final String CHANGE_COLNAME_ACTION ="Action";
	public static final String CHANGE_COLNAME_DESCRIPTION ="Description";
	//static protected Class[]  cTypes = {TreeTableModel.class, String.class, String.class, String.class};
	
	private String[] colNames;
	private ArrayList<Instance> completeData;
	private KnowledgeBase changeKB;
    private static TreeTableNode root;
	 
	
	
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
	
	public void update() {
		
		int[] childIndices = new int[1];
		if(root.getChildCount()!=0)
	      childIndices[0]= root.getChildCount()- 1;
		Object[] children = new Object[1];
	    if(root.getChildCount()!=0)
	      children[0] = root.getChildAt(childIndices[0]);
		fireTreeNodesChanged(getRoot(), rootPath.getPath() , childIndices, children);
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
	
		
		TreeTableNode newNode = new TreeTableNode(changeInst,changeKB);
		String actionType = ChangeCreateUtil.getType(changeKB, changeInst);
		if (actionType != null){
			if(!actionType.equals(ServerChangesUtil.CHANGE_LEVEL_TRANS_INFO)&& !actionType.equals("ROOT")){
				 root.addChild(newNode);
				 int[] childIndices = new int[1];
					if(root.getChildCount()!=0)
				      childIndices[0]= root.getChildCount()- 1;
					Object[] children = new Object[1];
				    if(root.getChildCount()!=0)
				      children[0] = root.getChildAt(childIndices[0]);
					fireTreeNodesInserted(getRoot(), rootPath.getPath() , childIndices, children);
				  if (completeUpdate) {
					 completeData.add(changeInst);
					}
				
			}
		}
		
			
		

			// If we have a transaction change, add the list of changes
			Cls changeInstType = changeInst.getDirectType();
			if (changeInstType.getName().equals(ServerChangesUtil.CHANGETYPE_TRANS_CHANGE)) {
			
				Collection relChanges = ChangeCreateUtil.getChanges(changeKB, changeInst);
				
				for (Iterator iter = relChanges.iterator(); iter.hasNext();) {
					Instance aInst = (Instance) iter.next();
					TreeTableNode transChild = new TreeTableNode(aInst,changeKB);
					newNode.addChild(transChild) ;        //if the change is a transaction, its children are the associated changes
				
					
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
		
		int i;
		
		int num = root.getChildCount();
		if(num!=0){
		  int[] childIndices = new int[num];
	      Object[] children = new Object[num];
	      for(i=0;i<num;i++)
	        children[i] = root.getChildAt(i);
	      root.removeChildren();
		  fireTreeNodesRemoved(getRoot(), rootPath.getPath() , childIndices, children);
		}
		Slot author = changeKB.getSlot(ServerChangesUtil.SLOT_NAME_AUTHOR);
		Slot created = changeKB.getSlot(ServerChangesUtil.SLOT_NAME_CREATED);
		Slot action = changeKB.getSlot(ServerChangesUtil.SLOT_NAME_ACTION);
		Slot desc = changeKB.getSlot(ServerChangesUtil.SLOT_NAME_CONTEXT);
		
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
        int i;
		
		int num = root.getChildCount();
		if(num!=0){
		  int[] childIndices = new int[num];
	      Object[] children = new Object[num];
	      for(i=0;i<num;i++)
	        children[i] = root.getChildAt(i);
	      root.removeChildren();
		  fireTreeNodesRemoved(getRoot(), rootPath.getPath() , childIndices, children);
		}
		
	
		for (Iterator iter = completeData.iterator(); iter.hasNext();) {
			Instance aInst = (Instance) iter.next();
			addChangeData(aInst, false);
	
		}
	
	}

}
