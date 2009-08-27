package edu.stanford.smi.protegex.changes;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.util.interval.GetTimeIntervalCalculator;
import edu.stanford.bmir.protegex.chao.util.interval.RemoteTimeIntervalCalculator;
import edu.stanford.bmir.protegex.chao.util.interval.TimeIntervalCalculator;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.server_changes.ChangesProject;

public class TimeUtilitiesTest extends TestCase {
    private OWLModel model;
    
    private OWLNamedClass a;
    private OWLNamedClass b;
    private OWLNamedClass c;
    private Date first;
    private Date second;
    
    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        JunitUtilities.buildScratchProject();
        List errors = new ArrayList();
        Project p = new Project(JunitUtilities.SCRATCH_PROJECT, errors);
        assertTrue(errors.isEmpty());
        model = (OWLModel) p.getKnowledgeBase();
        ChangesProject.initialize(p);
    }
    
    public void testBeforeAndAfter() throws InterruptedException, RemoteException {
        makeChanges();
        TimeIntervalCalculator t = new TimeIntervalCalculator(ChAOKbManager.getChAOKb(model));
        checkChanges(t);
    }
    

    
    public void testServer() throws IOException, InterruptedException {
        JunitUtilities.startServer();
        try {
            model = JunitUtilities.connectToServer();
            makeChanges();
            RemoteTimeIntervalCalculator t = GetTimeIntervalCalculator.get(model);
            checkChanges(t);
        }
        finally {
            JunitUtilities.stopServer();
        }
    }
    
    private void makeChanges() throws InterruptedException {
        a = model.createOWLNamedClass("A");

        Thread.sleep(1000);
        first = new Date();
        Thread.sleep(1000);

        b = model.createOWLNamedClass("B");

        Thread.sleep(1000);
        second = new Date();
        Thread.sleep(1000);

        c = model.createOWLNamedClass("C");
    }
    
    private void checkChanges(RemoteTimeIntervalCalculator t) throws RemoteException {
        boolean foundA;
        boolean foundB;
        boolean foundC;

        
        Collection<Change> changes = t.getTopLevelChanges(first, second);
        for (Change change : changes) {
            assertTrue(change.getApplyTo().getCurrentName().equals(b.getName()));
        }
        
        foundB = foundC = false;
        changes = t.getTopLevelChangesAfter(first);
        for (Change change : changes) {
            if (change.getApplyTo().getCurrentName().equals(b.getName()) && !foundC) {
                foundB = true;
            }
            else if (change.getApplyTo().getCurrentName().equals(c.getName())) {
                foundC = true;
            }
            else {
                fail();
            }
        }
        assertTrue(foundB && foundC);
        
        foundA = foundB = false;
        changes = t.getTopLevelChangesBefore(second);
        for (Change change : changes) {
            if (change.getApplyTo().getCurrentName().equals(a.getName()) && !foundB) {
                foundA = true;
            }
            else if (change.getApplyTo().getCurrentName().equals(b.getName())) {
                foundB = true;
            }
            else {
                fail();
            }
        }
        assertTrue(foundA && foundB);
    }

}
