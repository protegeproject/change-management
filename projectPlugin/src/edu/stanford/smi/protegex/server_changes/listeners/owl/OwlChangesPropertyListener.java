package edu.stanford.smi.protegex.server_changes.listeners.owl;

import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.smi.protege.event.SlotEvent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.PropertyAdapter;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;

public class OwlChangesPropertyListener extends PropertyAdapter{
    private PostProcessorManager changes_db;
    private ChangeFactory factory;

    public OwlChangesPropertyListener(OWLModel om) {
        changes_db = ChangesProject.getPostProcessorManager(om);
        factory = new ChangeFactory(changes_db.getChangesKb());
    }


    @SuppressWarnings("deprecation")
    @Override
    public final void directSubslotAdded(SlotEvent event) {
        if (event.isReplacementEvent()) {
            return;
        }
        super.directSubslotAdded(event);
    }
    
	@Override
	public void subpropertyAdded(RDFProperty subProp, RDFProperty prop) {
		StringBuffer context = new StringBuffer();
		context.append("Subproperty Added: ");
		context.append(subProp.getBrowserText());
		context.append(" (added to: ");
		context.append(prop.getBrowserText());
		context.append(")");
        ServerChangesUtil.createChangeStd(changes_db, factory.createSubproperty_Added(null), subProp, context.toString());
	}
	
	@SuppressWarnings("deprecation")
    @Override
	public void directSubslotRemoved(SlotEvent event) {
        if (event.isReplacementEvent()) {
            return;
        }
	    super.directSubslotRemoved(event);
	}

	@Override
	public void subpropertyRemoved(RDFProperty subProp, RDFProperty prop) {
			//String browserText0 = changes_db.getPossiblyDeletedBrowserText(arg0);
            StringBuffer context = new StringBuffer();
            context.append("Subproperty Removed: ");
            context.append(subProp.getBrowserText());
            context.append(" (removed from: ");
            context.append(prop.getBrowserText());
            context.append(")");
            ServerChangesUtil.createChangeStd(changes_db, factory.createSubproperty_Removed(null), subProp, context.toString());
	}


	@SuppressWarnings("deprecation")
    @Override
	public void directSuperslotAdded(SlotEvent event) {
        if (event.isReplacementEvent()) {
            return;
        }
	    super.directSuperslotAdded(event);
	}
	
	@Override
	public void superpropertyAdded(RDFProperty superProp, RDFProperty prop) {
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Added: ");
		context.append(superProp.getBrowserText());
		context.append(" (added to: ");
		context.append(prop.getBrowserText());
		context.append(")");
        ServerChangesUtil.createChangeStd(changes_db, factory.createSuperproperty_Added(null), superProp, context.toString());
	}
	
	@SuppressWarnings("deprecation")
    @Override
	public void directSuperslotRemoved(SlotEvent event) {
        if (event.isReplacementEvent()) {
            return;
        }
	    super.directSuperslotRemoved(event);
	}

	@Override
	public void superpropertyRemoved(RDFProperty superProp, RDFProperty prop) {
        //String browserText0 = changes_db.getPossiblyDeletedBrowserText(arg0);
        //String browserText1 = changes_db.getPossiblyDeletedBrowserText(arg1);
		StringBuffer context = new StringBuffer();
		context.append("Superproperty Removed: ");
		context.append(superProp.getBrowserText());
		context.append(" (removed from: " );
		context.append(prop.getBrowserText());
		context.append(")");
        ServerChangesUtil.createChangeStd(changes_db, factory.createSuperproperty_Added(null), superProp, context.toString());
	}

	@Override
	public void unionDomainClassAdded(RDFProperty prop, RDFSClass cls) {
		String propText = prop.getBrowserText();		
		String clsText = cls.getBrowserText();
		StringBuffer context = new StringBuffer();
		context.append("Domain Property Added: ");
		context.append(propText);
		context.append("(added to: ");
		context.append(clsText);
		context.append(")");
        ServerChangesUtil.createChangeStd(changes_db, factory.createDomainProperty_Added(null), prop, context.toString());
	}

	@Override
	public void unionDomainClassRemoved(RDFProperty prop, RDFSClass cls) {
		//String propText = changes_db.getPossiblyDeletedBrowserText(arg0);
		//String clsText = changes_db.getPossiblyDeletedBrowserText(arg1);
		StringBuffer context = new StringBuffer();
		context.append("Domain Property Removed: ");
		context.append(prop.getBrowserText());
		context.append("(removed from: ");
		context.append(cls.getBrowserText());
		context.append(")");
        ServerChangesUtil.createChangeStd(changes_db, factory.createDomainProperty_Removed(null), prop, context.toString());
	}

}
