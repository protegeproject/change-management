package edu.stanford.smi.protegex.server_changes;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.*;

import edu.stanford.smi.protege.*;
import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.model.framestore.EventDispatchFrameStore;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.plugin.*;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.ui.*;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;


import edu.stanford.smi.protegex.server_changes.listeners.ChangesClsListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesFrameListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesInstanceListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesKBListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesSlotListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesTransListener;
import edu.stanford.smi.protegex.server_changes.util.Util;
import edu.stanford.smi.protegex.storage.rdf.RDFBackend;

public class ChangesProject extends AbstractProjectPlugin {
    private static Project existingServerChangesProject;
    
    // this is bad but it removes a reverse dependency.
    private static final String CHANGES_TAB_CLASSNAME="edu.stanford.smi.protegex.changes.ChangesTab";

	
	private static Project currProj;
	private static Project changes;
	private static KnowledgeBase cKb;
	private static KnowledgeBase currKB;
	
	
	public static final String ANNOTATION_PROJECT_EXISTS = "annotation_proj_exists";
//	 Maintaing transaction objects
	private static Stack transStack;
	private static int transCount = 0;
	private static boolean inTransaction;
	
	// Transaction signals
	public static final String TRANS_SIGNAL_TRANS_BEGIN = "transaction_begin";
	public static final String TRANS_SIGNAL_TRANS_END = "transaction_end";
	public static final String TRANS_SIGNAL_START = "start";
	
	private static boolean inCreateClass = false;
	private static boolean inCreateSlot = false;

	private static String userName;
	
	private static boolean isOwlProject;

	public static String getUserName() {
		return currKB.getUserName();
	}
	
	public static String getTimeStamp() {
		Date currTime = new Date();
		
		String datePattern = "MM/dd/yyyy HH:mm:ss zzz";
		SimpleDateFormat format = new SimpleDateFormat(datePattern);
		String time = format.format(currTime);
		
		return time;
	}
	
	public static boolean getIsInTransaction() {
		return inTransaction;
	}
	
	public static boolean getInCreateClass() {
		return inCreateClass;
	}
	
	public static void setInCreateClass(boolean val) {
		inCreateClass = val;
	}
	
	public static boolean getInCreateSlot() {
		return inCreateSlot;
	}
	
	public static void setInCreateSlot(boolean val) {
		inCreateSlot = val;
	}
	

	
	
    public void afterCreate(Project p) {
        // do nothing
    }

    
       public void initialize(Project p) {	
		currProj = p;
		currKB = currProj.getKnowledgeBase();
		transStack = new Stack();
		createChangeProject(); 
		
		// AT THIS POINT, WE HAVE THE CHANGES PROJECT 'changes' and the KB 'cKb'. 
		// Creating the Root of the tree for the UI
		
		Instance ROOT = ServerChangesUtil.createChange(
	            cKb,
	            ServerChangesUtil.CHANGETYPE_INSTANCE_ADDED, 
	            "ROOT", 
	            "ROOT", 
	            "ROOT");
		
		
		TransactionUtility.initialize();	
	
		
		//Check to see if the project is an OWL one
		isOwlProject = Util.kbInOwl(currKB);
		
		// Register listeners
		if (isOwlProject) {
			Util.registerOwlListeners(currKB);
		} else {
			registerKBListeners();
		}
        if (changes.isMultiUserServer()) {
            ServerFrameStore.requestEventDispatch(currKB);
        }
		
		
	}

    private static void registerKBListeners() {
	currKB.addKnowledgeBaseListener(new ChangesKBListener());
	currKB.addClsListener(new ChangesClsListener());
	currKB.addInstanceListener(new ChangesInstanceListener());
	currKB.addSlotListener(new ChangesSlotListener());
	currKB.addTransactionListener(new ChangesTransListener());
	currKB.addFrameListener(new ChangesFrameListener());
    }
    
    public static void createChange(Instance aChange){
    	if (inTransaction) {
			transStack.push(aChange);
    	}
    }
    
	public static void createTransactionChange(String typ) {
		
		if (typ.equals(TRANS_SIGNAL_TRANS_BEGIN)) {
			inTransaction = true;
			transCount++;
			transStack.push(TRANS_SIGNAL_START);
			
		} else if (typ.equals(TRANS_SIGNAL_TRANS_END)) {
			transCount--;
			transStack = TransactionUtility.convertTransactions(transStack);
			
			// Indicates we are done (balanced start and ends)
			if (transCount==0) {
				inTransaction = false;
				Instance changeInst = TransactionUtility.findAggAction(transStack, isOwlProject);
				//checkForCreateChange(changeInst);	
		
				
				transStack.clear();
			} 
		} 
	}
	
