package com.beike.action.adweb;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.LogAction;
import com.beike.common.entity.adweb.AdWeb;
import com.beike.common.enums.user.ProfileType;
import com.beike.dao.WeiboDao;
import com.beike.dao.catlog.RegionCatlogDao;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.user.User;
import com.beike.service.adweb.AdWebService;
import com.beike.service.common.WeiboService;
import com.beike.service.cps.tuan360.CPSTuan360Service;
import com.beike.service.user.UserService;
import com.beike.util.BeanUtils;
import com.beike.util.DateUtils;
import com.beike.util.Digest;
import com.beike.util.HttpClientUtil;
import com.beike.util.PinyinUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.ThirdPartConstant;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.hao3604j.Tuan360Model;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**      
 * project:beiker  
 * Title:团360 CPS
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Aug 8, 2012 6:38:33 PM     
 * @version 1.0
 */
@Controller
public class Tuan360CpsAction {
	//30天有效期
	public static final int CPS360_COOKIE_DATE= 60 * 60 * 24 * 30;
	
	private final PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	private static final int CPS360_MAXCOUNT=2000;
	private final String date_time_regex  = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
	private static Log log = LogFactory.getLog(Tuan360CpsAction.class);
	
	@Resource(name = "userService")
	private UserService userService;
	
	private WeiboService weiboService;
	@Autowired
	private WeiboDao weiboDao;
	@Autowired
	private AdWebService adWebService;
	@Autowired
	private CPSTuan360Service cpsTuan360Service;
	@Resource(name="propertyCatlogDao")
	private RegionCatlogDao propertyCatlogDao;
	
