package com.beike.action;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

import com.beike.util.DateUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.json.JSONException;
import com.beike.util.json.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

@Controller
public class LogAction {
	private static final Log log = LogFactory.getLog(LogAction.class);
	public static final String DATE_FORMAT = "yyyyMMddHHmmss";
	public static int cookie_time = Integer.MAX_VALUE;
	public static final String LABEL_OUTSID = "abacusoutsid";
	public static final String LABEL_INSID = "abacusinsid";
	public static final String OPEN_PAGE_COUNT = "OPENPAGE";

	public static final String DETAIL_PAGE = "DETAIL_PAGE";

	public static final MemCacheService mem = MemCacheServiceImpl.getInstance();

	@RequestMapping("/log.do")
	public ModelAndView log(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> map = getLogMap(request, response);
		String action = request.getParameter("log_action");
		if (!StringUtils.isBlank(action)) {
			map.put("page", "");
			map.put("action", action);
			map.put("sid", request.getParameter(LABEL_OUTSID));
		}
		printLog(map);
		return null;
	}

	@RequestMapping("/logclick.do")
	public ModelAndView logclick(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			// 璁块棶鍩庡競
			String city = CityUtils.getCity(request, response);
			if (city == null) {
				city = "";
			}
			map.put("city", city);
			// 褰撳墠鐧诲綍鑰匢D
			Long uid = SingletonLoginUtils.getLoginUserid(request);
			if (uid == null) {
				map.put("uid", "");
			} else {
				map.put("uid", "" + uid);
			}
			// 璁块棶鑰匢P
			String ip = WebUtils.getIpAddr(request);
			if (ip == null) {
				ip = "";
			}
			map.put("ip", ip);

			String gsid = WebUtils.getCookieValue("bi_gsid", request);
			if (gsid == null) {
				gsid = "";
			}
			map.put("gsid", gsid);

			String csid = WebUtils.getCookieValue("bi_csid", request);
			if (csid == null) {
				csid = "";
			}
			map.put("csid", csid);

			String visit = WebUtils.getCookieValue("bi_visit", request);
			if (visit == null) {
				visit = "";
			}
			map.put("visit", visit);
			String pagekey = request.getParameter("pagekey");
			if (pagekey == null) {
				pagekey = "";
			}
			map.put("pagekey", pagekey);
			String pagepara = request.getParameter("pagepara");
			if (pagepara == null) {
				pagepara = "";
			}
			map.put("pagepara", pagepara);
			String url = request.getParameter("url");
			if (url == null) {
				url = "";
			}
			map.put("url", url);
			String aurl = request.getParameter("aurl");
			if (aurl == null) {
				aurl = "";
			}
			try {
				map.put("sid", getSid(url));
			} catch (Exception e) {
				e.printStackTrace();
			}
			String ldpageparam = WebUtils.getCookieValue("bi_ldpageparam",
					request);
			if (ldpageparam == null) {
				ldpageparam = "";
			}
			map.put("ldpageparam", ldpageparam);
			String ldpagekey = WebUtils.getCookieValue("bi_ldpagekey", request);
			if (ldpagekey == null) {
				ldpagekey = "";
			}
			map.put("ldpagekey", ldpagekey);
			map.put("aurl", aurl);
			String pos = request.getParameter("pos");
			if (pos == null) {
				pos = "";
			}
			map.put("pos", pos);
			String logJson = JsonUtil.mapToJson(map);
			log.info("qianpin_web_flow_click_data:" + logJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void printLog(Map<String, String> map) {
		try {
			String logJson = JsonUtil.mapToJson(map);
			log.info("qianpin_web_flow_data:" + logJson);
		} catch (JSONException e) {
			e.printStackTrace();
			log.info("exception,Map to JSON,map=" + map);
		}
	}

	public static Map<String, String> getLogMap(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			// add by ye.tian 璁板綍鐢ㄦ埛杩涘叆鍗冨搧缃戞墦寮�椤甸潰娆℃暟
			String openpage = WebUtils.getCookieValue(OPEN_PAGE_COUNT, request);
			Cookie cookie = null;
			if (openpage == null || "".equals(openpage)) {
				cookie = WebUtils.cookie(OPEN_PAGE_COUNT, "1", -1);
			} else {
				int count = Integer.parseInt(openpage);
				++count;
				cookie = WebUtils.cookie(OPEN_PAGE_COUNT, count + "", -1);
			}
			if (cookie != null) {
				response.addCookie(cookie);
			}

			// 璁块棶鍩庡競
			String city = CityUtils.getCity(request, response);
			if (city == null) {
				city = "";
			}
			map.put("city", city);

			// 褰撳墠鐧诲綍鑰匢D
			Long uid = SingletonLoginUtils.getLoginUserid(request);
			if (uid == null) {
				map.put("uid", "");
			} else {
				map.put("uid", "" + uid);
			}

			// 璁块棶鑰匢P
			String ip = WebUtils.getIpAddr(request);
			if (ip == null) {
				ip = "";
			}
			map.put("ip", ip);

			// 璁＄畻UV鏂瑰紡
			String gsid = WebUtils.getCookieValue("bi_gsid", request);
			String cid = WebUtils.getCookieValue("bi_cid", request);
			if (StringUtils.isBlank(cid)) {
				cid = UUID.randomUUID().toString() + "__"
						+ DateUtils.formatDate(new Date(), DATE_FORMAT);
				response.addCookie(WebUtils.cookie("bi_cid", cid, cookie_time));
			}
			map.put("cid", cid);
			if (!isRightGsid(gsid)) {
				gsid = UUID.randomUUID().toString() + "__"
						+ DateUtils.formatDate(new Date(), DATE_FORMAT);
			}
			if (gsid == null) {
				gsid = "";
			}
			response.addCookie(WebUtils.cookie("bi_gsid", gsid, cookie_time));
			map.put("gsid", gsid);

			// 缁熻娓犻亾
			String url = request.getParameter("url");
			if (url == null) {
				url = "";
			}
			String sid = "";
			String csid = WebUtils.getCookieValue("bi_csid", request);
			String ocsid = WebUtils.getCookieValue("bi_ocsid", request);
			String pathclick = WebUtils.getCookieValue("bi_pathclick", request);
			if (pathclick == null) {
				pathclick = "";
			}
			pathclick = URLDecoder.decode(pathclick);
			String lstclick = "";
			if (!StringUtils.isBlank(url)) {
				// 杩藉姞pathclick
				if (url.contains(LABEL_INSID + "2")) {
					String a = LABEL_INSID + "2=[a-z|0-9|A-Z|_]*";
					Pattern pattern = Pattern.compile(a);
					Matcher matcher = pattern.matcher(url);
					if (matcher.find()) {
						String click = matcher.group();
						click = click.replaceFirst(LABEL_INSID + "2=", "")
								.replace("__", ":");
						pathclick = pathclick + ";" + click;
						if (pathclick.length() > 640) {
							pathclick = pathclick
									.substring(pathclick.length() - 640);
						}
						try {
							response.addCookie(WebUtils.cookie("bi_pathclick",
									URLEncoder.encode(pathclick), 60 * 30));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				if (url.contains(LABEL_INSID)) {
					lstclick = getLSid(url);
					// if (!StringUtils.isBlank(lstclick)) {
					// if (pathclick.split(",").length > 50) {
					// pathclick = pathclick.substring(pathclick
					// .indexOf(",") + 1);
					// }
					// pathclick = pathclick + "," + lstclick;
					// }
				} else if (url.contains(LABEL_OUTSID)) {
					sid = getSid(url);
					if (!StringUtils.isBlank(sid)) {
						csid = sid;
						ocsid = sid;
					}
				}
			}
			// response.addCookie(WebUtils.cookie("bi_pathclick", pathclick,
			// -1));
			map.put("lstclick", lstclick);
			map.put("pathclick", pathclick);
			map.put("page", url);
			if (csid == null) {
				csid = "";
			}
			response.addCookie(WebUtils.cookie("bi_csid", csid, 60 * 30));
			if (ocsid == null) {
				ocsid = "";
			}
			response.addCookie(WebUtils.cookie("bi_ocsid", ocsid, cookie_time));
			map.put("sid", sid);
			map.put("csid", csid);
			map.put("ocsid", ocsid);

			// 涓�娆¤闂�
			String visit = WebUtils.getCookieValue("bi_visit", request);
			if (visit == null) {
				visit = "";
			}
			String ldpagekey = WebUtils.getCookieValue("bi_ldpagekey", request);
			if (ldpagekey == null) {
				ldpagekey = "";
			}
			String ldpageparam = WebUtils.getCookieValue("bi_ldpageparam",
					request);
			if (ldpageparam == null) {
				ldpageparam = "";
			}
			String ldpage = WebUtils.getCookieValue("bi_ldpage", request);
			if (ldpage == null) {
				ldpage = "";
			}
			ldpage = URLDecoder.decode(ldpage);
			String visitB = "0";
			if (StringUtils.isBlank(visit) && !StringUtils.isBlank(url)) {
				visit = UUID.randomUUID().toString() + "__"
						+ DateUtils.formatDate(new Date(), DATE_FORMAT);
				visitB = "1";
				ldpage = url;
				String[] strs = getPageKey(request);
				ldpagekey = strs[0];
				ldpageparam = strs[1];
			}
			// 椤甸潰鏍囧織
			String[] strs = getPageKey(request);
			map.put("pagekey", strs[0]);
			map.put("pageparam", strs[1]);
			map.put("visit", visit);
			map.put("visitB", visitB);
			map.put("action", "");
			map.put("ldpagekey", ldpagekey);
			map.put("ldpageparam", ldpageparam);
			map.put("ldpage", ldpage);
			response.addCookie(WebUtils.cookie("bi_visit", visit, 60 * 30));

			try {
				response.addCookie(WebUtils.cookie("bi_ldpage",
						URLEncoder.encode(ldpage), 60 * 30));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				response.addCookie(WebUtils.cookie("bi_ldpageparam",
						ldpageparam, 60 * 30));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				response.addCookie(WebUtils.cookie("bi_ldpagekey", ldpagekey,
						60 * 30));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// 鍟嗗搧鏇濆厜
			String whereareufrom = request.getParameter("whereareufrom");
			if (StringUtils.isBlank(whereareufrom)) {
				whereareufrom = "";
			} else {
				String str[] = whereareufrom.split(",");
				StringBuffer sb = new StringBuffer("");
				for (int i = 0; i < str.length; i++) {
					if (!StringUtils.isBlank(str[i])) {
						if (sb.indexOf(str[i]) < 0) {
							sb.append(str[i]).append(",");
						}
					}
				}
				whereareufrom = sb.toString();
			}
			map.put("exp", whereareufrom);

			String tReqId = WebUtils.getCookieValue("bi_t_req_id", request);
			if (tReqId == null) {
				tReqId = "";
			}
			map.put("t_req_id", tReqId);
			String tCrtId = WebUtils.getCookieValue("bi_t_crt_id", request);
			if (tCrtId == null) {
				tCrtId = "";
			}
			map.put("t_crt_id", tCrtId);
			String refer = request.getHeader("Referer");
			map.put("refer", refer);

			Cookie c = null;

			String cvalue = WebUtils.getCookieValue(DETAIL_PAGE, request);
			if (cvalue != null && !"".equals(cvalue)) {
				int cv = 0;
				try {
					cv = Integer.parseInt(cvalue);
				} catch (Exception e) {
					e.printStackTrace();
				}
				cv++;
				cookie = WebUtils.cookie(DETAIL_PAGE, cv + "", -1);
				if (cookie != null) {
					response.addCookie(cookie);
					mem.set(gsid + "_open_page", "OPEN");
				}
			} else {
				// add by ye.tian 璁板綍鐢ㄦ埛杩涘叆鍗冨搧缃戞墦寮�椤甸潰娆℃暟
				String str = (String) mem.get(gsid + "_open_page");
				// 鍋囧娌℃湁寮硅繃 寮鸿鍔犲叆cookie 椤甸潰閫氳繃cookie鍊煎垽鏂槸鍚﹀脊绐�
				if (str == null || "".equals(str)) {
					cookie = WebUtils.cookie(DETAIL_PAGE, "1", -1);
					if (cookie != null) {
						response.addCookie(cookie);
						mem.set(gsid + "_open_page", "OPEN");
					}
				} else {
					c = WebUtils.removeableCookie(DETAIL_PAGE);
				}
				if (c != null) {
					response.addCookie(c);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("web log error");
		}
		return map;
	}

	public static boolean isRightGsid(String gsid) {
		if (StringUtils.isBlank(gsid)) {
			return false;
		} else {
			if (!gsid.contains("__")) {
				return false;
			} else {
				String[] gsidTemp = gsid.split("__");
				if (gsidTemp.length != 2) {
					return false;
				} else {
					String dateStr = gsidTemp[1];
					if (!isDate(dateStr, DATE_FORMAT)) {
						return false;
					} else {
						String nowDate = DateUtils.formatDate(new Date(),
								"yyyyMMdd");
						String oldDate = DateUtils.formatDate(
								formatStr(dateStr, DATE_FORMAT), "yyyyMMdd");
						if (nowDate.equals(oldDate)) {
							return true;
						} else {
							return false;
						}
					}
				}
			}
		}
	}

	public static boolean isDate(String str, String format) {
		DateFormat df = new SimpleDateFormat(format);
		df.setLenient(true);
		try {
			df.parse(str);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static Date formatStr(String str, String format) {
		DateFormat df = new SimpleDateFormat(format);
		df.setLenient(true);
		try {
			return df.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getSid(String url) {
		if (url.contains(LABEL_OUTSID)) {
			String a = LABEL_OUTSID + "=[a-z|0-9|A-Z|_]*";
			Pattern pattern = Pattern.compile(a);
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				String sid = matcher.group();
				return sid.replaceFirst(LABEL_OUTSID + "=", "");
			} else {
				return "";
			}

		} else {
			return "";
		}
	}

	public static String getLSid(String url) {
		if (url.contains(LABEL_INSID + "2")) {
			String a = LABEL_INSID + "2=[a-z|0-9|A-Z|_]*";
			Pattern pattern = Pattern.compile(a);
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				String sid = matcher.group();
				sid = sid.replaceFirst(LABEL_INSID + "2=", "");
				if (sid.contains("__")) {
					if (sid.split("__").length >= 1) {
						return sid.split("__")[1];
					} else {
						return "";
					}
				} else {
					return "";
				}
			} else {
				return "";
			}
		} else if (url.contains(LABEL_INSID)) {
			String a = LABEL_INSID + "=[a-z|0-9|A-Z|_]*";
			Pattern pattern = Pattern.compile(a);
			Matcher matcher = pattern.matcher(url);
			if (matcher.find()) {
				String sid = matcher.group();
				return sid.replaceFirst(LABEL_INSID + "=", "");
			} else {
				return "";
			}

		} else {
			return "";
		}
	}

	public static String[] getPageKey(HttpServletRequest request) {
		String[] str = new String[] { "", "" };
		String pagekey = request.getParameter("pagekey");
		if (pagekey == null) {
			pagekey = "";
		}
		String pagepara = request.getParameter("pagepara");
		if (pagepara == null) {
			pagepara = "";
		}
		str[0] = pagekey;
		str[1] = pagepara;
		return str;
	}

	public static void main(String[] args) {
		String a = "%3Blst_1%3Alg0135_24303";
		System.out.println(URLDecoder.decode(a));
	}
}
