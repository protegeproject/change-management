/*
* This is an improved version of the 'recent changes.sql` file, where 
* we can filter changes based on time periods starting with a specific 
* date (as opposed to a sliding window defined by relative dates)
* and we do also filetering by entities that have a "...Section" type.
*/
   
/************************************************************
The final queries/statements
*/
   
DROP VIEW IF EXISTS recent_changes;

/*create a view from the above query (with timestamp more recent than a cutoff date), called 'recent_changes'.
    The recent_changes view would contain a subset of the ChAO database table,
    where the frame column contains recent changes and annotations, 
    and the short_value column contains the name of the ontology component 
    to which the change or annotation is applied.
    An "ontology component" is simply a placeholder in ChAO for the real concept
    (class, property, individual) from the domain ontology */
CREATE VIEW recent_changes AS
  SELECT CAST(rec_ann_date.short_value AS CHAR) as timestmp, all_ann.* FROM icd_ann as all_ann 
    JOIN icd_ann as rec_ann ON all_ann.frame = rec_ann.frame 
    JOIN icd_ann as rec_ann_date ON rec_ann.short_value = rec_ann_date.frame 
    WHERE all_ann.slot = "applyTo" AND 
    rec_ann.slot = 'timestamp' and
    STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), "%m/%d/%Y %H:%i:%s") 
		>= STR_TO_DATE("07/22/2020", "%m/%d/%Y") /*timestamp is on or after cutoff date*/
    ;


-- DROP TABLE IF EXISTS recent_chg_sum;
DROP VIEW IF EXISTS recent_chg_sum;

-- CREATE TEMPORARY TABLE recent_chg_sum AS
CREATE VIEW recent_chg_sum AS
SELECT all_ann_name.short_value as entity, 
	   recent_changes.timestmp,
       IFNULL(all_ann_context.short_value, all_ann_context.long_value) as context,
	   all_ann_context.short_value as context_short, 
       all_ann_context.long_value as context_long, 
       recent_changes.frame, recent_changes.slot, recent_changes.short_value        
       FROM recent_changes
	JOIN icd_ann AS all_ann_name ON all_ann_name.frame = recent_changes.short_value
	JOIN icd_ann AS all_ann_context ON all_ann_context.frame = recent_changes.frame
    WHERE all_ann_name.slot = 'currentName'
		AND all_ann_context.slot = "context"
;

/*
Return the list of distinct entities that have recent changes on them
(some entities may have only sub-changes attached to them, 
e.g. the super-classes of newly created classes)
*/
SELECT DISTINCT rec_chg.entity FROM recent_chg_sum AS rec_chg
    LEFT OUTER JOIN icd_umbrella AS icd ON rec_chg.entity = icd.frame
    WHERE icd.slot = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
		AND icd.short_value LIKE "%Section"
    LIMIT 30000;

/*
Return the full list of recent changes, with timestamp and context
(some entities may have only sub-changes attached to them, 
e.g. the super-classes of newly created classes)
*/
SELECT DISTINCT rec_chg.* FROM recent_chg_sum AS rec_chg
    LEFT OUTER JOIN icd_umbrella AS icd ON rec_chg.entity = icd.frame
    WHERE icd.slot = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
		AND icd.short_value LIKE "%Section"
    LIMIT 30000;




/************************************************************
The intermediate queries/statements that lead us to the final statements
*/
   
   
SELECT STR_TO_DATE(SUBSTRING("11/15/2009 21:02:37 PST", 1, 19), '%m/%d/%Y %H:%i:%s') as ts, 
	NOW(), 
    STR_TO_DATE("11/16/2009", '%m/%d/%Y') as baseline,
	STR_TO_DATE(SUBSTRING("11/15/2009 21:02:37 PST", 1, 19), '%m/%d/%Y %H:%i:%s') > STR_TO_DATE("11/16/2009", '%m/%d/%Y') as gt;
    
    
