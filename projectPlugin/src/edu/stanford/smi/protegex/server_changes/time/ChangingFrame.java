package edu.stanford.smi.protegex.server_changes.time;


import java.util.Date;
import java.util.List;

import edu.stanford.smi.protege.model.Instance;

public interface ChangingFrame {
    
    public List<Instance> getChanges();
    
    public List<Instance> getCompositeChanges();
   
    public String getFinalName();
    
    public String getInitialName();
    
    public List<String> getNames();
    
    public String getNameBefore(Instance change);
    
    public String getNameJustBefore(Date date);

}
