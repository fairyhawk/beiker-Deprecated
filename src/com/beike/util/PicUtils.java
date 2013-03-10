package com.beike.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class PicUtils {
	
	/**
	 * 限定图片支持格式
	 * @return
	 */
	public static List<String> picStuffList(){
		List<String> picList = new ArrayList<String>();
		picList.add("jpg");
		picList.add("jpeg");
		picList.add("bmp");
		picList.add("gif");
		return picList;
	}
	
	/**
	 * 限定图片大小30K
	 * @param picSize
	 * @return
	 */
	public static boolean picSize(long picSize,int size){
		long standSize = size*1024;
		boolean flag = true;
		if(standSize<picSize){
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 校验图片大小，以及图片格式是否正确
	 * @param file
	 * @return
	 */
	public static boolean validatorPic(MultipartFile file,int size){
		boolean flag = true;
		try{
			if(StringUtils.validNull(file.getOriginalFilename())){
				long picSize = file.getSize();
				String picName = file.getOriginalFilename();
				String ext = picName.substring(picName.lastIndexOf(".")+1,picName.length());
				if(!PicUtils.picStuffList().contains(ext.toLowerCase())){
					flag = false;
				}
				if(!PicUtils.picSize(picSize,size)){
					flag = false;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
}
