package com.beike.biz.service.hessian.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.hessian.VipHessianService;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.dao.businessbackground.EvaluationDao;
import com.beike.dao.businessbackground.OrderDao;
import com.beike.dao.businessbackground.VipDao;
import com.beike.dao.businessbackground.VipStatisticsDao;
import com.beike.util.DateUtils;
import com.beike.util.producttype.ProductTypeUtil;


@Service("vipHessianService")
public class VipHessianServiceImpl implements VipHessianService {
	@Autowired
	private VipDao vipDao;
	@Autowired
	private EvaluationDao evaluationDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private VipStatisticsDao vipStatisticsDao;
	
	@Override
	public List<Map<String, Object>> queryVip(Map<String, Object> params) {
		return vipDao.queryVip(params);
	}
	
	@Override
	public Map<String, Object> queryVipById(Long userId,Long guestId) {
		return vipDao.queryVipById(userId,guestId);
	}
	
	private Map<Long, Map<String, Object>> getGoodsMap(Set<Long> goodsidsSet){
		List<Map<String, Object>> lstGoods = evaluationDao.getGoodsByIds(goodsidsSet);
		Map<Long, Map<String, Object>> tmpGoodsMap = null;
		if(lstGoods != null && lstGoods.size() > 0){
			tmpGoodsMap = new HashMap<Long, Map<String, Object>>();
			for(Map<String, Object> tmpMap : lstGoods){
				tmpGoodsMap.put((Long)tmpMap.get("goodsid"), tmpMap);
			}
		}
		return tmpGoodsMap;
	}
	
	private Map<Long, Map<String, Object>> getSubGuestMap(Set<Long> subguestidsSet){
		List<Map<String, Object>> lstSubguests = evaluationDao.getSubGuests(subguestidsSet);
		Map<Long, Map<String, Object>> tmpSubGuestMap = null;
		if(lstSubguests != null && lstSubguests.size() > 0){
			tmpSubGuestMap = new HashMap<Long, Map<String, Object>>();
			for(Map<String, Object> tmpMap : lstSubguests){
				tmpSubGuestMap.put((Long)tmpMap.get("merchantid"), tmpMap);
			}
		}
		return tmpSubGuestMap;
	}
	

	public List<Map<String, Object>> queryIncomeStatistics(Map<String, Object> queryMap){
		if(queryMap == null){
			return null;
		}
		Long guestid = (Long)queryMap.get("guestid");  //商户ID
		if(guestid == null){
			return null;
		}
		/*String startDate = (String) queryMap.get("startDate");
		String endDate = (String) queryMap.get("endDate");
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			return null;
		}
		Date startTime = null;
		Date endTime = null;
		try {
			startTime = DateUtils.parseToDate(startDate + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
			endTime = DateUtils.parseToDate(endDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		long days = (endTime.getTime() - startTime.getTime()) / (24 * 60 * 60 * 1000);
		if(days > 365){
			return null;
		}
		queryMap.put("startDate", startTime);
		queryMap.put("endDate", endTime);*/
		
		List<Map<String, Object>> lstTrxOrderGoods = orderDao.getTrxOrderGoods(queryMap);
		if(lstTrxOrderGoods != null && lstTrxOrderGoods.size() > 0){
			Set<Long> goodsidsSet = new HashSet<Long>();
			Set<Long> subguestidsSet = new HashSet<Long>();
			for(Map<String, Object> tmpMap : lstTrxOrderGoods){
				goodsidsSet.add((Long)tmpMap.get("goodsid"));
				subguestidsSet.add((Long)tmpMap.get("subguestid"));
			}
			Map<Long, Map<String, Object>> tmpGoodsMap = getGoodsMap(goodsidsSet);
			Map<Long, Map<String, Object>> tmpSubGuestMap = getSubGuestMap(subguestidsSet);
			
			for(Map<String, Object> tmpMap : lstTrxOrderGoods){
				Long merchantid = (Long)tmpMap.get("subguestid");
				if(merchantid != null){
					if(tmpSubGuestMap != null){
						Map<String, Object> linshiSubGuestMap = tmpSubGuestMap.get(merchantid);
						if(linshiSubGuestMap != null){
							tmpMap.put("merchantname", linshiSubGuestMap.get("merchantname"));
						}
					}
				}
				
				Long goodsid = (Long)tmpMap.get("goodsid");
				String biztype = String.valueOf(tmpMap.get("biztype"));
				if(goodsid != null){
					if(tmpGoodsMap != null){
						Map<String, Object> map = tmpGoodsMap.get(goodsid);
						if(map != null){
							tmpMap.put("goodsname", map.get("goodsname"));
							String couponcash = (String)map.get("couponcash");
							String goodstype = ProductTypeUtil.getProductType(biztype, couponcash);
							tmpMap.put("goodstype", goodstype);
						}
					}
				}
			}
		}
		return lstTrxOrderGoods;
	}
	
