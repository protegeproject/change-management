package edu.stanford.bmir.protegex.chao.util.interval;

import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.ChangeFactory;
import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protegex.changes.ChangeProjectUtil;

public class GetTopLevelChangesTreeMapJob extends ProtegeJob {
    private static final long serialVersionUID = -8678035299685334982L;

    public GetTopLevelChangesTreeMapJob(KnowledgeBase changesKb) {
        super(changesKb);
    }

    /* If the changes KB is in a database, we will use directly SQL queries
     * to retrieve the top level changes. This is much faster than doing it through
     * the API. The fallback uses the default mechanism.
     *
     * @see edu.stanford.smi.protege.util.RemoteJob#run()
     */
    @Override
    public TreeMap<SimpleTime, Change> run() throws ProtegeException {
        KnowledgeBase chaoKb = getKnowledgeBase();

        TreeMap<SimpleTime, Change> changeMap = null;

        //try to use the direct database calls (much faster)
        if (chaoKb.getKnowledgeBaseFactory() instanceof DatabaseKnowledgeBaseFactory) {
            try {
                GetDirectDbTopLevelChanges dbChange = new GetDirectDbTopLevelChanges(chaoKb);
                changeMap = dbChange.fillChanges();
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "Failed to fill top level changes cache directly from the database. Trying the exoensive way..", e);
            }
        }

        //if not database, use the API way
        if (changeMap == null) {
            changeMap = fillChanges();
        }

        return changeMap;
    }

    private TreeMap<SimpleTime, Change> fillChanges() {
        KnowledgeBase changes_kb = getKnowledgeBase();
        ChangeFactory factory = new ChangeFactory(changes_kb);
        List<Change> changes = (List<Change>) factory.getAllChangeObjects(true);
        TreeMap<SimpleTime, Change> changeMap = new TreeMap<SimpleTime, Change>();
        for (Object o : changes) {
            Change change = (Change) o;
            if (!ChangeProjectUtil.isRoot(change) && change.getPartOfCompositeChange() == null
                    && change.getTimestamp().hasDate()) {
                changeMap.put(new SimpleTime(change.getTimestamp()), change);
            }
        }
        return changeMap;
    }


    @SuppressWarnings("unchecked")
    @Override
    public TreeMap<SimpleTime, Change> execute() throws ProtegeException {
        return (TreeMap) super.execute();
    }

}
