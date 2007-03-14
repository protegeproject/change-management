package edu.stanford.smi.protegex.server_changes.model;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;

public class GetSortedChangesJob extends ProtegeJob {
    
    public GetSortedChangesJob(KnowledgeBase changes_kb) {
        super(changes_kb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object run() throws ProtegeException {
        KnowledgeBase changes_kb = getKnowledgeBase();
        Cls change_cls = changes_kb.getCls(ChangeCls.Change.toString());
        List<Change> changes = new ArrayList<Change>(change_cls.getInstances());
        ChangeModel.removeRoots(changes);
        Collections.sort(changes, new ChangeDateComparator(changes_kb));
        return changes;
    }

}
