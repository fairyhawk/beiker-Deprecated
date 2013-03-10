package com.beike.util.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;

/**
 * @Title:JsonUtil
 * @Package com.beike.util.json
 * @Description:
 * @author ye.tian
 * @date Dec 13, 2010
 * @version V1.0
 */

public class JsonUtil {
	
	
	
	public static JSONObject stringToObject(String string) throws JSONException {
       return new JSONObject(string);
    }
	
	public static String listToJson(List<Map> list){
		JSONArray jsonArray=new JSONArray();
		for (Map map : list) {
			jsonArray.add(map);
		}
		return jsonArray.toJSONString();
	}
	
	/**
	 * Object 2  Json
	 * @param obj
	 * @return
	 * @throws JSONException
	 */
	public static String mapToJson(Map<String,String> obj) throws JSONException{
		if(obj==null) return "";
		Set<String> set=obj.keySet();
		JSONObject jo=new JSONObject();
		for (String string : set) {
			jo.put(string, obj.get(string));
		}
		return jo.toString();
	}
	public static void main(String[] args) throws JSONException {
		List<Map> list=new ArrayList<Map>();
		for(int i=0;i<5;i++){
			Map<String,String> map=new HashMap<String,String>();
			map.put("user", "123");
			map.put("user2", "456");
			map.put("user3", "789");
			list.add(map);
		}
		String str=listToJson(list);
//		str=str.replace("[\"", "[").replace("\"]", "]");
		System.out.println(str);
		
//		org.json.simple.JSONObject object=new org.json.simple.JSONObject();
//		object.put("111", "abc");
//		object.put("222", "abc");
//		object.put("333", "abc");
//		object.put("444", "abc");
//		
//		org.json.simple.JSONObject object2=new org.json.simple.JSONObject();
//		object2.put("111x", "abc");
//		object2.put("222x", "abc");
//		object2.put("333x", "abc");
//		object2.put("444x", "abc");
//		org.json.simple.JSONObject object3=new org.json.simple.JSONObject();
//		object3.put("111y", "abc");
//		object3.put("222y", "abc");
//		object3.put("333y", "abc");
//		object3.put("444y", "abc");
//		org.json.simple.JSONArray array=new JSONArray();
//		array.add(object);
//		array.add(object2);
//		array.add(object3);
//		System.out.println(array.toJSONString());
	}
}
