package com.beike.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.beike.common.exception.BaseException;
import com.beike.common.exception.TrxOrderException;
/**
 * @Title: StringUtils
 * @Description:
 * @author ye.tian
 * @date Apr 25, 2011
 * @version V1.0
 */
public class StringUtils {

	
	
	
	/**
	 * 手机号中奖4位加星号
	 * janwen
	 * @param mobile
	 * @return
	 *
	 */
	public static String starMobile(String mobile){
		if(mobile.length() == 11){
			String	starmobile = 
					String.valueOf(mobile.charAt(0)) +String.valueOf(mobile.charAt(1))
					+String.valueOf(mobile.charAt(2)) + "****" + String.valueOf(mobile.charAt(7))
					+String.valueOf(mobile.charAt(8)) + String.valueOf(mobile.charAt(9))
					+ String.valueOf(mobile.charAt(10));
			return starmobile;
		}
		return mobile;
	}
	
	/**
	 * 生成指定长度的随机字符串
	 * 
	 * @param strLength
	 * @return
	 */
	public static String getRandomString(int strLength) {
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < strLength; i++) {
			int charInt;
			char c;
			if (random.nextBoolean()) {
				charInt = 48 + random.nextInt(10);
				c = (char) charInt;
				buffer.append(c);
				continue;
			}
			charInt = 65;
			if (random.nextBoolean())
				charInt = 65 + random.nextInt(26);
			else
				charInt = 97 + random.nextInt(26);
			if (charInt == 79)
				charInt = 111;
			c = (char) charInt;
			buffer.append(c);
		}

