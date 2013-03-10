package com.beike.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.entity.goods.Goods;
import com.beike.form.BaiduOrderParams;
import com.beike.service.goods.GoodsService;

/**      
 * project:beiker  
 * Title:
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Oct 26, 2011 5:46:34 PM     
 * @version 1.0
 */
public class BaiduApiThread extends Thread {
	private final Log logger = LogFactory.getLog(BaiduApiThread.class);
	private String trxid;
	private String baiduUserId;
	private String chkGoodsId;
	private String tn;
	private String baiduid;
	private String bonusRate;
	private String contextPath;
	private TrxorderGoodsService trxorderGoodsService;
	private GoodsService goodsService;
	private String csid;
	
	public BaiduApiThread(String trxid, String baiduUserId, String chkGoodsId, 
			String tn, String baiduid, String bonusRate, String contextPath,
			TrxorderGoodsService trxorderGoodsService, GoodsService goodsService,
			String csid){
		this.trxid = trxid;
		this.baiduUserId = baiduUserId;
		this.chkGoodsId = chkGoodsId;
		this.tn = tn;
		this.baiduid = baiduid;
		this.bonusRate = bonusRate;
		this.contextPath = contextPath;
		this.trxorderGoodsService = trxorderGoodsService;
		this.goodsService = goodsService;
		this.csid = csid;
	}
	
	public void run(){
		logger.info("========================doSaveOrderToBaidu start===========================");
		doSaveOrderToBaidu();
		logger.info("========================doSaveOrderToBaidu end===========================");
	}
	
