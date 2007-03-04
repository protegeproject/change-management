package edu.stanford.smi.protegex.server_changes;



import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;

public class ServerChangesUtil {
    private static final Logger log = Log.getLogger(ServerChangesUtil.class);
    
    private ChangeModel model;
	
	private ServerChangesUtil(ChangeModel model) {
	    this.model = model;
    }
    

	
	public Annotation createAnnotation(String annotType,Collection annotatables) {
        Annotation a = (Annotation) model.createInstance(ChangeCls.Annotation);
        a.setAnnotates(annotatables);
        a.setCreated(Timestamp.getTimestamp(model));
        ChangeModel.logAnnotatableThing("Creating change for annotation", log, Level.FINE, a);
            // we need to determine what will trigger the listener
		return a;
	}
	
	public Annotation updateAnnotation(KnowledgeBase kb, Annotation a) {
	    a.setModified(Timestamp.getTimestamp(model));
        a.setAuthor(ChangesProject.getUserName(kb));
        if (a.getBody() == null) {
            a.setBody("");
        }
		ChangeModel.logAnnotatableThing("Updated Annotation", log, Level.FINE, a);
		return a;	
	}
    
    
    public static Collection<Instance> removeRoots(Collection<Instance> changes) {
        Collection<Instance> roots = new ArrayList<Instance>();
        for (Instance change : changes) {
          if (ChangeModel.isRoot((Change) change)) {
                roots.add(change);
            }
        }
        changes.removeAll(roots);
        return changes;
    }
 }
