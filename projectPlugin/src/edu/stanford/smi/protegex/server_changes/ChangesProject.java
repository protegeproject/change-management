package edu.stanford.smi.protegex.server_changes;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Level;

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
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.URIUtilities;


import edu.stanford.smi.protegex.server_changes.listeners.ChangesClsListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesFrameListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesInstanceListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesKBListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesSlotListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesTransListener;
import edu.stanford.smi.protegex.server_changes.util.Util;
import edu.stanford.smi.protegex.storage.rdf.RDFBackend;
import edu.stanford.smi.protegex.storage.rdf.RDFKnowledgeBase;

public class ChangesProject extends ProjectPluginAdapter {
	private static Project existingServerChangesProject;

	public static final String ANNOTATION_PROJECT_NAME_PREFIX = "annotation_";

	public static final String PROTEGE_NAMESPACE = "http://protege.stanford.edu/kb#";
	
	// Transaction signals
	public static final String TRANS_SIGNAL_TRANS_BEGIN = "transaction_begin";
	public static final String TRANS_SIGNAL_TRANS_END = "transaction_end";
	public static final String TRANS_SIGNAL_START = "start";

	// This is bad but it removes a reverse dependency.
	private static final String CHANGES_TAB_CLASSNAME="edu.stanford.smi.protegex.changes.ChangesTab";

	
	private static Project currentProj;
	private static KnowledgeBase currentKB;
	
	private static Project changesProj;
	private static KnowledgeBase changesKb;
		
	//	Maintaing transaction objects
	private static Stack transStack;
	private static int transCount = 0;
	private static boolean inTransaction;

	private static boolean inCreateClass = false;
	private static boolean inCreateSlot = false;

	private static boolean isOwlProject;

	public static HashMap createChangeName = new HashMap();
	public void afterLoad(Project p) {
		
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
	
	
	public void initialize(Project p) {	
		currentProj = p;
		currentKB = currentProj.getKnowledgeBase();
		transStack = new Stack();
		
		createChangeProject(); 

		if (changesKb == null) {
			Log.getLogger().warning("Could not initialize the annotations ontology. ChangesTab will probably not work");
			return;
		}
		
		// AT THIS POINT, WE HAVE THE CHANGES PROJECT 'changes' and the KB 'changesKb'. 
		// Creating the Root of the tree for the UI

		ServerChangesUtil.createChange(	changesKb, ServerChangesUtil.CHANGETYPE_INSTANCE_ADDED,	"ROOT", "ROOT",	"ROOT");

		TransactionUtility.initialize();	

		//Check to see if the project is an OWL one
		isOwlProject = Util.kbInOwl(currentKB);

		// Register listeners
		if (isOwlProject) {
			Util.registerOwlListeners(currentKB);
		} else {
			registerKBListeners();
		}

		if (changesProj.isMultiUserServer()) {
			ServerFrameStore.requestEventDispatch(currentKB);
		}		

	}

	private static void registerKBListeners() {
		currentKB.addKnowledgeBaseListener(new ChangesKBListener());
		currentKB.addClsListener(new ChangesClsListener());
		currentKB.addInstanceListener(new ChangesInstanceListener());
		currentKB.addSlotListener(new ChangesSlotListener());
		currentKB.addTransactionListener(new ChangesTransListener());
		currentKB.addFrameListener(new ChangesFrameListener());
	}

	public static void createChange(Instance aChange){
		if (inTransaction) {
			transStack.push(aChange);
		}
		
		checkForCreateChange(aChange);	
		
	}
	
	
	// takes care of case when class is created & then renamed - Adding original name of class and change instance to HashMap
	private static void checkForCreateChange(Instance aChange) {
		String changeAction = ServerChangesUtil.getAction(changesKb, aChange);
		if  ( (changeAction != null) && (changeAction.equals(ServerChangesUtil.CHANGETYPE_CLASS_CREATED)
				|| changeAction.equals(ServerChangesUtil.CHANGETYPE_SLOT_CREATED)
				|| changeAction.equals(ServerChangesUtil.CHANGETYPE_PROPERTY_CREATED)
				))
				{
			
			createChangeName.put(ServerChangesUtil.getApplyTo(changesKb, aChange), aChange);
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

		if (currentProj.isMultiUserServer()) {
			Server server = Server.getInstance();
			String annotationName = (String) new GetAnnotationProjectName(currentKB).execute();
			if (annotationName == null) {
				throw new RuntimeException("Annotation project not configured on server (use the " + 
						GetAnnotationProjectName.METAPROJECT_ANNOTATION_PROJECT_SLOT +
				" slot)");
			}
			changesProj = server.getProject(annotationName);
			changesKb = changesProj.getKnowledgeBase();
			return true;
		}

		boolean annotationsProjectExist = false;

		ArrayList errors = new ArrayList();

		URI annotationProjURI = getAnnotationProjectURI();

		File annotationProjFile = new File(annotationProjURI);
		
		//TODO: TT Check whether this works with real URIs
		if (annotationProjFile.exists()) {
			//annotation ontology exists        	      	
			changesProj = Project.loadProjectFromURI(annotationProjURI, errors);

			annotationsProjectExist = true;

		} else {
			//annotations ontology does not exist and it will be created
			URI changeOntURI = null;
			try {
				changeOntURI = ChangesProject.class.getResource("/projects/changes.pprj").toURI();
			} catch (URISyntaxException e) {
				Log.getLogger().log(Level.WARNING, "Could not find Changes Ontology", e);
			}

			RDFBackend rdfBackendFactory = new RDFBackend();

			changesProj = Project.loadProjectFromURI(changeOntURI, errors);

			RDFBackend.setSourceFiles(changesProj.getSources(), ANNOTATION_PROJECT_NAME_PREFIX + currentProj.getName() + ".rdfs", ANNOTATION_PROJECT_NAME_PREFIX + currentProj.getName() + ".rdf", PROTEGE_NAMESPACE);
			changesProj.setProjectURI(annotationProjURI);

			annotationsProjectExist = false;
		}


		if (changesProj == null) {
			Log.getLogger().warning("Failed to find or create annotation project");
			displayErrors(errors);
			return annotationsProjectExist;
		}

		changesKb = changesProj.getKnowledgeBase();

		currentProj.addProjectListener(new ProjectAdapter() {
			ArrayList errors = new ArrayList();
			public void projectSaved(ProjectEvent event) {

				changesProj.save(errors);
				displayErrors(errors);			
			}
		});

		return annotationsProjectExist;
	}



	private static URI getAnnotationProjectURI() {
		return URIUtilities.createURI(currentProj.getProjectDirectoryURI() + File.separator + ANNOTATION_PROJECT_NAME_PREFIX + currentProj.getName() + ".pprj");
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

	
	private static void displayErrors(Collection errors) {
		Iterator i = errors.iterator();
		while (i.hasNext()) {
			Object elem = i.next();			
			if (elem instanceof Throwable) {
				Log.getLogger().log(Level.WARNING, "Warnings at loading changes project", (Throwable)elem);
			} else if (elem instanceof MessageError) {
				Log.getLogger().log(Level.WARNING, ((MessageError)elem).getMessage(), ((MessageError)elem).getException());
			} else {
				Log.getLogger().warning(elem.toString());
			}
		}
	}
	
	
	public static String getUserName() {
		return currentKB.getUserName();
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

	public static KnowledgeBase getChangesKB() {
		return changesKb;
	}

	public static Project getChangesProj() {
		return changesProj;
	}

	public String getName() {
		return "Changes Project Plugin";
	}

	public static void main(String[] args) {
		Application.main(args);
	}

}
