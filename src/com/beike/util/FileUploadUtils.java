package com.beike.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**   
 * @Title: 
 * @Package 
 * @Description:
 * @author ye.tian  
 * @date Feb 27, 2011
 * @version V1.0   
 */

public class FileUploadUtils {
	
	public static void uploadImage(File src, File dest) throws Exception {
		InputStream input = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest);
		byte[] b = new byte[4 * 1024];
		int n = 0;
		while ((n = input.read(b)) != -1) {
			out.write(b, 0, n);
		}
		out.flush();
		input.close();
		out.close();
	}
	
	public static void uploadImage(InputStream input, File dest) throws Exception {
		OutputStream out = new FileOutputStream(dest);
		byte[] b = new byte[4 * 1024];
		int n = 0;
		while ((n = input.read(b)) != -1) {
			out.write(b, 0, n);
		}
		out.flush();
		input.close();
		out.close();
	}
}