/*get timestamp instances whose date is greater than a given cutoff date*/
SELECT * FROM icd_ann as rec_ann_date 
    WHERE 
    rec_ann_date.slot = 'date' and 
    STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), '%m/%d/%Y %H:%i:%s') 
		>= STR_TO_DATE("07/22/2020", '%m/%d/%Y') /*timestamp is on or after cutoff date*/
    LIMIT 30000;
    
/*get the change and/or annotation instances whose timestamp's date is greater than a given cutoff date*/
SELECT * FROM icd_ann as rec_ann 
    JOIN icd_ann as rec_ann_date ON rec_ann.short_value = rec_ann_date.frame 
    WHERE 
    rec_ann.slot = 'timestamp' and
    rec_ann_date.slot = 'date' and 
    STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), '%m/%d/%Y %H:%i:%s') 
		>= STR_TO_DATE("07/22/2020", '%m/%d/%Y'); /*timestamp is on or after cutoff date*/

/*get the change and/or annotation instances whose timestamp's date is greater than a given cutoff date
    and see what instance those changes and/or annotations are associated to*/
SELECT * FROM icd_ann as all_ann 
    JOIN icd_ann as rec_ann ON all_ann.frame = rec_ann.frame 
    JOIN icd_ann as rec_ann_date ON rec_ann.short_value = rec_ann_date.frame 
    WHERE all_ann.slot = "applyTo" AND 
    rec_ann.slot = 'timestamp' and
    STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), '%m/%d/%Y %H:%i:%s') 
		>= STR_TO_DATE("07/22/2020", '%m/%d/%Y') /*timestamp is on or after cutoff date*/
    LIMIT 1000;

DROP VIEW IF EXISTS recent_changes;

/*create a view from the above query (with timestamp more recent than a cutoff date), called 'recent_changes'.
    The recent_changes view would contain a subset of the ChAO database table,
    where the frame column contains recent changes and annotations, 
    and the short_value column contains the name of the ontology component 
    to which the change or annotation is applied.
    An "ontology component" is simply a placeholder in ChAO for the real concept
    (class, property, individual) from the domain ontology */
CREATE VIEW recent_changes AS
  SELECT CAST(rec_ann_date.short_value AS CHAR) as timestmp, all_ann.* FROM icd_ann as all_ann 
    JOIN icd_ann as rec_ann ON all_ann.frame = rec_ann.frame 
    JOIN icd_ann as rec_ann_date ON rec_ann.short_value = rec_ann_date.frame 
    WHERE all_ann.slot = "applyTo" AND 
    rec_ann.slot = 'timestamp' and
    STR_TO_DATE(SUBSTRING(rec_ann_date.short_value, 1, 19), "%m/%d/%Y %H:%i:%s") 
		>= STR_TO_DATE("07/22/2020", "%m/%d/%Y") /*timestamp is on or after cutoff date*/
    ;

/*use the above view to query for the "real names" of the ontology components
    (i.e. the URIs in the domain ontology of the "ontology components" that 
    are used as placeholders in ChAO)
    that have recent changes or annotations.
    
    Remove the LIMIT to get all values. 
    The URIs are in the all_ann.short_value.
    */
SELECT * FROM recent_changes 
    JOIN icd_ann as all_ann ON all_ann.frame = recent_changes.short_value 
    WHERE all_ann.slot = 'currentName'
    LIMIT 1000;

/* Same as the query above, but returns only the DISTINCT "real names" of the ontology components
	that have recent changes or annotations.
*/
SELECT DISTINCT all_ann.short_value FROM recent_changes 
    JOIN icd_ann as all_ann ON all_ann.frame = recent_changes.short_value 
    WHERE all_ann.slot = 'currentName'
    LIMIT 30000;

