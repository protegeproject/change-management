package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class OwlChangesPropertyListener extends PropertyAdapter{
    private PostProcessorManager changes_db;
    private ChangeFactory factory;

    public OwlChangesPropertyListener(OWLModel om) {
        changes_db = ChangesProject.getPostProcessorManager(om);
        factory = new ChangeFactory(changes_db.getChangesKb());
    }


	@Override
	public void subpropertyAdded(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Subproperty Added: ");
		context.append(arg0.getBrowserText());
		context.append(" (added to: ");
		context.append(arg1.getBrowserText());
		context.append(")");

        ServerChangesUtil.createChangeStd(changes_db, factory.createSubproperty_Added(null), arg0, context.toString());
	}

	@Override
	public void subpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
            //String browserText0 = changes_db.getPossiblyDeletedBrowserText(arg0);

            StringBuffer context = new StringBuffer();
            context.append("Subproperty Removed: ");
            context.append(arg0.getName());
            context.append(" (removed from: ");
            context.append(arg1.getBrowserText());
            context.append(")");

            ServerChangesUtil.createChangeStd(changes_db, factory.createSubproperty_Removed(null), arg0, context.toString());

	}

	@Override
	public void superpropertyAdded(RDFProperty arg0, RDFProperty arg1) {
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Added: ");
		context.append(arg0.getBrowserText());
		context.append(" (added to: ");
		context.append(arg1.getBrowserText());
		context.append(")");

        ServerChangesUtil.createChangeStd(changes_db, factory.createSuperproperty_Added(null), arg0, context.toString());
	}

	@Override
	public void superpropertyRemoved(RDFProperty arg0, RDFProperty arg1) {
        //String browserText0 = changes_db.getPossiblyDeletedBrowserText(arg0);
        //String browserText1 = changes_db.getPossiblyDeletedBrowserText(arg1);
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Removed: ");
		context.append(arg0.getName());
		context.append(" (removed from: " );
		context.append(arg1.getName());
		context.append(")");

        ServerChangesUtil.createChangeStd(changes_db, factory.createSuperproperty_Added(null), arg0, context.toString());

	}

	@Override
	public void unionDomainClassAdded(RDFProperty arg0, RDFSClass arg1) {
		String propText = arg0.getBrowserText();
		String propName = arg0.getName();
		String clsName = arg1.getName();
		String clsText = arg1.getBrowserText();

		StringBuffer context = new StringBuffer();
		context.append("Domain Property Added: ");
		context.append(propText);
		context.append("(added to: ");
		context.append(clsText);
		context.append(")");

        ServerChangesUtil.createChangeStd(changes_db, factory.createDomainProperty_Added(null), arg0, context.toString());

	}

	@Override
	public void unionDomainClassRemoved(RDFProperty arg0, RDFSClass arg1) {
		//String propText = changes_db.getPossiblyDeletedBrowserText(arg0);
		//String clsText = changes_db.getPossiblyDeletedBrowserText(arg1);

		StringBuffer context = new StringBuffer();
		context.append("Domain Property Removed: ");
		context.append(arg0.getName());
		context.append("(removed from: ");
		context.append(arg1.getName());
		context.append(")");

        ServerChangesUtil.createChangeStd(changes_db, factory.createDomainProperty_Removed(null), arg0, context.toString());

	}

}
