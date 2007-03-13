package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;


public class TreeTableNode {
    private Change changeInst;
    private List<TreeTableNode> children;
    private TreeTableNode parent;
    

    public TreeTableNode(Change changeInst, KnowledgeBase changeKB) {
		this.changeInst = changeInst;
		children = new ArrayList<TreeTableNode>();
    }

    
    public String toString() { 
        if (ChangeModel.isRoot(changeInst)) {
            return ChangeTableColumn.CHANGE_COLNAME_ACTION.getHeading();
        }
        else {
            return ChangeCreateUtil.getActionDisplay(changeInst);
        }
    }
    
    public Change getChange() {
        return changeInst;
    }

    public Object getValueAt(int i) {
        ChangeTableColumn col = ChangeTableColumn.values()[i];
        if (ChangeModel.isRoot(changeInst)) {
            return col.getHeading();
        }
    	switch (col) {
		case CHANGE_COLNAME_AUTHOR:
		    return changeInst.getAuthor();
		case CHANGE_COLNAME_CREATED: 
			return ((Timestamp) changeInst.getTimestamp()).getDate();
		case CHANGE_COLNAME_ACTION: 
		    return ChangeCreateUtil.getActionDisplay(changeInst);
		case CHANGE_COLNAME_DESCRIPTION: 
		    return changeInst.getContext();
		default:
            throw new UnsupportedOperationException("Developer missed a case");
		}
    }

    public void setValueAt(Object aValue, int i) {
    }

	public int getChildCount() {
		return children.size();
	}
	
	public Object[] getChildren(){
		return children.toArray();
	}

	public TreeTableNode getChildAt(int i) {
		return (TreeTableNode) children.get(i);
    }
    
    
    public TreeTableNode getParent() {
        return parent;
    }

    public void addChild(TreeTableNode child) {
       child.parent = this;
       children.add(child);    
    }
    
    public void removeChild(TreeTableNode child) {
        child.parent = null;
        children.remove(child);
    }
    
    public Instance getChildInstanceAt(int i){
    	return ((TreeTableNode) children.get(i)).changeInst;
    }
    
    public void removeChildren() {
        for (TreeTableNode child : children) {
            child.parent = null;
        }
    	children.clear();
    }
}


