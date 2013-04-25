/*get timestamp instances whose date matches a pattern*/
SELECT * FROM icd_ann as rec_ann_date 
    WHERE 
    rec_ann_date.slot = 'date' and (rec_ann_date.short_value LIKE "03/__/2012%") /*in March 2012*/
    LIMIT 30000;
    
/*get timestamp instances and show how many days past since their were created*/
SELECT *, TIMESTAMPDIFF(DAY, STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), '%m/%d/%Y %H:%i:%s'), NOW()) as days_since_timestamp FROM icd_ann as rec_ann_date 
    WHERE 
    rec_ann_date.slot = 'date'
    LIMIT 30000;

/*get timestamp instances whose date is in the last 14 days*/
SELECT * FROM icd_ann as rec_ann_date 
    WHERE 
    rec_ann_date.slot = 'date' and 
    (TIMESTAMPDIFF(DAY, STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), '%m/%d/%Y %H:%i:%s'), NOW())
        < 14) /*14 days*/
    LIMIT 30000;

/*get the change and/or annotation instances whose timestamp's date is in the last 14 days*/
SELECT * FROM icd_ann as rec_ann 
    JOIN icd_ann as rec_ann_date ON rec_ann.short_value = rec_ann_date.frame 
    WHERE 
    rec_ann.slot = 'timestamp' and
    rec_ann_date.slot = 'date' and 
    (TIMESTAMPDIFF(DAY, STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), '%m/%d/%Y %H:%i:%s'), NOW())
        < 14); /*14 days*/
    
/*get the change and/or annotation instances whose timestamp's date matches a pattern
    and see what instance those changes and/or annotations are associated to*/
SELECT * FROM icd_ann as all_ann 
    JOIN icd_ann as rec_ann ON all_ann.frame = rec_ann.frame 
    JOIN icd_ann as rec_ann_date ON rec_ann.short_value = rec_ann_date.frame 
    WHERE all_ann.slot = "applyTo" AND 
    rec_ann.slot = 'timestamp' and
    rec_ann_date.slot = 'date' and (rec_ann_date.short_value LIKE "03/__/2012%") /*in March 2012*/ 
    LIMIT 1000;

/*get the change and/or annotation instances whose timestamp's date is in the last 14 days
    and see what instance those changes and/or annotations are associated to*/
SELECT * FROM icd_ann as all_ann 
    JOIN icd_ann as rec_ann ON all_ann.frame = rec_ann.frame 
    JOIN icd_ann as rec_ann_date ON rec_ann.short_value = rec_ann_date.frame 
    WHERE all_ann.slot = "applyTo" AND 
    rec_ann.slot = 'timestamp' and
    (TIMESTAMPDIFF(DAY, STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), '%m/%d/%Y %H:%i:%s'), NOW())
        < 14) /*14 days*/
    LIMIT 1000;

	

/*create a view from the above query called 'recent_changes'.
    The recent_changes view would contain a subset of the ChAO database table,
    where the frame column contains recent changes and annotations, 
    and the short_value column contains the name of the ontology component 
    to which the change or annotation is applied.
    An "ontology component" is simply a placeholder in ChAO for the real concept
    (class, property, individual) from the domain ontology */
CREATE VIEW recent_changes AS
  SELECT all_ann.* FROM icd_ann as all_ann 
    JOIN icd_ann as rec_ann ON all_ann.frame = rec_ann.frame 
    JOIN icd_ann as rec_ann_date ON rec_ann.short_value = rec_ann_date.frame 
    WHERE all_ann.slot = "applyTo" AND 
    rec_ann.slot = 'timestamp' and
    (TIMESTAMPDIFF(DAY, STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), '%m/%d/%Y %H:%i:%s'), NOW())
        < 14) /*14 days*/
    ;

/*use the above view to query the "real names" of the ontology components
    (i.e. the URIs in the domain ontology of the "ontology components" that 
    are used as placeholders in ChAO)
    that have recent changes or annotations.
    
    Remove the LIMIT to get all values. 
    The URIs are in the all_ann.short_value.
    */
SELECT * FROM icd_ann as recent_changes 
    JOIN icd_ann as all_ann ON all_ann.frame = recent_changes.short_value 
    WHERE all_ann.slot = 'currentName'
    LIMIT 1000;