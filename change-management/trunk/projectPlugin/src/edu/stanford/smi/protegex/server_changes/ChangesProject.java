package edu.stanford.smi.protegex.server_changes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.WidgetDescriptor;
import edu.stanford.smi.protege.plugin.ProjectPluginAdapter;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesClsListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesFrameListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesInstanceListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesKBListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesSlotListener;
import edu.stanford.smi.protegex.server_changes.listeners.ChangesTransListener;
import edu.stanford.smi.protegex.server_changes.util.Util;

public class ChangesProject extends ProjectPluginAdapter {

	public static final String ANNOTATION_PROJECT_NAME_PREFIX = "annotation_";

	public static final String PROTEGE_NAMESPACE = "http://protege.stanford.edu/kb#";
	
	// Transaction signals
	public static final String TRANS_SIGNAL_TRANS_BEGIN = "transaction_begin";
	public static final String TRANS_SIGNAL_TRANS_END = "transaction_end";
	public static final String TRANS_SIGNAL_START = "start";
    
    private static Map<KnowledgeBase, ChangesDb> changesDbMap = new HashMap<KnowledgeBase, ChangesDb>();

	public void afterLoad(Project p) {
		
		if (!isChangesTabProject(p) || p.isMultiUserClient()) {
			return;
		}

		initialize(p);
	}
    
    public void beforeClose(Project p) {
        KnowledgeBase kb = p.getKnowledgeBase();
        ChangesDb changesDb  = getChangesDb(kb);
        if (changesDb != null) {
            changesDb.getChangesKb().dispose();
        }
    }
	
	
	public void initialize(Project p) {	
		Project currentProj = p;
		KnowledgeBase currentKB = currentProj.getKnowledgeBase();
		
		createChangeProject(currentKB); 

        ChangesDb changesDb = changesDbMap.get(currentKB);
        KnowledgeBase changesKb = changesDb.getChangesKb();
        Project changesProj = changesDb.getChangesProject();
		if (changesKb == null) {
			Log.getLogger().warning("Could not initialize the annotations ontology. ChangesTab will probably not work");
			return;
		}
		
		// AT THIS POINT, WE HAVE THE CHANGES PROJECT 'changes' and the KB 'changesKb'. 
		// Creating the Root of the tree for the UI

		ServerChangesUtil.createChange(currentKB, changesKb, ServerChangesUtil.CHANGETYPE_INSTANCE_ADDED,	"ROOT", "ROOT",	"ROOT");


		//Check to see if the project is an OWL one
		boolean isOwlProject = Util.kbInOwl(currentKB);

		// Register listeners
		if (isOwlProject) {
			Util.registerOwlListeners((OWLModel) currentKB);
		} else {
			registerKBListeners(currentKB);
		}

		if (changesProj.isMultiUserServer()) {
			ServerFrameStore.requestEventDispatch(currentKB);
		}		

	}

	private static void registerKBListeners(KnowledgeBase currentKB) {
		currentKB.addKnowledgeBaseListener(new ChangesKBListener(currentKB));
		currentKB.addClsListener(new ChangesClsListener(currentKB));
		currentKB.addInstanceListener(new ChangesInstanceListener(currentKB));
		currentKB.addSlotListener(new ChangesSlotListener(currentKB));
		currentKB.addTransactionListener(new ChangesTransListener(currentKB));
		currentKB.addFrameListener(new ChangesFrameListener(currentKB));
	}
    


	public static void createChange(KnowledgeBase currentKB, 
                                    KnowledgeBase changesKb, 
                                    Instance aChange){
        ChangesDb changesDb = getChangesDb(currentKB);
		if (changesDb.isInTransaction()) {
			changesDb.pushTransStack(aChange);
		}
		
		checkForCreateChange(changesDb, changesKb, aChange);	
		
	}
	
	
	// takes care of case when class is created & then renamed - Adding original name of class and change instance to HashMap
	private static void checkForCreateChange(ChangesDb changesDb, KnowledgeBase changesKb, Instance aChange) {
		String changeAction = ServerChangesUtil.getAction(changesKb, aChange);
		if  ( (changeAction != null) && (changeAction.equals(ServerChangesUtil.CHANGETYPE_CLASS_CREATED)
				|| changeAction.equals(ServerChangesUtil.CHANGETYPE_SLOT_CREATED)
				|| changeAction.equals(ServerChangesUtil.CHANGETYPE_PROPERTY_CREATED)
				))
				{
			
			changesDb.addChangeName(ServerChangesUtil.getApplyTo(changesKb, aChange), aChange);
		}
	}