	public Map<Long, Map<String, Object>> queryIncomeTotalStatistics(List<Map<String, Object>> lstOrderGoods){
		if(lstOrderGoods != null && lstOrderGoods.size() > 0){
			Map<Long, Map<String, Object>> totalStatisMap = new LinkedHashMap<Long, Map<String, Object>>();
			for(Map<String, Object> tmpMap : lstOrderGoods){
				Long subguestid = (Long)tmpMap.get("subguestid");
				String goodstype = (String)tmpMap.get("goodstype");   //商品类型
				BigDecimal income = (BigDecimal)tmpMap.get("totaldivideprice");
				Map<String, Object> merTotalMap = totalStatisMap.get(subguestid);
				if(merTotalMap == null){
					merTotalMap = new HashMap<String, Object>();
					merTotalMap.put("groupIncome", new BigDecimal(0));
					merTotalMap.put("onlineStoreIncome", new BigDecimal(0));
					merTotalMap.put("totalIncome", new BigDecimal(0));
				}
				merTotalMap.put("merchantname", tmpMap.get("merchantname"));
				merTotalMap.put("totalIncome", ((BigDecimal)merTotalMap.get("totalIncome")).add(income));
				if("网上商店".equals(goodstype)){
					merTotalMap.put("onlineStoreIncome", ((BigDecimal)merTotalMap.get("onlineStoreIncome")).add(income));
				}else{
					merTotalMap.put("groupIncome", ((BigDecimal)merTotalMap.get("groupIncome")).add(income));
				}
				totalStatisMap.put(subguestid, merTotalMap);
			}
			return totalStatisMap;
		}
		return null;
	}
	
	@Override
	public int queryEvaluateCount(Map<String, Object> queryMap){
		int count = 0;
		if(queryMap != null){
			Long merchantId = (Long)queryMap.get("merchantid");
			if(merchantId != null && merchantId > 0){
				count = evaluationDao.getEvaluateCount(queryMap);
			}
		}
		return count;
	}
	
	@SuppressWarnings("null")
	@Override
	public List<Map<String, Object>> queryEvaluation(Map<String, Object> queryMap, int curPage, int pageSize) {
		if(queryMap == null){
			return null;
		}
		Long merchantId = (Long)queryMap.get("merchantid");
		if(merchantId == null && merchantId <= 0){
			return null;
		}
		List<Map<String, Object>> lstEva = evaluationDao.getScrollEvaluate(queryMap, curPage, pageSize);
		if(lstEva != null && lstEva.size() > 0){
			Set<Long> goodsidsSet = new HashSet<Long>();
			Set<Long> subguestidsSet = new HashSet<Long>();
			for(Map<String, Object> evaMap : lstEva){
				goodsidsSet.add((Long)evaMap.get("goodsid"));
				subguestidsSet.add((Long)evaMap.get("subguestid"));
			}
			Map<Long, Map<String, Object>> tmpGoodsMap = getGoodsMap(goodsidsSet);
			Map<Long, Map<String, Object>> tmpSubGuestMap = getSubGuestMap(subguestidsSet);
			
			for(Map<String, Object> evaMap : lstEva){
				Long merchantid = (Long)evaMap.get("subguestid");
				if(merchantid != null){
					if(tmpSubGuestMap != null){
						Map<String, Object> linshiSubGuestMap = tmpSubGuestMap.get(merchantid);
						if(linshiSubGuestMap != null){
							evaMap.put("merchantname", linshiSubGuestMap.get("merchantname"));
						}
					}
				}
				
				String biztype = String.valueOf(evaMap.get("biztype"));
				Long goodsid = (Long)evaMap.get("goodsid");
				if(goodsid != null){
					if(tmpGoodsMap != null){
						Map<String, Object> map = tmpGoodsMap.get(goodsid);
						if(map != null){
							evaMap.put("goodsname", map.get("goodsname"));
							String couponcash = (String)map.get("couponcash");
							String goodstype = ProductTypeUtil.getProductType(biztype, couponcash);
							evaMap.put("goodstype", goodstype);
						}
					}
				}
			}
		}
		return lstEva;
	}
	
