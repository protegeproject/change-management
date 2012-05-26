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

    protected String getOntologyComponentType(Ontology_Component oc) {
        if (oc == null) {
            return "";
        } else if (oc.canAs(Ontology_Class.class)) {
            return ChangesExport.ENTITY_CLS;
        } else if (oc.canAs(Ontology_Property.class)) {
            return ChangesExport.ENTITY_PROP;
        } else if (oc.canAs(Ontology_Individual.class)) {
            return ChangesExport.ENTITY_IND;
        }
        return "";
    }

}