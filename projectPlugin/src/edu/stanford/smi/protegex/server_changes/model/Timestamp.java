package edu.stanford.smi.protegex.server_changes.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;

public class Timestamp implements Comparable<Timestamp> {
    public static final String ROOT_TIME_NAME = "Date and time the change was made";
    
    public static final String DATE_SLOT = "date";
    public static final String SEQUENCE_SLOT = "sequence";
    
    public static final String TIMESTAMP_CLASS = "Timestamp";
    
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss zzz");
    
    private static int sequence = 0;
    private Date time;
    private int counter;
    private boolean root = false;
    
    private Timestamp() { }
    
    public static void initialize(KnowledgeBase changes_kb) {
        ;
    }
    
    public static synchronized Timestamp getTimestamp() {
        Timestamp t = new Timestamp();
        t.time = new Date();
        t.counter = sequence++;
        return t;
    }
    
    public static Timestamp getTimestamp(Instance change) {
        Timestamp time = new Timestamp();
        
        KnowledgeBase changeskb = change.getKnowledgeBase();
        
        Slot type_slot = changeskb.getSlot(Model.SLOT_NAME_TYPE);
        Slot created_slot = changeskb.getSlot(Model.SLOT_NAME_CREATED);
        Slot date_slot = changeskb.getSlot(DATE_SLOT);
        Slot sequence_slot = changeskb.getSlot(SEQUENCE_SLOT);
        
        if (Model.CHANGE_LEVEL_ROOT.equals(change.getOwnSlotValue(type_slot))) {
            time.root = true;
            return time;
        }
        
        Instance t = (Instance) change.getOwnSlotValue(created_slot);
        String date_string = (String) t.getOwnSlotValue(date_slot);
        try {
            time.time = DATE_FORMAT.parse(date_string);
        }
        catch (ParseException pe) {
            Log.getLogger().log(Level.WARNING, "Exception parsing string from change ontology",pe);
            return null;
        }
        time.counter = (Integer) t.getOwnSlotValue(sequence_slot);
        return time;
    }
    
    public void setTimestamp(Instance change) {
        KnowledgeBase changeskb = change.getKnowledgeBase();
        Slot created_slot = changeskb.getSlot(Model.SLOT_NAME_CREATED);
        setTimestamp(change, created_slot);
    }
    
    public void setTimestamp(Instance change, Slot time_slot) {
        KnowledgeBase changeskb = change.getKnowledgeBase();
        
        Cls timestamp_class = changeskb.getCls(TIMESTAMP_CLASS);

        Slot date_slot = changeskb.getSlot(DATE_SLOT);
        Slot sequence_slot = changeskb.getSlot(SEQUENCE_SLOT);
        
        Instance time_instance = changeskb.createInstance(null, timestamp_class);
        change.setDirectOwnSlotValue(time_slot, time_instance);
        time_instance.setDirectOwnSlotValue(date_slot, getDateString());
        time_instance.setDirectOwnSlotValue(sequence_slot, new Integer(counter));
    }
    
    
    public long getTimeMillis() {
        return time.getTime();
    }
    
    public Date getDate() {
        return time;
    }
    
    public String getDateString() {
        if (root) {
            return ROOT_TIME_NAME;
        }
        else {
            return DATE_FORMAT.format(time);
        }
    }
    
    public String toString() {
        return getDateString();
    }
    

    public int compareTo(Timestamp other) {
        if (root && other.root) {
            return 0;
        }
        else if (root) {
            return -1;
        }
        else if (other.root) {
            return +1;
        }
        else {
            int date_compare = time.compareTo(other.time);
            if (date_compare > 0) {
                return 1;
            }
            else if (date_compare < 0) { 
                return -1;
            }
            else if (counter > other.counter) {
                return 1;
            }
            else if (counter < other.counter) {
                return -1;
            }
            else return 0;
        }
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof Timestamp)) {
            return false;
        }
        Timestamp other = (Timestamp) o;
        if (root && other.root) {
            return true;
        }
        else if (root || other.root) {
            return false;
        }
        return time.equals(other.time) && counter == other.counter;
    }
    

}
