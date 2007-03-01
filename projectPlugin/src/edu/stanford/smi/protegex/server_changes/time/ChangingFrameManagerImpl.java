package edu.stanford.smi.protegex.server_changes.time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.event.ClsAdapter;
import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.ServerChangesUtil;
import edu.stanford.smi.protegex.server_changes.model.Model;
import edu.stanford.smi.protegex.server_changes.model.Timestamp;
import edu.stanford.smi.protegex.server_changes.model.InstanceDateComparator;

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
    
    private Map<String, ChangingFrameImpl> nameToFrameMap;
    private Map<Instance, ChangingFrameImpl> changeToChangedFrameMap;
    private Map<String, List<Instance>> applyToToChangeMap;
    private Map<String, Set<ChangingFrame>> userToChangedFrameMap;
    
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
        nameToFrameMap = new HashMap<String, ChangingFrameImpl>();
        changeToChangedFrameMap = new HashMap<Instance, ChangingFrameImpl>();
        applyToToChangeMap = new HashMap<String, List<Instance>>();
        userToChangedFrameMap = new HashMap<String, Set<ChangingFrame>>();
        
        synchronized (changekb) {
            List kbchanges = new ArrayList(Model.getChangeInsts(changekb));
            ServerChangesUtil.removeRoots(kbchanges);
            Collections.sort(kbchanges, new InstanceDateComparator(changekb));
            for (Object o : kbchanges) {
                addChange((Instance) o);
            }
        }
    }
    
    public void addChange(Instance change) {
        if (Model.getType(change).equals(Model.CHANGE_LEVEL_ROOT)) return;
        synchronized (changekb) {
            Model.logChange("Adding change to changing frame manager", log, Level.FINE, change);
            Collection direct_types = change.getDirectTypes();
            
            ChangingFrameImpl frame = updateNameToFrameMap(change, direct_types);

            frame.addChange(change, direct_types);
            
            updateChangeToChangedFrameMap(change, frame);
            updateApplyToToChangeMap(change);
            updateUserToChangedFrameMap(change, frame);
        }
    }
    
    private ChangingFrameImpl updateNameToFrameMap(Instance change, 
                                                   Collection direct_types) {
        ChangingFrameImpl frame = null;
        if (isNameChange(direct_types)) {
            String old_name = Model.getNameChangedOldName(change);
            String new_name = Model.getNameChangedNewName(change);
            frame = nameToFrameMap.get(old_name);
            if (frame == null) {
                frame = new ChangingFrameImpl(old_name);  // names will be fixed later
                nameToFrameMap.put(new_name, frame);
            }
            else {
                nameToFrameMap.remove(old_name);
                nameToFrameMap.put(new_name, frame);
            }
        }
        else if (isCreateChange(direct_types)) {
            String name = Model.getApplyTo(change);
            frame = new ChangingFrameImpl(name);
            nameToFrameMap.put(name, frame);
        }
        else if (isDeleteChange(direct_types)) {
            String frame_name = Model.getApplyTo(change);
            frame = nameToFrameMap.get(frame_name);
            if (frame == null) {
                frame = new ChangingFrameImpl(frame_name);
            }
            else {
                nameToFrameMap.remove(frame_name);
            }
        }
        else {
            String name = Model.getApplyTo(change);
            frame = nameToFrameMap.get(name);
            if (frame == null) {
                frame = new ChangingFrameImpl(name);
                nameToFrameMap.put(name, frame);
            }
        }
        return frame;
    }
    
    private void updateChangeToChangedFrameMap(Instance change, ChangingFrameImpl frame) {
        changeToChangedFrameMap.put(change, frame);
    }
    
    private void updateApplyToToChangeMap(Instance change) {
        String change_name = Model.getApplyTo(change);
        List<Instance> changesForName = applyToToChangeMap.get(change_name);
        if (changesForName == null) {
            changesForName = new ArrayList<Instance>();
            applyToToChangeMap.put(change_name, changesForName);
        }
        changesForName.add(change);
    }
    
    private void updateUserToChangedFrameMap(Instance change, ChangingFrameImpl frame) {
        String user = Model.getAuthor(change);
        Set<ChangingFrame> frames = userToChangedFrameMap.get(user);
        if (frames == null) {
            frames = new HashSet<ChangingFrame>();
            userToChangedFrameMap.put(user, frames);
        }
        frames.add(frame);
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
    protected static int findPreviousChange(Timestamp d, List<Instance> changes) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("--------------------------------------Starting findPreviousChange");
            log.fine("Finding change just before " + d);
            if (changes.isEmpty()) {
                log.fine("no changes");
            }
            else {
                log.fine("First change date = " + Timestamp.getTimestamp(changes.get(0)));
                log.fine("Last change date = " + Timestamp.getTimestamp(changes.get(changes.size()-1)));
            }
        }
        if (changes.isEmpty()) return -1;
        Instance firstChange = changes.get(0);
        Timestamp change_date = Timestamp.getTimestamp(firstChange);
        int compare = d.compareTo(change_date);
        if (compare <= 0) {
            return -1;
        }
        else if (changes.size() == 1) {
            return 0;
        }
        Instance lastChange = changes.get(changes.size() - 1);
        change_date = Timestamp.getTimestamp(lastChange);
        compare = d.compareTo(change_date);
        if (compare > 0) {
            return changes.size() - 1;
        }
        return findPreviousChange(d, changes, 0, changes.size() - 1);
    }
    
    /*
     * Just like the other findPreviousChange but in this one the date for changes[start]
     * is strictly less than d which is less than or equal to the date for changes[end].
     */
    private static int findPreviousChange(Timestamp d, List<Instance> changes, int start, int end) {
        if (log.isLoggable(Level.FINE)) {
            log.fine("\tLooking between index " + start + " and " + end);
            log.fine("\tStart date = " + Timestamp.getTimestamp(changes.get(start)));
            log.fine("\tEnd date = " + Timestamp.getTimestamp(changes.get(end)));
        }
        if (end == start + 1) {
            return start;
        }
        int middle = (start + end) / 2;
        Instance middle_change = changes.get(middle);
        Timestamp change_date = Timestamp.getTimestamp(middle_change);
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
            return changeToChangedFrameMap.get(change);
        }
    }

    public ChangingFrame getChangingFrameByInitialName(String name) {
        synchronized (changekb) {
            List<Instance> changesForName = applyToToChangeMap.get(name);
            if (changesForName ==null || changesForName.isEmpty()) {
                return nameToFrameMap.get(name);
            }
            Instance change = changesForName.get(0);
            Collection direct_types = change.getDirectTypes();
            if (isCreateChange(direct_types)) {
                return null;
            }
            return changeToChangedFrameMap.get(change);
        }
    }

    public ChangingFrame getChangingFrameByLatestName(String name) {
        synchronized (changekb) {
            return nameToFrameMap.get(name);
        }
    }

    public ChangingFrame getApplyTo(Instance annotateableThing) {
        Timestamp d = Timestamp.getTimestamp(annotateableThing);
        String applyTo = Model.getApplyTo(annotateableThing);
        List<Instance> changesForName = applyToToChangeMap.get(applyTo);
        if (changesForName == null || changesForName.isEmpty()) {
            ChangingFrameImpl frame = new ChangingFrameImpl(applyTo);
            nameToFrameMap.put(applyTo, frame);
            return frame;
        }
        int index = findPreviousChange(d, changesForName);
        if (index < 0) {
            return changeToChangedFrameMap.get(changesForName.get(0));
        }
        else return changeToChangedFrameMap.get(changesForName.get(index));
    }
    
    @SuppressWarnings("unchecked")
    public Collection<ChangingFrame> getModifiedFrames() {
        return (Collection) changeToChangedFrameMap.values();
    }
    
    public Collection<String> getUsers() {
        return userToChangedFrameMap.keySet();
    }
    
    public Set<ChangingFrame> getFramesTouchedByUser(String user) {
        return userToChangedFrameMap.get(user);
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
            updateChanges(change);
            updateCompositeChanges(change);
            updateNames(change, direct_types);
        }
        
        private void updateChanges(Instance change) {
            frame_changes.add(change);
        }
        
        private void updateCompositeChanges(Instance change) {
            composite_frame_changes.add(change);
            Collection subChanges = Model.getChanges(change);
            if (subChanges != null && !subChanges.isEmpty()) {
                composite_frame_changes.removeAll(subChanges);
            }
        }
        
        private void updateNames(Instance change, Collection direct_types) {
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
        
        public ChangingFrameManager getFrameManager() {
            return ChangingFrameManagerImpl.this;
        }
        
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
        
        public String getNameJustBefore(Instance change) {
            return getNameJustBefore(Timestamp.getTimestamp(change));
        }

        public String getNameJustBefore(Timestamp date) {
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
        
        public String toString() {
            if (initial_name == null && final_name == null) {
                Instance last_change = name_changes.get(name_changes.size() -1);
                String last_name = Model.getApplyTo(last_change);
                return "Created & Deleted - last known name = " + last_name;
            }
            else if (initial_name == null) {
                return "Created <" + final_name + ">";
            }
            else if (final_name == null) {
                return "Deleted <" + initial_name + ">";
            }
            else {
                return final_name + (initial_name.equals(final_name) ? "" : " (renamed)");
            }
        }
    }
}
