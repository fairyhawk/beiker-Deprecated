package com.beike.service.cps.tuan360.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.cps.tuan360.CPSTuan360Dao;
import com.beike.service.cps.tuan360.CPSTuan360Service;
import com.beike.service.cps.tuan800.impl.CPSTuan800ServiceImpl;
import com.beike.util.DateUtils;
import com.beike.util.PropertyUtil;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Aug 9, 2012 2:36:00 PM     
 * @version 1.0
 */
@Service("cpsTuan360Service")
public class CPSTuan360ServiceImpl implements CPSTuan360Service {
	private final PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	private static final Logger logger = Logger.getLogger(CPSTuan800ServiceImpl.class);
	
	@Autowired
	private CPSTuan360Dao cpsTuan360Dao;
	
	@Override
	public int saveOrderNoPay(Map<String, Object> params) {
		if(params!=null && !params.isEmpty()){
			String trxOrderId = (String)params.get("trxorder_id");
			String cpsCookie = (String)params.get("cps_cookie");
			if(StringUtils.isNotEmpty(trxOrderId) && StringUtils.isNotEmpty(cpsCookie)){
				String[] aryCpsCookie = cpsCookie.split("-");
				String qihoo_id = "";
				String ext = "";
				if(aryCpsCookie!=null && aryCpsCookie.length>=2){
					qihoo_id = aryCpsCookie[0];
					ext = aryCpsCookie[1];
				}
				String qid = StringUtils.trimToEmpty((String)params.get("qid"));
				String fanli = propertyUtil.getProperty("CPS_TUAN360_FANLI");
				List<Map<String,Object>> lstOrderGoods = cpsTuan360Dao.getOrderInfo(trxOrderId);
				List<Object[]> listOrderParams = new ArrayList<Object[]>();
				if(lstOrderGoods!=null && lstOrderGoods.size()>0){
					for(Map<String,Object> tmpMap : lstOrderGoods){
						Object[] aryParam = new Object[12];
						aryParam[0] = tmpMap.get("trxorder_id");
						aryParam[1] = tmpMap.get("id");
						aryParam[2] = tmpMap.get("goods_id");
						
						aryParam[3] = tmpMap.get("pay_price");
						aryParam[4] = tmpMap.get("create_date");
						aryParam[5] = tmpMap.get("create_date");
						aryParam[6] = fanli;
						aryParam[7] = 0;
						aryParam[8] = qid;
						aryParam[9] = qihoo_id;
						aryParam[10] = ext;
						aryParam[11] = 1;
						
						listOrderParams.add(aryParam);
					}
					
					return cpsTuan360Dao.saveOrderNoPay(listOrderParams);
				}else{
					return 0;
				}
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}

	@Override
	public int updateOrderStatus(Long trxOrderId, int newStatus, int oldStatus) {
		return cpsTuan360Dao.updateOrderStatus(trxOrderId, newStatus, oldStatus);
	}

	@Override
	public List<Map<String, Object>> queryOrdersByOrderId(List<Long> lstOrderIds,
			int maxCount) {
		List<Map<String,Object>> listOrders = cpsTuan360Dao.queryOrdersByOrderId(lstOrderIds, maxCount);
		if(listOrders!=null && listOrders.size()>0){
			List<Long> lstGoodsIds = new ArrayList<Long>();
			for(Map<String,Object> tmpOrder : listOrders){
				lstGoodsIds.add((Long)tmpOrder.get("goods_id"));
			}
			
			List<Map<String,Object>> lstGoods = cpsTuan360Dao.queryCpsGoodsByGoodsId(lstGoodsIds);
			if(lstGoods!=null && lstGoods.size()>0){
				Map<Long,Map<String,Object>> hsCpsGoods = new HashMap<Long,Map<String,Object>>();
				for(Map<String,Object> tmpGoods : lstGoods){
					hsCpsGoods.put((Long)tmpGoods.get("goodsid"), tmpGoods);
				}
				
				for(Map<String,Object> tmpOrder : listOrders){
					Long orderGoodsId = (Long)tmpOrder.get("goods_id");
					Map<String,Object> mapGoods = hsCpsGoods.get(orderGoodsId);
					if(mapGoods!=null){
						tmpOrder.put("goodsname", mapGoods.get("goodsname"));
						tmpOrder.put("city", mapGoods.get("city"));
						tmpOrder.put("tagid", mapGoods.get("tagid"));
						tmpOrder.put("tagextid", mapGoods.get("tagextid"));
					}
				}
			}
		}
 		return listOrders;
	}

	@Override
	public List<Map<String, Object>> queryOrdersByCreateTime(String startTime,
			String endTime, Long lastOrderId, int maxCount) {
		List<Map<String,Object>> listOrders = cpsTuan360Dao.queryOrdersByCreateTime(startTime,
				endTime, lastOrderId, maxCount);
		
		if(listOrders!=null && listOrders.size()>0){
			List<Long> lstGoodsIds = new ArrayList<Long>();
			for(Map<String,Object> tmpOrder : listOrders){
				lstGoodsIds.add((Long)tmpOrder.get("goods_id"));
			}
			
			List<Map<String,Object>> lstGoods = cpsTuan360Dao.queryCpsGoodsByGoodsId(lstGoodsIds);
			if(lstGoods!=null && lstGoods.size()>0){
				Map<Long,Map<String,Object>> hsCpsGoods = new HashMap<Long,Map<String,Object>>();
				for(Map<String,Object> tmpGoods : lstGoods){
					hsCpsGoods.put((Long)tmpGoods.get("goodsid"), tmpGoods);
				}
				
				for(Map<String,Object> tmpOrder : listOrders){
					Long orderGoodsId = (Long)tmpOrder.get("goods_id");
					Map<String,Object> mapGoods = hsCpsGoods.get(orderGoodsId);
					if(mapGoods!=null){
						tmpOrder.put("goodsname", mapGoods.get("goodsname"));
						tmpOrder.put("city", mapGoods.get("city"));
						tmpOrder.put("tagid", mapGoods.get("tagid"));
						tmpOrder.put("tagextid", mapGoods.get("tagextid"));
					}
				}
			}
		}
 		return listOrders;
	}

	@Override
	public List<Map<String, Object>> queryOrdersByUpdTime(String updStartTime,
			String updEndTime, Long lastOrderId, int maxCount) {
		List<Map<String,Object>> listOrders = cpsTuan360Dao.queryOrdersByUpdTime(updStartTime,
				updEndTime, lastOrderId, maxCount);
		
		if(listOrders!=null && listOrders.size()>0){
			List<Long> lstGoodsIds = new ArrayList<Long>();
			for(Map<String,Object> tmpOrder : listOrders){
				lstGoodsIds.add((Long)tmpOrder.get("goods_id"));
			}
			
			List<Map<String,Object>> lstGoods = cpsTuan360Dao.queryCpsGoodsByGoodsId(lstGoodsIds);
			if(lstGoods!=null && lstGoods.size()>0){
				Map<Long,Map<String,Object>> hsCpsGoods = new HashMap<Long,Map<String,Object>>();
				for(Map<String,Object> tmpGoods : lstGoods){
					hsCpsGoods.put((Long)tmpGoods.get("goodsid"), tmpGoods);
				}
				
				for(Map<String,Object> tmpOrder : listOrders){
					Long orderGoodsId = (Long)tmpOrder.get("goods_id");
					Map<String,Object> mapGoods = hsCpsGoods.get(orderGoodsId);
					if(mapGoods!=null){
						tmpOrder.put("goodsname", mapGoods.get("goodsname"));
						tmpOrder.put("city", mapGoods.get("city"));
						tmpOrder.put("tagid", mapGoods.get("tagid"));
						tmpOrder.put("tagextid", mapGoods.get("tagextid"));
					}
				}
			}
		}
 		return listOrders;
	}

	@Override
	public List<Map<String, Object>> queryOrdersByBillMonth(String billMonth,
			Long lastOrderId, int maxCount) {
		List<Map<String,Object>> listOrders = null;
		try {
			Date stateTime = DateUtils.parseToDate(billMonth + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
			if(stateTime!=null){
				String endTime = DateUtils.getTimeBeforeOrAfterMonth(1,"yyyy-MM-dd HH:mm:ss",stateTime);
				listOrders = cpsTuan360Dao.queryOrdersByBillMonth(DateUtils.toString(stateTime,"yyyy-MM-dd HH:mm:ss"),
						endTime, lastOrderId, maxCount);
				
				if(listOrders!=null && listOrders.size()>0){
					List<Long> lstGoodsIds = new ArrayList<Long>();
					for(Map<String,Object> tmpOrder : listOrders){
						lstGoodsIds.add((Long)tmpOrder.get("goods_id"));
					}
					
					List<Map<String,Object>> lstGoods = cpsTuan360Dao.queryCpsGoodsByGoodsId(lstGoodsIds);
					if(lstGoods!=null && lstGoods.size()>0){
						Map<Long,Map<String,Object>> hsCpsGoods = new HashMap<Long,Map<String,Object>>();
						for(Map<String,Object> tmpGoods : lstGoods){
							hsCpsGoods.put((Long)tmpGoods.get("goodsid"), tmpGoods);
						}
						
						for(Map<String,Object> tmpOrder : listOrders){
							Long orderGoodsId = (Long)tmpOrder.get("goods_id");
							Map<String,Object> mapGoods = hsCpsGoods.get(orderGoodsId);
							if(mapGoods!=null){
								tmpOrder.put("goodsname", mapGoods.get("goodsname"));
								tmpOrder.put("city", mapGoods.get("city"));
								tmpOrder.put("tagid", mapGoods.get("tagid"));
								tmpOrder.put("tagextid", mapGoods.get("tagextid"));
							}
						}
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
 		return listOrders;
	}

	@Override
	public int cancelOrder(Long trxGoodsId) {
		if(trxGoodsId!=null){
			logger.info("===CPSTuan360ServiceImpl===cancelOrder===" + trxGoodsId);
			return cpsTuan360Dao.cancelOrder(trxGoodsId);
		}else{
			return 0;
		}
	}
}
