package com.beike.core.service.trx.notice.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.partner.Par360buyOrderGenerator;
import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.common.enums.trx.NoticeStatus;
import com.beike.core.service.trx.notice.PartnerNoticeService;
import com.beike.core.service.trx.partner.PartnerCommonService;
import com.beike.util.Configuration;
import com.beike.util.HttpClientUtilMultiThread;
import com.beike.util.StringUtils;

/**   
 * @title: Partner360buyNoticeServiceImpl.java
 * @package com.beike.core.service.trx.notice.impl
 * @description: 通知接口京东实现
 * @author wangweijie  
 * @date 2012-12-7 下午03:10:54
 * @version v1.0   
 */
@Service("partner360buyNoticeService")
public class Partner360buyNoticeServiceImpl implements PartnerNoticeService{
	private static String BUY360_PARTNERNO = Configuration.getInstance().getValue("BUY360_PARTNERNO"); //京东
	private static final Log logger = LogFactory.getLog(Partner360buyNoticeServiceImpl.class);
	private static final int INTERVAL[] = {0,1,2,4,8};	// 通知策略0,1,2,4,8秒
	
	@Resource(name = "partnerCommonService")
	private PartnerCommonService partnerCommonService;
	
	@Override
	public long getDelaySecond(int times) {
		if(times < 0 || times >= INTERVAL.length) times = 1;
		return INTERVAL[times];
	}

	@Override
	public boolean isEnd(NoticeStatus noticeStatus,int times) {
		return times>INTERVAL.length-1;
	}

	@Override
	public int isSuccess(String resMsg,String methodType) {
		boolean isSuccess = false;
		try {
			PartnerInfo partnerInfo = partnerCommonService.qryAvaParterByNoInMem(BUY360_PARTNERNO);
			if(null == partnerInfo){
				logger.error("++++++++{ERROR}+++++++++++BUY360_PARTNERNO="+BUY360_PARTNERNO+",partnerInfo is NULL");
				return -1;
			}
			Map<String,String> resultCodeInfoMap = Par360buyOrderGenerator.getResultCodeInfo(resMsg);
//			String resultCode = resultCodeInfoMap.get("ResultCode");
			//303-优惠券已使用
			logger.error("++++++++360 message validate error+++resultCode="+resultCodeInfoMap);

			String data = Par360buyOrderGenerator.get360buyDataMessage(resMsg,partnerInfo.getPartnerNo(), partnerInfo.getSessianKey(), partnerInfo.getKeyValue());
			Map<String,String> resMap = new HashMap<String, String>();
			try{
				resMap = Par360buyOrderGenerator.xml2Map(data);
			}catch (Exception e) {
				logger.error("+++++++++++{ERROR}+++ 360buy++xml parse error++");
			}
			//目前只支持验券
			if("http://tuan.360buy.com/VerifyCouponRequest".equals(methodType)){
				String verifyResult = StringUtils.toTrim(resMap.get("VerifyResult"));
				
				logger.info("++++++++++++++360buy check +++verifyResult="+verifyResult);
				//优惠券返回结果
				//200-验证成功；301-优惠券不存在；302-优惠券已过期；303-优惠券已使用；304-优惠券等待退款；305-优惠券已退款
				if("200".equals(verifyResult) || "303".equals(verifyResult)){
					isSuccess = true;
				}
			}else{
				logger.error("+++++++++++{ERROR}+++ 360buy++interface is not open+++");
			}
		} catch (Exception e) {
			logger.error("++++++++360buy++++${ERROR}++parse meesage error",e);
//			e.printStackTrace();
		}
			
		return isSuccess?0:-1;
	}

	@Override
	public String send(String content) throws Exception {
		/*
		 * 获得发送地址和内容
		 * 数据库content存放格式为url?param=xxx&content=xxx
		 * 以?分割，下标0为url；下标1为content内容
		 */
		String [] urlContent = content.split("\\?",2);
		String url = urlContent[0];  //
		String requestMsg = urlContent[1];//
		logger.info("+++++++++=send req to 360buy++++url="+url);
		String responseXml = HttpClientUtilMultiThread.sendPostHTTP(url, requestMsg, Par360buyOrderGenerator.CHAR_ENCODING);		//发送并接受响应报文
		return responseXml;
	}


	@Override
	public int getMaxReSendNum() {
		return INTERVAL.length;
	}
	
	//京东不需要随机
	@Override
	public boolean needRandomSend() {
		return true;
	}
}
