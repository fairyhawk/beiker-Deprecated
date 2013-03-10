DELIMITER $$
DROP PROCEDURE IF EXISTS 2bzhj;
CREATE PROCEDURE `2bzhj`()
BEGIN
	DECLARE done int(1) DEFAULT 0;

	DECLARE v_count INT(2) DEFAULT '0';
	DECLARE v_goodsid int(10) DEFAULT '0';
	DECLARE v_kindlywarnings VARCHAR(2000) DEFAULT '';
	DECLARE v_index INT(2) DEFAULT '0';
	DECLARE v_kindly VARCHAR(255) DEFAULT '';

	DECLARE cur CURSOR FOR SELECT goodsid, kindlywarnings, func_split_count(kindlywarnings, '|') FROM beiker_goods;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
	OPEN cur;
	REPEAT   
		FETCH cur INTO v_goodsid, v_kindlywarnings, v_count;
		SET v_index = 1;
		IF v_goodsid <> 0
		THEN
			WHILE v_count >= v_index
			DO
				SELECT func_split(v_kindlywarnings, '|', v_index) INTO v_kindly FROM DUAL;
				IF TRIM(v_kindly) <> ''
				THEN
					INSERT INTO beiker_goods_kindly(goods_id, kindlywarnings, create_time) VALUES(v_goodsid, v_kindly, NOW());
				END IF;
				SET v_index = v_index + 1, v_kindly = '';
			END WHILE;
		END IF;		
		SET v_goodsid = v_count = 0, v_kindlywarnings = '';
	UNTIL done END REPEAT;
	CLOSE cur; 
END;
$$
DELIMITER ;