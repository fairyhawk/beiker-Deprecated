package com.beike.common.bean.trx.partner;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.beike.util.Amount;
import com.beike.util.Configuration;
import com.beike.util.HttpClientUtil;
import com.beike.util.StringUtils;
import com.beike.util.security.AES;
import com.beike.util.security.Base64;

/**   
 * @title: Par360BuyOrderGenerator.java
 * @package com.beike.common.bean.trx.partner
 * @description: 京东报文转化及加解密
 * @author wangweijie  
 * @date 2012-8-29 下午02:47:29
 * @version v1.0   
 */
public class Par360buyOrderGenerator {
	private static final Log logger = LogFactory.getLog(Par360buyOrderGenerator.class);
	public static final String CHAR_ENCODING = "UTF-8"; 		//京东字符编码
	
	public static final String VERIFY_VOUCHER = "VerifyVoucher";		//验券
	public static final String QUERY_VOUCHER = "QueryVoucher";			//查询券
	
	/**
	 * 获得京东传入原始报文
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String get360buyReqMessage(InputStream is) throws IOException{
		StringBuffer message = new StringBuffer("");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is,CHAR_ENCODING));
			String line = br.readLine();
			while(line != null){
				message.append(line);
				line = br.readLine();
			}
		} catch (UnsupportedEncodingException e) {
			//字符编码转化失败
			logger.error("Error:resolving 360buy message by charset " + CHAR_ENCODING,e);
			e.printStackTrace();
		}
		return message.toString();
	}
	
	/**
	 * 获得partnerNo
	 * @param message
	 * @return
	 */
	public static String getPartnerNo(String message){
		int startIndex = message.indexOf("<VenderId>") + "<VenderId>".length();
		int endIndex = message.indexOf("</VenderId>");
		return message.substring(startIndex, endIndex);
	}
	
	/**
	 * 获得返回码
	 * @param message
	 * @return
	 */
	public static Map<String,String> getResultCodeInfo(String message){
		Map<String,String> resultInfoMap = new HashMap<String,String>();
		
		//key
		int startIndex = message.indexOf("<ResultCode>") + "<ResultCode>".length();
		int endIndex = message.indexOf("</ResultCode>");
		resultInfoMap.put("ResultCode", StringUtils.toTrim(message.substring(startIndex, endIndex)));
		
		//
		startIndex = message.indexOf("<ResultMessage>") + "<ResultMessage>".length();
		endIndex = message.indexOf("</ResultMessage>");
		resultInfoMap.put("ResultMessage", StringUtils.toTrim(message.substring(startIndex, endIndex)));
		return resultInfoMap;
	}
	/**
	 * 获得京东报文的请求数据
	 * @param sourceMessage:原始请求报文
	 * @return
	 */
	public static String get360buyDataMessage(String message,String partnerNo,String partnerKey,String key) throws Exception{
		Map<String,String> xmlMap = xml2Map(message);
		String venderId = xmlMap.get("VenderId");		//合作伙伴ID
		String xmlns = StringUtils.toTrim(xmlMap.get("xmlns"));
		if(!xmlns.endsWith("Response")){
			String venderKey = xmlMap.get("VenderKey");		//合作伙伴Key

			//判断合作伙伴ID和key是否合法(京东的key放到notice_key_value字段)
			if(!(venderId.equals(partnerNo) && venderKey.equals(partnerKey))){
				logger.info("+++++++++++++++360buy VenderId|VenderKey illegal ++{ERROR}+++++++++++++");
				return "10100"; //身份验证失败---合作伙伴ID验证失败
			}
		}
		
		String encrypt = xmlMap.get("Encrypt");			//是否加密
		String data = xmlMap.get("Data");				//报文体内容
		String zip = xmlMap.get("Zip");					//是否压缩--默认不压缩

		//判断是否进行加密,如果是加密则进行解密
		if(!"false".equalsIgnoreCase(encrypt.trim())){
			logger.info("+++++++++++++++++++encrty data=" +data);
			//如果报文经过压缩，则解压缩
			if("true".equalsIgnoreCase(zip)){
				data = Par360buyOrderGenerator.uncompress(data);
				logger.info("+++++++++++++unzip encrty data="+data);
			}
			//报文解密
			data = Par360buyOrderGenerator.decrypt(data, key);
		}
//		logger.info("+++++++++++++++360buy message+++" + data);
		return data;
	}
	
	/**
	 * 360buy报文解密
	 * 1、base64解码
	 * 2、AES解密
	 */
	public static String decrypt(String str,String key) throws UnsupportedEncodingException{
		//base64解码
		//logger.info("+++++++++++++++++++++++base64 decode:" + str);
		byte strByte[] = Base64.decode(str);
	
		//AES解密
		byte decryptByte[] = AES.DecryptToBytes(strByte, key.getBytes(CHAR_ENCODING));
		String decryptStr = new String(decryptByte,CHAR_ENCODING);
		logger.info("+++++++++++++++++++++++360buy decrypt:" + decryptStr);
		return decryptStr;
	}
	