	@Override
	public List<Map<String, Object>> queryTrxOrderInfo(Date date) {
		String dateStart= DateUtils.getTimeBeforeORAfter(date, 0, "yyyy-MM-dd");
		String dateEnd = DateUtils.getTimeBeforeORAfter(date, 1, "yyyy-MM-dd");
		//查询前一天的商家交易
		List<Map<String,Object>> lstTrxOrderInfo = vipStatisticsDao.queryTrxOrderInfo(dateStart,dateEnd);
		return lstTrxOrderInfo;
	}
	
	@Override
	public void addNewVipStatistics(Map<String,Object> trxOrderInfo) {
		if(trxOrderInfo!=null){
			Long userId = (Long)(trxOrderInfo.get("user_id"));
			Long guestId = (Long)(trxOrderInfo.get("guest_id"));
			int exist = vipStatisticsDao.queryVipOfGuest(userId, guestId);
			if(exist==0){
				vipStatisticsDao.addVipStatitics(trxOrderInfo);
			}
		}
	}
	
	/**
	 * 更新线上会员数量
	 * @param guestMap 商家和新增会员数的映射
	 * @return 
	 */
	@Override
	public void updateVipInfoForMonth(Date date){
		//本月
		String thisMonth = DateUtils.getTimeBeforeORAfter(date, 0, "yyyy-MM");
		//下月
		String nextMonth = DateUtils.getTimeBeforeOrAfterMonth(1, "yyyy-MM", date);
		Map<Long,Long> guestInfoMap = new HashMap<Long, Long>();
		//找出所有本月商家
		List<Map<String,Object>> guestInfoForMonth = vipStatisticsDao.queryGuestInfoByDate(thisMonth);
		if(guestInfoForMonth!=null && guestInfoForMonth.size()>0){
			for(Map<String,Object> guestInfo : guestInfoForMonth){
				Long guestId = (Long)(guestInfo.get("guest_id"));
				guestInfoMap.put(guestId, guestId);
			}
		}
		//所有商家的本月新增会员数
		List<Map<String,Object>> lstVipStatistics = vipStatisticsDao.queryVipStatisticsByMonth(thisMonth, nextMonth);
		if(lstVipStatistics!=null && lstVipStatistics.size()>0){
			for(Map<String,Object> vipStatistics : lstVipStatistics){
				Long guestId = (Long)(vipStatistics.get("guest_id"));
				Long vipNum = (Long)(vipStatistics.get("vip_num"));
				if(guestInfoMap.get(guestId)!=null){
					vipStatisticsDao.updateVipInfoByMonth(thisMonth, vipNum,guestId);
				}else{
					vipStatisticsDao.insertVipInfoByMonth(thisMonth, vipNum,guestId);
				}
			}
		}
	}
	
	@Override
	public int queryOnlineVipCount(Long guestId) {
		int allVipNum = vipStatisticsDao.queryVipStatistics(guestId);
		return allVipNum;
	}
	