	public static void createTransactionChange(KnowledgeBase currentKB, String typ) {
        ChangesDb changesDb = changesDbMap.get(currentKB);
        TransactionUtility tu = changesDb.getTransactionUtility();

		if (typ.equals(TRANS_SIGNAL_TRANS_BEGIN)) {
            changesDb.setInTransaction(true);
            changesDb.incrementTransactionCount();
            changesDb.pushTransStack(TRANS_SIGNAL_START);

		} else if (typ.equals(TRANS_SIGNAL_TRANS_END)) {
            changesDb.decrementTransactionCount();
			changesDb.setTransStack(tu.convertTransactions(changesDb.getTransStack()));

			// Indicates we are done (balanced start and ends)
			if (changesDb.getTransactionCount() ==0) {
                changesDb.setInTransaction(false);
				Instance changeInst = tu.findAggAction(changesDb.getTransStack(), Util.kbInOwl(currentKB));
				//checkForCreateChange(changeInst);	
				changesDb.clearTransStack();
			} 
		} 
	}

	private static void createChangeProject(KnowledgeBase currentKB) {
        ChangesDb changesDb = new ChangesDb(currentKB);
        changesDbMap.put(currentKB, changesDb);
	}

	private boolean isChangesTabProject(Project p) {
		String changesTabClassName = ChangesTab.class.getName();
		for (Object o : p.getTabWidgetDescriptors()) {
			WidgetDescriptor w = (WidgetDescriptor) o;
			if (w.isVisible() && changesTabClassName.equals(w.getWidgetClassName())) {
				return true;
			}
		}
		return false;
	}
	
	
	public static String getUserName(KnowledgeBase currentKB) {
		return currentKB.getUserName();
	}

	public static String getTimeStamp() {
		Date currTime = new Date();

		String datePattern = "MM/dd/yyyy HH:mm:ss zzz";
		SimpleDateFormat format = new SimpleDateFormat(datePattern);
		String time = format.format(currTime);

		return time;
	}
	
	public static boolean getIsInTransaction(KnowledgeBase kb) {
        ChangesDb changesDb = getChangesDb(kb);
		return changesDb.isInTransaction();
	}

	public static boolean getInCreateClass(KnowledgeBase kb) {
        ChangesDb changesDb = getChangesDb(kb);
		return changesDb.isInCreateClass();
	}

	public static void setInCreateClass(KnowledgeBase kb, boolean val) {
        ChangesDb changesDb = getChangesDb(kb);
        changesDb.setInCreateClass(val);
	}

	public static boolean getInCreateSlot(KnowledgeBase kb) {
        ChangesDb changesDb = getChangesDb(kb);
		return changesDb.isInCreateSlot();
	}

	public static void setInCreateSlot(KnowledgeBase kb, boolean val) {
        ChangesDb changesDb = getChangesDb(kb);
        changesDb.setInCreateSlot(val);
	}
    
    public static ChangesDb getChangesDb(KnowledgeBase kb) {
        return changesDbMap.get(kb);
    }

	public static KnowledgeBase getChangesKB(KnowledgeBase kb) {
        ChangesDb changesDb = changesDbMap.get(kb);
		return changesDb.getChangesKb();
	}

	public static Project getChangesProj(KnowledgeBase kb) {
        ChangesDb changesDb = changesDbMap.get(kb);
        if (changesDb == null) {
        		return null;
        }	
        return changesDb.getChangesProject();
	}

	public String getName() {
		return "Changes Project Plugin";
	}

	public static void main(String[] args) {
		Application.main(args);
	}

}
