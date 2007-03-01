package edu.stanford.smi.protegex.server_changes.time;


import java.util.Date;
import java.util.List;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.server_changes.model.Timestamp;

public interface ChangingFrame {
    
    public ChangingFrameManager getFrameManager();
    
    public List<Instance> getChanges();
    
    public List<Instance> getCompositeChanges();
   
    public String getFinalName();
    
    public String getInitialName();
    
    public List<String> getNames();
    
    public String getNameJustBefore(Instance change);
    
    public String getNameJustBefore(Timestamp date);
    
    public String toString();

}
