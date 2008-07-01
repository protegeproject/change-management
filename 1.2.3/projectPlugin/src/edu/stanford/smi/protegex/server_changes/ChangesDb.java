package edu.stanford.smi.protegex.server_changes;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.AnnotationCls;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeSlot;
import edu.stanford.smi.protegex.server_changes.model.generated.AnnotatableThing;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation_Removed;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Created_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Deleted_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Name_Changed;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;
import edu.stanford.smi.protegex.server_changes.postprocess.PostProcessor;
import edu.stanford.smi.protegex.server_changes.util.Util;
import edu.stanford.smi.protegex.storage.rdf.RDFBackend;

public class ChangesDb {

    private KnowledgeBase kb;
    private KnowledgeBase changes_kb;
    private Project changesProject;
    private ChangeModel model;
    
    /*
     * This map allows the ChangesDb manage transactions.  The map maintains transaction information
     * on a per session basis.  The transaction state class is responsible for tracking whether a transaction
     * is in progress and for closing out transactions.
     */
    private Map<RemoteSession, TransactionState> transactionMap = new HashMap<RemoteSession, TransactionState>();
    
    
    /*
     * This map maps frame ids to ontology components.
     */
    private Map<FrameID, Ontology_Component> frameIdMap = new HashMap<FrameID, Ontology_Component>();
    
    /*
     * a list of postprocessing jobs.
     */
    private List<PostProcessor> post_processors = new ArrayList<PostProcessor>();
    
    /*
     * This map tracks the relationship between frame id's and information about the frame.
     * After the delete event there may still be events that happen to the deleted frame.
     * We no longer have a name for the frame or an ontology component.  This map tracks
     * this information and allows us to recover it as needed.
     */
    
    private Map<FrameID, Deleted_Change> deletedFrameMap = new HashMap<FrameID, Deleted_Change>();
   

    
    public ChangesDb(KnowledgeBase kb) {
        this.kb = kb;
        getOrCreateChangesProject(kb);
        model = new ChangeModel(changes_kb);
        initializeFrameMap();
        Timestamp.initialize(model);
    }
    
    public void initializeFrameMap() {
        for (Instance i : model.getInstances(ChangeCls.Ontology_Component)) {
            Ontology_Component oc = (Ontology_Component) i;
            String name = oc.getCurrentName();
            if (name != null && !oc.isAnonymous()) {
                Frame frame = kb.getFrame(name);
                if (frame != null) {
                    frameIdMap.put(frame.getFrameID(), oc);
                }
            }
        }
    }
    
    private void getOrCreateChangesProject(KnowledgeBase kb) {
        final Project project = kb.getProject();

        if (project.isMultiUserServer()) {
            Server server = Server.getInstance();
            String annotationName = (String) new GetAnnotationProjectName(kb).execute();
            if (annotationName == null) {
                throw new RuntimeException("Annotation project not configured on server (use the " + 
                        GetAnnotationProjectName.METAPROJECT_ANNOTATION_PROJECT_SLOT +
                " slot)");
            }
            changesProject = server.getProject(annotationName);
            changes_kb = changesProject.getKnowledgeBase();
            return;
        }

        ArrayList errors = new ArrayList();

        URI annotationProjURI = getAnnotationProjectURI(project);

        File annotationProjFile = new File(annotationProjURI);
        
        //TODO: TT Check whether this works with real URIs
        if (annotationProjFile.exists()) {
            //annotation ontology exists                    
            changesProject = Project.loadProjectFromURI(annotationProjURI, errors);

        } else {
            //annotations ontology does not exist and it will be created
            URI changeOntURI = null;
            try {
                changeOntURI = ChangesProject.class.getResource("/projects/changes.pprj").toURI();
            } catch (URISyntaxException e) {
                Log.getLogger().log(Level.WARNING, "Could not find Changes Ontology", e);
            }

            changesProject = Project.loadProjectFromURI(changeOntURI, errors);

            RDFBackend.setSourceFiles(changesProject.getSources(), ChangesProject.ANNOTATION_PROJECT_NAME_PREFIX + project.getName() + ".rdfs", ChangesProject.ANNOTATION_PROJECT_NAME_PREFIX + project.getName() + ".rdf", ChangesProject.PROTEGE_NAMESPACE);
            changesProject.setProjectURI(annotationProjURI);

        }


        if (changesProject == null) {
            Log.getLogger().warning("Failed to find or create annotation project");
            ChangesProject.displayErrors(errors);
        }

        changes_kb = changesProject.getKnowledgeBase();
    }
    
    public static URI getAnnotationProjectURI(Project p) {
        return URIUtilities.createURI(p.getProjectDirectoryURI() + 
                                      "/" + 
                                      ChangesProject.ANNOTATION_PROJECT_NAME_PREFIX + 
                                      p.getName() + ".pprj");
    }
    
    


    
    /* -------------------------------- PostProcessing -------------------------------- */
    /* ToDo - put these in separate classes - there are getting to be too many of them */
    
