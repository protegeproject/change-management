package edu.stanford.bmir.protegex.chao.export.changes;

import edu.stanford.bmir.protegex.chao.change.api.Change;

class ICDChangesFilter extends DefaultChangesFilter {

        @Override
        public boolean isFilteredOut(Change change) {
            if (super.isFilteredOut(change)) {
                return true;
            }

            String author = change.getAuthor();
            String desc = change.getContext();

            if ((author != null && author.equalsIgnoreCase("WHO")) || desc.contains("Automatic") || desc.contains("Exported") ||
                    desc.contains("owl:equivalentClass") ) {
                return true;
            }

            return false;
        }

        @Override
        public EntityOperationType getEntityAndOperationType(Change change) {
            String desc = change.getContext();

            if (desc.contains("Subclass Added") ) {
                return new EntityOperationType(ChangesExport.OP_TYPE_ADD, ChangesExport.ENTITY_CLS);
            }

            if (desc.contains("Superclass Added") || desc.contains("Superclass Removed")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_MOVE, ChangesExport.ENTITY_CLS);
            }

            if (desc.contains("Annotation")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_PROP_CHANGE, getOntologyComponentType(change.getApplyTo()));
            }

            if (desc.contains("Retire")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_DELETE, ChangesExport.ENTITY_CLS);
            }

            if (desc.contains("Property: rdfs:subClassOf")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_MOVE, ChangesExport.ENTITY_CLS);
            }

            if (desc.contains("hierarchy") || desc.contains("Move")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_MOVE, ChangesExport.ENTITY_CLS);
            }

            if (desc.contains("Imported") || desc.contains("reference")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_REF, ChangesExport.ENTITY_CLS);
            }

            EntityOperationType entityOp = new EntityOperationType("","");
            if (desc.contains("Create")) {
                entityOp.setOperationType(ChangesExport.OP_TYPE_ADD);
                if (desc.contains("class")) {
                    entityOp.setEntityType(ChangesExport.ENTITY_CLS);
                }
                return entityOp;
            }

            if (desc.contains("Property:")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_PROP_CHANGE, getOntologyComponentType(change.getApplyTo()));
            }

            if (desc.contains("Replace") || desc.contains("Set") || desc.contains("Add") || desc.contains("Delete") ||
                    desc.contains("Remove") || desc.contains("Made")) {
                return new EntityOperationType(ChangesExport.OP_TYPE_PROP_CHANGE, ChangesExport.ENTITY_CLS);
            }

            return new EntityOperationType("", getOntologyComponentType(change.getApplyTo()));
        }

    }