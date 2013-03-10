package com.beike.action.pay;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.LogAction;
import com.beike.common.entity.trx.FilmGoodsOrder;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.RefundHandleType;
import com.beike.common.enums.trx.RefundSourceType;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.RefundService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.VoucherService;
import com.beike.dao.diancai.DianCaiDao;
import com.beike.dao.trx.FilmGoodsOrderDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.dao.trx.VoucherDao;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.entity.user.User;
import com.beike.page.Pager;
import com.beike.service.diancai.DianCaiService;
import com.beike.service.merchant.MerchantService;
import com.beike.util.Amount;
import com.beike.util.Constant;
import com.beike.util.RequestUtil;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.json.JSONException;
import com.beike.util.json.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * @Title: TrxOrderAction.java
 * @Package com.beike.action.pay
 * @Description: 我的订单/钱包action
 * @date Jun 13, 2011 2:16:22 PM
 * @author wh.cheng
 * @version v1.0
 */
@Controller
public class TrxOrderAction extends BaseTrxAction {
	private static Log logger = LogFactory.getLog(PurseAction.class);
	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;

	@Autowired
	private TrxorderGoodsService trxorderGoodsService;


	@Autowired
	private VoucherService voucherService;

	@Autowired
	private RefundService refundService;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private VoucherDao voucherDao;
	
	@Autowired
	private DianCaiService dianCaiService;
	
	@Autowired
	private DianCaiDao dianCaiDao;
	@Autowired
	private FilmGoodsOrderDao filmGoodsOrderDao;

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();
	
	@RequestMapping("/ucenter/showTrxGoodsOrder.do")
	public ModelAndView showTrxGoodsOrder(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) throws Exception {

		ModelAndView mav = new ModelAndView(VIEW_SHOW_TRX_ORDER);
		try {
			int cpage = RequestUtil.getUnsignedInt(request, Constant.CPAGE, 0);

			String qryType = request.getParameter("qryType");
			if (Constant.TRX_GOODS_UNCOMMENT.equals(qryType)
					|| Constant.TRX_GOODS_ALL.equals(qryType)
					|| Constant.TRX_GOODS_UNUSEED.equals(qryType)) {
			} else {
				qryType = Constant.TRX_GOODS_UNUSEED;
			}
			// TODO mem中取
			User user = SingletonLoginUtils.getMemcacheUser(request);
			Long userId = 0L;
			if (user != null) {
				if (user.getId() != 0) {
					userId = user.getId();
				}
			}
			// 跟据类型查出总记录数
			/*
			 * int totalRows = trxorderGoodsDao.findPageCountByUserId(userId,
			 * qryType);
			 */

			int totalRows = trxorderGoodsService.findPageCountByUserId(userId,
					qryType);

			// 查出显示在页面的类型订单数
			/*
			 * int unUsedCount = trxorderGoodsDao.findCountByUserId(userId,
			 * Constant.TRX_GOODS_UNUSEED);
			 */

			int unUsedCount = trxorderGoodsService.findCountByUserId(userId,
					Constant.TRX_GOODS_UNUSEED);

			/*
			 * int unCommentCount = trxorderGoodsDao.findCountByUserId(userId,
			 * Constant.TRX_GOODS_UNCOMMENT);
			 */

			int unCommentCount = trxorderGoodsService.findCountByUserId(userId,
					Constant.TRX_GOODS_UNCOMMENT);

			Pager pager = new Pager(cpage, totalRows, Constant.TRX_PAGE_SIZE);

			// Pager pager=PagerHelper.getPager(cpage, totalRows,
			// Constant.TRX_PAGE_SIZE);

			List<List<TrxorderGoods>> trxOrderGoodsList = trxorderGoodsService
					.listPageByUserIdAndType(userId, pager.getStartRow(),
							Constant.TRX_PAGE_SIZE, qryType);

			// trxorderGoodsService.injectGoodsInfo(trxOrderGoodsList, qryType);

			modelMap.addAttribute(Constant.PB, pager);
			modelMap.addAttribute("cpage", cpage);
			modelMap.addAttribute("resultList", trxOrderGoodsList);
			modelMap.addAttribute("unUsedCount", unUsedCount);
			modelMap.addAttribute("unCommentCount", unCommentCount);
			modelMap.addAttribute("user", user);
			// modelMap.addAttribute("totalRows", totalRows);
			modelMap.addAttribute("qryType", qryType);

			return mav;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("++++++++++++++++Trx-Exception:" + e.getStackTrace());
			throw new Exception();
		}

	}

	@RequestMapping("/ucenter/sendVoucher.do")
	public void reSendVouhcer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			// 加入验证码start
			String validCode = request.getParameter(Constant.USER_REGIST_CODE);

