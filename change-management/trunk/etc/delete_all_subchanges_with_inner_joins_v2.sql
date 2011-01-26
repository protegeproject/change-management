﻿DELIMITER $$

#************** How to run it *******************
# source ./delete_all_subchanges_with_inner_joins_v2.sql 
# call delete_subchanges('icd_ann'); - make sure you specify the right filename
#************************************************

DROP PROCEDURE IF EXISTS `protege`.`delete_subchanges` $$
CREATE DEFINER=`protege`@`%` PROCEDURE `delete_subchanges`(IN tablename CHAR(50))
BEGIN

#create copy of table to work on
DROP TABLE IF EXISTS tcck_ann2;

SET @v_command = CONCAT('CREATE TABLE tcck_ann2 AS SELECT * FROM ', tablename, ';');
PREPARE stmt3 FROM @v_command;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;

ALTER TABLE `tcck_ann2`
  ADD INDEX `tcck_ann2_I2` (`short_value`),
  ADD INDEX `tcck_ann2_I3` (`frame`);



#create temporal tables with the list of changes instances and
#timestamp instances belonging to the changes instances
#that need to be deleted from the ontology
DROP TABLE IF EXISTS all_subchanges;

CREATE TABLE all_subchanges AS
  SELECT frame AS id FROM tcck_ann2 where slot = "partOfCompositeChange";

ALTER TABLE `all_subchanges`
  ADD INDEX `all_subchanges_I1` (`id`);



DROP TABLE IF EXISTS timestamps_of_all_subchanges;

CREATE TABLE timestamps_of_all_subchanges AS
  SELECT short_value AS id FROM tcck_ann2 AS o
    INNER JOIN all_subchanges i ON i.id = o.frame
    WHERE
      o.slot = "timestamp";

ALTER TABLE `timestamps_of_all_subchanges`
  ADD INDEX `timestamps_of_all_subchanges_I1` (`id`);



#Delete all rows from the working copy
DELETE o FROM tcck_ann2 AS o
  INNER JOIN all_subchanges i ON i.id = o.frame;

DELETE o FROM tcck_ann2 AS o
  INNER JOIN all_subchanges i ON i.id = o.short_value;



DELETE o FROM tcck_ann2 AS o
  INNER JOIN timestamps_of_all_subchanges i ON i.id = o.frame;

DELETE o FROM tcck_ann2 AS o
  INNER JOIN timestamps_of_all_subchanges i ON i.id = o.short_value;



#Delete temporal tables
DROP TABLE IF EXISTS all_subchanges;

DROP TABLE IF EXISTS timestamps_of_all_subchanges;



#Rename working copy to (relate to) the original table name
SET @new_tablename = CONCAT('new_', tablename);
SET @v_command = CONCAT('DROP TABLE IF EXISTS ', @new_tablename, ';');
PREPARE stmt3 FROM @v_command;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;

SET @v_command = CONCAT('ALTER TABLE tcck_ann2 RENAME TO ', @new_tablename, ';');
PREPARE stmt3 FROM @v_command;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;


#create default Protege indexes
SET @v_command = CONCAT('ALTER TABLE `', @new_tablename, '` ',
  'DROP KEY `tcck_ann2_I2`, ',
  'DROP KEY `tcck_ann2_I3`, ',
  'ADD KEY `', tablename, '_I1` (`frame`,`slot`,`facet`,`is_template`,`value_index`), ',
  'ADD KEY `', tablename, '_I2` (`short_value`(255)), ',
  'ADD KEY `', tablename, '_I4` (`slot`,`frame_type`)');
PREPARE stmt3 FROM @v_command;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;



END $$

DELIMITER ;
