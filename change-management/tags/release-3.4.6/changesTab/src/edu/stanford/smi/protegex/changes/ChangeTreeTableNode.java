package edu.stanford.smi.protegex.changes;

import java.util.Collection;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.Composite_Change;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.changes.ui.Filter;


public class ChangeTreeTableNode extends AbstractChangeTreeTableNode {
    protected RootTreeTableNode root;
    private Change changeInst;
    private KnowledgeBase domainKb;


    public ChangeTreeTableNode(RootTreeTableNode root, Change changeInst, Filter filter) {
    	super(filter);
		this.changeInst = changeInst;
        this.root = root;
    }

    public KnowledgeBase getDomainKb() {
        return domainKb;
    }

    public void setDomainKb(KnowledgeBase domainKb) {
        this.domainKb = domainKb;
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
		case CHANGE_COLNAME_ENTITY:
            return getEntityName(domainKb, changeInst);
		default:
            throw new UnsupportedOperationException("Developer missed a case");
		}
    }

    public static String getEntityName(KnowledgeBase domainKb, Change change) {
        String entity = "(not specified)";
        Ontology_Component oc = change.getApplyTo();
        if (oc != null) {
            String currentName = oc.getCurrentName();
            if (currentName != null) {
                if (domainKb != null) {
                    Frame frame = domainKb.getFrame(currentName);
                    return frame == null ? currentName : frame.getBrowserText();
                } else {
                    return getShortName(currentName);
                }
            } else {
                String initialName = oc.getInitialName(); //TODO TT: should use mostRecentName - but it doesn't work properly
                if (initialName != null) {
                    return "[Deleted] " + getShortName(initialName);
                }
            }
        }
        return entity;
    }

    public static String getShortName(String fullName) {
        int index = fullName.lastIndexOf("#");
        if (index > -1) {
            return fullName.substring(index + 1, fullName.length());
        } else {
            index = fullName.lastIndexOf("/");
            if (index > -1) {
                return fullName.substring(index + 1, fullName.length());
            }
        }
        return fullName;
    }

    public void setValueAt(Object aValue, int i) {
        throw new UnsupportedOperationException("Could not set value");
    }

	public int getChildCount() {
		return getChildren().length;
	}

	public TreeTableNode[] getChildren(){
        if (changeInst.canAs(Composite_Change.class)) {
            Collection<Change> children = (changeInst.as(Composite_Change.class)).getSubChanges();
            TreeTableNode childArray[] = new TreeTableNode[children.size()];
            int index = 0;
            for (Object o : children) {
                ChangeTreeTableNode changeTreeTableNode = new ChangeTreeTableNode(root, (Change) o, filter);
                changeTreeTableNode.setDomainKb(domainKb);
                childArray[index++] = changeTreeTableNode;
            }
            return filter(childArray);
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
	        ChangeTreeTableNode changeTreeTableNode = new ChangeTreeTableNode(root, i, filter);
	        changeTreeTableNode.setDomainKb(domainKb);
            return changeTreeTableNode;
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


