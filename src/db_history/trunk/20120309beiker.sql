
DROP TABLE IF EXISTS `beiker_reg_lottery_record`;
CREATE TABLE `beiker_reg_lottery_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `is_lottery` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否中奖',
  `lottery_content` char(100) NOT NULL DEFAULT '' COMMENT '奖品',
  `description` char(100) NOT NULL DEFAULT '' COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='注册抽奖记录';
