package com.beike.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageZipUtil {
	
	/**
	 * 压缩图片文件<br>
	 * 先保存原文件，再压缩、上传
	 * 
	 * @param oldFile
	 *          要进行压缩的文件全路径
	 * @param width
	 *          宽度
	 * @param height
	 *          高度
	 * @param quality
	 *          质量
	 * @param smallIcon
	 *          小图片的后缀
	 * @return 返回压缩后的文件的全路径
	 */
	public static String zipImageFile(InputStream oldFile, String filePath, String fileName, int width, int height, float quality, String smallIcon) {
		if (oldFile == null) {
			return null;
		}
		String newImage = filePath + System.getProperties().getProperty("file.separator") + fileName;
		try {
			/** 获取原图片宽，高 */
			/*
			 * BufferedImage bimage = ImageIO.read(oldFile);//new File(oldFile)
			 * int imageWidth = bimage.getWidth(); int imageHeight =
			 * bimage.getHeight();
			 */

			/** 对服务器上的临时文件进行处理 */
			Image srcFile = ImageIO.read(oldFile);
			
			/*
			 * if ( imageWidth >= imageHeight) { width = imageWidth/2; height =
			 * (int)Math.round((imageHeight * width * 1.0 / imageWidth)); } else
			 * { height = imageHeight/2; width = (int)Math.round((imageWidth *
			 * height * 1.0 / imageHeight)); }
			 */

			/** 宽,高设定 */
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			tag.getGraphics().drawImage(srcFile, 0, 0, width, height, null);
			/*
			 * String filePrex = oldFile.substring(0, oldFile.indexOf('.'));
			 */
			/** 压缩后的文件名 */
			/*
			 * newImage = filePrex + smallIcon +
			 * oldFile.substring(filePrex.length());
			 */

			/** 压缩之后临时存放位置 */
			FileOutputStream out = new FileOutputStream(newImage);
			
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
			/** 压缩质量 */
			jep.setQuality(quality, true);
			encoder.encode(tag, jep);
			out.close();
			
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return newImage;
	}
	
	public static void resizeImage(InputStream oldFile, String filePath, String fileName, int width, int height) {
		if (oldFile == null) {
			return;
		}
		String newImage = filePath + System.getProperties().getProperty("file.separator") + fileName;
		FileOutputStream out = null;
		try {
			Image image = ImageIO.read(oldFile);
			int imageWidth = image.getWidth(null);
			int imageHeight = image.getHeight(null);
			
			float scale = getRatio(imageWidth, imageHeight, width, height);
			imageWidth = (int) (scale * imageWidth);
			imageHeight = (int) (scale * imageHeight);
			
			image = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_AREA_AVERAGING);
			// Make a BufferedImage from the Image.
			BufferedImage mBufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = mBufferedImage.createGraphics();
			
			g2.drawImage(image, 0, 0, imageWidth, imageHeight, Color.white, null);
			
			float[] kernelData2 = {-0.125f, -0.125f, -0.125f, -0.125f, 2, -0.125f, -0.125f, -0.125f, -0.125f};
			Kernel kernel = new Kernel(3, 3, kernelData2);
			ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
			mBufferedImage = cOp.filter(mBufferedImage, null);
			
			/** 压缩之后临时存放位置 */
			out = new FileOutputStream(newImage);
			
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(mBufferedImage);
			param.setQuality(0.9f, true);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(mBufferedImage);
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (null != out) {
				try {
					out.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static float getRatio(int width, int height, int maxWidth, int maxHeight) {
		float Ratio = 1.0f;
		float widthRatio;
		float heightRatio;
		widthRatio = (float) maxWidth / width;
		heightRatio = (float) maxHeight / height;
		if (widthRatio < 1.0 || heightRatio < 1.0) {
			Ratio = widthRatio <= heightRatio ? widthRatio : heightRatio;
		}
		return Ratio;
	}
	/**
	 * 压缩wap图片，如果图片宽度大于 wdith，则进行压缩
	 * @param oldFile InputStream对象，源文件流
	 * @param filePath 压缩后图片路径
	 * @param fileName 压缩后图片名称
	 * @param width		压缩后图片宽度
	 * @param height   压缩后图片高度
	 * @param auto 是否自动适应wap页面图片，宽度超过320时，按照当前图片当前比例压缩至320
	 * @throws IOException 
	 */
	public static void resizeWapImage(InputStream oldFile, String filePath, String fileName, int width, int height) throws IOException {
		if (oldFile == null) {
			return;
		}
		
		File tempPath = new File(filePath);
		if(!tempPath.isDirectory())
		{
			tempPath.mkdirs();
		}
		
		String newImage = filePath + System.getProperties().getProperty("file.separator") + fileName;
		FileOutputStream out = null;
		try {
			Image image = ImageIO.read(oldFile);
			int imageWidth = image.getWidth(null);
			int imageHeight = image.getHeight(null);
			
			if(imageWidth > width)
			{
				height = (int) (((float)imageHeight / (float)imageWidth ) * 320);
			}
			else{
				width = imageWidth;
				height = imageHeight;
			}
			
			float scale = getRatio(imageWidth, imageHeight, width, height);
			imageWidth = (int) (scale * imageWidth);
			imageHeight = (int) (scale * imageHeight);
			
			image = image.getScaledInstance(imageWidth, imageHeight, Image.SCALE_AREA_AVERAGING);
			// Make a BufferedImage from the Image.
			BufferedImage mBufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = mBufferedImage.createGraphics();
			
			g2.drawImage(image, 0, 0, imageWidth, imageHeight, Color.white, null);
			
			float[] kernelData2 = {-0.125f, -0.125f, -0.125f, -0.125f, 2, -0.125f, -0.125f, -0.125f, -0.125f};
			Kernel kernel = new Kernel(3, 3, kernelData2);
			ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
			mBufferedImage = cOp.filter(mBufferedImage, null);
			
			/** 压缩之后临时存放位置 */
			out = new FileOutputStream(newImage);
			
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(mBufferedImage);
			param.setQuality(0.9f, true);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(mBufferedImage);
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (null != out) {
				try {
					out.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
