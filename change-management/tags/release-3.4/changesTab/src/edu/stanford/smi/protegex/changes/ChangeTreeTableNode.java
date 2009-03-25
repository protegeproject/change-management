package edu.stanford.smi.protegex.changes;

import java.util.Collection;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.Composite_Change;


public class ChangeTreeTableNode implements TreeTableNode {
    protected RootTreeTableNode root;
    private Change changeInst;


    public ChangeTreeTableNode(RootTreeTableNode root, Change changeInst) {
		this.changeInst = changeInst;
        this.root = root;
    }

    public boolean isRoot() {
        return false;
    }


    @Override
	public String toString() {
        return ChangeProjectUtil.getActionDisplay(changeInst);
    }

    public Change getChange() {
        return changeInst;
    }

    public Object getValueAt(int i) {
        ChangeTableColumn col = ChangeTableColumn.values()[i];
    	switch (col) {
		case CHANGE_COLNAME_AUTHOR:
		    return changeInst.getAuthor();
		case CHANGE_COLNAME_CREATED:
			return changeInst.getTimestamp().getDate();
		case CHANGE_COLNAME_ACTION:
		    return ChangeProjectUtil.getActionDisplay(changeInst);
		case CHANGE_COLNAME_DESCRIPTION:
		    return changeInst.getContext();
		default:
            throw new UnsupportedOperationException("Developer missed a case");
		}
    }

    public void setValueAt(Object aValue, int i) {
        throw new UnsupportedOperationException("Could not set value");
    }

	public int getChildCount() {
        if (changeInst instanceof Composite_Change) {
            return ((Composite_Change) changeInst).getSubChanges().size();
        }
        else {
            return 0;
        }
	}

	public TreeTableNode[] getChildren(){
        if (changeInst instanceof Composite_Change) {
            Collection<Change> children = ((Composite_Change) changeInst).getSubChanges();
            TreeTableNode childArray[] = new TreeTableNode[children.size()];
            int index = 0;
            for (Object o : children) {
                childArray[index++] = new ChangeTreeTableNode(root, (Change) o);
            }
            return childArray;
        } else {
			return new TreeTableNode[0];
		}
	}

	public ChangeTreeTableNode getChildAt(int i) {
		return (ChangeTreeTableNode) getChildren()[i];
    }


	public TreeTableNode getParent() {
	    Change i = changeInst.getPartOfCompositeChange();
	    if (i == null) {
            if (!root.contains(this)) {
                root.addChild(this);
            }
	        return root;
	    }
	    else {
	        return new ChangeTreeTableNode(root, i);
	    }
	}

    @Override
	public boolean equals(Object o) {
        if (!(o instanceof ChangeTreeTableNode)) {
			return false;
		}
        ChangeTreeTableNode other = (ChangeTreeTableNode) o;
        if (other.isRoot()) {
            return false;
        }
        return changeInst.equals(other.changeInst);
    }

    @Override
	public int hashCode() {
        return changeInst.hashCode();
    }

}


