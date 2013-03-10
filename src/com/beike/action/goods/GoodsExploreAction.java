package com.beike.action.goods;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.LogAction;
import com.beike.action.user.BaseUserAction;
import com.beike.common.catlog.service.GoodsCatlogService;
import com.beike.common.enums.user.ProfileType;
import com.beike.core.service.trx.limit.PayLimitService;
import com.beike.dao.WeiboDao;
import com.beike.entity.catlog.GoodsCatlog;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.flagship.Flagship;
import com.beike.entity.goods.EvaluateGoods;
import com.beike.entity.goods.GoodKindly;
import com.beike.entity.goods.Goods;
import com.beike.entity.goods.ShowOrderGoods;
import com.beike.entity.user.User;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.form.OrderEvaluationForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.activity.GoodsActivityService;
import com.beike.service.comment.CommentService;
import com.beike.service.common.WeiboService;
import com.beike.service.diancai.DianCaiService;
import com.beike.service.flagship.FlagshipService;
import com.beike.service.goods.GoodsService;
import com.beike.service.goods.ad.ADGoodsService;
import com.beike.service.lucene.recommend.LuceneRecommendService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.service.unionpage.UnionPageService;
import com.beike.service.user.UserService;
import com.beike.service.waimai.WaiMaiService;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.Digest;
import com.beike.util.HttpClientUtil;
import com.beike.util.MobilePurseSecurityUtils;
import com.beike.util.PinyinUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.hao3604j.Tuan360Model;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.ipparser.IPSeeker;
import com.beike.util.singletonlogin.SingletonLoginUtils;


/**  
* @Title:  探索版详情页Action
* @Package com.beike.action.goods
* @Description: TODO
* @author wenjie.mai  
* @date May 28, 2012 1:04:49 PM
* @version V1.0  
*/
@Controller
public class GoodsExploreAction extends BaseUserAction {
	
	public static final String GOODS_TEMPLATE_URL="http://www.qianpin.com/jsp/templates/goodstemplate/goodstemplate.jsp";
	//活动商品标识集合
	public static List<Long> goodsIdList = EvaluateGoods.getGoodsIdList();
	
	public static List<Long> goodsListExtend=EvaluateGoods.getGoodsIdListExtend();
	
	public static List<Long> showOrdergoodsList = ShowOrderGoods.getGoodsIdList();
	
	@Autowired
	private GoodsActivityService goodsActivityService;//活动商品service
	
	private static final String MEMEVAL_KEY = "SDVQ4124DSLsdfKfgjGDFer12343WGVCH";

	private static String HUODONG_URL="http://www.qianpin.com/huodong/20121112/";
	/*
	 * 外卖
	 */
	@Autowired
	private WaiMaiService waiMaiService;
	/*
	 * 点菜
	 */
	@Autowired
	private DianCaiService dianCaiService;
	
	@Autowired
	private FlagshipService flagshipService;

	public void setDianCaiService(DianCaiService dianCaiService) {
		this.dianCaiService = dianCaiService;
	}

	public void setGoodsActivityService(GoodsActivityService goodsActivityService) {
		this.goodsActivityService = goodsActivityService;
	}

	public void setWaiMaiService(WaiMaiService waiMaiService) {
		this.waiMaiService = waiMaiService;
	}
	
