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

package edu.stanford.smi.protegex.changes.owl;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.stanford.smi.protege.model.CommandManager;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.framestore.undo.MacroCommand;
import edu.stanford.smi.protege.model.framestore.undo.UndoFrameStore;
import edu.stanford.smi.protegex.changes.listeners.owl.ChangesOwlKBListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesTransListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesClassListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesModelListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesPropertyListener;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

public class Util {

	private static HashMap frameIdMap = new HashMap();

	private static String OWL_KB_INDICATOR = "OWL";
	
	private Util() {}
	
	public static boolean kbInOwl(KnowledgeBase kb) {
		int index = (kb.getClass().getName().indexOf(OWL_KB_INDICATOR));
		return (index > 0);
	}
	
	public static void registerOwlListeners(KnowledgeBase kb) {
		((AbstractOWLModel) kb).addClassListener(new OwlChangesClassListener());
		((AbstractOWLModel) kb).addModelListener(new OwlChangesModelListener());
		((AbstractOWLModel) kb).addPropertyListener(new OwlChangesPropertyListener());
		kb.addTransactionListener(new ChangesTransListener());
		kb.addKnowledgeBaseListener(new ChangesOwlKBListener()); // Handles Class Deletes
	}
	
	public static void updateMap(String frameId, String name) {
		frameIdMap.put(frameId, name);
	}
	
	public static String getName (String frameId) {
		return (String)frameIdMap.get(frameId);
	}
	
	public static boolean frameExists(String frameId) {
		return frameIdMap.containsKey(frameId);
	}

	// Undo framestore possible use
	private static void ufsReg(KnowledgeBase currKB) {

		currKB.getCommandManager();
		CommandManager cm = currKB.getCommandManager();
		cm.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {
				UndoFrameStore ufs = (UndoFrameStore) arg0.getSource();
				ArrayList doneCmds = new ArrayList(ufs.getDoneCommands());
				Object element = doneCmds.get(doneCmds.size()-1);
				
				if (element instanceof MacroCommand) {
					MacroCommand mc = (MacroCommand) element;
					//lastMacroCmd = mc.getDescription();
				}
			}
			
		});
			 
	}
}
