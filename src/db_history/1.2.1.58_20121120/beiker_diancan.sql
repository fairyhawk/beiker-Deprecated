CREATE TABLE beiker_online_order (
	order_id int(10) unsigned NOT NULL COMMENT '活动编号',
	guest_id int(10) unsigned NOT NULL COMMENT '商家id',
	order_sn char(8) NOT NULL COMMENT '活动流水号',
	order_start_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '活动起始时间',
	order_end_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '活动结束时间',
	discount_engine enum('OVERALLFOLD', 'FULLLESS', 'INTERVALLESS') NOT NULL COMMENT '打折引擎(OVERALLFOLD:全场折扣 FULLLESS:满额减 INTERVALLESS:区间减)',
	order_explain varchar(500) NOT NULL DEFAULT '' COMMENT '活动说明',
	audit_status enum('TOONLINE', 'ONLINE', 'OFFLINE') NOT NULL DEFAULT 'TOONLINE' COMMENT '活动状态(TOONLINE:未上线, ONLINE:上线, OFFLINE:下线)',
	settle_discount decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商家结算折扣',
	createucid int(10) unsigned NOT NULL COMMENT '创建人',
	createtime timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
	onlineucid int(10) unsigned NOT NULL DEFAULT '0' COMMENT '上线操作人',
	onlinetime timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '上线时间',
	updateucid int(10) unsigned NOT NULL DEFAULT '0' COMMENT '下线操作人',
	updatetime timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '下线时间',
	PRIMARY KEY (order_id),
	KEY idx_guest_id(guest_id),
	UNIQUE KEY idx_order_sn(order_sn)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网上点餐活动信息';

CREATE TABLE beiker_engine_overallfold (
	engine_id int(10) unsigned NOT NULL COMMENT '引擎编号',
	order_id int(10) unsigned NOT NULL COMMENT '活动编号',
	discount decimal(4, 2) NOT NULL DEFAULT '0.00' COMMENT '折扣',
	PRIMARY KEY (engine_id),
	UNIQUE KEY idx_order_id(order_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='打折引擎-全场折扣';

CREATE TABLE beiker_engine_fullless (
	engine_id int(10) unsigned NOT NULL COMMENT '引擎编号',
	order_id int(10) unsigned NOT NULL COMMENT '活动编号',
	full_amount decimal(10, 2) NOT NULL DEFAULT '0.00' COMMENT '满额',
	less_amount decimal(10, 2) NOT NULL DEFAULT '0.00' COMMENT '减额',
	PRIMARY KEY (engine_id),
	UNIQUE KEY idx_order_id(order_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='打折引擎-满额减';

CREATE TABLE beiker_engine_intervalless (
	engine_id int(10) unsigned NOT NULL COMMENT '引擎编号',
	order_id int(10) unsigned NOT NULL COMMENT '活动编号',
	interval_amount decimal(10, 2) NOT NULL DEFAULT '0.00' COMMENT '区间额',
	less_amount decimal(10, 2) NOT NULL DEFAULT '0.00' COMMENT '减额',
	PRIMARY KEY (engine_id),
	KEY idx_order_id(order_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='打折引擎-区间减';

CREATE TABLE beiker_order_menu (
	menu_id int(10) unsigned NOT NULL COMMENT '菜品编号',
	order_id int(10) unsigned NOT NULL COMMENT '活动编号',
	menu_category varchar(200) NOT NULL DEFAULT '' COMMENT '菜品类目',
	menu_name varchar(200) NOT NULL DEFAULT '' COMMENT '菜品名称',
	menu_price decimal(10, 2) NOT NULL DEFAULT '0.00' COMMENT '菜品价格',
	menu_unit varchar(10) NOT NULL DEFAULT '' COMMENT '菜品单位',
	menu_sort smallint unsigned NOT NULL DEFAULT '0' COMMENT '菜品排序',
	menu_logo varchar(200) NOT NULL DEFAULT '' COMMENT '菜品图片',
	menu_explain varchar(500) NOT NULL DEFAULT '' COMMENT '菜品备注',
	PRIMARY KEY (menu_id),
	KEY idx_order_id(order_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网上订餐菜品信息';

CREATE TABLE beiker_order_guest_map (
	id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
	order_id int(10) unsigned NOT NULL COMMENT '活动编号',
	guest_id int(10) unsigned NOT NULL COMMENT '商家编号',
	guest_settle enum('TRUE','FALSE') NOT NULL DEFAULT 'FALSE' COMMENT '结算给该分店(商家)(FALSE:否, TRUE:是)',
	PRIMARY KEY (id),
	UNIQUE KEY idx_order_guest(order_id, guest_id),
	KEY idx_guest_id(guest_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='网上订餐-店铺关联信息';








CREATE TABLE beiker_takeaway(
	takeaway_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '外卖单编号',
	branch_id int(10) unsigned NOT NULL COMMENT '分店编号',
	takeaway_phone varchar(20) NOT NULL DEFAULT '' COMMENT '外卖电话',
	delivery_area varchar(100) NOT NULL DEFAULT '' COMMENT '送餐范围',
	start_amount decimal(10, 2) NOT NULL DEFAULT '0.00' COMMENT '起送金额',
	takeaway_time varchar(50) NOT NULL DEFAULT '' COMMENT '外卖时间',
	business_address varchar(200) NOT NULL DEFAULT '' COMMENT '商家地址',
	other_explain varchar(2000) NOT NULL DEFAULT '' COMMENT '其它说明',
	menu_type enum('W', 'T') NOT NULL DEFAULT 'W' COMMENT '菜单类型(T:图片版 W:文字版)',
	menu_logo varchar(200) NOT NULL DEFAULT '' COMMENT '菜单图片(对应T类型)',
	takeaway_status enum('TOONLINE', 'ONLINE', 'OFFLINE') NOT NULL DEFAULT 'TOONLINE' COMMENT '外卖单状态(TOONLINE:未上线, ONLINE:上线 OFFLINE:下线)',
	takeaway_file varchar(200) NOT NULL DEFAULT '' COMMENT '外卖附件文件',
	PRIMARY KEY (takeaway_id),
	UNIQUE KEY idx_branch_id(branch_id),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分店外卖信息';

CREATE TABLE beiker_takeaway_menu (
	menu_id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '外卖菜单编号',
	takeaway_id int(10) unsigned NOT NULL COMMENT '外卖单编号',
	branch_id int(10) unsigned NOT NULL COMMENT '分店编号',
	menu_category varchar(200) NOT NULL DEFAULT '' COMMENT '菜品类目',
  menu_name varchar(200) NOT NULL DEFAULT '' COMMENT '菜品名称',
  menu_price varchar(50) NOT NULL DEFAULT '' COMMENT '菜品价格',
  menu_unit varchar(10) NOT NULL DEFAULT '' COMMENT '菜品单位',
  menu_sort smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '菜品排序',
	PRIMARY KEY (menu_id),
	KEY idx_takeaway_id(takeaway_id),
	KEY idx_branch_id(branch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分店外卖菜单信息';


ALTER TABLE beiker_merchant ADD `is_support_takeaway` enum('1','0') NOT NULL DEFAULT '0' COMMENT '是否支持外卖 1.支持 0.不支持';
ALTER TABLE beiker_merchant ADD `is_support_online_meal` enum('1','0') NOT NULL DEFAULT '0' COMMENT '是否支持点餐 1.支持 0.不支持';
ALTER TABLE beiker_merchant ADD `lng` decimal(11,6) NOT NULL DEFAULT '0.000000' COMMENT '分店经度';
ALTER TABLE beiker_merchant ADD `lat` decimal(11,6) NOT NULL DEFAULT '0.000000' COMMENT '分店纬度';

ALTER TABLE beiker_goods ADD `is_menu` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否是点菜单商品';