package com.beike.core.service.trx.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.core.service.trx.TrxorderGoodsSnService;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.entity.common.Sms;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;

/**
 * @Title: TrxorderGoodsSnServiceImpl.java
 * @Package com.beike.core.service.trx
 * @Description:订单商品号获取服务类实现类(新起事务，独立出来使AOP生效)
 * @date 4 1, 2012 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("trxorderGoodsSnService")
public class TrxorderGoodsSnServiceImpl implements TrxorderGoodsSnService {
	
	@Autowired
	private SmsService smsService;
	
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;

	private final Log logger = LogFactory.getLog(TrxorderGoodsSnServiceImpl.class);

	/**
	 * 商品订单号生成（包括循环避重。变更为预取）
	 * 
	 * @param trxorderGoods
	 * @return
	 */
	public String createTrxGoodsSn() {

		String trxGoodsTbInt = StringUtils.randomBase();// 随机一位数字
		long ll = System.currentTimeMillis();
		Map<String, String> resultMap = trxorderGoodsDao.findTrxGoodsSn(trxGoodsTbInt);
		long l2 = System.currentTimeMillis();
		logger.info("+++++++++++++++++++++++++++findTrxGoodsSn:" + (l2 - ll));
		String trxGoodsSnPosFix = resultMap.get("sn");// 订单号后缀

		String trxGoodsSnId = resultMap.get("id"); // 订单号对应主键
		long ss = System.currentTimeMillis();
		trxorderGoodsDao.delTrxGoodsSn(trxGoodsTbInt, Integer
				.parseInt(trxGoodsSnId)); // 删除该笔订单
		logger.info("+++++++++++++++++++++++++++delTrxGoodsSn:"+ (System.currentTimeMillis() - ss));

		StringBuffer trxGoodsSb = new StringBuffer();
		trxGoodsSb.append("QP");
		trxGoodsSb.append(trxGoodsSnPosFix);
		logger.info("++++trxGoodsSn:" + trxGoodsSb.toString()+ "+++++++++++++++++++++");//

		return trxGoodsSb.toString();

	}
	
	/**
	 * 商品订单号生成（包括循环避重。变更为预取）
	 * 
	 * @param trxorderGoods
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized String createTrxGoodsSnKing() {

		List<Map<String,Object>> trxGoodsSnListInMem = (List<Map<String,Object>>) TrxConstant.trxGoodsSnPrefetchTgSnList;

		if (trxGoodsSnListInMem == null || trxGoodsSnListInMem.size() == 0) {// 如果内存里已经用尽，再次到库中取
			String trxGoodsTbInt = StringUtils.randomBase();// 随机一位数字
			long ll = System.currentTimeMillis();
			List<Map<String, Object>> tgSnList = trxorderGoodsDao.findTrxGoodsSnForOfset(trxGoodsTbInt,TrxConstant.trxGoodsSnPrefetchCount);
			if (tgSnList == null || tgSnList.size() == 0) {
				// 预取失败报警
				logger.info("+++tgSnPrefetch ->tgSnList in DB is null+++++  ");
				alertTgSnPrefetch("预取商品订单号为空");
			}
			TrxConstant.conTrxGoodsSnSynchronizedList(tgSnList);// 放入到内存中，并将List做线程安全处理
			long l2 = System.currentTimeMillis();
			StringBuffer idS = new StringBuffer();
			for (Map<String, Object> map : tgSnList) {
				idS.append(map.get("id"));
				idS.append(",");
			}
			String idsStr = idS.deleteCharAt(idS.length() - 1).toString();
			trxorderGoodsDao.delTrxGoodsSnByIds(trxGoodsTbInt, idsStr); // 删除该次预取的订单号
			logger.info("+++++++findTrxGoodsSn++++trxGoodsSnListInMem:"+trxGoodsSnListInMem+"+++snId:"+idsStr+"++++gapTime:"+ (l2 - ll));
			trxGoodsSnListInMem = (List<Map<String, Object>>) TrxConstant.trxGoodsSnPrefetchTgSnList;
		}
		 
		Map<String,Object> resultMap = trxGoodsSnListInMem.get(0);
		String trxGoodsSnPosFix = resultMap.get("sn").toString();// 订单号后缀

		long ss = System.currentTimeMillis();
		logger.info("+++++++++++++++++++++++++++delTrxGoodsSn+++trxGoodsSnPosFix:"+trxGoodsSnPosFix+"++gapTime:"+ (System.currentTimeMillis() - ss));
		
		TrxConstant.trxGoodsSnPrefetchTgSnList.remove(resultMap);//从缓存中删除此条数据
		StringBuffer trxGoodsSb = new StringBuffer();
		trxGoodsSb.append("QP");
		trxGoodsSb.append(trxGoodsSnPosFix);
		logger.info("++++trxGoodsSn:" + trxGoodsSb.toString()+ "+++++++++++++++++++++");//
		return trxGoodsSb.toString();
	}
	
	
	/**
	 * 订单号预取报警
	 * 
	 * @param alterParam
	 */
	public void alertTgSnPrefetch(String alterParam) {
		try {
			String alterVouPrefetchTel = TrxConstant.alterVouPrefetchTel;//接收手机号和凭证报警共用
			if (alterVouPrefetchTel == null
					|| alterVouPrefetchTel.length() == 0) {

				return;
			}
			String[] alterVouPrefetchTelAry = alterVouPrefetchTel.split(",");
			int aryCount = alterVouPrefetchTelAry.length;

			// 短信参数
			Object[] smsParam = new Object[] { alterParam };

			Sms sms = smsService.getSmsByTitle(TrxConstant.TGSN_PREFETCH_ALTER_SMS_TEMPLATE);// 获取短信实体
			String template = sms.getSmscontent(); // 获取短信模板

			String contentResult = MessageFormat.format(template, smsParam);
			for (int i = 0; i < aryCount; i++) {
				String mobile = alterVouPrefetchTelAry[i];
				SmsInfo sourceBean = new SmsInfo(mobile, contentResult, "15","1");

				logger.info("+++++++++++ alterTgSnrefetchTel:mobile:" + mobile+ "+++alterParam:" + alterParam + "++++++++");
				smsService.sendSms(sourceBean);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);

		}
	}
}