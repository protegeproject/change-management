package edu.stanford.bmir.protegex.chao.change.api;

import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultAnnotation_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultAnnotation_Change;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultAnnotation_Modified;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultAnnotation_Removed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultChange;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultClass_Change;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultClass_Created;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultClass_Deleted;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultComposite_Change;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultCreated_Change;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultDeleted_Change;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultDirectType_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultDirectType_Removed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultDisjointClass_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultDocumentation_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultDocumentation_Removed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultDomainProperty_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultDomainProperty_Removed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultIndividual_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultIndividual_Change;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultIndividual_Created;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultIndividual_Deleted;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultIndividual_Removed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultMaximum_Cardinality;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultMaximum_Value;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultMinimum_Cardinality;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultMinimum_Value;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultName_Changed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultProperty_Change;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultProperty_Created;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultProperty_Deleted;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultProperty_Value;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultSubclass_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultSubclass_Removed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultSubproperty_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultSubproperty_Removed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultSuperclass_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultSuperclass_Removed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultSuperproperty_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultSuperproperty_Removed;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultTemplateSlot_Added;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultTemplateSlot_Removed;
import edu.stanford.smi.protege.code.generator.wrapping.OntologyJavaMappingUtil;

/**
 * Generated by Protege (http://protege.stanford.edu).
 *
 * @version generated on Mon Aug 18 21:13:43 GMT-08:00 2008
 */
public class OntologyJavaMapping {

    public static void initMap() {
        OntologyJavaMappingUtil.add("Annotation_Added", Annotation_Added.class, DefaultAnnotation_Added.class);
        OntologyJavaMappingUtil.add("Annotation_Change", Annotation_Change.class, DefaultAnnotation_Change.class);
        OntologyJavaMappingUtil.add("Annotation_Modified", Annotation_Modified.class, DefaultAnnotation_Modified.class);
        OntologyJavaMappingUtil.add("Annotation_Removed", Annotation_Removed.class, DefaultAnnotation_Removed.class);
        OntologyJavaMappingUtil.add("Change", Change.class, DefaultChange.class);
        OntologyJavaMappingUtil.add("Class_Change", Class_Change.class, DefaultClass_Change.class);
        OntologyJavaMappingUtil.add("Class_Created", Class_Created.class, DefaultClass_Created.class);
        OntologyJavaMappingUtil.add("Class_Deleted", Class_Deleted.class, DefaultClass_Deleted.class);
        OntologyJavaMappingUtil.add("Composite_Change", Composite_Change.class, DefaultComposite_Change.class);
        OntologyJavaMappingUtil.add("Created_Change", Created_Change.class, DefaultCreated_Change.class);
        OntologyJavaMappingUtil.add("Deleted_Change", Deleted_Change.class, DefaultDeleted_Change.class);
        OntologyJavaMappingUtil.add("DirectType_Added", DirectType_Added.class, DefaultDirectType_Added.class);
        OntologyJavaMappingUtil.add("DirectType_Removed", DirectType_Removed.class, DefaultDirectType_Removed.class);
        OntologyJavaMappingUtil.add("DisjointClass_Added", DisjointClass_Added.class, DefaultDisjointClass_Added.class);
        OntologyJavaMappingUtil.add("Documentation_Added", Documentation_Added.class, DefaultDocumentation_Added.class);
        OntologyJavaMappingUtil.add("Documentation_Removed", Documentation_Removed.class, DefaultDocumentation_Removed.class);
        OntologyJavaMappingUtil.add("DomainProperty_Added", DomainProperty_Added.class, DefaultDomainProperty_Added.class);
        OntologyJavaMappingUtil.add("DomainProperty_Removed", DomainProperty_Removed.class, DefaultDomainProperty_Removed.class);
        OntologyJavaMappingUtil.add("Individual_Added", Individual_Added.class, DefaultIndividual_Added.class);
        OntologyJavaMappingUtil.add("Individual_Change", Individual_Change.class, DefaultIndividual_Change.class);
        OntologyJavaMappingUtil.add("Individual_Created", Individual_Created.class, DefaultIndividual_Created.class);
        OntologyJavaMappingUtil.add("Individual_Deleted", Individual_Deleted.class, DefaultIndividual_Deleted.class);
        OntologyJavaMappingUtil.add("Individual_Removed", Individual_Removed.class, DefaultIndividual_Removed.class);
        OntologyJavaMappingUtil.add("Maximum_Cardinality", Maximum_Cardinality.class, DefaultMaximum_Cardinality.class);
        OntologyJavaMappingUtil.add("Maximum_Value", Maximum_Value.class, DefaultMaximum_Value.class);
        OntologyJavaMappingUtil.add("Minimum_Cardinality", Minimum_Cardinality.class, DefaultMinimum_Cardinality.class);
        OntologyJavaMappingUtil.add("Minimum_Value", Minimum_Value.class, DefaultMinimum_Value.class);
        OntologyJavaMappingUtil.add("Name_Changed", Name_Changed.class, DefaultName_Changed.class);
        OntologyJavaMappingUtil.add("Property_Change", Property_Change.class, DefaultProperty_Change.class);
        OntologyJavaMappingUtil.add("Property_Created", Property_Created.class, DefaultProperty_Created.class);
        OntologyJavaMappingUtil.add("Property_Deleted", Property_Deleted.class, DefaultProperty_Deleted.class);
        OntologyJavaMappingUtil.add("Property_Value", Property_Value.class, DefaultProperty_Value.class);
        OntologyJavaMappingUtil.add("Subclass_Added", Subclass_Added.class, DefaultSubclass_Added.class);
        OntologyJavaMappingUtil.add("Subclass_Removed", Subclass_Removed.class, DefaultSubclass_Removed.class);
        OntologyJavaMappingUtil.add("Subproperty_Added", Subproperty_Added.class, DefaultSubproperty_Added.class);
        OntologyJavaMappingUtil.add("Subproperty_Removed", Subproperty_Removed.class, DefaultSubproperty_Removed.class);
        OntologyJavaMappingUtil.add("Superclass_Added", Superclass_Added.class, DefaultSuperclass_Added.class);
        OntologyJavaMappingUtil.add("Superclass_Removed", Superclass_Removed.class, DefaultSuperclass_Removed.class);
        OntologyJavaMappingUtil.add("Superproperty_Added", Superproperty_Added.class, DefaultSuperproperty_Added.class);
        OntologyJavaMappingUtil.add("Superproperty_Removed", Superproperty_Removed.class, DefaultSuperproperty_Removed.class);
        OntologyJavaMappingUtil.add("TemplateSlot_Added", TemplateSlot_Added.class, DefaultTemplateSlot_Added.class);
        OntologyJavaMappingUtil.add("TemplateSlot_Removed", TemplateSlot_Removed.class, DefaultTemplateSlot_Removed.class);
    }
}
