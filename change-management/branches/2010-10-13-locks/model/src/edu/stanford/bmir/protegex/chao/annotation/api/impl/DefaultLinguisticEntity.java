package edu.stanford.bmir.protegex.chao.annotation.api.impl;

import edu.stanford.smi.protege.code.generator.wrapping.AbstractWrappedInstance;
import edu.stanford.smi.protege.model.*;
import edu.stanford.bmir.protegex.chao.annotation.api.LinguisticEntity;
import edu.stanford.bmir.protegex.chao.annotation.api.*;

/**
 * Generated by Protege (http://protege.stanford.edu).
 * Source Class: LinguisticEntity
 *
 * @version generated on Wed Dec 09 15:45:51 PST 2009
 */
public class DefaultLinguisticEntity extends AbstractWrappedInstance
         implements LinguisticEntity {

    public DefaultLinguisticEntity(Instance instance) {
        super(instance);
    }


    public DefaultLinguisticEntity() {
    }

    // Slot label

    public String getLabel() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getLabelSlot());
    }


    public Slot getLabelSlot() {
        final String name = "label";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasLabel() {
        return hasSlotValues(getLabelSlot());
    }


    public void setLabel(String newLabel) {
        setSlotValue(getLabelSlot(), newLabel);
    }

    // Slot language

    public String getLanguage() {
        return (String) getWrappedProtegeInstance().getOwnSlotValue(getLanguageSlot());
    }


    public Slot getLanguageSlot() {
        final String name = "language";
        return getKnowledgeBase().getSlot(name);
    }


    public boolean hasLanguage() {
        return hasSlotValues(getLanguageSlot());
    }


    public void setLanguage(String newLanguage) {
        setSlotValue(getLanguageSlot(), newLanguage);
    }
}