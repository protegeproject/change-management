/**
 * 
 */
package edu.stanford.smi.protegex.changes;

import edu.stanford.smi.protegex.server_changes.model.ChangeModel.ChangeSlot;

public enum ChangeTableColumn {
  CHANGE_COLNAME_ACTION("Action", "Type of Change", ChangeSlot.action),
    CHANGE_COLNAME_DESCRIPTION("Description", "Details of the action", ChangeSlot.context),
    CHANGE_COLNAME_AUTHOR("Author", "Person who made the change", ChangeSlot.author),
    CHANGE_COLNAME_CREATED("Created", "Date and time the change was made", ChangeSlot.timestamp);

    private String name;
    private ChangeSlot search_slot;
    private String heading;
    private ChangeTableColumn(String name, 
                              String heading,
                              ChangeSlot search_slot) {
      this.name = name;
      this.heading = heading;
      this.search_slot = search_slot;
    }

    public String getName() {
      return name;
    }
    
    public String getHeading() {
        return heading;
    }
    
    public ChangeSlot getSearchSlot() {
        return search_slot;
    }
}