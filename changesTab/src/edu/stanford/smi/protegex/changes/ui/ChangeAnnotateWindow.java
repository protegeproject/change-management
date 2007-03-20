package edu.stanford.smi.protegex.changes.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.changes.AnnotationTableModel;
import edu.stanford.smi.protegex.changes.ChangeTableModel;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.changes.action.AnnotationShowAction;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeDateComparator;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Composite_Change;

public class ChangeAnnotateWindow {

	public static final String CHANGE_ANNOTATE_TITLE = "Changes for: ";

	private JFrame cmFrame;
	
	private ChangeTableModel cTableModel;
	private AnnotationTableModel aTableModel;
	private JTable cTable;
	private JTable aTable;
	
    private ChangeModel change_model;
	private KnowledgeBase change_kb;
	private String clsName;
	private List<String> names = new ArrayList<String>();
	private boolean nameChangeOn;

	
	public ChangeAnnotateWindow(ChangeModel change_model, String clsName, boolean nameChangeOn) {
        this.change_model = change_model;
		this.change_kb = change_model.getChangeKb();
		this.clsName = clsName;
		this.nameChangeOn = nameChangeOn;
	}
	
	public void show() {
		generateCMWindow(clsName);
	}
	
	private void generateCMWindow(String clsName) {
		
	    if (true) {
	        throw new UnsupportedOperationException("Not implemented yet");
        }
        JPanel cmPanel = new JPanel();
        Collection changes;
        List<Instance> relChanges = new ArrayList<Instance>();
        List<Instance> assocAnnotations = new ArrayList<Instance>();
        HashMap uniqueSet = new HashMap();
		
		cTable = new JTable(cTableModel);
		aTable = new JTable(aTableModel);
		
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
		
		if (nameChangeOn) {
			//cTable.addMouseListener(new ChangeShowAction(cTable, cTableModel, ChangesTab.getChangesProj()));
			aTable.addMouseListener(new AnnotationShowAction(aTable, aTableModel, change_kb.getProject()));
		}
		
		JScrollPane scroll = ComponentFactory.createScrollPane(cTable);
		JScrollPane scroll2 = ComponentFactory.createScrollPane(aTable);
		
		LabeledComponent changeHistLC = new LabeledComponent(ChangesTab.LABELCOMP_NAME_CHANGE_HIST, scroll,true);
		changeHistLC.doLayout();
		changeHistLC.addHeaderSeparator();
		
		LabeledComponent annotLC = new LabeledComponent(ChangesTab.LABELCOMP_NAME_ANNOTATIONS, scroll2, true);
		annotLC.doLayout();
		annotLC.addHeaderSeparator();
		annotLC.setMaximumSize(new Dimension(1000,1000));
		
		HeaderComponent changeView = new HeaderComponent(ChangesTab.HEADERCOMP_NAME_CHANGE_VIEWER, null, changeHistLC);
		HeaderComponent annotView = new HeaderComponent(ChangesTab.HEADERCOMP_NAME_ANNOTATE_VIEWER, null, annotLC);
		
		cmPanel.setLayout(new GridLayout(2,1));
		cmPanel.add(changeView);
		cmPanel.add(annotView);
	
		cmPanel.setVisible(true);
		cmFrame.add(cmPanel);
		cmFrame.setVisible(true);
		cmFrame.toFront();
		cmFrame.requestFocus();
	}
	
	private void getAllNames(Map<String, String> nameChanges, String name) {
		
		if (nameChanges.containsKey(name)) {
			names.add(nameChanges.get(name));
			getAllNames(nameChanges, (String) nameChanges.get(name));
		}
		return;
	}
}
