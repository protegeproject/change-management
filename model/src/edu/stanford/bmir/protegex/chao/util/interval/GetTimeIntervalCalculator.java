package edu.stanford.bmir.protegex.chao.util.interval;

import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.ProtegeJob;

public class GetTimeIntervalCalculator extends ProtegeJob {
    private static final long serialVersionUID = -8665809749046178579L;


    public GetTimeIntervalCalculator(KnowledgeBase kb) {
        super(kb);
    }

    @Override
    public RemoteTimeIntervalCalculator run() throws ProtegeException {
        KnowledgeBase changesKb = ChAOKbManager.getChAOKb(getKnowledgeBase());
        return new TimeIntervalCalculator(changesKb);
    }

    
    @Override
    public RemoteTimeIntervalCalculator execute() throws ProtegeException {
        return (RemoteTimeIntervalCalculator) super.execute();
    }
    
    public static RemoteTimeIntervalCalculator get(KnowledgeBase kb) {
        return new GetTimeIntervalCalculator(kb).execute();
    }
}
