package com.beike.hesssian.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.beike.biz.service.hessian.VipHessianService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml", "classpath:/springcontexttrx/trx-applicationContext.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class VipHessianServiceQueryVipTest {

	@Resource(name = "vipHessianService")
	private VipHessianService vipHessianService;

	@Test
	public void testQueryVip() {
		System.out.println(vipHessianService);
		/*
		 * 1.queryVip(params)，queryVipCount(params)
		 * Map<String, Object> params = new HashMap<String, Object>();
		params.put("guest_id", "913800171");
		params.put("email", "1@q.com");
		params.put("mobile", "13334444222");
		params.put("name", "aa");
		params.put("startRow", "0");
		params.put("pagesize", "1");
		int count = vipHessianService.queryVipCount(params);
		System.out.println("查询出的总个数为：" + count);
		List<Map<String, Object>> lstEvas = vipHessianService.queryVip(params);
		if(lstEvas != null && lstEvas.size() > 0){
			for(Map<String, Object> tmpMap : lstEvas){
				System.out.println(tmpMap);
			}
		}*/
		
		/*
		 * 2.queryVipProductCount(params),queryVipProduct(params)
		 * */Map<String, Object> params = new HashMap<String, Object>();
		params.put("guest_id", "913800171");
		params.put("user_id", "101585173");
		params.put("isConsume", "true");
		params.put("startRow", "0");
		params.put("pagesize", "10");
		int count = vipHessianService.queryVipProductCount(params);
		System.out.println("查询出的总个数为：" + count);
		List<Map<String, Object>> lstEvas = vipHessianService.queryVipProduct(params);
		if(lstEvas != null && lstEvas.size() > 0){
			for(Map<String, Object> tmpMap : lstEvas){
				System.out.println(tmpMap);
			}
		}
		
		/*
		 * 3.queryMenuByOrderId(trxorderId)
		 * Long trxorderId = 2709679l;
		List<Map<String, Object>> lstEvas = vipHessianService.queryMenuByOrderId(trxorderId);
		System.out.println(lstEvas);
		if(lstEvas != null && lstEvas.size() > 0){
			for(Map<String, Object> tmpMap : lstEvas){
				System.out.println(tmpMap);
			}
		}*/
	}

}