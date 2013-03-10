package com.beike.interceptors;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.beike.service.log.BeikeLogService;
import com.beike.util.DateUtils;
import com.beike.util.WebUtils;

/**
 * <p>
 * Title:超级拦截器
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date Apr 27, 2011
 * @author ye.tian
 * @version 1.0
 */

public abstract class BaseBeikeInterceptor extends HandlerInterceptorAdapter {
	private final Log log = LogFactory.getLog(BaseBeikeInterceptor.class);
	private List<String> path;

	private BeikeLogService beikeLogService;

	private Date beginDate;
	private Date endDate;

	private static final ThreadLocal<Map<String, String>> _config = new ThreadLocal<Map<String, String>>();

	public BeikeLogService getBeikeLogService() {
		return beikeLogService;
	}

	public void setBeikeLogService(BeikeLogService beikeLogService) {
		this.beikeLogService = beikeLogService;
	}

	abstract protected Serializable createLog(Map<String, String> map,
			HttpServletRequest request);

	// 报文拼装日志
	protected String paramdata(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		Set<String> set = map.keySet();
		for (String string : set) {
			sb.append(string);
			sb.append(":");
			sb.append(map.get(string));
			sb.append(",");
		}
		String returnString = "";
		if (map != null && map.size() > 0) {
			returnString = sb.substring(0, sb.lastIndexOf(","));
		}

		return returnString;

	}

	// 是否为我们过滤的路径
	private boolean filterPath(HttpServletRequest request) {
		boolean flag = false;
		String requesturi = request.getRequestURI();

		for (String listpath : path) {
			if (requesturi.indexOf(listpath) != -1) {
				flag = true;
				log.debug("拦截器过滤:" + listpath);
				break;
			}
		}

		return flag;
	}

	private void paramLogs(HttpServletRequest request) {
		Map map = request.getParameterMap();
		Set set = map.keySet();
		for (Object object : set) {
			Object value = map.get(object);
			log.debug("参数:" + object.toString());
			String[] values = (String[]) value;
			// log.info(request.getRequestURI()+"-->参数列表:"+object.toString()+":"+value);
			for (String s : values) {
				log.debug("参数值:" + s);
			}
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 判断是否我们过滤的路径
		boolean flag = filterPath(request);
		// 假如需要过滤的
		if (flag) {
			// 打印参数
			paramLogs(request);

			// 访问者IP地址
			String ipAddr = WebUtils.getIpAddr(request);
			// 访问路径
			String reuqestUrl = request.getRequestURI().toString();
			// TODO:需要判断是否需要日志策略，是记录到数据库里，还是正常打日志文件

			// 组装基本日志参数
			addConfig("ipAddr", ipAddr);
			addConfig("reuqestUrl", reuqestUrl);
			// TODO:放置访问参数
			// _config.get().putAll();
			beginDate = new Date();
			addConfig("beginDate", DateUtils.dateToStr(beginDate));
			return true;
		}
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		boolean flag = filterPath(request);
		if (flag) {
			// 方法执行结束时间
			endDate = new Date();
			// action执行相隔时间
			// addConfig("disTime", DateUtils.disTime(beginDate, endDate));
			// 组装额外的参数
		}
		Serializable logObj = createLog(getLogConfig(), request);

		// 相应的日志service去执行记录日志
		if (beikeLogService != null) {
			beikeLogService.processLog(logObj);
		}
		if (ex != null) {
			// 异常信息记录日志
			// log.info(ex);
			// String extmsg=BaseException.getErrorMessage(ex);
			// addConfig("errMsg", extmsg);
			// Serializable logErrObj=createLog(getLogConfig(),request);
			// 相应的日志service去执行记录日志 记录错误信息
			// beikeLogService.processLog(logErrObj);
		}

	}

	private void addConfig(String key, String value) {
		getLogConfig().put(key, value);
	}

	private Map<String, String> getLogConfig() {
		Map<String, String> map = _config.get();
		if (map == null) {
			map = new HashMap<String, String>();
			_config.set(map);
		}
		return map;
	}

	public List<String> getPath() {
		return path;
	}

	public void setPath(List<String> path) {
		this.path = path;
	}

}
