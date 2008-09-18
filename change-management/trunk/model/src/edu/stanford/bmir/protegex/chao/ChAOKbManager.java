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
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.ui.ProjectManager;
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
		 * TODO: not clear that we should remove from map if a project is closed.
		 * We should have a counter, that is increased when somebody gets a reference to the ChAO kb,
		 * and decreased when the corresponding project is closed.
		 * We should only remove the kb from the map, if the counter is 0.
		 * Or maybe use a WeakHashMap?
		 */
		@Override
		public void projectClosed(ProjectEvent event) {
			Project project = (Project) event.getSource();
			//FIXME:
			Log.getLogger().info(project + " closed. ChAO KB is still active.");
			//removeFromMap(project.getKnowledgeBase());
		};
	};


	public static KnowledgeBase getChAOKb(KnowledgeBase kb) {
		KnowledgeBase changesKb = kb2chaoKb.get(kb);
		if (changesKb != null || kb2chaoKb.containsKey(kb)) {
			return changesKb;
		}
		try {
			if (kb.getProject().isMultiUserClient()) {
				changesKb = getServerSideChangeKb(kb);
			} else {
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
		} catch (Exception e) {
			Log.getLogger().log(Level.WARNING,
					"Could not find annotation/changes ontology in the plugins directory.", e);
			return null;
		}
		ArrayList errors = new ArrayList();
		Project chaoPrj = Project.loadProjectFromURI(changeOntURI, errors);
		chaoPrj.setProjectURI(chaoURI);
		setChAOProjectURI(kb, chaoURI);

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

	private static KnowledgeBase getServerSideChangeKb(KnowledgeBase kb) {
		String annotationName = (String) new GetAnnotationProjectName(kb).execute();
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
		return null;
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

	public static void saveChAOProject(KnowledgeBase kb) {
		KnowledgeBase changesKb = kb2chaoKb.get(kb);
		if (changesKb == null) { return; }
		Project changesProject = changesKb.getProject();

		if (changesProject.isMultiUserClient()) { return; }

		if (changesKb.getKnowledgeBaseFactory() instanceof DatabaseKnowledgeBaseFactory) {
			return; //don't save if in DB mode
		}

		/*
		 * FIXME: right now it does not take into account, if there
		 * is a project client set with the chao URI. To be fixed very soon.
		 * (This is the old way it used to work..)
		 */
		if (changesKb.getKnowledgeBaseFactory() instanceof RDFBackend) {
			RDFBackend.setSourceFiles(changesProject.getSources(),
					CHAO_PROJECT_NAME_PREFIX + kb.getProject().getName() + ".rdfs",
					CHAO_PROJECT_NAME_PREFIX + kb.getProject().getName() + ".rdf",
					PROTEGE_NAMESPACE);
			changesProject.setProjectURI(ChAOKbManager.getChAOProjectURI(kb));
			ArrayList errors = new ArrayList();
			changesProject.save(errors);
			ProjectManager.getProjectManager().displayErrors("Errors at saving annotations/changes project", errors);
			return;
		}

		//for all other case, simply save
		ArrayList errors = new ArrayList();
		kb.getProject().save(errors);
		ProjectManager.getProjectManager().displayErrors("Errors at saving annotations/changes project", errors);
	}

	private static void putInMap(KnowledgeBase kb, KnowledgeBase chaoKb) {
		kb2chaoKb.put(kb, chaoKb);
		kb.getProject().addProjectListener(kbListener);
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