			String cookieCode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE",
					request);
			String sessionCode = (String) memCacheService.get("validCode_"
					+ cookieCode);
			Map<String, String> map = new HashMap<String, String>();
			map.put("RSPCODE", "2"); // 验证码不正确
			String jsonStr = "";
			try {
				jsonStr = JsonUtil.mapToJson(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (validCode == null || validCode.length() == 0
					|| cookieCode == null || sessionCode.length() == 0
					|| !validCode.equalsIgnoreCase(sessionCode)) {
				response.getWriter().write(jsonStr);
				return;

			}
			// 验证码end

			String emailStr = request.getParameter("email");
			String phoneStr = request.getParameter("mobile");
			String phoneStr2 = request.getParameter("mobile2");
			String sendType = "";
			logger.info("+++++" + emailStr + phoneStr + "+++++");

			User user = SingletonLoginUtils.getMemcacheUser(request);
			Long userId = getLoginUserId(request);
			Long trxgoodsId = (long) RequestUtil.getInt(request, "trxgoodsId");

			if (user != null) {
				if (user.getId() != 0) {
					userId = user.getId();
				}
			}
			// 基本参数校验
			if (StringUtils.validNull(phoneStr)
					&& StringUtils.validNull(emailStr)) {

				sendType = "BOTH";
			} else if (StringUtils.validNull(phoneStr)) {
				sendType = "SMS";

			} else if (StringUtils.validNull(emailStr)) {
				sendType = "EMAIL";

			} else {
				response.getWriter().write("FAILED");
				return;

			}

			// 用户鉴权
			boolean verifyResult = trxorderGoodsService.verifyBelong(trxgoodsId, userId,true);
			if (verifyResult) {

				// 凭证重发
				voucherService.reSendVoucher(trxgoodsId, phoneStr, emailStr,sendType,"");
				if(phoneStr2!=null&&!"".equals(phoneStr2)){
					response.getWriter().write(phoneStr2);
				}else{
				response.getWriter().write("SUCCESS");
				}
				return;

			}
		} catch (BaseException be) {
			response.getWriter().write("STATUS_INVALID");
			be.printStackTrace();
			logger.debug(be);
			return;

		} catch (Exception e) {
			e.printStackTrace();
			// response.getWriter().write("SYSTEM_ERROR");
			logger.debug(e);
			throw new Exception();
		}
	}

	@RequestMapping("/ucenter/refundApply.do")
	public void refundApply(HttpServletRequest request, HttpServletResponse response) {

		// 加入验证码start
		String validCode = request.getParameter(Constant.USER_REGIST_CODE);

		String cookieCode = WebUtils.getCookieValue("RANDOM_VALIDATE_CODE",request);
		String sessionCode = (String) memCacheService.get("validCode_"
				+ cookieCode);
		Map<String, String> map = new HashMap<String, String>();
		map.put("RSPCODE", "2"); // 验证码不正确
		String jsonStr = "";
		try {
			jsonStr = JsonUtil.mapToJson(map);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (validCode == null || validCode.length() == 0
				|| cookieCode == null || sessionCode.length() == 0
				|| !validCode.equalsIgnoreCase(sessionCode)) {
			try {
				response.getWriter().write(jsonStr);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;

		}
		// 验证码end
		
		String trxGoodsId = request.getParameter("trxGoodsId");
		String description = request.getParameter("description");
		Long trxGoodsIdLong = new Long(trxGoodsId);
		User user = SingletonLoginUtils.getMemcacheUser(request);
		Long userId = 0L;
		if (user != null) {
			if (user.getId() != 0) {
				userId = user.getId();
			}
		}

		// ModelAndView mav = new ModelAndView(VIEW_SHOW_TRX_ORDER);
		try {
			userId = getLoginUserId(request);

			// 用户鉴权
			boolean verifyResult = trxorderGoodsService.verifyBelong(trxGoodsIdLong, userId,true);
			if (verifyResult) {

				refundService.processApplyForRefundToAct(
						Long.parseLong(trxGoodsId), user.getMobile() + "",
						RefundSourceType.USER, RefundHandleType.MANUAL,
						description);

				response.getWriter().write("SUCCESS");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// request.setAttribute("ERRMSG", "REFUND_EROR");
			// return mav;
		}

		// return mav;
	}

	@RequestMapping("/ucenter/commentGoods.do")
	public void commentGoods(
	// @RequestParam(value = "trxGoodsId", required = true)
	// String trxGoodsId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String trxGoodsId = request.getParameter("trxGoodsId");
		Long trxGoodsIdLong = new Long(trxGoodsId);
		// ModelAndView mav = new ModelAndView(VIEW_SHOW_TRX_ORDER);
		User user = SingletonLoginUtils.getMemcacheUser(request);
		Long userId = 0L;
		if (user != null) {
			if (user.getId() != 0) {
				userId = user.getId();
			}
		}
		try {
			userId = getLoginUserId(request);

			// 先查询这笔订单是否评价过。如果是则响应提示

			double commentPoint = new BigDecimal(
					request.getParameter("COMMNETPOINT")).doubleValue();

			String commentContent = request.getParameter("COMMNETCONTENT");

			// 用户鉴权
			boolean verifyResult = trxorderGoodsService.verifyBelong(trxGoodsIdLong, userId,true);
			if (verifyResult) {

				Map<String, String> rspMap = trxorderGoodsService.addComment(
						trxGoodsIdLong, userId, commentPoint, commentContent);
				if (rspMap != null) {

					response.getWriter().write(rspMap.get("result"));
					return;
				}
				/************************ 日志开始 ************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "Dianpin");
				logMap.put("prdid", trxGoodsId);
				LogAction.printLog(logMap);
				/************************ 日志结束 ************************************/

			}
		} catch (Exception e) {
			e.printStackTrace();
			// request.setAttribute("ERRMSG", "COMMENT_ERROR");
			// return mav;
			logger.error("+++++++++Trx-Exception:+++" + e);
			throw new Exception();
		}

		// return mav;
	}

	/**
	 * 打印凭证
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */

	@RequestMapping("/ucenter/printVoucher.do")
	public ModelAndView printVoucher(HttpServletRequest request)
			throws Exception {
		ModelAndView mav = new ModelAndView(VIEW_PRINT_VOUCHER);
		try {
			String trxGoodsId = request.getParameter("trxGoodsId");
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user == null) {
				return new ModelAndView("redirect:/forward.do?param=login");
			}
			/*
			 * TrxorderGoods trxorderGoods = trxorderGoodsDao.findById(Long
			 * .parseLong(trxGoodsId), user.getId());
			 */

			TrxorderGoods trxorderGoods = trxorderGoodsService.findById(
					Long.parseLong(trxGoodsId), user.getId());

			if (trxorderGoods == null || !trxorderGoods.isSupVouReSend()) {// 不存在或者不满足查看条件
				logger.info("fuck：userId=" + user.getId() + ",trxGoodsId="+ trxGoodsId);
				return new ModelAndView("redirect:../404.html");
			}
			/*
			 * String mertOrdTel = merchantService
			 * .getFixTelByGoodsId(trxorderGoods.getGoodsId()); Map<String,
			 * String> mertMap = merchantService
			 * .getMerchantByGoodsId(trxorderGoods.getGoodsId());
			 */

			Voucher voucher = voucherDao.findById(trxorderGoods.getVoucherId());

			/*
			 * if (mertMap != null) { merchantName =
			 * mertMap.get("merchantname"); merchantAddr = mertMap.get("addr");
			 * }
			 */
			PayInfoParam payInfoParam = new PayInfoParam();

			payInfoParam.setTxrorderGoodsSn(trxorderGoods.getTrxGoodsSn());
			payInfoParam.setVoucherCode(voucher.getVoucherCode());
			payInfoParam.setGoodsName(trxorderGoods.getGoodsName());
			// payInfoParam.setMerchantName(merchantName);
			List<PayInfoParam> pifList = null;
			//如果是点餐订单 add by ljp
			if(trxorderGoods.getBizType()==1){
				pifList = merchantService.getMerchantsByMerchantId(trxorderGoods.getSubGuestId());
			}else{
				pifList = merchantService.getMerchantsByGoodsId(trxorderGoods.getGoodsId());
			}
			

			request.setAttribute("pifList", pifList);
			request.setAttribute("payInfoParam", payInfoParam);
			request.setAttribute("ordLoseDate",
					trxorderGoods.getOrderLoseDate());
			request.setAttribute("isSupVouReSendInMerApi",
					trxorderGoods.isSupVouReSendInMerApi());// 是否是通过商家Api发送凭证码

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("+++++++++Trx-Exception:" + e);

		}
		return mav;
	}
	/**
     * 获得该订单是否使用优惠劵
     * @return
     */
	@RequestMapping("/ucenter/getCouponPayment.do")
    public void getCouponPayment(HttpServletRequest request,HttpServletResponse response) {
	    String trxordergoodsId = request.getParameter("trxGoodsId");
	    try {
    	    logger.info("+++++++ trxOrderAction getCouponPayment ++++:"+trxordergoodsId);
    	    Long trxGoodsId = new Long(trxordergoodsId);
    	    Map<String, Object> map = new HashMap<String, Object>(2);
    	    Long couponId= 0L;
    	    TrxorderGoods trxorderGoods = trxorderGoodsService.findById(trxGoodsId);
    	    if(trxorderGoods!=null){
    	      Payment couponPayment  =refundService.getCouponPaymentByTrxId(trxorderGoods.getTrxorderId());
    	      if(couponPayment!=null){
    	          couponId=couponPayment.getCouponId();
    	          double amount =refundService.getRefundAmountByTrxGoodsId(Long.valueOf(trxordergoodsId));
    	          map.put("amount", amount);//本次可退款的金额
    	          logger.info("+++++++ trxOrderAction getCouponPayment couponId ++++:"+ couponPayment.getCouponId()+",trxordergoodsId:"+trxordergoodsId);
    	      }
    	    }
    	    map.put("couponId", couponId);
            JSONObject jsonObject = JSONObject.fromObject(map);
            response.getWriter().write(jsonObject.toString());
	    } catch (IOException e) {
            e.printStackTrace();
            logger.info("+++++++ trxOrderAction getCouponPayment error:"+trxordergoodsId);
        }
    }
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/ucenter/getFoodOrderInfo.do")
	public void  getFoodOrderInfo(ModelMap model, HttpServletRequest request,
			HttpServletResponse response){
		JSONObject j = new JSONObject();
		try {
			String trxGoodsId = request.getParameter("trxGoodsId");
			Map<String,List<OrderMenu>> temp= dianCaiService.getPaidOrderMenuByTrxGoodsid(trxGoodsId);
			Map<String,Object> orderAmount = dianCaiDao.getOrderAmount(trxGoodsId);
			
			List list = new ArrayList();
			
			Set<Entry<String, List<OrderMenu>>> entries = temp.entrySet();
			for(Entry<String, List<OrderMenu>> entry:entries){
				Map m = new HashMap();
				m.put("listName", entry.getKey());
				List<OrderMenu> orderMenus = entry.getValue();
				List foods = new ArrayList();
				for(OrderMenu orderMenu:orderMenus){
					Map food = new HashMap();
					food.put("menuname", orderMenu.getMenuName());
					food.put("menuPrice"	, orderMenu.getMenuPrice());
					food.put("menucount", orderMenu.getCount());
					foods.add(food);
				}
				m.put("caidanList", foods);
				list.add(m);
			}
			
			
			j.put("caidanPrice", orderAmount.get("source_price"));
			j.put("dingdanPrice", orderAmount.get("pay_price"));
			j.put("menu", list);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(j.toString());
			logger.info("--------this is json my qianpin---"+j.toString());
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/ucenter/getFilmOrderInfo.do")
	public void  getFilmOrderInfo(ModelMap model, HttpServletRequest request,
			HttpServletResponse response){
		try {
			String trxGoodsId = request.getParameter("trxGoodsId");
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("trxGoodsId", trxGoodsId);
			List<Object> filmGoodsOrders = filmGoodsOrderDao.queryFilmGoodsOrderByCondition(condition);
			FilmGoodsOrder filmGoodsOrder = (FilmGoodsOrder)filmGoodsOrders.get(0);
			JSONObject json = new JSONObject();
			json.put("yingpian", filmGoodsOrder.getFilmName());
			json.put("yingyuan", filmGoodsOrder.getCinemaName());
			json.put("riqi", (filmGoodsOrder.getShowTime().getMonth()+1)+"月"+filmGoodsOrder.getShowTime().getDate()+"日");
			int time = filmGoodsOrder.getShowTime().getMinutes();
			json.put("changci", filmGoodsOrder.getShowTime().getHours()+":"+(time < 10 ? "0"+time : time ) +"  "+filmGoodsOrder.getLanguage()+" "+filmGoodsOrder.getDimensional()+"  "+filmGoodsOrder.getHallName());
			List<String> list = new ArrayList<String>();
			String[] temp = filmGoodsOrder.getSeatInfo().split("\\|");
			for(int i =0 ; i< temp.length;i++){
				list.add(temp[i].split(":")[0]+"排"+temp[i].split(":")[1]+"座");
			}
			json.put("zuowei", list);
			json.put("jiage", filmGoodsOrder.getFilmPrice());
			json.put("heji", Amount.mul(filmGoodsOrder.getFilmCount(), filmGoodsOrder.getFilmPrice().doubleValue()));
			request.setAttribute("filmGoodsOrder", filmGoodsOrder);
			response.setCharacterEncoding("UTF-8");
			
			response.getWriter().write(json.toString());
			logger.info("--------this is json my qianpin- wpw --"+json.toString());
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TrxorderGoodsDao getTrxorderGoodsDao() {
		return trxorderGoodsDao;
	}

	public void setTrxorderGoodsDao(TrxorderGoodsDao trxorderGoodsDao) {
		this.trxorderGoodsDao = trxorderGoodsDao;
	}
	
	

}
