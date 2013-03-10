DROP TABLE IF EXISTS `beiker_goods_profile`;

CREATE TABLE `beiker_goods_profile` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `goodsid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商品ID',
  `sales_count` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '销售量',
  `detailpageurl` varchar(16) NOT NULL DEFAULT '' COMMENT '商品详细页',
  PRIMARY KEY (`id`),
  KEY `goodsid` (`goodsid`)
) ENGINE=InnoDB AUTO_INCREMENT=8192 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `beiker_shanghubao_profile`;

CREATE TABLE `beiker_shanghubao_profile` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `merchantid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商家ID',
  `shb_title_logo` varchar(64) NOT NULL DEFAULT '',
  `shb_logo1` varchar(128) NOT NULL DEFAULT '',
  `shb_logo2` varchar(128) NOT NULL DEFAULT '',
  `shb_logo3` varchar(128) NOT NULL DEFAULT '',
  `shb_logo4` varchar(128) NOT NULL DEFAULT '',
  `shb_logo5` varchar(128) NOT NULL DEFAULT '',
  `shb_logo6` varchar(128) NOT NULL DEFAULT '',
  `shb_logo7` varchar(128) NOT NULL DEFAULT '',
  `shb_logo8` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `merchantid` (`merchantid`)
) ENGINE=InnoDB AUTO_INCREMENT=1024 DEFAULT CHARSET=utf8 COMMENT='商家商户宝属性';

DROP TABLE IF EXISTS `beiker_merchant_profile`;

CREATE TABLE `beiker_merchant_profile` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `merchantid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商家ID',
  `mc_logo1` varchar(48) NOT NULL DEFAULT '',
  `mc_logo2` varchar(48) NOT NULL DEFAULT '',
  `mc_logo3` varchar(48) NOT NULL DEFAULT '',
  `mc_logo4` varchar(48) NOT NULL DEFAULT '',
  `mc_avg_scores` float(3,1) unsigned NOT NULL DEFAULT '0.0' COMMENT '商家评分',
  `mc_evaliation_count` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '评价次数',
  `mc_sale_count` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '商家累积销售量',
  `mc_fix_tel` varchar(32) NOT NULL DEFAULT '' COMMENT '商家电话',
  PRIMARY KEY (`id`),
  KEY `merchantid` (`merchantid`)
) ENGINE=InnoDB AUTO_INCREMENT=4096 DEFAULT CHARSET=utf8 COMMENT='商家属性';



