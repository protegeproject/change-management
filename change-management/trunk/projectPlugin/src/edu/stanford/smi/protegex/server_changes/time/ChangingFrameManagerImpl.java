package edu.stanford.smi.protegex.server_changes.time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.event.ClsAdapter;
import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.Model;
import edu.stanford.smi.protegex.server_changes.util.InstanceDateComparator;

public class ChangingFrameManagerImpl implements ChangingFrameManager {
    private static Logger log = Log.getLogger(ChangingFrameManagerImpl.class);
    
    private KnowledgeBase changekb;

    private Cls classCreatedClass;
    private Cls propertyCreatedClass;
    private Cls slotCreatedClass;
    private Cls instanceCreatedClass;
    
    private Cls classDeletedClass;
    private Cls propertyDeletedClass;
    private Cls slotDeletedClass;
    private Cls instanceDeletedClass;
    
    private Cls nameChangeClass;
    
    private Map<Instance, ChangingFrameImpl> applyToMap;
    private Map<String, List<Instance>> changesByNameMap;
    private Map<String, ChangingFrameImpl> currentNameMap;
    
    public ChangingFrameManagerImpl(KnowledgeBase changekb) {
        this.changekb = changekb;
        loadChangeTypes();
        initialize();
        changekb.addClsListener(new ClsAdapter() {
            public void directInstanceAdded(ClsEvent event) {
                Instance change = event.getInstance();
                addChange(change);
            }
        });
    }
    
    private void loadChangeTypes() {
        Model model = new Model(changekb);
        
        classCreatedClass = model.getClassCreatedClass();
        propertyCreatedClass = model.getPropertyCreatedClass();
        slotCreatedClass = model.getSlotCreatedClass();
        instanceCreatedClass = model.getInstanceCreatedClass();
        
        classDeletedClass = model.getClassDeletedClass();
        propertyDeletedClass = model.getPropertyDeletedClass();
        slotDeletedClass = model.getSlotDeletedClass();
        instanceDeletedClass = model.getInstanceDeletedClass();
        
        nameChangeClass = model.getNameChangedClass();
    }
    
    @SuppressWarnings("unchecked")
    private void initialize() {
        applyToMap       = new HashMap<Instance, ChangingFrameImpl>();
        changesByNameMap = new HashMap<String, List<Instance>>();
        currentNameMap   = new HashMap<String, ChangingFrameImpl>();
        synchronized (changekb) {
            List kbchanges = new ArrayList(Model.getChangeInsts(changekb));
            Collections.sort(kbchanges, new InstanceDateComparator(changekb));
            for (Object o : kbchanges) {
                addChange((Instance) o);
            }
        }
    }
    
    public void addChange(Instance change) {
        synchronized (changekb) {
            Model.logChange("Adding change to changing frame manager", log, Level.FINE, change);

            Collection direct_types = change.getDirectTypes();
            String old_frame_name;
            if (!isNameChange(direct_types)) {
                old_frame_name = Model.getApplyTo(change);
            }
            else {
                old_frame_name = Model.getNameChangedOldName(change);
            }
            ChangingFrameImpl frame = currentNameMap.get(old_frame_name);
            if (frame == null) {
                frame = new ChangingFrameImpl(old_frame_name);
                currentNameMap.put(old_frame_name, frame);
            }

            frame.addChange(change, direct_types);

            applyToMap.put(change, frame);
            List<Instance> changesForName = changesByNameMap.get(old_frame_name);
            if (changesForName == null) {
                changesForName = new ArrayList<Instance>();
                changesByNameMap.put(old_frame_name, changesForName);
            }
            changesForName.add(change);

            if (isNameChange(direct_types)) {
                String new_name = Model.getNameChangedNewName(change);
                currentNameMap.remove(old_frame_name);
                currentNameMap.put(new_name, frame);
            }
            else if (isDeleteChange(direct_types)) {
                currentNameMap.remove(old_frame_name);
            }
        }
    }
    
    protected boolean isCreateChange(Collection direct_types) {
        return direct_types.contains(classCreatedClass) ||
               direct_types.contains(propertyCreatedClass) ||
               direct_types.contains(slotCreatedClass) ||
               direct_types.contains(instanceCreatedClass);
    }
    
    protected boolean isDeleteChange(Collection direct_types) {
        return direct_types.contains(classDeletedClass) || 
               direct_types.contains(propertyDeletedClass) || 
               direct_types.contains(slotDeletedClass) ||
               direct_types.contains(instanceDeletedClass);
    }
    
    protected boolean isNameChange(Collection direct_types) {
        return direct_types.contains(nameChangeClass);
    }
    
