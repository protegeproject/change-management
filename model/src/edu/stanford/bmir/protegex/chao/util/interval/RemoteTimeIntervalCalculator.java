package edu.stanford.bmir.protegex.chao.util.interval;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import edu.stanford.bmir.protegex.chao.change.api.Change;

public interface RemoteTimeIntervalCalculator extends Remote {
    
    Collection<Change> getTopLevelChangesBefore(Date d) throws RemoteException;
    Collection<Change> getTopLevelChangesAfter(Date d) throws RemoteException;
    Collection<Change> getTopLevelChanges(Date start, Date end) throws RemoteException;
    void dispose() throws RemoteException;
}
