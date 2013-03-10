DROP TABLE IF EXISTS `beiker_account_notify_record`;
CREATE TABLE `beiker_account_notify_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '账户id',
  `sub_account_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '子账户ID',
  `user_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `notify_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '通知时间',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  `lose_balance` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '过期金额',
  `notify_type` char(15) NOT NULL DEFAULT '' COMMENT '通知时间点类型（30DAY,3DAY）',
  `is_notify` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否通知',
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `description` char(50) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='余额过期通知记录';


insert into beiker_smstemplate (smstitle,smscontent,smstype) values ('SMSACCOUNTNOTIFY_THREE','您在千品网的余额中，有{0}元将于近期过期，请尽快使用，如已使用请忽略。吃喝玩乐1折享用就在千品。(千品网)','SMSACCOUNTNOTIFY_THREE');

insert into beiker_smstemplate (smstitle,smscontent,smstype) values ('SMSACCOUNTNOTIFY_THIRTY','您的千品网余额中，有{0}元将于{1}过期，请尽快使用，如已使用请忽略，吃喝玩乐1折享用就在千品。(千品网)','SMSACCOUNTNOTIFY_THIRTY');
