package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class OwlChangesClassListener extends ClassAdapter {
    private OWLModel om;
    private PostProcessorManager changes_db;
    private ChangeFactory factory;

    public OwlChangesClassListener(OWLModel om) {
        this.om = om;
        changes_db = ChangesProject.getPostProcessorManager(om);
        factory = new ChangeFactory(changes_db.getChangesKb());
    }

	@Override
	public void instanceAdded(RDFSClass clz, RDFResource inst, ClsEvent event) {
	    if (event.isReplacementEvent()) {
	        return;
	    }
		String instText = inst.getBrowserText();
		String instName = inst.getName();
		StringBuffer context = new StringBuffer();
		context.append("Instance Added: ");
		context.append(instText);
		context.append(" (instance of: " );
		context.append(clz.getBrowserText() );
		context.append(")");

        ServerChangesUtil.createChangeStd(changes_db, factory.createIndividual_Added(null), inst, context.toString());
	}

	@Override
	public void instanceRemoved(RDFSClass clz, RDFResource inst, ClsEvent event) {
	        if (event.isReplacementEvent()) {
	            return;
	        }
            PostProcessorManager changesDb = ChangesProject.getPostProcessorManager(om);
            //String instText = changesDb.getPossiblyDeletedBrowserText(arg1);
            String instText = inst.getName();
            StringBuffer context = new StringBuffer();
            context.append("Instance Removed: ");
            context.append(instText);
            context.append(" (instance of: ");
            context.append(clz.getBrowserText());
            context.append(")");

            ServerChangesUtil.createChangeStd(changesDb, factory.createIndividual_Removed(null), inst, context.toString());
	}

	@Override
	public void addedToUnionDomainOf(RDFSClass arg0, RDFProperty arg1) {
	}

	@Override
	public void removedFromUnionDomainOf(RDFSClass arg0, RDFProperty arg1) {
	}

	@Override
	public void subclassAdded(RDFSClass arg0, RDFSClass arg1, ClsEvent event) {
	    if (event.isReplacementEvent()) {
	        return;
	    }
		String clsName = arg1.getName();
		String clsText = arg1.getBrowserText();
		StringBuffer context = new StringBuffer();
		context.append("Subclass Added: ");
		context.append(clsName);
		context.append(" (added to: ");
		context.append(arg0.getBrowserText());
		context.append(")");

        ServerChangesUtil.createChangeStd(changes_db, factory.createSubclass_Added(null), arg1, context.toString());
	}

	@Override
	public void subclassRemoved(RDFSClass clz, RDFSClass inst, ClsEvent event) {
	       if (event.isReplacementEvent()) {
	            return;
	        }
            //String clsName = changes_db.getPossiblyDeletedBrowserText(arg1);
            StringBuffer context = new StringBuffer();
            context.append("Subclass Removed: ");
            context.append(inst.getName());
            context.append(" (removed from: ");
            context.append(clz.getBrowserText());
            context.append(")");

            ServerChangesUtil.createChangeStd(changes_db, factory.createSubclass_Removed(null), inst, context.toString());
	}

	@Override
	public void superclassAdded(RDFSClass arg0, RDFSClass arg1) {
		String clsName = arg1.getName();
		String clsText = arg1.getBrowserText();
		StringBuffer context = new StringBuffer();
		context.append("Superclass Added: ");
		context.append(clsText);
		context.append(" (added to: ");
		context.append(arg0.getBrowserText());
		context.append(")");

        ServerChangesUtil.createChangeStd(changes_db, factory.createSuperclass_Added(null), arg1, context.toString());
	}

	@Override
	public void superclassRemoved(RDFSClass arg0, RDFSClass arg1) {
		//String clsName = changes_db.getPossiblyDeletedBrowserText(arg1);
		StringBuffer context = new StringBuffer();
		context.append("Superclass Removed: ");
		context.append(arg1.getName());
		context.append(" (removed from: ");
		context.append(arg0.getBrowserText());
		context.append(")");

        ServerChangesUtil.createChangeStd(changes_db, factory.createSuperclass_Removed(null), arg1, context.toString());
	}
}
