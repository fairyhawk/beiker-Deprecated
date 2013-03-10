

DROP TABLE IF EXISTS `beiker_adgoodsinfo`;

CREATE TABLE `beiker_adgoodsinfo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goodsid` int(10) unsigned NOT NULL COMMENT '商品id',
  `type` enum('1','2','3') NOT NULL DEFAULT '1' COMMENT '1.同类热销 2.美食热销 3.低价推荐',
  `cityid` int(11) NOT NULL COMMENT '城市id',
  `add_time` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `catlogid` int(11) NOT NULL DEFAULT '0' COMMENT '商品类别',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;