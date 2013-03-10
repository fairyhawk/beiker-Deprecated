package com.beike.action.diaoyudao;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.singletonlogin.SingletonLoginUtils;

@Controller
public class DiaoyudaoAction {
	private final Log log = LogFactory.getLog(DiaoyudaoAction.class);
	private final MemCacheService memCacheService = MemCacheServiceImpl
			.getInstance();
	private final static String MEM_KEY = "diaoyudao";
	private final int times = 600 * 60 * 24;

	@RequestMapping("/diaoyudao/getCount.do")
	public String getCount(HttpServletRequest request,
			HttpServletResponse response) {
		String ip = WebUtils.getIpAddr(request);
		Long count = (Long) memCacheService.get(MEM_KEY);
		if (count == null) {
			count = 0L;
		}
		Long uid = SingletonLoginUtils.getLoginUserid(request);
		if (uid == null) {
			uid = 0L;
		}
		log.info("get count=" + count + ",ip=" + ip + ",uid=" + uid);
		try {
			response.setContentType("text/json; charset=UTF-8");
			response.getOutputStream().write(count.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("/diaoyudao/addCount.do")
	public String addCount(HttpServletRequest request,
			HttpServletResponse response) {
		String ip = WebUtils.getIpAddr(request);
		Long uid = SingletonLoginUtils.getLoginUserid(request);
		if (uid == null) {
			uid = 0L;
		}
		Long count = (Long) memCacheService.get(MEM_KEY);
		if (count == null) {
			count = 0L;
		}
		count = count + 1;
		log.info("add count=" + count + ",ip=" + ip + ",uid=" + uid);
		memCacheService.set(MEM_KEY, count, times);
		try {
			response.setContentType("text/json; charset=UTF-8");
			response.getOutputStream().write(count.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
