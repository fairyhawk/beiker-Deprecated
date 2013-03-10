
DROP TABLE IF EXISTS `beiker_adweb`;

CREATE TABLE `beiker_adweb` (
  `adweb_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `adweb_name` varchar(24) NOT NULL COMMENT '广告名称',
  `adweb_trxurl` varchar(400) NOT NULL COMMENT '广告产生订单回传地址',
  `adweb_code` varchar(16) NOT NULL COMMENT '广告联盟编码',
  PRIMARY KEY (`adweb_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `beiker_adweblog`;

CREATE TABLE `beiker_adweblog` (
	`adweblog_id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`adcid` VARCHAR(24) NOT NULL COMMENT 'cid',
	`adwi` VARCHAR(96) NOT NULL COMMENT 'wi',
	`adcode` VARCHAR(16) NOT NULL COMMENT '广告联盟编号',
	`access_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '访问时间',
	PRIMARY KEY (`adweblog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `beiker_adwebtrxinfo`;

CREATE TABLE `beiker_adwebtrxinfo` (
  `adwebtrx_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `adwebid` int(10) unsigned NOT NULL COMMENT 'beiker_adweb主键',
  `trxorderid` varchar(32) NOT NULL default '' COMMENT '网站交易订单号',
  `adweb_cid` varchar(24) NOT NULL DEFAULT '' COMMENT '网站联盟渠道',
  `adweb_wi` varchar(96) NOT NULL DEFAULT '' COMMENT '网站联盟标示',
  `buycount` int(10) unsigned NOT NULL DEFAULT '1' COMMENT '订单数量',
  `ordermoney` decimal(10,2) not null DEFAULT '0.00' COMMENT '订单金额',
  `ordertime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '订单时间',
  `userid` int(10) unsigned NOT NULL COMMENT 'userid',
  PRIMARY KEY (`adwebtrx_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