	/**
	 * 访问跳转接口
	 * @param request
	 * @param response
	 */
	@RequestMapping("/360cps/cpsForward.do")
	public void queryFromTuan360CPS(HttpServletRequest request,
			HttpServletResponse response){
        String bid = StringUtils.trimToEmpty(request.getParameter("bid"));
        String qihoo_id = StringUtils.trimToEmpty(request.getParameter("qihoo_id"));
        String url = StringUtils.trimToEmpty(request.getParameter("url"));
        String from_url = StringUtils.trimToEmpty(request.getParameter("from_url"));
        String active_time = request.getParameter("active_time"); //请求时间的时间戳
        String ext = request.getParameter("ext");
        String qid = StringUtils.trimToEmpty(request.getParameter("qid"));
        String qmail = StringUtils.trimToEmpty(request.getParameter("qmail"));
        String qname = StringUtils.trimToEmpty(request.getParameter("qname"));
        String sign = request.getParameter("sign");
        
        log.info("360cps redirect param is : bid:"+bid+" qihoo_id:"+qihoo_id+" ext:"+ext+" url:"+url + " active_time:" + active_time);
        
        //bid ext不能为空
		if(StringUtils.isNotEmpty(ext) && StringUtils.isNotEmpty(bid)){
			//根据来源 查询 广告联盟 是否存在
			AdWeb adWeb= adWebService.getAdWebByCode(bid);
			if(adWeb!=null){
				String ourbid = propertyUtil.getProperty("CPS_TUAN360_BID");
				
				StringBuffer bufSignSource = new StringBuffer();
		        bufSignSource.append(ourbid).append("#");
		        bufSignSource.append(active_time).append("#");
		        bufSignSource.append(propertyUtil.getProperty("CPS_TUAN360_CP_KEY")).append("#");
		        bufSignSource.append(qid).append("#");
		        bufSignSource.append(qmail).append("#");
		        bufSignSource.append(qname);
		        
		        StringBuilder bufCookie = new StringBuilder("");
		        bufCookie.append(qihoo_id).append("-").append(ext);
		        Cookie cookie_360 = WebUtils.cookie(ThirdPartConstant.CPS_COOKIE_PARAM, bufCookie.toString(), CPS360_COOKIE_DATE);
				response.addCookie(cookie_360);
				
		        /**
		         * 签名验证失败或检查超时进行以下处理
					1.不进行自动登录。
					2.在服务端向该地址发送一个请求，具体参数见下。
					3.依然写入相应cookie信息，并跳转到url参数指定页面，让用户能正常访问页面。
		         */
		        String md5Value = Digest.signMD5(bufSignSource.toString(), "UTF-8");
		        Long curTime = System.currentTimeMillis();
		        
		        //验签
		        if(sign.equals(md5Value) && checkActiveTime(active_time,curTime)){
		        	//自动登录
		        	autoLogin(request,response,qid,qmail,qname);
		        }else{
		        	String from_ip = WebUtils.getIpAddr(request);
		        	//发送请求至360
		        	NameValuePair[] params = new NameValuePair[]{
		        		new NameValuePair("bid",ourbid),new NameValuePair("active_time",String.valueOf(curTime)),
		        		new NameValuePair("sign",md5Value),new NameValuePair("pre_bid",bid),
		        		new NameValuePair("pre_sign",sign),new NameValuePair("qid",qid),
		        		new NameValuePair("qname",qname),new NameValuePair("qmail",qmail),
		        		new NameValuePair("from_url",from_url),new NameValuePair("from_ip",from_ip)
		        	};
		        	sendFailedTo360Cps(params);
		        }
		        
		        //跳转URL
		        if(StringUtils.isEmpty(url)){
		        	String city = CityUtils.getCity(request, response);
		        	url = "http://"+city+".qianpin.com";
		        }
		        if(url.indexOf("abacusoutsid")==-1){
					StringBuilder sburl = new StringBuilder(url);
					if(url.indexOf("?")>=0){
						sburl.append("&");
					}else{
						sburl.append("?");
					}
					sburl.append("abacusoutsid=");
					sburl.append("api_free_360_");
					url=sburl.toString();
				}
		        try {
					response.sendRedirect(url);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				//参数非法
				log.info("cps tuan360 invaid bid");
				try {
					response.sendRedirect("http://www.qianpin.com");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			//参数非法
			log.info("cps tuan360 query param invaid");
			try {
				response.sendRedirect("http://www.qianpin.com");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 订单查询接口
	 * @param request
	 * @param response
	 */
	@RequestMapping("/360cps/query360CPSOrder.do")
	public void query360CPSOrder(HttpServletRequest request,
			HttpServletResponse response){
		try{
			String bid = StringUtils.trimToEmpty(request.getParameter("bid"));
			String active_time = request.getParameter("active_time");
			String sign = request.getParameter("sign");
			String order_ids = StringUtils.trimToEmpty(request.getParameter("order_ids"));
			String start_time = StringUtils.trimToEmpty(request.getParameter("start_time"));
			String end_time = StringUtils.trimToEmpty(request.getParameter("end_time"));
			String last_order_id = StringUtils.trimToEmpty(request.getParameter("last_order_id"));
			String updstart_time = StringUtils.trimToEmpty(request.getParameter("updstart_time"));
			String updend_time = StringUtils.trimToEmpty(request.getParameter("updend_time"));
			
			log.info("360cps query360CPSOrder param is : bid:"+bid+" sign:"+sign+" order_ids:"+order_ids+" start_time:"+start_time + " end_time:" + end_time
					+" last_order_id:"+last_order_id+" updstart_time:"+updstart_time+" updend_time:"+updend_time);
			
			Long lastOrderId = 0l;
			try{
				lastOrderId = Long.parseLong(last_order_id);
			}catch(Exception ex){
				lastOrderId = 0l;
			}
			response.setCharacterEncoding("utf-8");
			
			String ourbid = propertyUtil.getProperty("CPS_TUAN360_BID");
			Long curTime = System.currentTimeMillis();
			if(!checkActiveTime(active_time,curTime)){
				response.getWriter().write("检查超时.active_time="+curTime);
			}else{
				StringBuffer bufSignSource = new StringBuffer();
		        bufSignSource.append(ourbid).append("#");
		        bufSignSource.append(active_time).append("#");
		        bufSignSource.append(propertyUtil.getProperty("CPS_TUAN360_CP_KEY")).append("");
		        String md5Value = Digest.signMD5(bufSignSource.toString(), "UTF-8");
				if(!sign.equals(md5Value)){
					response.getWriter().write("签名验证失败");
				}else{
					List<Map<String,Object>> lstCpsOrders = null;
					if(StringUtils.isNotEmpty(order_ids)){
						//按照订单号查询
						String[] aryOrderIds = StringUtils.split(order_ids,",");
						if(aryOrderIds!=null){
							List<Long> lstOrderIds = new ArrayList<Long>();
							for(String orderid : aryOrderIds){
								try{
									lstOrderIds.add(Long.parseLong(orderid));
								}catch(Exception ex){
								}
							}
							lstCpsOrders = cpsTuan360Service.queryOrdersByOrderId(lstOrderIds, CPS360_MAXCOUNT);
						}
					}else if(StringUtils.isNotEmpty(start_time) && StringUtils.isNotEmpty(end_time)
							&& start_time.matches(date_time_regex) && end_time.matches(date_time_regex)){
						//按照下单时间查询
						lstCpsOrders = cpsTuan360Service.queryOrdersByCreateTime(start_time, end_time, lastOrderId, CPS360_MAXCOUNT);
					}else if(StringUtils.isNotEmpty(updstart_time) && StringUtils.isNotEmpty(updend_time)
							&& updstart_time.matches(date_time_regex) && updend_time.matches(date_time_regex)){
						//按照订单最后更新时间查询
						lstCpsOrders = cpsTuan360Service.queryOrdersByUpdTime(updstart_time, updend_time, lastOrderId, CPS360_MAXCOUNT);
					}
					response.getWriter().write(createOrderXML(lstCpsOrders,ourbid,0));
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 对账查询接口
	 * @param request
	 * @param response
	 */
	@RequestMapping("/360cps/query360CPSBill.do")
	public void query360CPSBill(HttpServletRequest request,
			HttpServletResponse response){
		try{
			String bid = StringUtils.trimToEmpty(request.getParameter("bid"));
			String active_time = request.getParameter("active_time");
			String sign = request.getParameter("sign");
			String bill_month = StringUtils.trimToEmpty(request.getParameter("bill_month"));
			String last_order_id = StringUtils.trimToEmpty(request.getParameter("last_order_id"));
			
			log.info("360cps query360CPSOrder param is : bid:"+bid+" sign:"+sign+" bill_month:"+bill_month+" last_order_id:"+last_order_id);
			
			Long lastOrderId = 0l;
			try{
				lastOrderId = Long.parseLong(last_order_id);
			}catch(Exception ex){
				lastOrderId = 0l;
			}
			response.setCharacterEncoding("utf-8");
			
			String ourbid = propertyUtil.getProperty("CPS_TUAN360_BID");
			Long curTime = System.currentTimeMillis();
			if(!checkActiveTime(active_time,curTime)){
				response.getWriter().write("检查超时.active_time="+curTime);
			}else{
				StringBuffer bufSignSource = new StringBuffer();
		        bufSignSource.append(ourbid).append("#");
		        bufSignSource.append(active_time).append("#");
		        bufSignSource.append(propertyUtil.getProperty("CPS_TUAN360_CP_KEY")).append("");
		        String md5Value = Digest.signMD5(bufSignSource.toString(), "UTF-8");
				if(!sign.equals(md5Value)){
					response.getWriter().write("签名验证失败");
				}else{
					List<Map<String,Object>> lstCpsOrders = null;
					if(StringUtils.isNotEmpty(bill_month) && bill_month.matches("\\d{4}-\\d{2}")){
						lstCpsOrders = cpsTuan360Service.queryOrdersByBillMonth(bill_month, lastOrderId, CPS360_MAXCOUNT);
					}
					response.getWriter().write(createOrderXML(lstCpsOrders,ourbid,1));
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 发起时间参数active_time，格式为时间戳，精确到秒，超出15分钟的，为无效超时调用，需要忽略该请求
	 * @param activeTime
	 * @param curTime
	 * @return
	 */
	private boolean checkActiveTime(String activeTime, Long curTime){
		boolean retValue = false;
		Long acTime = Long.parseLong(activeTime);
		if(acTime>=(curTime/1000-900)){
			retValue = true;
		}
		return retValue;
	}
	
	
	/**
	 * 360账号自动登录
	 * @param request
	 * @param response
	 * @param qid
	 * @param qname
	 * @param qmail
	 */
	private void autoLogin(HttpServletRequest request, HttpServletResponse response, 
			String qid, String qname, String qmail){
		try {
			if(StringUtils.isEmpty(qid)){
				return;
			}
			// 当前登录用户
			User currentUser = SingletonLoginUtils.getMemcacheUser(request);
			weiboService = (WeiboService) BeanUtils.getBean(request,"TUAN360CONFIGService");
			if (currentUser == null) {
				// 判断该360账号是否有绑定账户 假如有绑定自动设置为登录状态
				Long bindUserid = weiboService.getBindingAccessTokenByWeiboId(qid + "",ProfileType.TUAN360CONFIG);
				// 360账号登陆360用户ID cookie
				Cookie cookieUserid = WebUtils.cookie("TUAN360USERID",qid + "", -1);
				response.addCookie(cookieUserid);
				if (bindUserid != 0) {
					User user = userService.findById(bindUserid);
					Map<String, String> weiboNames = weiboDao.getWeiboScreenName(user.getId());
					if (weiboNames != null) {memCacheService.set("WEIBO_NAMES_" + user.getId(), weiboNames);
					}
					SingletonLoginUtils.addSingleton(user, userService, 
							user.getId()+ "", response, false, request);
				} else {
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
					
					// 后台注册百度用户，密码为8位随机小写字母、数字组合
					StringBuilder baiduId = new StringBuilder("360_");

					// 百度用户名不存在，使用baidu_百度用户名创建账号；否则使用baidu_百度用户ID创建账号
					if (org.apache.commons.lang.StringUtils.isNotBlank(qname)
							&& !userService.isUserExist(null, "360_"+qname)) {
						baiduId = baiduId.append(qname);
					} else {
						baiduId = baiduId.append(qid);
					}
					String ip=WebUtils.getIpAddr(request);
					try {
						User newUser = userService.addUserEmailRegist(baiduId.toString(),
								com.beike.util.StringUtils.getRandomString(8).toLowerCase(),ip);
						if (newUser != null) {
							Map<String, String> logMap2 = LogAction
									.getLogMap(request, response);
							logMap2.put("action", "u_snsreg");
							logMap2.put("sns", "TUAN360");
							logMap2.put("uid", newUser.getId() + "");
							LogAction.printLog(logMap2);

							// 打印日志
							Map<String, String> logMap = LogAction.getLogMap(request, response);
							logMap.put("action", "UserRegInSite");
							logMap.put("uid", newUser.getId() + "");
							LogAction.printLog(logMap);

							weiboService.addBindingAccess(newUser.getId(), accessToken,
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
						// 判断该360账号是否有绑定账户 假如有绑定自动设置为登录状态
						bindUserid = weiboService.getBindingAccessTokenByWeiboId(qid+ "", ProfileType.TUAN360CONFIG);
						// 360账号登陆360用户ID cookie
						cookieUserid = WebUtils.cookie("TUAN360USERID",qid, -1);
						response.addCookie(cookieUserid);
						if (bindUserid != 0) {
							User user = userService.findById(bindUserid);
							Map<String, String> weiboNames = weiboDao.getWeiboScreenName(user.getId());
							if (weiboNames != null) {memCacheService.set("WEIBO_NAMES_"
										+ user.getId(), weiboNames);
							}
							SingletonLoginUtils.addSingleton(user, userService, user.getId() + "",
									response, false, request);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * 
	 * @param params
	 */
	private void sendFailedTo360Cps(NameValuePair[] params){
		HttpClientUtil.getResponseByPostMethod("http://open.union.360.cn/gofailed",params,"UTF-8");
	}
	
	/**
	 * 
	 * @param lstCpsOrders
	 * @param bid
	 * @param itype 类型：0查询接口格式 1对账格式
	 * @return
	 */
	private String createOrderXML(List<Map<String,Object>> lstCpsOrders, String bid, int itype){
		StringBuilder bufxml = new StringBuilder();
		bufxml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><orders>\n");
		
		if(lstCpsOrders!=null && lstCpsOrders.size()>0){
			Map<Long, RegionCatlog> hsAllProperty = getAllPropertyCatlog();
			for(Map<String,Object> tmpOrder : lstCpsOrders){
				//商品数量
				BigDecimal bgCount = new BigDecimal(1);
				BigDecimal bgGoodsPrice = (BigDecimal)tmpOrder.get("pay_price");
				BigDecimal bgCoupon = (BigDecimal)tmpOrder.get("coupon");
				//分成比例
				Float fDivideRate = (Float)tmpOrder.get("dividerate");
				//商品ID
				Long goodsId = (Long)tmpOrder.get("goods_id");
				//城市拼音
				String cityPY = PinyinUtil.hanziToPinyin((String)tmpOrder.get("city"),"");
				if(org.apache.commons.lang.StringUtils.isEmpty(cityPY)){
					cityPY = "www";
				}
				//总佣金
				BigDecimal total_comm = bgGoodsPrice.multiply(bgCount)
						.multiply(new BigDecimal(fDivideRate)).setScale(2,RoundingMode.HALF_UP);
				//佣金明细:商品分类id,分成比例,分成金额,商品单价,数量
				StringBuilder bufcommission = new StringBuilder();
				Long lTagId = (Long)tmpOrder.get("tagid");
				if(lTagId==null){
					lTagId = 0l;
				}
				bufcommission.append(lTagId).append(",").append((int)(fDivideRate*100)).append("%,")
					.append(total_comm).append(",").append(bgGoodsPrice).append(",").append(bgCount)
					.append("|").append(bgCoupon);

				bufxml.append("<order>\n");
				bufxml.append("<bid>").append(bid).append("</bid>\n");
				bufxml.append("<qid>").append((String)tmpOrder.get("qid")).append("</qid>\n");
				bufxml.append("<qihoo_id>").append((String)tmpOrder.get("qihoo_id")).append("</qihoo_id>\n");
				bufxml.append("<ext>").append((String)tmpOrder.get("ext")).append("</ext>\n");
				bufxml.append("<order_id>").append((Long)tmpOrder.get("id")).append("</order_id>\n");
				bufxml.append("<order_time>").append(DateUtils.toString((Timestamp)tmpOrder.get("order_time"),"yyyy-MM-dd HH:mm:ss")).append("</order_time>\n");
				bufxml.append("<order_updtime>").append(DateUtils.toString((Timestamp)tmpOrder.get("order_updtime"),"yyyy-MM-dd HH:mm:ss")).append("</order_updtime>\n");
				bufxml.append("<server_price>").append("0.00").append("</server_price>\n");
				bufxml.append("<total_price>").append(bgGoodsPrice.setScale(2)).append("</total_price>\n");
				bufxml.append("<coupon>").append(bgCoupon.setScale(2)).append("</coupon>\n");
				bufxml.append("<total_comm>").append(total_comm).append("</total_comm>\n");
				//商品分类id,分成比例,分成金额,商品单价,数量
				bufxml.append("<commission>").append(bufcommission.toString()).append("</commission>\n");
				
				if(itype == 0){
					//订单商品的详细信息:商品分类id,商品名称,商品编号,商品单价,商品数量,商品一级分类名称_二级分类名称_商品当前分类名称,商品url
					StringBuilder bufp_info = new StringBuilder();
					//商品分类
					RegionCatlog property1 = hsAllProperty.get(lTagId);
					RegionCatlog property2 = hsAllProperty.get((Long)tmpOrder.get("tagextid"));
					String strProperty = "";
					if(property1!=null){
						strProperty = property1.getCatlogName();
					}
					if(property2!=null){
						strProperty = strProperty + "_" + property2.getCatlogName();
					}
					//商品URL
					StringBuilder bufURL = new StringBuilder();
					bufURL.append("http://").append(cityPY).append(".qianpin.com/goods/").append(goodsId).append(".html");
					String goodsURL = bufURL.toString();
					try{
						goodsURL = URLEncoder.encode(bufURL.toString(),"UTF-8");
					}catch(Exception ex){
					}
					
					String myOrderURL = "";
					bufURL = new StringBuilder();
					bufURL.append("http://").append(cityPY).append(".qianpin.com/goods/").append("/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNUSEED");
					try{
						myOrderURL = URLEncoder.encode(bufURL.toString(),"UTF-8");
					}catch(Exception ex){
					}

					bufp_info.append("delivery_address,1,,中国,,,,,,,|order_link,")
						.append(myOrderURL).append("|")
						.append(lTagId).append(",")
						.append(StringUtils.trimToEmpty((String)tmpOrder.get("goodsname")).replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll(",", "，").replaceAll("|", ""))
						.append(",").append(goodsId).append(",")
						.append(bgGoodsPrice).append(",").append(bgCount).append(",")
						.append(strProperty).append(",")
						.append(goodsURL);
					
					bufxml.append("<p_info>").append(bufp_info.toString()).append("</p_info>\n");
					bufxml.append("<status>").append((Integer)tmpOrder.get("order_status")).append("</status>\n");
				}
				
				bufxml.append("</order>\n");
			}
		}
		bufxml.append("</orders>\n");
		log.info(bufxml.toString());
		return bufxml.toString();
	}
	
	/**
	 * 获取商品分类Map
	 * @return
	 */
	private Map<Long,RegionCatlog> getAllPropertyCatlog(){
		Map<Long, RegionCatlog> hsAllProperty = null;
		try{
			hsAllProperty = (Map<Long, RegionCatlog>)memCacheService.get("ALL_PROPERTY_MAP");
		}catch(Exception ex){
		}
		
		if(hsAllProperty==null || hsAllProperty.isEmpty()){
			Map<Long, List<RegionCatlog>> tmpAll = propertyCatlogDao.getAllCatlog();
			if(tmpAll!=null && !tmpAll.isEmpty()){
				hsAllProperty = new HashMap<Long, RegionCatlog>();
				
				List<RegionCatlog> lstFirstRegion = tmpAll.get(0l);
				if(lstFirstRegion!=null && lstFirstRegion.size()>0){
					for(RegionCatlog tmpR : lstFirstRegion){
						if(tmpR!=null){
							hsAllProperty.put(tmpR.getCatlogid(), tmpR);
							List<RegionCatlog> lstSecondRegion = tmpAll.get(tmpR.getCatlogid());
							for(RegionCatlog tmpR2 : lstSecondRegion){
								if(tmpR2!=null){
									hsAllProperty.put(tmpR2.getCatlogid(), tmpR2);
								}
							}
						}
						
					}
				}
				memCacheService.set("ALL_PROPERTY_MAP", hsAllProperty, 60*60*24*30);
			}
		}
		return hsAllProperty;
	}
}