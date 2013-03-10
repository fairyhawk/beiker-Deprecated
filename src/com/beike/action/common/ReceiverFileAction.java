package com.beike.action.common;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.util.PropertyUtil;
import com.beike.util.WebUtils;

/**
 * <p>Title:前端应用服务器接收文件</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Jul 1, 2011
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class ReceiverFileAction {
	private Log log=LogFactory.getLog(this.getClass());
	private PropertyUtil propertyUtil=PropertyUtil.getInstance("project");
	
	@RequestMapping("/file/receiveFile.do")
	public String receiveFile(HttpServletRequest request){
		//TODO:判断ip 是否为自己服务器的IP访问过来的。
		String ip=WebUtils.getIpAddr(request);
		log.info("receive file ip==========>:"+ip);
		String serverIp=propertyUtil.getProperty("SERVER_IP");
		log.info("sever ip is "+serverIp);
		if(serverIp.indexOf(ip)!=-1){
			String path=request.getParameter("path");
			String realpath=request.getRealPath("");
			path=realpath+path;
			try {
				//删除首页历史碎片add by qiaowb 2012-01-11
				deleteOldHtmlFile(path);
				ReceiveFileUtils.receiveImage(request.getInputStream(),path);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 删除首页商品历史碎片
	 * @param path
	 */
	private void deleteOldHtmlFile(String path){
		File curFile = new File(path);
		// 获取文件名
		String filename = curFile.getName();
		
		String delFilePrefix = "";
		if("mainlistone_1.html".equals(filename)){
			delFilePrefix = "mainlistone_";
		}else if("mainlisttwo_1.html".equals(filename)){
			delFilePrefix = "mainlisttwo_";
		}else if("mainlistthree_1.html".equals(filename)){
			delFilePrefix = "mainlistthree_";
		}else if("mainlistfour_1.html".equals(filename)){
			delFilePrefix = "mainlistfour_";
		}
		//删除历史碎片文件
		if(!"".equals(delFilePrefix)){
			//文件目录
			String curDir = curFile.getParent();
			File dir = new File(curDir);
			FilenameFilterImpl filter = new FilenameFilterImpl(delFilePrefix);
			String[] names = dir.list(filter);
			if(names!=null && names.length>0){
				for(int i=0;i<names.length;i++){
					File delfile=new File(curDir+"/"+names[i]);
					if(delfile.isFile()){
						delfile.delete();
					}
				}
			}
		}
	}
	
	//通过文件名匹配
	private class FilenameFilterImpl implements FilenameFilter {
		private String name;

		public FilenameFilterImpl(String name) {
			this.name = name;
		}

		public boolean accept(File dir, String file) {
			return file.startsWith(name) && !file.equals(name + "1.html");
		}
	}
}