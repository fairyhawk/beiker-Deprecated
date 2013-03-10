package com.beike.action.lottery;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.LogAction;
import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.action.user.BaseUserAction;
import com.beike.entity.lottery.LotteryInfoNew;
import com.beike.entity.lottery.LotteryTicket;
import com.beike.entity.lottery.PrizeInfoNew;
import com.beike.entity.user.User;
import com.beike.form.GoodsForm;
import com.beike.service.goods.GoodsService;
import com.beike.service.invite.LotteryInviteService;
import com.beike.service.lottery.LotteryServiceNew;
import com.beike.util.TrxConstant;
import com.beike.util.TrxUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;

@Controller
@RequestMapping("/lottery/lotteryNewAction.do")
public class LotteryNewAction extends BaseUserAction {

	@Autowired
	private LotteryServiceNew lotteryServiceNew;
	@Autowired
	private LotteryInviteService inviteService;
	
	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;

	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();

	private Logger logger = Logger.getLogger(this.getClass());

	private static String CITY_CATLOG = "CITY_CATLOG";
	
	private static String SHORT_URL="SHORTURL";

	@Autowired
	private GoodsService goodsService;

	public GoodsService getGoodsService() {
		return goodsService;
	}

	public void setGoodsService(GoodsService goodsService) {
		this.goodsService = goodsService;
	}

