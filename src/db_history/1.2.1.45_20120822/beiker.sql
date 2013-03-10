CREATE TABLE `beiker_miaosha` (
	`id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
	`goods_id` INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商品ID',
	`m_title` VARCHAR(120) NOT NULL DEFAULT '' COMMENT '秒杀商品标题',
	`m_short_title` VARCHAR(30) NOT NULL DEFAULT '' COMMENT '短标题',
	`m_pay_price` DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT '0.00' COMMENT '秒杀价',
	`m_maxcount` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0' COMMENT '秒杀库存',
	`m_settle_price` DECIMAL(10,2) UNSIGNED NOT NULL DEFAULT '0.00' COMMENT '秒杀结算价',
	`m_single_count` TINYINT(4) UNSIGNED NOT NULL DEFAULT '0' COMMENT '秒杀个人限购数',
	`m_start_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀开始时间',
	`m_end_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
	`m_show_start_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀显示开始时间',
	`m_show_end_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀显示结束时间',
	`m_banner` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '秒杀banner图片路径',
	`is_used` TINYINT(4) UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否启用(1:是 0:否 2:作废)',
	`is_need_virtual` TINYINT(4) UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否需要虚拟销量(1:是 0:否)',
	`m_virtual_count` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0' COMMENT '虚拟销量',
	`m_sale_count` SMALLINT(5) UNSIGNED NOT NULL DEFAULT '0' COMMENT '实际销量',
	`createucid` INT(11) NOT NULL DEFAULT '0' COMMENT '创建人',
	`createtime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
	`updateucid` INT(11) NOT NULL DEFAULT '0' COMMENT '修改人',
	`updatetime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
	PRIMARY KEY (`id`),
	INDEX `idx_goods_id` (`goods_id`),
	INDEX `m_start_time` (`m_start_time`),
	INDEX `m_show_start_time` (`m_show_start_time`),
	INDEX `m_show_end_time` (`m_show_end_time`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀';

CREATE TABLE `beiker_miaosha_remind` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
	`userid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '用户ID',
	`miaoshaid` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '秒杀ID',
	`phone` CHAR(11) NOT NULL DEFAULT '' COMMENT '手机号',
	`addtime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
	`status` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0' COMMENT '状态',
	PRIMARY KEY (`id`),
	INDEX `miaoshaid` (`miaoshaid`, `phone`),
	INDEX `userid` (`userid`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀提醒';



-- 秒杀预订短信提醒
INSERT INTO `beiker_smstemplate` (`smstitle`, `smscontent`, `smstype`) VALUES ('SMS_MIAOSHA_REMIND', '您预约的秒杀商品“{0}”将于{1}开秒。【千品网】', 'SMS_MIAOSHA_REMIND');

-- 商品温馨提示
DROP TABLE IF EXISTS beiker_goods_kindly;
CREATE TABLE beiker_goods_kindly (
  id int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  goods_id int(11) NOT NULL COMMENT '商品ID',
  kindlywarnings varchar(255) NOT NULL DEFAULT '' COMMENT '温馨提示',
	high_light TINYINT(4) NOT NULL DEFAULT '0' COMMENT '高亮显示(0:否, 1:是)',
  create_time TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间',
  PRIMARY KEY (id),
  KEY idx_goods_id (goods_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品-温馨提示映射表';

insert into beiker_trx_rule (trx_title,trx_rule,create_date,description) values('MIAOSHA','ACTHIS:1',now(),'周年秒杀');
-- 购物车
alter table beiker_shopcart add  miaoshaid int(10) unsigned NOT NULL DEFAULT '0' COMMENT '秒杀ID';

alter table beiker_shopcart add unique idx_goodsid_userid_miaoshaid (`userid`,`goodsid`,`miaoshaid`);


-- 删除购物车脏数据
select shopcartid,merchantid,
goodsid,userid,buy_count,addtime,miaoshaid
from beiker_shopcart
group by userid,goodsid
having count(shopcartid)>1;

-- 还要删除大于1大于2的
delete from beiker_shopcart
where shopcartid in(
select shopcartid from(
select shopcartid,merchantid,
goodsid,userid,buy_count,addtime,miaoshaid
from beiker_shopcart
group by userid,goodsid
having count(shopcartid)>1) as temp);


alter table beiker_pay_limit add  miaosha_id int(10) unsigned NOT NULL DEFAULT '0' COMMENT '秒杀ID';
