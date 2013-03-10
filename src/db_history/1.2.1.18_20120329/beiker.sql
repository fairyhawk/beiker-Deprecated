insert into beiker_emailtemplate (templatecode,templatecontent,templatesubject) values ('REFUND_COUNT_ERROR','您好，购买商品ID为{0}商品因限购导致退款，退款时发生了{1}异常，请关注此问题','购买限购发生退款时退款异常');

insert into beiker_smstemplate (smstitle,smscontent,smstype) values ('SMS_REFUND_LIMIT_TEMPLATE','您购买的订单号为{0}的商品，超出了可购买限制，已退款到您的千品账户中，可致电400-186-1000办理退款到银行/支付宝。【千品网】','SMS_REFUND_LIMIT_TEMPLATE');

insert into beiker_smstemplate (smstitle,smscontent,smstype) values ('SMS_COUNT_LIMIT_TEMPLATE','您购买的订单号为{0}的商品由于超出购买数量，导致购买未成功。可致电400-186-1000办理退款【千品网】','SMS_COUNT_LIMIT_TEMPLATE');

update beiker_smstemplate set smscontent='您在千品网购买的“{0}”即将到期，到期时间{1}。请您尽快消费。【千品网】' where smstitle='SMS_TRXORDER_NOTIFY_OVERDUE'