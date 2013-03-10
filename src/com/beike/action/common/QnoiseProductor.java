package com.beike.action.common;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import nl.captcha.noise.NoiseProducer;

/**
 * <p>
 * Title:
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
 * @date Apr 26, 2012
 * @author ye.tian
 * @version 1.0
 */

public class QnoiseProductor implements NoiseProducer {

	@Override
	public void makeNoise(BufferedImage image) {
		if(image==null){
			 image = new BufferedImage(120, 40,
		 				BufferedImage.TYPE_INT_RGB); // 创建BufferedImage类的对象
		}
		Graphics g = image.getGraphics(); // 创建Graphics类的对象
		Graphics2D g2d = (Graphics2D) g; // 通过Graphics类的对象创建一个Graphics2D类的对象
		BasicStroke bs = new BasicStroke(2f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL); // 创建一个供画笔选择线条粗细的对象
		g2d.setStroke(bs); // 改变线条的粗细
	}

}
