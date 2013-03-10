package com.beike.util.img;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JavaTypeMapper;

public class JsonUtil {

	private static final Log log = LogFactory.getLog(JsonUtil.class);

	private static JsonFactory jf = new JsonFactory();

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getListFromJsonArray(String str) {
		try {
			if (StringUtils.isNotBlank(str)) {
				ArrayList<Map<String, Object>> arrList = (ArrayList<Map<String, Object>>) new JavaTypeMapper()
						.read(jf.createJsonParser(new StringReader(str)));
				return arrList;
			} else {
				log.warn("JacksonUtil.getListsFromJsonArray error| ErrMsg: input string is null ");
				return null;
			}
		} catch (Exception e) {
			log.error(
					"JacksonUtil.getListsFromJsonArray error| ErrMsg: "
							+ e.getMessage(), e);
			log.error("出错的JSON数据" + str);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapFromJsonString(String str) {
		try {
			if (StringUtils.isNotBlank(str)) {
				Map<String, Object> map = (Map<String, Object>) new JavaTypeMapper()
						.read(jf.createJsonParser(new StringReader(str)));
				return map;
			} else {
				log.warn("ErrMsg: input string is null ");
				return null;
			}
		} catch (Exception e) {
			log.error("ErrMsg: " + e.getMessage(), e);
			return null;
		}
	}

	public static String getJsonStringFromList(List<Map<String, Object>> list) {
		try {
			StringWriter sw = new StringWriter();
			JsonGenerator gen = jf.createJsonGenerator(sw);
			new JavaTypeMapper().writeAny(gen, list);
			gen.flush();
			return sw.toString();
		} catch (Exception e) {
			log.error(
					"JacksonUtil.getJsonStringFromMap error| ErrMsg: "
							+ e.getMessage(), e);
			return null;
		}
	}

	public static String getJsonStringFromMap(Map<String, Object> aMap) {
		try {
			StringWriter sw = new StringWriter();
			JsonGenerator gen = jf.createJsonGenerator(sw);
			new JavaTypeMapper().writeAny(gen, aMap);
			gen.flush();
			return sw.toString();
		} catch (Exception e) {
			log.error("ErrMsg: " + e.getMessage(), e);
			return null;
		}
	}

}
