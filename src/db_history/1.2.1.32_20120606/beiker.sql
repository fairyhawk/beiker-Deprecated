--UPDATE lucene search email alert template
update beiker_emailtemplate be set be.templatecontent='千品网搜索服务出现异常,请检查,异常信息如下:发生时间:{0}  搜索城市:{1}   搜索类型:{2}  搜索关键字:{3}' 
where templatecode='SEARCH_SERVICE_ALERT';
-- 增加手机端 短信模板

insert into  beiker_smstemplate(smscontent,smstitle,smstype)  
	values('您在千品网的验证码是:{0} 在客户端输入后即可完成手机认证。【千品网】','MOBILE_AUTHCODE','MOBILE_AUTHCODE'); 

-- update lucene alert email template
UPDATE `beiker_emailtemplate` SET `templatecontent`='千品网重建索引出现异常,请检查,异常信息如下{0}:发生时间:{1} ' WHERE  `templatecode`='SEARCH_SERVICE_ALERT' LIMIT 1;

CREATE TABLE `beiker_goods_brand_branch_updatetime` (
	`id` INT(10) NOT NULL COMMENT 'goodsid,brandid',
	`category` CHAR(50) NOT NULL COMMENT 'goods,brand',
	`last_update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间'
)
COMMENT='商品,品牌,分店最后更新时间'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;


CREATE TABLE `beiker_region_branch` (
	`id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	`brandid` INT(10) UNSIGNED NOT NULL COMMENT '品牌id',
	`branchid` INT(10) UNSIGNED NOT NULL COMMENT '分店id',
	`regionextid` INT(10) UNSIGNED NOT NULL,
	`regionid` INT(10) UNSIGNED NOT NULL,
	`city_id` INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `brandid` (`brandid`),
	INDEX `city_id` (`city_id`)
)
COMMENT='分店跟商圈的关系'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

alter table beiker_trxorder_goods  add  last_update_date  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'  COMMENT  '最后更新时间';

alter table beiker_trxorder_goods change is_send_mer_vou is_send_mer_vou tinyint(4);
