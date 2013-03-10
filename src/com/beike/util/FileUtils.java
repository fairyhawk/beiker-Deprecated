package com.beike.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Title:文件工具类
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date Jun 29, 2011
 * @author ye.tian
 * @version 1.0
 */

public class FileUtils {

	public static String getFileContent(String path) {
		File myFile = new File(path);
		if (!myFile.exists()) {
			System.err.println("Can't Find " + path);
		}
		BufferedReader in=null;
		StringBuilder sb=new StringBuilder();
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(myFile),"UTF-8");
			 in = new BufferedReader(read);
			String str;
			while ((str = in.readLine()) != null) {
				sb.append(str);
			}
			
		} catch (IOException e) {
			e.getStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	public static File writeFileContent(String content ,String path){
		File file=new File(path);
		FileOutputStream fileout=null;
		try{
			if(!file.exists()){
				file.createNewFile();
			}
			   fileout   =   new   FileOutputStream(file); 
			fileout.write(content.getBytes("utf-8"));
			fileout.flush();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fileout!= null){
				try {
					fileout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}
	
	public static void renameFile(String path){
		File f=new File(path);
		//获取文件的绝对路径（已经包括文件名）
	   String fileDir = f.getAbsolutePath();
	   //获取文件名
	   String filename = f.getName();
	   String newFileDir = f.getParent();
	   //为重命名做准备
	   File f_old = f;
	   File f_new = null;
	   String str = null;
	   //重命名文件时加上系统日期
	   Date d = new Date();
	   SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
	   str = date.format(d);
	   str = str.replace("-","");
	   //设置重命名规则
	   f_old = new File(fileDir);
	   f_new = new File(newFileDir,str+"_"+filename);
	   f_old.renameTo(f_new);
	}
	
	
	
	public static void main(String[] args) {
			File f=new File("G:\\1.txt");
			
			//获取文件的绝对路径（已经包括文件名）
		   String fileDir = f.getAbsolutePath();
		   //获取文件名
		   String filename = f.getName();
		   String newFileDir = f.getParent();
		   //为重命名做准备
		   File f_old = f;
		   File f_new = null;
		   String str = null;
		   //重命名文件时加上系统日期
		   Date d = new Date();
		   SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
		   str = date.format(d);
		   str = str.replace("-","");
		   //设置重命名规则
		   f_old = new File(fileDir);
		   f_new = new File(newFileDir,str+"_"+filename);
		   f_old.renameTo(f_new);
		   //这样，文件已经被移动到目标文件夹下，并且已经按规则重命名。

		
		
	}
	/**
	 * 
	* @Title: if the file of path then return true else return false
	* @Description: 文件是否存在 
	* @param @param path
	* @param @return    设定文件
	* @return boolean    返回类型
	* @throws
	 */
	public static boolean fileIsExists(HttpServletRequest request,String path){
		boolean result = true;
		path = request.getRealPath("")+path;
		File myFile = new File(path);
		if (!myFile.exists()) {
			result = false;
		}
		return result;
	}
}
