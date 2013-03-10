-- 商品排序权重
alter table beiker_goods_sort_weights add column tagid INT(11) UNSIGNED NOT NULL DEFAULT '0' COMMENT '商品分类';

update beiker_goods_sort_weights set tagid=10100 where tagid=0;