package com.beike.util.ipparser;

import com.beike.util.json.JSONArray;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;
import com.beike.util.json.JsonUtil;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Jun 28, 2011
 * @author ye.tian
 * @version 1.0
 */

public class TestIp {
	public static void main(String[] args) throws JSONException {
//		String json="{'geos':[{'city':'0010','longitude':'116.39794','latitude':'39.90817','city_name':'\u5317\u4eac','province':32,'prov_name':'\u5317\u4eac','pinyin':'beijing','more':'\u4e2d\u56fd\t\u5317\u4eac\t\u5317\u4eac\t\t\u7535\u4fe1\u901a','ip':'60.194.172.177'}]}"; 
//		
//		JSONObject object=JsonUtil.stringToObject(json);
//		JSONArray jsonArray=(JSONArray) object.get("geos");
//		JSONObject jo=(JSONObject) jsonArray.get(0);
//		String cityname=(String) jo.get("pinyin");
//		System.out.println(cityname);
		
		IPSeeker seeker = IPSeeker.getInstance();
		String ip ="219.137.148.0";//放入要测试的ip地址
        System.out.println(seeker.getCity(ip));
		

	}
}
