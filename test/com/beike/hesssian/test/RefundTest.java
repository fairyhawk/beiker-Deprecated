package com.beike.hesssian.test;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.beike.common.bean.trx.PaymentInfoGeneratorAlipay;
import com.beike.util.DateUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml","classpath:/springcontexttrx/trx-applicationContext.xml" })
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
public class RefundTest{
	
	@Test
	public void sendAlipayRefund(){
		String refundRequetId = "20121x030RUD4xx15138992112345A"; // 退款请求号
		/*
		 * 根据最新时间重新生成新的退款请求号（部分机构需要退款请求号和当前日期必须匹配）
		 */
		String oldProRefundReqId = refundRequetId;	// 此前生成的渠道退款请求订单号
		String subStrPro = oldProRefundReqId.substring(0, 8);				// 此前的前缀
		String newProRefundReqId = oldProRefundReqId.replace(subStrPro, DateUtils.toString(new Date(), "yyyyMMdd"));// 覆盖前缀，生成当日的前缀。即生成新退款请求订单号
		refundRequetId = newProRefundReqId;
		System.out.println("#####################新退款号:"+newProRefundReqId);
		
		String proExternalId = "2012091x301334297";// 支付机构交易流水号
		String payRequestId = "QPPay0x4180295T";
		String refundReqAmount = "3";// 需退款金额

		Long paymentId = new Long(4848360);		//获得支付ID，由
		try {
			PaymentInfoGeneratorAlipay.refundByTrxId(refundRequetId, proExternalId, refundReqAmount, "CNY", "",payRequestId,paymentId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}