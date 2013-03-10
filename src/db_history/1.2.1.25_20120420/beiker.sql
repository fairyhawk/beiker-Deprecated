CREATE TABLE `beiker_cps_tuan800` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`trxorder_id` INT(10) UNSIGNED NOT NULL COMMENT '订单id',
	`goods_id` INT(12) UNSIGNED NOT NULL COMMENT '商品id',
	`order_status` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0' COMMENT '订单状态:0(新下单)1(付款)5(退款)',
	`create_date` TIMESTAMP NOT NULL COMMENT '下单时间',
	`pay_price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '购买价格',
	`current_price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '现价',
	`divide_price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '分成价格',
	`trx_goods_sn` VARCHAR(15) NOT NULL COMMENT '订单号',
	`src` VARCHAR(20) NOT NULL COMMENT '来源(tuan800)',
	`outsrc` VARCHAR(10) NOT NULL COMMENT '主站还是联盟来源',
	`cid` INT(5) UNSIGNED NOT NULL COMMENT '推广标识(0)',
	`wi` VARCHAR(50) NOT NULL COMMENT '合作推广站点的信息',
	`uid` VARCHAR(40) NOT NULL COMMENT '团800网的用户id',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `trx_goods_sn` (`trx_goods_sn`)
)
COMMENT='团800cps'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;


INSERT INTO beiker_adweb VALUES(3,'团800cps','','tuan800_qianpin');
