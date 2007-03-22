
// Created on Mon Mar 19 14:22:16 PDT 2007
// "Copyright Stanford University 2006"

package edu.stanford.smi.protegex.server_changes.model.generated;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.model.ChangeDateComparator;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel;
import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeCls;


/** 
 */
public class Timestamp extends DefaultSimpleInstance {

	public Timestamp() {
	}


	public Timestamp(KnowledgeBase kb, FrameID id ) {
		super(kb, id);
	}


	public void setDate(String date) {
		ModelUtilities.setOwnSlotValue(this, "date", date);	}
	public String getDate() {
		return ((String) ModelUtilities.getOwnSlotValue(this, "date"));
	}

	public void setSequence(int sequence) {
		ModelUtilities.setOwnSlotValue(this, "sequence", new  Integer(sequence));	}
	public int getSequence() {
		return ((Integer) ModelUtilities.getOwnSlotValue(this, "sequence")).intValue();
	}
// __Code above is automatically generated. Do not change

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss zzz");
	private static int sequence = 0;
	private Date time;

	public static void initialize(ChangeModel model) {
	    ;
	}

	public static Timestamp getTimestamp(ChangeModel model) {
	    int counter;
	    Date date;
	    synchronized (Timestamp.class) {
	        date = new Date();
	        counter = sequence++;
	    }
	    Timestamp i = (Timestamp) model.createInstance(ChangeCls.Timestamp);
	    i.setDate(DATE_FORMAT.format(date));
	    i.setSequence(counter);
        return i;
	}



	public long getTimeMillis() {
	    return time.getTime();
	}

	public Date getDateParsed() {
	    if (time == null) {
	        String date = getDate();
	        try {
	            time = DATE_FORMAT.parse(date);
	        } catch (ParseException e) {
	            Log.getLogger().severe("Exception caught parsing the changes ontology - it is probably corrupted" + e);
	        }
	    }
	    return time;
	}


    /*
     * Frames are comparable so it is better not to replicate/overwrite that interface.
     */
	public int compareTimestamp(Timestamp other) {
		
		if (other == null) {
			return 1;
		}
		
	    Date myDate = getDateParsed();
	    Date otherDate = other.getDateParsed();

	    int date_compare = myDate.compareTo(otherDate);
	    if (date_compare > 0) {
	        return 1;
	    }
	    else if (date_compare < 0) { 
	        return -1;
	    } 
	    else {
	        int mySeq = getSequence();
	        int otherSeq = other.getSequence();

	        if (mySeq > otherSeq) {
	            return 1;
	        }
	        else if (mySeq < otherSeq) {
	            return -1;
	        }
	        else return 0;
	    }
	}
}
