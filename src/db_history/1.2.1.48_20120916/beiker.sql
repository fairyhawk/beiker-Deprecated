-- 非活动商品
CREATE TABLE `beiker_goods_activity` (
	`act_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '活动id',
	`goods_id` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商品id',
	`create_date` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '活动创建时间',
	PRIMARY KEY (`act_id`)
)
COMMENT='非活动商品'
ENGINE=InnoDB  DEFAULT CHARSET=utf8;



-- 不找零需求SQL


alter table beiker_vm_cancel_record add cancel_type varchar(20)  COMMENT '取消类型' AFTER amount;


alter table beiker_vm_account add not_change_rule varchar(20) NOT NULL DEFAULT '' COMMENT '找零规则'  AFTER is_fund;


alter table beiker_vm_account add is_not_change tinyint(4) unsigned NOT NULL DEFAULT '0' COMMENT '是否不找零' AFTER is_fund;








-- ############京东分销商SQL###################
DROP TABLE IF EXISTS beiker_partner_bind_voucher;

-- table: beiker_partner_bind_voucher
CREATE TABLE beiker_partner_bind_voucher
(
   id                   int NOT NULL AUTO_INCREMENT COMMENT '主键',
   trxorder_id          int(11) NOT NULL DEFAULT 0 COMMENT '交易订单ID',
   trx_goods_id         int(11) NOT NULL DEFAULT 0 COMMENT '商品订单ID',
   voucher_id           int(11) NOT NULL DEFAULT 0 COMMENT '凭证ID',
   partner_no           varchar(100) NOT NULL DEFAULT '' COMMENT '分销商编号',
   out_request_id       varchar(50) NOT NULL DEFAULT '' COMMENT '外部交易请求号(对分销商)',
   trx_goods_sn         varchar(50) NOT NULL DEFAULT '' COMMENT '商品订单序列号',
   voucher_code         varchar(32) NOT NULL DEFAULT '' COMMENT '凭证码',
   out_coupon_id        varchar(50) NOT NULL DEFAULT '' COMMENT '分销商券ID',
   out_coupon_pwd       varchar(50) NOT NULL DEFAULT '' COMMENT '分销商券密码',
   create_date          timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建日期',
   modify_date          timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改日期',
   PRIMARY KEY (id),
   UNIQUE INDEX beiker_partner_bind_voucher_couponid_index (partner_no,out_coupon_id),
   INDEX beiker_partner_bind_voucher_voucherid_index (voucher_id)
)COMMENT='分销商券绑定关系表'  ENGINE=InnoDB  DEFAULT CHARSET=utf8;


INSERT INTO beiker_partner (partner_no,key_value,user_id,is_available,partner_name,api_type,sub_name,create_date,update_date,session_key) VALUES ('9523','qianpin012345678',101585069,1,'京东','BUY360','京东',now(),now(),'qianPIN012345678');