    /**
     * Find the last change in the sorted list of changes such that the change date is 
     * strictly less than d.  Should be log time.
     * 
     * @param d - a date
     * @param changes a list of changes that has been sorted by date.
     * @return
     */
    protected static int findPreviousChange(Date d, List<Instance> changes) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("Finding change just before " + d);
            if (changes.isEmpty()) {
                log.fine("no changes");
            }
            else {
                log.fine("First change date = " + Model.getCreated(changes.get(0)));
                log.fine("Last change date = " + Model.getCreated(changes.get(changes.size()-1)));
            }
        }
        if (changes.isEmpty()) return -1;
        Instance firstChange = changes.get(0);
        Date change_date = Model.parseDate(Model.getCreated(firstChange));
        int compare = d.compareTo(change_date);
        if (compare <= 0) {
            return -1;
        }
        else if (changes.size() == 1) {
            return 0;
        }
        Instance lastChange = changes.get(changes.size() - 1);
        change_date = Model.parseDate(Model.getCreated(lastChange));
        compare = d.compareTo(change_date);
        if (compare > 0) {
            return changes.size();
        }
        return findPreviousChange(d, changes, 0, changes.size() - 1);
    }
    
    /*
     * Just like the other findPreviousChange but in this one the date for changes[start]
     * is strictly less than d which is less than or equal to the date for changes[end].
     */
    private static int findPreviousChange(Date d, List<Instance> changes, int start, int end) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("\tLooking between index " + start + " and " + end);
            log.fine("\tStart date = " + Model.getCreated(changes.get(start)));
            log.fine("\tEnd date = " + Model.getCreated(changes.get(end)));
        }
        if (end == start + 1) {
            return start;
        }
        int middle = (start + end) / 2;
        Instance middle_change = changes.get(middle);
        Date change_date = Model.parseDate(Model.getCreated(middle_change));
        int compare = d.compareTo(change_date);
        if (compare <= 0) {
            return findPreviousChange(d, changes, start, middle);
        }
        else {
            return findPreviousChange(d, changes, middle, end);
        }
    }
    

    /* ----------------------- Interfaces ----------------------- */
    public ChangingFrame getChangingFrame(Instance change) {
        synchronized (changekb) {
            return applyToMap.get(change);
        }
    }

    public ChangingFrame getChangingFrameByInitialName(String name) {
        synchronized (changekb) {
            List<Instance> changesForName = changesByNameMap.get(name);
            if (changesForName ==null || changesForName.isEmpty()) {
                return currentNameMap.get(name);
            }
            Instance change = changesForName.get(0);
            Collection direct_types = change.getDirectTypes();
            if (isCreateChange(direct_types)) {
                return null;
            }
            return applyToMap.get(change);
        }
    }

    public ChangingFrame getChangingFrameByLatestName(String name) {
        synchronized (changekb) {
            return currentNameMap.get(name);
        }
    }

    public ChangingFrame getApplyTo(Instance annotateableThing) {
        Date d = Model.parseDate(Model.getCreated(annotateableThing));
        String applyTo = Model.getApplyTo(annotateableThing);
        List<Instance> changesForName = changesByNameMap.get(applyTo);
        if (changesForName == null || changesForName.isEmpty()) {
            ChangingFrameImpl frame = new ChangingFrameImpl(applyTo);
            currentNameMap.put(applyTo, frame);
            return frame;
        }
        int index = findPreviousChange(d, changesForName);
        if (index < 0) {
            return applyToMap.get(changesForName.get(0));
        }
        else return applyToMap.get(changesForName.get(index));
    }
    
    
    /* ------------------------------------------------------------------------------- */

    public class ChangingFrameImpl implements ChangingFrame {
        
        List<Instance> frame_changes = new ArrayList<Instance>();
        List<Instance> composite_frame_changes = new ArrayList<Instance>();
        List<Instance> name_changes = new ArrayList<Instance>();
        List<String> names = new ArrayList<String>();
        String initial_name;
        String final_name;

        protected ChangingFrameImpl(String name) {
            initial_name = name;
            final_name   = name;
            names.add(name);
        }
        
        protected void addChange(Instance change, Collection direct_types) {
            frame_changes.add(change);
            composite_frame_changes.add(change);
            
            Collection subChanges = Model.getChanges(change);
            if (subChanges != null && !subChanges.isEmpty()) {
                composite_frame_changes.removeAll(subChanges);
            }
            if (isCreateChange(direct_types)) {
                initial_name = null;
                name_changes.add(change);
            }
            else if (isDeleteChange(direct_types)) {
                final_name = null;
                name_changes.add(change);
            }
            else if (isNameChange(direct_types)) {
                final_name = Model.getNameChangedNewName(change);
                names.add(final_name);
                name_changes.add(change);
            }
        }

        /* ----------------------- Interfaces ----------------------- */
        public List<Instance> getChanges() {
            return frame_changes;
        }

        public List<Instance> getCompositeChanges() {
            return composite_frame_changes;
        }

        public String getFinalName() {
            return final_name;
        }

        public String getInitialName() {
            return initial_name;
        }

        public String getNameJustBefore(Date date) {
            int index = findPreviousChange(date, name_changes);
            if (index < 0) {
                return initial_name;
            }
            else {
                Instance last_change = name_changes.get(index);
                Collection direct_types = last_change.getDirectTypes();
                if (isCreateChange(direct_types)) {
                    return Model.getApplyTo(last_change);
                }
                else if (isDeleteChange(direct_types)) {
                    return null;
                }
                else if (isNameChange(direct_types)) {
                    return Model.getNameChangedNewName(last_change);
                }
            }
            throw new RuntimeException("Programmer error");
        }

        public List<String> getNames() {
            return names;
        }
    }
}
