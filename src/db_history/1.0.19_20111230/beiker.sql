DROP TABLE IF EXISTS `beiker_newprize`;

CREATE TABLE `beiker_newprize` (
  `newprize_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `newprize_name` varchar(130) NOT NULL COMMENT '抽奖奖品名称',
  `newprize_starttime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '抽奖开始时间',
  `newprize_pagetitle` varchar(24) NOT NULL DEFAULT '' COMMENT '奖品页面标题',
  `newprize_pic` varchar(64) NOT NULL DEFAULT '' COMMENT '奖品图片名称',
  `newprize_detail` varchar(20000) not null default '' COMMENT '奖品详情',
  `newprize_number` varchar(24) NOT NULL DEFAULT '' COMMENT '抽奖编号',
  PRIMARY KEY (`newprize_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `beiker_newlorry`;

CREATE TABLE `beiker_newlorry` (
  `newlorry_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(10) unsigned NOT NULL COMMENT '用户id',
  `winnumber` varchar(20) NOT NULL COMMENT '奖号',
  `iswinner` enum('0','1','2') NOT NULL DEFAULT '0' COMMENT '是否中奖:0未中奖 1中奖 2.等待开奖',
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '参与时间',
  `numbersource` varchar(40) NOT NULL DEFAULT '' COMMENT '中奖来源信息',
  `getlorrystatus` enum('1','2','3') NOT NULL DEFAULT '3' COMMENT '获得奖品来源状态:1.24小时购买商品 2.邀请注册 3.自己参与',
  `newprize_id` int(10) unsigned NOT NULL COMMENT '奖品id',
  PRIMARY KEY (`newlorry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `beiker_shorturl`;

CREATE TABLE `beiker_shorturl` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(10) unsigned NOT NULL COMMENT '用户主键',
  `shortsecret` varchar(12) NOT NULL DEFAULT '' COMMENT '短地址',
  `shortmessage` varchar(120) NOT NULL DEFAULT '' COMMENT '短地址对应内容',
  `messagetype` enum('1','2') NOT NULL DEFAULT '2' COMMENT '短地址类型:1.0元抽奖邀请注册 2.其它',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `beiker_startprize`;

CREATE TABLE `beiker_startprize` (
  `startprize_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `startprize_winnumbers` int(10) unsigned NOT NULL DEFAULT '1' COMMENT '中奖人数',
  `startprize_desc` varchar(24) NOT NULL DEFAULT '' COMMENT '种子描述',
  `startprize_seedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开奖时间',
  `startprize_title` varchar(24) NOT NULL DEFAULT '' COMMENT '开奖小标题',
  `startprize_jointnumber` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '参与人数',
  `startprize_seed` varchar(24) NOT NULL DEFAULT '' COMMENT '开奖种子',
  `prize_id` int(10) unsigned NOT NULL COMMENT '关联beiker_newprize表',
  `strartprize_status` enum('1','2') NOT NULL DEFAULT '1' COMMENT '抽奖商品状态:1：已上线/未开奖 2：已开奖',
  `startprize_number` varchar(200) NOT NULL DEFAULT '' COMMENT '奖号 多的用逗号分隔',
  PRIMARY KEY (`startprize_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `beiker_prizeinvite`;

CREATE TABLE `beiker_prizeinvite` (
  `prizeinvite_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sourceid` int(10) unsigned NOT NULL COMMENT '邀请人ID',
  `targetid` int(10) unsigned NOT NULL COMMENT '被邀请人ID',
  `inventtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '邀请时间',
  `newlorry_id` int(10) unsigned NOT NULL COMMENT '奖券记录',
  PRIMARY KEY (`prizeinvite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `beiker_card`;
CREATE TABLE `beiker_card` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `card_no` char(16) NOT NULL DEFAULT '0' COMMENT '卡号',
  `card_pwd` varchar(50) NOT NULL DEFAULT '' COMMENT '卡密',
  `card_value` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '面值(冗余)',
  `card_type` char(10) NOT NULL DEFAULT '' COMMENT '卡类型(冗余)',
  `card_status` char(10) NOT NULL DEFAULT '' COMMENT '卡状态：待印刷入库、已印刷入库、已发放未激活、已发放已激活、已使用、已过期、已废弃',
  `bacth_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '所属批次',
  `order_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '所属购卡订单',
  `topup_channel` char(10) NOT NULL DEFAULT '' COMMENT '充值渠道',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间(冗余)',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '所属虚拟款项ID(冗余)',
  `biz_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '业务ID',
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `description` char(50) NOT NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `card_pwd` (`card_pwd`),
  UNIQUE KEY `card_no` (`card_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
