package com.beike.wap.action.coupon;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.entity.user.User;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.MCookieKey;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.action.user.MUserAction;
import com.beike.wap.entity.MCoupon;
import com.beike.wap.entity.MCouponCatlog;
import com.beike.wap.entity.MMerchant;
import com.beike.wap.service.MCatalogService;
import com.beike.wap.service.MCouponDetailService;
import com.beike.wap.service.MMerchantService;

/**
 * Title : GoodsAction
 * <p/>
 * Description :商品信息Action
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com </P> JDK Version Used : JDK 5.0 +
 * <p/>
 * Modification History :
 * <p/>
 * 
 * <pre>
 * NO.    Date    Modified By    Why & What is modified
 * </pre>
 * 
 * <pre>1     2011-06-14    lvjx            Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-14
 */
@Controller
@RequestMapping("/wap/coupon/couponDetailController.do")
public class MCouponDetailAction extends MBaseUserAction {
	/** 日志记录 */
	private static Log log = LogFactory.getLog(MUserAction.class);
	
	@Autowired
	private MCouponDetailService mCouponDetailService;
	
	@Autowired
	private MCatalogService mCatalogService;
	
	@Autowired
	private MMerchantService mMerchantService;
	
	/** 短信服务接口 */
	@Autowired
	private SmsService smsService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	/** 读取汉字提示信息文件 */
	private static final PropertyUtil propertyUtil = PropertyUtil
	.getInstance(Constant.WAP_CH_INFO);
	
	private static final PropertyUtil pathPropertyUtil = PropertyUtil
	.getInstance(Constant.WAP_PATH);
	
