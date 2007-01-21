package edu.stanford.smi.protegex.changes;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.exception.ProtegeIOException;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.server.RemoteClientProject;
import edu.stanford.smi.protege.server.RemoteServer;
import edu.stanford.smi.protege.server.RemoteServerProject;
import edu.stanford.smi.protege.server.RemoteSession;
import edu.stanford.smi.protege.server.Server;
import edu.stanford.smi.protege.server.ServerProject;
import edu.stanford.smi.protege.server.framestore.ServerFrameStore;
import edu.stanford.smi.protege.server.metaproject.MetaProjectInstance;
import edu.stanford.smi.protege.server.metaproject.MetaProject.SlotEnum;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protege.widget.AbstractTabWidget;
import edu.stanford.smi.protegex.changes.action.AnnotationShowAction;
import edu.stanford.smi.protegex.changes.action.ChangeShowAction;
import edu.stanford.smi.protegex.changes.action.ChangesSearchClear;
import edu.stanford.smi.protegex.changes.action.ChangesSearchExecute;
import edu.stanford.smi.protegex.changes.listeners.ChangesClsListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesFrameListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesInstanceListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesKBListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesSlotListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesTransListener;
import edu.stanford.smi.protegex.changes.owl.Util;
import edu.stanford.smi.protegex.changes.ui.*;
import edu.stanford.smi.protegex.storage.rdf.RDFBackend;
 
/**
 * Change Management Tab widget
 * 
 */
public class ChangesTab extends AbstractTabWidget {
	
	// Main UI tables
	public static final String HEADERCOMP_NAME_CHANGE_VIEWER = "Change Viewer";
	public static final String HEADERCOMP_NAME_ANNOTATE_VIEWER = "Annotation Viewer";
	public static final String LABELCOMP_NAME_CHANGE_HIST = "Change History";
	public static final String LABELCOMP_NAME_ANNOTATIONS = "Annotations";
	public static final String LABELCOMP_NAME_ASSOC_CHANGES = "Associated Changes";
	public static final String LABELCOMP_NAME_DETAIL_CHANGES = "Detailed Changes";
	public static final String ACTION_NAME_CREATE_ANNOTATE = "Create Annotation";
	public static final String ACTION_NAME_REMOVE_ANNOTATE = "Remove Annotation";
	public static final String ACTION_NAME_EDIT_ANNOTATE = "Edit Annotation";
	public static final String FILTER_NAME_DETAIL_VIEW = "Detailed View";
	public static final String FILTER_NAME_SUMMARY_VIEW = "Summary View";
		
	// Search Panel
	private static final String SEARCH_PANEL_TITLE = "Search";
	private static final String SEARCH_PANEL_BUTTON_GO = "Go";
	private static final String SEARCH_PANEL_BUTTON_CLEAR = "Clear"; 
	
	private static final String ANNOT_PANEL_TITLE = "Create Annotation";
	
	// Transaction signals
	public static final String TRANS_SIGNAL_TRANS_BEGIN = "transaction_begin";
	public static final String TRANS_SIGNAL_TRANS_END = "transaction_end";
	public static final String TRANS_SIGNAL_START = "start";
	
	
	public static final String ANNOTATION_PROJECT_EXISTS = "annotation_proj_exists";
	public static final String ANNOTATION_PROJECT_NAME_PREFIX = "annotation_";    
    
	private static final String CHANGES_TAB_NAME = "Changes";
        
        public static final String METAPROJECT_CHANGE_PROJECT_SLOT = "changesProject";
	
	private static String userName;
	private static Project currProj;
	private static Project changes;
	private static KnowledgeBase cKb;
	private static KnowledgeBase currKB;
	
	private static JTable cTable;
	private static JTable aTable;
	private static JTable acTable;
	//private static JTable dcTable;
	private static JComboBox annTypes;
	private static ChangeTableModel cTableModel;
	private static ChangeTableModel acTableModel;
	//private static ChangeTableModel dcTableModel;
	private static AnnotationTableModel aTableModel;
	
	private static Instance annotateInst;
	private static Instance instToEdit;
	
	private static HashMap nameChanges = new HashMap();
	private static HashMap createChangeName = new HashMap();
		
	// Maintaing transaction objects
	private static Stack transStack;
	private static int transCount = 0;
	private static boolean inTransaction;
	
	private static boolean inCreateClass = false;
	private static boolean inCreateSlot = false;
	private static boolean inRemoveAnnotation = false;
	
	private static boolean isOwlProject;
	
