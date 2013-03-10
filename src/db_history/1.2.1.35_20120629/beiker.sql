
---淘宝/58
CREATE TABLE `beiker_partner` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `partner_no` varchar(100) NOT NULL DEFAULT '' COMMENT '分销商接口编号',
  `key_value` varchar(100) NOT NULL DEFAULT '' COMMENT '密钥',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '对应的用户ID(防止分销商抽风，频繁切换预付方式)',
  `is_available` tinyint(4) unsigned NOT NULL DEFAULT '1' COMMENT '是否有效（默认为1：有效）',
  `partner_name` varchar(100) NOT NULL DEFAULT '' COMMENT '分销商名称',
  `trx_express` varchar(255) NOT NULL DEFAULT '' COMMENT '交易表达式，示例：IS_ADV:1;TRX_DIV_FEE:5%（是否预存：是；分销商分成比例：5%）',
  `api_type` varchar(20) NOT NULL DEFAULT '' COMMENT 'api类型（TC58；NOMAL常规及千品定制的接口api）',
  `sub_name` varchar(30) NOT NULL DEFAULT '' COMMENT '分销商简称',
  `sms_express` varchar(255) NOT NULL DEFAULT '' COMMENT '短信发送表达式',
  `version` int(10) NOT NULL DEFAULT '0' COMMENT ' 乐观锁版本号',
  `ip` varchar(500) NOT NULL DEFAULT '' COMMENT '合法IP,示例：202.1.120.1,201.123.4.1-100,201.23.1.*',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `description` char(255) NOT NULL DEFAULT '' COMMENT '描述信息',
  `sessian_key` varchar(100) NOT NULL DEFAULT '' COMMENT '淘宝提供，一年有效期',
  `notice_key_value` varchar(100) NOT NULL DEFAULT '' COMMENT '宝淘开放平台密钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='分销商'


CREATE TABLE `beiker_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `host_no` varchar(100) NOT NULL COMMENT '宿主编号。如：分销商接口编号；集群内部业务编号',
  `notice_type` varchar(100) NOT NULL DEFAULT '' COMMENT '通知类型。如：PARTNER为分销商；CLUSTER为内部服务器集群',
  `request_id` varchar(50) NOT NULL DEFAULT '' COMMENT '请求号',
  `content` text NOT NULL COMMENT '通知内容',
  `count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '已通知次数',
  `method_type` varchar(100) NOT NULL DEFAULT '' COMMENT '接口类型',
  `status` char(10) NOT NULL DEFAULT '' COMMENT 'INIT:新建;PROCESSING:处理中;FAIL:失败;SUCCESS:成功;',
  `rsp_msg` text NOT NULL COMMENT '响应信息',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建日期',
  `modify_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改日期',
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `token` varchar(50) NOT NULL DEFAULT '' COMMENT '唯一标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='接口补单（回调）表'


alter table beiker_trxorder add  out_request_id varchar(50) NOT NULL DEFAULT '' COMMENT '外部交易请求号(对分销商)' after request_id;

alter table beiker_trxorder add  mobile char(20)  NOT NULL DEFAULT '' COMMENT  '手机号(对分销商)' after extend_info;


alter table beiker_trxorder_goods add  out_goods_id   int(11) unsigned NOT NULL DEFAULT '0' COMMENT '分销商goods_id'  after goods_id;

insert into beiker_user (user_id,email,email_isavalible,mobile,mobile_isavalible,isavalible,createdate) values(100000001,'taobao@qianpin.com',1,'100000001',1,1,'now()');

insert into beiker_user (user_id,email,email_isavalible,mobile,mobile_isavalible,isavalible,createdate) values(100000002,'58tc@ianpin.com',1,'100000002',1,1,'now()');

insert into beiker_partner(partner_no,key_value,user_id,is_available,partner_name,trx_express,api_type,sub_name,sms_express,create_date,update_date,description,sessian_key,notice_key_value) values 
(884258008,'ad956b4b0ee6427085424eecf6c8dd85',100000001,1,'淘宝','','TAOBAO','淘宝','{"send_sms_temlate":"PARTAOBAO_VOUCHERDISPATCH","resend_sms_temlate":"PARTAOBAO_VOUCHERDISPATCH","rfund_sms_template":"0","verify_sms_tempalte":"0"}',now(),now(),'21000693','61003109a7dca733c11a4b64080dd448f673b147daf0f87884258008','312e8612eb3ea438433b044a5a3e4a3a')



insert into beiker_account(id,create_date,last_update_date,user_id,account_type,account_status) values (100000001,now(),now(),100000001,'CASH','ACTIVE');

insert into beiker_account(id,create_date,last_update_date,user_id,account_type,account_status) values (100000002,now(),now(),100000001,'VC','ACTIVE');

insert into beiker_account(id,create_date,last_update_date,user_id,account_type,account_status) values (100000003,now(),now(),100000002,'CASH','ACTIVE');

insert into beiker_account(id,create_date,last_update_date,user_id,account_type,account_status) values (100000004,now(),now(),100000002,'VC','ACTIVE');

insert into beiker_smstemplate(smstitle,smscontent,smstype) values ('PARTAOBAO_VOUCHERDISPATCH','淘宝&千品合作团购:您购买的“{0}”,订单号{1}服务密码{2},有效期至{3}.【千品网】','PARTAOBAO_VOUCHERDISPATCH')

insert into beiker_smstemplate(smstitle,smscontent,smstype) values ('PAR58TC_VOUCHERDISPATCH','58同城&千品合作团购:您购买的“{0}”,订单号{1}服务密码{2},请于{3}前致电商家预约消费.【千品网】','PAR58TC_VOUCHERDISPATCH')

update beiker_smstemplate set smscontent='您在千品网账户中有余额即将到期，到期时间{0}。请您登录账户查看并尽快消费。www.qianpin.com。【千品网】'  where smstitle='SMSACCOUNTNOTIFY_THREE'

update beiker_smstemplate set smscontent='您在千品网账户中有余额即将到期，到期时间{0}。请您登录账户查看并尽快消费。www.qianpin.com。【千品网】'  where smstitle='SMSACCOUNTNOTIFY_THIRTY'

update beiker_smstemplate set smscontent='您在千品网购买的“{0}”（订单尾号{1}）已成功消费，快来“我的千品”，在订单中评价本次消费吧！【千品网】'  where smstitle='SMS_TRXORDER_NOTIFY_INSPECT'

update beiker_smstemplate set smscontent='您购买的“{0}”（订单尾号{1}）已退款到支付宝/银行，退款{2}元，到账时间以支付宝/银行为准。400-186-1000【千品网】'  where smstitle='SMS_TRXORDER_NOTIFY_RETURN_BANK'


