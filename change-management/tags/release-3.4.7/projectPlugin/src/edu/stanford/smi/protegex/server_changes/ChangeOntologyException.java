package edu.stanford.smi.protegex.server_changes;

public class ChangeOntologyException extends Exception {
    
    private static final long serialVersionUID = 6631665568931918358L;

    public ChangeOntologyException() {}
    
    public ChangeOntologyException(String msg) {
        super(msg);
    }
    
    public ChangeOntologyException(String msg, Throwable t) {
        super(msg, t);
    }

}
