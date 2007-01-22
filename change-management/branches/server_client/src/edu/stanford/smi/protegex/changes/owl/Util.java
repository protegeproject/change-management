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
import edu.stanford.smi.protegex.changes.listeners.ChangesClsListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesFrameListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesInstanceListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesKBListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesSlotListener;
import edu.stanford.smi.protegex.changes.listeners.ChangesTransListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesClassListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesModelListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesPropertyListener;
import edu.stanford.smi.protegex.changes.listeners.owl.OwlChangesFrameListener;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;

public class Util {

	private static HashMap frameIdMap = new HashMap();

	private static String OWL_KB_INDICATOR = "OWL";
	
	private Util() {}
	
	public static boolean kbInOwl(KnowledgeBase kb) {
		int index = (kb.getClass().getName().indexOf(OWL_KB_INDICATOR));
		return (index > 0);
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
