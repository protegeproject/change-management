package edu.stanford.bmir.protegex.chao.util.interval;

import java.io.Serializable;
import java.util.Date;

import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Localizable;


public class SimpleTime implements Comparable<SimpleTime>, Serializable, Localizable {
    private static final long serialVersionUID = 7660908078388682306L;

    private Integer counter;
    private Date date;

    public SimpleTime() {
        this(new Date());
    }

    public SimpleTime(Date d) {
        date = d;
        counter = -1;
    }

    public SimpleTime(Timestamp time) {
        date = time.getDateParsed();
        counter = time.getSequence();
    }

    public SimpleTime(Date date, int sequence) {
        this.date = date;
        this.counter = sequence;
    }

    public Integer getCounter() {
        return counter;
    }

    public Date getDate() {
        return date;
    }

    public int compareTo(SimpleTime o) {
        //using reverse time ordering
        int result = o.getDate().compareTo(date);
        if (result == 0) {
            result = o.getCounter().compareTo(counter);
        }
        return result;
    }

    public void localize(KnowledgeBase kb) {

    }

    @Override
    public String toString() {
        return "<" + date + " [" + counter + "]>";
    }

}