	@RequestMapping(params = "method=showCouponDetail")
	public Object queryCouponShowMes(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		super.setCookieUrl(request, response);
		
		String err = request.getParameter("couponErr");
		if(err != null && !err.trim().equals(""))
		{
			model.addAttribute("couponErr", propertyUtil.getProperty(err));
		}
		else{
			model.addAttribute("couponErr", "");
		}
		
		String param = request.getParameter("couponId");
		log.info("show coupon details, coupon id is = " + param);
		if(null == param || "".equals(param)){
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		else{
			param = param.trim();
		}
		
		long couponId = 0;
		MCoupon coupon = null;
		try{
			couponId = Long.parseLong(param);
			coupon = mCouponDetailService.findById(couponId);
		}catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		
		if(null == coupon){
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		MCouponCatlog mCouponCatalog = null;
		try
		{
			mCouponCatalog = mCatalogService.getCouponCatlogById(couponId);
		}catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		
		Long browsecount = (Long) memCacheService
				.get(Constant.MEM_COUPON_BROWCOUNT + coupon.getId());
		Long downcount = (Long) memCacheService
				.get(Constant.MEM_COUPON_DOWNCOUNT + coupon.getId());
		
		if (browsecount == null) {
			browsecount = coupon.getBrowseCounts();
		}
		
		coupon.setBrowseCounts(browsecount+1);
		int validy = 60 * 60 * 24;
		if (downcount == null) {
			downcount = coupon.getDownCount();
			memCacheService.set(
					Constant.MEM_COUPON_DOWNCOUNT + coupon.getId(),
					downcount, validy);
		}
		coupon.setDownCount(downcount);
		
		// 增加浏览次数
		memCacheService.set(
				Constant.MEM_COUPON_BROWCOUNT + coupon.getId(),
				browsecount + 1, validy);
		MMerchant brand = null;
		List<MMerchant> branchList = null;
		try
		{
			brand = mMerchantService.getBrandById(coupon.getMerchantid());
			branchList = mMerchantService.getBranchByBrandId(coupon.getMerchantid());
		}catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		
		model.addAttribute("mCoupon", coupon);
		model.addAttribute("mCouponCatalog", mCouponCatalog);
		model.addAttribute("mBrand", brand);
		model.addAttribute("mBranchList", branchList);
		return new ModelAndView("wap/details/couponDetail");
	}
	
	@RequestMapping(params = "method=saveCoupon")
	public void saveCouponToLocal(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String param = request.getParameter("couponId");
		InputStream inStream = null;
		OutputStream outPut = null;
		String couponErr = "";
		long couponId = 0;
		MCoupon coupon = null;
		
		try
		{
			couponId = Long.parseLong(param);
			coupon = mCouponDetailService.findById(couponId);
			
			int bytesum = 0;
			int byteread = 0;
			String detail = coupon.getCouponDetailLogo();
			File wapFile = new File(pathPropertyUtil.getProperty("ZIP_UPLOADIMAGES_PATH") + detail);
			File downFile = null;
			if(!wapFile.exists())
			{
				File webFile = new File(pathPropertyUtil.getProperty("OLD_UPLOADIMAGES_PATH") + detail);
				if(!webFile.exists())
				{
					model.addAttribute("couponErr", MCookieKey.SAVE_COUPON_ERROR);
				}else
				{
					downFile = webFile;
				}
			}
			else
			{
				downFile = wapFile;
			}
			
            inStream = new FileInputStream(downFile);
            outPut = response.getOutputStream();
            
            Long downcount = (Long) memCacheService
			.get(Constant.MEM_COUPON_DOWNCOUNT + coupon.getId());
			if (downcount == null) {
				downcount = coupon.getDownCount();
			}
			
			log.info("down coupon : downcount = " + downcount);
			memCacheService.set(
					Constant.MEM_COUPON_DOWNCOUNT + coupon.getId(),
					downcount + 1);
			WebUtils.setMCookieByKey(response, "download_coupon_", "2", -1);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + coupon.getDetailLogoName() + "\"");
            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                System.out.println(bytesum);
                outPut.write(buffer, 0, byteread);
            }
		}catch (IOException e) {
			log.info("save coupon IOException where  id  = " + couponId);
			couponErr = MCookieKey.SAVE_COUPON_ERROR;
			e.printStackTrace();
		}catch (Exception e) {
			log.info("save coupon Exception where  id  = " + couponId);
			couponErr = MCookieKey.SAVE_COUPON_ERROR;
			e.printStackTrace();
		}
		finally{
			if(inStream != null){
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outPut != null)
			{
				try {
					outPut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			log.info("over ");
		}
//		model.addAttribute("couponErr", couponErr);
//		return new ModelAndView("redirect:/wap/coupon/couponDetailController.do?method=showCouponDetail&couponId="+couponId);
	}

	@RequestMapping(params = "method=sendCoupon")
	public Object sendCouponToMobiles(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String downmobile = request.getParameter("downmobile");
		String param = request.getParameter("couponId");
		
		long couponId = 0;
		if(downmobile == null || downmobile.trim().equals("") || !MobilePurseSecurityUtils.isJointMobileNumber(downmobile)){
			model.addAttribute("couponErr", MCookieKey.REG_MOBILE_FORMATE_ERROR);
			return new ModelAndView("redirect:/wap/coupon/couponDetailController.do?method=showCouponDetail&couponId="+param);
		}
		if(param == null || param.trim().equals("")){
			model.addAttribute("couponErr", MCookieKey.REG_MOBILE_FORMATE_ERROR);
			return new ModelAndView("redirect:/wap/coupon/couponDetailController.do?method=showCouponDetail&couponId="+param);
		}
		MCoupon coupon = null;
		try{
			couponId = Long.parseLong(param);
			coupon = mCouponDetailService.findById(couponId);
		}catch (Exception e) {
			model.addAttribute("couponErr", MCookieKey.SEND_COUPON_ERROR);
			return new ModelAndView("redirect:/wap/coupon/couponDetailController.do?method=showCouponDetail&couponId="+param);
		}
		if(coupon == null){
			model.addAttribute("couponErr", MCookieKey.SEND_COUPON_ERROR);
			return new ModelAndView("redirect:/wap/coupon/couponDetailController.do?method=showCouponDetail&couponId="+param);
		}
		
		String key = "COUPON_" + downmobile;
		String downMobileValue = (String) memCacheService.get(key);
		User user = getMemcacheUser(request);
		if(user == null || user.getMobile_isavalible() == 0||!downmobile.equals(user.getMobile()))
		{
			Date date = new Date();
			String nowDate = DateUtils.dateToStr(date);
			// 判断假如同一天已经download 5次了不能再下载了
			if (downMobileValue != null) {
				String svalue[] = downMobileValue.split("\\|");
				if (svalue != null && svalue.length == 2) {
					String vs = svalue[1];
					int count = Integer.parseInt(vs);

					if (count >= 5 && nowDate.equals(svalue[0])) {
						model.addAttribute("couponErr", MCookieKey.SEND_MORE_THAN_FIVE);
						return new ModelAndView("redirect:/wap/coupon/couponDetailController.do?method=showCouponDetail&couponId="+param);
					}
				}
			}
		}
		
		DateFormat dateFormate = new SimpleDateFormat("yyyy-MM-dd");
		// 发送短信
		StringBuilder message = new StringBuilder("");
		message.append("千品网优惠券编号为：").append(coupon.getCouponNumber()).append("，")
			.append(coupon.getSmstemplate()).append("有效期：")
			.append(dateFormate.format(coupon.getCreateDate()))
			.append("至").append(dateFormate.format(coupon.getEndDate()));
//		String message = "千品网优惠券编号为：" + coupon.getCouponNumber() + "，"
//				+ coupon.getSmstemplate() + "有效期："
//				+ dateFormate.format(coupon.getCreateDate()) + "至" + dateFormate.format(coupon.getEndDate()));
		log.info(message);
		SmsInfo sourceBean = new SmsInfo(downmobile, message.toString(), Constant.SMS_TYPE, "0");
		smsService.sendSms(sourceBean);
		// 记录memcache

		Date date = new Date();
		String nowDate = DateUtils.dateToStr(date);
		// 非第一次记录 取出次数加1
		if (downMobileValue != null) {
			String svalue[] = downMobileValue.split("\\|");
			String count = svalue[1];
			int scount = Integer.parseInt(count) + 1;
			if (nowDate.equals(svalue[0])) {
				memCacheService.set(key, svalue[0] + "|" + scount);
			} else {
				memCacheService.set(key, nowDate + "|" + scount);
			}
		} else {
			memCacheService.set(key, nowDate + "|" + 1);
		}
		// 下载记数
		Long downcount = (Long) memCacheService
				.get(Constant.MEM_COUPON_DOWNCOUNT + coupon.getId());
		if (downcount == null) {
			downcount = coupon.getDownCount();
		}
		memCacheService.set(
				Constant.MEM_COUPON_DOWNCOUNT + coupon.getId(),
				downcount + 1);

		return new ModelAndView("redirect:/wap/coupon/couponDetailController.do?method=showCouponDetail&couponId="+param);
	}

	/**
	 * 打开url链接，重试3次，防止网络不稳的情况
	 * @param logo 文件名称
	 * @return URLConnection
	 * @author k.w
	 */
	private URLConnection openUrlConn(String logo) {
		URLConnection conn = null;
		for(int i = 0; i < 3; i++)
		{
			try
			{
				StringBuilder logoUrl = new StringBuilder();
				logoUrl.append(Constant.WAP_URL_FIELD).append("/jsp/wap/uploadimages/").append(logo);
				URL url = new URL(logoUrl.toString()); 
				conn = url.openConnection();		
				return conn;
			}
			catch (Exception e) {
				e.printStackTrace();
				conn = null;
				continue;
			}
		}
		
		if(conn == null)
		{
			for(int i = 0; i < 3; i++)
			{
				try
				{
					StringBuilder logoUrl = new StringBuilder();
					logoUrl.append(Constant.WAP_URL_FIELD).append("/jsp/uploadimages/").append(logo);
					URL url = new URL(logoUrl.toString()); 
					conn = url.openConnection();
					return conn;
				}
				catch (Exception e) {
					e.printStackTrace();
					conn = null;
					continue;
				}
			}
		}
		return conn;
	}
}