	/**
	 * 360buy报文加密
	 * 1、AES加密
	 * 2、base64编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encrypt(String str,String key) throws UnsupportedEncodingException{
		//AES加密
		byte encryptByte[] = AES.Encrypt(str.getBytes(CHAR_ENCODING), key.getBytes(CHAR_ENCODING));
		
		//base64编码
		
		String encryptStr = Base64.encode(encryptByte);;
		logger.info("++++++++++++++++360buy message:" + str);
		logger.info("++++++++++++++++360buy encrypt message:" + encryptStr);
		return encryptStr;
	}
	
	/**
	 * 组装京东 请求报文
	 * @param messageData
	 * @return
	 */
	public static String packageRequestMsg(String messageData,String partnerNo,String partnerKey,String key) {
		
		String encryptData = "";
		try {
			if(null != messageData && !"".equals(messageData.trim())){
				encryptData = Par360buyOrderGenerator.encrypt(messageData, key);
			}else{
				encryptData ="";
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("360buy <<<<===============message ${ERROR}encrypt error", e);
			e.printStackTrace();
		}
		
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		xml.append("<Request xmlns=\"http://tuan.360buy.com/Request\">");
			xml.append("<Version>1.0</Version>");
			xml.append("<VenderId>"+partnerNo+"</VenderId>");
			xml.append("<VenderKey>"+partnerKey+"</VenderKey>");
			xml.append("<Zip>false</Zip>");
			xml.append("<Encrypt>true</Encrypt>");
			xml.append("<Data>"+encryptData+"</Data>");
		xml.append("</Request>");
		
		return xml.toString();
	}
	
	/**
	 * 组装京东 返回报文
	 * @param messageData
	 * @return
	 */
	public static String packageResponseMsg(Par360buyOrderParam resParam,String key) {

		//生成data
		logger.info("========>>>>>>>=======360buy data:" + resParam.getData());
		
		//对Data字段进行加密
		String data = resParam.getData();
		try {
			if(null != data && !"".equals(data.trim())){
				data = Par360buyOrderGenerator.encrypt(data, key);
			}
		} catch (UnsupportedEncodingException e) {
			data = "";
		}
		
		String resultCode = StringUtils.validNull(resParam.getResultCode())?resParam.getResultCode():"-1";  //-1表示接口调用异常 
		String resultMessage = StringUtils.validNull(resParam.getResultMessage())?resParam.getResultMessage():"interface handle error";
		
		//组装整体报文
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		xml.append("<Response xmlns=\"http://tuan.360buy.com/Response\">");
			xml.append("<Version>1.0</Version>");		//版本号
			xml.append("<VenderId>").append(StringUtils.toTrim(resParam.getVenderId())).append("</VenderId>");	//合作伙伴ID
			xml.append("<Zip>false</Zip>");		//是否压缩--不压缩
			xml.append("<Encrypt>true</Encrypt>"); //加密
			xml.append("<ResultCode>"+resultCode+"</ResultCode>");	//返回码
			xml.append("<ResultMessage>"+resultMessage+"</ResultMessage>");  //响应信息
			xml.append("<Data>").append(data).append("</Data>");
		xml.append("</Response>");
		
		logger.info("========>>>>>>>=======360buy message:" + xml.toString());
		return xml.toString();
	}
	
	
	// 解压缩
	public static String uncompress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes(CHAR_ENCODING));
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n = -1;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString("GBK")
		return out.toString(CHAR_ENCODING);
	}
	
	
	public static String sendRequestTo360buy(String partnerNo,String apiType,String methodType,String xmlData) {
		String url = getUrlByHostNo(partnerNo, apiType,methodType);
		String responseXml = HttpClientUtil.sendPostHTTP(url, xmlData, CHAR_ENCODING);		//发送并接受响应报文
		return responseXml;
	}
	
	/**
	 * xml转化成map
	 * @param xml
	 * @return
	 */
	public static Map<String,String> xml2Map(String xml) throws JDOMException,IOException{
		if(null==xml || "".equals(xml.trim())) return null;
		
		// 创建一个新的字符串     
	    StringReader xmlString = new StringReader(xml);     
	    // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入     
	    InputSource source = new InputSource(xmlString);     
		SAXBuilder saxBuilder = new SAXBuilder(false);  //使用默认解析器
		Map<String,String> xmlMap = null;
		try {
	        // 通过输入源构造一个Document     
			Document doc = saxBuilder.build(source);
			
			// 取的根元素     
	        Element root = doc.getRootElement();
			
	        //获得所有叶子节点
	        List<Object> elementList = getContent(root);
	        xmlMap = new HashMap<String,String>(elementList.size() + 1 );
	        
	        //添加命名空间
	        Namespace namespace = root.getNamespace();
			if (null != namespace) {
				xmlMap.put("xmlns"+namespace.getPrefix(), namespace.getURI());
			}
			
	        for(Object obj : elementList){
	        	String key = "";
	        	String value = "";
	        	if(obj instanceof Element){
	        		key= ((Element)obj).getName();
	        		value = ((Element)obj).getValue();
	        	}else{
	        		key= ((Attribute)obj).getName();
	        		value = ((Attribute)obj).getValue();
	        	}
	        	//不添加空 元素
	        	if(null == key || "".equals(key.trim()) || null == value || "".equals(value.trim())){
	        		continue;
	        	}
	        	if(xmlMap.containsKey(key)){
	        		value = xmlMap.get(key) + ";" + value;		//存在多个元素key一样，value值以;分割
	        	}
	        	xmlMap.put(key, value);
	        	
	        }
		} catch (JDOMException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}     
		return xmlMap;
	}
	
	/**
	 * String转化为京东数据对象
	 * @param paramInfo
	 * @return
	 */
	public static Par360buyOrderParam transReqInfo(String paramInfo){
		Par360buyOrderParam param = null;
		try{
			param = new Par360buyOrderParam();
			Map<String,String> map = Par360buyOrderGenerator.xml2Map(paramInfo);
			param.setMessage(map.get("xmlns"));		//设置方法名
			param.setJdTeamId(map.get("JdTeamId"));	//京东团购ID
			param.setVenderTeamId(map.get("VenderTeamId")); //合作伙伴团购ID
			param.setMobile(map.get("Mobile"));	//手机
			String orderDate = map.get("OrderDate");
			if(StringUtils.validNull(orderDate)){
				param.setOrderDate(new Date(Long.parseLong(orderDate)));	//下单时间
			}
			
			String teamPrice = map.get("TeamPrice");
			if(StringUtils.validNull(teamPrice)){
				//购买价 --京东以分为单位
				param.setTeamPrice(String.valueOf(Amount.div(Double.parseDouble(teamPrice),100, 2)));			
			}
			param.setCount(map.get("Count"));	//订购数量
			
			String origin = map.get("Origin");
			if(StringUtils.validNull(teamPrice)){
				//订单总额--京东以分为单位
				param.setOrigin(String.valueOf(Amount.div(Double.parseDouble(origin),100, 2)));	
			}
			param.setJdOrderId(map.get("JdOrderId"));		//京东订单ID
			
			String payTime = map.get("PayTime");
			if(StringUtils.validNull(payTime)){
				param.setPayTime(new Date(Long.parseLong(payTime)));		//付款时间
			}
			
			String refundMoney = map.get("RefundMoney");
			if(StringUtils.validNull(refundMoney)){
				//申请退款金额--京东以分为单位
				param.setRefundMoney(String.valueOf(Amount.div(Double.parseDouble(refundMoney),100, 2)));	
			}
			
			param.setVenderOrderId(map.get("VenderOrderId"));//合作伙伴订单ID
			param.setIsAllBack(map.get("IsAllBack"));	//是否该单全退
			
			String couponIds = map.get("CouponId");
			if(StringUtils.validNull(couponIds)){
				Map<String,String> couponMap = new LinkedHashMap<String,String>();
				String [] couponIdArray = couponIds.split(";");
				String [] couponPwdArray = StringUtils.toTrim(map.get("CouponPwd")).split(";");
				if(couponIdArray.length < couponPwdArray.length){
					logger.error("+++++++++++++360buy message{ERROR} :couponId.size not equals couponPwd.size+++++++++");
					return null;
				}
				//优惠券码 和密码 
				for(int i=0;i<couponIdArray.length;i++){
					//只有优惠券和秘密相同时，才设置couponPwd的值否则couponPwd值为空
					couponMap.put(couponIdArray[i], couponPwdArray.length==couponIdArray.length?couponPwdArray[i]:"");
				}
				param.setCouponMap(couponMap);
			}else{
				//退款的 京东券列表
				couponIds = map.get("Coupon");
				if(null != couponIds && !"".equals(couponIds.trim())){
					Map<String,String> couponMap = new LinkedHashMap<String,String>();
					String [] couponIdArray = couponIds.split(";");
					for(int i=0;i<couponIdArray.length;i++){
						couponMap.put(couponIdArray[i],"");
					}
					param.setCouponMap(couponMap);
				}
			}
			
		} catch (JDOMException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return param;
	}
	
	/**
	 * 获得所有叶子节点
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<Object> getContent(Element element){
		List<Object> elementList = new ArrayList<Object>();
		
		List<Object> attributes=element.getAttributes(); 
		if(!attributes.isEmpty()){
			elementList.addAll(attributes);
		}
		List<Object> childrenList = element.getChildren();
		if(null !=childrenList && childrenList.size()>0){
			for(Object childElement : childrenList){
				elementList.addAll(getContent((Element)childElement));
			}
		}else{
			elementList.add(element);
		}
		return elementList;
	}
	
	
	public static String getUrlByHostNo(String hostNo,String name,String methodType){
		String url = Configuration.getInstance().getValue(hostNo+"_"+name+"_"+methodType);
		return url;
	}
}
