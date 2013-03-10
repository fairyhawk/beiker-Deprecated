package com.beike.hesssian.test;

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


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext.xml", "classpath*:springcontexttrx/trx-applicationContext.xml" })
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
public class ShopCartTest
{

	@Resource(name="mcClient.trxHessianServiceGateWay")
	private  TrxHessianServiceGateWay trxHessianServiceGateWay;
	@Test
	public void testShopCart()
	{
		
		Map<String, String> sourceMap = new HashMap<String, String>();
		
		sourceMap.put("reqChannel", "BOSS") ;
		sourceMap.put("userId", "531288") ;
		//sourceMap.put("trxStatus", "SUCCESS") ;
		sourceMap.put("isFirst", "1") ;
		System.out.println("**************111************");
		//Map<String, String>  returnMap =  trxHessianServiceGateWay.getActByUserId(sourceMap) ;
		
		//sourceMap.put("pageSize", "20") ;
		//sourceMap.put("rowsOffset", "0") ;
		
		
		sourceMap.put("goodsId", "12497");
//		//sourceMap.put("goodsId", "13174|12497");
		sourceMap.put("goodsCount", "2");
		sourceMap.put("description", "");
//		sourceMap.put("reqChannel","MC");
//		sourceMap.put("shoppingCartId", "665771|665772");
		
		
		//Map<String, String>  returnMap =  trxHessianServiceGateWay.qryShoppingCart(sourceMap) ;
		Map<String, String>  returnMap =  trxHessianServiceGateWay.addShoppingCart(sourceMap) ;
		
			
		System.out.println("**************************"+returnMap);


	}
	
}
