package edu.stanford.bmir.protegex.chao.export.changes;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Class;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Individual;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Property;

class DefaultChangesFilter implements ProjectChangeFilter {

    public boolean isFilteredOut(Change change) {
        return change.getPartOfCompositeChange() != null;
    }

    public EntityOperationType getEntityAndOperationType(Change change) {
        return new EntityOperationType("", "");
    }

    /*
     * Filters
     */



    String getOntologyComponentType(Ontology_Component oc) {
        if (oc == null) {
            return "";
        } else if (oc instanceof Ontology_Class) {
            return ENTITY_CLS;
        } else if (oc instanceof Ontology_Property) {
            return ENTITY_PROP;
        } else if (oc instanceof Ontology_Individual) {
            return ENTITY_IND;
        }
        return "";
    }

}