package edu.stanford.bmir.protegex.chao.export.changes;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.Composite_Change;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.Ontology_Component;

class NCIChangesFilter extends DefaultChangesFilter {

    @Override
    public boolean isFilteredOut(Change change) {

        String desc = change.getContext();

        if (desc.contains("Save class") || desc.contains("Save diffs") || desc.contains("Create a copy (clone)") ||
                desc.contains("Create class") || desc.contains("BatchEdit") || desc.contains("Equivalent Class") ||
                desc.contains("Remove superclass") || desc.contains("Assert classification changes")) {
            return true;
        }
        if (desc.contains("rdfs:subClassOf") || desc.contains("owl:equivalentClass")) {
            return true;
        }
        if (desc.contains("(instance of: owl:Class)") || desc.contains("(added to: owl:Thing)")) {
            return true;
        }
        if (desc.contains("Remove superclass owl:Thing") || desc.contains("Superclass Removed: owl:Thing") || desc.contains("removed from: owl:Thing)")) {
            return true;
        }
        if (desc.contains("template slot")) {
            return true;
        }
        if (desc.contains("direct type : owl:DeprecatedClass")) {
            return true;
        }
        if(desc.contains("Subclass Removed:") && !desc.contains(" some ")) {
            return true;
        }
        if(desc.contains("Subclass Added:") && !desc.contains(" some ")) {
            return true;
        }

        Ontology_Component oc = change.getApplyTo();
        if (oc == null) {
            return true;
        } else {
            String entity = oc.getCurrentName();
            if (entity == null) {
                return true;
            } else {
                if (entity.contains("@")) { //filter out changes on anonymous classes
                    return true;
                }
            }
        }

        //filter out composite changes
        if (change.canAs(Composite_Change.class)) {
            Composite_Change compChange = change.as(Composite_Change.class);
            if (compChange.hasSubChanges()) {
                return true;
            }
        }

        return false;
    }


    @Override
    public EntityOperationType getEntityAndOperationType(Change change) {
        String entityType = getOntologyComponentType(change.getApplyTo());

        String desc = change.getContext();

        if (desc.contains("Annotation")) {
            return new EntityOperationType(ChangesExport.OP_TYPE_PROP_CHANGE, entityType);
        }

        if (desc.contains("Create")) {
            return new EntityOperationType(ChangesExport.OP_TYPE_ADD, entityType);
        }

        if (desc.contains("Domain Property")) {
            return new EntityOperationType(ChangesExport.OP_TYPE_PROP_CHANGE, entityType);
        }

        if (desc.contains("Superclass Added") || desc.contains("Subclass Added") ) {
            if (desc.contains(" some ")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_ADD, ChangesExport.ENTITY_RESTR);
            } else {
                return new EntityOperationType(ChangesExport.OP_TYPE_MOVE, entityType);
            }
        }

        if (desc.contains("Superclass Removed") || desc.contains("Subclass Removed")) {
            if (desc.contains(" some ")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_DELETE, ChangesExport.ENTITY_RESTR);
            }else {
                return new EntityOperationType(ChangesExport.OP_TYPE_MOVE, entityType);
            }
        }

        if (desc.contains("Property:")) {
            return new EntityOperationType(ChangesExport.OP_TYPE_PROP_CHANGE, entityType);
        }

        if (desc.contains("Remove disjoint") || desc.contains("Add disjoint")) {
            return new EntityOperationType(ChangesExport.OP_TYPE_PROP_CHANGE, entityType);
        }

        if(desc.contains("Instance Added:") && desc.contains("(instance of: owl:DeprecatedClass)")) {
            return new EntityOperationType(ChangesExport.OP_TYPE_DELETE, entityType);
        }

        if (desc.contains("Name change")) {
            return new EntityOperationType(ChangesExport.OP_TYPE_PROP_CHANGE, entityType);
        }

        if(desc.contains("Retire") || desc.contains("Delete")) {
            return new EntityOperationType(ChangesExport.OP_TYPE_DELETE, entityType);
        }

        return new EntityOperationType("", entityType);

    }

}