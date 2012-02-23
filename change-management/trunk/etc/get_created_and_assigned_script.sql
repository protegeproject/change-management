# ********************* Export all created classes and TAG assignement *******

# Looks into icd_ann and gets all "Create class" changes and their author, then from icd, the sorting label, the title and assigned primary TAG.
# Remove last line if you don't want to export to CSV.


SELECT icd_sl.short_value AS sorting_label, icd_title_label.short_value AS title,
       change_authors.short_value AS author, icd_pt.short_value AS assignedPrimaryTAG,
       oc.short_value AS created_class_id     
     FROM icd_ann AS all_changes 
  JOIN icd_ann AS change_authors ON change_authors.slot='author' AND all_changes.frame=change_authors.frame
  JOIN icd_ann AS change_applyto ON change_applyto.slot='applyTo' AND all_changes.frame=change_applyto.frame
  JOIN icd_ann AS oc ON oc.slot='currentName' AND change_applyto.short_value=oc.frame
  LEFT JOIN icd_umbrella AS icd_pt ON icd_pt.slot='http://who.int/icd#assignedPrimaryTAG' AND oc.short_value=icd_pt.frame
  LEFT JOIN icd_umbrella AS icd_sl ON icd_sl.slot='http://who.int/icd#sortingLabel' AND oc.short_value=icd_sl.frame
  JOIN icd_umbrella AS icd_title_id ON icd_title_id.slot='http://who.int/icd#icdTitle' AND oc.short_value=icd_title_id.frame
  JOIN icd_umbrella AS icd_title_label ON icd_title_label.slot='http://who.int/icd#label' AND icd_title_id.short_value=icd_title_label.frame
WHERE all_changes.slot='context' and all_changes.short_value like 'Create class%'
INTO OUTFILE '/tmp/export_icat_created_and_assign.csv' FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n' ;

# *************************************************************

