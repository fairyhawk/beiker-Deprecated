-- 商品排序权重
CREATE TABLE `beiker_goods_sort_weights` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
	`goodsid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商品ID',
	`newscore` DECIMAL(30,10) NOT NULL DEFAULT '0.0000000000' COMMENT '新品得分',
	`salesscore` DECIMAL(30,10) NOT NULL DEFAULT '0.0000000000' COMMENT '销量得分',
	`totalscore` DECIMAL(30,10) NOT NULL DEFAULT '0.0000000000' COMMENT '总得分',
	`manualadjust` INT(11) NOT NULL DEFAULT '0' COMMENT '人工调整分',
	`cityid` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '城市ID',
	`addtime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
	PRIMARY KEY (`id`)
)
COMMENT='商品排序权重' ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `beiker_catlog_good` ADD COLUMN `sortweights` DECIMAL(30,10) NOT NULL DEFAULT '0.0000000000' COMMENT '排序权重';

ALTER TABLE beiker_goods ADD old_goods_id int(11) NOT NULL DEFAULT '0' COMMENT '原商品id';

alter table beiker_trxlog add  trxlog_sub_type  char(30)  NOT NULL DEFAULT  ''  COMMENT  '交易日志子类型';
