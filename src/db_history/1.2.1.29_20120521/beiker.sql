INSERT INTO beiker_smstemplate SET smstitle='USER_MOBILE_UPDATE',smstype='USER_MOBILE_UPDATE',smscontent='您绑定了新手机号{0}(用户{1})如非本人操作，请咨询客服400-186-1000（9:00-19:00）【千品网】';

CREATE TABLE `beiker_user_snap` (
	`user_id` INT(11) UNSIGNED NOT NULL,
	`email` VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'email',
	`email_isavalible` ENUM('0','1') NOT NULL DEFAULT '0' COMMENT 'email是否验证',
	`mobile` CHAR(16) NOT NULL DEFAULT '' COMMENT '手机号',
	`mobile_isavalible` ENUM('0','1') NOT NULL DEFAULT '0' COMMENT 'mobile是否验证',
	`password` CHAR(64) NOT NULL DEFAULT '' COMMENT '密码',
	`isavalible` ENUM('0','1') NOT NULL DEFAULT '0' COMMENT '是否可用',
	`customerkey` CHAR(64) NOT NULL DEFAULT '' COMMENT '用户key,自动生成',
	`createdate` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
	`actiontime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '插入时间',
	INDEX `email` (`email`(10)),
	INDEX `mobile` (`mobile`(7)),
	INDEX `createdate` (`createdate`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;



delimiter $$
CREATE TRIGGER a_u_beiker_user_update 
BEFORE
UPDATE ON beiker.beiker_user FOR EACH ROW 
BEGIN 
	IF OLD.mobile!=NEW.mobile OR OLD.email!=NEW.email THEN
		INSERT INTO beiker_user_snap SET user_id=OLD.user_id,email=OLD.email,email_isavalible=OLD.email_isavalible,
		mobile=OLD.mobile,mobile_isavalible=OLD.mobile_isavalible, PASSWORD=OLD.password,isavalible=OLD.isavalible,
		customerkey=OLD.customerkey,createdate=OLD.createdate;
	END IF;
END;
$$
DELIMITER ;

INSERT INTO `beiker_tag_property` (`id`, `tag_name`, `parentid`, `tag_enname`, `boost`) VALUES (10500, '酒店旅游', 0, 'jiudianlvyou', 48);
INSERT INTO `beiker_tag_property` (`id`, `tag_name`, `parentid`, `tag_enname`, `boost`) VALUES (10501, '旅游度假', 10500, 'lvyou', 47);
INSERT INTO `beiker_tag_property` (`id`, `tag_name`, `parentid`, `tag_enname`, `boost`) VALUES (10502, '酒店旅馆', 10500, 'jiudianbinguan', 46);
INSERT INTO `beiker_tag_property` (`id`, `tag_name`, `parentid`, `tag_enname`, `boost`) VALUES (10510, '其它', 10500, '10510', 12);

update beiker_catlog_good set tagid=10500,tagextid=10501 where area_id in(101,102,103,104,272) and tagid=10200 and tagextid=10208;

update beiker_catlog_good set tagid=10500,tagextid=10502 where area_id in(101,102,103,104,272) and tagid=10400 and tagextid=10407;

update  beiker_emailtemplate set templatecontent='您好，商家ID为{0} 商品订单号为{1}发生了{2}异常，请关注此问题。' where templatecode='MERCHANT_TRX_ERROR';