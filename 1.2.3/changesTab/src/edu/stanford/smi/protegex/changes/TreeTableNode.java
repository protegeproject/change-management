package edu.stanford.smi.protegex.changes;

public interface TreeTableNode {

    boolean isRoot();
    
    TreeTableNode getParent();
    
    int getChildCount();
    
    TreeTableNode[] getChildren();
    
    TreeTableNode getChildAt(int i);

    Object getValueAt(int col);
    
    void setValueAt(Object v, int col);
}
