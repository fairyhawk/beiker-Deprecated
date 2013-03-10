package com.beike.util;

import com.beike.common.listener.CatlogListener;

/**
 * <p>Title:前端样式工具类 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Feb 27, 2012
 * @author ye.tian
 * @version 1.0
 */

public class CssUtils {
	
	/**
	 * 获得样式文件后缀
	 * 暂时用app启动时间标示
	 * <%=CssUtils.getStyleStr()%>
	 */
	public static String getStyleStr(){
		StringBuilder sb=new StringBuilder("?");
		sb.append(CatlogListener.APP_START_TIME);
		return sb.toString();
	}

}
