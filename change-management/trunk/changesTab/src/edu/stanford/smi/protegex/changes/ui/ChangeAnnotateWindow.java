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
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.changes.AnnotationTableModel;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangeTableModel;
import edu.stanford.smi.protegex.changes.ChangesTab;
import edu.stanford.smi.protegex.changes.action.AnnotationShowAction;
import edu.stanford.smi.protegex.server_changes.Model;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.util.InstanceDateComparator;

public class ChangeAnnotateWindow {

	public static final String CHANGE_ANNOTATE_TITLE = "Changes for: ";

	private JFrame cmFrame;
	
	private ChangeTableModel cTableModel;
	private AnnotationTableModel aTableModel;
	private JTable cTable;
	private JTable aTable;
	
	private KnowledgeBase cKb;
	private String clsName;
	private List<String> names = new ArrayList<String>();
	private boolean nameChangeOn;

	
	public ChangeAnnotateWindow(KnowledgeBase cKb, String clsName, boolean nameChangeOn) {
		this.cKb = cKb;
		this.clsName = clsName;
		this.nameChangeOn = nameChangeOn;
	}
	
	public void show() {
		generateCMWindow(clsName);
	}
	
	private void generateCMWindow(String clsName) {
		
		cmFrame = ComponentFactory.createFrame();
		ComponentUtilities.center(cmFrame);
		cmFrame.setSize(500,300);
		cmFrame.setTitle(CHANGE_ANNOTATE_TITLE + clsName);
		cmFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel cmPanel = new JPanel();
	
		cTableModel = new ChangeTableModel(cKb);
		aTableModel = new AnnotationTableModel(cKb);
		
		Collection changes = Model.getChangeInsts(cKb);
		List<Instance> relChanges = new ArrayList<Instance>();
		List<Instance> assocAnnotations = new ArrayList<Instance>();
		HashMap uniqueSet = new HashMap();

		// Construct name changed list
		names.clear();
		if (nameChangeOn) {
			getAllNames(ChangesTab.getNameChanges(), clsName);	
		} else {
			getAllNames(new HashMap<String, String>(), clsName);	
		}
		names.add(clsName);
		
		for (Iterator iter = changes.iterator(); iter.hasNext();) {
			Instance cInst = (Instance) iter.next();
			
			if (cInst.getDirectType().getName().equals(Model.CHANGETYPE_TRANS_CHANGE)) {
				Collection transChanges = Model.getTransChanges(cInst);
				boolean hasChangeRel = false;
				
				for (Iterator iterator = transChanges.iterator(); iterator
						.hasNext();) {
					Instance tcInst = (Instance) iterator.next();
					String tApplyToStr = Model.getApplyTo(tcInst);
					
					for (Iterator it2 = names.iterator(); it2
							.hasNext();) {
						String nameChange = (String) it2.next();
						if (tApplyToStr.equals(nameChange)) {
							hasChangeRel = true;
						}
					}
				}
				
				if (hasChangeRel) {
					relChanges.add(cInst);
					Collection assocList = Model.getAssocAnnotations(cInst);
					for (Iterator iterator = assocList.iterator(); iterator
							.hasNext();) {
						Instance elem = (Instance) iterator.next();
						if (!uniqueSet.containsKey(elem.getName())){
							assocAnnotations.add(elem);
							uniqueSet.put(elem.getName(), null);
						}
					}
				}
			
			} else {
				String info = Model.getType(cInst);
				
				if (!info.equals(Model.CHANGE_LEVEL_TRANS_INFO)){
					String applyToStr = Model.getApplyTo(cInst);
					for (Iterator iterator = names.iterator(); iterator
							.hasNext();) {
						String nameChange = (String) iterator.next();
						if (nameChange.equals(applyToStr)) {
							relChanges.add(cInst);
							Collection assocList = Model.getAssocAnnotations(cInst);
							for (Iterator it2 = assocList.iterator(); it2
									.hasNext();) {
								Instance elem = (Instance) it2.next();
								if (!uniqueSet.containsKey(elem.getName())){
									assocAnnotations.add(elem);
									uniqueSet.put(elem.getName(), null);
								}
							}
						}
					}
				}
			}
		}

		Collections.sort(relChanges, new InstanceDateComparator(cKb));
		for (Iterator iter = relChanges.iterator(); iter.hasNext();) {
			Instance aInst = (Instance) iter.next();
			cTableModel.addChangeData(aInst);
		}
		
		Collections.sort(assocAnnotations, new InstanceDateComparator(cKb));
		for (Iterator iter = assocAnnotations.iterator(); iter.hasNext();) {
			Instance aInst = (Instance) iter.next();
			aTableModel.addAnnotationData(aInst);
		}
		
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
			aTable.addMouseListener(new AnnotationShowAction(aTable, aTableModel, ChangesTab.getChangesProj()));
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