	private void doSaveOrderToBaidu(){
		try{
			//查询交易订单商品
			List<TrxorderGoods> lstGoodsCount = trxorderGoodsService.preQryInWtDBFindByTrxId(Long.parseLong(trxid));
			if(lstGoodsCount!=null && lstGoodsCount.size()>0){
				StringBuilder bufGoodsIds = new StringBuilder();
				//Map<String, Long> goodsCountMap = new HashMap<String, Long>();
				for (TrxorderGoods orderGoods : lstGoodsCount) {
					if(bufGoodsIds.indexOf(String.valueOf(orderGoods.getGoodsId()))<0){
						bufGoodsIds.append(String.valueOf(orderGoods.getGoodsId())).append(",");
					}
				}
				
				if (bufGoodsIds != null && bufGoodsIds.length() > 0) {
					bufGoodsIds.deleteCharAt(bufGoodsIds.length() - 1);
				}
				List<Goods> lstGoods = goodsService.getGoodsDaoByIdList(bufGoodsIds.toString());
				Map<String, Goods> goodsMap = new HashMap<String, Goods>();
				for (Goods goods : lstGoods) {
					goodsMap.put(String.valueOf(goods.getGoodsId()), goods);
				}
				
				if(lstGoodsCount!=null && lstGoodsCount.size()>0){
					for(int i=0;i<lstGoodsCount.size();i++){
						TrxorderGoods orderGoods = lstGoodsCount.get(i);
						String goodsId = String.valueOf(orderGoods.getGoodsId());
						logger.info("goodsId===" + goodsId + "===chkGoodsId===" + chkGoodsId);
						Goods curGoods = goodsMap.get(goodsId);
						//百度推广链接产品，需要写入百度团购
						if(goodsId.equals(chkGoodsId) &&  orderGoods.getBizType() == 0){
							String cityPY = PinyinUtil.hanziToPinyin(curGoods.getCity(),"");
							if(org.apache.commons.lang.StringUtils.isEmpty(cityPY)){
								cityPY = "www";
							}
							//long goodsCount = goodsCountMap.get(goodsId);
							long goodsCount = 1;
							BaiduOrderParams baiduParams = new BaiduOrderParams();
							//订单号，在提交方系统中唯一
							baiduParams.setOrder_id(orderGoods.getTrxGoodsSn());
							//团购商品短标题 <255 bytes
							baiduParams.setTitle(orderGoods.getGoodsName());
							//商品描述，例如： 价值186元的简单爱蛋糕（南瓜无糖） <2048bytes
							baiduParams.setSummary(orderGoods.getGoodsName());
							//团购商品图片（海报）url<255bytes
							baiduParams.setLogo(StaticDomain.getDomain("") + contextPath + Constant.UPLOAD_IMAGES_URL + curGoods.getLogo3());
							//团购商品url（需要和提交给百度导航的xml api中的商品地址完全一致）<255bytes
							if(csid!=null && csid.startsWith("api_free_baidunew")){
								baiduParams.setUrl("http://" + cityPY.toLowerCase() + ".qianpin.com/goods/" + goodsId + ".jsp?abacusoutsid=api_free_baidunew_"+goodsId);
							}else{
								baiduParams.setUrl("http://" + cityPY.toLowerCase() + ".qianpin.com/goods/" + goodsId + ".jsp?abacusoutsid=api_free_baidu_"+goodsId);
							}
							//商品单价 单位：分 如2100表示rmb21.00
							baiduParams.setPrice((long)(100*orderGoods.getPayPrice()));
							//购买数量
							baiduParams.setGoods_num(goodsCount);
							//总价 单位：分 例如：300000
							baiduParams.setSum_price(baiduParams.getPrice() * baiduParams.getGoods_num());
							//消费券过期时间，自Jan 1 1970 00:00:00 GMT的秒数; 0为不限制
							//先计算出最终的过期时间再入库
							/*String orderLoseDateDate = DateUtils.toString(curGoods.getOrderLoseDate(),"yyyy-MM-dd HH:mm:ss");
							if(orderLoseDateDate == null || "".equals(orderLoseDateDate)){
								orderLoseDateDate = "null";
							}
							Date orderLoseDateResult = DateUtils.compareDateInNull(new Date(), String.valueOf(curGoods.getOrderLoseAbsDate()),
									orderLoseDateDate);
							if(orderLoseDateResult == null){
								baiduParams.setExpire(0);
							}else{
								baiduParams.setExpire(orderLoseDateResult.getTime()/1000);
							}*/
							if(orderGoods.getOrderLoseDate() == null){
								baiduParams.setExpire(0);
							}else{
								baiduParams.setExpire(orderGoods.getOrderLoseDate().getTime()/1000);
							}
							//商家地址，例如：朝阳区建国路178号汇通时代广场; <1024bytes
							baiduParams.setAddr("");
							//百度uid，如无tn参数，则此参数必填
							baiduParams.setUid(baiduUserId);
							//用户手机号
							baiduParams.setMobile("");
							//百度推广渠道参数
							if(goodsId.equals(chkGoodsId)){
								//从tuan.baidu.com过来的链接中获取（建议保持在cookie，下单时保存在数据库，便于付款成功后回传）
								baiduParams.setTn(tn);
								//从tuan.baidu.com过来的链接中获取（建议保持在cookie，下单时保存在数据库，便于付款成功后回传）
								baiduParams.setBaiduid(baiduid);
							}
							//百度分成金额（单位：分），值为订单总价*分成比例，定为8%
							baiduParams.setBonus((long)(baiduParams.getSum_price()*Double.parseDouble(bonusRate)));
							//用户下单时间，即订单号码生成的时间（非用户支付完成时间），自Jan 1 1970 00:00:00 GMT的秒数
							if(orderGoods.getCreateDate()!=null){
								baiduParams.setOrder_time(orderGoods.getCreateDate().getTime()/1000l);
							}else{
								baiduParams.setOrder_time(new Date().getTime()/1000l);
							}
							//团购商品在百度团购API中对应的城市名称
							baiduParams.setOrder_city(curGoods.getCity());
							
							logger.info("baiduParams===" + baiduParams);
							
							BaiduOauthApiUtil.saveOrder(baiduParams);
						}
					}
				}
			}
		}catch(Exception ex){
			logger.debug("saveOrderToBaidu Exception");
			ex.printStackTrace();
		}
	}
}