	/**
	 * 奖品详情
	 * 
	 * @author janwen
	 * @time Dec 20, 2011 2:59:03 PM
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "command=showLotteryGoods")
	public String getLotteryInfoNew(HttpServletRequest request,
			HttpServletResponse response) {
		try {

			super.setCookieUrl(request, response);
			String newprize_id = request.getParameter("prizeid");
			int lotteryStatus=lotteryServiceNew.getLotteryInfoStatus(newprize_id);
			if(lotteryStatus==0){
				return "redirect:../404.html";
			}
			logger.info("抽奖ID为 prizeid:"+newprize_id);
			
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService
					.get(CITY_CATLOG);
			if (mapCity != null) {
				String city = CityUtils.getCity(request, response);
				// 查询推荐商品
				Long cityid = mapCity.get(city);
				if (cityid != null) {
					List<Long> recommendIds = lotteryServiceNew
							.getRecommendGoodsID(String.valueOf(cityid));
					List<GoodsForm> lstTuijian1GoodsForm = goodsService
							.getGoodsFormByChildId(recommendIds);
					request.setAttribute("tuijianlist", lstTuijian1GoodsForm);
				}
			}

			if (newprize_id != null && isDigital(newprize_id)) {
				// 先判断抽奖活动有效期,及是否存在该抽奖活动
				LotteryInfoNew lotteryInfo = lotteryServiceNew
						.getLotteryInfoNew(newprize_id);
				lotteryStatus = lotteryServiceNew.getLotteryStatus(newprize_id);
				// 活动结束,走其他action
				if (lotteryStatus == 0) {
					return "redirect:/lottery/lotteryNewAction.do?command=getFinalResult&prizeid="
							+ newprize_id;
				}
				if (lotteryInfo != null) {
					// 抽奖存在不判断是否结束
					// 判断是否登录 并且判断此人是否参加过抽奖
					User user = getMemcacheUser(request);
					if (user != null) {
						Long joinid = lotteryServiceNew.isJoined(newprize_id,
								user.getId() + "");
						if (joinid != null && joinid != 0) {
							request.setAttribute("isjoined", "true");
						}
					}
					request.setAttribute("lotteryInfo", lotteryInfo);
					if (lotteryInfo.getStartprize_seedtime() != null) {
						request.setAttribute("timeScale", (lotteryInfo
								.getStartprize_seedtime().getTime() - Calendar
								.getInstance().getTimeInMillis()) / 1000);
					}
					return "/lotteryNew/lotteryGoodsNew";

				} else {
					// 抽奖活动不存在404
					logger.info("抽奖活动不存在 prizeid:"+newprize_id);
					return "redirect:../404.html";
				}
			} else {
				// prizeid非数值直接404
				logger.info("prize not a number newprizeid:"+newprize_id);
				return "redirect:../404.html";
			}
		} catch (Exception e) {
			logger.error("2.0抽奖,奖品详情页异常");
			e.printStackTrace();
			return "redirect:../404.html";
		}

	}

	/**
	 * 正常抽奖流程,跳转到购物车页面
	 * 
	 * @author janwen
	 * @time Dec 20, 2011 9:12:37 PM
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "command=gotoShopcartNew")
	public String gotoShopcartNew(HttpServletRequest request,
			HttpServletResponse response) {
		try {

			// 先判断有无登录
			User user = getMemcacheUser(request);
			String newprize_id = request.getParameter("prizeid");
			super.setCookieUrl(request, response);

			if (newprize_id != null && isDigital(newprize_id)) {
				// 先判断抽奖活动是否存在,是否结束
				LotteryInfoNew lotteryInfoNew = lotteryServiceNew
						.getLotteryInfoNew(newprize_id);
				int lotteryStatus = lotteryServiceNew
						.getLotteryStatus(newprize_id);
				// 活动结束,走其他action
				if (lotteryStatus == 0) {
					return "redirect:/lottery/lotteryNewAction.do?command=getFinalResult&prizeid="
							+ newprize_id;
				}
				if (lotteryInfoNew != null && lotteryStatus != 0) {
					// 用户登录判断是否参与过抽奖
					if (user != null) {
						Long joined = lotteryServiceNew.isJoined(newprize_id,
								String.valueOf(user.getId()));
						if (joined != null && joined > 0) {
							// 参与过抽奖跳转到结果详情页
							return "redirect:/lottery/lotteryNewAction.do?command=gotoResultNew&prizeid="
									+ newprize_id;
						} else {
							// 打印日志
							Map<String, String> logMap2 = LogAction.getLogMap(request,
									response);
							logMap2.put("action", "h_join");
							logMap2.put("uid", user.getId() + "");
							logMap2.put("trx_id", newprize_id);
							LogAction.printLog(logMap2);
							
							// 用户登录未参与抽奖,跳转到购物车,并传递用户信息
							String loginType = TrxUtils.getTrxCookies(response,
									request, TrxConstant.TRX_LOGIN_TYPE
											+ user.getId());
							request.setAttribute("loginStatus", "loginSuc");
							// 获取手机号是否校验过
							if (1 == user.getMobile_isavalible()) {
								request.setAttribute("mobileVerifyStatus",
										"mobileVerifySuc");
							}
							// 调用内部余额查询接口
							Map<String, String> hessianMap = new HashMap<String, String>();
							hessianMap.put("userId", Long
									.toString(user.getId()));
							hessianMap.put("reqChannel","WEB");
							Map rspMap = trxHessianServiceGateWay
									.getActByUserId(hessianMap);

							if (rspMap == null) {
								request.setAttribute("ERRMSG", "账户余额获取失败！");
								throw new Exception();
							}
							// 如果有通讯。获取余额异常
							if (!"1".equals(rspMap.get("rspCode"))) {
								request.setAttribute("ERRMSG", "账户余额获取异常！");
								// return "error";
								throw new Exception();
							}
							String balanceAmount = (String) rspMap
									.get("balance");
							request.setAttribute("loginType", loginType);
							request
									.setAttribute("balanceAmount",
											balanceAmount);
							request.setAttribute("useremail", user.getEmail());
							request.setAttribute("usertel", user.getMobile());
							request
									.setAttribute("lotteryGoods",
											lotteryInfoNew);
							return "lotteryNew/shoppingCartNew";
						}
					} else {
						// 打印日志
						Map<String, String> logMap2 = LogAction.getLogMap(request,
								response);
						logMap2.put("action", "h_join");
						logMap2.put("uid", "");
						logMap2.put("trx_id", newprize_id);
						LogAction.printLog(logMap2);
						
						// 用户未登录,有可能未注册,走正常抽奖流程
						request.setAttribute("lotteryGoods", lotteryInfoNew);
						request.setAttribute("loginType", "");
						return "lotteryNew/shoppingCartNew";
					}
				} else {
					return "redirect:../404.html";
				}

			} else {
				// 非法prizeid 404
				return "redirect:../404.html";
			}
		} catch (Exception e) {
			logger.error("抽奖2.0购物车页面异常");
			e.printStackTrace();
			return "redirect:../404.html";
		}

	}

	/**
	 * 跳转到结果页
	 * 
	 * @author janwen
	 * @time Dec 21, 2011 1:15:18 PM
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@RequestMapping(params = "command=gotoResultNew")
	public String gotoResultNew(HttpServletRequest request,
			HttpServletResponse response) {

		try {

			String newprize_id = request.getParameter("prizeid");
			String startprize_id = "";
			User user = getMemcacheUser(request);
			// 左侧推荐商品
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService
					.get(CITY_CATLOG);

			if (mapCity != null) {
				String city = CityUtils.getCity(request, response);
				// 查询推荐商品
				Long cityid = mapCity.get(city);
				if (cityid != null) {
					List<Long> recommendIds = lotteryServiceNew
							.getRecommendGoodsID(String.valueOf(cityid));
					List<GoodsForm> lstTuijian1GoodsForm = goodsService
							.getGoodsFormByChildId(recommendIds);
					request.setAttribute("tuijianlist", lstTuijian1GoodsForm);
				}
			}
			if (newprize_id != null && isDigital(newprize_id)) {
				LotteryInfoNew lotteryInfoNew = lotteryServiceNew
						.getLotteryInfoNew(newprize_id);
				int lotteryStatus = lotteryServiceNew
						.getLotteryStatus(newprize_id);
				// 活动结束,走其他action
				if (lotteryStatus == 0) {
					return "redirect:/lottery/lotteryNewAction.do?command=getFinalResult&prizeid="
							+ newprize_id;
				}
				// 抽奖活动存在并且未结束,保存用户奖券信息
				if (lotteryInfoNew != null && lotteryStatus != 0) {
					startprize_id = lotteryInfoNew.getStartprize_id() + "";
					// 用户正常第一次参与抽奖,保存用户奖号信息
					if (user != null && user.getMobile_isavalible() == 1
							&& lotteryServiceNew.isJoined(newprize_id, String
									.valueOf(user.getId())) == 0) {
						lotteryServiceNew.saveLotteryTicketInfo(newprize_id,
								"参与抽奖获得", "3", String.valueOf(user.getId()),
								startprize_id);
						Thread.currentThread().sleep(1000);
						List<PrizeInfoNew> results = lotteryServiceNew
								.getPrizeInfoNew(newprize_id);
						List<LotteryTicket> tickets = lotteryServiceNew
								.getLotteryTicketInfo(newprize_id, String
										.valueOf(user.getId()));

						request.setAttribute("inviteurl", inviteService
								.addShortURL(user.getId(),"/lottery/lotteryNewAction.do?command=showLotteryGoods&uid=" + user.getId()+ "&prizeid=" + newprize_id,newprize_id));
						request.setAttribute("remindertime", 24 * 60 * 60);
						request.setAttribute("results", results);
						request.setAttribute("tickets", tickets);
						request.setAttribute("lotteryGoods", lotteryServiceNew
								.getLotteryInfoNew(newprize_id));

						// 0元 抽奖2.0版本 wenjie.mai
						Long userId = user.getId();

						String shortUrl=WebUtils.getCookieValue(SHORT_URL, request);
						if(!StringUtils.isEmpty(shortUrl)){
							List shortList = inviteService.getShortUrl(shortUrl);
							if(shortList  != null && shortList.size() >0){  // 邀请
								Map mx     = (Map) shortList.get(0);
								Long uid   = (Long) mx.get("user_id");
								if(!uid.equals(userId)){
									inviteService.addPrizeInviteRecord(uid, userId,Long.parseLong(newprize_id));
									Cookie rcookie=WebUtils.removeableCookie(SHORT_URL);
									response.addCookie(rcookie);
								}
							}

						}

						// 0元 抽奖2.0版本 wenjie.mai end
						return "lotteryNew/lucky_result_new";
					} else if (user != null
							&& lotteryServiceNew.isJoined(newprize_id, String
									.valueOf(user.getId())) != 0) {
						// 登录用户,参与过抽奖,直接跳转到结果页面,不保存奖号信息
						List<PrizeInfoNew> results = lotteryServiceNew
								.getPrizeInfoNew(newprize_id);
						List<LotteryTicket> tickets = lotteryServiceNew
								.getLotteryTicketInfo(newprize_id, String
										.valueOf(user.getId()));
						request.setAttribute("inviteurl", inviteService
								.getShortUrlByUserId(user.getId(),newprize_id));
						request.setAttribute("results", results);
						request.setAttribute("tickets", tickets);
						request.setAttribute("lotteryGoods", lotteryServiceNew
								.getLotteryInfoNew(newprize_id));
						Timestamp remindertime = lotteryServiceNew
								.getRemainderInviteTime(user.getId() + "",
										newprize_id);
						if (Calendar.getInstance().getTimeInMillis()
								- (remindertime.getTime() + 24 * 60 * 60 * 1000) > 0) {
							request.setAttribute("remindertime", 0);
						} else {
							request.setAttribute("remindertime", (remindertime
									.getTime()
									+ 24 * 60 * 60 * 1000 - Calendar
									.getInstance().getTimeInMillis()) / 1000);
						}

						return "lotteryNew/lucky_result_new";
					} else {
						// 用户未登录神码的并且抽奖活动未结束,跳转到抽奖详情页
						return "redirect:/lottery/lotteryNewAction.do?command=showLotteryGoods&prizeid="
								+ newprize_id;
					}
					// 抽奖活动存在,但抽奖活动已经结束
				} else if (lotteryInfoNew != null && lotteryStatus == 0) {
					// 登录用户未参与过,final抽奖活动结束标志符
					request.setAttribute("final", "1");
					if (user != null
							&& lotteryServiceNew.isJoined(newprize_id, String
									.valueOf(user.getId())) == 0) {
						List<PrizeInfoNew> results = lotteryServiceNew
								.getPrizeInfoNew(newprize_id);
						List<LotteryTicket> tickets = lotteryServiceNew
								.getLotteryTicketInfo(newprize_id, String
										.valueOf(user.getId()));
						request.setAttribute("results", results);
						request.setAttribute("tickets", tickets);
						request.setAttribute("lotteryGoods", lotteryServiceNew
								.getLotteryInfoNew(newprize_id));
						return "lotteryNew/lucky_result_new";
					} else if (user != null
							&& lotteryServiceNew.isJoined(newprize_id, String
									.valueOf(user.getId())) != 0) {
						// 登录用户,参与过抽奖,直接跳转到结果页面,不保存奖号信息
						List<PrizeInfoNew> results = lotteryServiceNew
								.getPrizeInfoNew(newprize_id);
						List<LotteryTicket> tickets = lotteryServiceNew
								.getLotteryTicketInfo(newprize_id, String
										.valueOf(user.getId()));
						request.setAttribute("results", results);
						request.setAttribute("tickets", tickets);
						request.setAttribute("lotteryGoods", lotteryServiceNew
								.getLotteryInfoNew(newprize_id));
						return "lotteryNew/lucky_result_new";
					} else {
						return "redirect:../404.html";
					}
				}

			} else {
				return "redirect:../404.html";
			}
			return "redirect:../404.html";

		} catch (Exception e) {
			logger.error("2.0抽奖结果页异常" + e.getMessage());
			return "redirect:../404.html";
		}

	}
	
	@RequestMapping(params = "command=showMyLottery")
	public String showMyLettery(HttpServletRequest request,HttpServletResponse response){
		
		User user=super.getMemcacheUser(request);
		if(user==null){
			return "user/login";
		}
		Long userId=user.getId();
		List<LotteryTicket> listLotteryTicket=lotteryServiceNew.getLotteryTicketInfo(userId);
		request.setAttribute("listLotteryTicket",listLotteryTicket);
		
		return "user/showMyLottery";
	}

	/**
	 * 抽奖活动结束专用action
	 */
	@RequestMapping(params = "command=getFinalResult")
	public String getFinalResults(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String newprize_id = request.getParameter("prizeid");
			User user = getMemcacheUser(request);
			// 左侧推荐商品
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService
					.get(CITY_CATLOG);

			if (mapCity != null) {
				String city = CityUtils.getCity(request, response);
				// 查询推荐商品
				Long cityid = mapCity.get(city);
				if (cityid != null) {
					List<Long> recommendIds = lotteryServiceNew
							.getRecommendGoodsID(String.valueOf(cityid));
					List<GoodsForm> lstTuijian1GoodsForm = goodsService
							.getGoodsFormByChildId(recommendIds);
					request.setAttribute("tuijianlist", lstTuijian1GoodsForm);
				}
			}
			if (newprize_id != null && isDigital(newprize_id)) {
				LotteryInfoNew lotteryInfoNew = lotteryServiceNew
						.getFinalLotteryResult(newprize_id);
				request.setAttribute("final", "1");
				// 抽奖活动存在并且结束
				if (lotteryInfoNew != null) {
					//用户登录判断查询相关奖号信息
					if(user != null){
						List<PrizeInfoNew> results = lotteryServiceNew
								.getPrizeInfoNew(newprize_id);
						List<LotteryTicket> tickets = lotteryServiceNew
								.getLotteryTicketInfo(newprize_id, String
										.valueOf(user.getId()));
						request.setAttribute("remindertime", 0);
						request.setAttribute("results", results);
						request.setAttribute("tickets", tickets);
						request.setAttribute("lotteryGoods", lotteryInfoNew);
					}else{
						//用户未登录
						List<PrizeInfoNew> results = lotteryServiceNew
						.getPrizeInfoNew(newprize_id);
					request.setAttribute("remindertime", 0);
						request.setAttribute("results",
								results);
						request.setAttribute("tickets",
								new ArrayList<LotteryTicket>());
						request.setAttribute("lotteryGoods", lotteryInfoNew);
					}
					
					return "lotteryNew/lucky_result_new";
				} else {
					return "redirect:../404.html";
				}
			} else {
				return "redirect:../404.html";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:../404.html";
		}
	}

	private Pattern pattern = Pattern.compile("\\d+");

	/**
	 * 所有id进行数字类型校验
	 * 
	 * @author janwen
	 * @time Dec 20, 2011 3:37:09 PM
	 * 
	 * @param source
	 * @return
	 */
	private boolean isDigital(String source) {
		Matcher matcher = pattern.matcher(source);
		return matcher.find();
	}
}
