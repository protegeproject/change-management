package edu.stanford.smi.protegex.server_changes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Created_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Deleted_Change;
import edu.stanford.smi.protegex.server_changes.model.generated.Name_Changed;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;
import edu.stanford.smi.protegex.server_changes.model.listeners.AbstractChangeListener;

public class NameChangeManager {
    private ChangeModel model;
    private KnowledgeBase changes_kb;
    private Map<String, Ontology_Component> name_map = new HashMap<String, Ontology_Component>();
    
    public NameChangeManager(ChangeModel model) {
        this.model = model;
        changes_kb = model.getChangeKb();
        addNameChangeListener();
    }
  
    private void addNameChangeListener() {
        synchronized (changes_kb) {
            List<Instance> changes = new ArrayList<Instance>(model.getInstances(ChangeCls.Change));
            Collections.sort(changes, new ChangeDateComparator(changes_kb));
            for (Object o : changes) {
                Change change = (Change) o;
                handleNameChange(change);
            }
            changes_kb.addFrameListener(new NameChangeListener());
        }
    }
    
    private void handleNameChange(Change change) {
        synchronized (changes_kb) {
            if (change instanceof Created_Change) {
                Ontology_Component frame = (Ontology_Component) change.getApplyTo();
                String name = ((Created_Change) change).getCreationName();
                name_map.put(name, frame);
            }
            else if (change instanceof Deleted_Change) {
                String name = ((Deleted_Change) change).getDeletionName();
                name_map.remove(name);
            }
            else if (change instanceof Name_Changed) {
                Name_Changed name_change = (Name_Changed) change;
                String oldName = name_change.getOldName();
                String newName = name_change.getNewName();
                name_map.put(newName, name_map.remove(oldName));
            }
        }
    }
    
    public class NameChangeListener extends AbstractChangeListener {
        public NameChangeListener() {
            super(model);
        }

        public void addChange(Change change) {
            handleNameChange(change);
        }
        
    }
    
    public Ontology_Component getOntologyComponent(String name, boolean create) {
        synchronized (changes_kb) {
            Ontology_Component frame = name_map.get(name);
            if (frame == null && create) {
                frame = (Ontology_Component) model.createInstance(ChangeCls.Ontology_Component);
                frame.setCurrentName(name);
                name_map.put(name, frame);
            }
            return frame;
        }
    }

}
