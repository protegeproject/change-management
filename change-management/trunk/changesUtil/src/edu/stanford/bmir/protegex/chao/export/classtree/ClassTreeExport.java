package edu.stanford.bmir.protegex.chao.export.classtree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;


public class ClassTreeExport {

    private static Logger log = Logger.getLogger(ClassTreeExport.class.getName());

    private OWLModel owlModel;

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            log.severe("First argument should be the file or project URI, " +
            		"second argument should be the path to the exported file, " +
            		"third argument should be the top level class to export.");
            System.exit(1);
        }

        String filePath = args[0];
        String exportFilePath = args[1];
        String topClassName = args[2];

        ClassTreeExport exporter = new ClassTreeExport();
        exporter.exportClassTree(exporter.getKb(filePath), exportFilePath, topClassName);
    }


    private KnowledgeBase getKb(String uri) {

        if (uri.endsWith("pprj")) { //load pprj
            ArrayList<?> errors = new ArrayList<Object>();

            Project prj = Project.loadProjectFromURI(URIUtilities.createURI(uri), errors);
            owlModel = (OWLModel) prj.getKnowledgeBase();

            if (errors.size() > 0) {
                log.warning("There were errors at loading project " + uri);
                for (Iterator<?> iterator = errors.iterator(); iterator.hasNext();) {
                    Object object = iterator.next();
                    log.warning(object.toString());
                }
            }
        } else if (uri.endsWith(".owl") || uri.endsWith("rdf") || uri.endsWith("rdfs")) { //load OWL files
            try {
                owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
            } catch (OntologyLoadException e) {
               log.log(Level.WARNING, e.getMessage(), e);
            }
        } else {
            log.warning("Unrecognized file type " + uri);
        }

        return owlModel;
    }


    public void exportClassTree(KnowledgeBase kb, String exportFilePath, String topClassName) throws IOException {
        RDFSClass topClass = getTopClass(topClassName);
        if (topClass == null) {
            log.warning("Could not find top class: " + topClassName);
            return;
        }

        Writer w = new FileWriter(new File(exportFilePath));
        TreeExport<RDFSClass> exporter = getTreeExporter();

        log.info("Starting class tree export on " + new Date());
        exporter.printTree(topClass, w);
        log.info("Ended class tree export on " + new Date());

        w.close();
    }

    private RDFSClass getTopClass(String topClassName) {
        return owlModel.getRDFSNamedClass(topClassName);
    }

    private TreeExport<RDFSClass> getTreeExporter() {
        return new TreeExport<RDFSClass>() {

            @Override
            public String getTreeNodeName(RDFSClass treeNode) {
                return treeNode.getName();
            }

            @Override
            public String getTreeNodeDisplayText(RDFSClass treeNode) {
            	return treeNode.getBrowserText();
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public List<RDFSClass> getTreeNodeChildren(RDFSClass treeNode) {
               return (List<RDFSClass>) treeNode.getSubclasses(false);
            }

        };
    }


}
