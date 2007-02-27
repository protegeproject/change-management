package edu.stanford.smi.protegex.server_changes.time;

import edu.stanford.smi.protege.model.Instance;

public interface ChangingFrameManager {

    public ChangingFrame getChangingFrame(Instance change);
    
    /**
     * Gets the changing class based on its latest name.
     * @param name
     * @return
     */
    public ChangingFrame getChangingFrameByLatestName(String name);
    
    public ChangingFrame getChangingFrameByInitialName(String name);
    
    public ChangingFrame getApplyTo(Instance annotateableThing);
    
}
