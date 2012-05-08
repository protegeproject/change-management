DELIMITER $$

#************** How to run it *******************
# source ./delete_old_changes.sql	- make sure to specify the correct path
# call delete_old_changes('icd_ann', '2010-06-01 00:00:00');	- make sure you specify the correct table name
#************************************************

DROP PROCEDURE IF EXISTS `protege`.`delete_old_changes` $$
CREATE DEFINER=`protege`@`%` PROCEDURE `delete_old_changes`(IN tablename CHAR(50), IN start_date CHAR(19))
BEGIN

#create copy of table to work on
DROP TABLE IF EXISTS tcck_ann2;

SET @v_command = CONCAT('CREATE TABLE tcck_ann2  ENGINE = InnoDB, DEFAULT CHARACTER SET = utf8 AS SELECT * FROM ', tablename, ';');
PREPARE stmt3 FROM @v_command;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;

ALTER TABLE `tcck_ann2`
  ADD INDEX `tcck_ann2_I2` (`short_value`),
  ADD INDEX `tcck_ann2_I3` (`frame`);



#create temporal tables with the list of changes instances and
#timestamp instances belonging to the changes instances
#that need to be deleted from the ontology
DROP TABLE IF EXISTS old_changes;

#CREATE TABLE old_changes AS
#  SELECT frame AS id FROM tcck_ann2 
#  WHERE slot = 'date' 
#    AND TIMESTAMPDIFF(SECOND, STR_TO_DATE(SUBSTRING(short_value, 1, 19), '%m/%d/%Y %H:%i:%s'), '2010-06-01 00:00:00') > 0;

SET @v_command = CONCAT('CREATE TABLE old_changes AS SELECT frame AS id FROM tcck_ann2 WHERE slot = "date" AND TIMESTAMPDIFF(SECOND, STR_TO_DATE(SUBSTRING(short_value, 1, 19), "%m/%d/%Y %H:%i:%s"), "', start_date,  '") > 0;');
PREPARE stmt3 FROM @v_command;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;

ALTER TABLE `old_changes`
  ADD INDEX `old_changes_I1` (`id`);



DROP TABLE IF EXISTS timestamps_of_old_changes;

CREATE TABLE timestamps_of_old_changes AS
  SELECT short_value AS id FROM tcck_ann2 AS o
    INNER JOIN old_changes i ON i.id = o.frame
    WHERE
      o.slot = "timestamp";

ALTER TABLE `timestamps_of_old_changes`
  ADD INDEX `timestamps_of_old_changes_I1` (`id`);



#Delete all rows from the working copy
DELETE o FROM tcck_ann2 AS o
  INNER JOIN old_changes i ON i.id = o.frame;

DELETE o FROM tcck_ann2 AS o
  INNER JOIN old_changes i ON i.id = o.short_value;



DELETE o FROM tcck_ann2 AS o
  INNER JOIN timestamps_of_old_changes i ON i.id = o.frame;

DELETE o FROM tcck_ann2 AS o
  INNER JOIN timestamps_of_old_changes i ON i.id = o.short_value;



#Delete temporal tables
DROP TABLE IF EXISTS old_changes;

DROP TABLE IF EXISTS timestamps_of_old_changes;



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
  'ADD KEY `', @new_tablename, '_I1` (`frame`,`slot`,`facet`,`is_template`,`value_index`), ',
  'ADD KEY `', @new_tablename, '_I2` (`short_value`(255)), ',
  'ADD KEY `', @new_tablename, '_I4` (`slot`,`frame_type`)');
PREPARE stmt3 FROM @v_command;
EXECUTE stmt3;
DEALLOCATE PREPARE stmt3;



END $$

DELIMITER ;
