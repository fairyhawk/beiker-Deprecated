package com.beike.util.lucene;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.beike.service.lucene.search.LuceneAlertService;
import com.beike.util.StringUtils;



public class LuceneAlertUtil {

	static final Log logger = LogFactory.getLog(LuceneAlertService.class);
	
	public static String getIP() {
		String localip = null;// 本地IP，如果没有配置外网IP则返回它
		String netip = null;// 外网IP

		String defaultip = "未能获取到异常机器ip";
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface
					.getNetworkInterfaces();
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					 if (ip.isSiteLocalAddress()
							&& !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
						localip = ip.getHostAddress();
						finded = true;
						logger.info("localip=" + localip);
						break;
					}else if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {// 外网IP
						netip = ip.getHostAddress();
						logger.info("netip=" + netip);
					} 
				}
			}
			logger.info("LuceneAlertUtil getIP: localip: " + localip
					+ " netip: " + netip);
			if (StringUtils.validNull(localip)) {
				return localip;
			} else {
				return netip;
			}
		} catch (SocketException e) {
			e.printStackTrace();
			logger.info("LuceneAlertUtil 获取ip地址异常");
		}
		return defaultip;
	}
}
