package com.beike.util.tencent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import com.beike.util.Constant;
import com.beike.util.PropertyUtil;
import com.beike.util.json.JSONArray;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;
import com.beike.util.json.JsonUtil;
import com.beike.util.tencent.QWeiboType.ResultType;

public class QWeiboMain {
	private static PropertyUtil property=PropertyUtil.getInstance(Constant.PROPERTY_FILE_NAME);
	private static String customKey = property.getProperty(Constant.TENCENT_APP_KEY);
	private static String customSecrect =property.getProperty(Constant.TENCENT_APP_SECRET);
	private static String tokenKey = null;
	private static String tokenSecrect = null;
	private static String verify = null;

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws JSONException {
//		syncRequest();
//		asyncRequest();
		String json="{'data':{'hasnext':1,'info':[{'city_code':'','country_code':'1','fansnum':38,'head':'http://app.qlogo.cn/mbloghead/5e7168963d689faf4710','idolnum':32,'isidol':true,'isvip':0,'location':'未知','name':'idealies','nick':'lxs','openid':'','province_code':'11','tag':null,'tweet':[{'from':'腾讯微博','id':'84039025251566','text':'有一种感情叫做隐身对其可见，有一种厌恶叫做在线对其隐身；有一种放弃叫做取消对其隐身可见，有一种悲剧是你隐身对其可见的人在线对你隐身……','timestamp':1313457475}]},{'city_code':'','country_code':'','fansnum':102,'head':'http://app.qlogo.cn/mbloghead/56a4e2a106e41bdbe642','idolnum':481,'isidol':false,'isvip':0,'location':'','name':'lvhaitgcpowl','nick':'高_代萱','openid':'','province_code':'','tag':null,'tweet':[{'from':'腾讯微博','id':'31079019201020','text':'我爱你，这是我的劫难。 ——《八月未央》要么','timestamp':1302760514}]},{'city_code':'','country_code':'CAN','fansnum':111,'head':'http://app.qlogo.cn/mbloghead/075fbb1c1767f5ffe448','idolnum':636,'isidol':false,'isvip':0,'location':'','name':'xiangqiungrpap','nick':'时间请定格贸','openid':'','province_code':'','tag':null,'tweet':[{'from':'腾讯微博','id':'10083028881139','text':'下课铃是如此的动听。所','timestamp':1303210171}]},{'city_code':'','country_code':'1','fansnum':47,'head':'http://app.qlogo.cn/mbloghead/98611067d122e89f731e','idolnum':113,'isidol':true,'isvip':0,'location':'未知','name':'xguoliang','nick':'LiAng','openid':'','province_code':'11','tag':null,'tweet':[{'from':'腾讯微博','id':'19619124070772','text':'www.tuanyanan.com正式上线了，请各位关注哦。。。','timestamp':1314414598}]},{'city_code':'','country_code':'1','fansnum':56,'head':'','idolnum':34,'isidol':true,'isvip':0,'location':'未知','name':'jinyulin119','nick':'金玉麟','openid':'','province_code':'11','tag':null,'tweet':[{'from':'腾讯微博','id':'50573112286413','text':'银样蜡枪头','timestamp':1315968595}]},{'city_code':'3','country_code':'1','fansnum':54,'head':'http://app.qlogo.cn/mbloghead/36ef163d302aba87f8d4','idolnum':76,'isidol':true,'isvip':0,'location':'未知','name':'fwr593507009','nick':'瑞','openid':'','province_code':'41','tag':[{'id':'77846971042806542','name':'旅游'},{'id':'3116172981967911833','name':'爱睡觉'},{'id':'3274154839212534452','name':'微博控'},{'id':'3428083006598920604','name':'上网'},{'id':'5205210226242765351','name':'成长ING'},{'id':'6187744654253283637','name':'学生'},{'id':'6221094186430830672','name':'自由射手座'},{'id':'9393533470027694381','name':'看电影'},{'id':'12058093787025748977','name':'三分钟热度'},{'id':'13657878042428889146','name':'乐活族'}],'tweet':[{'from':'腾讯微博','id':'1655068635352','text':'【my love现场版的,听了落泪,这个估计很多人都听过,但是再一次听一遍】||冷兔  http://url.cn/026TmQ ','timestamp':1315731211}]},{'city_code':'','country_code':'1','fansnum':221,'head':'http://app.qlogo.cn/mbloghead/61de5b6c617761eaad86','idolnum':365,'isidol':true,'isvip':0,'location':'未知','name':'surainqian','nick':'徐倩','openid':'','province_code':'11','tag':[{'id':'77846971042806542','name':'旅游'},{'id':'3428083006598920604','name':'上网'},{'id':'3582036462939157681','name':'网站运营'},{'id':'4186399696680047243','name':'产品经理'},{'id':'7632343735443733612','name':'听音乐'},{'id':'17492795109765712344','name':'SEO'},{'id':'18440450464762395634','name':'互联网'}],'tweet':[{'from':'腾讯微博','id':'49573103569416','text':'本来一向不都爱游戏的我，面对明星配游戏的宣传似乎也有想玩他们拿游戏的冲动，9月15日佣兵天下游戏正式公测，大家拭目以待吧！','timestamp':1315962484}]},{'city_code':'1','country_code':'1','fansnum':42,'head':'http://app.qlogo.cn/mbloghead/e5aba7d768425c559bb8','idolnum':61,'isidol':false,'isvip':0,'location':'未知','name':'ao13979081571','nick':'敖强林','openid':'','province_code':'36','tag':[{'id':'1627406106471620529','name':'完美主义'},{'id':'3235208608637297230','name':'狂吃不胖'},{'id':'3428083006598920604','name':'上网'},{'id':'3672325937360252338','name':'攒钱ING'},{'id':'8415310038816758834','name':'学习ING'},{'id':'8468239602781125813','name':'数码控'},{'id':'8883392011515948172','name':'IT民工'},{'id':'10623209587486369383','name':'苹果控'},{'id':'12058093787025748977','name':'三分钟热度'},{'id':'17168216440080056340','name':'爱狗'}],'tweet':[{'from':'腾讯微博','id':'71555065672251','text':'@pcpop_it我刚通过标签“苹果控 数码控”收听了你，原来我们是#同道中人#！','timestamp':1315284908}]},{'city_code':'','country_code':'','fansnum':8,'head':'','idolnum':91,'isidol':false,'isvip':0,'location':'','name':'yixiaoerguowuyu','nick':'一笑而过','openid':'','province_code':'','tag':null,'tweet':[{'from':'腾讯微博','id':'77022082329617','text':'@qqfarm微博收听量已经突破500万，农场委员会特送出大礼包奖励官方微博听众，希望玩家共同努力创造新奇迹！','timestamp':1310773753}]}],'timestamp':1315980892},'errcode':0,'msg':'ok','ret':0}";
		 JSONObject object=JsonUtil.stringToObject(json);
		 Object obj=object.get("data");
		 JSONObject dataObj=JsonUtil.stringToObject(obj.toString());
		 JSONArray jsonArray= (JSONArray) dataObj.get("info");
		 for(int i=0;i<jsonArray.length();i++){
			 Object o=jsonArray.get(i);
			 JSONObject jsonStr=JsonUtil.stringToObject(o.toString());
			 String nick=(String) jsonStr.get("name");
			 JSONArray jsonTweet=(JSONArray) jsonStr.get("tweet");
			 
			 for(int j=0;j<jsonTweet.length();j++){
				 Object objTweet= jsonTweet.get(j);
				 JSONObject jsonT=JsonUtil.stringToObject(objTweet.toString());
				 String id=(String) jsonT.get("id");
				 System.out.println(id+":"+nick);
			 }
			 
		 }
		 
	}

