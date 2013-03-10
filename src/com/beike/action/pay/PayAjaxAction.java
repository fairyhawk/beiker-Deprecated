package com.beike.action.pay;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.common.exception.BaseException;
import com.beike.entity.common.Sms;
import com.beike.entity.user.User;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.util.RandomNumberUtils;
import com.beike.util.TrxConstant;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.singletonlogin.SingletonLoginUtils;
@Controller
public class PayAjaxAction {
	
	private final Log logger = LogFactory.getLog(PayAjaxAction.class);
	private static final int SMS_RANDOM = 6;
	private static final String SMS_TYPE = "15";
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	@Autowired
	private SmsService smsService;
	/**
	 * 
	 * 功能:发送短信 ajax调用 参数: 
	 * 1.手机号:USER_MOBILE 
	 * 2.短信模板:smstemplate
	 * 返回: 
	 *    1.登录超时:login_timeout
	 *    2.验证多次:validate_timeout 
	 *    3.发送成功:ok //PM新需求，点击发送一次最多验证三次。(在此没有用到)
	 * @author ljp   
	 */
	// 单独发短信
	@RequestMapping("/pay/trxSendSms.do")
	public void trxSendSmsValidate(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("+++++++++++++==============this is trxSendSmsValidate  start ==========");
		//要发送的手机号
		String mobile = request.getParameter("USER_MOBILE");
		try {
			String result = "no";//返回值
			String smstemplate = TrxConstant.TRX_BEFORE_CHECK_PHONE_CODE;
			//获得用户id
			Long uid = SingletonLoginUtils.getLoginUserid(request);
			
			//从缓存服务器获得用户
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user != null) {
				user.setMobile(mobile);
				//缓存服务器上的校验码 (格式为     手机校验码:是否校验过( 0 未校验 1为已校验 ))
				String msCheckPhoneCode = (String) memCacheService.get(TrxConstant.TRX_RANDOMNUMBER_NEW + uid + mobile );
				
				if (msCheckPhoneCode != null && !"".equals(msCheckPhoneCode)) {
					String[] str = msCheckPhoneCode.split(":");
					String code = str[1];
					if (code != null && code.length()>0) {
						sendSmsValidate(mobile, request, smstemplate, code,user, response);
						result = "ok"; 
					}else{
						sendSmsValidate(mobile, request, smstemplate, "",user, response);
						result = "ok";
					}
	
				}else{
					// 发送短信
					sendSmsValidate(mobile, request, smstemplate, "", user, response);
					result = "ok";
				}
			}
			
			
			logger.info("+++++++++++user is "+user.getId()+"++++++++++++send msm to phone result is "+result);
			response.getWriter().write(result);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
		
	
	/**
	 * 发送短信验证
	 * 
	 * @param mobile  要发送电话
	 * @param smsTemplate短信模板 
	 * @param validateCode 
	 * @author ljp
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> sendSmsValidate(String mobile,
			HttpServletRequest request, String smsTemplate,
			String validateCode, User user, HttpServletResponse response) {
		Map<String, String> smsMap = null;

		String vCode = "";//手机校验码
		if (validateCode != null && !"".equals(validateCode)) {
			
			vCode = validateCode;
			
		}
		Sms sms = null;
		try {
			sms = smsService.getSmsByTitle(smsTemplate);
		} catch (BaseException e) {
			e.printStackTrace();
		}
		if (sms != null) {
			SmsInfo sourceBean = null;
			String content = "";
			String template = sms.getSmscontent();
			String randomNumbers = "";
			// update by ye.tian 2011-11-23 要求注册每次发送验证码是相同的
			if ("".equals(vCode)) {
				randomNumbers = RandomNumberUtils.getRandomNumbers(SMS_RANDOM);
				// 剩余条数不足了。加日志看。add by wenhua.cheng
				logger.info("++++++++++++randomNumbers:" + randomNumbers
						+ "+++++++++++++++++");

			} else {
				randomNumbers = vCode;
			}

			// 短信参数
			Object[] param = new Object[] { randomNumbers };
			content = MessageFormat.format(template, param);
			sourceBean = new SmsInfo(mobile, content, SMS_TYPE, "1");
			smsMap = smsService.sendSms(sourceBean);
			
			logger.info("++++++++++=====trxSendSmsValidate ========== save memCacheService ======mobile=="+mobile+"=======phoneValidateCode"+randomNumbers);
			memCacheService.set(TrxConstant.TRX_RANDOMNUMBER_NEW + user.getId()+ mobile , "1:"+randomNumbers+":0" ,TrxConstant.CHECK_PHONE_CODE_TIMEOUT );


			logger.info("++++++user mobile+++++++++++++" + user.getMobile()+ "++++++++++++++");

			if (smsMap == null) {
				smsMap = new HashMap<String, String>();
			}
			smsMap.put("validateCode", randomNumbers);
		}
		return smsMap;
	}
	
	/**
	 * ajax 校验手机支付码
	 * @param phoneValidCode手机号
	 * @return 输出页面内容            no 校验失败
	 *                        yes校验成功
	 *                   no_code 缓存服务器上没有校验码
	 * @author ljp
	 */
	@RequestMapping("/pay/checkPhoneCode.do")
	public String checkPhoneCode(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("+++++++++++++==============this is checkPhoneCode ajax   start ==========");
		//输入的手机校验码
		String phoneValidCode = request.getParameter("phoneValidCode");
		
		//取得当前用户信息
		User user = SingletonLoginUtils.getMemcacheUser(request);
		
		//返回信息
		String result = "no";
		if(user != null){
			//从缓存服务器上取手机校验码
			String smsPwdMemKey=TrxConstant.TRX_RANDOMNUMBER_NEW + user.getId()+ user.getMobile() ;
			String msCheckPhoneCode = (String) memCacheService.get(smsPwdMemKey);
			if(msCheckPhoneCode != null && !"".equals(msCheckPhoneCode) ){
				String[] temp = msCheckPhoneCode.split(":");
					String phoneCheckCodeForMS=temp[1];
					
					//如果缓存服务器上无有取到报错
					if("".equals(phoneCheckCodeForMS) || phoneCheckCodeForMS == null){
						result = "no_code";
					}else if(!phoneCheckCodeForMS.equals(phoneValidCode)){
						//如果缓存中的校验码与输入不一致报错
						result = "no";
					}else{
						result = "yes";
						memCacheService.set(smsPwdMemKey,"1:"+phoneValidCode+":1",TrxConstant.CHECK_PHONE_CODE_TIMEOUT);//此处更新mem
					}
					logger.info("++++++ms check phone code is +"+phoneCheckCodeForMS+"++++++++++checkPhoneCode for ajax result is "+ result);
			}
		}
		
		try {
			response.getWriter().write(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
}
