package com.beike.hesssian.test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
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
import com.beike.dao.businessbackground.OrderDao;
import com.beike.util.DateUtils;

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
public class VipHessianServiceTest {

	@Resource(name = "vipHessianService")
	private VipHessianService vipHessianService;
	
	@Resource(name = "orderDao")
	private OrderDao orderDao;

	@Test
	public void testGetEvaluateCount() {
		System.out.println(vipHessianService);
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("merchantid", 91693805L);
		queryMap.put("email", "loujanwen@gmail.com");
		//queryMap.put("subguestid", 1111L);
		int count = vipHessianService.queryEvaluateCount(queryMap);
		System.out.println("查询出的总个数为：" + count);
		List<Map<String, Object>> lstEvas = vipHessianService.queryEvaluation(queryMap, 1, 3);
		if(lstEvas != null && lstEvas.size() > 0){
			for(Map<String, Object> tmpMap : lstEvas){
				System.out.println(tmpMap);
			}
		}
	}
	
	@Test
	public void testStatOrderGoods(){
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("guestid", 513008173L);
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = DateUtils.parseToDate("2012-08-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			endDate = DateUtils.parseToDate("2012-12-12 23:59:59", "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		queryMap.put("startDate", startDate);
		queryMap.put("endDate", endDate);
		List<Map<String, Object>> lstOrderGoods = orderDao.getTrxOrderGoods(queryMap);
		for(Map<String, Object> tmpMap : lstOrderGoods){
			System.out.println(tmpMap);
		}
	}
	
	@Test
	public void testQueryIncomeStatistics(){
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("guestid", 919908132L);  //28007435L  84887998L
		/*Date startDate = null;
		Date endDate = null;
		try {
			startDate = DateUtils.parseToDate("2012-08-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			endDate = DateUtils.parseToDate("2012-12-12 23:59:59", "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
		String startDate = "2012-02-01";
		String endDate = "2012-12-12";
		queryMap.put("startDate", startDate);
		queryMap.put("endDate", endDate);
		List<Map<String, Object>> lstOrderGoods = vipHessianService.queryIncomeStatistics(queryMap);
		if(lstOrderGoods != null){
			for(Map<String, Object> tmpMap : lstOrderGoods){
				BigDecimal income = (BigDecimal)tmpMap.get("totaldivideprice");
				System.out.println(income);
				System.out.println(tmpMap);
			}
		}
		
		System.out.println("================================================================");
		Map<Long, Map<String, Object>> totalStatisMap = vipHessianService.queryIncomeTotalStatistics(lstOrderGoods);
		if(totalStatisMap != null){
			for (Map.Entry<Long, Map<String, Object>> entry : totalStatisMap.entrySet()) {
				System.out.print(entry.getKey() + ":" + entry.getValue() + "\t");
			}
		}
	}

}