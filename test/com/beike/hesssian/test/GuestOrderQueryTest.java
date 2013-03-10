package com.beike.hesssian.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.beike.biz.service.hessian.TrxHessianServiceGateWay;


/**   
 * @title: GuestOrderQueryTest.java
 * @package com.beike.hesssian.test
 * @description: 
 * @author wangweijie  
 * @date 2013-2-5 下午05:06:42
 * @version v1.0   
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext.xml", "classpath*:springcontexttrx/trx-applicationContext.xml" })
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
public class GuestOrderQueryTest {
	@Autowired
    private TrxHessianServiceGateWay  trxHessianServiceGateWay;
	
	@Test
	public void guestOrderList(){
		Map<String, String> sourceMap = new HashMap<String, String>();
		
		sourceMap.put("reqChannel", "BOSS") ;
		sourceMap.put("guestId", "116001103") ;
		sourceMap.put("querySize", "10");
		sourceMap.put("rowsOffset", "10");
		Map<String, Object>  returnMap = trxHessianServiceGateWay.qryTrxOrderGoodsForGuest(sourceMap);
		
		System.out.println("**************************"+returnMap);
	}
	
	@Test
	public void guestOrderDetail(){
		Map<String, String> sourceMap = new HashMap<String, String>();
		
		sourceMap.put("reqChannel", "BOSS") ;
		sourceMap.put("guestId", "10003136") ;
		sourceMap.put("trxOrderGoodsId", "7730289");
		Map<String, Object>  returnMap = trxHessianServiceGateWay.qryTgDetailForGuest(sourceMap);
		
		System.out.println("**************************"+returnMap);
	}
	
	@Test
	public void qryOrderByVouCodeAndGuestId(){
		Map<String, String> sourceMap = new HashMap<String, String>();
		
		sourceMap.put("reqChannel", "BOSS") ;
		sourceMap.put("guestId", "110012203") ;
		sourceMap.put("voucherCode", "16993954");
		Map<String, String>  returnMap = trxHessianServiceGateWay.qryOrderByVouCodeAndGuestId(sourceMap);
		
		System.out.println("**************************"+returnMap);
	}
	
	@Test
	public void qryTrxOrderGoodsDetailByIds(){
		Map<String, String> sourceMap = new HashMap<String, String>();
		
		sourceMap.put("reqChannel", "BOSS") ;
		sourceMap.put("trxOrderGoodsId", "7730605|7730606|7730607|7730608|7730609|7730610");
		Map<String, Object>  returnMap = trxHessianServiceGateWay.qryTrxOrderGoodsDetailForSettle(sourceMap);
		
		System.out.println("**************************"+returnMap);
	}
}
