package com.beike.hesssian.test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;

/**
 * Title : CheckVoucherTest.java <br/>
 * Description : TODO<br/>
 * Company : Sinobo <br/>
 * Copyright : Copyright (c) 2010-2012 All rights reserved.<br/>
 * Created : 2012-11-14 下午3:21:50 <br/>
 * 
 * @author Wenzhong Gu
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml", "classpath:/springcontexttrx/trx-applicationContext.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class CheckVoucherTest {

	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;

	@Test
	public void testTrxCreateInMC() throws UnsupportedEncodingException {

		Map<String, String> map = new HashMap<String, String>();

		map.put("reqChannel", "PARTNER");
		map.put("guestId", "718191624");
		map.put("voucherVerifySource", "SMS");
		map.put("voucherCode", "80389564");
		map.put("subGuestId", "1");
		map.put("description", "111111");

		Map<String, String> rspMap = trxHessianServiceGateWay.validateVoucher(map);

		System.out.println(rspMap.get("status"));
		System.out.println(rspMap.get("rspCode"));
		System.out.println("**************************" + rspMap);
	}

}