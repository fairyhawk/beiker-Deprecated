alter table beiker_account add  version int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号';
alter table beiker_accounthistory add  version int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号';
alter table beiker_payment add  version int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号';
alter table beiker_trxorder add  version int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号';
alter table beiker_trxorder_goods add  version int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号'; 
alter table beiker_voucher add  version int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号';
alter table beiker_refund_detail add  version int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号';
alter table beiker_refund_record add  version int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号';
alter table beiker_trxorder_goods add  is_dis tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:返现未下发 1:返现已下发';

CREATE TABLE `beiker_sub_account_0` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_sub_account_1` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_sub_account_2` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_sub_account_3` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_sub_account_4` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_sub_account_5` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_sub_account_6` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_sub_account_7` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_sub_account_8` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_sub_account_9` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项账户ID',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '余额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE `beiker_vm_account` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  `total_balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '总余额',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `vm_account_sort_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '账户类别ID',
  `is_fund` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是有金(0无，1有.默认为0)',
  `proposer` char(30) NOT NULL DEFAULT '' COMMENT '申请人',
  `cost_bear` char(30) NOT NULL DEFAULT '' COMMENT '成本承担方',
  `description` char(200) NOT NULL DEFAULT '' COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_vm_account_history` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项id',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `sub_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户子账户ID',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '发生额',
  `balance` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '当前余额',
  `request_id` varchar(50) NOT NULL DEFAULT '' COMMENT '请求号',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `vm_account_type` char(30) NOT NULL DEFAULT '' COMMENT '操作类型',
  `operator_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '操作员ID(0为系统)',
  `is_credit_act` tinyint(4) DEFAULT '0' COMMENT '是否出入账（对用户帐务）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_vm_account_sort` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `vm_account_sort` char(50) NOT NULL DEFAULT '' COMMENT '虚拟款项类别',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_vm_cancel_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项id',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `sub_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户子账户ID',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '发生额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `operator_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '操作员ID(0为系统)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `beiker_vm_trx_extend` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '虚拟款项ID',
  `account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户账户ID',
  `sub_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户子账户ID',
  `trxorder_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '交易订单ID',
  `biz_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '业务ID',
  `is_credit_act` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '是否入账',
  `payment_amount` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '支付记录金额',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '发生额',
  `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间(冗余)',
  `trx_request_id` varchar(30) NOT NULL DEFAULT '' COMMENT '交易订单号',
  `relevance_type` char(30) NOT NULL DEFAULT '' COMMENT '关联类型',
  `description` char(200) NOT NULL DEFAULT '' COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


insert into beiker_vm_account_sort (create_date,vm_account_sort) value ('2011-12-03 00:00:00','全站返现');
insert into beiker_vm_account_sort (create_date,vm_account_sort) value ('2011-12-03 00:00:00','员工激励');
insert into beiker_vm_account_sort (create_date,vm_account_sort) value ('2011-12-03 00:00:00','K计划');
insert into beiker_vm_account_sort (create_date,vm_account_sort) value ('2011-12-03 00:00:00','运营活动');

insert into  beiker_vm_account(create_date,update_date,lose_date,total_balance,balance,vm_account_sort_id,is_fund,proposer,cost_bear,description) value ('2011-12-03 00:00:00','2011-12-03 00:00:00','2012-08-18 00:00:00',1000000,1000000,1,0,'系统','运营部2012年运营成本','2011下半年全站返现');


insert into beiker_vm_account_history (vm_account_id,account_id,sub_account_id,amount,balance,create_date,vm_account_type,operator_id,is_credit_act) values 
(1,0,0,1000000,1000000,'2011-12-03 00:00:00','CREATE','0','1');



insert into  beiker_vm_account(create_date,update_date,lose_date,total_balance,balance,vm_account_sort_id,is_fund,proposer,cost_bear,description) value ('2011-12-03 00:00:00','2011-12-03 00:00:00','2012-02-18 00:00:00',200000,200000,2,0,'1212历史','1212历史','1212历史');



insert into beiker_vm_account_history (vm_account_id,account_id,sub_account_id,amount,balance,create_date,vm_account_type,operator_id,is_credit_act) values 
(2,0,0,200000,200000,'2011-12-03 00:00:00','CREATE','0','1');

INSERT INTO beiker_emailtemplate(templatecode,templatecontent,templatesubject)  VALUES ( 'DEBIT_VCACT_EMAIL_ALERT_TEMP', '主人,账户为ID{0}的账户,在发生交易金额{1}时子账户扣款异常.', '子账户扣款异常报警');



alter table beiker_sub_account_0 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';
alter table beiker_sub_account_1 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';
alter table beiker_sub_account_2 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';
alter table beiker_sub_account_3 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';
alter table beiker_sub_account_4 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';
alter table beiker_sub_account_5 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';
alter table beiker_sub_account_6 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';
alter table beiker_sub_account_7 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';
alter table beiker_sub_account_8 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';
alter table beiker_sub_account_9 add  is_lose tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0:未过期 1:已过期';


ALTER TABLE beiker_merchant ADD COLUMN domainname  VARCHAR(24) NOT NULL DEFAULT '' COMMENT '店铺域名';
ALTER TABLE beiker_merchant ADD COLUMN salescountent VARCHAR(4000) NOT NULL DEFAULT '' COMMENT '消费者说';
ALTER TABLE beiker_merchant ADD COLUMN ownercontent  VARCHAR(160) NOT NULL DEFAULT '' COMMENT '店长说';
ALTER TABLE beiker_goods ADD COLUMN  couponcash ENUM('0','1') NOT NULL DEFAULT '0' COMMENT '1是现金券 0不是现金券';
ALTER TABLE beiker_merchant ADD COLUMN csstemplatename  VARCHAR(16) COMMENT 'CSS模板文件名';
ALTER TABLE beiker_goods ADD COLUMN kindlywarnings VARCHAR(2000) NOT NULL DEFAULT ''  COMMENT '温馨提示'; 


INSERT INTO beiker_emailtemplate (`templatecode`, `templatecontent`, `templatesubject`) 
VALUES('FEEDBACK','用户称呼：{0}<br/>用户联系方式：{1}<br/>用户意见或建议：{2}','用户建议反馈');




update beiker_region_property brp set  brp.region_name='近郊其它地区' where brp.id=28600605 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='天桥其它地区' where brp.id=23700406 and brp.region_name='其它地区';	
update beiker_region_property brp set  brp.region_name='越秀其它地区' where brp.id=10300214 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='昌平其它地区' where brp.id=10101104 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='丰台其它地区' where brp.id=10100712 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='长清其它地区' where brp.id=23700701 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='近郊其它地区' where brp.id=23700805 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='汉阳其它地区' where brp.id=27200504 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='闵行其它地区' where brp.id=10200511 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='白云其它地区' where brp.id=10300510 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='崇文其它地区' where brp.id=10100607 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='卢湾其它地区' where brp.id=10200105 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='宝山其它地区' where brp.id=10201205 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='江岸其它地区' where brp.id=27200106 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='南山其它地区' where brp.id=10400308 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='黄浦其它地区' where brp.id=10200707 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='开福其它地区' where brp.id=28600304 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='石景山其它地区' where brp.id=10100806 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='近郊其它地区' where brp.id=10101208 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='历下其它地区' where brp.id=23700109 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='武昌其它地区' where brp.id=27200208 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='海珠其它地区' where brp.id=10300309 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='东西湖其它地区' where brp.id=27200803 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='徐汇其它地区' where brp.id=10200212 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='西城其它地区' where brp.id=10100311 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='海淀其它地区' where brp.id=10100417 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='东城其它地区' where brp.id=10100211 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='盐田其它地区' where brp.id=10400404 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='岳麓其它地区' where brp.id=28600504 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='浦东新其它地区' where brp.id=10200617 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='虹口其它地区' where brp.id=10201010 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='高新其它地区' where brp.id=23700603 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='松江其它地区' where brp.id=10201305 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='大兴其它地区' where brp.id=10100904 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='罗湖其它地区' where brp.id=10400208 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='青山其它地区' where brp.id=27200603 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='嘉定其它地区' where brp.id=10201405 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='芙蓉其它地区' where brp.id=28600106 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='荔湾其它地区' where brp.id=10300405 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='闸北其它地区' where brp.id=10200906 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='近郊其它地区' where brp.id=10201604 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='静安其它地区' where brp.id=10200305 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='市中其它地区' where brp.id=23700207 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='洪山其它地区' where brp.id=27200705 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='宝安其它地区' where brp.id=10400503 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='江汉其它地区' where brp.id=27200306 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='历城其它地区' where brp.id=23700506 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='雨花其它地区' where brp.id=28600405 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='朝阳其它地区' where brp.id=10100124 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='天心其它地区' where brp.id=28600203 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='龙岗其它地区' where brp.id=10400603 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='槐荫其它地区' where brp.id=23700306 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='普陀其它地区' where brp.id=10200811 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='宣武其它地区' where brp.id=10100509 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='福田其它地区' where brp.id=10400111 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='杨浦其它地区' where brp.id=10201107 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='天河其它地区' where brp.id=10300118 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='番禺其它地区' where brp.id=10300604 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='通州其它地区' where brp.id=10101004 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='天河其它地区' where brp.id=10300113 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='硚口其它地区' where brp.id=27200404 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='长宁其它地区' where brp.id=10200410 and brp.region_name='其它地区';
update beiker_region_property brp set  brp.region_name='近郊其它地区' where brp.id=10300707 and brp.region_name='其它地区';



ALTER TABLE beiker_tag_property ADD boost TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序次序';
update beiker_tag_property btp set btp.boost=100 where btp.tag_name='美食';
update beiker_tag_property btp set btp.boost=60 where btp.tag_name='休闲娱乐';
update beiker_tag_property btp set btp.boost=40 where btp.tag_name='丽人';
update beiker_tag_property btp set btp.boost=20 where btp.tag_name='生活服务';

update beiker_tag_property btp set btp.boost=99 where btp.tag_name='川菜';
update beiker_tag_property btp set btp.boost=98 where btp.tag_name='地方菜';
update beiker_tag_property btp set btp.boost=97 where btp.tag_name='日韩料理';
update beiker_tag_property btp set btp.boost=96 where btp.tag_name='湘菜';
update beiker_tag_property btp set btp.boost=95 where btp.tag_name='咖啡厅';
update beiker_tag_property btp set btp.boost=94 where btp.tag_name='麻辣香锅';
update beiker_tag_property btp set btp.boost=93 where btp.tag_name='烤鱼';
update beiker_tag_property btp set btp.boost=92 where btp.tag_name='自助餐';
update beiker_tag_property btp set btp.boost=91 where btp.tag_name='面包甜点';
update beiker_tag_property btp set btp.boost=90 where btp.tag_name='火锅';
update beiker_tag_property btp set btp.boost=89 where btp.tag_name='北京菜';
update beiker_tag_property btp set btp.boost=88 where btp.tag_name='小吃快餐';
update beiker_tag_property btp set btp.boost=87 where btp.tag_name='西餐';
update beiker_tag_property btp set btp.boost=86 where btp.tag_name='东北菜';
update beiker_tag_property btp set btp.boost=85 where btp.tag_name='新疆菜';
update beiker_tag_property btp set btp.boost=84 where btp.tag_name='粤菜';
update beiker_tag_property btp set btp.boost=83 where btp.tag_name='海鲜';
update beiker_tag_property btp set btp.boost=82 where btp.tag_name='其它';


update beiker_tag_property btp set btp.boost=59 where btp.tag_name='运动健身';
update beiker_tag_property btp set btp.boost=58 where btp.tag_name='电影院';
update beiker_tag_property btp set btp.boost=57 where btp.tag_name='KTV';
update beiker_tag_property btp set btp.boost=56 where btp.tag_name='温泉洗浴';
update beiker_tag_property btp set btp.boost=55 where btp.tag_name='养生按摩';
update beiker_tag_property btp set btp.boost=54 where btp.tag_name='酒吧';
update beiker_tag_property btp set btp.boost=53 where btp.tag_name='展览演出';
update beiker_tag_property btp set btp.boost=52 where btp.tag_name='旅游度假';
update beiker_tag_property btp set btp.boost=51 where btp.tag_name='茶馆';
update beiker_tag_property btp set btp.boost=50 where btp.tag_name='游乐游艺';
update beiker_tag_property btp set btp.boost=49 where btp.tag_name='其它';


update beiker_tag_property btp set btp.boost=39 where btp.tag_name='美容/SPA';
update beiker_tag_property btp set btp.boost=38 where btp.tag_name='美甲';
update beiker_tag_property btp set btp.boost=37 where btp.tag_name='美发';
update beiker_tag_property btp set btp.boost=36 where btp.tag_name='瑜伽/舞蹈';
update beiker_tag_property btp set btp.boost=35 where btp.tag_name='其它';

update beiker_tag_property btp set btp.boost=19 where btp.tag_name='医疗健康';
update beiker_tag_property btp set btp.boost=18 where btp.tag_name='汽车服务';
update beiker_tag_property btp set btp.boost=17 where btp.tag_name='酒店旅馆';
update beiker_tag_property btp set btp.boost=16 where btp.tag_name='教育/培训';
update beiker_tag_property btp set btp.boost=15 where btp.tag_name='摄影';
update beiker_tag_property btp set btp.boost=14 where btp.tag_name='宠物';
update beiker_tag_property btp set btp.boost=13 where btp.tag_name='婚嫁';
update beiker_tag_property btp set btp.boost=12 where btp.tag_name='其它';


update beiker_goods bg set bg.couponcash='1' where bg.goodsid in(15213,15280,15675,15583,15587,15588,15212,15581,15627,15193,15192,15224,15127,15366,15254,15330,15355,15519,15214,15253,15591,15733,15606,15731,15777,15784,15781,15594,15596,15787,15244,15786,15373,15108,15352,15241,15242,15067,15065,15535,15534,15369,15393,15679,15678,15455,15721,15618,15801,15881,15359,16139,15840,16078,16170,16150,16146,16175,15518,16003,16177,16204,16157,16302,16094,16436,16320,16030,16406,16045,16495,16232,16136,16446,16449,16540,16523,16521,16520,16509,16506,16504,16503,16499,16494,16471,16455,16546,16552,16579,16580,16583,16584,16612,15129,15132,15266,15268,15337,15431,15509,15549,15593,15626,15647,15713,15743,15802,15824,15828,15829,15833,15838,15842,15857,15859,15867,15883,15893,15895,15904,15948,15950,15971,15987,16017,16064,16068,16080,16092,16143,16144,16148,16159,16192,16201,16211,16275,16277,16330,16336,16338,16341,16374,16375,16378,16409,16411,16416,16481,16611,16628,16643,15170,16664,16665,16676,16689,16774,16730,16733,16745,16748,16743,16789,16797,16798,16829,16854,16874,16905,16908,16931,16925,16935);
