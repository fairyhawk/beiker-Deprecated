package com.beike.action.pay;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.LogAction;
import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.biz.service.trx.OrderFilmService;
import com.beike.biz.service.trx.OrderFoodService;
import com.beike.common.bean.trx.PaymentInfoGenerator;
import com.beike.common.bean.trx.PaymentInfoGeneratorAlipay;
import com.beike.common.bean.trx.QuickPayUtils;
import com.beike.common.bean.trx.UpopPayConfig;
import com.beike.common.entity.coupon.TrxCoupon;
import com.beike.common.entity.trx.FilmGoodsOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.core.service.trx.coupon.TrxCouponService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.entity.goods.Goods;
import com.beike.entity.lottery.LotteryInfoNew;
import com.beike.entity.miaosha.MiaoSha;
import com.beike.entity.onlineorder.OrderMenu;
import com.beike.entity.user.User;
import com.beike.form.GoodsForm;
import com.beike.service.adweb.AdWebService;
import com.beike.service.adweb.AdWebTrxInfoService;
import com.beike.service.cps.tuan360.CPSTuan360Service;
import com.beike.service.cps.tuan360.impl.CPSTuan360Thread;
import com.beike.service.cps.tuan800.CPSTuan800Service;
import com.beike.service.cps.tuan800.impl.CPSTuan800Thread;
import com.beike.service.goods.GoodsService;
import com.beike.service.invite.LotteryInviteService;
import com.beike.service.miaosha.MiaoShaService;
import com.beike.service.user.UserService;
import com.beike.util.AdWebThread;
import com.beike.util.Amount;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.PinyinUtil;
import com.beike.util.StringUtils;
import com.beike.util.ThirdPartConstant;
import com.beike.util.TrxConstant;
import com.beike.util.TrxUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.hao3604j.Tuan360ApiThread;
import com.beike.util.img.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**
 * @Title: PayAction.java
 * @Package com.beike.action.pay
 * @Description:
 * @date Jun 1, 2011 7:33:26 PM
 * @author wh.cheng
 * @version v1.0
 */
@Controller
public class PayAction {
	private final Log logger = LogFactory.getLog(PayAction.class);
	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();
	/*private static PropertyUtil propertyUtil = PropertyUtil
			.getInstance(Constant.PROPERTY_FILE_NAME);*/

	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;

	@Autowired
	private UserService userService;
	@Autowired
	private OrderFilmService orderFilmService;

	@Autowired
	private GoodsService goodsService;
	@Autowired
	private CPSTuan360Service cpsTuan360Service;
	@Autowired
	private CPSTuan800Service cpsTuan800Service;
	@Autowired
	private TrxSoaService trxSoaService;
	@Autowired
	private LotteryInviteService lotteryInviteService;
	@Autowired
	private TrxCouponService trxCouponService;

	public static final String ADWEB_COOKIE_SRC = "ADWEB_COOKIE_SRC"; // src

	public static final String ADWEB_COOKIE_CID = "ADWEB_COOKIE_CID"; // CID

	public static final String ADWEB_COOKIE_WI = "ADWEB_COOKIE_WI"; // WI

	@Autowired
	private AdWebTrxInfoService adWebTrxInfoService;

	// 订单商品明细 add by qiaowb
	@Resource(name = "trxorderGoodsService")
	private TrxorderGoodsService trxorderGoodsService;

	@Autowired
	private AdWebService adWebService;
	@Autowired
	private MiaoShaService miaoShaService;
	@Autowired
	private OrderFoodService orderFoodService;

	@SuppressWarnings({ "unchecked", "deprecation" })
	@RequestMapping("/pay/shoppingCart.do")
	// @RequestMapping(value = "/requestParam", method = RequestMethod.GET)
	public String getOrderDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.info("+++++++++进入shoppingCart++++++++++++++");

		String requesturl = WebUtils.getRequestPath(request);
		Cookie requestUrlCookie = WebUtils.cookie("REQUESTURI_REFER_COOKIE",requesturl, -1);
		response.addCookie(requestUrlCookie);
		request.getSession().setAttribute("REQUESTURI_REFER_COOKIE", requesturl);
		String loginType = "";
		Map<String, String> logMap2 = null;// 日志所需
		// 判断验证码
		String clientIp = WebUtils.getIpAddr(request);
		Map<String, Integer> map = (Map<String, Integer>) memCacheService.get("LOGIN_IP");
		String isMobileAvalible="";// 手机号是否校验通过
		Integer times = 0;
		if (map != null) {
			times = map.get(clientIp);
			if (times != null && times >= 3) {
				request.setAttribute("USER_LOGIN_CODE_OPEN", "true");
			}
		}