	private static ChangeMenu cMenu;
	private static RemoveInstanceAction remInst;
	private static EditInstanceAction editInst;
	private static AddInstanceAction addInst;
	
	//JTreeTable
	
	private static JTreeTable cTreeTable;
	private static ChangeTreeTableModel cTreeTableModel;
	
	
	public static void addNameChange(String oldName, String newName) {
		nameChanges.put(newName, oldName);
	}
	
	public static String getUserName() {
		return userName;
	}
	
	public static HashMap getNameChanges() {
		return nameChanges;
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
	
	public static boolean getInRemoveAnnotation() {
		return inRemoveAnnotation;
	}
	
	public static void setInRemoveAnnotation(boolean val) {
		inRemoveAnnotation = val;
	}
	
	// Initialize the plugin
	public void initialize() {
		//TODO remove
		//System.out.println("initialize!");
		
		userName = ApplicationProperties.getUserName();
		transStack = new Stack();
		
		currProj = getProject();
		currKB = currProj.getKnowledgeBase();
		
		// Annotation project exists, load associated changes/annotations
		if (createChangeProject()) {
			initTables();
			loadExistingData();
			//initColumnSizes(cTable);
		} else {
			initTables();
			//initColumnSizes(cTable);
		}
		
		TransactionUtility.initialize();
		
		//Check to see if the project is an OWL one
		isOwlProject = Util.kbInOwl(currKB);
		
		// Register listeners
		if (isOwlProject) {
			Util.registerOwlListeners(currKB);
		} else {
			registerKBListeners();
		}
        		
       // Initialize the tab text
		setLabel(CHANGES_TAB_NAME);
		
		// Initialize the UI
		initUI();
		cTreeTable.getTree().expandPath(AbstractTreeTableModel.rootPath);
		//cTreeTable.getTree().setRootVisible(false);
		
	}

	public static KnowledgeBase getChangesKB() {
		return cKb;
	}
	
	public static Project getChangesProj() {
		return changes;
	}
        
    public static String getAnnotationProjectName() {
      return ANNOTATION_PROJECT_NAME_PREFIX + currProj.getName() + ".pprj";
    }
    
    public static String getAnnotationSchemaName() {
      return ANNOTATION_PROJECT_NAME_PREFIX + currProj.getName() + ".rdfs";
    }
    
    public static String getAnnotationInstancesName() {
      return ANNOTATION_PROJECT_NAME_PREFIX + currProj.getName() + ".rdf";
    }
	
	private void initUI() {
		// Create menu item
		cMenu = new ChangeMenu(cKb,changes);
		JMenuBar menuBar = getMainWindowMenuBar();
	    menuBar.add (cMenu);
     	//cTreeTable.addMouseListener(new ChangeShowAction(cTreeTable, cTableModel, changes));
		//cTable.addMouseListener(new ChangeShowAction(cTable, cTableModel, changes));
		aTable.addMouseListener(new AnnotationShowAction(aTable, aTableModel, changes));
		JScrollPane scroll = ComponentFactory.createScrollPane(cTreeTable);
		//JScrollPane scroll = ComponentFactory.createScrollPane(cTable);
		//JScrollPane scroll1 = ComponentFactory.createScrollPane(dcTable);
		JScrollPane scroll2 = ComponentFactory.createScrollPane(aTable);
		JScrollPane scroll3 = ComponentFactory.createScrollPane(acTable);
				
		JPanel interPane = new JPanel();
		interPane.setLayout(new BoxLayout(interPane, BoxLayout.PAGE_AXIS));
		interPane.add(initSearchPanel());
		
		interPane.add(scroll);
		LabeledComponent changeHistLC = new LabeledComponent(LABELCOMP_NAME_CHANGE_HIST, interPane,true);
		
		changeHistLC.doLayout();
		changeHistLC.addHeaderSeparator();
		addInst = new AddInstanceAction(changeHistLC, ACTION_NAME_CREATE_ANNOTATE);
	    addInst.setEnabled(false);
//		FilterTransInfoAction fNonTransInst = new FilterTransInfoAction(FILTER_NAME_DETAIL_VIEW);
		//FilterTransAction fTransInst = new FilterTransAction(FILTER_NAME_SUMMARY_VIEW);
		//changeHistLC.addHeaderButton(fTransInst);
//		changeHistLC.addHeaderButton(fNonTransInst);
	    changeHistLC.setHeaderComponent(initAnnotPanel(), BorderLayout.EAST);
		changeHistLC.addHeaderButton(addInst);
		
		
		//LabeledComponent detailLC = new LabeledComponent(LABELCOMP_NAME_DETAIL_CHANGES, scroll1, true);
		//detailLC.doLayout();
		//detailLC.addHeaderSeparator();
		//detailLC.setMaximumSize(new Dimension(1000,800));
		//detailLC.setPreferredSize(new Dimension(scroll3.getWidth(),90));
		
		/*JSplitPane splitPanel1 = ComponentFactory.createTopBottomSplitPane(false);
		splitPanel1.setResizeWeight(0.5);
		splitPanel1.setDividerLocation(0.75);
		splitPanel1.setTopComponent(changeHistLC);
		splitPanel1.setBottomComponent(detailLC);*/
		
		
		
		LabeledComponent annotLC = new LabeledComponent(LABELCOMP_NAME_ANNOTATIONS, scroll2, true);
		annotLC.doLayout();
		annotLC.addHeaderSeparator();
		remInst = new RemoveInstanceAction(ACTION_NAME_REMOVE_ANNOTATE);
		editInst = new EditInstanceAction(ACTION_NAME_EDIT_ANNOTATE);
		remInst.setEnabled(false);
		editInst.setEnabled(false);
		annotLC.addHeaderButton(remInst);
		annotLC.addHeaderButton(editInst);
		//annotLC.setMaximumSize(new Dimension(1000,800));
		//annotLC.setPreferredSize(new Dimension(scroll3.getWidth(),250));
		
		LabeledComponent assocLC = new LabeledComponent(LABELCOMP_NAME_ASSOC_CHANGES, scroll3, true);
		assocLC.doLayout();
		assocLC.addHeaderSeparator();
		//assocLC.setMaximumSize(new Dimension(1000,800));
		//assocLC.setPreferredSize(new Dimension(scroll3.getWidth(),150));
		
		JSplitPane splitPanel = ComponentFactory.createTopBottomSplitPane(false);
		splitPanel.setResizeWeight(0.5);
		splitPanel.setDividerLocation(0.5);
		splitPanel.setTopComponent(annotLC);
		splitPanel.setBottomComponent(assocLC);
		
		//HeaderComponent changeView = new HeaderComponent(HEADERCOMP_NAME_CHANGE_VIEWER, null, splitPanel1);
		HeaderComponent changeView = new HeaderComponent(HEADERCOMP_NAME_CHANGE_VIEWER, null, changeHistLC);
		HeaderComponent annotView = new HeaderComponent(HEADERCOMP_NAME_ANNOTATE_VIEWER, null, splitPanel);
		
		setLayout(new GridLayout(2,1));
		add(changeView);
		add(annotView);
		//initColumnSizes(cTable);
	}
	
	private JPanel initAnnotPanel() {
		JPanel annotPanel = ComponentFactory.createPanel();
		JLabel annotLabel = new JLabel(ANNOT_PANEL_TITLE);
		String[] annotFields = {	AnnotationTableModel.TYPE_COMMENT, 
				                    AnnotationTableModel.TYPE_EXPLANATION,
				                    AnnotationTableModel.TYPE_EXAMPLE,
				                    AnnotationTableModel.TYPE_QUESTION,
				                    AnnotationTableModel.TYPE_ADVICE,
				                    AnnotationTableModel.TYPE_SEEALSO,
		                       };
		
		annTypes = new JComboBox(annotFields);
		annTypes.setSelectedIndex(0);
	
		
		annotPanel.add(annotLabel);
		annotPanel.add(annTypes);
		return annotPanel;
	}
	
	private JPanel initSearchPanel() {
		JPanel searchPanel = ComponentFactory.createPanel();
		JLabel searchLabel = new JLabel(SEARCH_PANEL_TITLE);
		String[] searchFields = {	ChangeTableModel.CHANGE_COLNAME_AUTHOR,
				                    ChangeTableModel.CHANGE_COLNAME_ACTION, 
				                    ChangeTableModel.CHANGE_COLNAME_DESCRIPTION,
									ChangeTableModel.CHANGE_COLNAME_CREATED
									
									};
		
		JComboBox cbox = new JComboBox(searchFields);
		cbox.setSelectedIndex(0);
	
		JTextField searchText = new JTextField(25);
		JButton searchButton = new JButton(SEARCH_PANEL_BUTTON_GO);
		//ActionListener searchExecute = new ChangesSearchExecute(cbox, searchText, cTableModel);
		ActionListener searchExecute = new ChangesSearchExecute(cbox, searchText, cTreeTableModel);
		searchButton.addActionListener(searchExecute);
		
		JButton clearButton = new JButton(SEARCH_PANEL_BUTTON_CLEAR);
		//ActionListener searchClear = new ChangesSearchClear(cTableModel);
		ActionListener searchClear = new ChangesSearchClear(cTreeTableModel);
		clearButton.addActionListener(searchClear);
		
		searchPanel.add(searchLabel);
		searchPanel.add(cbox);
		searchPanel.add(searchText);
		searchPanel.add(searchButton);
		searchPanel.add(clearButton);
		
		searchPanel.setLayout(new FlowLayout());
		//initColumnSizes(cTable);
		return searchPanel;
	}
	
/*	private void initColumnSizes(JTable table) {
		
		TableColumn column = null;
		for (int i = 0; i < 5; i++) {
		    column = table.getColumnModel().getColumn(i);
		    //System.out.println(table.getWidth()+"  " +column.getWidth()+ "   "+ column.getMaxWidth()+ "     " +  column.getMinWidth()+ "  "+ column.getPreferredWidth());
		   
		    if (i == 3) {
		        column.setPreferredWidth(15); 
		        column.setMaxWidth(15);
		    }
		    else {
		        column.setPreferredWidth(75);
		    }
		}
	}*/
	
	
	private void initTables() {
		// Create Tables
		Instance ROOT = ChangeCreateUtil.createChange(
	            cKb,
	            ChangeCreateUtil.CHANGETYPE_INSTANCE_ADDED, 
	            "ROOT", 
	            "ROOT", 
	            "ROOT");
		TreeTableNode root = new TreeTableNode(ROOT,cKb);
		
		
		
    	
    	
		
		
		cTableModel = new ChangeTableModel(cKb);
		acTableModel = new ChangeTableModel(cKb);
		//dcTableModel = new ChangeTableModel(cKb);
		aTableModel = new AnnotationTableModel(cKb);
		cTreeTableModel = new ChangeTreeTableModel(root, cKb);
		
		cTable = new JTable(cTableModel);
		acTable = new JTable(acTableModel);
		//dcTable = new JTable(dcTableModel);
		aTable = new JTable(aTableModel);
		cTreeTable = new JTreeTable(cTreeTableModel);
	
		ComponentFactory.configureTable(aTable);
		ComponentFactory.configureTable(acTable);
		//ComponentFactory.configureTable(dcTable);
		//ComponentFactory.configureTable(cTable);
		
		aTable.setShowGrid(false);
		aTable.setIntercellSpacing(new Dimension(0, 0));
		aTable.setColumnSelectionAllowed(false);
		aTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		aTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		
		acTable.setShowGrid(false);
		acTable.setIntercellSpacing(new Dimension(0, 0));
		acTable.setColumnSelectionAllowed(false);
		acTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		acTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		acTable.setDefaultRenderer(Object.class, new ColoredTableCellRenderer());
		//initColumnSizes(acTable);
		
		/*dcTable.setShowGrid(false);
		dcTable.setIntercellSpacing(new Dimension(0, 0));
		dcTable.setColumnSelectionAllowed(false);
		dcTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		dcTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dcTable.setDefaultRenderer(Object.class, new ColoredTableCellRenderer());
		initColumnSizes(dcTable);*/
		
	/*	cTable.setShowGrid(false);
		cTable.setIntercellSpacing(new Dimension(0, 0));
		cTable.setColumnSelectionAllowed(false);
		cTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		cTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		cTable.setDefaultRenderer(Object.class, new ColoredTableCellRenderer());*/
		//initColumnSizes(cTable);
		cTreeTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		cTreeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//cTreeTable.getTree().expandPath(AbstractTreeTableModel.rootPath);
		//cTreeTable.getTree().setRootVisible(false);
		
		
		
		ListSelectionModel lsm = aTable.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()){
					return;
				}
				
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if(!lsm.isSelectionEmpty()) {
					remInst.setEnabled(true);
					editInst.setEnabled(true);
					int selectedRow = lsm.getMinSelectionIndex();
					String instName = aTableModel.getInstanceName(selectedRow);
					Instance selectedInst = cKb.getInstance(instName);
					acTableModel.setChanges(ChangeCreateUtil.getAnnotationChanges(cKb, selectedInst));
				} 
			}
		});
		
		
		
		ListSelectionModel tlsm = cTreeTable.getSelectionModel();
		tlsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()){
					return;
				}
				
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if(!lsm.isSelectionEmpty()) {
					addInst.setEnabled(true);
			
				} 
			}
		});
		
		
		/*ListSelectionModel lsm1 = cTable.getSelectionModel();
		lsm1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()){
					return;
				}
				
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if(!lsm.isSelectionEmpty()) {
					int selectedRow = lsm.getMinSelectionIndex();
					Object instName = cTableModel.getObjInRow(selectedRow);
					Instance selectedInst = (Instance)instName;
					Cls changeInstType = selectedInst.getDirectType();
					if (changeInstType.getName().equals(ChangeCreateUtil.CHANGETYPE_TRANS_CHANGE)) {
						
						Collection relChanges = ChangeCreateUtil.getChanges(cKb, selectedInst);
						dcTableModel.setChanges(relChanges);
					} 
					else
						dcTableModel.clearData();	
				} 
			}
		});
	*/
		
	}
	
	private static void registerKBListeners() {
		currKB.addKnowledgeBaseListener(new ChangesKBListener());
		currKB.addClsListener(new ChangesClsListener());
		currKB.addInstanceListener(new ChangesInstanceListener());
		currKB.addSlotListener(new ChangesSlotListener());
		currKB.addTransactionListener(new ChangesTransListener());
		currKB.addFrameListener(new ChangesFrameListener());
	}
	
	private boolean createChangeProject() {
		boolean annotateExists = false;
		Collection errors = new ArrayList();
		
		// Check if annotations project already exists for this project.
		String annotationExists = (String)currProj.getClientInformation(ANNOTATION_PROJECT_EXISTS);
		
		URI changeOntURI = null;
		try {
			changeOntURI = ChangesTab.class.getResource("/projects/changes.pprj").toURI();
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
			String annotationName = getAnnotationProjectName();
			URI annotationURI;
                        if (currProj.isMultiUserClient()) {
                          changes = getRemoteAnnotationProject();
                        }
                        else {
                          try {
                            annotationURI = new URI(currProj.getProjectDirectoryURI()+"/" + annotationName);
                            changes = Project.loadProjectFromURI(annotationURI, errors);

                            changes.includeProject(changeOntURI,errors);
                            changes.mergeIncludedProjects();				
                          } catch (URISyntaxException e) {
                            errors.add(e);
                          }
                        }

			displayErrors(errors);
                        if (changes == null) { 
                          Log.getLogger().warning("Changes tab not initialized");
                          throw new RuntimeException("Changes tab not initialized");
                        }
			
			cKb = changes.getKnowledgeBase();
		}
		
		currProj.addProjectListener(new ProjectAdapter() {
			ArrayList errors = new ArrayList();
			public void projectSaved(ProjectEvent event) {
				String changesName = "annotation_" + currProj.getName();
				String myNameSpace = "http://protege.stanford.edu/kb#";
				RDFBackend.setSourceFiles(changes.getSources(), 
                                          getAnnotationSchemaName(), 
                                          getAnnotationInstancesName(), 
                                          myNameSpace);
				
				URI projUri;
				try {
					projUri = new URI(currProj.getProjectDirectoryURI()+"/"+changesName +".pprj");
					changes.setProjectURI(projUri);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				changes.save(errors);
				displayErrors(errors);
			
				//TODO remove
				//System.out.println("Successfully saved annotations");
			}
		});
		//TODO remove
		//System.out.println("Successfully loaded changes project");
		
		return annotateExists;
	}
        
        private Project getRemoteAnnotationProject() throws ProtegeException {
          String annotationProjectName 
              = (String) new RequestAnnotationProjectName(getKnowledgeBase()).execute();
          RemoteClientProject project = (RemoteClientProject) getKnowledgeBase().getProject();
          RemoteServer server = project.getServer();
          RemoteSession session = project.getSession();
          try {
            RemoteServerProject annotationProject = server.openProject(annotationProjectName, session);
            return RemoteClientProject.createProject(server, annotationProject, session, true);
          } catch (RemoteException re) {
            throw new ProtegeIOException(re);
          }
        }
	
        public static class RequestAnnotationProjectName extends ProtegeJob {
          private static final long serialVersionUID = -2383974265774152135L;
          

          public RequestAnnotationProjectName(KnowledgeBase kb) {
            super(kb);
          }

          @Override
          public Object run() throws ProtegeException {
            MetaProjectInstance project = getMetaProjectInstance();
            Instance projectInOnt = project.getProtegeInstance();
            KnowledgeBase metaOntology = projectInOnt.getKnowledgeBase();
            Slot changeAnnotationProjectSlot = metaOntology.getSlot(METAPROJECT_CHANGE_PROJECT_SLOT);
            Instance changeProject 
               = (Instance) projectInOnt.getOwnSlotValue(changeAnnotationProjectSlot);
            
            return (String) changeProject.getOwnSlotValue(project.getMetaProject().getSlot(SlotEnum.name));
          }
          
        }
        
	private static void loadExistingData() {
		Collection annotateInsts = ChangeCreateUtil.getAnnotationInsts(cKb);
		Collection changeInsts = ChangeCreateUtil.getChangeInsts(cKb);
		
		loadChanges(changeInsts);
		loadAnnotations(annotateInsts);
		
		//TODO remove
		//System.out.println("Successfully loaded associated changes/annotations.");
	}
	
	private static void loadChanges(Collection changeInsts) {
		ArrayList changeList = new ArrayList(changeInsts);
		Collections.sort(changeList, new InstanceDateComparator(cKb));
		
		for (Iterator iter = changeList.iterator(); iter.hasNext();) {
			Instance aInst = (Instance) iter.next();
			
			cTableModel.addChangeData(aInst);
			cTreeTableModel.addChangeData(aInst);
		}
	}
	
	private static void loadAnnotations(Collection annotateInsts) {
		
		ArrayList annotationList = new ArrayList(annotateInsts);
		Collections.sort(annotationList, new InstanceDateComparator(cKb));
		
		for (Iterator iter = annotationList.iterator(); iter.hasNext();) {
			Instance aInst = (Instance) iter.next();
			aTableModel.addAnnotationData(aInst);
		}
		
	}

	private static void displayErrors(Collection errors) {
		Iterator i = errors.iterator();
		while (i.hasNext()) {
			Object elem = i.next();
			System.out.println("className: " + elem.getClass().getName());
			if (elem instanceof Exception) {
				((Exception)elem).printStackTrace(System.out);
			} 
			
			//else {
			//	System.out.println("Error: " + elem);	
			//}
		}
	}
	
	public static void createChange(Instance aChange) {
		boolean addChange = true;
		
		if (aChange.getDirectType().getName().equals(ChangeCreateUtil.CHANGETYPE_NAME_CHANGED)) {
			String oldName = ChangeCreateUtil.getNameChangedOldName(cKb, aChange);
			String newName = ChangeCreateUtil.getNameChangedNewName(cKb, aChange);
			addNameChange(oldName, newName);
			
			if (createChangeName.containsKey(oldName)) {
				addChange = false;
				Integer rowCount= (Integer) createChangeName.get(oldName);
				Instance cChange = (Instance) cTableModel.getObjInRow(rowCount.intValue());
				
				System.out.println("going to update");
				//System.out.println updated
				ChangeCreateUtil.setInstApplyTo(cKb, cChange, newName);
				
				//cChange.setOwnSlotValue(ChangeOntUtil.getApplyToSlot(), newName);
				updateCreateName(cChange, oldName, newName);
				
				createChangeName.remove(oldName);
				cTableModel.update();
				cTreeTableModel.update();
			}
		}
		if (aChange.getDirectType().getName().equals(ChangeCreateUtil.CHANGETYPE_SUBCLASS_ADDED)) {
			addChange = false;
		}
		
		if (inTransaction) {
			transStack.push(aChange);
		} else {
			checkForCreateChange(aChange);	
			if (addChange) {
				cTableModel.addChangeData(aChange);
				cTreeTableModel.addChangeData(aChange);
				cMenu.setEnabledLastChange(true);
				cMenu.setChange(aChange);	
			}
		}
	}
	
	// setting context and applyTo
	private static void updateCreateName(Instance aChange, String oldName, String newName) {
		Collection changeList = ChangeCreateUtil.getTransChanges(cKb, aChange);
		String context = null;
		
		for (Iterator iter = changeList.iterator(); iter.hasNext();) {
			Instance cInst = (Instance) iter.next();
			String changeAction = ChangeCreateUtil.getAction(cKb, cInst);
			
			if (changeAction.equals(ChangeCreateUtil.CHANGETYPE_CLASS_CREATED)
						|| changeAction.equals(ChangeCreateUtil.CHANGETYPE_SLOT_CREATED)
						|| changeAction.equals(ChangeCreateUtil.CHANGETYPE_PROPERTY_CREATED)
						) 
				{
				
				ChangeCreateUtil.setInstApplyTo(cKb, cInst, newName);
				ChangeCreateUtil.setInstApplyTo(cKb, aChange, newName);
				
				//cInst.setOwnSlotValue(ChangeOntUtil.getApplyToSlot(), newName);
				//aChange.setOwnSlotValue(ChangeOntUtil.getApplyToSlot(), newName);
				
				String cCtxt = ChangeCreateUtil.getContext(cKb, cInst);
				int idx = cCtxt.indexOf(":");
				StringBuffer txt = new StringBuffer(cCtxt.substring(0, idx));
				txt.append(": ");
				txt.append(newName);
				context = txt.toString();
				
				ChangeCreateUtil.setInstContext(cKb, cInst, context);
				ChangeCreateUtil.setInstContext(cKb, aChange, context);
				
				//cInst.setOwnSlotValue(ChangeOntUtil.getContextSlot(), context);
				//aChange.setOwnSlotValue(ChangeOntUtil.getContextSlot(), context);
			}
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
				checkForCreateChange(changeInst);	
				
				cTableModel.addChangeData(changeInst);
				cTreeTableModel.addChangeData(changeInst);
				cMenu.setEnabledLastChange(true);
				cMenu.setChange(changeInst);
				
				transStack.clear();
			} 
		} 
	}
	
	
	// takes care of case when class is created & then renamed.
	private static void checkForCreateChange(Instance aChange) {
		String changeAction = ChangeCreateUtil.getAction(cKb, aChange);
		if (changeAction.equals(ChangeCreateUtil.CHANGETYPE_CLASS_CREATED)
				|| changeAction.equals(ChangeCreateUtil.CHANGETYPE_SLOT_CREATED)
				|| changeAction.equals(ChangeCreateUtil.CHANGETYPE_PROPERTY_CREATED)
				)
				{
			Integer rowCount = new Integer(cTableModel.getRowCount());
			createChangeName.put(ChangeCreateUtil.getApplyTo(cKb, aChange), rowCount);
		}
	}
	
	public static void updateAnnotationTable() {
		aTableModel.update();
	}
	
	public static void createAnnotation(Instance annotateInst) {
		Slot body = cKb.getSlot(ChangeCreateUtil.SLOT_NAME_BODY);
		Object bdy = annotateInst.getOwnSlotValue(body);
		if (bdy == null) {
			cKb.deleteInstance(annotateInst);
		}
		else{
		annotateInst = ChangeCreateUtil.updateAnnotation(cKb, annotateInst);
		aTableModel.addAnnotationData(annotateInst);
		}
	}
	
	public static String getTimeStamp() {
		Date currTime = new Date();
		
		String datePattern = "MM/dd/yyyy HH:mm:ss zzz";
		SimpleDateFormat format = new SimpleDateFormat(datePattern);
		String time = format.format(currTime);
		
		return time;
	}
	
	public static void main(String[] args) {
	
		edu.stanford.smi.protege.Application.main(args);
	}
	
	public class AddInstanceAction extends AbstractAction {
		
		Component myComp;
		
		public AddInstanceAction(Component c, String prompt) {
			super(prompt, Icons.getCreateClsNoteIcon());
			myComp = c;
		}
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			
			if (cTreeTable.getSelectedRowCount() > 0) {
				
				int[] selected = cTreeTable.getSelectedRows();
				Collection chngInsts = ChangeCreateUtil.getChangeInsts(cKb);
				Object[] chngInstArray = chngInsts.toArray();
				Collection chngInstSelected = new ArrayList();
				
				for (int i = 0; i < selected.length; i++) {
					Instance changeInst = (Instance)cTreeTableModel.getObjInRow(selected[i]-1);
					//Instance changeInst = (Instance)cTableModel.getObjInRow(selected[i]-1);
					chngInstSelected.add(changeInst);
				}
			    String annotType = (String)annTypes.getSelectedItem();
				annotateInst = ChangeCreateUtil.createAnnotation(cKb, annotType, chngInstSelected);
				JFrame edit = changes.show(annotateInst);
				
				edit.addWindowListener(new WindowListener() {
					
					public void windowClosed(WindowEvent arg0) {
						createAnnotation(annotateInst);	
						//addInst.setEnabled(false);
					}

					public void windowClosing(WindowEvent arg0) {
					}
					
					public void windowOpened(WindowEvent arg0) {
					}
					
					public void windowIconified(WindowEvent arg0) {
					}

					public void windowDeiconified(WindowEvent arg0) {
					}
					
					public void windowActivated(WindowEvent arg0) {
					}
					
					public void windowDeactivated(WindowEvent arg0) {
					}
				});
				edit.setVisible(true);
			}
		}
	}
	
	public class RemoveInstanceAction extends AbstractAction {
		
		public RemoveInstanceAction(String prompt) {
			super(prompt, Icons.getDeleteClsNoteIcon());
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			int numSelect = aTable.getSelectedRowCount();
			
			if (numSelect > 1) { 
				aTableModel.removeAnnotationData(aTable.getSelectedRows());
			} else if (numSelect == 1) {
				String delName = aTableModel.getInstanceName(aTable.getSelectedRow());
				Instance instToDel = cKb.getInstance(delName);
				cKb.deleteInstance(instToDel);
				aTableModel.removeAnnotationData(aTable.getSelectedRow());
			}
			
			aTable.clearSelection();
			remInst.setEnabled(false);
			editInst.setEnabled(false);
			
			
		}
	}
	
	public class EditInstanceAction extends AbstractAction {
		public EditInstanceAction(String prompt) {
			super(prompt, Icons.getClsNoteIcon());
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			int numSelect = aTable.getSelectedRowCount();
			
			if (numSelect == 1) {
				String instEditName = aTableModel.getInstanceName(aTable.getSelectedRow());
				
				instToEdit = cKb.getInstance(instEditName);
				
				JFrame edit = changes.show(instToEdit);
				edit.addWindowListener(new WindowListener() {
					public void windowOpened(WindowEvent arg0) {
					}

					public void windowClosing(WindowEvent arg0) {
					}

					public void windowClosed(WindowEvent arg0) {
						aTableModel.editAnnotationData(instToEdit, aTable.getSelectedRow());
						remInst.setEnabled(false);
						editInst.setEnabled(false);
					}

					public void windowIconified(WindowEvent arg0) {
					}

					public void windowDeiconified(WindowEvent arg0) {
					}

					public void windowActivated(WindowEvent arg0) {
					}

					public void windowDeactivated(WindowEvent arg0) {
					}
				});
				
				edit.setVisible(true);
				
			}
		}
	}
	
	/*public class FilterTransAction extends AbstractAction {
		public FilterTransAction(String prompt) {
			super(prompt, Icons.getViewClsIcon());
		}

		 (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 
		public void actionPerformed(ActionEvent arg0) {
			//cTableModel.filterTrans();
			//acTableModel.filterTrans();
			//testing TreeTableModel
			Instance ROOT = ChangeCreateUtil.createChange(
		            cKb,
		            ChangeCreateUtil.CHANGETYPE_INSTANCE_ADDED, 
		            "ROOT", 
		            "ROOT", 
		            "ROOT");
			TreeTableNode root = new TreeTableNode(ROOT,cKb);
			Instance CHILD = ChangeCreateUtil.createChange(
		            cKb,
		            ChangeCreateUtil.CHANGETYPE_INSTANCE_ADDED, 
		            "CHILD", 
		            "CHILD", 
		            "CHILD");
			TreeTableNode child = new TreeTableNode(CHILD,cKb);
			Instance CHILD1 = ChangeCreateUtil.createChange(
		            cKb,
		            ChangeCreateUtil.CHANGETYPE_INSTANCE_ADDED, 
		            "CHILD1", 
		            "CHILD1", 
		            "CHILD1");
			TreeTableNode child1 = new TreeTableNode(CHILD1,cKb); 	
			root.addChild(child);
			
			ChangeTreeTableModel ttModel = new ChangeTreeTableModel(root, cKb);
	    	
	    	JTreeTable tt = new JTreeTable(ttModel);
	    	//tt.setShowGrid(true);
	    	tt.getTree().setRootVisible(false);
	    
	    	JFrame f = new JFrame();
	    	f.addWindowListener(new WindowListener()  {
	    		
				public void windowClosing(WindowEvent e) {
				    System.exit(0);
				}
				public void windowOpened(WindowEvent arg0) {
				}

				
				public void windowClosed(WindowEvent arg0) {
					
				}

				public void windowIconified(WindowEvent arg0) {
				}

				public void windowDeiconified(WindowEvent arg0) {
				}

				public void windowActivated(WindowEvent arg0) {
				}

				public void windowDeactivated(WindowEvent arg0) {
				}
		    });	    
	    	f.getContentPane().add(new JScrollPane(tt));
	    	f.pack();
	    	f.setVisible(true);
	    	//root.addChild(child1);
	    	

		}
	}*/
	
	/*public class FilterTransInfoAction extends AbstractAction {
		public FilterTransInfoAction(String prompt) {
			super(prompt, Icons.getViewClsIcon());
		}
		 (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 
		public void actionPerformed(ActionEvent arg0) {
			cTableModel.filterTransInfo();
			acTableModel.filterTransInfo();
		}
	}
	
	public class FilterAllAction extends AbstractAction {
		public FilterAllAction(String prompt) {
			super(prompt, Icons.getViewClsIcon());
		}
		 (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 
		public void actionPerformed(ActionEvent arg0) {
			cTableModel.filterAll();
			acTableModel.filterAll();
		}
	}*/
        
}