package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protegex.server_changes.model.generated.Change;

public class RootTreeTableNode implements TreeTableNode {
    List<TreeTableNode> children = new ArrayList<TreeTableNode>(); // Actually TreeTableNode
    
    public RootTreeTableNode() {

    }
    
    public boolean isRoot() {
        return true;
    }
    
    public String toString() { 
        return ChangeTableColumn.CHANGE_COLNAME_ACTION.getHeading();
    }

    public Object getValueAt(int i) {
        ChangeTableColumn col = ChangeTableColumn.values()[i];
        return col.getHeading();
    }
    
    public void setValueAt(Object v, int col) {
        throw new UnsupportedOperationException("Can't set the columns of the root");
    }

    public int getChildCount() {
        return children.size();
    }
    
    public TreeTableNode[] getChildren(){
        return children.toArray(new TreeTableNode[children.size()]);
    }

    public TreeTableNode getChildAt(int i) {
        return children.get(i);
    }
    
    
    public ChangeTreeTableNode getParent() {
        return null;
    }
    
    public int addChild(ChangeTreeTableNode node) {
        if (!children.contains(node)) {
            children.add(node);
            return children.size() - 1;
        }
        else return children.indexOf(node);
    }
    
    public int removeChild(ChangeTreeTableNode node) {
        int index = 0;
        for (Object o : children) {
            if (node.equals(o)) {
                break;
            }
            index++;
        }
        if (index < children.size()) {
            children.remove(index);
            return index;
        }
        else {
            return -1;
        }
    }
    
    public boolean contains(ChangeTreeTableNode t) {
        return children.contains(t);
    }
    
    public boolean equals(Object o) {
        return o instanceof RootTreeTableNode;
    }
    
    public  int hashCode() {
        return 42;
    }

}
