package com.beike.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**   
 * @Title: ThreadContext
 * @Description:
 * @author ye.tian  
 * @date Apr 25, 2011
 * @version V1.0   
 */
public class ThreadContext {
	private Date currentDate;
	private Map<String,String> messages = new HashMap<String,String>();
	private Map<String,Object> values = new HashMap<String,Object>();
	
	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> objClass, String key){
		return (T)values.get(key);
	}
	
	public void addValue(String key, Object value){
		values.put(key, value);
	}
	
	public void addMessage(String key, String message){
		messages.put(key, message);
	}
	
	public String getMessage(String key){
		return messages.get(key);
	}
	
	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * ��ȡ�̰߳󶨶���������
	 * @return
	 */
	public static ThreadContext getContext(){
		return threadContext.get();
	}
	
	/**
	 * ���̶߳���������������
	 */
	public static void clearContext(){
		threadContext.remove();
	}

	/**
	 * �̰߳����
	 */
	private static ThreadLocal<ThreadContext> threadContext = new ThreadLocal<ThreadContext>() {
		protected ThreadContext initialValue() {
			return new ThreadContext();
		}
	};
}
