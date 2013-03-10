package com.beike.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: LotteryConstant.java
 * @Package com.beike.util
 * @Description: 运营抽奖相关常量
 * @date 04 26, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
public class LotteryConstant {
	
	   
	/**
	 * 满额抽奖: 指定中奖名单显示的个数
	 */
	public static final int FULLLOTTERY_NUM = 7;
	
	/**
	* 满额抽奖: 商品概率
	*/
	public static Map<String, String> lotteryFullMap = new HashMap<String, String>();
	
	/**
	* 满额抽奖: 城市映射
	*/
	public static Map<String, String> lotteryFullCityMap = new HashMap<String, String>();
	
	/**
	* 满额抽奖: 开始时间,结束时间
	*/

	public static String  fullBeginDate = "2012-04-28 12:00:00";
	public static String  fullEndDate = "2012-05-01 23:59:59";

	
	static{
	//满额抽奖:商品指定概率
	lotteryFullMap.put("beijing", "27005:1-30;25318:31-60;26954:61-80;29056:81-90;23986:91-98;24029:99-100;29774:101-101;24235:101-101;29991:101-101");
	lotteryFullMap.put("jinan", "16829:1-15;29580:16-27;29280:28-39;24224:40-51;32202:52-63;16201:64-75;26146:76-87;30195:88-95;16866:96-100");
	lotteryFullMap.put("tianjin", "26002:1-23;20604:24-56;19979:47-69;21953:60-84;27335:75-90;24419:81-95;21163:86-100;29630:101-101;26635:101-101");
	lotteryFullMap.put("wuhan", "28639:1-10;30658:11-20;28985:21-30;25467:31-42;29528:43-52;28634:53-64;31935:65-76;22005:77-88;27226:89-100");
	lotteryFullMap.put("shanghai", "24176:1-10;22348:11-20;20369:21-30;32379:31-42;28633:43-52;31191:53-64;30466:65-76;20065:77-88;31389:89-100");
	lotteryFullMap.put("hangzhou", "30323:1-10;31660:11-25;31047:26-35;31435:36-45;26022:46-55;29153:56-70;29150:71-75;26408:76-85;21864:86-100");
	lotteryFullMap.put("guangzhou", "24456:1-20;21911:21-40;25587:41-55;26944:56-65;28000:66-80;29108:81-100;23200:101-101;26024:101-101;28984:101-101");
	lotteryFullMap.put("nanjing", "31316:1-25;23933:26-45;26835:46-70;31526:71-80;31524:81-85;27006:86-90;26830:91-95;26852:96-100;24164:101-101");
	lotteryFullMap.put("shenzhen", "30631:1-18;28664:19-34;12951:35-44;28665:45-59;31761:60-75;30261:76-92;19769:93-100;30260:101-101;23699:101-101");
	lotteryFullMap.put("changsha", "19692:1-50;19697:51-100;31725:101-101;31723:101-101;29807:101-101;27117:101-101;21071:101-101;30950:101-101;24411:101-101");
	lotteryFullMap.put("zhengzhou", "31443:1-10;29449:11-20;28223:21-30;29225:31-42;28536:43-52;28282:53-64;28222:65-76;31028:77-88;27808:89-100");
	lotteryFullMap.put("nanchang", "31479:1-20;31939:21-40;31875:41-60;31812:61-80;31589:81-100;32002:101-101;31852:101-101;31919:101-101;31782:101-101");
	lotteryFullMap.put("chongqing", "31809:1-30;26283:31-50;30561:51-70;32014:71-80;28287:81-90;28605:91-100;25728:101-101;29127:101-101;32447:101-101");
	lotteryFullMap.put("changchun", "24348:1-20;22923:21-40;24993:41-60;23177:61-70;25368:71-80;29451:81-90;21649:91-100;32558:101-101;31486:101-101");
	lotteryFullMap.put("chengdu", "32248:1-25;26990:26-50;29626:51-100;31404:101-101;32463:101-101;28243:101-101;30778:101-101;27535:101-101;27091:101-101");
	lotteryFullMap.put("shenyang", "26117:1-10;29258:11-20;27385:21-30;28615:31-42;26421:43-52;27648:53-64;27063:65-76;25270:77-88;24959:89-100");
	lotteryFullMap.put("hefei", "31640:1-10;31495:11-20;32418:21-30;27849:31-42;30592:43-52;29282:53-64;29308:65-76;22483:77-88;21222:89-100");
	lotteryFullMap.put("fuzhou", "21566:1-5;21716:6-15;23181:16-25;21103:26-40;23393:41-55;26962:56-70;24066:71-85;23481:86-100;32646:101-101");
	
	lotteryFullCityMap.put("beijing", "北京");
	lotteryFullCityMap.put("jinan", "济南");
	lotteryFullCityMap.put("tianjin", "天津");
	lotteryFullCityMap.put("wuhan", "武汉");
	lotteryFullCityMap.put("shanghai", "上海");
	lotteryFullCityMap.put("hangzhou", "杭州");
	lotteryFullCityMap.put("guangzhou", "广州");
	lotteryFullCityMap.put("nanjing", "南京");
	lotteryFullCityMap.put("shenzhen", "深圳");
	lotteryFullCityMap.put("changsha", "长沙");
	lotteryFullCityMap.put("zhengzhou", "郑州");
	lotteryFullCityMap.put("nanchang", "南昌");
	lotteryFullCityMap.put("chongqing", "重庆");
	lotteryFullCityMap.put("changchun", "长春");
	lotteryFullCityMap.put("chengdu", "成都");
	lotteryFullCityMap.put("shenyang", "沈阳");
	lotteryFullCityMap.put("hefei", "合肥");
	lotteryFullCityMap.put("fuzhou", "福州");

}
	
}
