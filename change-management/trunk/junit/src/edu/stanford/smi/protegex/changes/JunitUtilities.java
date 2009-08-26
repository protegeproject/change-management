package edu.stanford.smi.protegex.changes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.owl.jena.creator.NewOwlProjectCreator;
import edu.stanford.smi.protegex.storage.rdf.RDFBackend;

public class JunitUtilities {
    
    public final static String NS = "http://bmir.stanford.edu/changes";
    
    public final static String SCRATCH_PROJECT = "build/projects/scratch.pprj";
    public final static String CHANGES_PROJECT = "build/projects/annotation_scratch.pprj";


    public static void buildScratchProject() throws Exception {
        buildOwlProject();
        buildChangesProject();
    }
    
    @SuppressWarnings("unchecked")
    private static void buildOwlProject() throws Exception {
        NewOwlProjectCreator creator = new NewOwlProjectCreator();
        creator.setOntologyName(NS);
        List errors = new ArrayList();
        creator.create(errors);
        if (!errors.isEmpty()) {
            throw new OntologyLoadException();
        }
        creator.getOwlModel().save(new File("build/projects/scratch.owl").toURI());
    }
    
    @SuppressWarnings("unchecked")
    private static void buildChangesProject() {
        List errors = new ArrayList();
        Project changes = new Project("projects/changes.pprj", errors);
        RDFBackend.setSourceFiles(changes.getSources(), "annotation_scratch.rdfs", "annotation_scratch.rdf", "http://protege.stanford.edu/kb#");
        if (!errors.isEmpty())  {
            throw new RuntimeException("Could not open changes project");
        }
        changes.setProjectFilePath(CHANGES_PROJECT);
        changes.save(errors);
        if (!errors.isEmpty())  {
            throw new RuntimeException("Could not open changes project");
        }
    }
    
    public static void main(String[] args) throws Exception {
        buildScratchProject();
    }

}
