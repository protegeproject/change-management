package edu.stanford.smi.protegex.server_changes;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.NameChangeManager;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.AnnotatableThing;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Created_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Name_Changed;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.generated.Timestamp;
import edu.stanford.smi.protegex.server_changes.util.Util;
import edu.stanford.smi.protegex.storage.rdf.RDFBackend;

public class ChangesDb {

    private KnowledgeBase kb;
    private KnowledgeBase changes_kb;
    private Project changesProject;
    private ChangeModel model;
    private Map<RemoteSession, TransactionState> transactionMap = new HashMap<RemoteSession, TransactionState>();
    private NameChangeManager nameChangeManager;
    
    private Set<RemoteSession> inCreateClass = new HashSet<RemoteSession>();
    private Set<RemoteSession> inCreateSlot  = new HashSet<RemoteSession>();
    
    private Map<Ontology_Component, Created_Change> lastCreateByComponent = new HashMap<Ontology_Component, Created_Change>();
    
    private Map<FrameID, String> frameIdMap = new HashMap<FrameID, String>();
    
    public ChangesDb(KnowledgeBase kb) {
        this.kb = kb;
        getOrCreateChangesProject(kb);
        model = new ChangeModel(changes_kb);
        nameChangeManager = new NameChangeManager(model);

        Timestamp.initialize(model);
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
    
    

    private RemoteSession getCurrentSession() {
        RemoteSession session = ServerFrameStore.getCurrentSession();
        if (session != null) return session;
        else return StandaloneSession.getInstance();
    }
    
    private void postProcessChange(Change aChange) {
        checkForTransaction(aChange);
        checkForCreateAndNameChange(aChange);
    }
    
    private void checkForTransaction(Change aChange) {
        TransactionState tstate = getTransactionState();
        if (tstate.inTransaction()) {
            tstate.addToTransaction(aChange);
        }
    }
    
    
    // takes care of case when class is created & then renamed - Adding original name of class and change instance to HashMap
    private void checkForCreateAndNameChange(Change aChange) {

        if  (aChange instanceof Created_Change) {
            lastCreateByComponent.put((Ontology_Component) aChange.getApplyTo(), (Created_Change) aChange);
        }
        if (aChange instanceof Name_Changed) {
            possiblyCombineWithCreate((Name_Changed) aChange);
        }
    }
    
    private void possiblyCombineWithCreate(Name_Changed changeInst) {
        String newName = changeInst.getNewName();
        Ontology_Component created = (Ontology_Component) changeInst.getApplyTo();
        Change createOp = lastCreateByComponent.get(created);
        if (createOp != null) {
            lastCreateByComponent.remove(created);
            Composite_Change existingCreateTrans = (Composite_Change) changeInst.getPartOfCompositeChange();
            if (existingCreateTrans != null) {
                createOp = existingCreateTrans;
            }
            Set<Change> changes = new HashSet<Change>();
            changes.add(changeInst);
            changes.add(createOp);
            Composite_Change transaction = (Composite_Change) createChange(ChangeCls.Composite_Change);
            transaction.setSubChanges(changes);
            finalizeChange(transaction, created, "Created " + newName, ChangeModel.CHANGE_LEVEL_TRANS);
        }
    }
    
    /* -------------------------------------Interfaces ------------------------------*/


    
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
    
    public Ontology_Component getOntologyComponent(String name, boolean create) {
        return nameChangeManager.getOntologyComponent(name, create);
    }
    
    public TransactionState getTransactionState() {
        TransactionState state = transactionMap.get(getCurrentSession());
        if (state == null) {
            state = new TransactionState(this);
            transactionMap.put(getCurrentSession(), state);
        }
        return state;
    }

    public boolean isInCreateClass() {
        return inCreateClass.contains(getCurrentSession());
    }
    
    public void setInCreateClass(boolean val) {
        RemoteSession session = getCurrentSession();
        if (val) {
            inCreateClass.add(session);
        }
        else {
            inCreateClass.remove(session);
        }
    }
    
    public boolean isInCreateSlot() {
        return inCreateSlot.contains(getCurrentSession());
    }
    
    public void setInCreateSlot(boolean val) {
        RemoteSession session = getCurrentSession();
        if (val) {
            inCreateSlot.add(session);
        }
        else {
            inCreateSlot.remove(session);
        }
    }
    
    public void updateMap(FrameID frameId, String name) {
        frameIdMap.put(frameId, name);
    }
    
    public String getPossiblyDeletedFrameName (Frame frame) {
        if (frame.isDeleted()) {
            return (String)frameIdMap.get(frame.getFrameID());
        }
        else {
            return frame.getName();
        }
    }
    
    public String getPossiblyDeletedBrowserText(Frame frame) {
        if (frame.isDeleted()) {
            return (String)frameIdMap.get(frame.getFrameID());
        }
        else {
            return frame.getBrowserText();
        }
    }
    
    /**
     * This implementation must be sychronized with ChangeModel.isRoot();
     * 
     * @return
     */
    public Change createRootChange() {
        Change root = createChange(ChangeCls.Composite_Change);
        finalizeChange(root, null, ChangeModel.CHANGE_TYPE_ROOT, ChangeModel.CHANGE_TYPE_ROOT);
        return root;
    }
    
    public Change createChangeStd(ChangeCls type, 
                                  String applyTo,
                                  String context) {
        Ontology_Component frame = getOntologyComponent(applyTo, true);
        Change change = createChange(type);
        finalizeChange(change, frame, context, ChangeModel.CHANGE_LEVEL_INFO);
        return change;
    }
    
    public Change createChange(ChangeCls type) {
        Change change = (Change) model.createInstance(type);
        change.setAction(type.toString());
        return change;
    }
    
    public void finalizeChange(Change change,
                               Ontology_Component applyTo,
                               String context,
                               String type) {
        change.setAuthor(getCurrentUser());
        change.setContext(context);
        change.setType(type);
        change.setTimestamp(Timestamp.getTimestamp(model));
        change.setApplyTo(applyTo);  // this is what passes the change to the change tab
                                     // so it  must happen last.  see AbstractChangeListener
        postProcessChange(change);
    }
    
    public Annotation createAnnotation(Cls direct_type) {
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
