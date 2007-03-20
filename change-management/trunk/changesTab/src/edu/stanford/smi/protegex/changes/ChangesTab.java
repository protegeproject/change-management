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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.DefaultKnowledgeBase;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.framestore.FrameStoreManager;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.server.RemoteProjectManager;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.AbstractTabWidget;
import edu.stanford.smi.protegex.changes.action.AnnotationShowAction;
import edu.stanford.smi.protegex.changes.action.ChangesSearchClear;
import edu.stanford.smi.protegex.changes.action.ChangesSearchExecute;
import edu.stanford.smi.protegex.changes.listeners.ChangesListener;
import edu.stanford.smi.protegex.changes.ui.ChangeMenu;
import edu.stanford.smi.protegex.changes.ui.ColoredTableCellRenderer;
import edu.stanford.smi.protegex.changes.ui.JTreeTable;
import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.GetAnnotationProjectName;
import edu.stanford.smi.protegex.server_changes.model.AnnotationCreationComparator;
import edu.stanford.smi.protegex.server_changes.model.ChangeDateComparator;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.AnnotationCls;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeSlot;
import edu.stanford.smi.protegex.server_changes.model.generated.AnnotatableThing;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Name_Changed;
import edu.stanford.smi.protegex.server_changes.model.generated.Subclass_Added;
 
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
	
	private static final String CHANGES_TAB_NAME = "Changes";

	private static Project currProj;
	private static Project changes_project;
    private static ChangeModel model;
	private static KnowledgeBase changes_kb;
	private static KnowledgeBase currKB;
    private static ChangeCreateUtil createUtil;
	
	private static JTable annotationsTable;
	private static JTable annotationChangesTable;
	
	private static JComboBox annotationTypes;
	private static ChangeTableModel annotationChangesTableModel;
	
	private static AnnotationTableModel annotationsTableModel;
	
	private static Annotation annotateInst;
	private static Instance instToEdit;
	private static String OWL_KB_INDICATOR = "OWL";
	

	private static ChangeMenu changesMenu;
	private static RemoveInstanceAction removeAnnotationAction;
	private static EditInstanceAction editAnnotationAction;
	private static AddInstanceAction addAnnotationAction;
	

	//JTreeTable
	
	private static JTreeTable cTreeTable;
	private static ChangeTreeTableModel cTreeTableModel;
	
	private static boolean inRemoveAnnotation = false;

	

	public static boolean getInRemoveAnnotation() {
		return inRemoveAnnotation;
	}
	
	public static void setInRemoveAnnotation(boolean val) {
		inRemoveAnnotation = val;
		
		
	}
	
	
	public static boolean kbInOwl(KnowledgeBase kb) {
		int index = (kb.getClass().getName().indexOf(OWL_KB_INDICATOR));
		return (index > 0);
	}
	
	public static KnowledgeBase getChangesKB() {
		return changes_kb;
	}
	
	public static Project getChangesProj() {
		return changes_project;
	}
	
	// Initialize the plugin
	public void initialize() {
	    setLabel(CHANGES_TAB_NAME);
	    
	    currProj = getProject();
	    currKB = currProj.getKnowledgeBase();

	    // GET INSTANCE OF CHANGE PROJECT 'changes' and corresponding KB 'cKB' HERE
	    getChangeProject();

	    // ASSUMING THAT THE CHANGES PROJECT EXISTS 
	    initTables();
	    loadExistingData();

	    changes_kb.addFrameListener(new ChangesListener(model));		
	   
	    // Initialize the UI
	    initUI();	    
	}

	
	private void initUI() {
		// Create menu item
		changesMenu = new ChangeMenu(getKnowledgeBase(), changes_kb);
		JMenuBar menuBar = getMainWindowMenuBar();
	    menuBar.add (changesMenu);

		annotationsTable.addMouseListener(new AnnotationShowAction(annotationsTable, annotationsTableModel, changes_project));
		JScrollPane scroll = ComponentFactory.createScrollPane(cTreeTable);

		JScrollPane scroll2 = ComponentFactory.createScrollPane(annotationsTable);
		JScrollPane scroll3 = ComponentFactory.createScrollPane(annotationChangesTable);
				
		JPanel interPane = new JPanel();
		interPane.setLayout(new BoxLayout(interPane, BoxLayout.PAGE_AXIS));
		interPane.add(initSearchPanel());
		
		interPane.add(scroll);
		LabeledComponent changeHistLC = new LabeledComponent(LABELCOMP_NAME_CHANGE_HIST, interPane,true);
		
		changeHistLC.doLayout();
		changeHistLC.addHeaderSeparator();
		addAnnotationAction = new AddInstanceAction(changeHistLC, ACTION_NAME_CREATE_ANNOTATE);
	    addAnnotationAction.setEnabled(false);

	    changeHistLC.setHeaderComponent(initAnnotPanel(), BorderLayout.EAST);
		changeHistLC.addHeaderButton(addAnnotationAction);
		
			
		LabeledComponent annotLC = new LabeledComponent(LABELCOMP_NAME_ANNOTATIONS, scroll2, true);
		annotLC.doLayout();
		annotLC.addHeaderSeparator();
		removeAnnotationAction = new RemoveInstanceAction(ACTION_NAME_REMOVE_ANNOTATE);
		editAnnotationAction = new EditInstanceAction(ACTION_NAME_EDIT_ANNOTATE);
		removeAnnotationAction.setEnabled(false);
		editAnnotationAction.setEnabled(false);
		annotLC.addHeaderButton(removeAnnotationAction);
		annotLC.addHeaderButton(editAnnotationAction);
	
		
		LabeledComponent assocLC = new LabeledComponent(LABELCOMP_NAME_ASSOC_CHANGES, scroll3, true);
		assocLC.doLayout();
		assocLC.addHeaderSeparator();
	
		
		JSplitPane splitPanel = ComponentFactory.createTopBottomSplitPane(false);
		splitPanel.setResizeWeight(0.5);
		splitPanel.setDividerLocation(0.5);
		splitPanel.setTopComponent(annotLC);
		splitPanel.setBottomComponent(assocLC);
		
		
		HeaderComponent changeView = new HeaderComponent(HEADERCOMP_NAME_CHANGE_VIEWER, null, changeHistLC);
		HeaderComponent annotView = new HeaderComponent(HEADERCOMP_NAME_ANNOTATE_VIEWER, null, splitPanel);
		
		JSplitPane splitPanelBig = ComponentFactory.createTopBottomSplitPane(false);
		splitPanelBig.setTopComponent(changeView);
		splitPanelBig.setBottomComponent(annotView);
		splitPanelBig.setResizeWeight(0.7);
		splitPanelBig.setDividerLocation(0.7);
		
		add(splitPanelBig);
		
		cTreeTable.getTree().expandPath(cTreeTableModel.getRootPath());
		
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
		
		annotationTypes = new JComboBox(annotFields);
		annotationTypes.setSelectedIndex(0);
	
		
		annotPanel.add(annotLabel);
		annotPanel.add(annotationTypes);
		return annotPanel;
	}
	
	private JPanel initSearchPanel() {
		JPanel searchPanel = ComponentFactory.createPanel();
		JLabel searchLabel = new JLabel(SEARCH_PANEL_TITLE);
		String[] searchFields = {	ChangeTableColumn.CHANGE_COLNAME_AUTHOR.getName(),
				                    ChangeTableColumn.CHANGE_COLNAME_ACTION.getName(), 
				                    ChangeTableColumn.CHANGE_COLNAME_DESCRIPTION.getName(),
									ChangeTableColumn.CHANGE_COLNAME_CREATED.getName()
									};
		
		JComboBox cbox = new JComboBox(searchFields);
		cbox.setSelectedIndex(0);
	
		JTextField searchText = new JTextField(25);
		JButton searchButton = new JButton(SEARCH_PANEL_BUTTON_GO);
	
		ActionListener searchExecute = new ChangesSearchExecute(cbox, searchText, cTreeTableModel);
		searchButton.addActionListener(searchExecute);
		
		JButton clearButton = new JButton(SEARCH_PANEL_BUTTON_CLEAR);
	
		ActionListener searchClear = new ChangesSearchClear(cTreeTableModel);
		clearButton.addActionListener(searchClear);
		
		searchPanel.add(searchLabel);
		searchPanel.add(cbox);
		searchPanel.add(searchText);
		searchPanel.add(searchButton);
		searchPanel.add(clearButton);
		
		searchPanel.setLayout(new FlowLayout());
	
		return searchPanel;
	}
	

	
	
	private void initTables() {
		// Create Tables
		
		annotationChangesTableModel = new ChangeTableModel(model);

		annotationsTableModel = new AnnotationTableModel(changes_kb);
		cTreeTableModel = new ChangeTreeTableModel(model);
		
		annotationChangesTable = new JTable(annotationChangesTableModel);
	
		annotationsTable = new JTable(annotationsTableModel);
		cTreeTable = new JTreeTable(cTreeTableModel);
	
		ComponentFactory.configureTable(annotationsTable);
		ComponentFactory.configureTable(annotationChangesTable);
	
		
		annotationsTable.setShowGrid(false);
		annotationsTable.setIntercellSpacing(new Dimension(0, 0));
		annotationsTable.setColumnSelectionAllowed(false);
		annotationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		annotationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		
		annotationChangesTable.setShowGrid(false);
		annotationChangesTable.setIntercellSpacing(new Dimension(0, 0));
		annotationChangesTable.setColumnSelectionAllowed(false);
		annotationChangesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		annotationChangesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		annotationChangesTable.setDefaultRenderer(Object.class, new ColoredTableCellRenderer());
		
		cTreeTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		cTreeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	
		
		
		
		ListSelectionModel lsm = annotationsTable.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()){
					return;
				}
				
				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if(!lsm.isSelectionEmpty()) {
					removeAnnotationAction.setEnabled(true);
					editAnnotationAction.setEnabled(true);
					int selectedRow = lsm.getMinSelectionIndex();
					String instName = annotationsTableModel.getInstanceName(selectedRow);
					Instance selectedInst = changes_kb.getInstance(instName);
					annotationChangesTableModel.setChanges(((Annotation) selectedInst).getAnnotates());
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
					addAnnotationAction.setEnabled(true);
			
				} 
			}
		});
		
		
		
	}
	

    private static void getChangeProject(){
        // NEED TO ADD IMPLEMENTATION FOR SERVER MODE
        // But this project must "essentially" be the same as the project that the project plugin is using
        // same events, contents etc.
        // it also runs after the changes project plugin has initialized.
        if (currProj.isMultiUserClient()) {
            getServerSideChangeProject();
        }
        else {
            if (ChangesProject.getChangesProj(currKB) == null) { // the tab has just been configured so the
                new ChangesProject().afterLoad(currProj);  // project plugin is not initialized                           
            }
            changes_project = ChangesProject.getChangesProj(currKB);
            changes_kb = changes_project.getKnowledgeBase();
            ChangesDb changes_db = ChangesProject.getChangesDb(currKB);
            model = changes_db.getModel();
            
            createUtil = new ChangeCreateUtil(model);
        }
    }
    
    private static void getServerSideChangeProject() {
        String annotationName = (String) new GetAnnotationProjectName(currKB).execute();
        if (annotationName == null) {
            Log.getLogger().warning("annotation project not configured (use " +
                                    GetAnnotationProjectName.METAPROJECT_ANNOTATION_PROJECT_SLOT +
                                    " slot)");
        }
        RemoteProjectManager manager = RemoteProjectManager.getInstance();
        FrameStoreManager fsmanager = ((DefaultKnowledgeBase) currKB).getFrameStoreManager();
        RemoteClientFrameStore rcfs = (RemoteClientFrameStore) fsmanager.getFrameStoreFromClass(RemoteClientFrameStore.class);
        changes_project = manager.connectToProject(rcfs.getRemoteServer(), rcfs.getSession(), annotationName);
        changes_kb = changes_project.getKnowledgeBase();
        
        model = new ChangeModel(changes_kb);
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
	
	
	private static void loadExistingData() {      
		Collection<Instance> annotateInsts = model.getInstances(AnnotationCls.Annotation);
		loadChanges();
		loadAnnotations(annotateInsts);
	}
	
	private static void loadChanges() {
		for (Change aInst : model.getSortedTopLevelChanges()) {
			cTreeTableModel.addChangeData(aInst);
		}
	}

	
	
	public static void createChange(Change aChange) {
		boolean addChange = true;
		
		cTreeTableModel.addChangeData(aChange);
		changesMenu.setEnabledLastChange(true);
		changesMenu.setChange(aChange);
	}
    
    public static void modifyChange(Change aChange, Slot slot, List oldValues) {
        cTreeTableModel.update(aChange, slot, oldValues);
    }
	
	
	private static void loadAnnotations(Collection<Instance> annotateInsts) {
		
		List<Instance> annotationList = new ArrayList<Instance>(annotateInsts);
		Collections.sort(annotationList, new AnnotationCreationComparator());
		
		for (Iterator iter = annotationList.iterator(); iter.hasNext();) {
			Instance aInst = (Instance) iter.next();
			annotationsTableModel.addAnnotationData((Annotation) aInst);
		}
		
	}


	

	public static void updateAnnotationTable() {
		annotationsTableModel.update();
	}
	
	public static void createAnnotation(Annotation annotateInst) {
        String body = ((Annotation) annotateInst).getBody();
		if (body == null) {
			changes_kb.deleteInstance(annotateInst);
		}
		else{
		    annotateInst = createUtil.updateAnnotation(annotateInst);
		    annotationsTableModel.addAnnotationData((Annotation) annotateInst);
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
				final Collection chngInstSelected = new ArrayList();
				
				for (int i = 0; i < selected.length; i++) {
					AnnotatableThing changeInst = (AnnotatableThing)cTreeTableModel.getObjInRow(selected[i]-1);
			
					chngInstSelected.add(changeInst);
				}
			    String annotType = (String)annotationTypes.getSelectedItem();
				annotateInst = createUtil.createAnnotation(annotType, chngInstSelected);
				JFrame edit = changes_project.show(annotateInst);
				
				edit.addWindowListener(new WindowListener() {
					
					public void windowClosed(WindowEvent arg0) {
						createAnnotation((Annotation) annotateInst);	
						
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
			int numSelect = annotationsTable.getSelectedRowCount();
			
			if (numSelect > 1) { 
				annotationsTableModel.removeAnnotationData(annotationsTable.getSelectedRows());
			} else if (numSelect == 1) {
				String delName = annotationsTableModel.getInstanceName(annotationsTable.getSelectedRow());
				Instance instToDel = changes_kb.getInstance(delName);
				changes_kb.deleteInstance(instToDel);
				annotationsTableModel.removeAnnotationData(annotationsTable.getSelectedRow());
			}
			
			annotationsTable.clearSelection();
			removeAnnotationAction.setEnabled(false);
			editAnnotationAction.setEnabled(false);
			
			
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
			int numSelect = annotationsTable.getSelectedRowCount();
			
			if (numSelect == 1) {
				String instEditName = annotationsTableModel.getInstanceName(annotationsTable.getSelectedRow());
				
				instToEdit = changes_kb.getInstance(instEditName);
				
				JFrame edit = changes_project.show(instToEdit);
				edit.addWindowListener(new WindowListener() {
					public void windowOpened(WindowEvent arg0) {
					}

					public void windowClosing(WindowEvent arg0) {
					}

					public void windowClosed(WindowEvent arg0) {
						annotationsTableModel.editAnnotationData(instToEdit, annotationsTable.getSelectedRow());
						removeAnnotationAction.setEnabled(false);
						editAnnotationAction.setEnabled(false);
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
	
	
}

