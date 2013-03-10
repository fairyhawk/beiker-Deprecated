package com.beike.wap.action.goods;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.pay.PayInfoParam;
import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.entity.user.User;
import com.beike.service.goods.GoodsService;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MMerchant;
import com.beike.wap.service.MGoodsService;
import com.beike.wap.service.MMerchantService;
import com.beike.wap.utils.WapBaiduApiThread;

/**
 * Title : GoodsAction
 * <p/>
 * Description :商品购买信息Action
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
 * <pre>1     2011-10-19    lvjx            Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-10-19
 */
@Controller
@RequestMapping("/wap/goods/goodsBuyController.do")
public class MGoodsBuyAction extends MBaseUserAction {

	@RequestMapping(params = "method=buyGoodsStep1")
	public ModelAndView buyGoodsStep1(
			HttpServletRequest request, HttpServletResponse response,
			ModelMap modelMap) {
		try {
			// 增加判断是否登录，未登录进入登录界面
			String goodsId = request.getParameter("goodsIds");
			int goodsIdInt = 0;
			if (StringUtils.validNull(goodsId)) {
				goodsIdInt = Integer.parseInt(goodsId);
			}
			MGoods mGoods = goodsService.queryDetailShowMes(goodsIdInt);
			modelMap.addAttribute("mGoods", mGoods);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		ModelAndView step1View = new ModelAndView("wap/buy/buyStep1");
		return step1View;
	}

	@SuppressWarnings("static-access")
	@RequestMapping(params = "method=buyGoodsStep2")
	public ModelAndView buyGoodsStep2(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
		ModelAndView step2View = null;
		try {
			
			String flag = request.getParameter("flag");
			
			String goodsId = request.getParameter("goodsId");
			String buySum = request.getParameter("sum");
			
			int goodsIdInt = 0;
			if (StringUtils.validNull(goodsId)) {
				goodsIdInt = Integer.parseInt(goodsId);
			}
			MGoods mGoods = goodsService.queryDetailShowMes(goodsIdInt);
			modelMap.addAttribute("mGoods", mGoods);
			User user = getMemcacheUser(request);
			if (null == user) {
				return new ModelAndView("redirect:/wap/user/toUserLogin.do");
			}
			modelMap.addAttribute("user", user);
			
			modelMap.addAttribute("buySum", buySum);
			if (!StringUtils.validNull(buySum)) {
				modelMap.addAttribute("error", "购买数量必须填写!");
				return new ModelAndView("wap/buy/buyStep1");
			}
			if (!this.validatorDigital(buySum)) {
				modelMap.addAttribute("error", "购买数量只能填写数字!");
				return new ModelAndView("wap/buy/buyStep1");
			}
			int buyCount = Integer.parseInt(buySum.trim());
			if (0 == buyCount) {
				modelMap.addAttribute("error", "购买数量必须大于0 !");
				return new ModelAndView("wap/buy/buyStep1");
			}
			if (0!=mGoods.getVirtualCount()&&mGoods.getVirtualCount() < buyCount) {
				modelMap.addAttribute("error", "个人可购买数量为"
						+ mGoods.getVirtualCount() + "件!");
				return new ModelAndView("wap/buy/buyStep1");
			}
			int salesCount = mGoods.getMaxcount()
					- Integer.parseInt(mGoods.getGoodsCount());
			if (salesCount < buyCount) {
				modelMap.addAttribute("error", "可售剩余数量为" + salesCount + "件!");
				return new ModelAndView("wap/buy/buyStep1");
			}
			double rebateSum = mGoods.getRebatePrice() * buyCount;
			String rebateSumStr = new BigDecimal(rebateSum).setScale(2, BigDecimal.ROUND_HALF_UP).toString(); 
			modelMap.addAttribute("rebateSum", rebateSumStr);
			double totalMoney = mGoods.getCurrentPrice() * buyCount;
			modelMap.addAttribute("totalMoney", totalMoney);

			// TODO 调用内部余额查询接口
			Map<String, String> hessianMap = new HashMap<String, String>();
			hessianMap.put("userId", String.valueOf(user.getId()));
			hessianMap.put("reqChannel","WAP");
			Map<String, String> rspMap = trxHessianServiceGateWay
					.getActByUserId(hessianMap);
			if (null == rspMap) {
				modelMap.addAttribute("error", "账户余额获取失败！");
				return new ModelAndView("wap/buy/buyStep1");
			}
			if (!"1".equals(rspMap.get("rspCode"))) {
				modelMap.addAttribute("error", "账户余额获取异常！");
				return new ModelAndView("wap/buy/buyStep1");
			}
			String balanceAmount = rspMap.get("balance");
			double amount = 0d;
			double pay = 0d;
			// 判断余额是否足够，如果足够isOK的值为isOK，页面显示使用余额支付
			if (StringUtils.validNull(balanceAmount)) {
				amount = Double.valueOf(balanceAmount);
				if (amount >= totalMoney) {
					modelMap.addAttribute("isOK", "isOK");
				} else {
					pay = totalMoney - amount;
					String payStr = new BigDecimal(pay).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
					modelMap.addAttribute("pay", payStr);
				}
			}
			modelMap.addAttribute("balanceAmount", balanceAmount);
			if (StringUtils.validNull(flag)) {
				if ("pwdnull".equals(flag)) {
					modelMap.addAttribute("error", "密码不能为空!");
				}
				if ("pwdwrong".equals(flag)) {
					modelMap.addAttribute("error", "密码错误!");
				}
				if("pwdformat".equals(flag)){
					modelMap.addAttribute("error", "密码为6-16位英文字母、数字或下划线");
				}
				if ("single".equals(flag)) {
					modelMap.addAttribute("error", "个人可购买数量为"
							+ mGoods.getVirtualCount() + "件!");
				}
				if ("total".equals(flag)) {
					modelMap.addAttribute("error", "可售剩余数量为" + salesCount
							+ "件!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		step2View = new ModelAndView("wap/buy/buyStep2");
		return step2View;
	}

	@RequestMapping(params = "method=buyGoodsStep3")
	public ModelAndView buyGoodsStep3(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
		ModelAndView step3View = null;
		try {
			String goodsId = request.getParameter("goodsId");
			String buySum = request.getParameter("sum");
			String pwd = request.getParameter("pwd");
			if(!MobilePurseSecurityUtils.isPasswordAvailable(pwd)){
				modelMap.addAttribute("goodsId", goodsId);
				modelMap.addAttribute("sum", buySum);
				modelMap.addAttribute("flag", "pwdformat");
				return new ModelAndView("redirect:/wap/goods/goodsBuyController.do?method=buyGoodsStep2");
			}
			super.setCookieUrl(request, response);
			User user = getMemcacheUser(request);
			if (null == user) {
				return new ModelAndView("redirect:/wap/user/toUserLogin.do");
			}
			if (!StringUtils.validNull(pwd)) {
				modelMap.addAttribute("goodsId", goodsId);
				modelMap.addAttribute("sum", buySum);
				modelMap.addAttribute("flag", "pwdnull");
				return new ModelAndView(
						"redirect:/wap/goods/goodsBuyController.do?method=buyGoodsStep2");
			}
			String customerkey = user.getCustomerkey();
			String secretPassword = MobilePurseSecurityUtils.secrect(pwd,
					customerkey);
			if (!(secretPassword.equals(user.getPassword()))) {
				modelMap.addAttribute("goodsId", goodsId);
				modelMap.addAttribute("sum", buySum);
				modelMap.addAttribute("flag", "pwdwrong");
				return new ModelAndView(
						"redirect:/wap/goods/goodsBuyController.do?method=buyGoodsStep2");
			}

			int goodsIdInt = 0;
			if (StringUtils.validNull(goodsId)) {
				goodsIdInt = Integer.parseInt(goodsId);
			}
			Map<String, String> hessianMap = new HashMap<String, String>();
			// Map rspMap = null;
			MGoods mGoods = goodsService.queryDetailShowMes(goodsIdInt);
			int buyCount = Integer.parseInt(buySum);
			// 下面这两个判断需要重新跳转,跳转之后需要修改step2中的提示信息
			if (0!=mGoods.getVirtualCount()&&mGoods.getVirtualCount() < buyCount) {
				modelMap.addAttribute("goodsId", goodsId);
				modelMap.addAttribute("sum", buySum);
				modelMap.addAttribute("flag", "single");
				return new ModelAndView("wap/buy/buyStep1");
			}
			int salesCount = mGoods.getMaxcount()
					- Integer.parseInt(mGoods.getGoodsCount());
			if (salesCount < buyCount) {
				modelMap.addAttribute("goodsId", goodsId);
				modelMap.addAttribute("sum", buySum);
				modelMap.addAttribute("flag", "total");
				return new ModelAndView("wap/buy/buyStep1");
			}

			String orderLoseAbsDate = String.valueOf(mGoods
					.getOrderLoseAbsDate());
			String orderLoseDate = String.valueOf(mGoods.getOrderLoseDate());
			if(!"null".equals(orderLoseDate)&&StringUtils.validNull(orderLoseDate)){
				if(orderLoseDate.trim().length()<18){
					orderLoseDate = orderLoseDate.substring(0, 10).concat(" 00:00:00");
				}
			}
			Date orderLoseDateStr = DateUtils.compareDateInNull(new Date(),
					orderLoseAbsDate, orderLoseDate);
			String overTime = DateUtils.formatDate(orderLoseDateStr,
					"yyyy-MM-dd");

			String userId = String.valueOf(user.getId());
			String userTel = user.getMobile();

			// 主装客户端Hessian数据
			
			hessianMap.put("goodsCount",buySum);
			hessianMap.put("goodsId",goodsId );
			
			hessianMap.put("userId", userId);
			hessianMap.put("reqChannel","WAP");
			hessianMap.put("userIp", StringUtils.getIpAddr(request));

/*			Map<String, String> rspMap = trxHessianServiceGateWay
				.createTrxOrder(hessianMap);*/
			Map<String, String> rspMap =null;
			if (rspMap == null) {
				logger.info("+++++++++++userId:" + userId + "->RSPCODE is null"+ "+++++++++++++++++++++++++++++++++");
				// request.setAttribute("ERRMSG", "支付失败！");
				// return "error";
				throw new Exception();
			}

			String rspStatus = rspMap.get("status");
			String rspCode = rspMap.get("rspCode");
			logger.info("++++++++rspStatus:" + rspStatus + "->rspCode:"
					+ rspCode + "+++++++++++");
			

			if ("SUCCESS".equals(rspStatus) && "1".equals(rspCode)) {
				baiduWapOrdersReturn(rspMap.get("trxOrderId"), request);
				
				// 如果余额支付成功，直接到成功页面
				modelMap.addAttribute("overTime", overTime);
				modelMap.addAttribute("userTel", userTel);
				modelMap.addAttribute("goodsId", goodsId);
				return new ModelAndView(
						"redirect:/wap/goods/goodsBuyController.do?method=paySuccess");

			} else {
				// request.setAttribute("ERRMSG", "支付失败！");
				// return "error";
				logger.info("+++++++++++++remote call trxhessian end:"
						+ System.currentTimeMillis());
				throw new Exception();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
	}
	
	/**
	 * 百度wap订单回传（支付成功后）
	 * @param trxOrderId
	 * @param request
	 */
	private void baiduWapOrdersReturn(String trxOrderId, HttpServletRequest request){
		try{
			//百度参数（存放在Cookie里）
			String baiduParam = WebUtils.getCookieValue("BAIDU_REFERER_PARAM", request);
			if (baiduParam != null) {
				logger.info("baiduParam===" + baiduParam);
			}
			if (org.apache.commons.lang.StringUtils.isNotEmpty(baiduParam)) {
				//百度团购
				String tn = "";
				String baiduid = "";
				String chkGoodsId = "";
				if (baiduParam != null && !"".equals(baiduParam)) {
					String[] aryParam = baiduParam.split("\\|");
					if (aryParam != null && aryParam.length >= 3) {
						tn = aryParam[0];
						baiduid = aryParam[1];
						chkGoodsId = aryParam[2];
					}
				}
				logger.info("=====start saveOrderToBaidu thread=====");
				WapBaiduApiThread baiduThread = new WapBaiduApiThread(trxOrderId, null, chkGoodsId, tn, baiduid, propertyUtil.getProperty("BAIDU_API_BONUS"), request.getContextPath(), trxorderGoodsService,
						wgoodsService,null);
				baiduThread.start();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@RequestMapping(params = "method=paySuccess")
	public ModelAndView paySuccess(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
		ModelAndView success = null;
		try {
			String overTime = request.getParameter("overTime");
			String userTel = request.getParameter("userTel");
			String goodsId = request.getParameter("goodsId");

			PayInfoParam payInfoParam = new PayInfoParam();
			payInfoParam.setUserTel(userTel);
			payInfoParam.setGoodsId(goodsId);
			payInfoParam.setVoucherCode(overTime);
			modelMap.addAttribute("pay", "paySuccess");
			modelMap.addAttribute("payInfoParam", payInfoParam);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		success = new ModelAndView("wap/buy/buyStep3");
		return success;
	}

	@RequestMapping(params = "method=toShopChart")
	public ModelAndView goShopChartToWeb(HttpServletRequest request,HttpServletResponse response,
			ModelMap modelMaps) {
		ModelAndView view = null;
		try {
			//super.setCookieUrl(request, response);
			User user = getMemcacheUser(request);
			if (null == user) {
				return new ModelAndView("redirect:/wap/user/toUserLogin.do");
			}
			String goodsId = request.getParameter("goodsId");
			String buySum = request.getParameter("sum");
			MMerchant merchant = merchantService.getBrandByGoodId(goodsId);
			boolean flag = false;
			if(null!=merchant){
				flag = goodsService.addShopItem(goodsId, String.valueOf(merchant.getMerchantid()), Integer.parseInt(buySum),String.valueOf(user.getId()));
			}
			//如果失败的话跳转到哪里?
			if(flag){
				view = new ModelAndView("wap/buy/buyStep3");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		return view;
	}

	private static boolean validatorDigital(String buySum) {
		boolean flag = false;
		Pattern p = Pattern.compile("(\\d)+");
		Matcher m = p.matcher(buySum.trim());
		if (m.matches()) {
			flag = true;
		}
		return flag;
	}

	@Resource(name = "wapGoodsService")
	private MGoodsService goodsService;
	
	// 订单商品明细
	@Resource(name = "trxorderGoodsService")
	private TrxorderGoodsService trxorderGoodsService;
	@Autowired
	private GoodsService wgoodsService;
	private static PropertyUtil propertyUtil = PropertyUtil
	.getInstance(Constant.PROPERTY_FILE_NAME);
	

	@Resource(name = "wapClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;

	@Resource(name = "mMerchantService")
	private MMerchantService merchantService;

	private final Log logger = LogFactory.getLog(MGoodsBuyAction.class);
}
