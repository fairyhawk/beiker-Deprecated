package com.beike.action.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.beike.util.FileUtils;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Jul 1, 2011
 * @author ye.tian
 * @version 1.0
 */

public class ReceiveFileUtils {
	/**
	 * 根据文件流的方式来获取传递的图片,可以设定图片名称
	 * 
	 * @param is
	 *            IO流
	 * @param ImageName
	 *            图片名称
	 * @return 
	 * @throws Exception
	 */
	public static void receiveImage(InputStream is,String path) throws Exception {
		//假如以前有此文件 重命名 日期
		File f=new File(path);
//		if(!f.exists()){
//			FileUtils.renameFile(path);
//		}
		
		//获取文件的绝对路径（已经包括文件名）
		String newFileDir = f.getParent();
		OutputStream os = null;
		try {
			File dirFile = new File(newFileDir);
			if(!dirFile.exists()){
				dirFile.mkdir();
			}
			os = new FileOutputStream(path);

			// 8k缓存数据
			byte[] buffer = new byte[1024 * 8];
			// 设置读进缓存的字节数
			int len;
			while ((len = is.read(buffer)) != -1) {
				// 将缓存数据写入磁盘
				os.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭输出流
			os.close();
			// 关闭输入流
			is.close();
		}
	}
}
