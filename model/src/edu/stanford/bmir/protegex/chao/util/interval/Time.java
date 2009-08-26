package edu.stanford.bmir.protegex.chao.util.interval;

import java.util.Date;

import edu.stanford.bmir.protegex.chao.ontologycomp.api.Timestamp;


public class Time implements Comparable<Time> {
    private Integer counter;
    private Date date;

    public Time() {
        this(new Date());
    }
    
    public Time(Date d) {
        date = d;
        counter = -1;
    }
    
    public Time(Timestamp time) {
        date = time.getDateParsed();
        counter = time.getSequence();
    }
        
    public Integer getCounter() {
        return counter;
    }

    public Date getDate() {
        return date;
    }

    public int compareTo(Time o) {
        int result = date.compareTo(o.getDate());
        if (result == 0) {
            result = counter.compareTo(o.getCounter());
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "<" + date + " [" + counter + "]>";
    }

}
