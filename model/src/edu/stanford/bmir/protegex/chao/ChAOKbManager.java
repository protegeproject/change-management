package edu.stanford.bmir.protegex.chao;

import java.io.File;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import javax.naming.OperationNotSupportedException;

import edu.stanford.bmir.protegex.chao.util.GetAnnotationProjectName;
import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.KnowledgeBaseFactory;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.server.RemoteProjectManager;
import edu.stanford.smi.protege.server.RemoteServer;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.server.metaproject.MetaProject;
import edu.stanford.smi.protege.server.metaproject.ProjectInstance;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.FileUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.storage.rdf.RDFBackend;

/**
 * Manages the creation and retrieval of annotation/changes (ChAO) knowledge bases attached to domain knowledge bases.
 * Keeps a map between the domain kb and its corresponding ChAO kb.
 * All software should use this class to retrieve the ChAO kb.
 *
 * @author Tania Tudorache <tudorache@stanford.edu>
 *
 */
public class ChAOKbManager {

	private static final String CHAO_PRJ_INTERNAL_PATH = "/projects/changes.pprj";
	public static final String CHAO_PROJECT_NAME_PREFIX = "annotation_";
	public static final String CHAO_PROJECT_CLIENT_INFO_KEY = "annotationProject";
	public static final String PROTEGE_NAMESPACE = "http://protege.stanford.edu/kb#";

	private static HashMap<KnowledgeBase, KnowledgeBase> kb2chaoKb = new HashMap<KnowledgeBase, KnowledgeBase>();


	/**
	 * Attach this listener to all kb-s, so that we can remove them from the map.
	 */
	private static ProjectListener kbListener = new ProjectAdapter() {
		/*
		 * TODO: Find a better solution.
		 * The current implementation will remove from the map
		 * the kb - changes kb association, if the domain kb is closed.
		 * This should work for most cases. A better solution can be found,
		 * for other cases.
		 */
		@Override
		public void projectClosed(ProjectEvent event) {
			Project project = (Project) event.getSource();
			removeFromMap(project.getKnowledgeBase());
		};
	};


	/**
	 * Returns the annotations and changes KB (ChAO KB) associated to the domain kb given as argument.
	 * This method handles the cases when the caller is:<br/>
	 * <ol>
	 *  <li> A standalone Protege application, </li>
	 *	<li> A Protege multi-user client connected to a protege server.
	 *   In this case, it will return the annotations/changes ontology associated on the server </li>
	 *   <li> A Protege multi-user server, in which case it will
	 *   get the CHAO kb as configured in the metaproject. </li>
	 * </ol>
	 * @param kb - the domain kb
	 * @return - the associated ChAO KB, if one exists, or null otherwise
	 */
	public static KnowledgeBase getChAOKb(KnowledgeBase kb) {
		KnowledgeBase changesKb = kb2chaoKb.get(kb);
		if (changesKb != null || kb2chaoKb.containsKey(kb)) {
			return changesKb;
		}
		try {
			if (kb.getProject().isMultiUserClient()) {
				changesKb = getChAOKbFromServer(kb);
			}
			else if (kb.getProject().isMultiUserServer()) {
			    changesKb = getChAOKbOnServer(kb);
			}
			else {
				changesKb = getFileBasedChAOKb(kb);
			}
			putInMap(kb, changesKb);
		} catch(Throwable e) {
			Log.getLogger().warning("There were errors at getting the Changes project attached to "
					+ kb + ". Error message: " + e.getMessage());
		}

		return changesKb;
	}


