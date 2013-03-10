
insert into beiker_smstemplate (smstitle,smscontent,smstype) values ('SMS_TRXORDER_NOTIFY_INSPECT','您在千品网购买的“{0}”（订单尾号{1}）已成功消费，您可登录www.qianpin.com评价本次消费。【千品网】','SMS_TRXORDER_NOTIFY_INSPECT');
insert into beiker_smstemplate (smstitle,smscontent,smstype) values ('SMS_TRXORDER_NOTIFY_RETURN_ACT','您在千品网购买的“{0}”（订单尾号{1}）已退款成功，退款金额为{2}元。【千品网】','SMS_TRXORDER_NOTIFY_RETURN_ACT');
insert into beiker_smstemplate (smstitle,smscontent,smstype) values ('SMS_TRXORDER_NOTIFY_RETURN_BANK','您在千品网购买的“{0}”（订单尾号{1}）已退款到支付宝/银行，退款金额为{2}元，到账时间以支付宝/银行为准。【千品网】','SMS_TRXORDER_NOTIFY_RETURN_BANK');
insert into beiker_smstemplate (smstitle,smscontent,smstype) values ('SMS_TRXORDER_NOTIFY_OVERDUE','您有订单即将到期，到期时间{0}。请您尽快登录www.qianpin.com查看。【千品网】','SMS_TRXORDER_NOTIFY_OVERDUE');

CREATE TABLE `beiker_trxorder_notify_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `notify_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '通知时间',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `notify_type` char(15) NOT NULL DEFAULT '' COMMENT '通知类型（一次性、10天、三天）',
  `biz_type` char(15) NOT NULL DEFAULT '' COMMENT '业务类型（验证、退款、过期）',
  `is_notify` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否已通知',
  `express` varchar(200) NOT NULL DEFAULT '' COMMENT '业务表达式',
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `description` varchar(200) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='订单通知记录表'


