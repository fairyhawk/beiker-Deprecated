package com.beike.common.gc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service("GCDaemon")
public class GCDaemon {
	private final Log logger = LogFactory.getLog(GCDaemon.class);

	public void gc() {
		long begin = System.currentTimeMillis();
		Runtime rt = Runtime.getRuntime();
		double beforeTotal = rt.maxMemory() / 1024.0 / 1024.0;
		double beforeFree = rt.freeMemory() / 1024.0 / 1024.0;
		System.gc();
		rt = Runtime.getRuntime();
		double afterTotal = rt.maxMemory() / 1024.0 / 1024.0;
		double afterFree = rt.freeMemory() / 1024.0 / 1024.0;
		long end = System.currentTimeMillis();
		logger.info("full gc:times=" + (end - begin) + ",beforeTotal="
				+ beforeTotal + ",afterTotal=" + afterTotal + ",subTotal="
				+ (beforeTotal - afterTotal) + ",beforeFree=" + beforeFree
				+ ",afterFree=" + afterFree + ",subFree="
				+ (afterFree - beforeFree));
	}
}
