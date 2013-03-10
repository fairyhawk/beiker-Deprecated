package com.beike.util.mobile;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.beike.service.mobile.SearchParam;

/**
 * 输入参数映射
 * @author janwen
 * Mar 28, 2012
 */
public class Map2Object {

	
	/**
	 * 将查询结果元素（map对象）转化为具体对象并做trim处理
	 * 
	 * @param params
	 * @param clzss
	 * @return Object
	 * @author ran.li
	 * 
	 * @FIXME janwen
	 */
	public static Object convertResultMapToObject(Map<String, Object> params,
			Class<SearchParam> clzss) throws Exception {
		Object obj = clzss.newInstance();
		Method[] methods = clzss.getMethods();

		if (params != null) {
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				for (Method method : methods) {
					if (method.getName().startsWith("set")
							&& key.replaceAll("_", "").equalsIgnoreCase(
									method.getName().replaceAll("_", "").substring(3))) {
						method
								.invoke(obj, typeUtil(method
										.getParameterTypes()[0], params
										.get(key).toString().trim()));
					}
				}

			}
		}
		return obj;

	}

	static final Logger logger = Logger.getLogger(Map2Object.class);
	/**
	 * 基本数据类型转换
	 * 
	 * @param type
	 * @param obj
	 * @return Object
	 * @author ran.li
	 */
	@SuppressWarnings("unchecked")
	private static Object typeUtil(Class type, Object obj) {

		if (type.getName().equalsIgnoreCase("int")) {
			if (null == obj) {
				return Integer.parseInt("0");
			}
		}

		if (null == obj) {
			return null;
		}
		String str = obj.toString();
		SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (type == Integer.class || type.getName().equalsIgnoreCase("int")) {
			return Integer.parseInt(str);
		} else if (type == Date.class
				|| type.getName().equalsIgnoreCase("Date")) {
			Date d = null;
			try {
				d = smp.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return d;
		} else if (type == Timestamp.class
				|| type.getName().equalsIgnoreCase("Timestamp")) {
			return Timestamp.valueOf(str);
		} else if (type == BigDecimal.class
				|| type.getName().equalsIgnoreCase("BigDecimal")) {
			return new BigDecimal(str);
		} else if (type == Double.class
				|| type.getName().equalsIgnoreCase("Double")) {
			return Double.parseDouble(str);
		} else if (type == Float.class
				|| type.getName().equalsIgnoreCase("float")) {
			return Float.parseFloat(str);
		} else if (type == Long.class
				|| type.getName().equalsIgnoreCase("long")) {
			return Long.parseLong(str);
		} else if (type == Short.class
				|| type.getName().equalsIgnoreCase("Short")) {
			return Short.parseShort(str);
		} else if (type == Boolean.class
				|| type.getName().equalsIgnoreCase("boolean")) {
			return Boolean.parseBoolean(str);
		}
		return str;
	}
	
	
}
