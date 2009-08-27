package edu.stanford.bmir.protegex.chao.util.interval;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.server.socket.RmiSocketFactory;
import edu.stanford.smi.protege.server.socket.SSLFactory;

public class RemoteTimeIntervalCalculatorImpl extends UnicastRemoteObject
        implements RemoteTimeIntervalCalculator {
    private static final long serialVersionUID = -2238637845547157175L;
    
    private TimeIntervalCalculator delegate;
    
    public RemoteTimeIntervalCalculatorImpl(KnowledgeBase changesKb) throws RemoteException {
        super(SSLFactory.getServerPort(SSLFactory.Context.ALWAYS),
              new RmiSocketFactory(SSLFactory.Context.ALWAYS),
              new RmiSocketFactory(SSLFactory.Context.ALWAYS));
        delegate = new TimeIntervalCalculator(changesKb);
    }



    public Collection<Change> getTopLevelChanges(Date start, Date end) {
        return delegate.getTopLevelChanges(start, end);
    }

    public Collection<Change> getTopLevelChangesAfter(Date d) {
        return new ArrayList<Change>(delegate.getTopLevelChangesAfter(d));
    }

    public Collection<Change> getTopLevelChangesBefore(Date d) {
        return new ArrayList<Change>(delegate.getTopLevelChangesBefore(d));
    }
    
    public void dispose() {
        delegate.dispose();
    }

}
