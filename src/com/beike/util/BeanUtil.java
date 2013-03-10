package com.beike.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanUtil {
	/**
	 * 将查询结果（map组成的List）转化成具体的对象列表
	 * 
	 * @param list
	 * @param clzss
	 * @return List
	 * @author ran.li
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> convertResultToObjectList(
			List<Map<String, Object>> list, Class clzss) throws Exception {
		List<Object> objList = new ArrayList<Object>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> result = list.get(i);
				objList.add(convertResultMapToObject(result, clzss));
			}
		}
		return objList;
	}

	/**
	 * 将查询结果元素（map对象）转化为具体对象
	 * 
	 * @param result
	 * @param clzss
	 * @return Object
	 * @author ran.li
	 */
	private static Object convertResultMapToObject(Map<String, Object> result,
			Class<Object> clzss) throws Exception {
		Object obj = clzss.newInstance();
		Method[] methods = clzss.getMethods();

		if (result != null) {
			Set<String> keySet = result.keySet();
			for (String key : keySet) {
				for (Method method : methods) {
					if (method.getName().startsWith("set")
							&& key.replaceAll("_", "").equalsIgnoreCase(
									method.getName().substring(3))) {
						method
								.invoke(obj, typeUtil(method
										.getParameterTypes()[0], result
										.get(key)));
					}
				}

			}
		}
		return obj;

	}

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

