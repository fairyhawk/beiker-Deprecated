
-- 0723线下优惠券（交易相关）

create table beiker_discount_coupon
(
   id                   int not null auto_increment comment '主键',
   coupon_no            char(16) not null default '' comment '优惠券编号',
   coupon_pwd           varchar(50) not null default '' comment '优惠券密码',
   coupon_value         smallint(5) not null default 0 comment '优惠券面值',
   coupon_type          tinyint(4) not null default 0 comment '优惠券类型',
   coupon_status        char(10) not null default '' comment '优惠券状态：INIT 初始化;ACTIVE 激活;USED 已使用;DESTORY 废弃;TIMEOUT 过期;',
   batch_no             char(10) not null default '' comment '所属批次',
   topup_channel        char(10) not null default '' comment '充值渠道',
   user_id              int(11) not null default 0 comment '用户ID',
   create_operator_id 	int(10) NOT NULL DEFAULT '0' COMMENT '创建操作员ID',
   active_operator_id 	int(10) NOT NULL DEFAULT '0' COMMENT '激活操作员ID',
   vm_account_id        int(10) not null default 0 comment '所属虚拟款项ID(冗余)',
   biz_id               int(10) not null default 0 comment '业务ID',
   create_date          timestamp not null default '0000-00-00 00:00:00' comment '创建日期',
   modify_date          timestamp not null default '0000-00-00 00:00:00' comment '修改日期',
   active_date          timestamp not null default '0000-00-00 00:00:00' comment '激活日期',
   lose_date            timestamp not null default '0000-00-00 00:00:00' comment '过期时间(冗余)',
   description          varchar(100) not null default '' comment '描述',
   version              int(10) not null default 0 comment '乐观锁版本号',
   primary key (id)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='线下优惠券';

create unique index coupon_no_unique_index on beiker_discount_coupon
(
   coupon_no
);


create unique index coupon_pwd_unique_index on beiker_discount_coupon
(
   coupon_pwd
);

insert beiker_vm_account_sort (create_date,vm_account_sort) values (now(),'线下优惠券');

