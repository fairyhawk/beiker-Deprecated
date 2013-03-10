-- 酒店预订
-- 前台
ALTER TABLE beiker_goods ADD is_scheduled ENUM('0','1') NOT NULL DEFAULT '0' COMMENT '是否支持预定0:否1:是';

ALTER TABLE beiker_merchant ADD scheduled_count INT(10) NOT NULL DEFAULT '0' COMMENT '可预定量';

DROP TABLE IF EXISTS `beiker_branch_scheduled_phone`;
CREATE TABLE `beiker_branch_scheduled_phone` (
   `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `branch_id` INT(11) NOT NULL COMMENT '分店ID',
   `phone` VARCHAR(30) NOT NULL COMMENT '手机号码',
   `is_send` ENUM('0','1') NOT NULL DEFAULT '0' COMMENT '是否发短信：0未发 1已发',
   `add_time` TIMESTAMP NOT NULL COMMENT '添加时间',
   PRIMARY KEY (`id`)
 ) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='分店与接收预定通知手机号映射表';

DROP TABLE IF EXISTS `beiker_scheduled_application_form`;
CREATE TABLE `beiker_scheduled_application_form` (
   `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `goods_id` INT(11) UNSIGNED NOT NULL COMMENT '商品ID',
   `branch_id` INT(11) NOT NULL COMMENT '分店ID',
   `guest_id` INT(11) NOT NULL COMMENT '商家ID',
   `trx_id` INT(11) NOT NULL COMMENT '商品订单ID',
   `person` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '预订人',
   `phone` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '预订人手机号码',
   `message` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '预订留言',
   `status` ENUM('0','1','2','3','4') NOT NULL DEFAULT '0' COMMENT '预定状态：0消费者提交 1商家接受 2商家拒绝 3消费者取消 4与商家电话沟通预定申请',
   `scheduled_consumption_datetime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '预定消费日期',
   `proposal_consumption_datetime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '建议预定消费日期',
   `createucid` INT(10) DEFAULT NULL COMMENT '创建人id',
   `createtime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '提交日期',
   `updateucid` INT(10) DEFAULT NULL COMMENT '修改人id',
   `updatetype` TINYINT(4) DEFAULT NULL COMMENT '修改人用户类型 0:网站用户 1:后台用户',
   `updatetime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新日期',
   PRIMARY KEY (`id`)
 ) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='预定申请单信息表';

DROP TABLE IF EXISTS `beiker_scheduled_application_log`;
CREATE TABLE `beiker_scheduled_application_log` (
   `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
   `scheduled_id` INT(11) UNSIGNED NOT NULL COMMENT '预定申请ID',
   `status` ENUM('0','1','2','3','4') NOT NULL DEFAULT '0' COMMENT '预定状态：0消费者提交 1商家接收 2商家拒绝 3消费者取消 4与商家电话沟通预定申请',
   `remark` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '备注',
   `createucid` INT(10) DEFAULT NULL COMMENT '创建人id',
   `createtype` TINYINT(4) DEFAULT NULL COMMENT '创建人用户类型 0:网站用户 1:后台用户',
   `add_time` TIMESTAMP NOT NULL COMMENT '添加时间',
   PRIMARY KEY (`id`)
 ) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='预定申请管理日志表';



 -- 索引
 ALTER TABLE beiker_scheduled_application_form ADD INDEX idx_trx_id(trx_id);
 
ALTER TABLE beiker_scheduled_application_form ADD INDEX idx_branch_id(branch_id);
 ALTER TABLE beiker_scheduled_application_form ADD INDEX idx_guest_id(guest_id);
ALTER TABLE beiker_scheduled_application_form ADD INDEX idx_goods_id(goods_id);
 
ALTER TABLE beiker_scheduled_application_form ADD INDEX idx_phone(phone);
  
ALTER TABLE beiker_scheduled_application_log ADD INDEX idx_scheduled_id(scheduled_id);
 
ALTER TABLE beiker_branch_scheduled_phone ADD INDEX idx_branch_id(branch_id);

-- 预订成功短信模版
INSERT INTO beiker_smstemplate SET smstitle='SMS_BOOKING_SUCCESS_MESSAGE',
smscontent='您好，您的{0}有1条新的预订申请，订单号{1}。详见商家后台并请做好后续接待工作。【千品网】',
smstype='SMS_BOOKING_SUCCESS_MESSAGE';
-- 需要确认预订短信模版
INSERT INTO beiker_smstemplate SET smstitle='SMS_BOOKING_CONFIRMED_MESSAGE',
smscontent='您好，刚刚有顾客向您提交了新的预订申请。订单号{0}为维护良好的顾客体验，请登录商家后台尽快处理。【千品网】',
smstype='SMS_BOOKING_CONFIRMED_MESSAGE';

-- 酒店预订结束
