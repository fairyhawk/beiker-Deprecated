package com.beike.core.service.trx.partner.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.VoucherInfo;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.core.service.trx.partner.PartnerVoucherService;
import com.beike.util.Configuration;
import com.beike.util.HttpUtils;
import com.beike.util.PartnerUtil;
import com.beike.util.img.JsonUtil;

/**
 * @Title: PartnerVoucherFor58ServiceImpl.java
 * @Package  com.beike.core.service.trx.parter
 * @Description:  合作分销商API 凭证远程查询及推送 service  for 58
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerVoucherFor58Service")
public class PartnerVoucherFor58ServiceImpl implements PartnerVoucherService{

	private final Log logger = LogFactory.getLog(PartnerVoucherFor58ServiceImpl.class);

	private static String getUrlByHostNo(String hostNo,String name){
		String url = Configuration.getInstance().getValue(hostNo+"_"+name);
		
		return url;
	}
    /**
     * 远程查询分销商voucher info
     * @param voucherInfo
     * @param userId
     * @return
     */
	@SuppressWarnings("unchecked")
	public Map<String,String> qryVoucherInfoToPar(VoucherInfo voucherInfo,PartnerInfo partnerInfo)
	{
		logger.info("+++++++++++++58TC++++++++++qryVoucherInfoToPar+++++start++++++++");
		Map<String,String>  resultMap=new HashMap<String,String>();
			/*   --------------------------此接口本次版本注释掉，为单笔查询接口
			//调用58查询接口 
			String ticketId =String.valueOf(voucherInfo.getVoucher().getId());
			String tickets[] = new String[]{ticketId};
			Map<String,Object> jsonMap = new HashMap<String, Object>();
			 ticketId = Arrays.toString(tickets);
			jsonMap.put("ticketIds", ticketId);
			String jsonStrArr = JsonUtil.getJsonStringFromMap(jsonMap);
			jsonStrArr = jsonStrArr.replace("\\", "");
			jsonStrArr = jsonStrArr.replace("\"[","[\"");
			 jsonStrArr = jsonStrArr.replace("]\"","\"]");
			String param = PartnerUtil.cryptDes(jsonStrArr,partnerInfo.getKeyValue());
			
			Map<String,String> map = new HashMap<String, String>();
			map.put("m", "emc.groupbuy.ticket.findinfo.byid");
			map.put("sn","1");
			map.put("appid",partnerInfo.getPartnerNo());
			map.put("f", "json");
			map.put("param", param);*/
		
		String ticketId = String.valueOf(voucherInfo.getVoucher().getId());
		String orderId = String.valueOf(voucherInfo.getTrxorder().getId());
		Map<String,Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("ticketId", ticketId);
		jsonMap.put("orderId", orderId);
		jsonMap.put("ticketIdIndex", "0");
		String jsonStr = JsonUtil.getJsonStringFromMap(jsonMap);
		logger.info("+++++++++++++58TC++++++++++qryVoucherInfoToPar+++jsonStr:"+jsonStr);
		String param = PartnerUtil.cryptDes(jsonStr,partnerInfo.getKeyValue());
		Map<String,String> map = new HashMap<String, String>();
		map.put("m","emc.groupbuy.order.ticketcheck");
		map.put("sn","1");
		map.put("appid",partnerInfo.getPartnerNo());
		map.put("f", "json");
		map.put("param", param);
			logger.info("+++++++++++++58TC++++++++++qryVoucherInfoToPar+++map:"+map);
			try {
				String url = getUrlByHostNo(partnerInfo.getPartnerNo(),partnerInfo.getApiType());
				List<String> responseStr =	 HttpUtils.URLPost(url, map);
				logger.info("++++++++++++responseStr=++++"+responseStr);
				if (responseStr.size() == 0 || responseStr == null) {
					resultMap.put("status","ANTI_VALIDATE");
				}else{
				String status = "";//返回数据状态
				String msg = "";//返回数据状态信息
				String data = "";//返回数据信息
				String trxStatus = "";//返回数据订单信息状态
				String result = "";//验券成功与否
					String currentResult = responseStr.get(0);
					
					Map<String,Object> objMap = JsonUtil.getMapFromJsonString(currentResult);
					if(objMap.get("status")!=null){
					status = objMap.get("status").toString();
					}
					if(objMap.get("msg")!=null){
					msg = objMap.get("msg").toString();
					}
					data = objMap.get("data").toString();
					//String desryptStr = PartnerUtil.decryptDes(currentResult,partnerInfo.getKeyValue());
					
					logger.info("+++++58TC++++++++++qryVoucherInfoToPar++++++++ticketId="+ticketId+"+++++++++"+"++++++++");
					if("10000".equals(status)){
						
					if (data == null || data.equals("")) {
						resultMap.put("status", "ANTI_VALIDATE");
					}else{
					String returnJson =	PartnerUtil.decryptDes(data,partnerInfo.getKeyValue());
					 //returnJson = returnJson.replace("[","");
					 //returnJson = returnJson.replace("]","");
					logger.info("+++++58TC++++++++++qryVoucherInfoToPar++++++++returnJson="+returnJson+"+++++++++++++++++");
					Map<String,Object> returnMap = JsonUtil.getMapFromJsonString(returnJson);
					logger.info("+++++58TC++++++++++qryVoucherInfoToPar++++++++returnJson="+returnJson+"+++++++++++++++++");
					Set<String> keySet =  returnMap.keySet();
					  for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
						  String skey = it.next();
							
							if("status".equals(skey)){
								trxStatus = returnMap.get(skey).toString();
							}
							if("result".equals(skey)){
								result = returnMap.get(skey).toString();
							}
						}
					  logger.info("++++++status="+status+"++++++++msg="+msg+"++++++++");
				
						
					
						
						//58订单状态判断
						if("1".equals(trxStatus)&&"1".equals(result)){
							resultMap.put("status","ALLOW_VALIDATE");
						}else{
							resultMap.put("status","ANTI_VALIDATE");
						}
					}
					}else{
						resultMap.put("status","ANTI_VALIDATE");
						logger.info("++++++++++++status="+status+"++++++++msg="+msg);
					}
				}
			} catch (IOException e) {
				resultMap.put("status","ANTI_VALIDATE");
				e.printStackTrace();
			}
			
			return resultMap;
		
	}
	

	

	/**
	 * 推送分销商voucher info
	 * @param voucherInfo
	 * @param partnerinfo
	 * @return
	 */
	public String pushVoucherInfo(VoucherInfo voucherInfo,PartnerInfo partnerInfo)
	{
		String returnStutas = "";

		logger.info("++++++++++++58TC+++++++pushVoucherInfo+++++++++++++");
		
			/*//调用58订单同步接口
			String ticketId = String.valueOf(voucherInfo.getVoucher().getId());
			String orderId = String.valueOf(voucherInfo.getTrxorder().getId());
			Map<String,Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put("ticketId", ticketId);
			jsonMap.put("orderId", orderId);
			jsonMap.put("ticketIdIndex", "0");
			String jsonStr = JsonUtil.getJsonStringFromMap(jsonMap);
			String param = PartnerUtil.cryptDes(jsonStr,partnerInfo.getKeyValue());
			Map<String,String> map = new HashMap<String, String>();
			map.put("m","emc.groupbuy.order.ticketcheck");
			map.put("sn","1");
			map.put("appid",partnerInfo.getPartnerNo());
			map.put("f", "json");
			map.put("param", param);
			
			noticeSendService.createNotice(partnerInfo.getPartnerNo(), partnerInfo.getApiType(),
					voucherInfo.getTrxorder().getOutRequestId(), map,"emc.groupbuy.order.ticketcheck");*/
				return returnStutas;



	
	
	}
	
	 

}