    private void postProcessChange(Change aChange) {
        checkForTransaction(aChange);
        for (PostProcessor p : post_processors) {
            p.addChange(aChange);
        }
    }
    
    /*
     * If I am in a transaction, add the change to the transaction.
     */
    private void checkForTransaction(Change aChange) {
        TransactionState tstate = getTransactionState();
        if (tstate.inTransaction()) {
            tstate.addToTransaction(aChange);
        }
    }
    
    


    
    /* -------------------------------------Interfaces ------------------------------*/


    public KnowledgeBase getKb() {
        return kb;
    }
    
    public KnowledgeBase getChangesKb() {
        return changes_kb;
    }
    
    public Project getChangesProject() {
        return changesProject;
    }
    
    public ChangeModel getModel() {
        return model;
    }
    
    public String getCurrentUser() {
        return ChangesProject.getUserName(kb);
    }
    
    public boolean isOwl() {
        return Util.kbInOwl(kb);
    }
    
    public RemoteSession getCurrentSession() {
        RemoteSession session = ServerFrameStore.getCurrentSession();
        if (session != null) return session;
        else return StandaloneSession.getInstance();
    }
    
    public void addPostProcessor(PostProcessor p) {
        p.initialize(this);
        post_processors.add(p);
    }
    
    public PostProcessor getPostProcessor(Class<? extends PostProcessor> clazz) {
        for (PostProcessor p : post_processors) {
            if (clazz.isAssignableFrom(p.getClass())) {
                return p;
            }
        }
        return null;
    }
    
    
    public Ontology_Component getOntologyComponent(Frame frame) {
    	return getOntologyComponent(frame, false);
    }    
    
    public Ontology_Component getOntologyComponent(Frame frame, boolean create) {
        FrameID frameId = frame.getFrameID();
        Ontology_Component oc = frameIdMap.get(frameId);
        if (oc == null && create) {
            oc = (Ontology_Component) model.createInstance(getOntologyComponentType(frame));
            if (!frame.isDeleted()) {
                oc.setCurrentName(frame.getName());
            }
            frameIdMap.put(frameId, oc);
        }
        return oc;
    }
    
    public ChangeCls getOntologyComponentType(Frame frame) {
        if (frame instanceof Cls) {
            return ChangeCls.Ontology_Class;
        }
        else if (frame instanceof Slot) {            
            return ChangeCls.Ontology_Property;
        }
        else {
        	return ChangeCls.Ontology_Individual;            
        }
        
    }
    
    public TransactionState getTransactionState() {
        TransactionState state = transactionMap.get(getCurrentSession());
        if (state == null) {
            state = new TransactionState(this);
            transactionMap.put(getCurrentSession(), state);
        }
        return state;
    }

    
    public void updateDeletedFrameIdToNameMap(FrameID frameId, Deleted_Change deletion) {
        deletedFrameMap.put(frameId, deletion);
    }
    
    public String getPossiblyDeletedFrameName (Frame frame) {
        if (frame.isDeleted()) {
            Deleted_Change deletion = deletedFrameMap.get(frame.getFrameID());
            if (deletion == null) {
                return null;
            }
            return deletion.getDeletionName();
        }
        else {
            return frame.getName();
        }
    }
    
    public String getPossiblyDeletedBrowserText(Frame frame) {
        if (frame.isDeleted()) {
            return getPossiblyDeletedFrameName(frame);
        }
        else {
            return frame.getBrowserText();
        }
    }
    
    public Change createChange(ChangeCls type) {
        Change change = (Change) model.createInstance(type);
        change.setAction(type.toString());
        return change;
    }
    
    public void finalizeChange(Change change,
                               Ontology_Component applyTo,
                               String context) {
        change.setAuthor(getCurrentUser());
        change.setContext(context);
        change.setTimestamp(Timestamp.getTimestamp(model));
        change.setApplyTo(applyTo);  // this is what passes the change to the change tab
                                     // so it  must happen last.  see AbstractChangeListener
        postProcessChange(change);
    }
    
    public Annotation createAnnotation(Cls direct_type) {
    	if (direct_type == null) {
    		direct_type = getModel().getCls(AnnotationCls.Comment);
    	}
        return (Annotation) direct_type.createDirectInstance(null);
    }

    public void finalizeAnnotation(Annotation annotation,
                                   Collection<AnnotatableThing> annotated,
                                   String body) {
        Timestamp timestamp = Timestamp.getTimestamp(model);
        annotation.setBody(body);
        annotation.setAuthor(getCurrentUser());
        if (annotation.getCreated() == null) {
            annotation.setCreated(timestamp);
        }
        annotation.setModified(timestamp);
        annotation.setAnnotates(annotated);  // this is what passes the change to the annotation listeners.
                                             // so it must happen last.  See AbstractChangeListener.
    }
}
