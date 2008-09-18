package edu.stanford.smi.protegex.changes;

import java.util.Collections;
import java.util.List;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.bmir.protegex.chao.util.ChangeDateComparator;
import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.ProtegeJob;

public class GetSortedChangesJob extends ProtegeJob {

    public GetSortedChangesJob(KnowledgeBase changes_kb) {
        super(changes_kb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object run() throws ProtegeException {
        KnowledgeBase changes_kb = getKnowledgeBase();
        ChangeFactory factory = new ChangeFactory(changes_kb);
        List<Change> changes = (List<Change>) factory.getAllChangeObjects(true);
        ChangeProjectUtil.removeRoots(changes);
        Collections.sort(changes, new ChangeDateComparator(changes_kb));
        return changes;
    }

}
