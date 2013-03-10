DELIMITER $$
SET GLOBAL log_bin_trust_function_creators = 1;
DROP FUNCTION IF EXISTS func_split_count;
CREATE FUNCTION func_split_count(
	v_text VARCHAR(2000),
	v_regex CHAR(1)
) RETURNS INT(2)
BEGIN
  RETURN 1 + (LENGTH(v_text) - LENGTH(REPLACE(v_text, v_regex, '')));
END;

DROP FUNCTION IF EXISTS func_split;  
CREATE FUNCTION func_split(  
	v_text VARCHAR(2000),
	v_regex CHAR(1),
	v_order INT(2)
) RETURNS VARCHAR(255) CHARSET utf8  
BEGIN  
	DECLARE result VARCHAR(255) DEFAULT '';  
	SET result = REVERSE(SUBSTRING_INDEX(REVERSE(SUBSTRING_INDEX(v_text, v_regex, v_order)), v_regex, 1));  
	RETURN result;  
END;
SET GLOBAL log_bin_trust_function_creators = 0;
$$
DELIMITER ;
