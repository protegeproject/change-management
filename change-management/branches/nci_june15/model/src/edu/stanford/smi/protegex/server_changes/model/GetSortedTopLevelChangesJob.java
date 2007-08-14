package edu.stanford.smi.protegex.server_changes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;

public class GetSortedTopLevelChangesJob extends ProtegeJob {
    public GetSortedTopLevelChangesJob(KnowledgeBase changes_kb) {
        super(changes_kb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object run() throws ProtegeException {
        KnowledgeBase changes_kb = getKnowledgeBase();
        Cls change_cls = changes_kb.getCls(ChangeCls.Change.toString());
        Collection changes = change_cls.getInstances();
        List<Change> top_level_changes = new ArrayList<Change>();
        for (Object o : changes) {
            Change change = (Change) o;
            if (!ChangeModel.isRoot(change) && change.getPartOfCompositeChange() == null) {
                top_level_changes.add(change);
            }
        }
        Collections.sort(top_level_changes, new ChangeDateComparator(changes_kb));
        return top_level_changes;
    }
}
