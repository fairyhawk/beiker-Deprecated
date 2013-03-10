
insert into  beiker_smstemplate(smscontent,smstitle,smstype)  values('您在千品网购买的“{0}”,千品订单号{1},服务密码由商家发送。有效期至{2}【千品网】','MER_VOUCHERDISPATCH_API_SMS','MER_VOUCHERDISPATCH_API_SMS');    


insert into  beiker_emailtemplate(templatecode,templatecontent,templatesubject)  values('MERCHANT_API_VOUCHER_EMAIL','亲爱的千品网用户：<br/><br/>您在千品网购买的“{0}”；<br/><br/>服务密码由商家发送；<br/><br/>千品订单号为：{1}；<br/><br/>商家订单号为：{2}；<br/><br/>有效期至{3}。<br/><br/>若需重发，请<a href="http://www.qianpin.com/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNUSEED">进入我的千品</a><br/><br/>请您提前预约，祝您消费愉快!<br/><br/>本邮件由系统自动发出，请勿回复。<br/><br/><br/>千品网<br/><br/>{4}','千品网服务密码邮件');    


insert into  beiker_emailtemplate(templatecode,templatecontent,templatesubject)  values('MERCHANT_TRX_ERROR','您好，商家ID为{0} 商品{1}发生了{2}异常，请关注此问题。','凭证发送异常报警');   

