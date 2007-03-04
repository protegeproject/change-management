/**
 * 
 */
package edu.stanford.smi.protegex.changes;

import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeSlot;

public enum ChangeTableColumn {
  CHANGE_COLNAME_ACTION("Action", ChangeSlot.action),
    CHANGE_COLNAME_DESCRIPTION("Description", ChangeSlot.body),
    CHANGE_COLNAME_AUTHOR("Author", ChangeSlot.author),
    CHANGE_COLNAME_CREATED("Created", ChangeSlot.timestamp);

    private String name;
    private ChangeSlot search_slot;
    private ChangeTableColumn(String name, ChangeSlot search_slot) {
      this.name = name;
      this.search_slot = search_slot;
    }

    public String getName() {
      return name;
    }
    
    public ChangeSlot getSearchSlot() {
        return search_slot;
    }
}