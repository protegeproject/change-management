/* This file contains a list of queries and instructions for how to use those queries 
   and convert it to an update statement, in order to modify the author of a large number 
   of ICD ChAO entries.
   In this example we try to replace the author of the changes that contain a message 
   starting with "Automatic import of the isGrouping" 
   from "ttania" (or whatever it is currently) with "WHO" */


/* 1. select all changes that have a particular type of message */
SELECT * FROM icd_ann 
    WHERE slot = "context" AND short_value LIKE "Automatic import of the isGrouping%" 
    ORDER BY frame;
    
/* 2. and count them */
SELECT count(*) FROM icd_ann 
    WHERE slot = "context" AND short_value LIKE "Automatic import of the isGrouping%";
    
    
/* 3. select the author entries of all changes that have a particular type of message */
SELECT * FROM icd_ann 
    WHERE slot = "author" AND
        frame in (
        	SELECT frame FROM icd_ann 
        	WHERE slot = "context" AND short_value LIKE "Automatic import of the isGrouping%") ORDER BY frame;
 

/* 4. and count them */
SELECT count(*) FROM icd_ann 
    WHERE slot = "author" AND 
    	  short_value="ttania" AND	/*this is optional - try it both with and without it, and see that you get the same result. Replace "ttania" with the "old" author that you want to change */
        frame in (
        	SELECT frame FROM icd_ann 
        	WHERE slot = "context" AND short_value LIKE "Automatic import of the isGrouping%");

/* 5. Count the already existing changes having the "new" author */
SELECT count(*) FROM icd_ann 
    WHERE slot = "author" AND short_value="WHO";

    
/* convert query 3. to an update statement, such as:        	
CREATE TABLE tmp AS
    SELECT frame FROM icd_ann 
        	WHERE slot = "context" AND short_value LIKE "Automatic import of the isGrouping%";

ALTER TABLE `tmp`
  ADD INDEX `tmp_I1` (`frame`);
  
  
UPDATE icd_ann SET short_value="WHO"
    WHERE slot = "author" AND
        frame in (SELECT frame FROM tmp);
        
DROP TABLE tmp;
        
   After running the update script rerunning query 5. should return the SUM OF THE PREVIOUS RESULTS of query 4 and query 5.
 */
	