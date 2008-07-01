package edu.stanford.smi.protegex.server_changes.postprocess;

import edu.stanford.smi.protegex.server_changes.ChangesDb;
import edu.stanford.smi.protegex.server_changes.model.generated.Change;

/**
 * This guy is very similar to a listener - maybe these concepts should be merged.
 * @author tredmond
 *
 */
public interface PostProcessor {
    public void initialize(ChangesDb changes_db);
    
    public void addChange(Change change);
}
