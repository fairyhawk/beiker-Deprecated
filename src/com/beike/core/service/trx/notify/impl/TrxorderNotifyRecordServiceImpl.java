package com.beike.core.service.trx.notify.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.enums.trx.NotifyType;
import com.beike.common.enums.trx.TrxBizType;
import com.beike.core.service.trx.notify.TrxorderNotifyProcessService;
import com.beike.core.service.trx.notify.TrxorderNotifyRecordService;
import com.beike.core.service.trx.partner.PartnerCommonService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.entity.notify.TrxorderNotifyRecord;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;

/**
 * 订单余额过期提醒服务实现
 * 
 * @author jianjun.huo
 * 
 */
@Repository("trxorderNotifyRecordService")
public class TrxorderNotifyRecordServiceImpl implements TrxorderNotifyRecordService {

	private final Log logger = LogFactory.getLog(TrxorderNotifyRecordServiceImpl.class);

	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;

	@Autowired
	private TrxSoaService trxSoaService;
	@Autowired
	private PartnerCommonService partnerCommonService;
	@Autowired
	private TrxorderNotifyProcessService trxorderNotifyProcessService;

	@Override
	public void noTscProcessAllLoseTrxOrder() {
		String strDate = DateUtils.getStringDate();
		String loseToNow10DateStr = DateUtils.getNextDay(strDate, "10");// 当前时间后推10天
		String loseToNow3DateStr = DateUtils.getNextDay(strDate, "3");// 当前时间后推3天

		Date loseToNow10Date = DateUtils.toDate(loseToNow10DateStr, "yyyy-MM-dd HH:mm:ss");
		Date loseToNow3Date = DateUtils.toDate(loseToNow3DateStr, "yyyy-MM-dd HH:mm:ss");

		// 10天过期总数量
		int lose10Count = trxorderGoodsDao.findLoseCountDate(loseToNow10Date);
		// 3天过期总数量
		int lose3Count = trxorderGoodsDao.findLoseCountDate(loseToNow3Date);

		logger.info("++++++++strDate:" + strDate + "loseToNow10DateStr:" + loseToNow10DateStr + "+++loseToNow3DateStr:" + loseToNow3DateStr + "+++++++++");
		logger.info("+++++++++++++lose10Count=" + lose10Count + "++++++++++++++++++++++++lose3Count=" + lose3Count);
		if (lose10Count + lose3Count == 0) {
			logger.info("++++++++qryAllLoseTrxOrder->lose10Count+lose3Count=0 +++++++++++");
			return;
		}

		int lostMaxCount = lose10Count > lose3Count ? lose10Count : lose3Count;
		int daemonLength = TrxConstant.DAENON_LENGTH / 2;
		int length = (lostMaxCount + daemonLength) / daemonLength;// 获取本日循环次数
		logger.info("++++qryAllLoseTrxOrder+++++++date=" + new Date() + "+++++++TrxOrder count=" + length + "+++++++++++++++");

		// 组装需要执行list
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 组装需要执行list并对其进行系统处理
		for (int i = 0; i < length; i++) {

			// 组装三天list
			int startCount = i * daemonLength;// 起步值
			if (lose3Count > startCount) {
				int endCount = lose3Count - startCount > daemonLength ? daemonLength : lose3Count - startCount;
				logger.info("++++++++++lose3Count+++++++++startCount=" + startCount + "++++endCount=" + endCount);
				List<Map<String, Object>> list3 = trxorderGoodsDao.findLoseNowDate(loseToNow3Date, startCount, endCount);
				logger.info("++++++++++lose3Count+++++++++list3=" + list3);
				list.addAll(list3);
			}
			// 组装十天list
			if (lose10Count > startCount) {
				int endCount = lose10Count - startCount > daemonLength ? daemonLength : lose10Count - startCount;
				logger.info("++++++++++lose10Count+++++++++startCount=" + startCount + "++++endCount=" + endCount);
				List<Map<String, Object>> list10 = trxorderGoodsDao.findLoseNowDate(loseToNow10Date, startCount, endCount);
				logger.info("++++++++++lose10Count+++++++++list10=" + list10);
				list.addAll(list10);
			}

			if (list != null && list.size() > 0) {

				for (Map<String, Object> map : list) {
					Date lostDate = (Date) map.get("orderLoseDate");
					String sqlDate = DateUtils.toString(lostDate, "yyyy-MM-dd HH:mm:ss");
					String twoDay = DateUtils.getTwoDay(sqlDate, strDate);// 两个日期间隔天数

					if ("10".equals(twoDay)) {
						map.put("notifyType", EnumUtil.transEnumToString(NotifyType.TENDAYS));
						map.put("afterDay", 10);

					} else if ("3".equals(twoDay)) {
						map.put("notifyType", EnumUtil.transEnumToString(NotifyType.THREE));
						map.put("afterDay", 3);
					}
				}

				logger.info("++++++++strDate:" + strDate + "++++++++++returnList.size()=" + list.size());
			}
			// 组装需要执行list结束

			// 对获取到的list进行系统处理
			for (Map<String, Object> map : list) {

				Long userId = (Long) map.get("userId");
				NotifyType notifyType = NotifyType.valueOf(map.get("notifyType").toString());
				TrxBizType bizType = TrxBizType.OVERDUE;
				boolean isNotify = false;
				// 分销商订单不做短信提醒
				PartnerInfo partnerInfo = partnerCommonService.qryParterByUserIdInMem(userId);// 获取分销商
				if (partnerInfo != null) {
					continue;
				}
				List<Long> goodsIdListForTitle = new ArrayList<Long>();// 商品简称
				goodsIdListForTitle.add((Long) map.get("goodsid"));
				Map<Long, String> goodsTitleMap = trxSoaService.findGoodsTitle(goodsIdListForTitle); // 查询商品简称
				String goodsTitle = goodsTitleMap.get(map.get("goodsid"));
				goodsTitle = StringUtils.cutffStr(goodsTitle, TrxConstant.smsVouGoodsNameCount, "");// 商品简称
				Date express = (Date) map.get("orderLoseDate");
				String expressStr = goodsTitle +":"+map.get("count").toString()+ ":" +map.get("afterDay").toString()+":"+ DateUtils.dateToStr(express);
				TrxorderNotifyRecord trxorderNotifyRecord = new TrxorderNotifyRecord(userId, new Date(), isNotify, expressStr, bizType, notifyType);
				
				// 数据处理
				trxorderNotifyProcessService.processTrxorderNotify(trxorderNotifyRecord);

			}
			list.clear();
		}
	}

}