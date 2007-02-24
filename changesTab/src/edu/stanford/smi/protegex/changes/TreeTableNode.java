package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;


public class TreeTableNode {
    private Instance changeInst;
    private List<TreeTableNode> children;
    private TreeTableNode parent;

    private KnowledgeBase changeKB;
    

    public TreeTableNode(Instance changeInst, KnowledgeBase changeKB) {
		this.changeInst = changeInst;
		children = new ArrayList<TreeTableNode>();
		this.changeKB = changeKB;
    }

    
    public String toString() { 
		return ChangeCreateUtil.getActionDisplay(changeKB, changeInst);
    }

    public Object getValueAt(int i) {
    	
		Object ctxt = null;
    	switch (i) {

		case 2:
			ctxt = ChangeCreateUtil.getAuthor(changeKB, changeInst);
			
			break;
		case 3: 
			ctxt = ChangeCreateUtil.getCreated(changeKB, changeInst);
	         
			break;
		case 0: 
			ctxt = ChangeCreateUtil.getActionDisplay(changeKB, changeInst);
		   
			break;
		case 1: 
			ctxt = ChangeCreateUtil.getContext(changeKB, changeInst);
		
			break;
		}
   
		return ctxt;
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