		return buffer.toString();
	}

	/**
	 * MD5加密方法
	 * 
	 * @param str
	 *            String
	 * @return String
	 */
	public static String md5(String str) {
		if (str == null) {
			return null;
		}
		byte newByte1[] = str.getBytes();
		try {
			MessageDigest messagedigest = MessageDigest.getInstance("MD5");
			byte newByte2[] = messagedigest.digest(newByte1);
			String cryptograph = "";
			for (int i = 0; i < newByte2.length; i++) {
				String temp = Integer.toHexString(newByte2[i] & 0x000000ff);
				if (temp.length() < 2)
					temp = "0" + temp;
				cryptograph += temp;
			}
			return cryptograph;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 验证Email地址是否有效
	 * 
	 * @param sEmail
	 * @return
	 */
	public static boolean validEmail(String sEmail) {
		String pattern = "^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		return sEmail.matches(pattern);
	}

	/**
	 * 验证字符�?大长�?
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validMaxLen(String str, int length) {
		if (str == null || str.equals("")) {
			return false;
		}
		if (str.length() > length) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 验证字符�?小长�?
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validMinLen(String str, int length) {
		if (str == null || str.equals("")) {
			return false;
		}
		if (str.length() < length) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 验证字符串是否为空
	 * 
	 * @param str
	 * @return true:不为空
	 */
	public static boolean validNull(String str) {
		if (str == null || str.trim().equals("")) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * janwen
	 * @param str
	 * @return
	 *
	 */
	public static boolean validNull(String... str) {
		for(int i=0;i<str.length;i++){
			if (str[i] == null || str[i].trim().equals("")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证两个字符串是否相等且不能为空
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean equals(String str1, String str2) {
		if (str1 == null || str1.equals("") || str2 == null || str2.equals("")) {
			return false;
		}
		return str1.equals(str2);
	}

	/**
	 * 将字符型转为Int�?
	 * 
	 * @param str
	 * @return
	 */
	public static int toInt(String str) {
		int value = 0;
		if (str == null || str.equals("")) {
			return 0;
		}
		try {
			value = Integer.parseInt(str);
		} catch (Exception ex) {
			ex.printStackTrace();
			value = 0;
			ex.printStackTrace();
		}
		return value;
	}

	/**
	 * 把数组转换成String
	 * 
	 * @param array
	 * @return
	 */
	public static String arrayToString(Object[] array, String split) {
		if (array == null) {
			return "";
		}
		StringBuffer str = new StringBuffer("");
		for (int i = 0; i < array.length; i++) {
			if (i != array.length - 1) {
				str.append(array[i].toString()).append(split);
			} else {
				str.append(array[i].toString());
			}
		}
		return str.toString();
	}

	/**
	 * 得到WEB-INF的绝对路�?
	 * 
	 * @return
	 */
	public static String getWebInfPath() {
		String filePath = Thread.currentThread().getContextClassLoader()
				.getResource("").toString();
		if (filePath.toLowerCase().indexOf("file:") > -1) {
			filePath = filePath.substring(6, filePath.length());
		}
		if (filePath.toLowerCase().indexOf("classes") > -1) {
			filePath = filePath.replaceAll("/classes", "");
		}
		if (System.getProperty("os.name").toLowerCase().indexOf("window") < 0) {
			filePath = "/" + filePath;
		}
		if (!filePath.endsWith("/"))
			filePath += "/";
		return filePath;
	}

	/**
	 * 得到根目录绝对路�?(不包含WEB-INF)
	 * 
	 * @return
	 */
	public static String getRootPath() {
		String filePath = Thread.currentThread().getContextClassLoader()
				.getResource("").toString();
		if (filePath.toLowerCase().indexOf("file:") > -1) {
			filePath = filePath.substring(6, filePath.length());
		}
		if (filePath.toLowerCase().indexOf("classes") > -1) {
			filePath = filePath.replaceAll("/classes", "");
		}
		if (filePath.toLowerCase().indexOf("web-inf") > -1) {
			filePath = filePath.substring(0, filePath.length() - 9);
		}
		if (System.getProperty("os.name").toLowerCase().indexOf("window") < 0) {
			filePath = "/" + filePath;
		}

		if (filePath.endsWith("/"))
			filePath = filePath.substring(0, filePath.length() - 1);

		return filePath;
	}

	public static String getRootPath(String resource) {
		String filePath = Thread.currentThread().getContextClassLoader()
				.getResource(resource).toString();
		if (filePath.toLowerCase().indexOf("file:") > -1) {
			filePath = filePath.substring(6, filePath.length());
		}
		if (filePath.toLowerCase().indexOf("classes") > -1) {
			filePath = filePath.replaceAll("/classes", "");
		}
		if (filePath.toLowerCase().indexOf("web-inf") > -1) {
			filePath = filePath.substring(0, filePath.length() - 9);
		}
		if (System.getProperty("os.name").toLowerCase().indexOf("window") < 0) {
			filePath = "/" + filePath;
		}

		if (!filePath.endsWith("/"))
			filePath += "/";

		return filePath;
	}

	/**
	 * 格式化页�?
	 * 
	 * @param page
	 * @return
	 */
	public static int formatPage(String page) {
		int iPage = 1;
		if (page == null || page.equals("")) {
			return iPage;
		}
		try {
			iPage = Integer.parseInt(page);
		} catch (Exception ex) {
			ex.printStackTrace();
			iPage = 1;
		}
		return iPage;
	}

	/**
	 * 将计量单位字节转换为相应单位
	 * 
	 * @param size
	 * @return
	 */
	public static String getFileSize(String fileSize) {
		String temp = "";
		DecimalFormat df = new DecimalFormat("0.00");
		double dbFileSize = Double.parseDouble(fileSize);
		if (dbFileSize >= 1024) {
			if (dbFileSize >= 1048576) {
				if (dbFileSize >= 1073741824) {
					temp = df.format(dbFileSize / 1024 / 1024 / 1024) + " GB";
				} else {
					temp = df.format(dbFileSize / 1024 / 1024) + " MB";
				}
			} else {
				temp = df.format(dbFileSize / 1024) + " KB";
			}
		} else {
			temp = df.format(dbFileSize / 1024) + " KB";
		}
		return temp;
	}

	/**
	 * 得到�?�?32位随机字�?
	 * 
	 * @return
	 */
	public static String getEntry() {
		Random random = new Random(100);
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(new String(
				"yyyyMMddHHmmssS"));
		return md5(formatter.format(now) + random.nextDouble());
	}

	/**
	 * 将中文汉字转成UTF8编码
	 * 
	 * @param str
	 * @return
	 */
	public static String toUTF8(String str) {
		if (str == null || str.equals("")) {
			return "";
		}
		try {
			return new String(str.getBytes("ISO8859-1"), "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public static String to(String str, String charset) {
		if (str == null || str.equals("")) {
			return "";
		}
		try {
			return new String(str.getBytes("ISO8859-1"), charset);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	public static String getRandStr(int n) {
		Random random = new Random();
		String sRand = "";
		for (int i = 0; i < n; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
		}
		return sRand;
	}

	/**
	 * 得到�?个数字的大写(�?到十之内)
	 * 
	 * @param num
	 * @return
	 */
	public static String getChineseNum(int num) {
		String[] chineseNum = new String[] { "�?", "�?", "�?", "�?", "�?",
				"�?", "�?", "�?", "�?", "�?" };
		return chineseNum[num];
	}

	public static String replaceEnter(String str) {
		if (str == null)
			return null;
		return str.replaceAll("\r", "").replaceAll("\n", "");
	}

	/**
	 * 去除HTML 元素
	 * 
	 * @param element
	 * @return
	 */
	public static String getTxtWithoutHTMLElement(String element) {
		if (null == element) {
			return element;
		}
		Pattern pattern = Pattern.compile("<[^<|^>]*>");
		Matcher matcher = pattern.matcher(element);
		StringBuffer txt = new StringBuffer();
		while (matcher.find()) {
			String group = matcher.group();
			if (group.matches("<[\\s]*>")) {
				matcher.appendReplacement(txt, group);
			} else {
				matcher.appendReplacement(txt, "");
			}
		}
		matcher.appendTail(txt);
		String temp = txt.toString().replaceAll("[\r|\n]", "");
		//多个连续空格替换为一个空格
		temp = temp.replaceAll("\\s+", " ");
		return temp;
	}

	/**
	 * clear trim to String
	 * 
	 * @return
	 */
	public static String toTrim(String strtrim) {
		if (null != strtrim && !strtrim.equals("")) {
			return strtrim.trim();
		}
		return "";
	}

	/**
	 * 交易trxInfoList 内数据判断长度是否一致并重新组装 add by wenhua.cheng
	 * 
	 * @param trxInfoList
	 * @return
	 */
	public static List<String[]> transTrxInfo(List<String> trxInfoList) {
		List<String[]> resultList = new ArrayList<String[]>();
		int firstSize = 0;
		int temp = 0;// 计数器
		for (String item : trxInfoList) {
			String[] stringAarray = StringUtils.toTrim(item).split("\\|");
			int arrayNum = stringAarray.length;
			if (temp == 0) {
				firstSize = arrayNum;
			}
			if (firstSize != arrayNum) {
				throw new IllegalArgumentException(
						"trxInfo array size not equals"+item);
			}
			temp += 1;
			resultList.add(stringAarray);
		}
		return resultList;

	}

	

	/**
	 * 序列号备用随机数
	 * 
	 * @return
	 */
	public static String getSysTimeRandom() {
		return System.currentTimeMillis() + "" + new Random().nextInt(100);

	}

	/**
	 * 商品订单序列号备用随机数--指定位数
	 * 
	 * @return
	 */
	public static String getSysTimeRandom(int count) {

		String resultRandom = System.currentTimeMillis() + ""
				+ new Random().nextInt(100);

		String resultRandomPro = "";
		int resultCount = resultRandom.length();
		if (count >= resultCount) {
			for (int i = 0; i < count - resultCount; i++) {

				resultRandomPro += "0";

			}
			return resultRandomPro + resultRandom;
		} else {

			return resultRandom.substring(resultCount - 1 - count,
					resultCount - 1);
		}

	}

	/**
	 * UUID
	 */
	// public static String createUUID() {
	// UUID uuid = UUID.randomUUID();
	// return uuid.toString().replaceAll("-", "").substring(0, 19);
	// }
	//
	public static String createUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	/**
	 * 参数转换
	 * 
	 * @param source
	 * @return
	 */
	public static String[] parseParam(String source) {

		if (source == null || "".equals(source)) {
			throw new IllegalArgumentException("source is null");
		}
		String[] resultAry = source.split("&");
		return resultAry;
	}

	/**
	 * 参数转换 renli.yu
	 * 
	 * @param source
	 * @return
	 */
	public static String[] parseParamArray(String source) {

		if (source == null || "".equals(source)) {
			throw new IllegalArgumentException("source is null");
		}
		String[] resultAry = source.split("\\|");
		return resultAry;
	}

	public static String convStrToHessian(String item, int count)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(URLEncoder.encode(item, "utf-8")).append("|");
		}
		if (sb != null && sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public static String convToHessian(String item, int count)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(URLEncoder.encode(item, "utf-8")).append("|");
		}

		return sb.toString();
	}

	public static String convAryToStr(String sourceStr, String sourceChar,
			String resultChar, boolean isTrans) {
		if (isTrans) {
			sourceChar = "\\" + sourceChar;
		}
		String[] sourceStrAry = sourceStr.split(sourceChar);

		int count = sourceStrAry.length;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			try {
				Long.parseLong(sourceStrAry[i]); // 如果不为数字，则抛异常

			} catch (Exception e) {
				e.printStackTrace();
			}

			sb.append(sourceStrAry[i]).append(resultChar);
		}
		if (sb != null && sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public static String convListToString(List<Map<String, Object>> list,
			String flag) {
		StringBuilder sb = new StringBuilder();
		int count = list.size();
		for (int i = 0; i < count; i++) {
			sb.append(list.get(i).get(flag));
			sb.append(",");
		}
		if (sb != null && sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public static String queryParam(String param, String queryParam) {
		if (validNull(param)) {
			return queryParam + "=" + param + "&";
		} else {
			return "";
		}
	}

	/**
	 * Description : 处理图片，图片名称加时间戳，以免冲突
	 * 
	 * @param picName
	 * @param picStuff
	 * @return
	 */
	public static String formatPicName(String picName) {
		@SuppressWarnings("unused")
		String picN = picName.substring(0, picName.lastIndexOf("."));
		// 根据ext进行判断是否是允许的类型
		String ext = picName.substring(picName.lastIndexOf("."), picName
				.length());
		StringBuilder pic = new StringBuilder();
		pic.append(new Date().getTime()).append("_").append(IdGen.genId())
				.append(ext.toLowerCase());
		return pic.toString();
	}

	/**
	 * Description : 讲字符串类型转换为java.sql.Timestamp
	 * 
	 * @param time
	 * @return
	 */
	public static Timestamp convertToTimestamp(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		java.util.Date myDate = null;
		Timestamp myTimestamp = null;
		try {
			myDate = sdf.parse(time);
			myTimestamp = new Timestamp(myDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myTimestamp;
	}

	/**
	 * 按传入字数截断并加结尾符号
	 * 
	 * @param sourceStr
	 * @param length
	 * @param charactor
	 * @return
	 */
	public static String cutffStr(String sourceStr, int length, String charactor) {
		String resultStr = sourceStr;
		if (sourceStr == null || "".equals(sourceStr)) {

			return "";
		}
		if (sourceStr.length() > length) {
			resultStr = sourceStr.substring(0, length);
			resultStr += charactor;

		}

		return resultStr;

	}

	/**
	 * 根据字符串判断boolean
	 * 
	 * @param booleanStr
	 * @return
	 * @throws TrxOrderException 
	 */
	public static boolean transBoolean(String booleanStr) throws TrxOrderException {

		if ("0".equals(booleanStr)) {

			return false;

		} else   if ("1".equals(booleanStr)){

			return true;
		}else {
			throw new TrxOrderException(BaseException.TRX_TURE_FALSE__INVALID);
			
		}

	}

	/**
	 * 随机取模
	 * 
	 * @return
	 */
	public static String randomBase() {

		String result = String.valueOf(System.currentTimeMillis() % 10);

		return result;

	}

	/**
	 * 根据用户账户ID取模
	 * 
	 * @param id
	 * @return
	 */

	public static Long getDeliveryIdBase(Long id) {
		if (id == null) {
			throw new IllegalArgumentException();
		}

		return id % 10;

	}

	public static boolean isNumber(String str) {
		if (str == null || str.trim().equals("")) {
			return false;
		}
		boolean flag = false;
		try {
			Long.parseLong(str);
			flag = true;
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static String getLength(Object goodsName, int length) {
		if (goodsName == null) {
			return null;
		} else {
			String temp = String.valueOf(goodsName);
			if (temp.length() <= length) {
				return temp;
			} else {
				temp = temp.substring(0, length) + "...";
				return temp;
			}
		}
	}
























	/**
	 * 替换email中@符号前的一半字符为*号
	 * @param email
	 * @return
	 */
	public static String handleEmail(String email){
		if(email==null){
			return "";
		}else{
			String[] aryEmail = email.split("@");
			if(aryEmail!=null && aryEmail.length==2){
				if(aryEmail[0] != null){
					String firstPart = aryEmail[0].substring(aryEmail[0].length()/2,aryEmail[0].length());
					if(firstPart != null && !"".equals(firstPart)){
						char repeatChar[] = new char[firstPart.length()];
						for(int i=0;i<firstPart.length();i++){
							repeatChar[i] = '*';
						}
						email = email.replaceFirst(firstPart + "@", new String(repeatChar) + "@");
					}
				}
			}
		}
		return email;
	}

	/**
	 * @param strIp1 获取的分销商IP
	 * @param StrIp2数据库白名单IP
	 * @return
	 */
	public static boolean checkIp(String strIp1,String StrIp2){
		boolean boo = false;
		if("".equals(StrIp2)){
			return true;
		}
		 boolean isOrderIpRule=strIp1.matches("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		 if(!isOrderIpRule){
			 return boo;
		 }
		String ipArray[] = StrIp2.split(",");
		for(int i = 0;i<ipArray.length;i++){  
			String ipArr = ipArray[i];
			String ipay = "";
			if(ipArr.contains("*")){   //如格式为192.168.1.*判断
				ipay = ipArr.substring(0,ipArr.lastIndexOf("."));
				boo = strIp1.substring(0,strIp1.lastIndexOf(".")).equals(ipay);
				if(boo){
					return boo;
				}
			}else if(ipArr.contains("-")){  //如格式为192.168.1.1-155判断
				 ipay = ipArr.substring(ipArr.lastIndexOf(".")+1);
				 String ipayArray[] = ipay.split("-");
				 String ips = strIp1.substring(strIp1.lastIndexOf(".")+1);
				 if(Integer.parseInt(ipayArray[0])<=Integer.parseInt(ips)&&Integer.parseInt(ips)<=Integer.parseInt(ipayArray[1])){
					 boo = true;
					 return boo;
				 }
			}else{   //如格式为192.168.1.1判断
				boo = strIp1.equals(ipArr);
				if(boo){
					return boo;
				}
			}
		}
		return boo;
	}
	
	/**
	 * 获取服务器IP地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	/**
	 * Str  判空 add  by wenhua.cheng
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		
		return org.apache.commons.lang.StringUtils.isEmpty(str);
	}
	
	/**
	 * 组装字符串 为字符串添加前缀后缀 add by wangweijie
	 * @param str
	 * @param prefix	 前缀
	 * @param suffix	后缀
	 * @return
	 */
	public static String packagingString(String str,String prefix,String suffix){
		if(StringUtils.isEmpty(str)) str = "";
		if(StringUtils.isEmpty(prefix)) prefix = "";
		if(StringUtils.isEmpty(suffix)) suffix = "";
		return prefix + str + suffix;
	}
	
	private static final String regex_mobile = "^1\\d{10}$";
	
	/**
	 * 
	 * janwen
	 * @param tocheckNo
	 * @return 手机号码校验
	 *
	 */
	public static boolean isMobileNo(String tocheckNo){
		return Pattern.matches(regex_mobile, tocheckNo);
	}
	public static void main(String[] args) {
		// System.out.print(cutffStr("我是测试商品上市上", 10, "..."));
		//String testStr = "【台江区】【交通便利】仅5元！到店另付70元乐享天然居145元4-5人套餐！爆炒腰花+荔枝肉+茶菇松劲肉+冬笋炒酸菜+青菜+豆腐干贝羹+餐具！尝特色美味，品精致生活，您还在犹豫什么？";
		System.out.println(handleEmail("176786787126.com"));
	}
	private static final String regex_digital = "^[1-9]\\d{0,}";
	/**
	 * 
	 * janwen
	 * @param source
	 * @param ingoreDigital 忽略数字校验
	 * @return
	 *
	 */
	public static boolean neNullAndDigital(String source,boolean ingoreDigital,Integer length){
		boolean isvalid = false;
		if(source != null && !"".equals(source.trim()) ){
			isvalid = true;
		}
		if(!ingoreDigital && isvalid){
			isvalid = Pattern.matches(regex_digital,source);
		}
		if(isvalid && length != null){
			isvalid = source.trim().length() <= length;
		}
		return isvalid;
	}
	
	
	/**
	 * 字符串对以分号分隔的字符串转化为数组，并对数组按有小到大的排序
	 * add by wangweijie 2012-11-16
	 * @return
	 */
	public static String[] sortArray(String[] array){
		//冒泡排序--有小到大顺序
		for(int i=0;i<array.length;i++){
			for(int j=0;j<i;j++){
				if(array[i].compareTo(array[j]) < 0){
					String temp = array[j];
					array[j] = array[i];
					array[i] = temp;
				}
			}
		}
		return array;
	}
	
	/**
	 * 字符串折半查找(数组必须是由小到大排列的有序数组
	 * -1代表未查到，否则返回查找的下标
	 * add by wangweijie 2012-11-16
	 */
	public static int bisearch(final String[] sourceArray,final String seek){
		if(null == sourceArray || sourceArray.length==0 || null == seek){
			return -1;
		}
		
		int bottom = 0;
		int top = sourceArray.length-1;
		int mid;
		
		while(bottom<=top){
			mid = (bottom+top)/2;
			int result = sourceArray[mid].compareTo(seek);
			if(0==result){
				return mid;
			}else if(result > 0){
				top = mid-1;
			}else{
				bottom = mid+1;
			}
		}
		return -1;
	}
}