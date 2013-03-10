
-- 记录用户登陆
CREATE TABLE `beiker_user_login_log` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标识',
	`user_id` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '用户标识',
	`user_email` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '用户邮箱',
	`login_ip` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '用户登陆IP',
	`login_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '用户登陆时间',
	PRIMARY KEY (`id`)
) 
COMMENT='用户登陆日志记录'
 ENGINE=InnoDB  DEFAULT CHARSET=utf8;
 
-- 增加用户安全手机短信模板 
INSERT INTO   beiker_smstemplate  (  smstitle, smscontent, smstype) 
VALUES ('TRX_BEFORE_CHECK_PHONE_CODE',  '您在千品网的动态支付密码是：{0}，在网站输入后即可支付。【千品网】','TRX_BEFORE_CHECK_PHONE_CODE'   ) ;
-- 订单号预取报警
insert into beiker_smstemplate (smstitle,smscontent,smstype)
  values ('TGSN_PREFETCH_ALTER_SMS_TEMPLATE','订单号预取报警：{0},请立即处理。','TGSN_PREFETCH_ALTER_SMS_TEMPLATE');

