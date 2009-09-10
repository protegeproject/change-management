package edu.stanford.smi.protegex.changes;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import edu.stanford.bmir.protegex.chao.ChAOKbManager;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.OntologyComponentFactory;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Class;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.User;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.server_changes.ChangesProject;

public class UserTest extends TestCase {
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
    
    public void testUserType() {
        OntologyComponentFactory factory = new OntologyComponentFactory(chaoKb);
        Instance a = factory.getOntology_ClassClass().createDirectInstance("test");
        assertTrue(a instanceof Ontology_Class);
        Instance u = factory.getUserClass().createDirectInstance("Timothy Redmond");
        assertTrue(u instanceof User);
    }
    
}
