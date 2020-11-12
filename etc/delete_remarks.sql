/* These queries are to retrieve (and with a slight modification to DELETE)
   all rows in icd_umbrella about ICF remarks. */

CREATE VIEW remarkTerms AS
	SELECT short_value FROM icd_umbrella 
		WHERE slot = "http://who.int/icd#remark";

SELECT short_value FROM remarkTerms;


/* replace "SELECT *" with "DELETE" to delete the matching rows */
-- DELETE
SELECT * FROM icd_umbrella
	WHERE frame IN (SELECT short_value FROM remarkTerms)
		OR
        short_value IN (SELECT short_value FROM remarkTerms);


DROP VIEW remarkTerms;


SELECT * FROM icd_umbrella
WHERE short_value = "http://who.int/icd#RemarkTerm";

