-- 是否VIP商户
ALTER TABLE `beiker_merchant`ADD COLUMN `isvipbrand` TINYINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否VIP商户:0否 1是';

ALTER TABLE beiker_merchant_profile ADD COLUMN mc_score INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商家评价得分';
ALTER TABLE beiker_merchant_profile ADD COLUMN mc_well_count INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商家很好评价次数';
ALTER TABLE beiker_merchant_profile ADD COLUMN mc_satisfy_count INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商家满意评价次数';
ALTER TABLE beiker_merchant_profile ADD COLUMN mc_poor_count INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商家差评价次数';

ALTER TABLE beiker_goods_profile ADD COLUMN well_count INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商品很好评价次数';
ALTER TABLE beiker_goods_profile ADD COLUMN satisfy_count INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商品满意评价次数';
ALTER TABLE beiker_goods_profile ADD COLUMN poor_count INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商品差评价次数';

-- 评价记录表
CREATE TABLE `beiker_order_evaluation` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`score` TINYINT(3) UNSIGNED NOT NULL DEFAULT '1' COMMENT '评价打分：0很好；1满意；2差',
	`evaluation` VARCHAR(150) NULL DEFAULT NULL COMMENT '评价内容',
	`merchantid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '品牌ID',
	`userid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '用户ID',
	`goodsid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商品ID',
	`trxorderid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '交易ID',
	`issysdefault` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否系统默认评价：1是 0否',
	`addtime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
	`status` TINYINT(4) NOT NULL DEFAULT '0' COMMENT '评价状态：0正常；1屏蔽',
	`ordercount` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0' COMMENT '本次评价订单数',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单评价表';

-- 评价附图表
CREATE TABLE `beiker_evaluation_photo` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`evaluationid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '评价ID',
	`photourl` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '图片地址',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='评价附图表';

-- 分店评价关系表
CREATE TABLE `beiker_branch_evaluation` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`branchid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '分店ID',
	`evaluationid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '评价ID',
	`ordercount` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0' COMMENT '本次评价分店订单数',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分店评价关系表';

-- 店铺属性表
CREATE TABLE `beiker_branch_profile` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`merchantid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商家ID',
	`branchid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '分店ID',
	`well_count` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '分店很好评价次数',
	`satisfy_count` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '分店满意评价次数',
	`poor_count` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '分店差评价次数',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `branchid` (`branchid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='店铺属性';

-- 用户扩展信息表
CREATE TABLE `beiker_user_expand` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`userid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '用户id',
	`nickname` VARCHAR(50) NULL DEFAULT '' COMMENT '昵称',
	`realname` VARCHAR(50) NULL DEFAULT '' COMMENT '真实姓名',
	`gender` TINYINT(3) UNSIGNED NULL DEFAULT '0' COMMENT '性别：0男 1女',
	`avatar` VARCHAR(150) NULL DEFAULT '' COMMENT '头像地址',
	PRIMARY KEY (`id`),
	INDEX `userid` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户扩展信息';

-- 用户地址信息表
CREATE TABLE `beiker_user_address` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
	`userid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '用户id',
	`province` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '省',
	`city` VARCHAR(10) NOT NULL DEFAULT '' COMMENT '市',
	`area` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '区',
	`address` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '地址',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户地址';