	@Override
	public int queryNewVipCount(int months,Long guestId) {
		String dateForMonth = DateUtils.getTimeBeforeOrAfterMonth(months, "yyyy-MM", new Date());
		int numForMonth = vipStatisticsDao.queryVipNumByMonth(dateForMonth,guestId);
		return numForMonth;
	}
	
	@Override
	public int queryOldVipActive(Long guestId,int days) {
		String dateBefore = DateUtils.getTimeBeforeORAfter(days, "yyyy-MM-dd");
		dateBefore = dateBefore+" 00:00:00";
		List<Map<String,Object>> lstVipInfoBefore = vipStatisticsDao.queryVipInfoByDate(guestId, dateBefore);
		int percent = 0;
		StringBuilder userIds = new StringBuilder();
		if(lstVipInfoBefore!=null && lstVipInfoBefore.size()>0){
			for(Map<String,Object> vipInfoBefore : lstVipInfoBefore){
				userIds.append(String.valueOf(vipInfoBefore.get("user_id"))).append(",");
			}
			int buyActive = vipStatisticsDao.queryBuyActivityByUserIds(guestId, dateBefore, userIds.substring(0,userIds.length()-1));
			percent = Math.round(buyActive*(float)100/lstVipInfoBefore.size());
		}
		return percent;
	}
	
