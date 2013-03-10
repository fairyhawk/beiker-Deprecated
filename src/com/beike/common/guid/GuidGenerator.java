package com.beike.common.guid;

/**
 * @Title: GuidGenerator.java
 * @Package com.beike.common.guid
 * @Description: 序列号生成器
 * @author wh.cheng@sinobogroup.com
 * @date May 6, 2011 8:28:27 PM
 * @version V1.0
 */
public interface GuidGenerator {
	public String getGuid(String type);

	public String getGuid();

	public String getSpecialId(String project, int length);

	public String getSpecialId(String project, int length, boolean appendJvmId);
	
	public String  gainCode(String prefix);

	public String getSpecialId(final String project, int length,
			boolean appendJvmId,Long count);
	public  String getSpecialIdFalse(final String project, int length);
}