	private static void asyncRequest() {
		QWeiboAsyncApi api = new QWeiboAsyncApi();
        Scanner in = new Scanner(System.in);
        
//        System.out.println("GetRequestToken......");
//        if (api.getRequestToken(customKey, customSecrect)) {
//        	System.out.println("ok");
//        }
        
//        System.out.println("Get verification code......");
//        tokenKey = "c017888ad7c649788be3a971d9da090d";
//        tokenSecrect = "7debf1f444a98118a0c2ff84e11204d5";
//        if( !java.awt.Desktop.isDesktopSupported() ) {
//
//            System.err.println( "Desktop is not supported (fatal)" );
//            System.exit( 1 );
//        }
//        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
//        if(desktop == null || !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
//
//            System.err.println( "Desktop doesn't support the browse action (fatal)" );
//            System.exit( 1 );
//        }
//        try {
//			desktop.browse(new URI("http://open.t.qq.com/cgi-bin/authorize?oauth_token=" + tokenKey));
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit( 1 );
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			System.exit( 1 );
//		}
//		
//        System.out.println("Input your verification code：");
//        verify = in.nextLine(); 
//        
//        System.out.println("GetAccessToken......");
//        if (api.getAccessToken(customKey, customSecrect, tokenKey, tokenSecrect, verify)) {
//        	System.out.println("ok");
//        }
        
        customKey = "27d1186230a443d1ac7f514a96376adb";
	    customSecrect = "eff11cc20a1d582b81f1affe3e754889";
	    tokenKey = "3b48537e3e41413d96b649da579e026d";
	    tokenSecrect = "c52f7911a7e97b3e446190c0bf3a5cb6";
//        System.out.println("GetHomeMsg......");
//      	if (api.getHomeMsg(customKey, customSecrect, tokenKey, tokenSecrect, ResultType.ResultType_Json, PageFlag.PageFlag_First, 20)) {
//      		System.out.println("ok");
//      	}
	    
	    if (api.publishMsg(customKey, customSecrect, tokenKey, tokenSecrect, "测试a", "C:\\Users\\sampanweng\\Desktop\\QWeiboSDK\\QWBlogAPISDK_proj\\c#\\QWeiboTest\\head.bmp", ResultType.ResultType_Json)) {
	    	System.out.println("ok");
	    }
        
        System.out.println("exit 0");
        in.close();
	}

//	private static void syncRequest() {
//		QWeiboSyncApi api = new QWeiboSyncApi();
//		String response = null;
//        Scanner in = new Scanner(System.in);
//		
//		System.out.println("GetRequestToken......");
//		response = api.getRequestToken(customKey, customSecrect);
//
//        System.out.println("Response from server：");
//        System.out.println(response);
//        
//        if (!parseToken(response)) {
//        	return;
//        }
//        
//        System.out.println("Get verification code......");
//        if( !java.awt.Desktop.isDesktopSupported() ) {
//
//            System.err.println( "Desktop is not supported (fatal)" );
//            System.exit( 1 );
//        }
//        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
//        if(desktop == null || !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
//
//            System.err.println( "Desktop doesn't support the browse action (fatal)" );
//            System.exit( 1 );
//        }
//        try {
//			desktop.browse(new URI("http://open.t.qq.com/cgi-bin/authorize?oauth_token=" + tokenKey));
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit( 1 );
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			System.exit( 1 );
//		}
//		
//        System.out.println("Input your verification code：");
//        verify = in.nextLine(); 
//        
//        System.out.println("GetAccessToken......");
//        String args[]=response.split("&");
//        if(args!=null&&args.length>0){
//        	tokenKey=args[0].split("=")[1];
//        	tokenSecrect=args[1].split("=")[1];
//        }
//        response = api.getAccessToken(customKey, customSecrect, tokenKey, tokenSecrect, verify);
//        System.out.println("Response from server：");
//        System.out.println(response);
//
//        if (!parseToken(response)){
//        	return;
//        }
//        
//        api.getFansList(200, 0, customKey, customSecrect, tokenKey, tokenSecrect, ResultType.ResultType_Json);
////        System.out.println("Publishing......");
////        response = api.publishMsg(customKey, customSecrect, tokenKey, tokenSecrect, "测试接口", "", ResultType.ResultType_Json);//"head.bmp"
////        api.getUserMsg(customKey, customSecrect, tokenKey, tokenSecrect,ResultType.ResultType_Json);
////        System.out.println("Response from server：");
////        System.out.println(response);
//        in.close();
//	}

	static boolean parseToken(String response) {
		if (response == null || response.equals("")) {
			return false;
		}

		String[] tokenArray = response.split("&");

		if (tokenArray.length < 2) {
			return false;
		}

		String strTokenKey = tokenArray[0];
		String strTokenSecrect = tokenArray[1];

		String[] token1 = strTokenKey.split("=");
		if (token1.length < 2) {
			return false;
		}
		tokenKey = token1[1];

		String[] token2 = strTokenSecrect.split("=");
		if (token2.length < 2) {
			return false;
		}
		tokenSecrect = token2[1];

		return true;
	}
}
