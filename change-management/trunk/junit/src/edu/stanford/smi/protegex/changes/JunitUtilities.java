package edu.stanford.smi.protegex.changes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.RemoteProjectManager;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.Shutdown;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.creator.NewOwlProjectCreator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.storage.rdf.RDFBackend;

public class JunitUtilities {
    private static final Logger log = Log.getLogger(JunitUtilities.class);
    
    public final static String NS = "http://bmir.stanford.edu/changes";
    
    public final static String SCRATCH_PROJECT = "build/projects/scratch.pprj";
    public final static String CHANGES_PROJECT = "build/projects/annotation_scratch.pprj";
    public final static String META_PROJECT    = "build/projects/metaproject.pprj";


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
    
    public static void startServer() throws IOException  {
        File jar = new File(System.getenv("PROTEGE_HOME") + "/protege.jar");
        if (!jar.exists()) {
            log.warning("Need to set PROTEGE_HOME before running server tests");
            log.warning("System tests not configured");
            throw new RuntimeException("PROTEGE_HOME not set");
        }
        System.setProperty("java.rmi.server.codebase", jar.toURL().toString());
        String [] serverArgs = {"", META_PROJECT};
        if (log.isLoggable(Level.FINE)) {
            log.fine("starting server");
        }
        Server.startServer(serverArgs);
    }
    
    public static void stopServer() {
        String [] shutdownArgs = {""};
        Shutdown.main(shutdownArgs);
    }
    
    public static OWLModel connectToServer() {
        Project p = RemoteProjectManager.getInstance().getProject("localhost", "Timothy Redmond", "troglodyte", "Scratch", true);
        return (OWLModel) p.getKnowledgeBase();
    }
    
    public static void main(String[] args) throws Exception {
        buildScratchProject();
    }

}