	@RequestMapping("/goodstemplate/getGoodsTemplate.do")
	public Object getGoodsTemplates(ModelMap model,HttpServletRequest request,HttpServletResponse response){
		String city=request.getParameter("city");
		
		if(org.apache.commons.lang.StringUtils.isBlank(city)){
			city="beijing";
		}
		StringBuilder sb=new StringBuilder(GOODS_TEMPLATE_URL);
		sb.append("?city_template=");
		sb.append(city);
		
		String responseHtml=HttpClientUtil.getResponseByGet(sb.toString(), null,"UTF-8");
		try {
			response.getWriter().write(responseHtml);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	@RequestMapping("/city/getCity.do")
	public Object showCity(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String requestIP=request.getParameter("ip");
		String city="";
		if(org.apache.commons.lang.StringUtils.isBlank(requestIP)){
			city="beijing";
			try {
				response.getWriter().print(city);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		IPSeeker seeker = IPSeeker.getInstance();
		 city = seeker.getCity(requestIP);
		
		if(org.apache.commons.lang.StringUtils.isBlank(city)){
			city="beijing";
		}
		try {
			response.getWriter().print(city);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	public GoodsExploreAction() {
		
	}
	
	private static Log log = LogFactory.getLog(GoodsExploreAction.class);
	
	private final PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private GoodsCatlogService goodsCatlogService;
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	
	@Autowired
	private ShopsBaoService shopsBaoService;
	
	@Autowired
	private PayLimitService payLimitService;
	
	private static String REGION_CATLOG = "BASE_REGION_CATLOG";

	private static String PROPERTY_CATLOG = "PROPERTY_CATLOG_NEW";
	
	@Autowired
	private LuceneRecommendService luceneRecommendService;
	
	@Autowired
	private ADGoodsService adGoodsService;
	
	@Autowired
	private UnionPageService unionPageService;
	
	private String GOODS_URL = "/goods/";
	
	private String DETAIL_URL="http://www.qianpin.com/goods/showGoodDetail.do";

	private String GOOD_EXPLORE_URL="http://www.qianpin.com/goods/showGoodExplore.do";
	
	private static String CITY_CATLOG = "CITY_CATLOG";
	private WeiboService weiboService;
	@Resource(name = "userService")
	private UserService userService;
	@Autowired
	private WeiboDao weiboDao;
	@Autowired
	private CommentService commentService;
	private static final String TUAN360_APP_SECRET = "TUAN360_APP_SECRET";
	private static final String TUAN360_APP_KEY = "TUAN360_APP_KEY";
	@SuppressWarnings("unchecked")
	@RequestMapping("/goods/redirectGoods.do")
	public Object show(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String goodId = request.getParameter("goodId");
		if (goodId == null || "".equals(goodId)) {
			request.setAttribute("ERRMSG", "没有找到相关商品!");
			return new ModelAndView("redirect:../404.html");
		}
		
		//获得请求所有参数
		String requestParam=WebUtils.parseQueryString(request);
		StringBuilder sb=new StringBuilder();
		String abstractousideID=request.getParameter("abacusoutsid");
		
		// 360的处理
		if (!org.apache.commons.lang.StringUtils.isBlank(abstractousideID)
				&& (abstractousideID.startsWith("api_free_360") || abstractousideID
						.startsWith("api_fee_360"))) {

			String qid = request.getParameter("qid");
			String qname = request.getParameter("qname");
			String qmail = request.getParameter("qmail");
			String sign = request.getParameter("sign");
			String from = request.getParameter("from");
			
			
			
			
			if(org.apache.commons.lang.StringUtils.isNotBlank(qid)&&org.apache.commons.lang.StringUtils.isNotBlank(sign)){
				// qid|qname|qmail|from|key|secret
				StringBuilder sb_source = new StringBuilder();
				String _key = propertyUtil.getProperty(TUAN360_APP_KEY);
				String _secret = propertyUtil.getProperty(TUAN360_APP_SECRET);
				sb_source.append(qid);
				sb_source.append("|");
				sb_source.append(qname);
				sb_source.append("|");
				sb_source.append(qmail);
				sb_source.append("|");
				sb_source.append(from);
				sb_source.append("|");
				sb_source.append(_key);
				sb_source.append("|");
				sb_source.append(_secret);

				Tuan360Model accessToken = new Tuan360Model();
				if(org.apache.commons.lang.StringUtils.isNotBlank(qid)){
					accessToken.setQid(qid);
				}
				if(org.apache.commons.lang.StringUtils.isNotBlank(qname)){
					accessToken.setQname(qname);
				}
				if(org.apache.commons.lang.StringUtils.isNotBlank(qmail)){
					accessToken.setQmail(qmail);
				}

				String md5Value = Digest.signMD5(sb_source.toString(), "UTF-8");
				try {
					
					// 验证签名一致再处理
					if (org.apache.commons.lang.StringUtils.isNotBlank(qid)
							&& sign != null && sign.equals(md5Value)) {

						try {
							qname = URLDecoder.decode(qname, "UTF-8");
							qmail = URLDecoder.decode(qmail, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						// 当前登录用户
						User currentUser = SingletonLoginUtils
								.getMemcacheUser(request);
						weiboService = (WeiboService) BeanUtils.getBean(request,
								"TUAN360CONFIGService");
						if (currentUser == null) {
							// 判断该百度是否有绑定账户 假如有绑定自动设置为登录状态
							Long bindUserid = weiboService
									.getBindingAccessTokenByWeiboId(qid + "",
											ProfileType.TUAN360CONFIG);
							// 百度账号登陆百度用户ID cookie
							Cookie cookieUserid = WebUtils.cookie("TUAN360USERID",
									qid + "", -1);
							response.addCookie(cookieUserid);
							if (bindUserid != 0) {
								User user = userService.findById(bindUserid);
								Map<String, String> weiboNames = weiboDao
										.getWeiboScreenName(user.getId());
								if (weiboNames != null) {
									memCacheService.set("WEIBO_NAMES_"
											+ user.getId(), weiboNames);
								}
								SingletonLoginUtils
										.addSingleton(user, userService, user
												.getId()
												+ "", response, false, request);
							} else {
								// 后台注册百度用户，密码为8位随机小写字母、数字组合
								StringBuilder baiduId = new StringBuilder("360_");

								// 百度用户名不存在，使用baidu_百度用户名创建账号；否则使用baidu_百度用户ID创建账号
								if (org.apache.commons.lang.StringUtils
										.isNotBlank(qname)
										&& !userService.isUserExist(null, "360_"
												+ qname)) {
									baiduId = baiduId.append(qname);
								} else {
									baiduId = baiduId.append(qid);
								}
								String ip=WebUtils.getIpAddr(request);
								try {
									User newUser = userService.addUserEmailRegist(
											baiduId.toString(),
											com.beike.util.StringUtils
													.getRandomString(8)
													.toLowerCase(),ip);
									if (newUser != null) {
										Map<String, String> logMap2 = LogAction
												.getLogMap(request, response);
										logMap2.put("action", "u_snsreg");
										logMap2.put("sns", "TUAN360");
										logMap2.put("uid", newUser.getId() + "");
										LogAction.printLog(logMap2);

										// 打印日志
										Map<String, String> logMap = LogAction
												.getLogMap(request, response);
										logMap.put("action", "UserRegInSite");
										logMap.put("uid", newUser.getId() + "");
										LogAction.printLog(logMap);

										weiboService.addBindingAccess(newUser
												.getId(), accessToken,
												ProfileType.TUAN360CONFIG);

										Map<String, String> weiboNames = weiboDao
												.getWeiboScreenName(newUser.getId());
										if (weiboNames != null) {
											memCacheService.set("WEIBO_NAMES_"
													+ newUser.getId(), weiboNames);
										}
										SingletonLoginUtils.addSingleton(newUser,
												userService, newUser.getId() + "",
												response, false, request);
									}
								} catch (Exception ex) {
									ex.printStackTrace();
									// 判断该百度是否有绑定账户 假如有绑定自动设置为登录状态
									bindUserid = weiboService
											.getBindingAccessTokenByWeiboId(qid
													+ "", ProfileType.TUAN360CONFIG);
									// 百度账号登陆百度用户ID cookie
									cookieUserid = WebUtils.cookie("TUAN360USERID",
											qid, -1);
									response.addCookie(cookieUserid);
									if (bindUserid != 0) {
										User user = userService
												.findById(bindUserid);
										Map<String, String> weiboNames = weiboDao
												.getWeiboScreenName(user.getId());
										if (weiboNames != null) {
											memCacheService.set("WEIBO_NAMES_"
													+ user.getId(), weiboNames);
										}
										SingletonLoginUtils.addSingleton(user,
												userService, user.getId() + "",
												response, false, request);
									}
								}
							}
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			sb.append(DETAIL_URL);
			if(org.apache.commons.lang.StringUtils.isNotBlank(goodId)){
				sb.append("?goodId=");
				sb.append(goodId);
			}
			if(org.apache.commons.lang.StringUtils.isNotBlank(abstractousideID)){
				sb.append("&abacusoutsid=");
				sb.append(abstractousideID);
			}
			try {
				response.sendRedirect(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		
		
		
		//判断abacusoutsid 以api_fee_800 或者api_free_800 开头 即团800过来的页面 才可能有奇偶IP 进入不同页面
		if(!org.apache.commons.lang.StringUtils.isBlank(abstractousideID)&&(abstractousideID.startsWith("api_free_800")||abstractousideID.startsWith("api_fee_800"))){
			//取出IP尾部部分
			String requestIP=WebUtils.getIpAddr(request);
			String ipprefix="";
			//有点没逗号  如 172.164.23.1
			if(requestIP.indexOf(".")!=-1&&requestIP.indexOf(",")==-1){
				ipprefix=requestIP.substring(requestIP.lastIndexOf(".")+1);
			}
			//有点有逗号 如 172.164.23.1,172.164.23.2
			//取前面的ip 尾数
			else if(requestIP.indexOf(".")!=-1&&requestIP.indexOf(",")!=-1){
				ipprefix=requestIP.split(",")[0].substring(requestIP.split(",")[0].lastIndexOf(".")+1);
			}
			if(!org.apache.commons.lang.StringUtils.isBlank(ipprefix)){
				Long prefixNum=0l;
				
				try {
					prefixNum = Long.parseLong(ipprefix);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//ip尾数 为偶数进入详情页 奇数到探索版详情页
				if(prefixNum%2==0){
					sb.append(DETAIL_URL);
					if(!org.apache.commons.lang.StringUtils.isBlank(requestParam)){
						sb.append("?");
						sb.append(requestParam);
					}
				}else{
					sb.append(GOOD_EXPLORE_URL);
					if(!org.apache.commons.lang.StringUtils.isBlank(requestParam)){
						sb.append("?");
						sb.append(requestParam);
					}
				}
				
				try {
					response.sendRedirect(sb.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}
		//非团800过来 直接进入详情页
		sb.append(DETAIL_URL);
		if(!org.apache.commons.lang.StringUtils.isBlank(requestParam)){
			sb.append("?");
			sb.append(requestParam);
		}
		
		try {
			response.sendRedirect(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * 功能:商品详情(探索版)
	 * 
	 * 成功后跳转页面:/jsp/goods/showGoodExplore.jsp
	 * 
	 * 请求参数：
	 * 
	 * goodId：商品id
	 * 
	 * 返回参数：
	 * 
	 * detailUrl：request 范围 包含页面路径
	 * 
	 * 1.goodDetail:Goods对象 request范围 具体属性找到Goods.java 里面有注释(搜索类快捷键ctrl+shift+R)
	 * ${goodDetail.goodsname}
	 * 
	 * 2.recommendedGoods:List里是GoodsForm对象 GoodsForm里属性参照 Goods 类即可 <c:forEach
	 * items="${recommendedGoods}" var="t"> ${t.logo2}
	 * 
	 * 3.MERCHANT_SCORES:商家评价分数 ${MERCHANT_SCORES}
	 * 
	 * 4.MERCHANT_INFO.MerchantForm 对象
	 * 
	 * 1)MERCHANT_INFO.merchantname 商家名称
	 * 
	 * 2)MERCHANT_INFO.sevenrefound 7天是否退款 1显示图 0 不显示
	 * 
	 * 3)MERCHANT_INFO.overrefound 过期退款 1显示图 0 不显示
	 * 
	 * 4)MERCHANT_INFO.quality 质量 1显示图 0 不显示
	 * 
	 * 5.count:评价多少次
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/goods/showGoodExplore.do")
	public Object showGoodExplore(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			String goodId = request.getParameter("goodId");
			if (goodId == null || "".equals(goodId)) {
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}
			
			/**
			 * 评价调研需求 start  add by guoqingcun 2012-08-10
			 */
			//1.判断当前访问的id是否是 参与活动的ID
//			boolean isActiveGoods = false;
//			boolean isActiveGoodsExtend=false;
			
			long gsid = Long.parseLong(goodId);
//			if(goodsIdList.contains(gsid)){
//				isActiveGoods = true;
//			}
			
//			if(goodsListExtend.contains(gsid)){
//				isActiveGoodsExtend=true;
//			}

			
			
			Integer evaluateCount = null;
			String promptMsg = null;
			//2.如果是  查询数据库 看当前商品 有几个有效评价
//			if(isActiveGoodsExtend){
//				evaluateCount = commentService.getEvaluateGoodCount(gsid);
//				if(isActiveGoods){
//					if(evaluateCount==0){
//						promptMsg = "新品上线抢先体验，1条评价=“全额免单”，先到先得！！";
//					}else if(evaluateCount==1){
//						promptMsg = "新品上线抢先体验，1条评价=“半价免单”，先到先得！！";
//					}else if(evaluateCount==2){
//						promptMsg = "新品上线抢先体验，1条评价=“10%免单”，先到先得！！";
//					}
//				}
//				if(isActiveGoodsExtend){
//					if(evaluateCount==0){
//						promptMsg = "买商品写评价，第1位评价用户得20元，先到先得！！";
//					}else if(evaluateCount==1){
//						promptMsg = "买商品写评价，第2位评价用户得10元，先到先得！！";
//					}else if(evaluateCount==2){
//						promptMsg = "买商品写评价，第3位评价用户得5元，先到先得！！";
//					}else if(evaluateCount>=3){
//						promptMsg="买商品写评价，每位评价用户得1元，机会有限，立即抢购！！";
//					}
//				}
				
//			}
			request.setAttribute("promptMsg", promptMsg);
			/**
			 * 评价调研需 end
			 */
			

			// 百度推广链接参数写入cookie add by qiaowb 2011-10-18
			String referer = request.getHeader("referer");
			String tn = request.getParameter("tn");
			String baiduid = request.getParameter("baiduid");
			if (tn != null && !"".equals(tn) && "baidutuan_tg".equals(tn)
					&& baiduid != null && !"".equals(baiduid)) {
				Cookie cookie = WebUtils.cookie("BAIDU_REFERER_PARAM", tn + "|"
						+ baiduid + "|" + goodId, -1);
				response.addCookie(cookie);
				// 百度推广
				request.setAttribute("baidu_access", "Y");
			}

			GoodsCatlog goodsCatlog = null;
			Goods goods = null;
			List<GoodKindly> kindlist = null;
			try {
				goods    = goodsService.findById(Long.parseLong(goodId));
				kindlist = goodsService.getGoodKindlyById(Long.parseLong(goodId));
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}
			if (goods == null) {
				request.setAttribute("ERRMSG", "没有找到相关商品!");
				return new ModelAndView("redirect:../404.html");
			}
			
//			else{
//				GoodsActivity goodsActivity = goodsActivityService.findActivityByGoodsId(goods.getGoodsId());
//				if(null == goodsActivity){
//					request.setAttribute("activitymsg", "【中秋满月赠返现】全场满100返20千品币，满200返40千品币");
//				}else{
//					request.setAttribute("activitymsg", "此商品不参加【中秋满月赠返现】活动");
//				}
//			}
			String staticurl = propertyUtil.getProperty("STATIC_URL");
			// 汉语转换成拼音
			String city = PinyinUtil.hanziToPinyin(goods.getCity(), "");
			
			//沈阳分公司与卖呆儿网合作的“晒评论送Q币”活动，需将部分商品详情页增加飘红文字
			String showOrderNote = "";
//			if(city.trim().equalsIgnoreCase("shenyang")&&showOrdergoodsList.contains(gsid)){
//				showOrderNote = "【晒评论，送10Q币；登录“卖呆网”晒本单评论得10Q币】";
//			}
			
			
			
			
			// 获取当前城市ID
			Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get(CITY_CATLOG);
			
			if (mapCity == null) {
				mapCity = BeanUtils.getCity(request, "regionCatlogDao");
				memCacheService.set(CITY_CATLOG, mapCity);
			}

			Long cityid = null;
			if (mapCity != null) {
				cityid = mapCity.get(city.trim());
			}
			request.setAttribute("NOWCITY", city);
			response.addCookie(WebUtils.cookie(CityUtils.CITY_COOKIENAME, city,
					CityUtils.validy));
			
			String refer = request.getRequestURL().toString();
			// 商品所在城市访问路径前缀
			String xcity = "http://" + city;
			if (!refer.startsWith(xcity)) {
				if ("true".equals(staticurl)) {
					StringBuilder sb = new StringBuilder();
					sb.append("redirect:http://" + city + ".qianpin.com/goods/");
					sb.append(goodId);
					sb.append(".html");
					String params = WebUtils.parseQueryString(request);
					if (!org.apache.commons.lang.StringUtils.isBlank(params)) {
						sb.append("?");
						sb.append(WebUtils.replaceParams(params, "goodId"));
					}

					return new ModelAndView(sb.toString());
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append("redirect:http://" + city + ".qianpin.com/goods/showGoodExplore.do");
					String params = WebUtils.parseQueryString(request);
					if (!org.apache.commons.lang.StringUtils.isBlank(params)) {
						sb.append("?");
						sb.append(params);
					}
					return new ModelAndView(sb.toString());
				}

			}
			/**
			 * ********将一些商品信息放入memcache，并和uuid配对，在购买页面中取 start add by
			 * wenhua.cheng*******
			 */
			StringBuffer goodsDetailInfo = new StringBuffer();
			// 从memCahe里取商品信息
			try {
				goodsDetailInfo.append(URLEncoder.encode(goods.getGoodsname(),
						"utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}// 商品名字--1
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
			goodsDetailInfo.append(goods.getGuestId()); // guestID--7
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseAbsDate());// 订单过期时间段--8
			goodsDetailInfo.append("&");
			goodsDetailInfo.append(goods.getOrderLoseDate());// 订单过期时间点--9

			String goodsDetailKey = StringUtils.createUUID();
			MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
			memCacheService.set(goodsDetailKey, goodsDetailInfo.toString(),
					18000);
			request.setAttribute("goodsDetailKey", goodsDetailKey);

			/** ********将一些商品信息放入memcache，并和uuid配对，在购买页面中取 end******* */
			goodsCatlog = goodsService
					.searchGoodsRegionById(goods.getGoodsId());
			request.setAttribute("goodDetail", goods);
			request.setAttribute("goodsCatlog", goodsCatlog);
			request.setAttribute("goodKindly", kindlist);

			// 包含页面的url
			String detailUrl = goodsService.getGoodDetailIncliudeUrl(Long
					.parseLong(goodId));

			// 获得商品的实际销售量
			String salescount = goodsService.salesCount(Long.parseLong(goodId));
			if (org.apache.commons.lang.StringUtils.isEmpty(salescount)) {
				salescount = "0";
			}
			/**
			 * 此处是用于显示的商品数量
			 * 
			 * Add by zx.liu
			 */
			int viewSalesCount = goods.getVirtualCount();
			if (null != salescount && salescount.trim().length() > 0) {
				viewSalesCount = Integer.parseInt(salescount)
						+ goods.getVirtualCount();
			}
			// 商品的实际销售量
			// request.setAttribute("SALES_COUNT", salescount);
			// 用于页面显示的商品数量
			request.setAttribute("SALES_COUNT", viewSalesCount);
			request.setAttribute("REAL_SALES_COUNT", salescount);
			request.setAttribute("UPLOAD_IMAGES_URL",
					Constant.UPLOAD_IMAGES_URL);

			// 判断此文件是否存在 假如不存在页面不包含
			String detailFile = request.getRealPath("") + "/jsp/goods_detail/"
					+ detailUrl;
			File file = new File(detailFile);
			if (!file.exists()) {
				detailUrl = null;
			}
			request.setAttribute("detailUrl", detailUrl);
			BigDecimal big = new BigDecimal(goods.getSourcePrice()
					- goods.getCurrentPrice());
			big = big.setScale(1, BigDecimal.ROUND_HALF_UP);
			request.setAttribute("offerPrice", big.floatValue());

			MerchantForm merchantForm = shopsBaoService
					.getMerchantDetailByGoodsId(Long.parseLong(goodId));
			request.setAttribute("merchantForm", merchantForm);
			
			//如果是美食类商品
			if(goodsCatlog.getTagid().equals(10100L)){
				request.setAttribute("isRichFood", true);
				//点菜、外卖--start
				/*
				 * 外卖
				 */
				List<Object> tkouts = (List<Object>)memCacheService.get("TAKEOUT_"+merchantForm.getId());
				if(null==tkouts&&null!=merchantForm){
					tkouts = waiMaiService.getTakeOutByMerId(Long.valueOf(merchantForm.getId()));
					if(null!=tkouts){
						memCacheService.set("TAKEOUT_"+merchantForm.getId(), tkouts, 30*60);
					}
				}
				request.setAttribute("tkouts", tkouts);
	            /*
	             * 点菜
	             */
	            List<Object> orders = (List<Object>)memCacheService.get("ORDER_"+merchantForm.getId());
	            if(null==orders&&null!=merchantForm){
	            	orders = dianCaiService.getOrderByMerId(Long.valueOf(merchantForm.getId()));
	            	if(null!=orders){
	            		memCacheService.set("ORDER_"+merchantForm.getId(), orders, 30*60);
	            	}
	            }
	            request.setAttribute("orders", orders);
	            
				//点菜、外卖--end
			}
			
            
			// 判断用户是否登录
			User user = SingletonLoginUtils.getMemcacheUser(request);
			// 最大可购买数量
			Long canCount = (long) goods.getGoodsSingleCount();
			Long ssCount = goods.getMaxcount() - Long.parseLong(salescount);// 商品上限量与已购买量差
			if (canCount != 0) {
				if (user != null) {
					canCount = payLimitService.allowPayCount(canCount, user
							.getId(), Long.parseLong(goodId),0L);
				}
				if (canCount > ssCount) {
					canCount = ssCount;
				}
			} else {
				canCount = ssCount;
			}

			if (user == null) {
				request.setAttribute("need_login", "Y");
			}
			request.setAttribute("maxBuyCount", canCount);

			// 商圈、属性分类
			Map<String, Map<Long, List<RegionCatlog>>> regionMap = (Map<String, Map<Long, List<RegionCatlog>>>) memCacheService
					.get(REGION_CATLOG);

			Map<Long, Map<Long, List<RegionCatlog>>> propertCatlogMap = (Map<Long, Map<Long, List<RegionCatlog>>>) memCacheService.get(PROPERTY_CATLOG);

			Map<Long, List<RegionCatlog>> property_catlog = null;

			// 假如memcache里没有就从数据库里查
			if (regionMap == null) {
				regionMap = BeanUtils.getCityCatlog(request, "regionCatlogDao");
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
			Map<Long, List<RegionCatlog>> curRegionMap = regionMap.get(city);
			Map<Long, RegionCatlog> regionKeyMap = new HashMap<Long, RegionCatlog>();
			if (curRegionMap != null && !curRegionMap.isEmpty()) {
				for (Long regionKey : curRegionMap.keySet()) {
					List<RegionCatlog> lstRegion = curRegionMap.get(regionKey);
					if (lstRegion != null && lstRegion.size() > 0) {
						for (RegionCatlog region : lstRegion) {
							regionKeyMap.put(region.getCatlogid(), region);
						}
					}
				}
			}
			// 商品分类
			Map<Long, RegionCatlog> tagKeyMap = new HashMap<Long, RegionCatlog>();
			if (property_catlog != null && !property_catlog.isEmpty()) {
				for (Long tagKey : property_catlog.keySet()) {
					List<RegionCatlog> lstTag = property_catlog.get(tagKey);
					if (lstTag != null && lstTag.size() > 0) {
						for (RegionCatlog region : lstTag) {
							tagKeyMap.put(region.getCatlogid(), region);
						}
					}
				}
			}

			// 一级商圈
			String regionName = "";
			List<Map<String, Object>> lstFirstRegion = goodsService
					.getGoodsFirstRegionById(Long.parseLong(goodId));
			if (lstFirstRegion != null && lstFirstRegion.size() == 1) {
				RegionCatlog tmpregion = regionKeyMap.get(lstFirstRegion.get(0)
						.get("regionid"));
				if (tmpregion != null) {
					regionName = tmpregion.getCatlogName();
				}
			}
			request.setAttribute("firstRegionName", regionName);

			// 0220推荐
			int category_id = 0;
			Long goodsRegionid = 0L;
			Long goodsTagid = 0L;
			// 推荐商品
			List<Map<String, Object>> lstRegionIds = goodsService
					.getGoodsRegionIds(Long.parseLong(goodId));
			// 相关分类
			List<Object[]> lstGoodsTag = new LinkedList<Object[]>();
			// 相关商圈
			List<Object[]> lstGoodsRegion = new LinkedList<Object[]>();
			// 用于处理二级地域重复
			int iNextRegionCount = 0;
			String tmpGoodsRegionId = "";
			List<Long> idlist = null;
			List<GoodsForm> lowestForm = null;
			if (lstRegionIds != null && lstRegionIds.size() > 0) {
				Map<String, Object> mapRegionIds = lstRegionIds.get(0);

				Set<String> noRepeatSet = new HashSet<String>();
				for (int i = 0; i < lstRegionIds.size(); i++) {
					Map<String, Object> catlog = lstRegionIds.get(i);
					Long tagId = (Long) catlog.get("tagid");
					Long tagNextId = (Long) catlog.get("tagextid");
					Long regionId = (Long) catlog.get("regionid");
					Long regionextId = (Long) catlog.get("regionextid");
					if (i == 0) {
						category_id = tagId.intValue();
					}
					// 分类
					RegionCatlog tagA = tagKeyMap.get(tagId);
					if (!noRepeatSet.contains(String.valueOf(tagId))) {
						noRepeatSet.add(String.valueOf(tagId));
						if (tagA != null) {
							lstGoodsTag.add(new String[] {
									tagA.getRegion_enname(), "",
									tagA.getCatlogName() });
						}
					}
					if (!noRepeatSet.contains(String.valueOf(String
							.valueOf(tagId)
							+ "|" + tagNextId))) {
						noRepeatSet.add(String.valueOf(String.valueOf(tagId)
								+ "|" + tagNextId));

						RegionCatlog tagB = tagKeyMap.get(tagNextId);

						if (tagA != null && tagB != null) {
							lstGoodsTag.add(new String[] {
									tagA.getRegion_enname(),
									tagB.getRegion_enname(),
									tagB.getCatlogName() });
						}
					}

					// 商圈
					RegionCatlog regionA = regionKeyMap.get(regionId);
					if (!noRepeatSet.contains(String.valueOf(regionId))) {
						noRepeatSet.add(String.valueOf(regionId));
						if (regionA != null) {
							lstGoodsRegion.add(new String[] {
									regionA.getRegion_enname(), "",
									regionA.getCatlogName() });
						}
					}
					if (!noRepeatSet.contains(String.valueOf(String
							.valueOf(regionId)
							+ "|" + regionextId))) {
						noRepeatSet.add(String.valueOf(String.valueOf(regionId)
								+ "|" + regionextId));

						RegionCatlog regionB = regionKeyMap.get(regionextId);
						if (regionA != null && regionB != null) {
							lstGoodsRegion.add(new String[] {
									regionA.getRegion_enname(),
									regionB.getRegion_enname(),
									regionB.getCatlogName() });
						}
					}

					// 二级商圈数超过两个，不使用二级商圈做推荐算法
					if (iNextRegionCount <= 1
							&& tmpGoodsRegionId.indexOf(String
									.valueOf(regionextId)) < 0) {
						if (i == 0) {
							tmpGoodsRegionId = String.valueOf(regionextId);
						} else {
							tmpGoodsRegionId = tmpGoodsRegionId + ","
									+ regionextId;
						}

						iNextRegionCount++;
						if (iNextRegionCount > 2) {
							tmpGoodsRegionId = null;
						}
					}
				}

				request.setAttribute("lstGoodsTag", lstGoodsTag);
				request.setAttribute("lstGoodsRegion", lstGoodsRegion);

				goodsRegionid = (Long) mapRegionIds.get("regionid");
				goodsTagid = (Long) mapRegionIds.get("tagid");
			}
			//运营活动
			
			List<Map<String,Object>> listHuodong=goodsService.getHuodongGoodsId(Long.parseLong(goodId));
			
			if(listHuodong!=null&&listHuodong.size()>0){
					showOrderNote="【晒评论，送10Q币；登录“卖呆网”晒本单评论得10Q币】";
//					 12日活动
//					showOrderNote="11月12日-11月18日【狂欢延续不降温】【全场满100元返10元千品币】 <a class='link_1' target='_blank' href='"+getHuodongUrl(city)+"'>查看详情>> </a>";
			}
//					else{
//				showOrderNote="此商品不参与双11满返活动";
//				 12日活动
//				showOrderNote="此商品不参与满返活动";
//			}
			request.setAttribute("showOrderNote", showOrderNote);
			
			
			//DP页推荐位算法A/B测试
			String flag = request.getParameter("iseven");
			boolean isEven = false;
			if(StringUtils.validNull(flag)){
				isEven = Boolean.parseBoolean(flag);
			}
			if(isEven){
				/**
				 * 商品推荐
				 * 看了该商品的用户也看了（同品类热销），周边人气，网站热销使用新算法（使用缓存，每天算一次）
				 * add by xuxiaoxian 20120919
				 */
				List<Long> sameBrandGoodsid = goodsService.getTopGoodsByMerchantId(
						Long.parseLong(merchantForm.getId()), 2, goodId + "");
				List<Long> temp = new ArrayList<Long>();
				temp.addAll(sameBrandGoodsid);
				temp.add(goods.getGoodsId());
				Map<String,String> goodsMap = new HashMap<String,String>();
				goodsMap.put("goodid", goodId);
				goodsMap.put("currentPrice", goods.getCurrentPrice()+"");
				goodsMap.put("cityId", cityid.toString());
				Map<String,List<Long>> map = goodsService.getCommendGoodsId(goodsMap);
				List<Long> tplrx = map.get("tplrx");
				List<Long> zbrq = map.get("zbrq");
				List<Long> wzrx = map.get("wzrx");
				temp.addAll(tplrx);
				temp.addAll(zbrq);
				temp.addAll(wzrx);
				
				// 低价商品算法修改(使用缓存)
				List<Long> lowpriceGoodsid = goodsService.getPartLowestGoods(
						cityid, 1L, StringUtils.arrayToString(temp.toArray(), ","));
				temp.addAll(lowpriceGoodsid);
				
				//用户刷新页面的时候推荐位商品刷新
				String showLevel = WebUtils.getCookieValue("DP_RECOMMEND_SHOWLEVEL", request);
				int level = 0;
				if(StringUtils.validNull(showLevel)){
					level = Integer.parseInt(showLevel);
					if(level >= 3){
						level = 1;
					}else{
						level = level+1;
					}
				}else{
					level = 1;
				}
				Cookie cookie = WebUtils.cookie("DP_RECOMMEND_SHOWLEVEL", level+"", -1);
				response.addCookie(cookie);
				
				tplrx =  getLevelSubList(tplrx, level);
				zbrq =  getLevelSubList(zbrq, level);
				wzrx = getLevelSubList(wzrx, level);
				
				List<Integer> percentList = new ArrayList<Integer>();
				percentList.add(0,(int)(61+Math.random()*30));
				percentList.add(1,(int)(41+Math.random()*20));
				percentList.add(2,(int)(21+Math.random()*20));
				percentList.add(3,(int)(10+Math.random()*11));
				
				//买过该商品的用户也买了
				request.setAttribute("sameCategoryGoods", 
												goodsService.getGoodsFormByChildId(tplrx));
				request.setAttribute("percentList", percentList);
				//周边人气商品
				request.setAttribute("lstTuijianGoodsForm",
												goodsService.getGoodsFormByChildId(zbrq));
				//网站热销
				request.setAttribute("foodCategoryGoods", goodsService.getGoodsFormByChildId(wzrx));
							
				//同店在售
				request.setAttribute("sameBrandGoods", goodsService
												.getGoodsFormByChildId(sameBrandGoodsid));
				// 低价推荐
				request.setAttribute("lowpriceGoods", goodsService
												.getGoodsFormByChildId(lowpriceGoodsid));
			}else{
				// 0220商品推荐（原推荐算法）
				List<Long> sameBrandGoodsid = goodsService.getTopGoodsByMerchantId(
						Long.parseLong(merchantForm.getId()), 2, goodId + "");
				List<Long> temp = new ArrayList<Long>();
				temp.addAll(sameBrandGoodsid);
				temp.add(goods.getGoodsId());
	
				List<Long> sameCategoryGoodsid = adGoodsService
						.getSameCategoryGoods(Integer.parseInt(cityid.toString()),
								category_id, "1", StringUtils.arrayToString(temp
										.toArray(), ","), 8);
				sameCategoryGoodsid = getRandomGoodsId(sameCategoryGoodsid, 4);
				temp.addAll(sameCategoryGoodsid);
	
				List<Long> foodCategoryGoodsid = adGoodsService
						.getSameCategoryGoods(Integer.parseInt(cityid.toString()),
								category_id, "2", StringUtils.arrayToString(temp
										.toArray(), ","), 8);
				foodCategoryGoodsid = getRandomGoodsId(foodCategoryGoodsid, 4);
				temp.addAll(foodCategoryGoodsid);
				// 低价商品算法修改(使用缓存)
				List<Long> lowpriceGoodsid = goodsService.getPartLowestGoods(
						cityid, 1L, StringUtils.arrayToString(temp.toArray(), ","));
				temp.addAll(lowpriceGoodsid);
	
				// 同店推荐
				request.setAttribute("sameBrandGoods", goodsService
						.getGoodsFormByChildId(sameBrandGoodsid));
				// 同类推荐
				request.setAttribute("sameCategoryGoods", goodsService
						.getGoodsFormByChildId(sameCategoryGoodsid));
				// 美食热销推荐
				request.setAttribute("foodCategoryGoods", goodsService
						.getGoodsFormByChildId(foodCategoryGoodsid));
				// 低价推荐
				request.setAttribute("lowpriceGoods", goodsService
						.getGoodsFormByChildId(lowpriceGoodsid));
	
				// 周边人气，暂时沿用原推荐商品算法
				List<Long> lstTuijian = goodsService.getSaleWithGoodsIds(
						goodsRegionid, tmpGoodsRegionId, goodsTagid, cityid, 20L,
						"");
				log.debug("lstTuijian===" + lstTuijian);
	
				// 剔除其他推荐商品ID，查询推荐商品
				if (lstTuijian != null) {
					if (temp != null && temp.size() > 0) {
						lstTuijian.removeAll(temp);
					}
					if (lstTuijian.size() > 4) {
						lstTuijian = lstTuijian.subList(0, 4);
					}
					List<GoodsForm> lstTuijianGoodsForm = goodsService
							.getGoodsFormByChildId(lstTuijian);
					request
							.setAttribute("lstTuijianGoodsForm",
									lstTuijianGoodsForm);
				}
			}

			// 查询商品数量最多的一级商圈
			List<Long> lstTopRegionId = goodsService.getTopRegionCatlogId(
					cityid, 6L);
			List<RegionCatlog> lstTopRegion = new ArrayList<RegionCatlog>();
			for (Long topRegion : lstTopRegionId) {
				lstTopRegion.add(regionKeyMap.get(topRegion));
			}
			request.setAttribute("lstTopRegion", lstTopRegion);

			try{
				//关键词搜索
				String[] aryRecommendKeywords = luceneRecommendService.getRecommend(goods.getGoodsname(), 20);
				List<Map<String,String>> listRecommendMsg = unionPageService.getListMsgByKeyWords(aryRecommendKeywords,20);
				request.setAttribute("recommandKeyWord", listRecommendMsg);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			// 增加日志2012-01-17
			Map<String, String> mapLog = LogAction.getLogMap(request, response);
			mapLog.put("action", "p_dp");
			mapLog.put("goodid", goodId);
			LogAction.printLog(mapLog);
			
			/*//add by qiaowb 2012-06-07 评价展示
			String score = request.getParameter("score");
			//非0-2之间的数,默认查询所有的评价信息 0很好1满意2差
			int thescore = -1;
			if(score != null && score != ""){
				try{
					thescore = Integer.parseInt(score);
				}catch(NumberFormatException e){
					thescore = -1;
				}
			}
			
			Long userId = 0l;
			if(user != null){
				userId = user.getId();
			}
			Long gid = Long.parseLong(goodId);
			
			// 当前页
			String currentPage = request.getParameter("cpage");
			// 计算分页
			int totalCount = commentService.getEvaluateGoodCount(userId,gid,thescore);	
			if (currentPage == null || "".equals(currentPage)) {
				currentPage = "1";
			}			
						
			Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),totalCount, 5);

			request.setAttribute("pager", pager);
			
			List<OrderEvaluationForm> evaForm = null;
			
			evaForm=(List<OrderEvaluationForm>) memCacheService.get("evaFormlist_"+gid+"_"+currentPage);
			if(evaForm==null||evaForm.size()==0){
				List<Long> normallist = commentService.getEvaluateGoodID(userId, gid, pager,thescore);
				if(normallist != null && normallist.size() >0){
					request.setAttribute("EVAFLAG", "1"); // 有该商品的评价信息
					evaForm = commentService.getEvaluationInfoByIds(normallist);
					memCacheService.set("evaFormlist_"+gid+"_"+currentPage, evaForm);
				}
			}
			request.setAttribute("evaFormlist", evaForm);*/
			
			
			//author wenjie.mai 显示品牌旗舰店 2013.02.02 start***
			Long merid = goodsService.getMerchantIdByGoodId(Long.valueOf(goodId));
			Flagship flagship = null;
			if(merid > 0){
				flagship = flagshipService.getFlagshipByMerchantId(merid);
			}
			request.setAttribute("flagship", flagship);
			//author wenjie.mai 显示品牌旗舰店 2013.02.02 end****
			
			if(isEven){
				return "/goods/showGoodExplore_b";
			}else{
				return "/goods/showGoodExplore";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * DP页新版评价展示规则（很好满意+5%差评）时间逆序排列
	 * add by xuxiaoxian 20130106
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/goods/getEvaluatePageList.do")
	public String getEvaluatePageList(HttpServletRequest request,HttpServletResponse response){
		String currentPage = request.getParameter("cpage");
		String goodsId = request.getParameter("goodsId");
		try{
			if(StringUtils.validNull(goodsId)){
				Long gsid = Long.parseLong(goodsId);
				if (StringUtils.isEmpty(currentPage)) {
					currentPage = "1";
				}
				List<Long> branchIdList = goodsService.getBranchIdByGoodsId(gsid);
				if(branchIdList.size() > 5){
					branchIdList = branchIdList.subList(0, 5);
				}
				List<Long> evalIdList = (List<Long>)memCacheService.get("DP_EVAL_BRANCHID_"+gsid);
				if(null == evalIdList){
					evalIdList = commentService.getEvaluationIdByMerchantId(branchIdList);
					if(evalIdList.size() > 0){
						memCacheService.set("DP_EVAL_BRANCHID_"+gsid, evalIdList, 60*60*24*7);
					}
				}				
				if(evalIdList.size() > 0){
					int totalCount = evalIdList.size();
					Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage), totalCount, 5);
					int startIndex = pager.getStartRow();
					int endIndex = pager.getStartRow()+pager.getPageSize();
					if(endIndex >totalCount){
						endIndex = totalCount;
					} 
					evalIdList = evalIdList.subList(startIndex, endIndex);
					List<OrderEvaluationForm> evaForm = commentService.getEvaluationInfoByIds(evalIdList);
					request.setAttribute("pager", pager);
					request.setAttribute("evaFormlist", evaForm);
					return "/goods/goodsEval";
				}
			}			
		}catch(Exception e){
			e.printStackTrace(); 
			log.info("Loading goods evaluation information failure.............");
		}
		return null;
	}
	/**
	 * 后台审核评价，屏蔽商品评价成功刷新商品缓存
	 * @param request
	 * @param response void
	 * @throws
	 */
	@RequestMapping("/goods/removeEvalMemKey.do")
	public void removeEvalMemKey(HttpServletRequest request,HttpServletResponse response){
		String flag = "success";
		try{
			String memGoodsId = request.getParameter("memGoodsId");
			String hmac = request.getParameter("hmac");
			boolean isPass = MobilePurseSecurityUtils.isPassHmac(hmac,MEMEVAL_KEY,memGoodsId);
			log.info("DP remove evaluation memcache goodsId:"+memGoodsId);
			if(isPass){
				String[] arrGoods = memGoodsId.split(",");
				for(int i = 0;i < arrGoods.length ; i++ ){
					memCacheService.remove("DP_EVAL_BRANCHID_"+arrGoods[i]);
				}
			}
		}catch(Exception e){
			flag = "fail";
			log.info("刷新商品评价的缓存KEY失败、、、");
			e.printStackTrace();
		}
		try {
			response.getWriter().write(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** 
	 * @description:分页显示，不够的时候显示上一页的信息，不够一页隐藏
	 * @param list
	 * @param level
	 * @return List<Long>
	 * @throws 
	 */
	private List<Long> getLevelSubList(List<Long> list,int level){
		int index = level*4;
		if(list.size() < index){
			if(level == 3 && list.size() > 8){
				list = list.subList(4, 8);
			}else if((level == 3 && list.size() > 4) || (level == 2 && list.size() >4)){
				list = list.subList(0, 4);
			}else{
				list = new ArrayList<Long>();
			}
		}else{
			list = list.subList((level-1)*4, index);
		}
		return list;
	}
	
	private static String getHuodongUrl(String city){
		return HUODONG_URL+city+".jsp?abacusinsid=phbn01";
	}
	
	/**
	 * 从商品ID集合中随机取固定数量商品ID
	 * 
	 * @param lstSource
	 * @param iCnt
	 * @return
	 */
	private List<Long> getRandomGoodsId(List<Long> lstSource, int iCnt) {
		if (lstSource != null && lstSource.size() > iCnt) {
			Random random = new Random(); // 随机数类
			StringBuffer bufIndex = new StringBuffer();
			int[] aryIndex = new int[iCnt];
			for (int i = 0; i < iCnt; i++) {
				// 取不重复的索引数
				int curI = random.nextInt(lstSource.size());
				while (bufIndex.indexOf(String.valueOf(curI)) >= 0) {
					curI = random.nextInt(lstSource.size());
				}
				bufIndex.append(curI);
				aryIndex[i] = curI;

			}
			// 排序
			Arrays.sort(aryIndex);
			List<Long> lstResult = new ArrayList<Long>();
			for (int i = 0; i < aryIndex.length; i++) {
				lstResult.add(lstSource.get(aryIndex[i]));
			}
			return lstResult;
		} else {
			return lstSource;
		}
	}
}
