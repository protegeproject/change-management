package edu.stanford.smi.protegex.changes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.ModalDialog;
import edu.stanford.smi.protege.util.SelectableContainer;
import edu.stanford.smi.protege.util.SelectableTable;
import edu.stanford.smi.protege.util.ViewAction;
import edu.stanford.smi.protegex.changes.AnnotationTableModel;
import edu.stanford.smi.protegex.changes.ChangeTableModel;
import edu.stanford.smi.protegex.changes.ChangeTreeTableModel;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.changes.action.AnnotationShowAction;
import edu.stanford.smi.protegex.server_changes.ChangesProject;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeSlot;
import edu.stanford.smi.protegex.server_changes.model.generated.Annotation;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Property;

public class ChangeAnnotateWindow extends SelectableContainer {

	public static final String CHANGE_ANNOTATE_TITLE = "Changes for: ";
	
	private ChangeTableModel cTableModel;
	private AnnotationTableModel aTableModel;
	private SelectableTable cTable;
	private SelectableTable aTable;
	
    private ChangeModel change_model;
	private KnowledgeBase change_kb;
	private String frameName;
	private List<String> names = new ArrayList<String>();
	private boolean nameChangeOn;

	
	public ChangeAnnotateWindow(ChangeModel change_model, String frameName, boolean nameChangeOn) {
        this.change_model = change_model;
		this.change_kb = change_model.getChangeKb();
		this.frameName = frameName;
		this.nameChangeOn = nameChangeOn;
	}
	
	public void show() {
		buildGUI(frameName);
		
		ComponentFactory.showInFrame(this, "Change annotations");
	}
	
	private void buildGUI(String clsName) {
		
		aTableModel = new AnnotationTableModel(change_kb);
		cTableModel = new ChangeTableModel(change_model);
		
		cTableModel.setChanges(getChanges());
 		
		cTable = new SelectableTable();
		cTable.setModel(cTableModel);
		
		aTable = new SelectableTable();
		aTable.setModel(aTableModel);
		
		ComponentFactory.configureTable(aTable);
		ComponentFactory.configureTable(cTable);
		
		aTable.setShowGrid(false);
		aTable.setIntercellSpacing(new Dimension(0, 0));
		aTable.setColumnSelectionAllowed(false);
		aTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		aTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		cTable.setShowGrid(false);
		cTable.setIntercellSpacing(new Dimension(0, 0));
		cTable.setColumnSelectionAllowed(false);
		cTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		cTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		cTable.setDefaultRenderer(Object.class, new ColoredTableCellRenderer());
		
	/*	if (nameChangeOn) {
			//cTable.addMouseListener(new ChangeShowAction(cTable, cTableModel, ChangesTab.getChangesProj()));
			aTable.addMouseListener(new AnnotationShowAction(aTable, aTableModel, change_kb.getProject()));
		}
		*/
		JScrollPane scroll = ComponentFactory.createScrollPane(cTable);
		JScrollPane scroll2 = ComponentFactory.createScrollPane(aTable);
		
		LabeledComponent changeHistLC = new LabeledComponent(ChangesTab.LABELCOMP_NAME_CHANGE_HIST, scroll,true);
		changeHistLC.doLayout();
		changeHistLC.addHeaderSeparator();
		
		LabeledComponent annotLC = new LabeledComponent(ChangesTab.LABELCOMP_NAME_ANNOTATIONS, scroll2, true);
		annotLC.doLayout();
		annotLC.addHeaderSeparator();
		annotLC.setMaximumSize(new Dimension(1000,1000));
		
		annotLC.addHeaderButton(new ViewAction("View Annotation", aTable) {
			@Override			
			public void onView() {
				int[] selRows = aTable.getSelectedRows(); 
				for (int i = 0; i < selRows.length; i++) {
					Instance instance = (Instance) aTableModel.getObjInRow(selRows[i]);
					change_kb.getProject().show(instance);
				}				
			}
		});

		changeHistLC.addHeaderButton(new ViewAction("View Change", cTable) {
			@Override
			public void onView() {
				int[] selRows = cTable.getSelectedRows(); 
				for (int i = 0; i < selRows.length; i++) {
					Instance instance = (Instance) cTableModel.getObjInRow(selRows[i]);
					change_kb.getProject().show(instance);
				}				
			}
		});
		
		ListSelectionModel lsm = cTable.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()){
					return;
				}

				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if(!lsm.isSelectionEmpty()) {
					int selectedRow = lsm.getMinSelectionIndex();		
					
					Change change = (Change) cTableModel.getObjInRow(selectedRow);
										
					aTableModel.setAnnotations(change.getAssociatedAnnotations());
				} 
			}
		});
		
		
		JSplitPane splitPanel = ComponentFactory.createTopBottomSplitPane(true);
		splitPanel.setResizeWeight(0.5);
		splitPanel.setDividerLocation(0.5);
		splitPanel.setTopComponent(changeHistLC);
		splitPanel.setBottomComponent(annotLC);
		
		add(splitPanel);
	}

	private Collection<Instance> getChanges() {
		//TT: The selected frame cannot be deleted (theoretically!) because the user has selected it in the GUI
		//Anyway, this method does not return anything if the frame was deleted. This should be fixed later.		
		
		ArrayList<Instance> changes = new ArrayList<Instance>();
		
		Slot currentNameSlot = change_model.getSlot(ChangeSlot.currentName);
		
		Collection frames = change_kb.getMatchingFrames(currentNameSlot, null, false, frameName, 10); 	
		
		for (Iterator iter = frames.iterator(); iter.hasNext();) {
			Frame frame = (Frame) iter.next();
			
			if (frame instanceof Ontology_Component) {
				changes.addAll(((Ontology_Component)frame).getChanges());
				break;
			}			
		}
		
		return changes;
	}


}
