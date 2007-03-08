package edu.stanford.smi.protegex.server_changes;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
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
     * This map maps names to the corresponding ontology component.  This map is critical to the 
     * correct operation of the changes tab.
     */
    private Map<String, Ontology_Component> name_map = new HashMap<String, Ontology_Component>();
    
    /*
     * This map tracks the relationship between frame id's and the names of the frames.
     * When the delete event happens the name of the frame being deleted is present in the 
     * event.  But there are events that occur immediately following the delete.  These events
     * do not include the name of the frame being deleted.  This map allows us to retrieve the previous
     * name and therefore know which frame is being effected.
     */
    private Map<FrameID, String> frameIdMap = new HashMap<FrameID, String>();
   
    /*
     * When a component has just been created this map determines the change that caused the change.
     * This is used in the slightly tricky code that creates a transaction around sequential create +
     * name change operations.
     */
    private Map<RemoteSession, Created_Change> lastCreateBySession = new HashMap<RemoteSession, Created_Change>();

    
    public ChangesDb(KnowledgeBase kb) {
        this.kb = kb;
        getOrCreateChangesProject(kb);
        model = new ChangeModel(changes_kb);
        for (Object o : model.getSortedChanges()) {
            Change change = (Change) o;
            checkForNameChanges(change);
        }

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
    
    /* -------------------------------- PostProcessing -------------------------------- */
    /* ToDo - put these in separate classes - there are getting to be too many of them */
    
    private void postProcessChange(Change aChange) {
        checkForTransaction(aChange);
        checkForCreateAndNameChange(aChange);
        checkForNameChanges(aChange);
        possiblyCombineAnnotations(aChange);
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
    
    
    /*
     * This is a little tricky.  I  am trying to combine a create operation
     * followed by a name change into a single transaction.  There are several cases.
     * The simple case is the sequence
     *      create operation
     *      name change.
     * In this case we create a transaction (**after the fact**) combining the create operation and 
     * the name change.  But in the sequence
     *      create operation
     *      not name change (or name change of different object)
     * The transaction does not happen in this case.  A similar case is 
     *      create operation
     *      begin transaction
     *        xxx
     *      end transaction
     * In this case the attempt to create the transaction also must be aborted. Finally there
     * is the owl case:
     *      begin transaction
     *         create change
     *         xxx
     *      end  transaction
     *      name change
     * Here we - again after the fact - create the transaction
     *      begin transaction
     *        begin transaction
     *          create change
     *          xxx
     *        end transaction
     *        name change
     *      end transaction
     * I think that the following code works.  Note that lastCreateBySession is only set by
     * ChangesDb.startChangeTransaction.  This gives the routine creating a change event a 
     * chance to not try to create a session.
     */
    private void checkForCreateAndNameChange(Change aChange) {
        if (!getTransactionState().inTransaction()) {
            Created_Change previous_change = lastCreateBySession.get(getCurrentSession());
            if (previous_change == null) return;
            if (aChange instanceof Name_Changed) {
                possiblyCombineWithCreate((Name_Changed) aChange);
            }
            if (aChange instanceof Composite_Change) {
                Composite_Change transaction = (Composite_Change) aChange;
                Collection sub_changes = transaction.getSubChanges();
                if (sub_changes != null && sub_changes.contains(previous_change)) return;
            }
            lastCreateBySession.remove(getCurrentSession());
        }
    }
    
    private void possiblyCombineWithCreate(Name_Changed changeInst) {
        String newName = changeInst.getNewName();
        Ontology_Component renamed_frame = (Ontology_Component) changeInst.getApplyTo();
        Change createOp = lastCreateBySession.get(getCurrentSession());
        if (createOp != null) {
            Ontology_Component created = (Ontology_Component) createOp.getApplyTo();
            if (renamed_frame.equals(created)) {
                Composite_Change existingCreateTrans = (Composite_Change) createOp.getPartOfCompositeChange();
                if (existingCreateTrans != null) {
                    createOp = existingCreateTrans;
                }
                List<Change> changes = new ArrayList<Change>();
                changes.add(createOp);
                changes.add(changeInst);
                Composite_Change transaction = (Composite_Change) createChange(ChangeCls.Composite_Change);
                transaction.setSubChanges(changes);
                finalizeChange(transaction, created, "Created " + newName);
            }
        }
    }
    
    
    /*
     * Update the name map.
     */
    public void checkForNameChanges(Change change) {
        synchronized (changes_kb) {
            if (change instanceof Created_Change) {
                Ontology_Component frame = (Ontology_Component) change.getApplyTo();
                String name = ((Created_Change) change).getCreationName();
                name_map.put(name, frame);
            }
            else if (change instanceof Deleted_Change) {
                String name = ((Deleted_Change) change).getDeletionName();
                name_map.remove(name);
            }
            else if (change instanceof Name_Changed) {
                Name_Changed name_change = (Name_Changed) change;
                String oldName = name_change.getOldName();
                String newName = name_change.getNewName();
                name_map.put(newName, name_map.remove(oldName));
            }
        }
    }
    
    private Map<RemoteSession, List<Annotation_Change>> lastAnnotationsBySession
                = new HashMap<RemoteSession, List<Annotation_Change>>();
    /*
     * Combine Annotations.
     */
    private void possiblyCombineAnnotations(Change aChange) {
        List<Annotation_Change> previous_annotations = lastAnnotationsBySession.get(getCurrentSession());
        
        if (aChange instanceof Annotation_Change) {
            Annotation_Change annotation = (Annotation_Change) aChange;

            if (previous_annotations == null) {
                previous_annotations = new ArrayList<Annotation_Change>();
                previous_annotations.add(annotation);
                lastAnnotationsBySession.put(getCurrentSession(), previous_annotations);
                return;
            }
            
            Ontology_Component applyTo = (Ontology_Component) annotation.getApplyTo();
            Ontology_Component property = (Ontology_Component) annotation.getOntologyAnnotation();
            
            Annotation_Change earlier_annotation = previous_annotations.get(0);
            if (!applyTo.equals(earlier_annotation.getApplyTo()) || 
                    !property.equals(earlier_annotation.getOntologyAnnotation())) {
                combineAnnotations(previous_annotations);
                List<Annotation_Change> new_annotations = new ArrayList<Annotation_Change>();
                new_annotations.add(annotation);
                lastAnnotationsBySession.put(getCurrentSession(), new_annotations);
                return;
            }
            else {
                previous_annotations.add(annotation);
                return;
            }
        }
        else if (previous_annotations != null) {
            combineAnnotations(previous_annotations);
        }
        
    }
    
    private void combineAnnotations(List<Annotation_Change> annotations) {
        lastAnnotationsBySession.remove(getCurrentSession());
        if (annotations.size() <= 1) return;
        Ontology_Component applyTo = (Ontology_Component) annotations.get(0).getApplyTo();
        Composite_Change transaction = (Composite_Change) createChange(ChangeCls.Composite_Change);
        
        transaction.setSubChanges(annotations);
        
        finalizeChange(transaction, applyTo, getContextForAnnotations(annotations));

    }
    
    private String getContextForAnnotations(Collection<Annotation_Change> annotations) {
        String context = null;
        for (Annotation_Change annotation : annotations) {
            context = annotation.getContext();
            if (!(annotation instanceof Annotation_Removed)) {
                break;
            }
        }
        return context;
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
        synchronized (changes_kb) {
            Ontology_Component frame = name_map.get(name);
            if (frame == null && create) {
                frame = (Ontology_Component) model.createInstance(ChangeCls.Ontology_Component);
                frame.setCurrentName(name);
                name_map.put(name, frame);
            }
            return frame;
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
    
    public void startChangeTransaction(Created_Change change) {
        lastCreateBySession.put(getCurrentSession(), change);
    }

    
    public void updateDeletedFrameIdToNameMap(FrameID frameId, String name) {
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
        finalizeChange(root, null, "Root");
        return root;
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