	public static KnowledgeBase createRDFFileChAOKb(KnowledgeBase kb, URI chaoURI) {
		KnowledgeBase changesKb = kb2chaoKb.get(kb);
		if (changesKb != null) {
			return changesKb;
		}

		URI changeOntURI = null;
		try {
			changeOntURI = ChAOKbManager.class.getResource(CHAO_PRJ_INTERNAL_PATH).toURI();
		} catch (Throwable e) {
			Log.getLogger().log(Level.WARNING,
					"Could not find annotation/changes ontology in the plugins directory.", e);
			return null;
		}
		ArrayList errors = new ArrayList();
		Project chaoPrj = Project.loadProjectFromURI(changeOntURI, errors);
		chaoPrj.setProjectURI(chaoURI);
		//don't set the property, if the project was not saved
		if (kb.getProject().getProjectDirectoryURI() != null) {
			setChAOProjectURI(kb, chaoURI);
		}

		RDFBackend.setSourceFiles(chaoPrj.getSources(),
				CHAO_PROJECT_NAME_PREFIX + kb.getProject().getName() + ".rdfs",
				CHAO_PROJECT_NAME_PREFIX + kb.getProject().getName() + ".rdf",
				PROTEGE_NAMESPACE);
		chaoPrj.setProjectURI(getChAOProjectURI(kb));

		putInMap(kb, chaoPrj.getKnowledgeBase());
		return chaoPrj.getKnowledgeBase();
	}


	//TODO: Not implemented yet
	public static KnowledgeBase createDBChAOKb(KnowledgeBase kb, KnowledgeBaseFactory factory,
			PropertyList sources, URI chaoURI) {
		throw new RuntimeException(new OperationNotSupportedException());
	}

	private static KnowledgeBase getChAOKbFromServer(KnowledgeBase kb) {
		String annotationName = new GetAnnotationProjectName(kb).execute();
		if (annotationName == null) {
			Log.getLogger().warning("Annotation/Change project not configured on server (use " +
					GetAnnotationProjectName.METAPROJECT_ANNOTATION_PROJECT_SLOT +
			" slot)");
			return null;
		}
		RemoteProjectManager project_manager = RemoteProjectManager.getInstance();
		FrameStoreManager framestore_manager = ((DefaultKnowledgeBase) kb).getFrameStoreManager();
		RemoteClientFrameStore remote_frame_store = framestore_manager.getFrameStoreFromClass(RemoteClientFrameStore.class);
		RemoteServer server = remote_frame_store.getRemoteServer();
		RemoteSession session = remote_frame_store.getSession();
		try {
			session = server.cloneSession(session);
		} catch (RemoteException e) {
			Log.getLogger().info("Could not find server side annotation/change project " + e);
			return null;
		}
		Project changes_project = project_manager.connectToProject(server, session, annotationName);
		return changes_project == null ? null: changes_project.getKnowledgeBase();
	}

	private static KnowledgeBase getChAOKbOnServer(KnowledgeBase kb) {
	    Server server = Server.getInstance();
	    String serverProjectName = null;
	    // ToDo - this is very awkward and inefficient (each call involves io)
	    for (String name : server.getAvailableProjectNames(null)) {
	        if (server.getProject(name).equals(kb.getProject())) {
	            serverProjectName = name;
	            break;
	        }
	    }
	    if (serverProjectName != null) {
	        MetaProject mp = server.getMetaProjectNew();
	        ProjectInstance pi = mp.getProject(serverProjectName);
	        String chaoProjectName = pi.getAnnotationProject().getName();
	        return server.getProject(chaoProjectName).getKnowledgeBase();
	    }
	    return null;
	}

	private static KnowledgeBase getFileBasedChAOKb(KnowledgeBase kb) {
		URI annotationProjURI = getChAOProjectURI(kb);
		File annotationProjFile = new File(annotationProjURI);
		if (annotationProjFile.exists()) {
			ArrayList errors = new ArrayList();
			Project chaoPrj = null;
			try {
				chaoPrj = Project.loadProjectFromURI(annotationProjURI, errors);
			} catch (Throwable t) {
				Log.getLogger().log(Level.WARNING, "Error at loading annotation/changes project from " + annotationProjURI +
						". Error message: " + t.getMessage(), t);
			}
			if (errors.size() > 0) {
				Log.getLogger().warning("There were errors at loading annotation/change project from " + annotationProjURI +
				". See log for more information.");
			}
			return chaoPrj != null ? chaoPrj.getKnowledgeBase() : null;
		}
		else {
		    /*
		     * ToDo Tania - this originally returned null.  Caused trouble with the first time the
		     * changes tab was opened.  Is there any reason not to create it here?
		     */
		    //return createRDFFileChAOKb(kb, annotationProjURI);
			return null;
		}
	}

