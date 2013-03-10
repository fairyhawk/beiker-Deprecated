package com.beike.util.hao3604j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.entity.goods.Goods;
import com.beike.service.goods.GoodsService;
import com.beike.util.DateUtils;
import com.beike.util.PinyinUtil;

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
public class Tuan360ApiThread extends Thread {
	private final Log logger = LogFactory.getLog(Tuan360ApiThread.class);
	private final String trxid;
	private final String tuan360UserId;
	private final String contextPath;
	private final TrxorderGoodsService trxorderGoodsService;
	private final GoodsService goodsService;
	
	public Tuan360ApiThread(String trxid, String tuan360UserId, String contextPath,
			TrxorderGoodsService trxorderGoodsService, GoodsService goodsService){
		this.trxid = trxid;
		this.tuan360UserId = tuan360UserId;
		this.contextPath = contextPath;
		this.trxorderGoodsService = trxorderGoodsService;
		this.goodsService = goodsService;
	}
	
	@Override
	public void run(){
		logger.info("========================doSaveOrderToTuan360 start===========================");
		doSaveOrderToTuan360();
		logger.info("========================doSaveOrderToTuan360 end===========================");
	}
	
	private void doSaveOrderToTuan360(){
		try{
			//查询交易订单商品
			List<TrxorderGoods> lstGoodsCount = trxorderGoodsService.preQryInWtDBFindByTrxId(Long.parseLong(trxid));
			if(lstGoodsCount!=null && lstGoodsCount.size()>0){
				logger.info("lstGoodsCount.size()===" + lstGoodsCount.size());
				StringBuilder bufGoodsIds = new StringBuilder();
				//Map<String, Long> goodsCountMap = new HashMap<String, Long>();
				for (TrxorderGoods orderGoods : lstGoodsCount) {
					//秒杀商品订单不回传 add by qiaowb 2012-08-10
					if(orderGoods.getTrxRuleId() == 3){
						continue;
					}
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
						//秒杀商品订单不回传 add by qiaowb 2012-08-10
						if(orderGoods.getTrxRuleId() == 3){
							continue;
						}
						String goodsId = String.valueOf(orderGoods.getGoodsId());
						Goods curGoods = goodsMap.get(goodsId);

						String cityPY = PinyinUtil.hanziToPinyin(curGoods.getCity(),"");
						if(org.apache.commons.lang.StringUtils.isEmpty(cityPY)){
							cityPY = "www";
						}
						//long goodsCount = goodsCountMap.get(goodsId);
						int goodsCount = 1;
						Tuan360OrderParams tuan360Params = new Tuan360OrderParams();
						//qid INT 360用户ID，360用户唯一标识
						tuan360Params.setQid(tuan360UserId);
						//order_id INT 订单号
						tuan360Params.setOrder_id(orderGoods.getTrxGoodsSn());
						//order_time int 订单时间，格式：20100714090000
						tuan360Params.setOrder_time(DateUtils.toString(orderGoods.getCreateDate(), "yyyyMMddHHmmss"));
						//pid string 商品标识
						tuan360Params.setPid(goodsId);
						//price string 商品单价 单位:元 例如：19.20元
						tuan360Params.setPrice(String.valueOf(orderGoods.getPayPrice()));
						//number int 购买数量
						tuan360Params.setNumber(goodsCount);
						//total_price int 总价 单位:元 例如：300.50
						tuan360Params.setTotal_price(String.valueOf(orderGoods.getPayPrice()));
						//goods_url string 网站商品url，例：http://www.sitename.com/######
						tuan360Params.setGoods_url("http://" + cityPY.toLowerCase() + ".qianpin.com/goods/" + goodsId + ".jsp");
						//title string 商品短标题,例：云南原生态云南火锅。20汉字以内，用于团购提醒
						if(curGoods.getGoodsTitle()!=null){
							if(curGoods.getGoodsTitle().length()>20){
								tuan360Params.setTitle(curGoods.getGoodsTitle().substring(0, 20));
							}else{
								tuan360Params.setTitle(curGoods.getGoodsTitle());
							}
						}else{
							tuan360Params.setTitle("");
						}
						
						//desc string 商品描述，例：仅售78元！价值186元的简单爱蛋糕（黑森林蛋糕/南瓜无糖慕斯），两款任选其一
						tuan360Params.setDesc(orderGoods.getGoodsName());
						//spend_close_time int 消费截止时间，例：20100714090000
						if(orderGoods.getOrderLoseDate() == null){
							tuan360Params.setSpend_close_time(DateUtils.toString(new Date(), "yyyyMMddHHmmss"));
						}else{
							tuan360Params.setSpend_close_time(DateUtils.toString(orderGoods.getOrderLoseDate(), "yyyyMMddHHmmss"));
						}
						//merchant_addr string 商家地址，例：朝阳区建国路178号汇通时代广场
						tuan360Params.setMerchant_addr("-");
						
						Tuan360Client.saveToTuan360(tuan360Params);
					}
				}
			}
		}catch(Exception ex){
			logger.debug("saveOrderToTuan360 Exception");
			ex.printStackTrace();
		}
	}
}