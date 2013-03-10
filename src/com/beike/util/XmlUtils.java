package com.beike.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

/**   
 * @title: XmlUtils.java
 * @package com.beike.util
 * @description: xml工具类
 * @author wangweijie 
 * @date 2012-6-26 下午05:19:19
 * @version v1.0   
 */
public class XmlUtils {
	
	private static String delimiter = ";";
	
	
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
	        List<Element> elementList = getLeafElement(root);
	        
	        xmlMap = new HashMap<String,String>(elementList.size());
	        for(Element element : elementList){
	        	String key = element.getName();
	        	String value = element.getValue();
	        	//不添加空 元素
	        	if(null == key || "".equals(key.trim()) || null == value || "".equals(value.trim())){
	        		continue;
	        	}
	        	if(xmlMap.containsKey(key)){
	        		value = xmlMap.get(key) + delimiter + value;		//存在多个元素key一样，value值以;分割
	        	}else{
	        		xmlMap.put(key, value);
	        	}
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
	 * 获得所有叶子节点
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<Element> getLeafElement(Element element){
		List<Element> elementList = new ArrayList<Element>();
		List<Element> childrenList = element.getChildren();
		if(null !=childrenList && childrenList.size()>0){
			for(Element childElement : childrenList){
				elementList.addAll(getLeafElement(childElement));
			}
		}else{
			elementList.add(element);
		}
		return elementList;
	}
	
	public static String object2xml(String xmlHead,String rootElement,Object objectXml){
		if(null == xmlHead || "".equals(xmlHead.trim())){
			xmlHead = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		}
		StringBuffer xml = new StringBuffer(xmlHead);
		xml.append(elmentObject2xml(rootElement,objectXml));
		return xml.toString();
	}
	
	@SuppressWarnings("unchecked")
	private static String elmentObject2xml(String elementName ,Object objectXml){
		StringBuffer xml = new StringBuffer("");
		
		if(null == objectXml){
			xml.append("");
		}else{
			//List
			if(objectXml instanceof List){
				List listXml = (List)objectXml;
				for(Object subObjectXml : listXml){
					xml.append(elmentObject2xml(elementName,subObjectXml));
				}
			}
			//Set
			else if (objectXml instanceof Set){
				Set setXml = (Set)objectXml;
				for(Object subObjectXml : setXml){
					xml.append(elmentObject2xml(elementName,subObjectXml));
				}
			}
			
			//数组
			else if(objectXml instanceof Object[]){
				Object[] arrayXml = (Object[])objectXml;
				for(Object subObjectXml : arrayXml){
					xml.append(elmentObject2xml(elementName,subObjectXml));
				}
			}
			//map
			else if(objectXml instanceof Map){
				Map mapXml = (Map)objectXml;
				xml.append("<" + elementName + ">");
				for(Object key : mapXml.keySet()){
					xml.append(elmentObject2xml(key.toString(),mapXml.get(key)));
				}
				xml.append("<" + elementName + ">");
			}
			//其他基本类型
			else if(objectXml instanceof String || objectXml instanceof Integer || objectXml instanceof Float || objectXml instanceof Boolean 
				|| objectXml instanceof Short || objectXml instanceof Double || objectXml instanceof Long || objectXml instanceof BigDecimal 
				|| objectXml instanceof BigInteger  || objectXml instanceof Byte  || objectXml instanceof Date){
				xml.append("<" + elementName + ">");
				xml.append(objectXml);
				xml.append("<" + elementName + ">");
			}
			//bean
			else{
				PropertyDescriptor[] props = null;             
				try {               
					props = Introspector.getBeanInfo(objectXml.getClass(), Object.class).getPropertyDescriptors();             
				} catch (IntrospectionException e) {}
				if (props != null) {               
					xml.append("<" + elementName + ">");
					for (int i = 0; i < props.length; i++) {                 
						try {
							xml.append(elmentObject2xml(props[i].getName(),props[i].getReadMethod().invoke(objectXml)));
						} catch (Exception e) {}               
					}
					xml.append("<" + elementName + ">");
				}
				else {              
					xml.append("<" + elementName + "><" + elementName + ">");				
				}             
			}
		}
		return xml.toString();
	}
	
//	public static void main(String[] args) throws Exception {
//		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
//				+"<alipay xmlns=\"http://tuan.360buy.com/SendOrderRequest\">"
//					+"<is_success>T</is_success>"
//					+"<request>"
//						+"<param name=\"_input_charset\">UTF-8</param>"
//						+"<param name=\"service\">single_trade_query</param>"
//						+"<param name=\"partner\">2088601322494371</param>"
//						+"<param name=\"out_trade_no\">QAPay00967461R</param>"
//					+"</request>"
//					+"<response>"
//						+"<trade>"
//							+"<body>0.15-11111111020-12497-1020-880397</body>"
//							+"<buyer_email>huangyuan@vip.qq.com</buyer_email>"
//							+"<buyer_id>2088002014080601</buyer_id>"
//							+"<coupon_used_fee>0.00</coupon_used_fee>"
//							+"<discount>0.00</discount>"
//							+"<flag_trade_locked>0</flag_trade_locked>"
//							+"<gmt_create>2012-06-25 16:56:50</gmt_create>"
//							+"<gmt_last_modified_time>2012-06-25 16:56:51</gmt_last_modified_time>"
//							+"<gmt_payment>2012-06-25 16:56:51</gmt_payment>"
//							+"<is_total_fee_adjust>F</is_total_fee_adjust>"
//							+"<operator_role>B</operator_role>"
//							+"<out_trade_no>QAPay00967461R</out_trade_no>"
//							+"<payment_type>1</payment_type>"
//							+"<price>0.01</price>"
//							+"<quantity>1</quantity>"
//							+"<seller_email>alipay@qianpin.com</seller_email>"
//							+"<seller_id>2088601322494371</seller_id>"
//							+"<subject>这个是测试......</subject>"
//							+"<to_buyer_fee>0.00</to_buyer_fee>"
//							+"<to_seller_fee>0.01</to_seller_fee>"
//							+"<total_fee>0.01</total_fee>"
//							+"<trade_no>2012062547948360</trade_no>"
//							+"<trade_status>TRADE_FINISHED</trade_status>"
//							+"<use_coupon>F</use_coupon>"
//						+"</trade>"
//					+"</response>"
//					+"<sign>59019996449ac9f31b99349df28c56c6</sign>"
//					+"<sign_type>MD5</sign_type>"
//				+"</alipay>";
//		
//		System.out.println(XmlUtils.xml2Map(xml));
//		
//		
//		Map<String,Object> map = new HashMap<String, Object>();
//		Map<String,Object> subMap = new HashMap<String, Object>();
//		subMap.put("totalCount", "1");
//		subMap.put("errorCount", "2");
//		
//		Map<String,Object> thirdMap = new HashMap<String, Object>();
//		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
//		for(int i=0;i<3;i++){
//			Map<String,String> _3map = new HashMap<String, String>();
//			_3map.put("voucherCode", "voucherCode_"+i);
//			_3map.put("issueTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//			_3map.put("consumptionTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//			_3map.put("voucherCount", "voucherCount_"+i);
//			list.add(_3map);
//		}
//		thirdMap.put("VoucherInfo", list);
//		
//		subMap.put("voucherInfoList",thirdMap);
//		String[] s = new String[]{"1","2","3","4"};
//		subMap.put("testList", s);
//		System.out.println(XmlUtils.object2xml("", "response", subMap));
//	}
}
