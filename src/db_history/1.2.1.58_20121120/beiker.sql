-- ----------优惠券3期begin-----------------
-- Table: beiker_coupon_activity  优惠券活动表
DROP TABLE IF EXISTS beiker_coupon_activity;
CREATE TABLE beiker_coupon_activity
(
   id                   INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
   vm_account_id        INT(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '虚拟款项ID',
   activity_name        VARCHAR(100) NOT NULL DEFAULT '' COMMENT '优惠券活动名称',
   activity_type        VARCHAR(20) NOT NULL DEFAULT '' COMMENT '优惠券活动类型(MARKETING_ONLINE:市场线上活动；MARKETING_OFFLINE:市场线下活动；OPERATING:运营活动)',
   csid                 VARCHAR(50) NOT NULL DEFAULT '' COMMENT '渠道代码（用户来源csid)',
   start_date           TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '活动开始时间',
   end_date             TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '活动结束时间',
   limit_amount         DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '金额限制，0表示不限制。否则必须大于等于该金额',
   limit_tagid          VARCHAR(50) NOT NULL DEFAULT '' COMMENT '一级属性id 限制，以分号做分割,为空说明无此限制，秒杀对应的tag_id为100',
   coupon_balance       SMALLINT(5) NOT NULL DEFAULT 0 COMMENT '优惠券面值（元为单位)',
   coupon_total_num     INT(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '优惠券张数',
   coupon_start_date    TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '优惠券开始时间',
   coupon_end_date      TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '优惠券结束时间',
   total_balance        DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '总金额',
   operator_id          INT(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '操作人',
   create_date          TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
   modify_date          TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
   description          VARCHAR(100) NOT NULL DEFAULT '' COMMENT '描述（备注)',
   version              INT(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
   PRIMARY KEY (id)
)ENGINE=INNODB  DEFAULT CHARSET=utf8 COMMENT='优惠券活动';


-- Table: beiker_trx_coupon  优惠券表（3期）                           
DROP TABLE IF EXISTS beiker_trx_coupon;
CREATE TABLE beiker_trx_coupon
(
   id                   INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
   coupon_no            CHAR(16) NOT NULL DEFAULT '' COMMENT '优惠券编号',
   coupon_pwd           VARCHAR(50) NOT NULL DEFAULT '' COMMENT '优惠券密码',
   coupon_type          TINYINT(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '优惠券类型：0线上；1线下',
   coupon_balance       DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '优惠券面值',
   coupon_status        CHAR(10) NOT NULL DEFAULT '' COMMENT '优惠券状态：INIT 初始化;BINDING:绑定;USED 已使用;TIMEOUT 过期',
   activity_id          INT(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '所属活动ID',
   user_id              INT(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户ID',
   vm_account_id        INT(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '所属虚拟款项ID(冗余)',
   is_credit_act        TINYINT(4) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否入账：0:未入账；1：已经入账',
   request_id						CHAR(25) NOT NULL DEFAULT '' COMMENT '入账请求号',
   start_date           TIMESTAMP DEFAULT '0000-00-00 00:00:00' COMMENT '生效日期',
   end_date             TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
   bind_date            TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '绑定日期',
   use_date             TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '使用日期',
   create_date          TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建日期',
   modify_date          TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改日期',
   description          VARCHAR(100) NOT NULL DEFAULT '' COMMENT '描述',
   version              INT(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
   PRIMARY KEY (id),
   UNIQUE INDEX idx_trxcoupon_no_pwd(coupon_no,coupon_pwd),
	 INDEX idx_trxcoupon_activityid (activity_id) USING BTREE,
	 INDEX idx_trxcoupon_vmid (vm_account_id) USING BTREE,
	 INDEX idx_userid (user_id) USING BTREE
)ENGINE=INNODB  DEFAULT CHARSET=utf8 COMMENT '优惠券';


ALTER TABLE beiker_payment ADD COLUMN coupon_id INT(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '优惠券ID' AFTER pro_external_id,ADD INDEX idx_payment_coupon(coupon_id) USING BTREE;
ALTER TABLE beiker_vm_account ADD COLUMN is_refund TINYINT(4) UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否支持退款，0：不支持；1：可退款' AFTER is_fund;
ALTER TABLE beiker_vm_trx_extend ADD INDEX idx_vm_account_id(vm_account_id) USING BTREE,ADD INDEX idx_trxorder_id(trxorder_id) USING BTREE;
INSERT INTO beiker_vm_account_sort (create_date,vm_account_sort) VALUES(NOW(),'优惠券三期');

-- -------------优惠券3期end----------