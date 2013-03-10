package com.beike.util;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.common.entity.adweb.AdWeb;
import com.beike.common.entity.adweb.AdWebTrxInfo;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.service.adweb.AdWebService;
import com.beike.service.adweb.AdWebTrxInfoService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Mar 1, 2012
 * @author ye.tian
 * @version 1.0
 */

public class AdWebThread extends Thread {

	private Log logger = LogFactory.getLog(AdWebThread.class);

	private AdWebTrxInfoService adWebTrxInfoService;
	
	private TrxorderGoodsService trxorderGoodsService;
	
	private AdWebService adWebService;

	private String trxid;

	private String src;

	private String cid;

	private String wi;

	private Long userId;
	
	private int status;
	
	public AdWebTrxInfoService getAdWebTrxInfoService() {
		return adWebTrxInfoService;
	}

	public void setAdWebTrxInfoService(AdWebTrxInfoService adWebTrxInfoService) {
		this.adWebTrxInfoService = adWebTrxInfoService;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getWi() {
		return wi;
	}

	public void setWi(String wi) {
		this.wi = wi;
	}

	public String getTrxid() {
		return trxid;
	}

	public void setTrxid(String trxid) {
		this.trxid = trxid;
	}

	public AdWebThread() {

	}

	@Override
	public void run() {
		logger.info("===AdWebThread===begin===");
		try{
			logger.info("===AdWebThread===trxid===" + trxid + "===status===" + status);
				//根据来源 查询 广告联盟 是否存在
				AdWeb adWeb = adWebService.getAdWebByCode(src);
				if(adWeb!=null){
					//查询交易订单商品
					List<TrxorderGoods> lstGoodsCount = trxorderGoodsService.preQryInWtDBFindByTrxId(Long.parseLong(trxid));
					if(lstGoodsCount!=null && lstGoodsCount.size()>0){

//						for (TrxorderGoods trxorderGoods : lstGoodsCount) {
						
						
							double ordermoney =0.0d;
							for (TrxorderGoods orderGoods : lstGoodsCount) {
								//秒杀商品订单不回传 点菜 add by qiaowb 2012-08-10
								if(orderGoods.getTrxRuleId() == 3 || orderGoods.getBizType() == 1){
									continue;
								}
								ordermoney = ordermoney + orderGoods.getPayPrice();
							}
							Date createDate = lstGoodsCount.get(0).getCreateDate();
							Timestamp nowDate=new Timestamp(createDate.getTime());
							AdWebTrxInfo adWebTrx = new AdWebTrxInfo();
							adWebTrx.setAdwebid(adWeb.getAdwebid());
							adWebTrx.setTrxorderid(trxid);
							adWebTrx.setAdcid(cid);
							adWebTrx.setAdwi(wi);
							adWebTrx.setBuycount(1);
							adWebTrx.setOrderMoney(ordermoney);
							adWebTrx.setUserId(userId);
							adWebTrx.setStatus(status);
							
							adWebTrx.setOrderTime(nowDate);
							// 保存交易数据
							Long adTrxId = adWebTrxInfoService.addAdWebTrxInfo(adWebTrx);
							logger.info("===AdWebThread===adTrxId===" + adTrxId);
							
							
							// 回传广告联盟
							int random=(int) (Math.random()*10);
							String responseText="";
							//10以内随机数0-9  只有0不发 90%概率
							if(random>0){
								responseText = adWebTrxInfoService.httpRequestUrl("", 
											cid, wi, trxid, "1", "0", "");
								logger.info("===AdWebThread===responseText===" + responseText);
								if (!"0".equals(responseText)) {
									logger.info("http url error responsetext is " + responseText
											+ " trxid:" + trxid);
								}
							}
//						}
						
					}
				
				
				}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		logger.info("===AdWebThread===begin===");
	}

	public AdWebThread(AdWebTrxInfoService adWebTrxInfoService, TrxorderGoodsService trxorderGoodsService, AdWebService adWebService, 
			String trxid, Long userId, String src, String cid, String wi, int status) {
		this.adWebTrxInfoService = adWebTrxInfoService;
		this.trxorderGoodsService = trxorderGoodsService;
		this.adWebService = adWebService;
		this.trxid = trxid;
		this.src = src;
		this.cid = cid;
		this.wi = wi;
		this.userId = userId;
		this.status = status;
	}
}