	@Override
	public List<Map<String,Object>> queryAllVipNumByDate(Long guestId, String startDate,String endDate) {
		try {
			long distance = DateUtils.getDistinceMonth(startDate+"-01", endDate+"-01");
			//获取该商家截止日期以前每月的所有数据
			List<Map<String,Object>> lstVipNumForMonth = vipStatisticsDao.queryVipNumForMonthByDate(endDate,guestId);
			List<Map<String,Object>> lstVipNumInfo = new ArrayList<Map<String,Object>>();
			Map<String,Long> vipNumMap = new HashMap<String, Long>();
			Long allNumBeforeStartDate = 0L;
			int incRate = 0;
			if(lstVipNumForMonth!=null && lstVipNumForMonth.size()>0){
				for(Map<String,Object> vipNumForMonthMap : lstVipNumForMonth){
					String dateTime = (String)vipNumForMonthMap.get("date_time");
					Long vipNum = (Long)vipNumForMonthMap.get("vip_num");
					//取到開始日期之前的会员总数
					if(DateUtils.getDistinceMonth(startDate+"-01", dateTime+"-01")<0){
						allNumBeforeStartDate = allNumBeforeStartDate + vipNum;
					}
					vipNumMap.put(dateTime,vipNum);
				}
		     }
			for(int i=0;i<=distance;i++){
				Date sourceTime = DateUtils.parseToDate(startDate, "yyyy-MM");
				String nextMonth = DateUtils.getTimeBeforeOrAfterMonth(1, "yyyy-MM", sourceTime);
				Map<String,Object> vipNumInfo = new HashMap<String, Object>();
				Long vipNum = vipNumMap.get(startDate);
				if(vipNum==null){
					vipNumInfo.put("date", startDate);
					vipNumInfo.put("vip_num",0L);
					vipNumInfo.put("vip_all_num",allNumBeforeStartDate);
					vipNumInfo.put("inc_rate", 0);
				}else{
					vipNumInfo.put("date", startDate);
					vipNumInfo.put("vip_num",vipNum);
					if(allNumBeforeStartDate==0){
						vipNumInfo.put("inc_rate",0);
					}else{
						incRate = Math.round(vipNum*(float)100/allNumBeforeStartDate);
						vipNumInfo.put("inc_rate",incRate);
					}
					allNumBeforeStartDate = allNumBeforeStartDate + vipNum;
					vipNumInfo.put("vip_all_num",allNumBeforeStartDate);
				}
				startDate = nextMonth;
				lstVipNumInfo.add(vipNumInfo);
			}
			  return lstVipNumInfo;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Map<String, Object> queryVipStatistics(Long guestId) {
		Map<String,Object> vipStatisticsMap = new HashMap<String, Object>();
		//线上会员总数
		int onLineVipNum = queryOnlineVipCount(guestId);
		//本月新增会员数
		int newVipNum = queryNewVipCount(0,guestId);
		//三个月内新增会员数
		int newVipNum3 = queryNewVipCount(-2,guestId);
		//十二个月内新增会员数
		int newVipNum12 = queryNewVipCount(-11,guestId);
		//30天老会员活跃度
		int activeDegree30 = queryOldVipActive(guestId,-30);
		//60天老会员活跃度
		int activeDegree60 = queryOldVipActive(guestId,-60);
		vipStatisticsMap.put("onLineVipNum",onLineVipNum);
		vipStatisticsMap.put("newVipNum",newVipNum);
		vipStatisticsMap.put("newVipNum3",newVipNum3);
		vipStatisticsMap.put("newVipNum12",newVipNum12);
		vipStatisticsMap.put("activeDegree30",activeDegree30);
		vipStatisticsMap.put("activeDegree60",activeDegree60);
		return vipStatisticsMap;
	}

	@Override
	public List<Map<String, Object>> queryVipProduct(Map<String, Object> params) {
		List<Map<String, Object>> data = vipDao.queryVipProduct(params);
		if(data != null && data.size() > 0){
			Set<Long> goodsids = new HashSet<Long>();
			Set<Long> guestids = null;
			if(params.get("isConsume").toString().equals("true")){
				guestids = new HashSet<Long>();
			}
			for (Map<String, Object> map : data) {
				goodsids.add(Long.parseLong(map.get("goods_id").toString()));
				if(params.get("isConsume").toString().equals("true")){
					guestids.add(Long.parseLong(map.get("sub_guest_id").toString()));
				}
			}
			
			Map<Long, Map<String, Object>> convertgoods = getGoodsMap(goodsids);
			Map<Long, Map<String, Object>> convertguests = getSubGuestMap(guestids);
			
			/*List<Map<String, Object>> goods = null;
			Map<Object, Map<String, Object>> convertgoods = null;
			if(goodsids != null){
				goods = evaluationDao.getGoodsByIds(goodsids);
				convertgoods = new HashMap<Object, Map<String,Object>>();
				for(Map<String, Object> map : goods){
					convertgoods.put(map.get("goodsid"), map);
				}
			}
			List<Map<String, Object>> guests = null;
			Map<Object, Map<String, Object>> convertguests = null;
			if(guestids != null){
				guests = evaluationDao.getSubGuests(guestids);
				convertguests = new HashMap<Object, Map<String,Object>>();
				for(Map<String, Object> map : guests){
					convertguests.put(map.get("merchantid"), map);
				}
			}*/
			for (Map<String, Object> map : data) {
				map.put("product_type", ProductTypeUtil.getProductType(String.valueOf(map.get("biz_type")), String.valueOf(convertgoods.get(map.get("goods_id")).get("couponcash"))));
				if(params.get("isConsume").toString().equals("true")){
					map.put("guest_name", convertguests.get(map.get("sub_guest_id")).get("merchantname"));
				}
			}
		}
		return data;
	}

	@Override
	public int queryVipCount(Map<String, Object> params) {
		return vipDao.queryVipCount(params);
	}

	@Override
	public int queryVipProductCount(Map<String, Object> params) {
		return vipDao.queryVipProductCount(params);
	}

	@Override
	public Map<String, Object> queryMenuByOrderId(Long trxorderId,Long guestId) {
		int count = vipDao.queryTrxOrderGoodsCount(trxorderId, guestId);
		Map<String, Object> map = null;
		if(count > 0){
			List<Map<String, Object>> data = vipDao.queryMenuByOrderId(trxorderId);
			map = orderDao.queryOrderPrice(trxorderId);
			map.put("data", data);
		}
		return map;
	}

	@Override
	public Map<String, Object> queryOnlineOrder(Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> tgTemp = vipStatisticsDao.queryTuanGouDetail(params);
		if(tgTemp != null && tgTemp.size() > 0){
			Map<Long, Map<String, Object>> tgDetail = new HashMap<Long, Map<String,Object>>();
			for(Map<String, Object> map : tgTemp){
				String trxStatus = String.valueOf(map.get("trx_status"));
				Long goodsid = Long.parseLong(map.get("goods_id").toString());
				if(tgDetail.containsKey(goodsid)){
					tgDetail.get(goodsid).put("sale_count", Long.parseLong(tgDetail.get(goodsid).get("sale_count").toString()) + 1);
					if(trxStatus.equals(TrxStatus.USED.toString()) 
							|| trxStatus.equals(TrxStatus.COMMENTED.toString())){
						tgDetail.get(goodsid).put("consumed_count", Long.parseLong(tgDetail.get(goodsid).get("consumed_count").toString()) + 1);
					}
					if(trxStatus.equals(TrxStatus.REFUNDTOACT.toString()) 
							|| trxStatus.equals(TrxStatus.REFUNDTOBANK.toString())){
						tgDetail.get(goodsid).put("refund_count", Long.parseLong(tgDetail.get(goodsid).get("refund_count").toString()) + 1);
					}
				}else {
					Map<String, Object> tmpMap = new HashMap<String, Object>();
					tmpMap.put("goods_id", goodsid);
					tmpMap.put("goods_type", ProductTypeUtil.getProductType(String.valueOf(map.get("biz_type")), String.valueOf(map.get("couponcash"))));
					tmpMap.put("goods_name", map.get("goodsname"));
					tmpMap.put("sale_count", 1);
					if(trxStatus.equals(TrxStatus.USED.toString()) 
							|| trxStatus.equals(TrxStatus.COMMENTED.toString())){
						tmpMap.put("consumed_count", 1);
					}else {
						tmpMap.put("consumed_count", 0);
					}
					if(trxStatus.equals(TrxStatus.REFUNDTOACT.toString()) 
							|| trxStatus.equals(TrxStatus.REFUNDTOBANK.toString())){
						tmpMap.put("refund_count", 1);
					}else {
						tmpMap.put("refund_count", 0);
					}
					tgDetail.put(goodsid, tmpMap);
				}
			}
			List<Long> goodsids = new ArrayList<Long>();
			goodsids.addAll(tgDetail.keySet());
			Collections.sort(goodsids);
			List<Map<String, Object>> tuanGouDetail = new ArrayList<Map<String,Object>>();
			for(Long goodId : goodsids){
				tuanGouDetail.add(tgDetail.get(goodId));
			}
			result.put("tuanGouDetail", tuanGouDetail);
		}
		List<Map<String, Object>> general = new ArrayList<Map<String,Object>>();
		Map<String, Object> tgGeneral = new HashMap<String, Object>();
		params.put("isMenu", 0);
		int tgCount = vipStatisticsDao.queryTotalCount(params);
		int tgNewVipCount = vipStatisticsDao.queryNewVipCount(params);
		int tgOldVipCount = vipStatisticsDao.queryOldVipCount(params);
		tgGeneral.put("name", "团购");
		tgGeneral.put("count", tgCount);
		tgGeneral.put("newVipCount", tgNewVipCount);
		tgGeneral.put("oldVipCount", tgOldVipCount-tgNewVipCount);
		Map<String, Object> sdGeneral = new HashMap<String, Object>();
		params.put("isMenu", 1);
		int sdCount = vipStatisticsDao.queryTotalCount(params);
		int sdNewVipCount = vipStatisticsDao.queryNewVipCount(params);
		int sdOldVipCount = vipStatisticsDao.queryOldVipCount(params);
		sdGeneral.put("name", "网上商店");
		sdGeneral.put("count", sdCount);
		sdGeneral.put("newVipCount", sdNewVipCount);
		sdGeneral.put("oldVipCount", sdOldVipCount-sdNewVipCount);
		general.add(tgGeneral);
		general.add(sdGeneral);
		result.put("general", general);
		return result;
	}
}
