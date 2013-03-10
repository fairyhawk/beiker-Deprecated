package com.beike.action.adweb;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.common.entity.adweb.AdWeb;
import com.beike.service.adweb.AdWebLogService;
import com.beike.service.adweb.AdWebService;
import com.beike.service.adweb.AdWebTrxInfoService;
import com.beike.service.cps.tuan800.CPSTuan800Service;
import com.beike.util.DateUtils;
import com.beike.util.Digest;
import com.beike.util.PropertiesReader;
import com.beike.util.WebUtils;

/**
 * <p>Title: 广告联盟跳转action</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 29, 2012
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class AdWebAction {
	
	public static final String ADWEB_COOKIE_SRC="ADWEB_COOKIE_SRC";  //src
	
	public static final String ADWEB_COOKIE_CID="ADWEB_COOKIE_CID";  //CID
	
	public static final String ADWEB_COOKIE_WI="ADWEB_COOKIE_WI";	//WI
	@Autowired
	private CPSTuan800Service cpsTuan800Service;
	public static final String ADWEB_COOKIE_TUAN800="cps_tuan800";
	
	public Log log=LogFactory.getLog(AdWebAction.class);
	
	//30天有效期
	public static final int AVALIABLE_COOKIE_DATE= 60 * 60 * 24*30;
	/**
	 * http://www.qianpin.com/adweb/queryAdWebTrxStatus.do?cid=101&d=20120410
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/adweb/queryAdWebTrxStatus.do")
	public String queryAdWebTrxStatus(ModelMap model,HttpServletRequest request,HttpServletResponse response){
		String cid=request.getParameter("cid");
		String d=request.getParameter("d");
		String responseText="";
		if(StringUtils.isBlank(cid)||StringUtils.isBlank(d)){
			try {
				log.info("queryAdWebTrxStatus param is null cid:"+cid+" d:"+d);
				response.getWriter().write(responseText);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		Date date=null;
		try {
			date = DateUtils.parseToDate(d, "yyyyMMdd");
			d=DateUtils.dateToStr(date);
		} catch (ParseException e1) {
			
		}
		
		try {
			responseText = adWebTrxInfoService.generateAdWebTrxInfoList(d, cid);
		} catch (Exception e) {
			responseText="";
			log.info("queryAdWebTrxStatus is error cid:"+cid+" d:"+d);
			e.printStackTrace();
		}
		try {
			response.getWriter().write(responseText);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 查询广告联盟订单接口
	 */
	@RequestMapping("/adweb/queryAdWebTrxInfo.do")
	public String queryAdWebTrxInfo(ModelMap model,HttpServletRequest request,HttpServletResponse response){
		
		String src=request.getParameter("src");
		String cid=request.getParameter("cid");
		String d=request.getParameter("d");
		String responseText="";
		log.info("/adweb/queryAdWebTrxInfo.do param is : src:"+src+" cid:"+cid +" date:"+d);
		if(StringUtils.isEmpty(src)||StringUtils.isEmpty(cid)||StringUtils.isEmpty(d)){
			try {
				log.info("param is null: src:"+src+" cid:"+cid +" date:"+d);
				response.getWriter().write(responseText);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			responseText = adWebTrxInfoService.generateAdWebTrxInfoList(src,
					cid, d);
		} catch (Exception e) {
			responseText="";
			log.info("generate adwebtrxinfo error src:"+src+" cid:"+cid +" date:"+d);
		}
		try {
			response.getWriter().write(responseText);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 广告联盟通过推广链接点进来
	 * http://www.qianpin.com/adweb/unionForward.do?src=yiqifa_qianpin&cid=101&wi=NDgwMDB8dGVzdA==&url=http://www.qianpin.com
	 * http://www.qianpin.com/adweb/unionForward.do?src=tuan800_qianpin&cid=101&wi=NDgwMDB8dGVzdA==&outsrc=union&url=http://www.qianpin.com?abacusoutsid=CPS_tuan800_qianpin_qq&uid=123123123
	 */
	@RequestMapping("/adweb/unionForward.do")
	public String unionForward(ModelMap model, HttpServletRequest request,
			HttpServletResponse response){
		//来源
		String src=request.getParameter("src");
		//广告联盟渠道
		String cid=request.getParameter("cid");
		//下级网站信息
		String wi=request.getParameter("wi");
		//最终跳转的url
		String url=request.getParameter("url");
		log.info("adweb redirect param is : src:"+src+" cid:"+cid+" wi"+wi+" url:"+url);
		if(StringUtils.isNotEmpty(src)){
			//根据来源 查询 广告联盟 是否存在
			AdWeb adWeb=adWebService.getAdWebByCode(src);
			if(adWeb!=null){
				//TODO: 插入广告联盟日志表
//				adWebLogService.addAdWebLog(cid, wi, src);
				
				log.info("======addAdWebLog=====cid:"+cid+"===wi:"+wi+"====src:"+src);
				
				String uid=request.getParameter("uid");
				if(!StringUtils.isBlank(uid)&&"tuan800_qianpin".equals(src)){
					String outsrc=request.getParameter("outsrc");
					if(StringUtils.isBlank(outsrc)){
						outsrc="";
					}
					
//					var cook = _src + '|' + _cid + '|' + _outsrc + "|" + _wi +'|' + _uid;
					StringBuilder sb=new StringBuilder();
					sb.append(src);
					sb.append("|");
					sb.append(cid);
					sb.append("|");
					sb.append(outsrc);
					sb.append("|");
					sb.append(wi);
					sb.append("|");
					sb.append(uid);
					Cookie cookie_800=WebUtils.cookie(ADWEB_COOKIE_TUAN800, sb.toString(), AVALIABLE_COOKIE_DATE);
					response.addCookie(cookie_800);
					
					
					//处理tuan800 cps abacusoutsid参数
					if(url.indexOf("abacusoutsid")!=-1&&"1".equals(cid)){
						StringBuilder sburl=new StringBuilder(url.substring(0,url.indexOf("?")));
						sburl.append("?");
						sburl.append("abacusoutsid=");
						sburl.append("CPS_tuan800_qianpin");
						url=sburl.toString();
					}
				}else{
					Cookie cookie_src=WebUtils.cookie(ADWEB_COOKIE_SRC, src, AVALIABLE_COOKIE_DATE);
					response.addCookie(cookie_src);
					
					Cookie cookie_cid=WebUtils.cookie(ADWEB_COOKIE_CID, cid, AVALIABLE_COOKIE_DATE);
					response.addCookie(cookie_cid);
					
					Cookie cookie_wi=WebUtils.cookie(ADWEB_COOKIE_WI, wi, AVALIABLE_COOKIE_DATE);
					response.addCookie(cookie_wi);
				}	
			}else{
				log.info("add adweb error adWeb is null src:"+src+" cid:"+cid+" wi"+wi+" url:"+url);
			}
		}
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
public static void main(String[] args) {
	String src="tuan800";
	String cid="";
	String d="2012-07-24";
	String ed="2012-07-25";
	
	String md5_value = Digest.signMD5((src + "|" + cid +"|" + d + "|" + ed + "|" +sitekey
			+ "|" + sitesecuritykey),"utf-8");
	System.out.println(md5_value);
}
	
	/**
	 * cpstuan800查询接口
	 * @author janwen
	 * @param request
	 * @param response
	 * @return 分页返回
	 * @throws IOException 
	 */
	@RequestMapping("/queryorder.do")
	public void query4CPSTuan800(HttpServletRequest request,
			HttpServletResponse response){
		String src = request.getParameter("src");
		String cid = request.getParameter("cid");
		String d = request.getParameter("d");
		String ed = request.getParameter("ed");
		String page = request.getParameter("page");
		String md5_value = Digest.signMD5((src + "|" + cid +"|" + d + "|" + ed + "|" +sitekey
				+ "|" + sitesecuritykey),"utf-8");
		if(ed == null || "".equals(ed)){
			//ed =  DateUtils.toString(Calendar.getInstance().getTime(),null);
			ed = d;
		}
		String sign = request.getParameter("sign");
		String regex  = "\\d{4}-\\d{2}-\\d{2}";
		
		AdWeb adWeb=adWebService.getAdWebByCode(src);
		
		boolean isvalid = (adWeb != null  && notNull(d) && d.matches(regex) && notNull(ed) && ed.matches(regex)&&(notNull(sign) && sign.equals(md5_value)));
		log.info("cpstuan800 query request md5=" + md5_value );
		try {
			if(isvalid){
				//log.info("query4CPSTuan800 md5=" + Digest.signMD5(src + "|" + cid +"|" + d + "|" + ed + "|" +sitekey
				//			+ "|" + sitesecuritykey));
				d = d + " 00:00:00";
				ed = ed + " 23:59:59";
				if( "0".equals(cid) || !notNull(cid)){
					cid = null;
				}
				Long count = cpsTuan800Service.getTotalResults(d, ed,cid);
				Long pagesize = getPageSize(count);
				//分页查询参数
				if((notNull(page) && page.matches("\\d+"))){
					if(pagesize >= new Long(page)){
						StringBuilder str = new StringBuilder();;
						 str.append(cpsTuan800Service.getOrder4Tuan800(new Long(page).intValue()-1*pagesize, d, ed,cid));
						response.setCharacterEncoding("utf-8");
						String return_string = str.toString();
						log.info("query4CPSTuan800 return via pagination " + return_string);
						response.getWriter().write(return_string);
						
					}else{
						response.getWriter().write("cps tuan800 query param invaid by page");
					}
				}else{
					//正常查询
					StringBuilder str = new StringBuilder();;
					for(int i=0;i<pagesize;i++){
						 str.append(cpsTuan800Service.getOrder4Tuan800(i*500L, d, ed,cid));
					}
					response.setCharacterEncoding("utf-8");
					
					String return_string = str.toString();
					log.info("query4CPSTuan800 return  " + return_string);
					response.getWriter().write(return_string);
				}
			}else{
				//参数非法
				log.info("cps tuan800 query param invaid");
				response.getWriter().write("cps tuan800 query param invaid");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("cps tuan800 query exception");
			//response.getWriter().write("cps tuan800 query param invaid");
		}
		
		
	}
	private Long getPageSize(Long count) {
		Long pages = 0L;
		if (count % 1000L == 0) {
			pages = count / 500L;
		} else {
			pages = (count / 500L) + 1L;
		}
		return pages;
	}
	private boolean notNull(String args){
		if(args != null && !"".equals(args.trim())){
			return true;	
		}
		return false;
	}
	
	private static final String sitekey = PropertiesReader.getValue("project", "CPS_TUAN800_SITEKEY");
	
	private static final String sitesecuritykey = PropertiesReader.getValue("project", "CPS_TUAN800_SITESECURITY");
	
	@Autowired
	private AdWebService adWebService;
	
	@Autowired
	private AdWebTrxInfoService adWebTrxInfoService;
	
	@Autowired
	private AdWebLogService adWebLogService;

	public AdWebService getAdWebService() {
		return adWebService;
	}


	public void setAdWebService(AdWebService adWebService) {
		this.adWebService = adWebService;
	}


	public AdWebTrxInfoService getAdWebTrxInfoService() {
		return adWebTrxInfoService;
	}


	public void setAdWebTrxInfoService(AdWebTrxInfoService adWebTrxInfoService) {
		this.adWebTrxInfoService = adWebTrxInfoService;
	}
	
}
