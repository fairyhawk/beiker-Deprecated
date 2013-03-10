
-- 旗舰店优惠信息表

CREATE TABLE `beiker_special_offers` (
  `offers_id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '信息编号',
  `guest_id` INT(10) UNSIGNED NOT NULL COMMENT '商家编号',
  `brand_id` INT(10) NOT NULL DEFAULT '0' COMMENT '品牌编号',
  `begin_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `end_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '结束时间',
  `offers_contents` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '优惠内容',
  `offers_status` ENUM('INIT','ONLINE','OFFLINE') NOT NULL DEFAULT 'INIT' COMMENT '信息状态(INIT:初始, ONLINE:上线, OFFLINE:下线)',
  PRIMARY KEY (`offers_id`),
  KEY `idx_guest_id` (`guest_id`) USING BTREE
)  ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='旗舰店优惠信息';

ALTER TABLE `beiker_merchant`  ADD COLUMN `environment` varchar(200) NOT NULL DEFAULT '' COMMENT '店铺环境';
ALTER TABLE `beiker_merchant`  ADD COLUMN `capacity` varchar(40) NOT NULL DEFAULT '' COMMENT '接待能力';
ALTER TABLE `beiker_merchant`  ADD COLUMN `otherservice` varchar(200) NOT NULL DEFAULT '' COMMENT '其他服务';

CREATE TABLE `beiker_vip_statistics` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '用户id',
  `guest_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商家id',
  `goods_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商品id',
  `vip_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '成为vip时间',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique` (`user_id`,`guest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商家后台商家会员关系表';


CREATE TABLE `beiker_vip_statistics_month` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `guest_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商家id',
  `vip_num` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '本月新增vip数',
  `date_time` char(7) NOT NULL DEFAULT '' COMMENT '月份',
  `update_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商家后台商家会员月度表';

ALTER TABLE `beiker_trxorder_goods` ADD INDEX `comment_id` (`comment_id`);

-- 列表页筛选增加商品代金券选项

ALTER TABLE `beiker_goods`
	CHANGE COLUMN `couponcash` `couponcash` ENUM('0','1','2') NOT NULL DEFAULT '0' COMMENT '2是商品代金券1是现金券 0不是现金券' AFTER `send_rules`;

-- 财务系统更新增加商家账户入账状态
ALTER TABLE beiker_trxorder_goods   ADD COLUMN credit_status VARCHAR(20) DEFAULT '' NOT NULL