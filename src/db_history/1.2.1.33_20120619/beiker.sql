
-- 评价表增加 发布状态字段
ALTER TABLE beiker_order_evaluation ADD COLUMN publishstatus INT(10) NOT NULL DEFAULT '0' COMMENT '发布状态:0未发布;1已发布';