		try {
			// 先判断有无登录
			String mobileTemp = null;
			User user = SingletonLoginUtils.getMemcacheUser(request);
			if (user != null) {
				logger.info("+++++++++user->Id:"+user.getId());
				Map<String, Object> userMap = trxSoaService.preQryInWtDBMobileUserById(user.getId());
				logger.info("+++++++++userMap:"+userMap);
				isMobileAvalible=userMap.get("ismobile").toString();
				mobileTemp = "1".equals(isMobileAvalible)?userMap.get("mobile").toString():"";
				loginType = TrxUtils.getTrxCookies(response, request,TrxConstant.TRX_LOGIN_TYPE + user.getId());

				request.setAttribute("loginStatus", "loginSuc");
				
				
				
				if ("1".equals(isMobileAvalible)) {
					request.setAttribute("mobileVerifyStatus","mobileVerifySuc");
					/************************* 日志开始 *************************************/
					Map<String, String> logMap = LogAction.getLogMap(request,
							response);
					logMap.put("action", "CreateOrder");
					LogAction.printLog(logMap);

					logMap2 = LogAction.getLogMap(request, response);
					String tCrtId = UUID.randomUUID().toString()
							+ "__"
							+ DateUtils
									.formatDate(new Date(), "yyyyMMddHHmmss");
					response.addCookie(WebUtils.cookie("bi_t_crt_id", tCrtId,
							-1));
					logMap2.put("action", "t_crt");
					logMap2.put("t_crt_id", tCrtId);// 只放不打，在后面同等条件下再打
					/************************* 日志结束 *************************************/

				}

			}
			String payStatus = request.getParameter("payStatus");
			logger.info("++++++++++user:" + user + "++++");

			String goodsDetailKey = request.getParameter("goodsDetailKey");
			String goodsDetailInfo = (String) memCacheService.get(goodsDetailKey);
			String[] goodsDetailInfos = goodsDetailInfo.split("\\|");

			int goodsCount = goodsDetailInfos.length;// 商品个数 （日志所需）
			int goodsOrderCount = 0;// 商品订单总数量（日志所需）
			List<Goods> goodsList = new ArrayList<Goods>();

			// 限购商品，增加验证码逻辑:验证码标志 初始化
			int validCodeFlag = 0;
			boolean miaoshaBoo = false;
			String isMenu = "0";
			for (int i = 0; i < goodsDetailInfos.length; i++) {
				Goods goods = new Goods();
				goods.setGoodsname(URLDecoder.decode(
						goodsDetailInfos[i].split("&")[0], "UTF-8"));
				goods.setGoodsId(Long.valueOf(goodsDetailInfos[i].split("&")[1]));
				goods.setSourcePrice(Double.valueOf(goodsDetailInfos[i]
						.split("&")[2]));
				goods.setPayPrice(Double.valueOf(goodsDetailInfos[i].split("&")[3]));
				goods.setRebatePrice(Double.valueOf(goodsDetailInfos[i]
						.split("&")[4]));
				goods.setDividePrice(Double.valueOf(goodsDetailInfos[i]
						.split("&")[5]));
				goods.setGuestId(Long.valueOf(goodsDetailInfos[i].split("&")[6]));
				goods.setOrderLoseAbsDate(Long.valueOf(goodsDetailInfos[i]
						.split("&")[7]));

				// goods.setOrderLoseDate(DateUtils.toDate(goodsDetailInfos[i].split("&")[8],
				// "yyyy-MM-dd HH:mm:ss"));
				goods.setMerchantname(URLDecoder.decode(
						goodsDetailInfos[i].split("&")[9], "UTF-8"));
				int goodsCountInt = Integer.parseInt(goodsDetailInfos[i]
						.split("&")[10]);

				// 限购商品，增加验证码逻辑
				if (goods.getPayPrice() < goods.getDividePrice()) {
					validCodeFlag = 1;
				}

				if (goodsCountInt < 1 || goodsCountInt > 999) {
					throw new RuntimeException("购买数量不正确");
				}
				goods.setGoodsCount(goodsDetailInfos[i].split("&")[10]);
				goods.setMerchantid(goodsDetailInfos[i].split("&")[11]);
				String miaoshaId = goodsDetailInfos[i].split("&")[16];
				goods.setMiaoshaid(miaoshaId);
				if (!"0".equals(miaoshaId)) {
					miaoshaBoo = true;// 是否展示
				}
				if(goodsDetailInfos[i].split("&").length>19){
					isMenu = goodsDetailInfos[i].split("&")[17];
					goods.setIsMenu(Integer.valueOf(isMenu));
				}
				goodsList.add(goods);
			}
			//新增点餐功能 add by ljp 20121116
			if("1".equals(isMenu)){
				List<Long> menuIds = new ArrayList<Long>();
				//点餐信息
				List<Map<String, Integer>> buyInfo = (List<Map<String, Integer>>)new JSONParser().parse(goodsDetailInfos[0].split("&")[18]);
				Map<String, Integer> buyInfoMap = new HashMap<String, Integer>();
				for(Map<String,Integer> idCount : buyInfo){
					Set<Entry<String, Integer>> entries = idCount.entrySet();
					for(Entry<String, Integer>e:entries ){
						menuIds.add(Long.parseLong(e.getKey()));
						buyInfoMap.put(e.getKey(), Integer.parseInt(e.getValue()+""));
					}
				}
				Map orderMenuInfo = orderFoodService.queryOrderMenuByIds(menuIds);
				List<String> category = (List<String>)orderMenuInfo.get("category");
				List<OrderMenu> temp = new ArrayList<OrderMenu>();
				List<OrderMenu> orderMenus = (List<OrderMenu>)orderMenuInfo.get("orderMenus");
				for(OrderMenu om : orderMenus){
					om.setCount(buyInfoMap.get(om.getMenuId()+""));
					temp.add(om);
				}
				request.setAttribute("orderMenus", temp);//菜品信息
				request.setAttribute("categorys", category);//菜品的种类
				request.setAttribute("isMenu", "1");//是点餐下单
				request.setAttribute("sourcePrice", goodsDetailInfos[0].split("&")[2]);//打折前的价格
				request.setAttribute("payPrice", goodsDetailInfos[0].split("&")[3]);//支付价格
				request.setAttribute("guestId", goodsDetailInfos[0].split("&")[19]);//分店
			}
			
			//新增在线先坐电影票功能 add by ljp 20121205
			if("2".equals(isMenu)){
				//获得uuid
				String uuid =goodsDetailInfos[0].split("&")[18];
				//从缓存中取得购买的电影票信息
				String filmInfo = (String)memCacheService.get("filmInfo"+uuid);
				String[] temps = filmInfo.split("&");
				String seatInfo= temps[0];//(String)request.getParameter("SeatInfo");//座位信息。格式：11:12。其中11 表示11 排，12 表示12 座， 多个座位用|分隔。如：	11:12|11:11|11:13
				//String sid =temps[1];//(String) request.getParameter("SID");//订单号
				String flagId = temps[2];//request.getParameter("FlagID");//锁座请求唯一标识(由渠道请求锁座时传入)
				//根据锁坐请求唯一标识  从缓存中取得放映流水号
				String showIndex =  (String)memCacheService.get(flagId);
				
				
				Map<String, Object> filmShowInfo = orderFilmService.queryFilmShowByShowIndex(Long.parseLong(showIndex));
				FilmGoodsOrder filmGoodsOrder = new FilmGoodsOrder();
				filmGoodsOrder.setFilmName((String)filmShowInfo.get("film_name"));//影片名称
				filmGoodsOrder.setDimensional((String)filmShowInfo.get("dimensional"));//场次版本。如：2D 3D 普通等
				Map<String, Object> cinemaInfo = orderFilmService.queryCinemaInfoByCinemaId((Long)filmShowInfo.get("cinema_id"));
				filmGoodsOrder.setCinemaName((String)cinemaInfo.get("name"));
				filmGoodsOrder.setSeatInfo(seatInfo);
				filmGoodsOrder.setShowTime((Timestamp)filmShowInfo.get("show_time"));
				filmGoodsOrder.setFilmPrice((BigDecimal)filmShowInfo.get("v_price"));
				filmGoodsOrder.setHallName((String)filmShowInfo.get("hall_name"));
				filmGoodsOrder.setLanguage((String)filmShowInfo.get("language"));
				request.setAttribute("filmInfo", filmGoodsOrder);
				request.setAttribute("showTime", (filmGoodsOrder.getShowTime().getMonth()+1)+"月"+filmGoodsOrder.getShowTime().getDate()+"日");
				int time = filmGoodsOrder.getShowTime().getMinutes();
				request.setAttribute("time", filmGoodsOrder.getShowTime().getHours()+":"+(time < 10 ? "0"+time : time ));
				request.setAttribute("isMenu", "2");//是网上先票电影票
			}
			
			
			double payPricesun = 0.00;// 总购买价格
			double rebatePricesun = 0.00;// 总返现价格
			List<PayInfoParam> listShopCart = new ArrayList<PayInfoParam>();// 页面展示商品信息数据
			List<PayInfoParam> piflist = null;
			String shopb = "";// 品牌转换标志位
			String ss = "";// 上次品牌id
			String yy = "";// 上次品牌名称
			for (int i = 0; i < goodsList.size(); i++) {
				Goods goods = goodsList.get(i);
				// 总购买价格
				payPricesun = payPricesun + Double.valueOf(goods.getPayPrice())
						* Double.valueOf(goods.getGoodsCount());
				// 总返现价格
				rebatePricesun = rebatePricesun
						+ Double.valueOf(goods.getRebatePrice())
						* Double.valueOf(goods.getGoodsCount());

				// 页面信息需求star
				PayInfoParam pif = new PayInfoParam();
				if (!shopb.equals(goods.getMerchantid()) && !"".equals(shopb)) {
					pif.setMerchantId(ss);
					pif.setMerchantName(yy);
					pif.setPif(piflist);
					listShopCart.add(pif);
					shopb = goods.getMerchantid();
					piflist = new ArrayList<PayInfoParam>();
				}
				if ("".equals(shopb)) {
					shopb = goods.getMerchantid();
					piflist = new ArrayList<PayInfoParam>();
				}
				ss = goods.getMerchantid();// 品牌id
				yy = goods.getMerchantname();// 品牌名称
				PayInfoParam pif1 = new PayInfoParam();
				pif1.setGoodsName(goods.getGoodsname());// 商品名称
				pif1.setGoodsCount(Integer.parseInt(goods.getGoodsCount()));// 商品数量
				goodsOrderCount += Integer.parseInt(goods.getGoodsCount());// 商品订单总数量（日志所需）
				pif1.setPayPrice(goods.getPayPrice() + "");// 商品购买价格
				pif1.setMiaoshaid(goods.getMiaoshaid());
				pif1.setTotalPriceItem(Amount.cutOff(
						Integer.parseInt(goods.getGoodsCount())
								* goods.getPayPrice(), 2)
						+ ""); // 单个商品购买总价
				piflist.add(pif1);

				// 日志埋点2.0。日志侵入性太大了,这个日志有些参数是多余的，上一步已经打过。用AWK抓即可拿到后三个参数.....@#￥#%，PM太特么懒了。。
				/************************ 日志开始 *************************************/
				if ("1".equals(isMobileAvalible)) {

					logMap2.put("goodid", String.valueOf(goods.getGoodsId()));
					logMap2.put("goodnum", goods.getGoodsCount());
					logMap2.put("inamt", String.valueOf(goods.getPayPrice()));
					LogAction.printLog(logMap2);
					/************************* 日志结束 *************************************/
				}
			}
			logger.info("++++++++++++" + payPricesun + "++++++++++"
					+ rebatePricesun);
			PayInfoParam pif2 = new PayInfoParam();
			pif2.setMerchantId(ss);
			pif2.setMerchantName(yy);
			pif2.setPif(piflist);
			listShopCart.add(pif2);
			// 页面信息需求end

			// 添加返现总金额，购买总金额
			PayInfoParam payInfoParam = new PayInfoParam("", Amount.cutOff(
					payPricesun, 2) + "", Amount.cutOff(rebatePricesun, 2) + "");

			payInfoParam.setGoodsDetailKey(goodsDetailKey);// 继续传递uuidKey

			/** ********************** 日志开始 *********************************** */
			Map<String, String> logMap = LogAction.getLogMap(request, response);
			logMap.put("action", "BuyOrder");
			logMap.put("prdnum", goodsCount + "");// 商品个数
			logMap.put("prdsum", goodsOrderCount + "");// 商品订单总数量
			logMap.put("prdamt", payPricesun + "");// 商品购买总价
			LogAction.printLog(logMap);

			/** ********************** 日志结束 *********************************** */
			// payInfoParam.setGoodsCount(goodsCount);
			String userTel = "";
			if (user != null) {
				// 用户ID
				String userId = String.valueOf(user.getId());
				// 用户email
				String userEmail = user.getEmail();
				// 用户tel
				if(mobileTemp != null && !"".equals(mobileTemp) && mobileTemp.length() == 11){
					userTel = mobileTemp.substring(0, 3)+"****"+mobileTemp.substring(7,11);
				}
				

				// 调用内部余额查询接口
				Map<String, String> hessianMap = new HashMap<String, String>();
				hessianMap.put("userId", userId);
				hessianMap.put("reqChannel", "WEB");
				hessianMap.put("trxAmount", Amount.cutOff(
						payPricesun, 2)+"");
				hessianMap.put("isSubAccountLose","0");
				Map<String, String> rspMap = trxHessianServiceGateWay.getActByUserId(hessianMap);

				if (rspMap == null) {
					logger.info("+++++++++++userId:" + userId
							+ "->rspMap is null++++++++++++++++++");
					request.setAttribute("ERRMSG", "账户余额获取失败！");
					// return "error";
					throw new Exception();
				}
				// 如果有通讯。获取余额异常
				if (!"1".equals(rspMap.get("rspCode"))) {
					request.setAttribute("ERRMSG", "账户余额获取异常！");
					// return "error";
					throw new Exception();
				}
				logger.info("+++++++++++userId:" + userId + "->balance:"
						+ rspMap.get("balance") + "++++++++++++++++++");

				String balanceAmount = rspMap.get("balance");
				//String changeVmAccount = rspMap.get("vmAmountListStr");//不找零信息
				payInfoParam.setUserTel(userTel);
				payInfoParam.setUserEmail(userEmail);
				payInfoParam.setBalanceAmount(balanceAmount);
				//payInfoParam.setChangeVmAccount(changeVmAccount);
			}
			payInfoParam.setGoodsCount(goodsList.size());
			request.setAttribute("payInfoParam", payInfoParam);
			request.setAttribute("payStatus", payStatus);
			request.setAttribute("loginType", loginType);
			request.setAttribute("listShopCart", listShopCart);
			request.setAttribute("miaoshaBoo", miaoshaBoo);
			List<List<String>> bankList = TrxConstant
					.getPayWayResultChannelList();// 加载可用银行接口list
			request.setAttribute("alipayBankList", bankList.get(0));
			request.setAttribute("upopBankList", bankList.get(2));
			request.setAttribute("payBankList", bankList.get(1));
			request.setAttribute("payBankSize", bankList.get(1).size());

			// 赋一个令牌
			String uuidTokenKey = StringUtils.createUUID();
			memCacheService.set(uuidTokenKey, StringUtils.createUUID(), 1800);// 配对Token
			// 往客户端发送Token
			request.setAttribute(Constant.UUID_TOKEN_KEY, uuidTokenKey);

			// 下单 验证码 逻辑开始
			memCacheService.set(goodsDetailKey + "IS_LIMIT_VALIDATE_CODE",
					validCodeFlag, 1800);
			logger.info("++shoppingCart++++++goodsDetailKey=" + goodsDetailKey
					+ "+++++validCodeFlag=" + validCodeFlag + "+++++++++");

			request.setAttribute("IS_LIMIT_VALIDATE_CODE", validCodeFlag);
			
			
			if(user!=null){
				// 账户余额
				String balanceAmount = payInfoParam.getBalanceAmount();
				// 计算支付金额-账户余额
				double bankAmount = Amount.sub(payPricesun, Double.valueOf(balanceAmount));
				// 计算页面上账户应支付的金额
				String balanceAmountForPay = bankAmount > 0 ? balanceAmount: Amount.cutOff(payPricesun,2)+"";
				logger.info("+++user id is =="+user.getId()+"====balanceAmountForPay ====="+balanceAmountForPay);
				request.setAttribute("balanceAmountForPay", balanceAmountForPay);

				logger.info("+++++++++user is " + user.getId()+ "++====user account balance is ==========="+ balanceAmount);

				// 增加账户安全逻辑 add by ljp 20120919
				boolean checkCodeFlag = isNeedSmsPwdForTrx(user.getId(),mobileTemp, Double.parseDouble(balanceAmountForPay));

				String checkCodeFlagStr = checkCodeFlag ? "1" : "0";
				
				logger.info("+++++++++user is " + user.getId()+ "++checkCodeFlagStr"+ checkCodeFlagStr);
				
				/*
				 * 增加优惠券展示 add by wangweijie 
				 */
				Set<String> goodsIdSet = new TreeSet<String>();
				Set<String> miaoshaIdSet = new TreeSet<String>();
				boolean isUseCoupon = true;
				for(Goods goods : goodsList){
					//普通商品
					if(!"0".equals(goods.getMiaoshaid())){
						miaoshaIdSet.add(goods.getMiaoshaid());
					}
					if(goods.getIsMenu()==1||goods.getIsMenu()==2){
						//优惠券是否受限
						isUseCoupon = false;
						break;
					}
					//秒杀
					else{
						goodsIdSet.add(String.valueOf(goods.getGoodsId()));
					}
				}
				Map<String, List<TrxCoupon>> trxCouponMap = trxCouponService.queryTrxCouponByUserIdForShow(user.getId(), goodsIdSet.toArray(new String[goodsIdSet.size()]), miaoshaIdSet.toArray(new String[miaoshaIdSet.size()]), Double.parseDouble(payInfoParam.getPayPrice()),isUseCoupon);
				//向页面传优惠券的信息
				if(null == trxCouponMap || trxCouponMap.size()==0){
					request.setAttribute("exist_trxcoupon", false);  //是否存在优惠券
				}else{
					request.setAttribute("exist_trxcoupon", true);
					request.setAttribute("notLimit_trxcoupon", trxCouponMap.get("NOT_LIMIT_COUPONLIST"));		//可用优惠券列表
					request.setAttribute("limit_trxcoupon", trxCouponMap.get("LIMIT_COUPONLIST"));	//不可用优惠券列表
				}
				// 页面上带***的手机号
				request.setAttribute("mobile", userTel);
				request.setAttribute("hiddenMobile", mobileTemp);
				request.setAttribute("checkCodeFlag", checkCodeFlagStr);// 是否展示动态支付码标识位
			}	
			
			return "/pay/shoppingCart";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("++++++++++++Trx-Exception:" + e.getStackTrace());

			throw new Exception();
		}

	}

	@RequestMapping("/pay/goshopping.do")
	public ModelAndView goShopping(HttpServletRequest request,HttpServletResponse response) throws Exception {

		String goodsidCount = request.getParameter("goodsidCount");
		logger.info("++++++++/pay/goshopping.do++++++++"+goodsidCount);
		String goodsid = "";
		String goodsArray[] = goodsidCount.split("\\|");
		for (int i = 0; i < goodsArray.length; i++) {
			String goodsIdItem = goodsArray[i].split("\\.")[0];
			String msIdItem = "";
			msIdItem = goodsArray[i].split("\\.")[2];
			if (msIdItem == null || "".equals(msIdItem)) {
				msIdItem = "0";
			}
			if (!StringUtils.isNumber(goodsIdItem)) {
				logger.debug("++++++++goodsIdItem  is not numberic!++++++");
				throw new Exception();
			}

			goodsid = goodsid + goodsArray[i].split("\\.")[0] + "-" + goodsArray[i].split("\\.")[2] + ",";
		}
		if (goodsid.contains(",")) {
			goodsid = goodsid.substring(0, goodsid.lastIndexOf(","));
		}

		List<Goods> goodsList = goodsService.findByIdList(goodsid);

		/** ********************** 日志开始 初始化 *********************************** */
		// 日志埋点2.0
		Map<String, String> logMap = LogAction.getLogMap(request, response);
		String tReqId = UUID.randomUUID().toString() + "__" + DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
		response.addCookie(WebUtils.cookie("bi_t_req_id", tReqId, -1));
		logMap.put("action", "t_req");
		logMap.put("t_req_id", tReqId);
		/** ********************** 日志结束 初始化 *********************************** */
		StringBuffer goodsDetailInfo = new StringBuffer();// 将一些商品信息放入memcache
		for (int i = 0; i < goodsList.size(); i++) {
			Goods goods = goodsList.get(i);
			Long miaoshaId = Long.valueOf(goods.getMiaoshaid());
			if (miaoshaId != 0) {
				MiaoSha miaosha = miaoShaService.getMiaoShaById(miaoshaId);
				goods.setGoodsname(miaosha.getMsTitle());
				goods.setPayPrice(miaosha.getMsPayPrice());
				goods.setDividePrice(miaosha.getMsSettlePrice());
			}
			try {
				goodsDetailInfo.append(URLEncoder.encode(goods.getGoodsname(),
						"utf-8"));// 商品名字--1
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGoodsId());// 商品ID---2
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getSourcePrice());// 商品原价格--3
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getPayPrice());// 商品购买价格--4
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getRebatePrice());// 商品返现价格--5
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getDividePrice());// 商品分成价格--6
			// TODO加入过期时间等
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGuestId());// guestID--7
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseAbsDate());// 订单过期时间段--8
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseDate());// 订单过期时间点--9
			goodsDetailInfo.append("&");
			// 加入品牌名称
			try {
				goodsDetailInfo.append(URLEncoder.encode(
						goods.getMerchantname(), "utf-8"));// 品牌名称--10
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			goodsDetailInfo.append("&");
			// 获取商品数量
			Long goodsCount = 0L;
			for (int j = 0; j < goodsArray.length; j++) {
				if (goods.getGoodsId().toString()
						.equals(goodsArray[j].split("\\.")[0])
						&& goods.getMiaoshaid().toString()
								.equals(goodsArray[j].split("\\.")[2])) {
					goodsCount = Long
							.parseLong((goodsArray[j].split("\\."))[1]);
					break;
				}
			}
			goodsDetailInfo.append(goodsCount + "");// 商品数量--11
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getMerchantid());// 品牌id--12
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getGoodsSingleCount());// 商品个人购买上限--13
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getIsRefund());// 是否自动退款--14
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getSendRules());// 是否发送商家校验码--15
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getIsadvance());// 是否预付款 0：否 1：是--16
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(miaoshaId);// 秒杀ID--17
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getIsMenu());// 是否为点餐商品--18
			goodsDetailInfo.append("&");
			goodsDetailInfo.append("json");//此处为点餐生成的json --19
			goodsDetailInfo.append("&");
			goodsDetailInfo.append("0");//分店id---20
			goodsDetailInfo.append("|");
			// 组装memcache信息结束
			/**
			 * ********************** 日志开始 数据填充
			 * ***********************************
			 */
			logMap.put("goodid", String.valueOf(goods.getGoodsId()));// 商品id
			logMap.put("goodnum", String.valueOf(goodsCount));// 商品数量
			logMap.put("inamt", String.valueOf(goods.getPayPrice()));// 商品购买单价
			LogAction.printLog(logMap);
			/**
			 * ********************** 日志结束 数据填充
			 * ***********************************
			 */
		}
		String goodsDetailInfoStr = goodsDetailInfo.toString();
		if (goodsDetailInfoStr.contains("|")) {
			goodsDetailInfoStr = goodsDetailInfoStr.substring(0,
					goodsDetailInfoStr.lastIndexOf("|"));
		}

		String goodsDetailKey = StringUtils.createUUID();

		memCacheService.set(goodsDetailKey, goodsDetailInfoStr, 18000);// 商品信息放入memcache
		return new ModelAndView("redirect:" + Constant.USER_TRX_LOGIN_REGISTER
				+ "?goodsDetailKey=" + goodsDetailKey);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@RequestMapping("/pay/goToPay.do")
	public Object goToPay(
			@RequestParam("goodsDetailKey") String goodsDetailKey,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		try {
			User user = SingletonLoginUtils.getMemcacheUser(request);
			
			if(user==null){
				
				return getOrderDetail(request, response);
			}
			String ipStr = StringUtils.getIpAddr(request);
			String userId = user.getId() + "";// 用户ID
			String phoneValidCode = request.getParameter("phoneValidCode");//短信动态支付密码
		
			/*
			 * 选择优惠券支付 add by wangweijie
			 * couponId =0 代表不使用优惠券支付
			 */
			String couponId = request.getParameter("couponId");
			try{
				Long.parseLong(couponId);
			}catch (Exception e) {
				logger.error("+++++++++++++++couponId="+couponId+"+++++is not valid++++");
				couponId = "0";
			}

			
			// 限购商品, 增加 验证码 校验 add by huojj
			/***********************************************/
			// 验证码标记
			Object validCodeFlag = memCacheService.get(goodsDetailKey+ "IS_LIMIT_VALIDATE_CODE");

			logger.info("+++++++++gotopay++++goodsDetailKey=" + goodsDetailKey
					+ "+++validCodeFlag=" + validCodeFlag);

			if (null == validCodeFlag) {// mem过期

				return getOrderDetail(request, response);
			} else {
				if (1 == Integer.parseInt(validCodeFlag.toString())) {
					String validCode = request
							.getParameter(Constant.USER_REGIST_CODE);
					String cookieCode = WebUtils.getCookieValue(
							"RANDOM_VALIDATE_CODE", request);
					String sessionCode = (String) memCacheService
							.get("validCode_" + cookieCode);
					memCacheService.remove("validCode_" + cookieCode);
					if (null == validCode || "".equals(validCode)) {

						request.setAttribute("msgValidCode", "1");
						return getOrderDetail(request, response);
					} else if (!validCode.equals(sessionCode)) {

						request.setAttribute("msgValidCode", "2");
						return getOrderDetail(request, response);
					}
				}
			}

			/***********************************************/

			String isRepeatSub = (String) request.getAttribute("REPEAT_SUBMIT");
			if ("true".equals(isRepeatSub)) {
				// request.setAttribute("ERRMSG", "重复提交！请重新下单！");
				// return "error";
				logger.info("+++++++++gotopay++++goodsDetailKey="+ goodsDetailKey + "+++REPEAT_SUBMIT");
				throw new Exception();
			}

			
			String qryResultKey = request.getParameter("qryResultKey");// 补单KEY
		
	
			// 增加用户手机号的校验
			Map<String, Object> userMap = trxSoaService.findMobileUserById(user.getId());
			if (userMap == null || "0".equals(userMap.get("ismobile"))|| StringUtils.isEmpty(userMap.get("mobile").toString())) {
				logger.debug("+++userId:"+user.getId()+"userMap:"+userMap);
				throw new Exception();
			}
			
			String mobile = userMap.get("mobile").toString();// 用户tel
			
			//增加账户安全(也就是手机验证码) add by ljp 20120919
			boolean isChkFlag= checkSmsPwdForTrx(Long.valueOf(userId),mobile, phoneValidCode);
			if(!isChkFlag){// 如果验证未通过。则重定向让用户重新验证动态密码
			
				return new ModelAndView("redirect:" + Constant.USER_TRX_LOGIN_REGISTER
						+ "?goodsDetailKey=" + goodsDetailKey);
			}

			Map<String, String> hessianMap = new HashMap<String, String>();
			// Map rspMap = null;
			PayInfoParam payInfoParam = new PayInfoParam();

			String goodsDetailInfo = (String) memCacheService.get(goodsDetailKey);
			logger.info("+++++++++++++++++++goodsDetailKey:" + goodsDetailKey
					+ "++++++++++++" + goodsDetailInfo + "++++++++++++");

			if (goodsDetailInfo == null || "".equals(goodsDetailInfo)) {
				logger.info("+++++++++++goodsDetailInfo is null++++++++++");
				request.setAttribute("ERRMSG", "订单已过期，请重新下单!");
				return "error";
			}

			String[] goodsDetailArray = StringUtils
					.parseParamArray(goodsDetailInfo);

			double rebateAmount = 0.00;

			String goodsNameResult = "";
			String goodsIdResult = "";
			String goodsSourcePriceResult = "";
			String payPriceResult = "";
			String rebatePriceResult = "";
			String dividePriceResult = "";
			// 新增
			String guestIdResult = "";
			String orderLoseAbsDateResult = "";
			String orderLoseDateResult = "";
			String shopids = "";
			String isRefund = "";
			String rules = "";
			String advance = "";// 是否预付款
			String miaoshaId = "";// 秒杀ID
			String bizType="";//是否为点菜商品
			String menuJson="";//如果是点菜商品则购买菜单信息
			String subGuestId ="" ;//分店id
			for (int i = 0; i < goodsDetailArray.length; i++) {

				String[] goodsDetail = goodsDetailArray[i].split("&");

				// 商品名字
				String goodsName = URLDecoder.decode(goodsDetail[0], "utf-8");
				// 商品ID
				String goodsId = goodsDetail[1];
				// 商品原价
				String goodsSourcePrice = goodsDetail[2];
				// 购买价格
				String payPrice = goodsDetail[3];
				// 返现价格
				String rebatePrice = goodsDetail[4];
				// 分成价格
				String dividePrice = goodsDetail[5];

				String refund = goodsDetail[13];

				String sendRules = goodsDetail[14];
				// 是否预付款
				String isadvance = goodsDetail[15];
				String msId = goodsDetail[16];
				String type = "0";
				String mj = "json";
				String sguestId = "0";
				//此处加if 是因为在线期间用户支付的容错
				if( goodsDetail.length>19 ){
					type = goodsDetail[17];
					mj = goodsDetail[18];
					sguestId = goodsDetail[19];
				}
				// 新增
				String guestId = goodsDetail[6];
				String orderLoseAbsDate = goodsDetail[7];
				String orderLoseDate = goodsDetail[8];
				String goodsCountInt = goodsDetail[10];
				int goodsCount = Integer.parseInt(goodsCountInt);

				// 组装购物车id

				shopids = shopids + goodsId + ".";
				// 用户ID
				// String userId = goodsDetail[9];
				// 用户email
				// String userEmail = goodsDetail[10];
				// 用户tel
				// String userTel = goodsDetail[11];

				// 拼装数据

				goodsNameResult = goodsNameResult
						+ StringUtils.convToHessian(goodsName, goodsCount);
				goodsIdResult = goodsIdResult
						+ StringUtils.convToHessian(goodsId, goodsCount);
				goodsSourcePriceResult = goodsSourcePriceResult
						+ StringUtils.convToHessian(goodsSourcePrice,
								goodsCount);
				payPriceResult = payPriceResult
						+ StringUtils.convToHessian(payPrice, goodsCount);
				rebatePriceResult = rebatePriceResult
						+ StringUtils.convToHessian(rebatePrice, goodsCount);
				dividePriceResult = dividePriceResult
						+ StringUtils.convToHessian(dividePrice, goodsCount);

				// 新增
				guestIdResult = guestIdResult
						+ StringUtils.convToHessian(guestId, goodsCount);

				orderLoseAbsDateResult = orderLoseAbsDateResult
						+ StringUtils.convToHessian(orderLoseAbsDate,
								goodsCount);

				orderLoseDateResult = orderLoseDateResult
						+ StringUtils.convToHessian(orderLoseDate, goodsCount);

				// 新增是否自动退款字段
				isRefund = isRefund
						+ StringUtils.convToHessian(refund, goodsCount);
				// 是否发送商家自有校验码
				rules = rules
						+ StringUtils.convToHessian(sendRules, goodsCount);
				// 是否预付款
				advance = advance
						+ StringUtils.convToHessian(isadvance, goodsCount);
				miaoshaId = miaoshaId
						+ StringUtils.convToHessian(msId, goodsCount);
				// 计算返现金额总额
				rebateAmount = rebateAmount
						+ new BigDecimal(rebatePrice).doubleValue()
						* goodsCount;
				bizType = bizType + StringUtils.convToHessian(type, goodsCount);
				menuJson= menuJson + StringUtils.convToHessian(mj, goodsCount);
				subGuestId = subGuestId+StringUtils.convToHessian(sguestId, goodsCount);
			}

			rebateAmount = Amount.cutOff(rebateAmount, 2);
			if (goodsNameResult.contains("|")) {
				goodsNameResult = goodsNameResult.substring(0,
						goodsNameResult.lastIndexOf("|"));
				goodsIdResult = goodsIdResult.substring(0,
						goodsIdResult.lastIndexOf("|"));
				goodsSourcePriceResult = goodsSourcePriceResult.substring(0,
						goodsSourcePriceResult.lastIndexOf("|"));
				payPriceResult = payPriceResult.substring(0,
						payPriceResult.lastIndexOf("|"));
				rebatePriceResult = rebatePriceResult.substring(0,
						rebatePriceResult.lastIndexOf("|"));
				dividePriceResult = dividePriceResult.substring(0,
						dividePriceResult.lastIndexOf("|"));
				// 新增
				guestIdResult = guestIdResult.substring(0,
						guestIdResult.lastIndexOf("|"));
				orderLoseAbsDateResult = orderLoseAbsDateResult.substring(0,
						orderLoseAbsDateResult.lastIndexOf("|"));
				orderLoseDateResult = orderLoseDateResult.substring(0,
						orderLoseDateResult.lastIndexOf("|"));
				// 新增是否自动退款字段
				isRefund = isRefund.substring(0, isRefund.lastIndexOf("|"));
				rules = rules.substring(0, rules.lastIndexOf("|"));// 是否发送商家自有校验码
				advance = advance.substring(0, advance.lastIndexOf("|"));// 是否预付款
				miaoshaId = miaoshaId.substring(0, miaoshaId.lastIndexOf("|"));// 秒杀ID
				bizType  = bizType.substring(0,bizType.lastIndexOf("|"));
				menuJson = menuJson.substring(0,menuJson.lastIndexOf("|"));
				subGuestId = subGuestId.substring(0,subGuestId.lastIndexOf("|"));
			}

			if (shopids.contains(".")) {
				shopids = shopids.substring(0, shopids.lastIndexOf("."));// 商品id,用.分割
				// 删除购物车用
			}
			payInfoParam.setRebateAmount(rebateAmount + "");
			payInfoParam.setUserTel(mobile);
			String typeChannel = request.getParameter("providerChannel");// 支付通道和银行接口
			String providerType = typeChannel.substring(0,typeChannel.indexOf("-"));// 支付通道
			String providerChannel = typeChannel.substring(typeChannel.indexOf("-") + 1);// 银行接口
			// 主装客户端Hessian数据
			hessianMap.put("mobile", mobile);
			hessianMap.put("userIp", ipStr);
			hessianMap.put("userId", userId);
			hessianMap.put("goodsName", goodsNameResult);
			hessianMap.put("goodsId", goodsIdResult);
			hessianMap.put("sourcePrice", goodsSourcePriceResult);
			hessianMap.put("payPrice", payPriceResult);
			hessianMap.put("rebatePrice", rebatePriceResult);
			hessianMap.put("dividePrice", dividePriceResult);
			hessianMap.put("providerType", providerType);
			hessianMap.put("providerChannel", providerChannel);

			// 新增
			hessianMap.put("guestId", guestIdResult);
			hessianMap.put("orderLoseAbsDate", orderLoseAbsDateResult);
			hessianMap.put("orderLoseDate", orderLoseDateResult);

			// 新增自动退款字段
			hessianMap.put("isRefund", isRefund);
			hessianMap.put("isSendMerVou", rules);// 是否发送商家自有校验码
			hessianMap.put("isadvance", advance);// 是否预付款
			hessianMap.put("miaoshaId", miaoshaId);// 秒杀ID
			
			//新增优惠券字段
			hessianMap.put("couponId", couponId);
			
			hessianMap.put("payMp", rebateAmount + "-" + mobile + "-"+ shopids + "-" + userId);// 切记不能用竖杠 shopids是商品id删除购物车信息用
			hessianMap.put("trxType", "NORMAL");// 常规交易
			hessianMap.put("reqChannel", "WEB");
			hessianMap.put("bizType", bizType);
			hessianMap.put("bizInfo", subGuestId+"|"+URLDecoder.decode(menuJson));
			//加网上选坐电影票功能 add by ljp
			if("2".equals(bizType)){
				//从缓存中取得购买的电影票信息
				String filmInfo = (String)memCacheService.get("filmInfo"+menuJson);
				String[] temps = filmInfo.split("&");
				String sid =temps[1];//(String) request.getParameter("SID");//订单号
				FilmGoodsOrder filmGoodsOrder = createFilmGoodsOrder(menuJson, userId, temps);
				//创建filmgoodsorder数据 并调用网票网下单接口
				Map map = orderFilmService.addFilmGoodsOrder(filmGoodsOrder, sid, user.getMobile());
				hessianMap.put("bizInfo",JsonUtil.getJsonStringFromMap(map));//id||sid||payNo
			}
			logger.info("+++++++userId:+" + userId + "++++++hessianMap:"+ hessianMap + "++++++++++");
			Map<String, String> rspMap = trxHessianServiceGateWay.createTrxOrder(hessianMap);

			if (rspMap == null || "".equals(rspMap)) {
				logger.info("+++++++++++userId:" + userId+ "->RSPCODE  is null!" + "++++++++++++++++++++++");
				// request.setAttribute("ERRMSG", "支付失败！");
				// return "error";
				throw new Exception();

			}

			String rspStatus = rspMap.get("status");
			String payLinkInfo = rspMap.get("payLinkInfo");
			String rspCode = rspMap.get("rspCode");
			String payLimitDes = rspMap.get("payLimitDes");
			String trxOrderId = rspMap.get("trxOrderId");// 交易订单ID
			String trxOrderReqId = rspMap.get("trxOrderReqId");// 交易订单号
			String payRequestId = rspMap.get("payRequestId");
			;// 网银支付请求号
			logger.info("++++++++++userId:" + userId + "++++rspStatus:"
					+ rspStatus + "->rspCode:" + rspCode + "+++++++++++");

			if ("2000".equals(rspCode)) {
				String[] payArray = payLimitDes.split("\\|\\|");
				String payLimitKey = payLimitKey(goodsDetailArray, payArray);
				if (!"".equals(payLimitKey)) {
					return new ModelAndView("redirect:"
							+ "/pay/buyError.do?payLimitKey=" + payLimitKey);
				}
			}

			if ("FAILED".equals(rspStatus)) {
				throw new Exception();
				// request.setAttribute("ERRMSG", "支付失败:" + rspCode);
				// return "error";
			}
			//点餐新增加 by ljp 201121
			if(	"1".equals(bizType)){
				logger.info("-----------back of createorder we will createMenuGoodsOrder -- in param---"+rspMap);
				orderFoodService.createMenuGoodsOrder(rspMap);
			}
			// 支付前保存第三方标志至memcached
			saveThirdPartFlagToMem(request, trxOrderId);

			// 支付成功前保存广告联盟订单add by qiaowb 2012-03-07
			saveOrderToAdWeb(request, userId, trxOrderId, 0);

			// cps tuan800下单 by janwen
			saveOrderToCPS(request, userId, trxOrderId, 0);
			
			if ("SUCCESS".equals(rspStatus) && "1".equals(rspCode)) {
				// 如果余额支付成功，直接到成功页面
				payInfoParam.setOrdRequestId(trxOrderReqId);// 交易请求订单号

				/************************* 日志开始 ***********************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "PayToAccount");
				logMap.put("trxid", trxOrderId);// 交易订单ID
				LogAction.printLog(logMap);
				// 日志埋点2.0-账户支付
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "t_crt_submit");
				logMap2.put("trxid", trxOrderId);// 交易订单ID
				LogAction.printLog(logMap2);

				// 账户支付成功
				Map<String, String> logMap3 = LogAction.getLogMap(request,
						response);
				logMap3.put("action", "t_order");
				logMap3.put("trxid", trxOrderId);// 交易订单ID
				LogAction.printLog(logMap3);
				/*********************** 日志结束 ************************************/

				// 第三方订单回写:余额支付
				saveOrderToThirdPart(request, trxOrderId);
				// request.setAttribute("payInfoParam", payInfoParam);
				// String payResutInfo=StringUtils.createUUID();
				// memCacheService.set(payResutInfo, payInfoParam);

				try {
					// 获得0元抽奖 2.0版本 奖品相关信息 wenjie.mai
					doLotteryInvite(userId, trxOrderId);
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("===lottery invite error ====userid:" + userId
							+ " trxId=" + trxOrderId);
				}
				try {
					updateAdwebStatus(trxOrderId);
				} catch (Exception e) {

				}
				// cps tuan800支付成功 by janwen
				saveOrderToCPS(request, userId, trxOrderId, 1);
				return new ModelAndView("redirect:"
						+ "/pay/paySuccess.do?rebateAmount="
						+ payInfoParam.getRebateAmount() + "&userTel="
						+ payInfoParam.getUserTel() + "&shopCartIdStr="
						+ shopids + "&userId=" + userId + "&trxOrderId="
						+ trxOrderId); // 新添加userid和商品id，用.分割，删除购物车用

			} else if ("NEEDPAY".equals(rspStatus) && "1".equals(rspCode)) {

				logger.info("+++++++++userId:" + userId + "++++++payLinkInfo:"
						+ payLinkInfo + "++++++++++");

				request.setAttribute("payLinkInfo", payLinkInfo);

				/************************ 日志开始 ********************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "PayToBank");
				logMap.put("trxid", trxOrderId);
				LogAction.printLog(logMap);

				// 日志埋点2.0
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "t_crt_submit");
				logMap2.put("trxid", trxOrderId);// 交易订单ID
				LogAction.printLog(logMap2);
				/************************* 日志结束 **********************************/
				// 拼装支付信息
				String payInfoString = payRequestId + "|"
						+ payInfoParam.getRebateAmount() + "|"
						+ payInfoParam.getUserTel() + "|" + goodsDetailKey
						+ "|" + shopids + "|" + userId + "|" + providerType
						+ "|" + DateUtils.getStringTodayto() + "|" + trxOrderId;
				// 往mem里放入支付相关信息
				memCacheService.set(qryResultKey, payInfoString, 360000);
				logger.info("++++++++qryResultKey=" + qryResultKey
						+ "++++++payInfoString=" + payInfoString);
				if ("UPOP".equals(providerType)) {
					return "/pay/upopPayGotoBank";
				} else {
					return "/pay/payGotoBank";
				}
			} else {
				// request.setAttribute("ERRMSG", "支付失败！");
				// return "error";
				logger.info("+++++++++++++remote call trxhessian end:"
						+ System.currentTimeMillis());
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);

			throw new Exception();

		}

	}


	/**
	 * 创建filmgoodsorder实体
	 * @param menuJson
	 * @param userId
	 * @param temps
	 * @return
	 * @throws Exception
	 */
	private FilmGoodsOrder createFilmGoodsOrder(String menuJson, String userId, String[] temps ) throws Exception{
		
		String seatInfo= temps[0];//(String)request.getParameter("SeatInfo");//座位信息。格式：11:12。其中11 表示11 排，12 表示12 座， 多个座位用|分隔。如：	11:12|11:11|11:13
		String sid =temps[1];//(String) request.getParameter("SID");//订单号
		String flagId = temps[2];//request.getParameter("FlagID");//锁座请求唯一标识(由渠道请求锁座时传入)
		//根据锁坐请求唯一标识  从缓存中取得放映流水号
		String showIndx =  (String)memCacheService.get(flagId);
		
		
		Map<String, Object> filmShowInfo = orderFilmService.queryFilmShowByShowIndex(Long.parseLong(showIndx));
		Map<String, Object> cinemaInfo = orderFilmService.queryCinemaInfoByCinemaId((Long)filmShowInfo.get("cinema_id"));
		FilmGoodsOrder filmGoodsOrder = new FilmGoodsOrder();
		filmGoodsOrder.setCinemaName((String)cinemaInfo.get("name"));
		filmGoodsOrder.setCreateDate(new Timestamp(new Date().getTime()));
		filmGoodsOrder.setDescription("");
		filmGoodsOrder.setDimensional((String)filmShowInfo.get("dimensional"));
		filmGoodsOrder.setFilmCount(new Long(seatInfo.split("\\|").length));
		filmGoodsOrder.setFilmName((String)filmShowInfo.get("film_name"));
		filmGoodsOrder.setFilmPayNo("");
		filmGoodsOrder.setFilmPrice((BigDecimal)filmShowInfo.get("v_price"));
		filmGoodsOrder.setFilmShowId((Long)filmShowInfo.get("id"));
		filmGoodsOrder.setFilmTrxSn(sid);
		filmGoodsOrder.setHallName((String)filmShowInfo.get("hall_name"));
		filmGoodsOrder.setLanguage((String)filmShowInfo.get("language"));
		filmGoodsOrder.setSeatInfo(seatInfo);
		filmGoodsOrder.setShowTime((Timestamp)filmShowInfo.get("show_time"));
		filmGoodsOrder.setTrxGoodsId(0L);
		filmGoodsOrder.setTrxOrderId(0L);
		filmGoodsOrder.setTrxStatus("INIT");
		filmGoodsOrder.setUpdateDate(new Timestamp(new Date().getTime()));
		filmGoodsOrder.setUserId(Long.parseLong(userId));
		filmGoodsOrder.setVersion(1L);
		return filmGoodsOrder;
	}

	@SuppressWarnings("unchecked")
	public void doLotteryInvite(String userId, String TrxId) {
		logger.info("do 24 hour add lottery userId:" + userId + " trxId:"
				+ TrxId);
		Long prizeid = null; // 奖品ID
		List prizeList = lotteryInviteService.getNewLottery(Long
				.parseLong(userId)); // 查询该用户的奖品信息
		logger.info("doLotteryInvite flag1: "
				+ (prizeList != null && prizeList.size() > 0));
		if (prizeList != null && prizeList.size() > 0) {
			Map xp = (Map) prizeList.get(0);
			prizeid = (Long) xp.get("newprize_id");
		}
		logger.info("doLotteryInvite flag2: " + prizeid);
		if (prizeid != null) {
			Long uid = Long.valueOf(userId);
			String nowTime = DateUtils.getNowTime();
			List los = lotteryInviteService.getLotteryForMySelf(uid, prizeid);
			logger.info("doLotteryInvite flag3: "
					+ (los != null && los.size() > 0));
			if (los != null && los.size() > 0) { // 自己参与过抽奖
				Map mp = (Map) los.get(0);
				Timestamp createTime = (Timestamp) mp.get("createtime");
				String lotteryTime = createTime.toString();
				double flag = DateUtils.disDateTime(nowTime, lotteryTime);
				logger.info("doLotteryInvite flag4: "
						+ (flag > 0 && flag <= 24));
				if (flag > 0 && flag <= 24) { // 24小时内购买商品
					List lo = lotteryInviteService.getLotteryInviteByUserId(
							Long.parseLong(userId), prizeid, "1");
					LotteryInfoNew li = lotteryInviteService
							.getPrizeInfo(String.valueOf(prizeid)); // 查找奖品信息
					logger.info("doLotteryInvite flag5: "
							+ (li != null && prizeid != null));
					if (li != null && prizeid != null) { // 抽奖活动未结束
						logger.info("doLotteryInvite flag6: "
								+ (lo != null && lo.size() < 5));
						if (lo != null && lo.size() < 5) { // 24小时内购买商品，最多获得5张奖券
							int num = lo.size();
							List trxList = trxorderGoodsService
									.findGonTrxGoodsSnByTrxId(Long
											.parseLong(TrxId)); // 获得订单好
							logger.info("doLotteryInvite flag6: "
									+ (trxList != null && trxList.size() > 0));
							if (trxList != null && trxList.size() > 0) {
								for (int i = 0; i < trxList.size(); i++) { // 一笔支付记录购买多个商品，存在多个订单号
									if (num < 5) {
										Map mpx = (Map) trxList.get(i);
										String goodsSn = (String) mpx
												.get("trx_goods_sn");
										logger.info("订单号....." + goodsSn);
										List gs = lotteryInviteService
												.getNewLotteryByGoodsSn(goodsSn);
										if (gs == null || gs.size() == 0) {
											lotteryInviteService
													.addNewLotteryInfo(
															String.valueOf(prizeid),
															"购买商品,订单号"
																	+ goodsSn,
															"1",
															userId,
															li.getStartprize_id()); // 增加优惠券
										}
										++num;
									} else {
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@RequestMapping("/pay/paySuccess.do")
	public String paySuccess(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String rebateAmount = request.getParameter("rebateAmount");
		String userTel = request.getParameter("userTel");
		String shopCartIdStr = request.getParameter("shopCartIdStr");
		String userId = request.getParameter("userId");
		String trxOrderId = request.getParameter("trxOrderId");

		PayInfoParam payInfoParam = new PayInfoParam();
		payInfoParam.setRebateAmount(rebateAmount);
		payInfoParam.setUserTel(userTel);

		request.setAttribute("payInfoParam", payInfoParam);
		String shopCartIdStrRst = StringUtils.convAryToStr(shopCartIdStr, ".",
				",", true);

		if (!StringUtils.isNumber(userId)) {
			logger.debug("+++++++++userId is  not numberic++++++++++++++");
			throw new Exception();

		}

		List<TrxorderGoods> listGoodsTitle = getBookingInfo(shopCartIdStrRst,
				trxOrderId, request);
		request.setAttribute("listGoodsTitle", listGoodsTitle);
		// String isRepeatSub=(String) request.getAttribute("REPEAT_SUBMIT");
		// if("true".equals(isRepeatSub)){
		// request.setAttribute("ERRMSG", "重复提交！请重新下单！");
		// return "error";
		// }
		// // 赋一个令牌
		// String uuidTokenKey = StringUtils.createUUID();
		// memCacheService.set(uuidTokenKey, StringUtils.createUUID(),1800);//
		// 配对Token
		// // 往客户端发送Token
		// request.setAttribute(Constant.UUID_TOKEN_KEY, uuidTokenKey);

		// 支付成功推荐商品4个
		getRecommondGoodsList(request, shopCartIdStrRst);

		return "/pay/paySuccess";
	}

	/**
	 * 预订商品显示商品名称
	 * 
	 * @param shopCartIdStrRst
	 * @param request
	 */
	public List<TrxorderGoods> getBookingInfo(String shopCartIdStr,
			String trxorderId, HttpServletRequest request) {
		List<Long> goodsIdList = new ArrayList<Long>();
		List<TrxorderGoods> rtnTgList = new ArrayList<TrxorderGoods>();
		String[] shopArrayStr = shopCartIdStr.split(",");

		int count = shopArrayStr.length;
		for (int i = 0; i < count; i++) {
			Long shopLong = Long.valueOf(shopArrayStr[i]);
			goodsIdList.add(shopLong);
		}
		Map<Long, String> map = trxSoaService
				.findGoodsTitleAndIsscheduled(goodsIdList);
		/*
		 * boolean booGoods = false; Set<Long> key = map.keySet(); for (Iterator
		 * it = key.iterator(); it.hasNext();) { String goods =
		 * map.get(it.next()); String[] strArray = goods.split("-"); if
		 * ("1".equals(strArray[1])) { booGoods = true; break; } } if
		 * (!booGoods) {// 如果商品中没有可以预定商品，不继续处理以下逻辑 return rtnTgList; }
		 */

		List<TrxorderGoods> tgList = trxorderGoodsService
				.preQryInWtDBFindByTrxId(Long.valueOf(trxorderId));
		boolean miaoshaBoo = false;
		for (TrxorderGoods tg : tgList) {
			boolean boo = true;
			if (!"0".equals(tg.getExtend_info()) && tg.getTrxRuleId() == 3) {
				miaoshaBoo = true;
			}
			Long goodsId = tg.getGoodsId();
			String mapStr = map.get(goodsId);
			String[] strArray = mapStr.split("-");
			if ("1".equals(strArray[1])) {
				Map<String, Object> bkMap = trxSoaService.findBytrxgoodsId(tg
						.getId());
				if (bkMap == null || bkMap.isEmpty()) {
					tg.setGoodsTitle(strArray[0]);
					for (TrxorderGoods togk : rtnTgList) {
						if (tg.getTrxorderId().equals(togk.getTrxorderId())
								&& tg.getGoodsId().equals(togk.getGoodsId())) {
							boo = false;
							break;
						}
					}
					if (boo) {
						rtnTgList.add(tg);
					}
				}
			}
		}
		request.setAttribute("miaoshaBoo", miaoshaBoo);
		return rtnTgList;

	}

	@SuppressWarnings("unchecked")
	public void getRecommondGoodsList(HttpServletRequest request,
			String shopCartIdStrRst) {
		try {
			if (!org.apache.commons.lang.StringUtils.isBlank(shopCartIdStrRst)) {
				List<Map<String, Object>> lstGoods = goodsService
						.getMostExpGoodsId(shopCartIdStrRst);
				if (lstGoods != null && lstGoods.size() > 0) {
					Map<String, Object> mapGoodsInfo = lstGoods.get(0);
					Long mostExpensiveGoodsId = (Long) mapGoodsInfo
							.get("goodsid");
					String city = PinyinUtil.hanziToPinyin(
							(String) mapGoodsInfo.get("city"), "");
					// 推荐商品
					List<Map<String, Object>> lstRegionIds = goodsService
							.getGoodsRegionIds(mostExpensiveGoodsId);
					// 用于处理二级地域重复
					int iNextRegionCount = 0;
					String tmpGoodsRegionId = "";
					if (lstRegionIds != null && lstRegionIds.size() > 0) {
						Map<String, Object> mapRegionIds = lstRegionIds.get(0);

						for (int i = 0; i < lstRegionIds.size(); i++) {
							Map<String, Object> catlog = lstRegionIds.get(i);
							// 只有一个区域
							if (lstRegionIds.size() == 1) {
								tmpGoodsRegionId = String.valueOf(catlog
										.get("regionextid"));
							} else {
								if (i == 0) {
									iNextRegionCount = 1;
									tmpGoodsRegionId = String.valueOf(catlog
											.get("regionextid"));
								} else {
									if (tmpGoodsRegionId
											.indexOf(String.valueOf(catlog
													.get("regionextid"))) < 0) {
										if (iNextRegionCount <= 1) {
											tmpGoodsRegionId = tmpGoodsRegionId
													+ ","
													+ catlog.get("regionextid");
										}
										iNextRegionCount++;
										if (iNextRegionCount > 2) {
											tmpGoodsRegionId = null;
											break;
										}
									}
								}
							}
						}

						// 获取当前城市ID
						Map<String, Long> mapCity = (Map<String, Long>) memCacheService
								.get("CITY_CATLOG");
						if (mapCity == null) {
							mapCity = BeanUtils.getCity(request,
									"regionCatlogDao");
							memCacheService.set("CITY_CATLOG", mapCity);
						}

						Long cityid = null;
						if (mapCity != null) {
							cityid = mapCity.get(city.trim());
						}
						// 与商品详情页取相同数量推荐商品，保证缓存共享
						List<Long> lstTuijian = goodsService
								.getSaleWithGoodsIds(
										(Long) mapRegionIds.get("regionid"),
										tmpGoodsRegionId,
										(Long) mapRegionIds.get("tagid"),
										cityid, 11L, "");

						// 剔除当前商品ID，查询推荐商品
						if (lstTuijian != null) {
							if (lstTuijian.contains(mostExpensiveGoodsId)) {
								lstTuijian.remove(mostExpensiveGoodsId);
							}
							if (lstTuijian.size() > 4) {
								lstTuijian = lstTuijian.subList(0, 4);
							}
							List<GoodsForm> lstTuijianGoodsForm = goodsService
									.getGoodsFormByChildId(lstTuijian);
							request.setAttribute("lstTuijianGoodsForm",
									lstTuijianGoodsForm);
						}
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping("/pay/buyError.do")
	public String buyError(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String payLimitKey = request.getParameter("payLimitKey");

		String payLimitInfo = (String) memCacheService.get(payLimitKey);

		if (payLimitInfo == null || "".equals(payLimitInfo)) {
			throw new Exception("payLimitInfo  is null");

		}
		List<PayInfoParam> listShopCart = new ArrayList<PayInfoParam>();// 个人超出上限页面展示商品信息数据
		String listShop = payLimitInfo.split(";")[0];
		String[] payLimitAry = listShop.split("\\|");
		int payLimitAryCount = payLimitAry.length;
		boolean buyBoo = false;
		String goodsId = "";
		for (int i = 0; i < payLimitAryCount; i++) {
			String[] itemPayLimitAry = payLimitAry[i].split("-");

			PayInfoParam pip = new PayInfoParam();
			pip.setGoodsId(itemPayLimitAry[0]);// 商品Id
			try {
				pip.setGoodsName(URLDecoder.decode(itemPayLimitAry[1], "utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// 商品名称
			pip.setGoodsCount(Integer.parseInt(itemPayLimitAry[2]));// 商品上限数量
			try {
				pip.setMerchantName(URLDecoder.decode(itemPayLimitAry[3],
						"utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// 品牌名称
			pip.setMerchantId(itemPayLimitAry[4]);// 品牌id
			pip.setIsavaliable(itemPayLimitAry[5]);// 是否上下架
			pip.setPaylimit(itemPayLimitAry[6]);// 支付限购类型
			listShopCart.add(pip);
			if (!"0".equals(itemPayLimitAry[7].trim())) {
				goodsId = itemPayLimitAry[0];
				buyBoo = true;
			}
		}

		request.setAttribute("listShopCart", listShopCart);
		// request.setAttribute("strTitle", strTitle);
		if (buyBoo) {
			request.setAttribute("goodsId", goodsId);
			return "/buy_miaoshaError";
		}
		return "/buy_error";
	}

	public String checkMemCache(String goodsDetailKey,
			HttpServletRequest request, User user) {

		if (goodsDetailKey == null || "".equals(goodsDetailKey)) {
			logger.info("+++++++++++goodsDetailKey is null+++商品信息丢失+++++++");
			request.setAttribute("ERRMSG", "订单已过期，请重新下单!");
			return "error";
		}
		String goodsDetailInfo = (String) memCacheService.get(goodsDetailKey);
		logger.info("+++++++++++++++++++goodsDetailKey:" + goodsDetailKey
				+ "++++++++++++" + goodsDetailInfo + "++++++++++++");

		if (goodsDetailInfo == null || "".equals(goodsDetailInfo)) {
			logger.info("+++++++++++goodsDetailInfo is null++++++++++");
			request.setAttribute("ERRMSG", "订单已过期，请重新下单!");
			return "error";
		}
		// 加入用户信息
		StringBuffer sb = new StringBuffer();
		sb.append(goodsDetailInfo);
		if (user != null) {
			sb.append("&");
			sb.append(user.getId());
			sb.append("&");
			sb.append(user.getEmail());
			sb.append("&");
			sb.append(user.getMobile());
		}
		return sb.toString();
	}

	@RequestMapping("/pay/payCallBackYeepay.do")
	public Object payCallBack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String r1_Code = request.getParameter("r1_Code");// 支付结果
		String r2_TrxId = request.getParameter("r2_TrxId");// 易宝支付交易流水号
		String r3_Amt = request.getParameter("r3_Amt");// 支付金额
		String r4_Cur = request.getParameter("r4_Cur");// 交易币种
		String r5_Pid = request.getParameter("r5_Pid");// 商品名称
		String r6_Order = request.getParameter("r6_Order");// 商户订单号
		String r7_Uid = request.getParameter("r7_Uid");// 易宝支付会员ID
		String r8_MP = request.getParameter("r8_MP");// 商户扩展信息
		String r9_BType = request.getParameter("r9_BType");// 交易结果返回类型
		String hmac = request.getParameter("hmac");// 签名数据

		logger.info("++++callbcakUrl:" + request.getRequestURL()
				+ request.getQueryString());
		// if("2".equals(r9_BType)){
		// //响应文本流，不管我们业务有没有顺利进行。代表：我们已收到支付机构的后台通讯回调
		//
		// }

		boolean result = PaymentInfoGenerator.verifyCallback(hmac, r1_Code,
				r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid, r8_MP,
				r9_BType);
		PayInfoParam payInfoParam = new PayInfoParam();
		Map<String, String> sourceMap = new HashMap<String, String>();

		sourceMap.put("payRequestId", r6_Order);
		sourceMap.put("proExternallId", r2_TrxId);
		sourceMap.put("sucTrxAmount", r3_Amt);
		sourceMap.put("reqChannel", "WEB");
		if (result) {// 验签成功，并处理平台内部业务逻辑

			Map<String, String> rspMap = trxHessianServiceGateWay
					.complateTrx(sourceMap);
			if (rspMap == null) {

				request.setAttribute("ERRMSG", "系统错误！");
				return "error";
			}

			String rspCode = rspMap.get("rspCode");
			String payLimitDes = rspMap.get("payLimitDes");

			logger.info("+++++payRequestId:" + r6_Order + "->rspCode:"
					+ rspCode + "+++++++++++");
			if ("1".equals(rspCode)) {// 支付成功。拆解原样返回信息
				String resultAry[] = r8_MP.split("-");
				logger.info("+++++++++++++++" + r8_MP);
				payInfoParam.setOrdRequestId(r6_Order);
				payInfoParam.setRebateAmount(resultAry[0] + "");
				payInfoParam.setUserTel(resultAry[1]);
				payInfoParam.setMertOrdTel(resultAry[2]);

				request.setAttribute("payInfoParam", payInfoParam);

				String shopCartIdStr = resultAry[2];// 商品ID
				String userId = resultAry[3];
				String trxorderId = resultAry[4];
				String shopCartIdStrRst = StringUtils.convAryToStr(
						shopCartIdStr, ".", ",", true);

				// paySuccessGoodsTitle(trxorderId,request);

				if (!StringUtils.isNumber(userId)) {
					logger.debug("+++++++++userId is  not numberic++++++++++++++");
					throw new Exception();

				}

				// 查找推荐商品
				getRecommondGoodsList(request, shopCartIdStrRst);

				/************************* 日志开始 ************************************ */
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "BuySuccedd");
				logMap.put("trxid", resultAry[4]);
				LogAction.printLog(logMap);

				// 日志埋点2.0。网银支付成功
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "t_order");
				logMap2.put("trxid", resultAry[4]);
				LogAction.printLog(logMap2);
				/************************* 日志结束 *************************************/
				try {
					// 获得0元抽奖 2.0版本 奖品相关信息 wenjie.mai
					doLotteryInvite(userId, resultAry[4]);
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("===lottery invite error ====userid:" + userId
							+ " trxId=" + resultAry[4]);
				}

				if ("2".equals(r9_BType)) { // 如果是后台通知，则会写文本流

					// response.getWriter().write("success");
					// 第三方订单回写:易宝支付
					saveOrderToThirdPart(request, resultAry[4]);

					// cps tuan800易宝支付成功 by janwen
					saveOrderToCPS(request, userId, resultAry[4], 1);
					// 保存广告联盟订单add by qiaowb 2012-03-02
					try {
						updateAdwebStatus(resultAry[4]);
					} catch (Exception e) {

					}
					return "/pay/payBackSuccess"; // 页面中响应文本流

				}
				// 由于超限部分成功跳转
				if (payLimitDes != null && !"".equals(payLimitDes)) {
					List<PayInfoParam> listShopCart = new ArrayList<PayInfoParam>();
					listShopCart = tolimit(payLimitDes, request);
					if (listShopCart == null) {
						return "/buy_miaoshaError";
					}
					request.setAttribute("listShopCart", listShopCart);
					return "/buy_error_limit";
				}

				return new ModelAndView("redirect:"
						+ "/pay/paySuccess.do?rebateAmount="
						+ payInfoParam.getRebateAmount() + "&userTel="
						+ payInfoParam.getUserTel() + "&shopCartIdStr="
						+ shopCartIdStrRst + "&userId=" + userId
						+ "&trxOrderId=" + trxorderId);
			} else {
				// request.setAttribute("ERRMSG", "系统错误：" + rspCode);
				// return "error";
				throw new Exception();
			}

		} else {
			logger.error("+++++++somebody modify callbak data  ++++++++hamc failed!++++");
			// /request.setAttribute("ERRMSG", "回调数据验签失败！");
			throw new Exception();
			// return "error";
		}

	}

	/**
	 * 支付超限跳转页面信息
	 * 
	 * @param payLimitDes
	 */
	public List<PayInfoParam> tolimit(String payLimitDes,
			HttpServletRequest request) {

		List<PayInfoParam> listShopCart = new ArrayList<PayInfoParam>();

		String payLimitArray[] = new String[1];
		if (payLimitDes.contains("||")) {
			payLimitArray = payLimitDes.split("\\|\\|");
		} else {
			payLimitArray[0] = payLimitDes;
		}
		boolean boo = false;
		for (int i = 0; i < payLimitArray.length; i++) {
			String payLimit[] = payLimitArray[i].split("\\|");
			String goodsId = payLimit[0];
			String goodsName = payLimit[1];
			String goodsTitle = payLimit[2];
			String limitCount = payLimit[3];
			String ismaxorLimit = payLimit[4];
			String mishaId = payLimit[5];
			if (!"0".equals(mishaId)) {
				request.setAttribute("goodsId", goodsId);
				boo = true;
			}
			List<Long> goodsList = new ArrayList<Long>();
			goodsList.add(Long.valueOf(goodsId));
			Map<String, Object> mapmerchant = trxSoaService
					.findMerchantName(goodsList);
			String merchantId = mapmerchant.get("merchantId").toString();
			String merchantName = (String) mapmerchant.get("merchantName");

			PayInfoParam pip = new PayInfoParam();
			pip.setGoodsId(goodsId);
			pip.setGoodsName(goodsName);
			pip.setMerchantName(goodsTitle);
			pip.setGoodsCount(Integer.valueOf(limitCount));
			pip.setMerchantId(merchantId);
			pip.setMerchantName(merchantName);
			if ("0".equals(ismaxorLimit.trim())) {
				pip.setPaylimit("MAXLIMIT");
			} else {
				pip.setPaylimit("PAYLIMIT");
			}
			listShopCart.add(pip);
		}
		if (boo) {
			return null;
		}
		return listShopCart;

	}

	@RequestMapping("/pay/payCallBackAlipay.do")
	@SuppressWarnings("unchecked")
	public Object aliCallback(HttpServletRequest request,
			HttpServletResponse response) throws Exception {// 获取支付宝GET过来反馈信息

		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();

		logger.info("++++alipayCallbcakUrl:" + request.getRequestURL() + "?"
				+ request.getQueryString());
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = values[0];

			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
			if ("notify_id".equals(name)) {
				valueStr = URLEncoder.encode(valueStr, "utf-8");
			}
			params.put(name, valueStr);
		}

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//

		String trade_no = request.getParameter("trade_no"); // 支付宝交易号
		String order_no = request.getParameter("out_trade_no"); // 获取订单号
		String total_fee = request.getParameter("total_fee"); // 获取总金额
		String body = request.getParameter("body");
		if (body != null) {
			body = new String(body.getBytes("ISO-8859-1"), "utf-8");// 商品描述、订单备注、描述
		}
		String trade_status = request.getParameter("trade_status"); // 交易状态

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

		// 计算得出通知验证结果
		boolean verify_result = PaymentInfoGeneratorAlipay.verify(params);

		PayInfoParam payInfoParam = new PayInfoParam();
		Map<String, String> sourceMap = new HashMap<String, String>();

		sourceMap.put("payRequestId", order_no);
		sourceMap.put("proExternallId", trade_no);
		sourceMap.put("sucTrxAmount", total_fee);
		sourceMap.put("reqChannel", "WEB");
		if (verify_result
				&& ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED"
						.equals(trade_status))) {// 验证成功
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 请在这里加上商户的业务逻辑程序代码

			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			Map<String, String> rspMap = trxHessianServiceGateWay
					.complateTrx(sourceMap);
			if (rspMap == null) {

				request.setAttribute("ERRMSG", "系统错误！");
				return "error";
			}

			String rspCode = rspMap.get("rspCode");
			String payLimitDes = rspMap.get("payLimitDes");

			logger.info("+++++payRequestId:" + order_no + "->rspCode:"
					+ rspCode + "+++++++++++");
			if ("1".equals(rspCode)) {// 支付成功。拆解原样返回信息
				String resultAry[] = body.split("-");
				logger.info("+++++++++++++++" + body);
				payInfoParam.setOrdRequestId(order_no);
				payInfoParam.setRebateAmount(resultAry[0] + "");
				payInfoParam.setUserTel(resultAry[1]);
				payInfoParam.setMertOrdTel(resultAry[2]);

				request.setAttribute("payInfoParam", payInfoParam);

				String shopCartIdStr = resultAry[2];// 商品ID
				String userId = resultAry[3];
				String trxorderId = resultAry[4];
				String shopCartIdStrRst = StringUtils.convAryToStr(
						shopCartIdStr, ".", ",", true);
				if (!StringUtils.isNumber(userId)) {
					logger.debug("+++++++++userId is  not numberic++++++++++++++");
					throw new Exception();

				}
				// paySuccessGoodsTitle(trxorderId,request);
				// 调用异部删除购物车

				/************************* 日志开始 *************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "BuySuccedd");
				logMap.put("trxid", resultAry[4]);
				LogAction.printLog(logMap);

				// 日志埋点2.0。网银支付成功
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "t_order");
				logMap2.put("trxid", resultAry[4]);
				LogAction.printLog(logMap2);
				/************************* 日志结束 *************************************/
				// 查找推荐商品
				getRecommondGoodsList(request, shopCartIdStrRst);
				try {
					// 获得0元抽奖 2.0版本 奖品相关信息 wenjie.mai
					doLotteryInvite(userId, resultAry[4]);
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("===lottery invite error ====userid:" + userId
							+ " trxId=" + resultAry[4]);
				}

				// 由于超限部分成功跳转
				if (payLimitDes != null && !"".equals(payLimitDes)) {
					List<PayInfoParam> listShopCart = new ArrayList<PayInfoParam>();
					listShopCart = tolimit(payLimitDes, request);
					if (listShopCart == null) {
						return "/buy_miaoshaError";
					}
					request.setAttribute("listShopCart", listShopCart);
					return "/buy_error_limit";
				}

				return new ModelAndView("redirect:"
						+ "/pay/paySuccess.do?rebateAmount="
						+ payInfoParam.getRebateAmount() + "&userTel="
						+ payInfoParam.getUserTel() + "&shopCartIdStr="
						+ shopCartIdStrRst + "&userId=" + userId
						+ "&trxOrderId=" + trxorderId); // 新添加userid和商品id，用.分割，删除购物车用
			} else {
				// 该页面可做页面美工编辑
				logger.error("+++++++++验证失败++++++++++++");
				throw new Exception();
			}
		} else {
			logger.error("+++++++somebody modify callbak data  ++++++++hamc failed!++++");
			throw new Exception();
			// return "error";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/pay/payCallBackAlipayBackNotify.do")
	// 支付宝异步回调地址
	public String aliCallbackNotifyBack(HttpServletRequest request,
			HttpServletResponse response) throws Exception {// 获取支付宝GET过来反馈信息

		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		logger.info("++++alipayBackNotifyCallbcakUrl:"
				+ request.getRequestURL() + "?" + request.getQueryString());

		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = values[0];

			params.put(name, valueStr);
		}

		logger.info("++++alipayBackNotifyCallbcakUrl:params++++++" + params);
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//

		String trade_no = request.getParameter("trade_no"); // 支付宝交易号
		String order_no = request.getParameter("out_trade_no"); // 获取订单号
		String total_fee = request.getParameter("total_fee"); // 获取总金额

		String body = request.getParameter("body");
		if (body != null) {
			body = new String(body.getBytes("ISO-8859-1"), "utf-8");// 商品描述、订单备注、描述
		}
		String trade_status = request.getParameter("trade_status"); // 交易状态

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

		// 计算得出通知验证结果
		boolean verify_result = PaymentInfoGeneratorAlipay.verify(params);

		PayInfoParam payInfoParam = new PayInfoParam();
		Map<String, String> sourceMap = new HashMap<String, String>();

		sourceMap.put("payRequestId", order_no);
		sourceMap.put("proExternallId", trade_no);
		sourceMap.put("sucTrxAmount", total_fee);
		sourceMap.put("reqChannel", "WEB");
		if ("TRADE_SUCCESS".equals(trade_status) && verify_result) {// 验证成功
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 请在这里加上商户的业务逻辑程序代码

			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			Map<String, String> rspMap = trxHessianServiceGateWay
					.complateTrx(sourceMap);
			if (rspMap == null) {

				request.setAttribute("ERRMSG", "系统错误！");
				return "error";
			}

			String rspCode = rspMap.get("rspCode");
			logger.info("+++++payRequestId:" + order_no + "->rspCode:"
					+ rspCode + "+++++++++++");
			if ("1".equals(rspCode)) {// 支付成功。拆解原样返回信息
				String resultAry[] = body.split("-");
				logger.info("+++++++++++++++" + body);
				payInfoParam.setOrdRequestId(order_no);
				payInfoParam.setRebateAmount(resultAry[0] + "");
				payInfoParam.setUserTel(resultAry[1]);
				payInfoParam.setMertOrdTel(resultAry[2]);

				request.setAttribute("payInfoParam", payInfoParam);

				String userId = resultAry[3];

				if (!StringUtils.isNumber(userId)) {
					logger.debug("+++++++++userId is  not numberic++++++++++++++");
					throw new Exception();

				}
				// 调用异部删除购物车

				/************************* 日志开始 *************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "BuySuccedd");
				logMap.put("trxid", resultAry[4]);
				LogAction.printLog(logMap);

				// 日志埋点2.0。网银支付成功
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "t_order");
				logMap2.put("trxid", resultAry[4]);
				LogAction.printLog(logMap2);
				/************************ 日志结束 ************************************ */

				// 第三方订单回写:支付宝支付异步回调
				saveOrderToThirdPart(request, resultAry[4]);
				// cps tuan800支付宝支付成功 by janwen
				saveOrderToCPS(request, userId, resultAry[4], 1);
				// 保存广告联盟订单add by qiaowb 2012-03-02
				try {
					updateAdwebStatus(resultAry[4]);
				} catch (Exception e) {

				}
				try {
					// 获得0元抽奖 2.0版本 奖品相关信息 wenjie.mai
					doLotteryInvite(userId, resultAry[4]);
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("===lottery invite error ====userid:" + userId
							+ " trxId=" + resultAry[4]);
				}
				return "/pay/payBackSuccess"; // 页面中响应文本流（异步回调）
			} else {
				// 该页面可做页面美工编辑
				logger.error("+++++++++验证失败++++++++++++");
				throw new Exception();
			}
		}
		//支付宝超过3个月，不可退款异步通知
		else if("TRADE_FINISHED".equals(trade_status) && verify_result){
			logger.info("++TRADE_FINISHED+++payRequestId:" + order_no + "++++++++++");
			return "/pay/payBackSuccess"; // 页面中响应文本流（异步回调）
		} else {
			logger.error("+++++++somebody modify callbak data  ++++++++hamc failed:"
					+ "++++");
			throw new Exception();
			// return "error";
		}
	}

	@RequestMapping("/pay/payCallBackUpop.do")
	public Object upopCallback(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			request.setCharacterEncoding(UpopPayConfig.charset);
		} catch (UnsupportedEncodingException e) {
		}

		String[] resArr = new String[UpopPayConfig.notifyVo.length];
		for (int i = 0; i < UpopPayConfig.notifyVo.length; i++) {
			resArr[i] = request.getParameter(UpopPayConfig.notifyVo[i]);
		}
		String signature = request.getParameter(UpopPayConfig.signature);
		String signMethod = request.getParameter(UpopPayConfig.signMethod);

		String trade_no = request.getParameter("qid"); // 银联交易号
		String order_no = request.getParameter("orderNumber"); // 获取订单号
		String total_fee = request.getParameter("orderAmount"); // 获取总金额

		Double orderAmount = Amount.div(Double.valueOf(total_fee), 100);// 转换为元。极其重要！！！
		String body = request.getParameter("extendInfo");// 千品网相关字段
		if (body != null) {
			body = new String(body.getBytes("ISO-8859-1"), "utf-8");// 商品描述、订单备注、描述
		}

		response.setContentType("text/html;charset=" + UpopPayConfig.charset);
		response.setCharacterEncoding(UpopPayConfig.charset);

		PayInfoParam payInfoParam = new PayInfoParam();
		Map<String, String> sourceMap = new HashMap<String, String>();

		sourceMap.put("payRequestId", order_no);
		sourceMap.put("proExternallId", trade_no);
		sourceMap.put("sucTrxAmount", orderAmount.toString());
		sourceMap.put("reqChannel", "WEB");

		Boolean signatureCheck = new QuickPayUtils().checkSign(resArr,
				signMethod, signature);
		if (signatureCheck && "00".equals(resArr[10])) {
			// 验证成功
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 请在这里加上商户的业务逻辑程序代码

			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			Map<String, String> rspMap = trxHessianServiceGateWay
					.complateTrx(sourceMap);
			if (rspMap == null) {

				request.setAttribute("ERRMSG", "系统错误！");
				return "error";
			}

			String rspCode = rspMap.get("rspCode");
			String payLimitDes = rspMap.get("payLimitDes");

			if (rspMap == null) {

				request.setAttribute("ERRMSG", "系统错误！");
				return "error";
			}

			logger.info("+++++payRequestId:" + order_no + "->rspCode:"
					+ rspCode + "+++++++++++");
			if ("1".equals(rspCode)) {// 支付成功。拆解原样返回信息
				String resultAry[] = body.split("-");
				logger.info("+++++++++++++++" + body);
				payInfoParam.setOrdRequestId(order_no);
				payInfoParam.setRebateAmount(resultAry[0] + "");
				payInfoParam.setUserTel(resultAry[1]);
				payInfoParam.setMertOrdTel(resultAry[2]);

				request.setAttribute("payInfoParam", payInfoParam);

				String shopCartIdStr = resultAry[2];// 商品ID
				String userId = resultAry[3];
				String trxorderId = resultAry[4];
				String shopCartIdStrRst = StringUtils.convAryToStr(
						shopCartIdStr, ".", ",", true);
				// paySuccessGoodsTitle(trxorderId,request);
				if (!StringUtils.isNumber(userId)) {
					logger.debug("+++++++++userId is  not numberic++++++++++++++");
					throw new Exception();

				}
				// 调用异部删除购物车

				/************************* 日志开始 *************************************/
				Map<String, String> logMap = LogAction.getLogMap(request,
						response);
				logMap.put("action", "BuySuccedd");
				logMap.put("trxid", resultAry[4]);
				LogAction.printLog(logMap);

				// 日志埋点2.0。网银支付成功
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "t_order");
				logMap2.put("trxid", resultAry[4]);
				LogAction.printLog(logMap2);
				/************************* 日志结束************************************ */
				// shopCartService.removeShopCartBySuc(shopCartIdStrRst + "|"+
				// userId); //支持手机接口后，删除购物车放入hessian处理
				// 查找推荐商品
				getRecommondGoodsList(request, shopCartIdStrRst);
				try {
					// 获得0元抽奖 2.0版本 奖品相关信息 wenjie.mai
					doLotteryInvite(userId, resultAry[4]);
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("===lottery invite error ====userid:" + userId
							+ " trxId=" + resultAry[4]);
				}

				// 由于超限部分成功跳转
				if (payLimitDes != null && !"".equals(payLimitDes)) {
					List<PayInfoParam> listShopCart = new ArrayList<PayInfoParam>();
					listShopCart = tolimit(payLimitDes, request);
					if (listShopCart == null) {
						return "/buy_miaoshaError";
					}
					request.setAttribute("listShopCart", listShopCart);
					return "/buy_error_limit";
				}

				return new ModelAndView("redirect:"
						+ "/pay/paySuccess.do?rebateAmount="
						+ payInfoParam.getRebateAmount() + "&userTel="
						+ payInfoParam.getUserTel() + "&shopCartIdStr="
						+ shopCartIdStrRst + "&userId=" + userId
						+ "&trxOrderId=" + trxorderId);
			} else {
				// 该页面可做页面美工编辑
				logger.error("+++++++++验证失败++++++++++++");
				throw new Exception();
			}
		} else {

			logger.error("+++++++somebody modify callbak data  ++++++++hamc failed:"
					+ "++++");
			throw new Exception();
			// return "error";

		}
	}

	@RequestMapping("/pay/payCallBackUpopBackNotify.do")
	public String upopCallbackService(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			request.setCharacterEncoding(UpopPayConfig.charset);
		} catch (UnsupportedEncodingException e) {
		}

		String[] resArr = new String[UpopPayConfig.notifyVo.length];
		for (int i = 0; i < UpopPayConfig.notifyVo.length; i++) {
			resArr[i] = request.getParameter(UpopPayConfig.notifyVo[i]);
		}
		String signature = request.getParameter(UpopPayConfig.signature);
		String signMethod = request.getParameter(UpopPayConfig.signMethod);

		String trade_no = request.getParameter("qid"); // 银联交易号
		String order_no = request.getParameter("orderNumber"); // 获取订单号
		String total_fee = request.getParameter("orderAmount"); // 获取总金额
		Double orderAmount = Amount.div(Double.valueOf(total_fee), 100);// 转换为元。极其重要！！！
		String body = request.getParameter("extendInfo");// 千品网相关字段
		if (body != null) {
			body = new String(body.getBytes("ISO-8859-1"), "utf-8");// 商品描述、订单备注、描述
		}

		response.setContentType("text/html;charset=" + UpopPayConfig.charset);
		response.setCharacterEncoding(UpopPayConfig.charset);

		PayInfoParam payInfoParam = new PayInfoParam();
		Map<String, String> sourceMap = new HashMap<String, String>();

		sourceMap.put("payRequestId", order_no);
		sourceMap.put("proExternallId", trade_no);
		sourceMap.put("sucTrxAmount", orderAmount.toString());
		sourceMap.put("reqChannel", "WEB");

		logger.info("++++UPOPBackNotifyCallbcakUrl:sourceMap++++++++"
				+ sourceMap);

		Boolean signatureCheck = new QuickPayUtils().checkSign(resArr,
				signMethod, signature);
		if (signatureCheck && "00".equals(resArr[10])) {
			// 验证成功
			// ////////////////////////////////////////////////////////////////////////////////////////
			// 请在这里加上商户的业务逻辑程序代码

			// ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			Map<String, String> rspMap = trxHessianServiceGateWay
					.complateTrx(sourceMap);
			if (rspMap == null) {

				request.setAttribute("ERRMSG", "系统错误！");
				return "error";
			}

			String rspCode = rspMap.get("rspCode");

			logger.info("+++++payRequestId:" + order_no + "->rspCode:"
					+ rspCode + "+++++++++++");
			if ("1".equals(rspCode)) {// 支付成功。拆解原样返回信息
				String resultAry[] = body.split("-");
				logger.info("+++++++++++++++" + body);
				payInfoParam.setOrdRequestId(order_no);
				payInfoParam.setRebateAmount(resultAry[0] + "");
				payInfoParam.setUserTel(resultAry[1]);
				payInfoParam.setMertOrdTel(resultAry[2]);

				request.setAttribute("payInfoParam", payInfoParam);

				String userId = resultAry[3];
				if (!StringUtils.isNumber(userId)) {
					logger.debug("+++++++++userId is  not numberic++++++++++++++");
					throw new Exception();

				}
				// 日志埋点2.0。网银支付成功
				Map<String, String> logMap2 = LogAction.getLogMap(request,
						response);
				logMap2.put("action", "t_order");
				logMap2.put("trxid", resultAry[4]);
				LogAction.printLog(logMap2);
				/************************ 日志结束 *********************************** */
				// shopCartService.removeShopCartBySuc(shopCartIdStrRst + "|"
				// + userId);
				// 第三方订单回写:银联支付异步回调
				saveOrderToThirdPart(request, resultAry[4]);
				// cps tuan800支付宝支付成功 by janwen
					saveOrderToCPS(request, userId, resultAry[4], 1);
				// 保存广告联盟订单add by qiaowb 2012-03-02
				adWebTrxInfoService.updateAdWebTrxStatus(resultAry[4]);
				try {
					// 获得0元抽奖 2.0版本 奖品相关信息 wenjie.mai
					doLotteryInvite(userId, resultAry[4]);
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("===lottery invite error ====userid:" + userId
							+ " trxId=" + resultAry[4]);
				}
				return "/pay/payUpopBackSuccess"; // 页面中响应文本流（异步回调）
			} else {
				// 该页面可做页面美工编辑
				logger.error("+++++++++验证失败++++++++++++");
				throw new Exception();
			}
		} else {
			logger.error("+++++++somebody modify callbak data  ++++++++hamc failed:"
					+ "++++");
			throw new Exception();
			// return "error";
		}
	}

	/**
	 * 自动补单
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/pay/qryPayResult.do")
	public ModelAndView qryPayResult(HttpServletRequest request)
			throws Exception {
		Map<String, String> sourceMap = new HashMap<String, String>();
		try {

			String qryResultKey = request.getParameter("qryResultKey");
			logger.info("+++++++++++++++++qryResultKey=" + qryResultKey);
			String payInfo = (String) memCacheService.get(qryResultKey);
			logger.info("+++++++++++++++++payInfo=" + payInfo);
			String payRuestId = payInfo.split("\\|")[0];
			String rebateAmount = payInfo.split("\\|")[1];
			String userTel = payInfo.split("\\|")[2];
			String goodsDetailKey = payInfo.split("\\|")[3];
			String shopids = payInfo.split("\\|")[4];// 商品id,.分割
			String userId = payInfo.split("\\|")[5];
			String providerType = payInfo.split("\\|")[6];
			String cDateForUpopQry = payInfo.split("\\|")[7];// UPOP 自动查单及补单专用
			String trxorderId = payInfo.split("\\|")[8];
			sourceMap.put("payRequestId", payRuestId);
			sourceMap.put("providerType", providerType);
			sourceMap.put("createDate", cDateForUpopQry);
			sourceMap.put("reqChannel", "WEB");
			Map<String, String> rspMap = trxHessianServiceGateWay
					.complatePayStauts(sourceMap);

			// 由于超限部分成功跳转
			String payLimitPostPayCacheKey = TrxConstant.PAY_LIMIT_DES_POST_PAY_CACHE_KEY
					+ payRuestId;
			String payLimitDes = (String) memCacheService
					.get(payLimitPostPayCacheKey);
			payLimitDes = payLimitDes == null ? "" : payLimitDes;
			if (payLimitDes != null && !"".equals(payLimitDes)) {
				List<PayInfoParam> listShopCart = new ArrayList<PayInfoParam>();
				listShopCart = tolimit(payLimitDes, request);
				if (listShopCart == null) {
					return new ModelAndView("/buy_miaoshaError");
				}
				request.setAttribute("listShopCart", listShopCart);
				return new ModelAndView("/buy_error_limit");
			}

			String rspCode = rspMap.get("rspCode");
			logger.info("+++++++++auto Qry Result payRuestId:" + "->RSPCODE:"
					+ rspCode + "++++++++++++");
			if ("1".equals(rspCode)) {
				return new ModelAndView("redirect:"
						+ "/pay/paySuccess.do?rebateAmount=" + rebateAmount
						+ "&userTel=" + userTel + "&shopCartIdStr=" + shopids
						+ "&userId=" + userId + "&trxOrderId=" + trxorderId);
			} else {

				return new ModelAndView("redirect:"
						+ "/pay/shoppingCart.do?goodsDetailKey="
						+ goodsDetailKey + "&payStatus=INIT");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("+++++++++Trx-Exception:" + e);

		}
		return null;
	}

	public String checkMemCacheUser(String goodsDetailInfoStr,
			HttpServletRequest request, User user) {

		if (goodsDetailInfoStr == null || "".equals(goodsDetailInfoStr)) {
			logger.info("+++++++++++goodsDetailKey is null+++商品信息丢失+++++++");
			request.setAttribute("ERRMSG", "订单已过期，请重新下单!");
			return "error";
		}
		// 加入用户信息
		StringBuffer sb = new StringBuffer();
		sb.append(goodsDetailInfoStr);
		if (user != null) {
			sb.append("|");
			sb.append(user.getId());
			sb.append("|");
			sb.append(user.getEmail());
			sb.append("|");
			sb.append(user.getMobile());
		}
		return sb.toString();

	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public GoodsService getGoodsService() {
		return goodsService;
	}

	public void setGoodsService(GoodsService goodsService) {
		this.goodsService = goodsService;
	}

	/**
	 * 
	 * @param request
	 * @param userId
	 * @param trxid
	 * @param status(0:下单,1:支付成功)
	 */
	private void saveOrderToCPS(HttpServletRequest request, String userId,
			String trxid, int status) {
		logger.info("===saveOrderToCPSTuan800===begin===" + trxid + "===status==="
				+ status);
		// cps cookie
		String csid = WebUtils.getCookieValue("bi_csid", request);
		//var cook = _src + '|' + _cid + '|' + _outsrc + "|" + _wi +'|' + _uid;
		String cps_cookie_tuan800 = WebUtils.getCookieValue("cps_tuan800", request);
		String cps_cookie_tuan360 = WebUtils.getCookieValue(ThirdPartConstant.CPS_COOKIE_PARAM, request);
		
		logger.info("===saveOrderToCPS===cps_cookie_tuan800==="
				+ org.apache.commons.lang.StringUtils.trimToEmpty(cps_cookie_tuan800)
				+ "===csid==="
				+ org.apache.commons.lang.StringUtils.trimToEmpty(csid));
		logger.info("===cps_cookie_tuan360==="
				+ org.apache.commons.lang.StringUtils.trimToEmpty(cps_cookie_tuan360));
				
		try {
			Map<String,Object> params = new HashMap<String, Object>();
			// status为0时，下单
			if (status == 0) {
				//cpstuan800 cookie不为空且csid不为空并且bi_csid以CPS_开头,并且包含tuan800_qianpin
				
				if(org.apache.commons.lang.StringUtils.isNotEmpty(cps_cookie_tuan800) 
						&& org.apache.commons.lang.StringUtils.isNotEmpty(csid)){
					//设置cookie缓存给tuan800cpc用,cpc只在支付成功后回调
					memCacheService.set("cps_tuan800_trx_order_id"+trxid,cps_cookie_tuan800);
					memCacheService.set("cps_tuan800_csid"+trxid,csid);
				}
				
				
				if(org.apache.commons.lang.StringUtils.isNotEmpty(cps_cookie_tuan800) 
						&& org.apache.commons.lang.StringUtils.isNotEmpty(csid) 
						&& csid.startsWith("CPS") && csid.contains("tuan800_qianpin")){
					params.put("cps_cookie", cps_cookie_tuan800);
					params.put("trxorder_id", trxid);
					
					CPSTuan800Thread cpsThread = new CPSTuan800Thread(cpsTuan800Service, params, status);
					cpsThread.start();
				}else if(org.apache.commons.lang.StringUtils.isNotEmpty(cps_cookie_tuan360) 
						&& org.apache.commons.lang.StringUtils.isNotEmpty(csid) 
						&& (csid.contains("free_360_") || csid.contains("fee_360_"))){
					String tuan360UserId = WebUtils.getCookieValue("TUAN360USERID",request);
					
					memCacheService.set("cps_tuan360_trx_order_id"+trxid, cps_cookie_tuan360);
					memCacheService.set("cps_tuan360_csid"+trxid, csid);
					
					params.put("cps_cookie", cps_cookie_tuan360);
					params.put("trxorder_id", trxid);
					params.put("qid", tuan360UserId);
					
					CPSTuan360Thread cps360Thread = new CPSTuan360Thread(cpsTuan360Service, params, status);
					cps360Thread.start();
				}
			}else if(status == 1){
				String cps_cookie_cache = (String)memCacheService.get("cps_tuan800_trx_order_id"+trxid);
				String csid_cache  = (String)memCacheService.get("cps_tuan800_csid"+trxid);
				
				String cps_cookie_cache_tuan360 = (String)memCacheService.get("cps_tuan360_trx_order_id"+trxid);
				String csid_cache_tuan360  = (String)memCacheService.get("cps_tuan360_csid"+trxid);
				
				memCacheService.remove("cps_tuan800_trx_order_id"+trxid);
				memCacheService.remove("cps_tuan800_csid"+trxid);
				
				memCacheService.remove("cps_tuan360_trx_order_id"+trxid);
				memCacheService.remove("cps_tuan360_csid"+trxid);
				if(org.apache.commons.lang.StringUtils.isNotEmpty(cps_cookie_cache) && org.apache.commons.lang.StringUtils.isNotEmpty(csid_cache) 
						&& csid_cache.startsWith("CPS") 
						&& csid_cache.contains("tuan800_qianpin")){
					params.put("trxorder_id", trxid);
					params.put("csid", csid_cache);
					params.put("cps_cookie", cps_cookie_cache);
					CPSTuan800Thread cpsThread = new CPSTuan800Thread(cpsTuan800Service, params, status);
					cpsThread.start();
				}else if(org.apache.commons.lang.StringUtils.isNotEmpty(cps_cookie_cache_tuan360) 
						&& org.apache.commons.lang.StringUtils.isNotEmpty(csid_cache_tuan360) 
						&& (csid_cache_tuan360.contains("free_360_") || csid_cache_tuan360.contains("fee_360_"))){
					params.put("cps_cookie", cps_cookie_cache_tuan360);
					params.put("trxorder_id", trxid);
					
					CPSTuan360Thread cps360Thread = new CPSTuan360Thread(cpsTuan360Service, params, status);
					cps360Thread.start();
				}else if(org.apache.commons.lang.StringUtils.isNotEmpty(cps_cookie_cache) && org.apache.commons.lang.StringUtils.isNotEmpty(csid_cache) 
						&& (csid_cache.startsWith("api_free") || csid_cache.startsWith("api_fee")) 
						&& csid_cache.contains("_800_")){
					//tuan800新添加cpc
					////var cook = _src + '|' + _cid + '|' + _outsrc + "|" + _wi +'|' + _uid;
					//规则:bi_csid以api_free||api_fee开头并且 包含_800_
					String[] cps_value = cps_cookie_cache.split("\\|");
					if(cps_value != null && cps_value.length>1 && "0".equals(cps_value[1])){
						params.put("trxorder_id", trxid);
						params.put("csid", csid_cache);
						params.put("cps_cookie", cps_cookie_cache);
						CPSTuan800Thread cpsThread = new CPSTuan800Thread(cpsTuan800Service, params, status);
						cpsThread.start();	
					}
				}else if(org.apache.commons.lang.StringUtils.isNotEmpty(cps_cookie_cache) && org.apache.commons.lang.StringUtils.isNotEmpty(csid_cache) 
						&& "tuan800_ad_shoupingtl".equals(csid_cache)){
					//tuan800新添加cpc
					////var cook = _src + '|' + _cid + '|' + _outsrc + "|" + _wi +'|' + _uid;
					//规则:bi_csid未tuan800_ad_shoupingtl
					String[] cps_value = cps_cookie_cache.split("\\|");
					if(cps_value != null && cps_value.length>1 && "0".equals(cps_value[1])){
						params.put("trxorder_id", trxid);
						params.put("csid", csid_cache);
						params.put("cps_cookie", cps_cookie_cache);
						CPSTuan800Thread cpsThread = new CPSTuan800Thread(cpsTuan800Service, params, status);
						cpsThread.start();	
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("saveOrderToCPS error");
		}
		logger.info("===saveOrderToCPS===end===" + trxid + "===status==="+ status);
	}

	/**
	 * 广告联盟订单写入
	 * 
	 * @param request
	 * @param userId
	 * @param trxid
	 * @param status
	 */
	private void saveOrderToAdWeb(HttpServletRequest request, String userId,
			String trxid, int status) {
		logger.info("===saveOrderToAdWeb===begin===" + trxid + "===status==="
				+ status);
		// 广告联盟cookie
		String adweb_src = WebUtils.getCookieValue(ADWEB_COOKIE_SRC, request);
		String adweb_cid = WebUtils.getCookieValue(ADWEB_COOKIE_CID, request);
		String adweb_wi = WebUtils.getCookieValue(ADWEB_COOKIE_WI, request);

		String csid = WebUtils.getCookieValue("bi_csid", request);

		logger.info("===saveOrderToAdWeb===src==="
				+ org.apache.commons.lang.StringUtils.trimToEmpty(adweb_src)
				+ "===cid==="
				+ org.apache.commons.lang.StringUtils.trimToEmpty(adweb_cid)
				+ "===wi==="
				+ org.apache.commons.lang.StringUtils.trimToEmpty(adweb_wi)
				+ "===csid==="
				+ org.apache.commons.lang.StringUtils.trimToEmpty(csid));
		try {
			// status为0时，支付前记录交易订单条件：广告联盟cookie不为空且csid为空或者csid以api开头且包含src
			if ((status == 0
					&& org.apache.commons.lang.StringUtils
							.isNotEmpty(adweb_src)
					&& org.apache.commons.lang.StringUtils
							.isNotEmpty(adweb_cid)
					&& org.apache.commons.lang.StringUtils.isNotEmpty(adweb_wi) && (org.apache.commons.lang.StringUtils
					.isEmpty(csid) || (csid.startsWith("CPS") && csid
					.contains(adweb_src))))
					|| status == 1) {
				AdWebThread awt = new AdWebThread(adWebTrxInfoService,
						trxorderGoodsService, adWebService, trxid,
						Long.parseLong(userId), adweb_src, adweb_cid, adweb_wi,
						status);
				awt.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("save adweb error ");
		}
		logger.info("===saveOrderToAdWeb===end===" + trxid + "===status==="
				+ status);
	}

	private void updateAdwebStatus(String trxorderId) {
		List<TrxorderGoods> lstGoodsCount = trxorderGoodsService
				.preQryInWtDBFindByTrxId(Long.parseLong(trxorderId));
		// 支付成功后保存广告联盟订单add by qiaowb 2012-03-02
		if (lstGoodsCount != null && lstGoodsCount.size() > 0) {
			for (TrxorderGoods trxorderGoods : lstGoodsCount) {
				adWebTrxInfoService.updateAdWebTrxStatus(trxorderGoods
						.getTrxGoodsSn());
			}
		}
	}

	/**
	 * 支付前限购解析
	 * 
	 * @param goodsDetailArray
	 * @param strPays
	 * @param strTitle
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String payLimitKey(String[] goodsDetailArray, String[] strPays)
			throws UnsupportedEncodingException {
		StringBuilder payLimitSb = new StringBuilder();// 需要往memcache放的sb

		for (int i = 0; i < goodsDetailArray.length; i++) {
			String[] goodsDetail = goodsDetailArray[i].split("&");
			for (int j = 0; j < strPays.length; j++) {

				if (goodsDetail[1].equals(strPays[j].split("\\|")[0])) {

					payLimitSb.append(goodsDetail[1]);
					payLimitSb.append("-");
					payLimitSb.append(URLDecoder.decode(
							strPays[j].split("\\|")[1], "utf-8"));// 商品名称
					payLimitSb.append("-");
					payLimitSb.append(strPays[j].split("\\|")[4]);// 商品上限数量
					payLimitSb.append("-");
					payLimitSb.append(URLDecoder
							.decode(goodsDetail[9], "utf-8"));// 品牌名称
					payLimitSb.append("-");
					payLimitSb.append(goodsDetail[11]);// 品牌id
					payLimitSb.append("-");
					String isavaliable = strPays[j].split("\\|")[5];
					payLimitSb.append(isavaliable);// 是否上下架
					payLimitSb.append("-");
					payLimitSb.append(strPays[j].split("\\|")[6]);
					payLimitSb.append("-");
					payLimitSb.append(strPays[j].split("\\|")[7]);
					payLimitSb.append("|");

				}
			}
		}
		String payLimitKey = "";
		if (payLimitSb != null && payLimitSb.length() > 0) {
			payLimitKey = StringUtils.createUUID();
			// request.setAttribute("listShopCart", listShopCart);
			payLimitSb.deleteCharAt(payLimitSb.length() - 1); // 删掉最后一个
			// payLimitSb.append(";");
			// payLimitSb.append(strTitle);// 超限标题（个人超限或总量超限）
			memCacheService.set(payLimitKey, payLimitSb.toString());
		}
		return payLimitKey;
	}

	/**
	 * 支付前保存第三方标志至memcached
	 * 
	 * @param request
	 * @param trxOrderId
	 */
	private void saveThirdPartFlagToMem(HttpServletRequest request,
			String trxOrderId) {
		try {
			String csid = WebUtils.getCookieValue("bi_csid", request);
			if (org.apache.commons.lang.StringUtils.isNotEmpty(csid)) {
				/*if (csid.startsWith("api") && csid.contains("baidu")) {
					// 百度团购
					// 百度一站通标志写入缓存
					String baiduUserId = WebUtils.getCookieValue("BAIDUUSERID",
							request);
					String baiduParam = WebUtils.getCookieValue(
							"BAIDU_REFERER_PARAM", request);
					logger.info("====set baiducookie memCacheService start====");
					if (baiduUserId != null && !baiduUserId.trim().equals("")
							&& csid != null && !csid.trim().equals("")
							&& csid.startsWith("api") && csid.contains("baidu")) {
						memCacheService.set(trxOrderId + "BAIDUUSERID",
								baiduUserId);
					}
					if (baiduParam != null && !baiduParam.trim().equals("")
							&& csid != null && !csid.trim().equals("")
							&& csid.startsWith("api") && csid.contains("baidu")) {
						memCacheService.set(trxOrderId + "BAIDU_REFERER_PARAM",
								baiduParam);
						memCacheService.set(trxOrderId + "BAIDU_BI_CSID", csid);
					}
					logger.info("====set baiducookie memCacheService end====");
				} else*/ 
				if (csid.startsWith("api_fee_360") || csid.startsWith("api_free_360")) {
					// 360团购
					logger.info("====set tuan360cookie memCacheService start====");
					String tuan360UserId = WebUtils.getCookieValue(
							"TUAN360USERID", request);
					if (org.apache.commons.lang.StringUtils
							.isNotEmpty(tuan360UserId)) {
						memCacheService.set(trxOrderId + "TUAN360USERID",
								tuan360UserId);
						logger.info("====set tuan360cookie memCacheService end====");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 支付成功后订单回传至第三方
	 * 
	 * @param request
	 * @param trxid
	 *            交易订单ID
	 * @author qiaowb
	 */
	private void saveOrderToThirdPart(HttpServletRequest request, String trxid) {
		try {
			logger.info("trxid===" + trxid);
			// 取缓存数据

			// 百度用户ID、百度参数
			//String baiduUserId = (String) memCacheService.get(trxid
			//		+ "BAIDUUSERID");
			//String baiduParam = (String) memCacheService.get(trxid
			//		+ "BAIDU_REFERER_PARAM");
			//String baiduCsid = (String) memCacheService.get(trxid
			//		+ "BAIDU_BI_CSID");

			// 团360用户ID
			String tuan360UserId = (String) memCacheService.get(trxid
					+ "TUAN360USERID");

			// 清除缓存
			logger.info("====remove memCacheService start====");
			//memCacheService.remove(trxid + "BAIDUUSERID");
			//memCacheService.remove(trxid + "BAIDU_REFERER_PARAM");
			memCacheService.remove(trxid + "TUAN360USERID");
			logger.info("====remove memCacheService end====");

			//if (baiduUserId != null) {
			//	logger.info("BAIDUUSERID===" + baiduUserId);
			//}
			//if (baiduParam != null) {
			//	logger.info("baiduParam===" + baiduParam);
			//}
			if (tuan360UserId != null) {
				logger.info("tuan360UserId===" + tuan360UserId);
			}

			/*if (org.apache.commons.lang.StringUtils.isNotEmpty(baiduParam)) {
				// 百度团购
				String tn = "";
				String baiduid = "";
				String chkGoodsId = "";
				if (baiduParam != null && !"".equals(baiduParam)) {
					try{
						baiduParam = URLDecoder.decode(baiduParam,"UTF-8");
						logger.info("baiduParam decode===" + baiduParam);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					String[] aryParam = baiduParam.split("\\|");
					if (aryParam != null && aryParam.length >= 3) {
						tn = aryParam[0];
						baiduid = aryParam[1];
						chkGoodsId = aryParam[2];
					}
				}
				logger.info("=====start saveOrderToBaidu thread=====");
				BaiduApiThread baiduThread = new BaiduApiThread(trxid,
						baiduUserId, chkGoodsId, tn, baiduid,
						propertyUtil.getProperty("BAIDU_API_BONUS"),
						request.getContextPath(), trxorderGoodsService,
						goodsService, baiduCsid);
				baiduThread.start();
			} else*/ 
			if (org.apache.commons.lang.StringUtils.isNotEmpty(tuan360UserId)) {
				// 360团购
				logger.info("=====start saveOrderToTuan360 thread=====");
				Tuan360ApiThread tuan360Thread = new Tuan360ApiThread(trxid,
						tuan360UserId, request.getContextPath(),
						trxorderGoodsService, goodsService);
				tuan360Thread.start();
			} else {
				logger.info("=====do not need saveOrderToThirdPart=====");
				return;
			}
		} catch (Exception ex) {
			logger.info("saveOrderToThirdPart Exception");
			logger.info("====remove memCacheService start====");
			//memCacheService.remove(trxid + "BAIDUUSERID");
			//memCacheService.remove(trxid + "BAIDU_REFERER_PARAM");
			memCacheService.remove(trxid + "TUAN360USERID");
			//memCacheService.remove(trxid + "BAIDU_BI_CSID");
			logger.info("====remove memCacheService end====");
			ex.printStackTrace();
		}
	}

	/**
	 * 手机动态短信支付密码码验证结果
	 * @param trxSmsPwdKey
	 * @param reqSmsPwd  页面传入的smsPwd
	 * @return
	 */
	public  boolean checkSmsPwdForTrx(Long userId,String mobile,String reqSmsPwd){
		
		boolean isChkFlag=false;
		String trxSmsPwdKey = TrxConstant.TRX_RANDOMNUMBER_NEW + userId+ mobile ;
		try {
			if (TrxConstant.CHECK_CODE_FLAG) {// 手机短信动态支付密码全局开关。默认为true
				String trxSmsPwdInfo = (String) memCacheService.get(trxSmsPwdKey);// 不管金额以及余额规则是否满足，此key均在mem中存在
				String isNeedCheck = trxSmsPwdInfo.split(":")[0];//是否需要校验（是否触发阀值）
				String trxSmsPwd = trxSmsPwdInfo.split(":")[1];//mem里校验码
				String trxSmsPwdChkFlag = trxSmsPwdInfo.split(":")[2];//校验结果
				
				logger.debug("+++checkSmsPwdForTrx+++trxSmsPwdKey:"+trxSmsPwdKey+"++++trxSmsPwdInfo:"+trxSmsPwdInfo);
				if("0".equals(isNeedCheck)){// 如果不需要校验, 放行
					
					isChkFlag=true;
				}else  if ("1".equals(trxSmsPwdChkFlag)){
					
					isChkFlag=true;
				}else if(trxSmsPwd.equalsIgnoreCase(reqSmsPwd)) {//在此处校验。若后期扩展到字母的动态码,则大小写不敏感
					
					isChkFlag=true;
					memCacheService.set(trxSmsPwdKey,"1:"+trxSmsPwd+":1",TrxConstant.CHECK_PHONE_CODE_TIMEOUT);//此处更新mem
				}else{
					isChkFlag=true;//需要校验
					
				}
				
			}else{
				
				isChkFlag=true;
			}

		}catch(Exception e){//memKey过期、不存在、数组越界、密码不匹配等。在此catch后统一处理
			e.printStackTrace();
			logger.debug("++++trxSmsPwdKey:"+trxSmsPwdKey+"++++e:"+e);
			return true;
		}
		
		return isChkFlag;
		
	}
		
	/**
	 * 是否需要在交易过程共验证短信支付动态密码
	 * @param userId
	 * @param mobile
	 * @param reqIp
	 * @param balanceAmountForPay
	 * @return
	 */
	public boolean isNeedSmsPwdForTrx(Long userId,String mobile,double balanceAmountForPay){
		
		boolean result=true;
		String trxSmsPwdKey = TrxConstant.TRX_RANDOMNUMBER_NEW + userId+ mobile ;
		try {
			if (TrxConstant.CHECK_CODE_FLAG) {

				String trxSmsPwdInfo = (String) memCacheService.get(trxSmsPwdKey);
				String isNeedChkFlag = "";// 是否需要校验
				logger.info("++isNeedSmsPwdForTrx++trxSmsPwdKey:"+trxSmsPwdKey+"++++trxSmsPwdInfo:"+trxSmsPwdInfo);
				if (StringUtils.isEmpty(trxSmsPwdInfo)) {// 如果此前mem中没有数据，则加入

					if (balanceAmountForPay < TrxConstant.SMS_PWD_AMOUNT) {// 若没有触发应余额支付的阀值

						isNeedChkFlag = "0";// 不需要校验
						result = false;
					} else {
						isNeedChkFlag = "1";// 需要校验
					}
				    //mem 中trxSmsPwdKey 值：是否需要验证：验证短信码：验证结果
					memCacheService.set(trxSmsPwdKey, isNeedChkFlag+":"  + ""+ ":0", TrxConstant.CHECK_PHONE_CODE_TIMEOUT);
					logger.info("++isNeedSmsPwdForTrx++trxSmsPwdKey:"+trxSmsPwdKey+"++++isNeedChkFlag:"+isNeedChkFlag);

				} else {// 若之前有数据

					String isChkFlag = trxSmsPwdInfo.split(":")[2];// 之前是否校验通过.

					if("1".equals(isChkFlag)){ //若校验通过，放心
						
						result = false;
					}else if(balanceAmountForPay < TrxConstant.SMS_PWD_AMOUNT){//若本次账户应支付金额没有触发阀值
						isNeedChkFlag = "0";// 不需要校验
						memCacheService.set(trxSmsPwdKey, isNeedChkFlag+":"  + ""+ ":0", TrxConstant.CHECK_PHONE_CODE_TIMEOUT);
						logger.info("++isNeedSmsPwdForTrx++trxSmsPwdKey:"+trxSmsPwdKey+"++++isNeedChkFlag:"+isNeedChkFlag);
						result = false;
					}else{
						
						result=true;
					}

				}
			}else{
				
				result = false;
			}
			
		}catch(Exception e){//此处最可能抛异常是在mem里value长度变更时越界。程序修改上线但mem里值未过期时。此时，统一返回不需要校验
			e.printStackTrace();
			logger.debug("++++trxSmsPwdKey:"+trxSmsPwdKey+"++++e:"+e);
			return false;
			
		}
		
		return result;
	}
		
}