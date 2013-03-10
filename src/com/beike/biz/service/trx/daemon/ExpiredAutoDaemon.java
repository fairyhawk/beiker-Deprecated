package com.beike.biz.service.trx.daemon;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.ExpiredService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.impl.ExpiredServiceImpl;
import com.beike.util.TrxConstant;

/**
 * @Title: ExpiredAutoDaemon.java
 * @Package com.beike.biz.service.trx
 * @Description: 到时自动过期
 * @date May 30, 2011 6:25:08 AM
 * @author wh.cheng
 * @version v1.0
 */
@Service("expiredAutoDaemon")
public class ExpiredAutoDaemon {

	public Log logger = LogFactory.getLog(ExpiredServiceImpl.class);

	@Autowired
	public TrxorderGoodsService trxorderGoodsService;

	@Autowired
	public ExpiredService expiredService;
	
	//@Autowired
	//private TrxOrderService trxOrderService;
	//@Autowired
	//private PartnerCommonService partnerCommonService;

	public void executeAutoExpired() {
		// TODO Auto-generated method stub

		logger.info("++++++++++++++++start auto expired ++++++++++++");
		int notifyCount = trxorderGoodsService.qryLoseListByIsRefundCount(new Date(), false);
		int leng = 0;
		int daemonLength = TrxConstant.DAENON_LENGTH;
		if(notifyCount>0){
			leng = (notifyCount+daemonLength)/daemonLength;
		
		}else{
			return;
		}
		for(int i=0;i<leng;i++){
			int start = i*daemonLength;
		// 取出过期且不支持退款的订单
		List<TrxorderGoods> trxorderGoodsList = trxorderGoodsService.qryLoseListByIsRefund(new Date(), false,start,daemonLength);

		if (trxorderGoodsList != null && trxorderGoodsList.size() != 0) {

			// 调用过期
			for (TrxorderGoods item : trxorderGoodsList) {
				logger.info("++++++++++++++++start item expired :"+ "TrxGoodsSn:" + item.getTrxGoodsSn() + "++++++++++"
						+ "trxOrderGoodsId:" + item.getId()+ "++++isSendMerVou:" + item.isSendMerVou()+ "+++++++++");
				try {
					
					//TrxOrder trxorder = trxOrderService.findById(item.getTrxorderId());
					//Long userId = trxorder.getUserId();
					//如果是分销商进来的订单
					//PartnerInfo partnerInfo = partnerCommonService.qryParterByUserIdInMem(userId);
					//58不支持千品侧进行过期操作（变更为支持）
					/*if(partnerInfo!=null){
						
						if(PartnerApiType.TC58.name().equals(partnerInfo.getApiType())){
							continue;
						}
					}*/

					//if (!item.isSendMerVou()) { // 如果是发平台凭证               -----------------更改数据库isSendMerVou的属性后注释掉---------
					expiredService.processExpired(item);

					//} else { // 如果是发商家凭证

					// expiredService.processExpiredToUsed(item);
					// //需求变更，此功能去掉：用购买后立即置位已使用和 一次性将发送商家验证码的商品订单置为已使用

					//}

				} catch (BaseException e) {
					logger.error(e);
					e.printStackTrace();
				} catch (Exception e) {
					logger.error(e);
					e.printStackTrace();
				}

			}
		}
		}

	}
}