/* Same as the query above, but returns only the ontology components 
	that correspond to ICD entities and have recent changes or annotations.
*/
SELECT DISTINCT all_ann.short_value FROM recent_changes 
    JOIN icd_ann as all_ann ON all_ann.frame = recent_changes.short_value 
    LEFT OUTER JOIN icd_umbrella AS icd ON all_ann.short_value = icd.frame
    WHERE all_ann.slot = 'currentName'
		AND icd.slot = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
    LIMIT 30000;

SELECT * FROM recent_changes
	JOIN icd_ann AS all_ann ON all_ann.frame = recent_changes.frame
    WHERE all_ann.slot = "context"
    LIMIT 1000;

SELECT recent_changes.short_value, all_ann.short_value as context_short, all_ann.long_value as context_long FROM recent_changes
	JOIN icd_ann AS all_ann ON all_ann.frame = recent_changes.frame
    WHERE all_ann.slot = "context"
    LIMIT 1000;


-- DROP TABLE IF EXISTS recent_chg_sum;
DROP VIEW IF EXISTS recent_chg_sum;

-- CREATE TEMPORARY TABLE recent_chg_sum AS
CREATE VIEW recent_chg_sum AS
SELECT all_ann_name.short_value as entity, 
	   recent_changes.timestmp,
       IFNULL(all_ann_context.short_value, all_ann_context.long_value) as context,
	   all_ann_context.short_value as context_short, 
       all_ann_context.long_value as context_long, 
       recent_changes.frame, recent_changes.slot, recent_changes.short_value        
       FROM recent_changes
	JOIN icd_ann AS all_ann_name ON all_ann_name.frame = recent_changes.short_value
	JOIN icd_ann AS all_ann_context ON all_ann_context.frame = recent_changes.frame
    WHERE all_ann_name.slot = 'currentName'
		AND all_ann_context.slot = "context"
;

DESCRIBE recent_chg_sum;

SELECT entity, timestmp, context FROM recent_chg_sum
LIMIT 30000;

SELECT DISTINCT entity FROM recent_chg_sum
LIMIT 30000;

SELECT COUNT(*) FROM recent_chg_sum;

/*
SELECT @@SQL_MODE;
SET @@SQL_MODE = REPLACE(@@SQL_MODE, 'NO_ZERO_DATE', '');
SET @@SQL_MODE = REPLACE(@@SQL_MODE, 'NO_ZERO_IN_DATE', '');
SELECT @@SQL_MODE;

DROP TABLE IF EXISTS recent_chg_sum;
CREATE TEMPORARY TABLE recent_chg_sum (
	entity CHAR(255),
    timestmp CHAR(25),
    context VARCHAR(2500)
);
#INSERT INTO recent_chg_sum
*/


SELECT DISTINCT rec_chg.entity FROM recent_chg_sum AS rec_chg
    LEFT OUTER JOIN icd_umbrella AS icd ON rec_chg.entity = icd.frame
    WHERE icd.slot = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
    LIMIT 30000;

SELECT rec_chg.*, icd.short_value, COUNT(icd.short_value) AS type_count FROM recent_chg_sum AS rec_chg
    LEFT OUTER JOIN icd_umbrella AS icd ON rec_chg.entity = icd.frame
    WHERE icd.slot = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
		AND icd.short_value NOT LIKE "%Section"
    GROUP BY icd.short_value
    LIMIT 30000;

SELECT rec_chg.*, icd.short_value FROM recent_chg_sum AS rec_chg
    LEFT OUTER JOIN icd_umbrella AS icd ON rec_chg.entity = icd.frame
    WHERE icd.slot = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
		AND icd.short_value LIKE "%Section"
    GROUP BY icd.short_value
    LIMIT 30000;

SELECT DISTINCT rec_chg.* FROM recent_chg_sum AS rec_chg
    LEFT OUTER JOIN icd_umbrella AS icd ON rec_chg.entity = icd.frame
    WHERE icd.slot = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
		AND icd.short_value LIKE "%Section"
    LIMIT 30000;
