package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyComponentFactory;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.OntologyComponentCache;
import edu.stanford.smi.protegex.server_changes.PostProcessorManager;

public class RenameTest extends TestCase {
    private OWLModel model;
    private KnowledgeBase chaoKb;
    
    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
        JunitUtilities.buildScratchProject();
        List errors = new ArrayList();
        Project p = new Project(JunitUtilities.SCRATCH_PROJECT, errors);
        assertTrue(errors.isEmpty());
        model = (OWLModel) p.getKnowledgeBase();
        ChangesProject.initialize(p);
        chaoKb = ChAOKbManager.getChAOKb(model);
    }
    
    @Override
    protected void tearDown() throws Exception {
        model.dispose();
    }
    
    @SuppressWarnings("unchecked")
    public void testRename() {
        String originalName = JunitUtilities.NS + "#A";
        OWLClass a0 = model.createOWLNamedClass(originalName);
        OWLClass b = (OWLClass) a0.rename(JunitUtilities.NS + "#B");
        OWLClass a1 = model.createOWLNamedClass(originalName);
        OWLClass c = (OWLClass) a1.rename(JunitUtilities.NS + "#C");
        assertTrue(a0.equals(a1));

        OntologyComponentFactory factory = new OntologyComponentFactory(chaoKb);
        assertTrue(factory.getOntology_ComponentClass().getInstanceCount() == 2);

        assertTrue(OntologyComponentCache.getOntologyComponent(a0) == null);

        PostProcessorManager changesDb = new PostProcessorManager(model);
        assertTrue(changesDb.getOntologyComponent(a0) == null);
        assertTrue(changesDb.getOntologyComponent(b) != null);
        assertTrue(changesDb.getOntologyComponent(c) != null);
        assertTrue(!changesDb.getOntologyComponent(b).equals(changesDb.getOntologyComponent(c)));
        
        assertTrue(OntologyComponentCache.getOntologyComponent(a0) == null);
    }
    
}
