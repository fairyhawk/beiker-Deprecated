package com.beike.common.bean.trx.partner;

import java.util.Map;

import com.beike.common.enums.trx.TrxStatus;
import com.beike.util.DateUtils;
import com.beike.util.img.JsonUtil;
/**
 * 58参数转换以及加解密
 * @author q1w2e3r4
 *
 */
public class Par58OrderGenerator {
	/**
	 * 转换并组装58请求相关参数
	 * @param parmInfo
	 * @return
	 */
	public static Par58OrderParam transReqOrderInfo(String paramInfo){
		Par58OrderParam pop = new Par58OrderParam();
		if(paramInfo!=null&&!"".equals(paramInfo)){
			Map<String,Object> map = getParamMap(paramInfo);
			if(map.get("orderId")!=null){
				pop.setOrderId(map.get("orderId").toString());//订单号
			}
			if(map.get("groupbuyIdThirdpart")!=null){
				pop.setGoodsId((map.get("groupbuyIdThirdpart").toString()));//平台goodsId
			}
			if(map.get("prodPrice")!=null){
				pop.setPayPrice(map.get("prodPrice").toString()); //58支付价格
			}
			if(map.get("prodCount")!=null){
				pop.setProdCount(map.get("prodCount").toString());//商品数量
			}
			if(map.get("createDate")!=null){
				pop.setCreateDate(map.get("createDate").toString());//订单创建时间
			}
			if(map.get("payDate")!=null){
				pop.setPayDate(map.get("payDate").toString());//订单支付时间
			}
			if(map.get("state")!=null){
				pop.setState(map.get("state").toString());//订单状态
			}
			if(map.get("mobile")!=null){
				pop.setMobile(map.get("mobile").toString());//用户手机号
			}
			if(map.get("status")!=null){
				pop.setStatus(map.get("status").toString());//券状态
			}
			if(map.get("ticketId")!=null){
				pop.setVoucherId(map.get("ticketId").toString());//第三方券号,分销商传过来的券号，对应平台的voucherId
			}
			if(map.get("groupbuyId")!=null){
				pop.setOutGoodsId(map.get("groupbuyId").toString());
			}
			if(map.get("status")!=null){
				pop.setStatus(map.get("status").toString());//券状态
			}
			if(map.get("reason")!=null){
				pop.setReason(map.get("reason").toString());//退款原因
			}
			if(map.get("ticketIds")!=null){
				pop.setVoucherId(map.get("ticketIds").toString());//我侧voucherId
			}
			if(map.get("startTime")!=null){
				pop.setStartTime(DateUtils.toDate(map.get("startTime").toString(),"yyyy-MM-dd HH:mm:ss"));//起始时间
			}
			if(map.get("endTime")!=null){
				pop.setEndTime(DateUtils.toDate(map.get("endTime").toString(),"yyyy-MM-dd HH:mm:ss"));//结束时间
			}
		}
		
		return pop;
	}
	
	
	public static Map<String,Object> getParamMap(String paramInfo){
		Map<String,Object> jsonMap = JsonUtil.getMapFromJsonString(paramInfo);
		/*String[] paramArray = paramInfo.split("&");
		for(int i = 0;i<paramArray.length;i++){
			String key = paramArray[i].split("=")[0];
			String value = paramArray[i].split("=")[1];
			map.put(key, value);
		}*/
		return jsonMap;
	}
	
	/**
	 * 交易状态请求转换（对58）
	 * @param trxStatus
	 * @return
	 */
	public static String transReqTrxStatus(String  trxStatusStr){
		
		//0 未使用 1 已使用 2 已退款 3退款中
		//9 过期  10未使用退款，11已过期退款，12已消费退款（58责任），13已消费退款（商家责任）（去掉状态2，3，4）
		StringBuilder  trxStatusRstSB =new StringBuilder();
		String trxStatusRstStr="";
		if("-1".equals(trxStatusStr)){
			
			trxStatusRstSB.append(TrxStatus.SUCCESS);
			trxStatusRstSB.append(",");
			trxStatusRstSB.append(TrxStatus.USED);
	/*		trxStatusRstSB.append(",");
			trxStatusRstSB.append(TrxStatus.REFUNDACCEPT);*/
			trxStatusRstSB.append(",");
			trxStatusRstSB.append(TrxStatus.REFUNDTOACT);
			trxStatusRstSB.append(",");
			trxStatusRstSB.append(TrxStatus.EXPIRED);
			trxStatusRstSB.append(",");
			trxStatusRstSB.append(TrxStatus.COMMENTED);
			trxStatusRstSB.append(",");
			//trxStatusRstSB.append(TrxStatus.RECHECK);
			//trxStatusRstSB.append(TrxStatus.REFUNDTOBANK);
			trxStatusRstSB.append(TrxStatus.EXPIRED);

		}else if("0".equals(trxStatusStr)){
			
			trxStatusRstSB.append(TrxStatus.SUCCESS);
			
		}else if("1".equals(trxStatusStr)){
			
			trxStatusRstSB.append(TrxStatus.USED);
			trxStatusRstSB.append(",");
			trxStatusRstSB.append(TrxStatus.COMMENTED);
			
		}else if("10".equals(trxStatusStr) ||  "11".equals(trxStatusStr)){
			
			trxStatusRstSB.append(TrxStatus.REFUNDTOACT);
			
			/*}else if("3".equals(trxStatusStr)  || "4".equals(trxStatusStr)){//TODO  千品没有订单锁定功能，暂时用退款中替代锁定状态
			
			trxStatusRstSB.append(TrxStatus.REFUNDACCEPT);
			*/
		}else if("9".equals(trxStatusStr)){
			
			trxStatusRstSB.append(TrxStatus.EXPIRED);
			
		}
		if(trxStatusRstSB.length()>0){
			trxStatusRstStr=trxStatusRstSB.toString();
			if(trxStatusRstStr.indexOf(",")!=-1){
				trxStatusRstStr=trxStatusRstStr.replace(",", "','");
			}
			trxStatusRstStr="'"+trxStatusRstStr+"'";
		}
		//其余状态千品暂无，直接返回空
		return trxStatusRstStr;
		
	
		
	}
	
	
	/**
	 * 交易状态相应转换（对58）
	 * @param trxStatus
	 * @return
	 */
	public static String transRspTrxStatus(TrxStatus  trxStatusStr){
		
		//0 未使用 1 已使用
		//9 过期  10未使用退款，11已过期退款，12已消费退款（58责任），13已消费退款（商家责任）
		String statusCode="undefined";//约定状态外或者我们暂时不支持的统一为未定义 
		if(trxStatusStr!=null){
			if(TrxStatus.SUCCESS.equals(trxStatusStr)){
				
				statusCode= "0";
			}else if (TrxStatus.USED.equals(trxStatusStr)||TrxStatus.COMMENTED.equals(trxStatusStr)){
				
				statusCode="1";
				
			}else if (TrxStatus.EXPIRED.equals(trxStatusStr)){
				
				statusCode="9";
				
			}else if (TrxStatus.REFUNDTOACT.equals(trxStatusStr)){
				
				statusCode= "10";
				
			}
		
	}
		return statusCode;
}
	
}
	