	private static boolean createChangeProject() {
		
		// NEED TO ADD IMPLEMENTATION FOR MULTI-USER MODE
        if (currProj.isMultiUserServer()) {
            Server server = Server.getInstance();
            String annotationName = (String) new GetAnnotationProjectName(currKB).execute();
            if (annotationName == null) {
                throw new RuntimeException("Annotation project not configured on server (use the " + 
                                           GetAnnotationProjectName.METAPROJECT_ANNOTATION_PROJECT_SLOT +
                                           " slot)");
            }
            changes = server.getProject(annotationName);
            cKb = changes.getKnowledgeBase();
            return true;
        }
		
		
		boolean annotateExists = false;
		Collection errors = new ArrayList();
		
		// Check if annotations project already exists for this project.
		String annotationExists = (String)currProj.getClientInformation(ANNOTATION_PROJECT_EXISTS);
		
		URI changeOntURI = null;
		try {
			changeOntURI = ChangesProject.class.getResource("/projects/changes.pprj").toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		// No anntation project exists, create one
		if (annotationExists == null) {
			String baseName = "annotation";
			String myNameSpace = "http://protege.stanford.edu/kb#";
		
			changes = Project.loadProjectFromURI(changeOntURI, errors);
			
			URI annotateURI = null;
			try {
				annotateURI = new URI(changes.getProjectURI().toString() +"/annotation.pprj");
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			changes.setProjectURI(annotateURI);
			cKb = changes.getKnowledgeBase();
			displayErrors(errors);
			
			RDFBackend.setSourceFiles(changes.getSources(), baseName + ".rdfs", baseName + ".rdf", myNameSpace);
			currProj.setClientInformation(ANNOTATION_PROJECT_EXISTS, "yes");

		// Annotation project exists	
		} else {
			annotateExists = true;
			String annotationName = "annotation_" + currProj.getName() + ".pprj";
			URI annotationURI;
			try {
				annotationURI = new URI(currProj.getProjectDirectoryURI()+"/" + annotationName);
				changes = Project.loadProjectFromURI(annotationURI, errors);
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			changes.includeProject(changeOntURI,errors);
			changes.mergeIncludedProjects();
			displayErrors(errors);
			
			cKb = changes.getKnowledgeBase();
		}
		
		currProj.addProjectListener(new ProjectAdapter() {
			ArrayList errors = new ArrayList();
			public void projectSaved(ProjectEvent event) {
				String changesName = "annotation_" + currProj.getName();
				String myNameSpace = "http://protege.stanford.edu/kb#";
				RDFBackend.setSourceFiles(changes.getSources(), changesName +".rdfs", changesName + ".rdf", myNameSpace);
				
				URI projUri;
				try {
					projUri = new URI(currProj.getProjectDirectoryURI()+"/"+changesName +".pprj");
					changes.setProjectURI(projUri);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				changes.save(errors);
				displayErrors(errors);
			
			
			}
		});

		
		return annotateExists;
	}
	
	
	private static void displayErrors(Collection errors) {
		Iterator i = errors.iterator();
		while (i.hasNext()) {
			Object elem = i.next();
			System.out.println("className: " + elem.getClass().getName());
			if (elem instanceof Exception) {
				((Exception)elem).printStackTrace(System.out);
			} 
			
		
		}
	}
	
    
    public void afterLoad(Project p) {
        KnowledgeBase kb = p.getKnowledgeBase();
        if (!isChangesTabProject(p) || p.isMultiUserClient()) {
            return;
        }
        if (p.isMultiUserServer() && existingServerChangesProject != null &&
                !existingServerChangesProject.equals(p)) {
            Log.getLogger().info("Can only have one server side project with the Changes Plugin");
            return;
        }
        else if (p.isMultiUserServer()) {
            existingServerChangesProject = p;
        }

      initialize(p);
    }
    
    private boolean isChangesTabProject(Project p) {
        String changesTabClassName = CHANGES_TAB_CLASSNAME;
        for (Object o : p.getTabWidgetDescriptors()) {
            WidgetDescriptor w = (WidgetDescriptor) o;
            if (w.isVisible() && changesTabClassName.equals(w.getWidgetClassName())) {
                return true;
            }
        }
        return false;
    }

    

	public static KnowledgeBase getChangesKB() {
		return cKb;
	}
	
	public static Project getChangesProj() {
		return changes;
	}

    public void afterShow(ProjectView view, ProjectToolBar toolBar, ProjectMenuBar menuBar) {

    }

    public void afterSave(Project p) {
        // do nothing
    }
    
    public void beforeSave(Project p) {
        // do nothing
    }
    
    public void beforeHide(ProjectView view, ProjectToolBar toolBar, ProjectMenuBar menuBar) {
        // do nothing
    }

    public void beforeClose(Project p) {
        // do nothing
    }

    public String getName() {
        return "Changes Project Plugin";
    }

    public void dispose() {
        // do nothing
    }
    
	
	
    
    public static void main(String[] args) {
        Application.main(args);
    }


}
