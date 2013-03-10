insert into beiker_trx_rule (trx_title,trx_rule,create_date,description) values ('FULL_LOTTERY','ACTHIS:0',now(),'满额抽奖2012五一第一期'); 

CREATE TABLE `beiker_full_lottery_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `lottery_type` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '奖抽类型(0:线上商品;1:线下商品;2:虚拟币充值)',
  `is_lottery` tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '是否中奖',
  `lottery_content` char(100) NOT NULL DEFAULT '' COMMENT '奖品',
  `description` char(100) NOT NULL DEFAULT '' COMMENT '描述',
  `city_name` char(50) NOT NULL DEFAULT '' COMMENT '城市名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='购买额满抽奖记录'

alter table beiker_cps_tuan800 drop index trx_goods_sn;
ALTER TABLE beiker_cps_tuan800 CHANGE `create_date` `create_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;