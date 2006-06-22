package edu.stanford.smi.protegex.changes.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.widget.TabWidget;
import edu.stanford.smi.protegex.changes.ChangeCreateUtil;
import edu.stanford.smi.protegex.changes.ChangesTab;

public class ChangeMenu extends JMenu {

	public static final String MENU_TITLE = "Change";
	public static final String MENU_ITEM_ANNOTATE_LAST = "Annotate Last Change";
	public static final String MENU_ITEM_CHANGE_INFO = "Selected Change Info";
	
	protected AnnotateLastChange lastChange = new AnnotateLastChange();
	protected SelectedChangeInfo selChangeInfo = new SelectedChangeInfo();
	protected Instance lastInst;
	
	private KnowledgeBase cKb;
	private Project changeProj;
	private Instance annotateInst;
	
	public ChangeMenu(KnowledgeBase cKb, Project changeProj) {
		super(MENU_TITLE);
		setMnemonic(KeyEvent.VK_C);
		
		this.cKb = cKb;
		this.changeProj = changeProj;
		
		JMenuItem annotate = new JMenuItem(MENU_ITEM_ANNOTATE_LAST);
		JMenuItem changeInfo = new JMenuItem(MENU_ITEM_CHANGE_INFO);
		
		annotate.setAction(lastChange);
		annotate.setMnemonic(KeyEvent.VK_A);
		annotate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
		
		changeInfo.setAction(selChangeInfo);
		changeInfo.setMnemonic(KeyEvent.VK_K);
		changeInfo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,ActionEvent.CTRL_MASK));
		
		add(annotate);
		add(changeInfo);
		
		lastChange.setEnabled(false);
		selChangeInfo.setEnabled(true);	
	}
	
	public class AnnotateLastChange extends AbstractAction {
		
		public AnnotateLastChange() {
			super(MENU_ITEM_ANNOTATE_LAST);
		}
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			Collection changeInsts = new ArrayList();
			changeInsts.add(lastInst);
			annotateInst = ChangeCreateUtil.createAnnotation(cKb, changeInsts);
			
			JFrame aEdit = changeProj.show(annotateInst);
			aEdit.addWindowListener(new WindowListener() {
				
				public void windowClosed(WindowEvent arg0) {
					ChangesTab.createAnnotation(annotateInst);	
					setEnabledLastChange(false);
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
			aEdit.setVisible(true);
		}
	}
	
	public class SelectedChangeInfo extends AbstractAction {

		public SelectedChangeInfo() {
			super(MENU_ITEM_CHANGE_INFO);
		}
		
		public void actionPerformed(ActionEvent arg0) {
			ProjectView pvw = ProjectManager.getProjectManager().getCurrentProjectView();
			TabWidget tWidget = pvw.getSelectedTab();
			Collection selection = tWidget.getSelection();
			
			ArrayList selArray = new ArrayList(selection);
			Object elem = selArray.get(selArray.size()-1);
			String className = null;
			if (elem instanceof Cls) {
				className = ((Cls)elem).getName();
				ChangeAnnotateWindow cmWindow = new ChangeAnnotateWindow(cKb, className, true);
				cmWindow.show();
			}
		}
	}
	
	public void setEnabledLastChange(boolean val) {
		lastChange.setEnabled(val);
	}
	
	public void setChange(Instance lastInst) {
		this.lastInst = lastInst;
	}
}