	public static URI getChAOProjectURI(KnowledgeBase kb) {
		URI annotPrjUri = null;
		Project prj = kb.getProject();
		String annotPrjPath = (String) prj.getClientInformation(CHAO_PROJECT_CLIENT_INFO_KEY);
		if (annotPrjPath != null) {
			if (URIUtilities.isURI(annotPrjPath)) {
				annotPrjUri = URIUtilities.createURI(annotPrjPath);
			} else {
				annotPrjUri = URIUtilities.resolve(kb.getProject().getProjectURI(), annotPrjPath);
			}
			if (annotPrjUri == null) {
				Log.getLogger().warning("Could not load annotation/changes project from: " + annotPrjPath +
				". Trying default location.");
			} else {
				return annotPrjUri;
			}
		}
		String defaultAnnotPath = prj.getProjectDirectoryURI() + "/" + CHAO_PROJECT_NAME_PREFIX + prj.getName() + ".pprj";
		return URIUtilities.createURI(defaultAnnotPath);
	}

	/**
	 * This method should be called only in stand alone mode.
	 * It will have unpredictable results is called in client-server mode.
	 * After calling this method, make sure to call save on the domain project,
	 * otherwise this change will not be persistent.
	 * @param kb - the domain kb
	 * @param uri - the new URI of the annotation/change project
	 */
	public static void setChAOProjectURI(KnowledgeBase kb, URI uri) {
		URI relativeURI = URIUtilities.relativize(kb.getProject().getProjectURI(), uri);
		if (relativeURI == null) {
			Log.getLogger().warning("Could not set annotation/change project URI to " + uri);
			return;
		}
		kb.getProject().setClientInformation(CHAO_PROJECT_CLIENT_INFO_KEY, relativeURI.toString());
		//removeFromMap(kb); //TODO - check this
	}

	public static int saveChAOProject(KnowledgeBase kb) {
		KnowledgeBase changesKb = kb2chaoKb.get(kb);
		if (changesKb == null) { return -1; }
		Project changesProject = changesKb.getProject();

		if (changesProject.isMultiUserClient()) { return -1; }

		if (changesKb.getKnowledgeBaseFactory() instanceof RDFBackend) {
			URI savePprjURI = getChAOProjectURI(kb);
			if (savePprjURI == null) {
				Log.getLogger().warning("Could not save associated ChAO knowledge base. Could not retrieve the save location.");
				return -1;
			}
			String saveRDFSURI = FileUtilities.replaceExtension(URIUtilities.getName(savePprjURI), ".rdfs");
			String saveRDFURI = FileUtilities.replaceExtension(URIUtilities.getName(savePprjURI), ".rdf");

			RDFBackend.setSourceFiles(changesProject.getSources(), saveRDFSURI, saveRDFURI, PROTEGE_NAMESPACE);
			changesProject.setProjectURI(savePprjURI);

			ArrayList errors = new ArrayList();
			changesProject.save(errors);
			ProjectManager.getProjectManager().displayErrors("Errors at saving annotations/changes project", errors);
			return 0;
		}

		//for all other case, simply save
		ArrayList errors = new ArrayList();
		kb.getProject().save(errors);
		ProjectManager.getProjectManager().displayErrors("Errors at saving annotations/changes project", errors);
		return 0;
	}

	private static void putInMap(KnowledgeBase kb, KnowledgeBase chaoKb) {
		boolean alreadyInMap = kb2chaoKb.keySet().contains(kb);
		KnowledgeBase existingChaoKb = kb2chaoKb.put(kb, chaoKb);
		//TODO: check condition..
		if (!alreadyInMap) {
		    kb.getProject().addProjectListener(kbListener);
		}
	}

	private static void removeFromMap(KnowledgeBase kb) {
		kb2chaoKb.remove(kb);
		kb.getProject().removeProjectListener(kbListener);
	}

	public void dispose() {
		for (KnowledgeBase kb : kb2chaoKb.keySet()) {
			removeFromMap(kb);
		}
		kb2chaoKb.clear();
	}
}
