/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2005.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.changes.action;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JTable;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protegex.changes.ChangeTableModel;

public class ChangeShowAction implements MouseListener{

	JTable cTable;
	ChangeTableModel cTableModel;
	Project changes;
	
	public ChangeShowAction(JTable cTable, ChangeTableModel cTableModel, Project changes) {
		this.cTable = cTable;
		this.cTableModel = cTableModel;
		this.changes = changes;
	}
	
	// Show change instance on double click in change table
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount()==2) {
			int index = cTable.getSelectedRow();
			Instance changeInst = (Instance)cTableModel.getObjInRow(index);
			JFrame changeAction = changes.show(changeInst);
			changeAction.addWindowListener(new WindowListener() {
				
				public void windowClosed(WindowEvent arg0) {
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
			changeAction.setVisible(true);
		}
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

}
