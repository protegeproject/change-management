package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.util.interval.TimeIntervalUtilities;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.server_changes.ChangesProject;

public class TimeUtilitiesTest extends TestCase {
    private OWLModel model;
    private KnowledgeBase changesKb;
    
    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        JunitUtilities.buildScratchProject();
        List errors = new ArrayList();
        Project p = new Project(JunitUtilities.SCRATCH_PROJECT, errors);
        assertTrue(errors.isEmpty());
        model = (OWLModel) p.getKnowledgeBase();
        changesKb = ChAOKbManager.getChAOKb(model);
        ChangesProject.initialize(p);
    }
    
    public void testBeforeAndAfter() throws InterruptedException {
        model.createOWLNamedClass("A");
        
        Thread.sleep(100);
        Date first = new Date();
        Thread.sleep(100);
        
        model.createOWLNamedClass("B");
        
        Thread.sleep(100);
        Date second = new Date();
        Thread.sleep(100);
        
        model.createOWLNamedClass("C");
        
        Collection<Change> changes = TimeIntervalUtilities.getTopLevelChanges(changesKb, first, second);
        assertTrue(changes.size() == 1);
    }

}
