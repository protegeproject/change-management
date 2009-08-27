package edu.stanford.bmir.protegex.chao.util.interval;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.LocalizeUtils;
import edu.stanford.smi.protege.util.ProtegeJob;

public class GetTimeIntervalCalculator extends ProtegeJob {
    private static final long serialVersionUID = -8665809749046178579L;


    public GetTimeIntervalCalculator(KnowledgeBase kb) {
        super(kb);
    }

    @Override
    public RemoteTimeIntervalCalculator run() throws ProtegeException {
        KnowledgeBase changesKb = ChAOKbManager.getChAOKb(getKnowledgeBase());
        try {
            return new RemoteTimeIntervalCalculatorImpl(changesKb);
        }
        catch (RemoteException re) {
            throw new ProtegeException(re);
        }
    }

    
    @Override
    public RemoteTimeIntervalCalculator execute() throws ProtegeException {
        return (RemoteTimeIntervalCalculator) super.execute();
    }
    
    public static RemoteTimeIntervalCalculator get(KnowledgeBase kb) {
        if (kb.getProject().isMultiUserClient()) {
            return new ClientTimeIntervalCalculator(ChAOKbManager.getChAOKb(kb),
                                                    new GetTimeIntervalCalculator(kb).execute());
        }
        else {
            return new TimeIntervalCalculator(ChAOKbManager.getChAOKb(kb));
        }
    }
    
    private static class ClientTimeIntervalCalculator implements RemoteTimeIntervalCalculator  {
        private KnowledgeBase changesKb;
        private RemoteTimeIntervalCalculator delegate;
        
        private ClientTimeIntervalCalculator(KnowledgeBase changesKb, RemoteTimeIntervalCalculator delegate) {
            this.changesKb = changesKb;
            this.delegate = delegate;
        }

        public Collection<Change> getTopLevelChanges(Date start, Date end) throws RemoteException {
            Collection<Change> changes = delegate.getTopLevelChanges(start, end);
            LocalizeUtils.localize(changes, changesKb);
            return changes;
        }

        public Collection<Change> getTopLevelChangesAfter(Date d) throws RemoteException {
            Collection<Change> changes = delegate.getTopLevelChangesAfter(d);
            LocalizeUtils.localize(changes, changesKb);
            return changes;
        }

        public Collection<Change> getTopLevelChangesBefore(Date d) throws RemoteException {
            Collection<Change> changes = delegate.getTopLevelChangesBefore(d);
            LocalizeUtils.localize(changes, changesKb);
            return changes;
        }
        
        public void dispose() throws RemoteException {
            delegate.dispose();
        }
        
        
    }
}
