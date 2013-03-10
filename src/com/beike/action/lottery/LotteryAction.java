package com.beike.action.lottery;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.action.user.BaseUserAction;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.goods.Goods;
import com.beike.entity.lottery.LotteryInfo;
import com.beike.entity.lottery.PrizeInfo;
import com.beike.entity.user.User;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.service.goods.GoodsService;
import com.beike.service.lottery.LotteryService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.PinyinUtil;
import com.beike.util.TrxConstant;
import com.beike.util.TrxUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;

@Controller
@RequestMapping("/lottery/lotteryAction.do")
public class LotteryAction extends BaseUserAction {

	@Autowired
	private GoodsService goodsService;
	@Autowired
	private ShopsBaoService shopsBaoService;
	@Autowired
	private LotteryService lotteryService;
	
	@Resource(name = "webClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	private static String REGION_CATLOG = "BASE_REGION_CATLOG";

	private static String PROPERTY_CATLOG = "PROPERTY_CATLOG_NEW";

	// 跳转到抽奖购物车
	@RequestMapping(params = "command=lotteryShopcart")
	public String getLotteryShopcart(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//访问url放到cookie里  update by ye.tian
		setCookieUrl(request, response);
		
		String prizeid = request.getParameter("prizeid");
		// 先判断有无登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		PrizeInfo prizeInfo;
		if (prizeid == null) {
			return "redirect:../404.html";
		} else if (user != null) {
			prizeInfo = lotteryService.getPrizeInfo(prizeid);
			if (Calendar.getInstance().getTime().after(prizeInfo.getEndtime())) {
				// 抽奖过期了
				return "redirect:/lottery/lotteryAction.do?command=getAwardResult&prizeid="
						+ prizeid;
			}
			// 是否已参加
			LotteryInfo lotteryInfo = lotteryService.isJoined(prizeid, Long
					.toString(user.getId()));

			// 用户已经参加抽奖展现抽奖结果页
			if (lotteryInfo != null) {
				return "redirect:/lottery/lotteryAction.do?command=getAwardResult&prizeid="
						+ prizeid;
			} else {
				// 用户登陆走正常抽奖流程

				super.setCookieUrl(request, response);
				String goodsid = request.getParameter("goodsid");
				Long goodsLong = Long.parseLong(goodsid);
				Goods goods = goodsService.findById(goodsLong);
				MerchantForm merchantForm = goodsService
						.getMerchantById(goodsLong);
				LotteryGoods lotteryGoods = new LotteryGoods();
				lotteryGoods.setGoodsId(goods.getGoodsId().toString());
				lotteryGoods.setGoodsName(goods.getGoodsname());
				lotteryGoods.setPayPrice(Double.toString(goods
						.getCurrentPrice()));
				lotteryGoods.setRebatePrice(Double.toString(goods
						.getRebatePrice()));
				lotteryGoods.setMerchantId(merchantForm.getId());
				lotteryGoods.setMerchantName(merchantForm.getMerchantname());

				String loginType = "";
				try {

					if (user != null) {

						loginType = TrxUtils.getTrxCookies(response, request,
								TrxConstant.TRX_LOGIN_TYPE + user.getId());
						request.setAttribute("loginStatus", "loginSuc");
						// 获取手机号是否校验过
						if (1 == user.getMobile_isavalible()) {
							request.setAttribute("mobileVerifyStatus",
									"mobileVerifySuc");
						}
						// TODO 调用内部余额查询接口
						Map<String, String> hessianMap = new HashMap<String, String>();
						hessianMap.put("userId", Long.toString(user.getId()));
						hessianMap.put("reqChannel","WEB");
						Map rspMap = trxHessianServiceGateWay
								.getActByUserId(hessianMap);

						if (rspMap == null) {
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

						String balanceAmount = (String) rspMap.get("balance");
						lotteryGoods.setBalanceAmount(balanceAmount);
						lotteryGoods.setUserEmail(user.getEmail());
						lotteryGoods.setUserTel(user.getMobile());
					}

					request.setAttribute("loginType", loginType);
					request.setAttribute("lotteryGoods", lotteryGoods);

					return "/lottery/shoppingCart";
				} catch (Exception e) {

					return "redirect:../500.html";
				}
			}

		} else {
			// 用户没登陆走正常抽奖流程
			super.setCookieUrl(request, response);
			prizeInfo = lotteryService.getPrizeInfo(prizeid);
			int status = prizeInfo.getStatus();
			if (Calendar.getInstance().getTime().after(prizeInfo.getEndtime())
					|| 2 == status || 3 == status) {
				// 抽奖过期了
				return "redirect:/lottery/lotteryAction.do?command=getAwardResult&prizeid="
						+ prizeid;
			} else {
				String goodsid = request.getParameter("goodsid");
				Long goodsLong = Long.parseLong(goodsid);
				Goods goods = goodsService.findById(goodsLong);
				MerchantForm merchantForm = goodsService
						.getMerchantById(goodsLong);
				LotteryGoods lotteryGoods = new LotteryGoods();
				lotteryGoods.setGoodsId(goods.getGoodsId().toString());
				lotteryGoods.setGoodsName(goods.getGoodsname());
				lotteryGoods.setPayPrice(Double.toString(goods
						.getCurrentPrice()));
				lotteryGoods.setRebatePrice(Double.toString(goods
						.getRebatePrice()));
				lotteryGoods.setMerchantId(merchantForm.getId());
				lotteryGoods.setMerchantName(merchantForm.getMerchantname());

				String loginType = "";
				try {

					if (user != null) {

						loginType = TrxUtils.getTrxCookies(response, request,
								TrxConstant.TRX_LOGIN_TYPE + user.getId());

						request.setAttribute("loginStatus", "loginSuc");
						// 获取手机号是否校验过
						if (1 == user.getMobile_isavalible()) {
							request.setAttribute("mobileVerifyStatus",
									"mobileVerifySuc");
						}
						lotteryGoods.setUserEmail(user.getEmail());
						lotteryGoods.setUserTel(user.getMobile());
					}

					request.setAttribute("loginType", loginType);
					request.setAttribute("lotteryGoods", lotteryGoods);

					return "/lottery/shoppingCart";
				} catch (Exception e) {

					return "redirect:../500.html";
				}
			}

		}

	}

	@RequestMapping(params = "command=getAwardResult")
	public String getAwardResult(HttpServletRequest request,
			HttpServletResponse response) throws NumberFormatException,
			Exception {
		
		
		try {
			PrizeInfo prizeInfo;
			String prizeid = request.getParameter("prizeid");
			User user = SingletonLoginUtils.getMemcacheUser(request);
			request.setAttribute("UPLOAD_IMAGES_URL",
					Constant.UPLOAD_IMAGES_URL);
			
			if (prizeid == null) {
				return "redirect:../404.html";
			}
			prizeInfo = lotteryService.getPrizeInfo(prizeid);
			Long mostExpensiveGoodsId = prizeInfo.getGoods_id();
			String city = WebUtils.getCookieValue(CityUtils.CITY_COOKIENAME, request);
			//推荐商品
			List<Map<String, Object>> lstRegionIds = goodsService.getGoodsRegionIds(mostExpensiveGoodsId);
			//用于处理二级地域重复
			int iNextRegionCount = 0;
			String tmpGoodsRegionId = "";
			if(lstRegionIds!=null && lstRegionIds.size()>0){
				Map<String, Object> mapRegionIds = lstRegionIds.get(0);
				
				for(int i=0;i<lstRegionIds.size();i++) {
					Map<String, Object> catlog  = lstRegionIds.get(i);
					//只有一个区域
					if(lstRegionIds.size() == 1){
						tmpGoodsRegionId = String.valueOf(catlog.get("regionextid"));
					}else{
						if(i == 0){
							iNextRegionCount = 1;
							tmpGoodsRegionId = String.valueOf(catlog.get("regionextid"));
						}else{
							if(tmpGoodsRegionId.indexOf(String.valueOf(catlog.get("regionextid")))<0){
								if(iNextRegionCount<=1){
									tmpGoodsRegionId = tmpGoodsRegionId + "," + catlog.get("regionextid");
								}
								iNextRegionCount ++;
								if(iNextRegionCount>2){
									tmpGoodsRegionId = null;
									break;
								}
							}
						}
					}
				}
				
				//获取当前城市ID
				Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get("CITY_CATLOG");
				if (mapCity == null) {
					mapCity = BeanUtils.getCity(request, "regionCatlogDao");
					memCacheService.set("CITY_CATLOG", mapCity);
				}
				
				Long cityid = null;
				if (mapCity != null) {
					cityid = mapCity.get(city.trim());
				}
				//与商品详情页取相同数量推荐商品，保证缓存共享
				List<Long> lstTuijian = goodsService.getSaleWithGoodsIds(
						(Long) mapRegionIds.get("regionid"), tmpGoodsRegionId,
						(Long) mapRegionIds.get("tagid"), cityid, 11L, "");
				
				//剔除当前商品ID，查询推荐商品
				if(lstTuijian!=null){
					if(lstTuijian.contains(mostExpensiveGoodsId)){
						lstTuijian.remove(mostExpensiveGoodsId);
					}
					if(lstTuijian.size()>4){
						lstTuijian = lstTuijian.subList(0, 4);
					}
					List<GoodsForm> lstTuijianGoodsForm = goodsService.getGoodsFormByChildId(lstTuijian);
					request.setAttribute("lstTuijianGoodsForm", lstTuijianGoodsForm);
				}
			}
			if (user != null) {

				String userid = Long.toString(user.getId());
				LotteryInfo lotteryInfo = lotteryService.isJoined(prizeid,
						userid);
				// 已经登录用户,已经抽奖进入抽奖购物车页面
				if (lotteryInfo != null) {
					//prizeInfo = lotteryService.getPrizeInfo(prizeid);
					request.setAttribute("lotteryInfo", lotteryInfo);
					request.setAttribute("lotteryGoods", goodsService
							.findById(prizeInfo.getGoods_id()));
					request.setAttribute("prizeInfo", prizeInfo);
					request.setAttribute("winners", lotteryService
							.getLotteryWinnersNo(prizeid));
					request.setAttribute("flag", "joined");
					return "/lottery/lucky_result";
				} else {
					// 已登录用户,未参加抽奖
					//prizeInfo = lotteryService.getPrizeInfo(prizeid);
					if (Calendar.getInstance().getTime().before(
							prizeInfo.getEndtime())) {
						// 从抽奖商品点击进入抽奖购物车页面--->到结果页
						lotteryInfo = lotteryService.saveLotteryInfo(prizeid,
								userid);
						request.setAttribute("lotteryInfo", lotteryInfo);
						// PrizeInfo prizeInfoNew = lotteryService
						// .getPrizeInfo(prizeid);
						request.setAttribute("lotteryGoods", goodsService
								.findById(prizeInfo.getGoods_id()));
						request.setAttribute("prizeInfo", prizeInfo);
						request.setAttribute("flag", "normal");
						request.setAttribute("winners", lotteryService
								.getLotteryWinnersNo(prizeid));
						lotteryService
								.participantEmail(Long.parseLong(prizeid), Long
										.parseLong(userid));
						return "/lottery/lucky_result";
					}
				}

				//prizeInfo = lotteryService.getPrizeInfo(prizeid);
				// 用户登录,商品过期,其它路径进入抽奖结果页的标识符
				request.setAttribute("prizeInfo", prizeInfo);
				request.setAttribute("flag", "valid");
				request.setAttribute("winners", lotteryService
						.getLotteryWinnersNo(prizeid));
				return "/lottery/lucky_result";
			} else {
				// 用户未登录或者商品过期,其它路径进入抽奖结果页的标识符
				//prizeInfo = lotteryService.getPrizeInfo(prizeid);
				request.setAttribute("prizeInfo", prizeInfo);
				request.setAttribute("flag", "valid");
				request.setAttribute("winners", lotteryService
						.getLotteryWinnersNo(prizeid));
				return "/lottery/lucky_result";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "redirect:../500.html";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "command=showLotteryGoods")
	public String getLotteryGoodsDetail(HttpServletRequest request,
			HttpServletResponse response) throws NumberFormatException,
			Exception {

		try {
			Long goodsid;
			String prizeid = request.getParameter("prizeid");
			// 用户登陆后跳转路径
			super.setCookieUrl(request, response);
			if (prizeid != null || "".equals(prizeid)) {

				PrizeInfo prizeInfo = lotteryService.getPrizeInfo(prizeid);
				if (prizeInfo != null) {
					request.setAttribute("lotteryGoodsInfo", prizeInfo);
					GoodsCatlog goodsCatlog = null;
					Goods goods = null;
					goodsid = prizeInfo.getGoods_id();
					goods = goodsService.findById(goodsid);
					// 汉语转换成拼音
					String city = PinyinUtil.hanziToPinyin(goods.getCity(), "");
					
					Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get("CITY_CATLOG");
					if (mapCity == null) {
						mapCity = BeanUtils.getCity(request, "regionCatlogDao");
						memCacheService.set("CITY_CATLOG", mapCity);
					}

					Long cityid = null;
					if (mapCity != null) {
						cityid = mapCity.get(city.trim());
					}
					
					goodsCatlog = goodsService.searchGoodsRegionById(goods
							.getGoodsId());
					request.setAttribute("goodDetail", goods);
					request.setAttribute("goodsCatlog", goodsCatlog);

					// 包含页面的url
					String detailUrl = goodsService
							.getGoodDetailIncliudeUrl(goodsid);
					// 获得销售量
					String salescount = goodsService.salesCount(goodsid);
					// 销售量
					request.setAttribute("SALES_COUNT", salescount);
					request.setAttribute("UPLOAD_IMAGES_URL",
							Constant.UPLOAD_IMAGES_URL);
					// 判断此文件是否存在 假如不存在页面不包含

					String detailFile = request.getRealPath("")
							+ "/jsp/goods_detail/" + detailUrl;
					File file = new File(detailFile);
					if (!file.exists()) {
						detailUrl = null;
					}
					request.setAttribute("detailUrl", detailUrl);
					BigDecimal big = new BigDecimal(goods.getSourcePrice()
							- goods.getCurrentPrice());
					big = big.setScale(1, BigDecimal.ROUND_HALF_UP);
					request.setAttribute("offerPrice", big.floatValue());


					// 商家信息

					MerchantForm merchantForm = shopsBaoService
							.getMerchantDetailByGoodsId(goodsid);
					request.setAttribute("merchantForm", merchantForm);

					if (merchantForm != null && merchantForm.getId() != null) {

						request.setAttribute("prizeInfo", prizeInfo);
						request.setAttribute("timeScale", (prizeInfo
								.getEndtime().getTime() - Calendar
								.getInstance().getTimeInMillis()) / 1000);
						request.setAttribute("prizeid", prizeid);
						// 商圈、属性分类
						Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService
								.get(REGION_CATLOG);

						Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService.get(PROPERTY_CATLOG);

						Map<Long, List<RegionCatlog>> property_catlog = null;

						// 假如memcache里没有就从数据库里查
						if (regionMap == null) {
							regionMap = BeanUtils.getCityCatlog(request,
									"regionCatlogDao");
							memCacheService.set(REGION_CATLOG, regionMap);
						}

						if(propertCatlogMap == null){
							propertCatlogMap = BeanUtils.getCatlog(request,"propertyCatlogDao");
							property_catlog  = propertCatlogMap.get(cityid);
							memCacheService.set(PROPERTY_CATLOG, propertCatlogMap,60*60*24*360);
						}else{
							property_catlog  = propertCatlogMap.get(cityid);
						}
						// 当前城市商圈
						Map<Long, List<RegionCatlog>> curRegionMap = regionMap
								.get(city);
						//
						Map<Long, RegionCatlog> regionKeyMap = new HashMap<Long, RegionCatlog>();
						if (curRegionMap != null && !curRegionMap.isEmpty()) {
							for (Long regionKey : curRegionMap.keySet()) {
								List<RegionCatlog> lstRegion = curRegionMap
										.get(regionKey);
								if (lstRegion != null && lstRegion.size() > 0) {
									for (RegionCatlog region : lstRegion) {
										regionKeyMap.put(region.getCatlogid(),
												region);
									}
								}
							}
						}

						Map<Long, RegionCatlog> tagKeyMap = new HashMap<Long, RegionCatlog>();
						if (property_catlog != null
								&& !property_catlog.isEmpty()) {
							for (Long tagKey : property_catlog.keySet()) {
								List<RegionCatlog> lstTag = property_catlog
										.get(tagKey);
								if (lstTag != null && lstTag.size() > 0) {
									for (RegionCatlog region : lstTag) {
										tagKeyMap.put(region.getCatlogid(),
												region);
									}
								}
							}
						}

						// 一级商圈
						String regionName = "";
						List<Map<String, Object>> lstFirstRegion = goodsService
								.getGoodsFirstRegionById(goodsid);
						if (lstFirstRegion != null
								&& lstFirstRegion.size() == 1) {
							RegionCatlog tmpregion = regionKeyMap
									.get((Long) lstFirstRegion.get(0).get(
											"regionid"));
							if (tmpregion != null) {
								regionName = tmpregion.getCatlogName();
							}
						}
						request.setAttribute("firstRegionName", regionName);

					
						List<Map<String, Object>> lstRegionIds = goodsService
								.getGoodsRegionIds(goodsid);
						// 相关分类
						List<Object[]> lstGoodsTag = new LinkedList<Object[]>();
						// 相关商圈
						List<Object[]> lstGoodsRegion = new LinkedList<Object[]>();
						// 用于处理二级地域重复
						int iNextRegionCount = 0;
						String tmpGoodsRegionId = "";
						if (lstRegionIds != null && lstRegionIds.size() > 0) {
							Map<String, Object> mapRegionIds = lstRegionIds
									.get(0);

							for (int i = 0; i < lstRegionIds.size(); i++) {
								Map<String, Object> catlog = lstRegionIds
										.get(i);
								// 分类
								if (i == 0) {
									Object[] aryGoodTag1 = new Object[3];
									RegionCatlog tag1 = tagKeyMap.get(catlog
											.get("tagid"));
									if (tag1 != null) {
										aryGoodTag1[0] = tag1
												.getRegion_enname();
										aryGoodTag1[2] = tag1.getCatlogName();
										lstGoodsTag.add(aryGoodTag1);
									}

									Object[] aryGoodTag2 = new Object[3];
									RegionCatlog tag2 = tagKeyMap.get(catlog
											.get("tagextid"));
									if (tag1 != null && tag2 != null) {
										aryGoodTag2[0] = tag1
												.getRegion_enname();
										aryGoodTag2[1] = tag2
												.getRegion_enname();
										aryGoodTag2[2] = tag2.getCatlogName();
										lstGoodsTag.add(aryGoodTag2);
									}
								}
								// 只有一个区域
								if (lstRegionIds.size() == 1) {
									Object[] aryGoodRegion1 = new Object[3];
									RegionCatlog region1 = regionKeyMap
											.get(catlog.get("regionid"));
									if (region1 != null) {
										aryGoodRegion1[0] = region1
												.getRegion_enname();
										aryGoodRegion1[2] = region1
												.getCatlogName();
										lstGoodsRegion.add(aryGoodRegion1);
									}

									Object[] aryGoodRegion2 = new Object[3];
									RegionCatlog region2 = regionKeyMap
											.get(catlog.get("regionextid"));
									if (region1 != null && region2 != null) {
										aryGoodRegion2[0] = region1
												.getRegion_enname();
										aryGoodRegion2[1] = region2
												.getRegion_enname();
										aryGoodRegion2[2] = region2
												.getCatlogName();
										lstGoodsRegion.add(aryGoodRegion2);
									}

									tmpGoodsRegionId = String.valueOf(catlog
											.get("regionextid"));
								} else {
									if (i == 0) {
										Object[] aryGoodRegion1 = new Object[3];
										RegionCatlog region1 = regionKeyMap
												.get(catlog.get("regionid"));
										if (region1 != null) {
											aryGoodRegion1[0] = region1
													.getRegion_enname();
											aryGoodRegion1[2] = region1
													.getCatlogName();
											lstGoodsRegion.add(aryGoodRegion1);
										}

										Object[] aryGoodRegion2 = new Object[3];
										RegionCatlog region2 = regionKeyMap
												.get(catlog.get("regionextid"));
										if (region1 != null && region2 != null) {
											aryGoodRegion2[0] = region1
													.getRegion_enname();
											aryGoodRegion2[1] = region2
													.getRegion_enname();
											aryGoodRegion2[2] = region2
													.getCatlogName();
											lstGoodsRegion.add(aryGoodRegion2);
										}

										iNextRegionCount = 1;
										tmpGoodsRegionId = String
												.valueOf(catlog
														.get("regionextid"));
									} else {
										Object[] aryGoodRegion = null;
										RegionCatlog region1 = regionKeyMap
												.get(catlog.get("regionid"));
										RegionCatlog region2 = regionKeyMap
												.get(catlog.get("regionextid"));
										if (region1 != null && region2 != null) {
											aryGoodRegion = new Object[3];
											aryGoodRegion[0] = region1
													.getRegion_enname();
											aryGoodRegion[1] = region2
													.getRegion_enname();
											aryGoodRegion[2] = region2
													.getCatlogName();
										}

										if (tmpGoodsRegionId.indexOf(String
												.valueOf(catlog
														.get("regionextid"))) < 0) {
											if (iNextRegionCount <= 1) {
												if (aryGoodRegion != null) {
													lstGoodsRegion.remove(0);
													lstGoodsRegion
															.add(aryGoodRegion);
												}

												tmpGoodsRegionId = tmpGoodsRegionId
														+ ","
														+ catlog
																.get("regionextid");
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

							List<Long> lstTuijian1 = goodsService
									.getSaleWithGoodsIds((Long) mapRegionIds
											.get("regionid"), tmpGoodsRegionId,
											(Long) mapRegionIds.get("tagid"),
											cityid, 11L, "");

							// 剔除当前商品ID，查询推荐商品
							if (lstTuijian1 != null) {
								if (lstTuijian1.contains(goodsid)) {
									lstTuijian1.remove(goodsid);
								} else if (lstTuijian1.size() > 10) {
									lstTuijian1.remove(lstTuijian1.size() - 1);
								}
								if (lstTuijian1.size() > 0) {
									List<GoodsForm> lstTuijian1GoodsForm = goodsService
											.getGoodsFormByChildId(lstTuijian1);
									
									
									
									request.setAttribute(
											"lstTuijian2GoodsForm",
											lstTuijian1GoodsForm);
								}
							}
							request.setAttribute("lstGoodsTag", lstGoodsTag);
							request.setAttribute("lstGoodsRegion",
									lstGoodsRegion);
						}
						return "lottery/product";
					}

				} else {
					return "redirect:../404.html";
				}

			}

		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:../404.html";
	}
}
