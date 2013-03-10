package com.beike.action.common;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.captcha.Captcha;
import nl.captcha.Captcha.Builder;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;
import nl.captcha.gimpy.BlockGimpyRenderer;
import nl.captcha.gimpy.DropShadowGimpyRenderer;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.renderer.ColoredEdgesWordRenderer;
import nl.captcha.text.renderer.WordRenderer;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date Apr 26, 2012
 * @author ye.tian
 * @version 1.0
 */
@Controller
public class SimpleCaptchaFilter extends HttpServlet{   
    
	private final MemCacheService memCacheService = MemCacheServiceImpl
	.getInstance();
	private static final long serialVersionUID = -4881903412407596145L;

	private static final String PARAM_HEIGHT = "height"; //高度 默认为50   
   
    private static final String PARAM_WIDTH = "width";//宽度 默认为200   
       
    private static final String PAEAM_NOISE="noise";//干扰线条 默认是没有干扰线条   
       
//    private static final String PAEAM_TEXT="text";//文本   

    protected int _width = 120;   
    protected int _height = 40;   
    protected boolean _noise=false;   
    protected String _text=null;   
       
    /**  
     * 因为获取图片只会有get方法  
     */  
    @RequestMapping("/user/service.do") 
    public void processRequest(HttpServletRequest req, HttpServletResponse resp)   
          throws ServletException, IOException {   
    	String width=req.getParameter("width");
    	String height = req.getParameter("height");
    	String isnoise=req.getParameter("noise");
    	try {
			if (!StringUtils.isBlank(width)) {
				_width = Integer.parseInt(width);
			}
			if (!StringUtils.isBlank(height)) {
				_height = Integer.parseInt(height);
			}
			if(!StringUtils.isBlank(isnoise)){
				_noise=false;
			}
			
		} catch (Exception e) {
			
		}
		Builder builder=new Captcha.Builder(_width, _height);   
         //增加边框   
//         builder.addBorder();   
         //是否增加干扰线条   
         if(_noise==true){
        	 builder.addNoise(); 
        	 builder.addNoise(new QnoiseProductor());
         }   
        
		
		
		
         //----------------自定义字体大小-----------   
         //自定义设置字体颜色和大小 最简单的效果 多种字体随机显示   
         List<Font> fontList = new ArrayList<Font>();   
       fontList.add(new Font("Arial", Font.HANGING_BASELINE, 40));//可以设置斜体之类的   
         fontList.add(new Font("Courier", Font.BOLD, 40));       
//         DefaultWordRenderer dwr=new DefaultWordRenderer(Color.green,fontList);   
            
         //加入多种颜色后会随机显示 字体空心   
       List<Color> colorList=new ArrayList<Color>();   
       colorList.add(Color.blue); 
       ColoredEdgesWordRenderer dwr= new ColoredEdgesWordRenderer(colorList,fontList);   
            
         WordRenderer wr=dwr;   
         char[] numberChar = new char[] {'0','1','2', '3', '4', '5', '6', '7', '8','9','a', 'b', 'c', 'd',   
               'e', 'f', 'g', 'h', 'k', 'm', 'n', 'p', 'r', 'w', 'x', 'y' };
         
         builder.addText(new DefaultTextProducer(4,numberChar),wr);
       //--------------添加背景-------------   
         //设置背景渐进效果 以及颜色 form为开始颜色，to为结束颜色   
//         GradiatedBackgroundProducer gbp=new GradiatedBackgroundProducer();   
//         gbp.setFromColor(Color.yellow);   
//         gbp.setToColor(Color.red);   
         //无渐进效果，只是填充背景颜色   
       FlatColorBackgroundProducer  fbp=new FlatColorBackgroundProducer(Color.WHITE);   
         //加入网纹--一般不会用   
//       SquigglesBackgroundProducer  sbp=new SquigglesBackgroundProducer();   
         // 没发现有什么用,可能就是默认的   
//       TransparentBackgroundProducer tbp = new TransparentBackgroundProducer();   
         
            
         builder.addBackground(fbp);   
         //---------装饰字体---------------   
         // 字体边框齿轮效果 默认是3   
         builder.gimp(new BlockGimpyRenderer(1));   
         //波纹渲染 相当于加粗   
        builder.gimp(new RippleGimpyRenderer());   
         //修剪--一般不会用   
//       builder.gimp(new ShearGimpyRenderer(Color.red));   
         //加网--第一个参数是横线颜色，第二个参数是竖线颜色   
//       builder.gimp(new FishEyeGimpyRenderer(Color.red,Color.yellow));   
         //加入阴影效果 默认3，75    
         builder.gimp(new DropShadowGimpyRenderer());   
         //创建对象   
        Captcha captcha =  builder .build();   
            
        String codeuuid = UUID.randomUUID().toString();
        int validy = -1;
        Cookie cookie = WebUtils.cookie("RANDOM_VALIDATE_CODE", codeuuid,
        		validy);
        resp.addCookie(cookie);
        memCacheService.set("validCode_" + codeuuid, captcha.getAnswer());
        
     CaptchaServletUtil.writeImage(resp, captcha.getImage());   
     
     
     
//		req.getSession().setAttribute("captcha", captcha);   
  }   
}  

