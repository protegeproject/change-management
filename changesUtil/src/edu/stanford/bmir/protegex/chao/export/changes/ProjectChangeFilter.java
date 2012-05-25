package edu.stanford.bmir.protegex.chao.export.changes;

import edu.stanford.bmir.protegex.chao.change.api.Change;

interface ProjectChangeFilter {
    boolean isFilteredOut(Change change);
    EntityOperationType getEntityAndOperationType(Change change);
}