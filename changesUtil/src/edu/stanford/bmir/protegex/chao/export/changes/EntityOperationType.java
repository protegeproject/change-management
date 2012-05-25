package edu.stanford.bmir.protegex.chao.export.changes;

class EntityOperationType {
    String operationType;
    String entityType;

    public EntityOperationType(String opType, String entityType) {
        this.operationType = opType;
        this.entityType = entityType;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getEntityType() {
        return entityType;
    }
    public void setOperationType(String opType) {
        this.operationType = opType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}