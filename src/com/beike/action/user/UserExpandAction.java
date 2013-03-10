package com.beike.action.user;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.beike.entity.user.User;
import com.beike.entity.user.UserAddress;
import com.beike.entity.user.UserExpand;
import com.beike.service.sensitiveword.SensitivewordFilterService;
import com.beike.service.user.UserAddressService;
import com.beike.service.user.UserExpandService;
import com.beike.util.ImageZipUtil;
import com.beike.util.PicUtils;
import com.beike.util.StringUtils;
import com.beike.util.img.ImgUtil;
import com.beike.util.json.JsonUtil;
import com.beike.util.singletonlogin.SingletonLoginUtils;

/**      
 * project:beiker  
 * Title:用户扩展信息
 * Description:
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Mar 16, 2012 11:28:37 AM     
 * @version 1.0
 */
@Controller
public class UserExpandAction extends BaseUserAction{
	
	@Autowired
	private UserAddressService userAddressService;
	
	@Autowired
	private UserExpandService userExpandService;
	
	@RequestMapping("/userexpend/getUserExpandInfo.do")
	public String getUserExpandInfo(HttpServletRequest request,HttpServletResponse response) {
		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		try{
			if(user == null){
				response.getWriter().print("");
			}else{
				//用户扩展信息
				UserExpand userExpand = userExpandService.getUserExpandByUserId(user.getId());
				//用户地址
				List<UserAddress> lstAddress = userAddressService.getUserAddressByUserId(user.getId());
				
				Map<String,String> jsonMap = new HashMap<String,String>();
				if(userExpand!=null){
					jsonMap.put("nickname", userExpand.getNickName());
					jsonMap.put("realname", userExpand.getRealName());
					jsonMap.put("gender", String.valueOf(userExpand.getGender()));
					jsonMap.put("avatar", userExpand.getAvatar());
				}else{
					jsonMap.put("nickname", "");
					jsonMap.put("realname", "");
					jsonMap.put("gender", "");
					jsonMap.put("avatar", "");
				}
				if(lstAddress!=null && lstAddress.size()>0){
					UserAddress address = lstAddress.get(0);
					jsonMap.put("addressid", String.valueOf(address.getId()));
					jsonMap.put("province", address.getProvince());
					jsonMap.put("city", address.getCity());
					jsonMap.put("area", address.getArea());
					jsonMap.put("address", address.getAddress());
				}else{
					jsonMap.put("addressid", "0");
					jsonMap.put("province", "");
					jsonMap.put("city", "");
					jsonMap.put("area", "");
					jsonMap.put("address", "");
				}
				response.setContentType("text/json; charset=UTF-8");
				response.setHeader("progma", "no-cache");
				response.setHeader("Cache-Control", "no-cache");
				
				String text = JsonUtil.mapToJson(jsonMap);
				response.getWriter().print(text);
			}
		}catch(Exception e){
		}
		return null;
	}
	
	/**
	 * 保存用户扩展信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/userexpend/saveUserExpandInfo.do")
	public String saveUserExpandInfo(HttpServletRequest request,HttpServletResponse response) {
		String nickname = request.getParameter("nickname");
		String realname = request.getParameter("realname");
		String gender = request.getParameter("gender");
		String province = request.getParameter("province");
		String city = request.getParameter("addresscity");
		String area = request.getParameter("area");
		String address = request.getParameter("address");
		String addressid = request.getParameter("addressid");
		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if(user == null){
			return "redirect:/forward.do?param=login";
		}else{
			nickname = com.beike.util.StringUtils.getTxtWithoutHTMLElement(nickname);
			realname = com.beike.util.StringUtils.getTxtWithoutHTMLElement(realname);
			address = StringUtils.getTxtWithoutHTMLElement(address);
			//用户扩展信息
			UserExpand userExpand = new UserExpand();
			userExpand.setUserId(user.getId());
			userExpand.setNickName(nickname);
			userExpand.setRealName(realname);
			userExpand.setGender(Integer.parseInt(gender));
			
			//用户地址
			UserAddress userAddress = new UserAddress();
			userAddress.setUserid(user.getId());
			userAddress.setProvince(province);
			userAddress.setCity(city);
			userAddress.setArea(area);
			userAddress.setAddress(address);
			if(addressid==null || "".equals(addressid) || "0".equals(addressid)){
				userAddress.setId(null);
			}else{
				userAddress.setId(Long.parseLong(addressid));
			}
			userExpandService.updateUserExpand(userExpand,userAddress);
			
			request.setAttribute("saveret", true);
		}
		
		return "/user/useraccount";
	}
	
	/**
	 * 更换头像
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/userexpend/changeUserAvatar.do")
	public String changeUserAvatar(HttpServletRequest request,HttpServletResponse response) {
		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if(user == null){
			return "redirect:/forward.do?param=login";
		}else{
			//用户扩展信息
			UserExpand userExpand = userExpandService.getUserExpandByUserId(user.getId());
			request.setAttribute("userExpand",userExpand);
			return "/user/changeUserHeadIcon";
		}
	}
	
	/**
	 * ajax上传头像
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping("/userexpend/getNewUserAvatar.do")
	public String getNewUserAvatar(@RequestParam("qqfile") MultipartFile headIcon,
			HttpServletRequest request,HttpServletResponse response) {
        String saveResult = "";
        File picFile = null;
        try {
        	if(PicUtils.validatorPic(headIcon, 1024)){
	        	String filename = headIcon.getOriginalFilename();
	        	String imagepath="/jsp/headicon/";
	            String path = request.getRealPath(imagepath);
	            String prizePicStr = StringUtils.formatPicName(filename);
	            ImageZipUtil.resizeImage(headIcon.getInputStream(),path,prizePicStr,260,260);
	            
	            picFile = new File(path + System.getProperties().getProperty("file.separator") + prizePicStr);
	            String imgPath = "";
	            String imaName = "";
	            Map<String,Object> mapRet = ImgUtil.sendImg(imagepath, picFile);
				if(mapRet!=null){
					imgPath = mapRet.get("fileUrl").toString();
					imaName = mapRet.get("fileName").toString();
				}else{
					imgPath = "";
					imaName = "";
				}
	            saveResult = "{\"success\": \"true\",\"filename\":\"" + imaName + "\",\"filepath\":\"" + imgPath+ "\"}";
            }else{
            	saveResult = "{\"success\": \"false\",\"error\":\"图片格式不符合要求或图片超过1M!\"}";
            }
        } catch (FileNotFoundException ex) {
            saveResult = "{\"success\": \"false\",\"error\":\"文件不存在!\"}";
        } catch (IOException ex) {
            saveResult = "{\"success\": \"false\",\"error\":\"上传失败，请重试!\"}";
        }catch(Exception ex){
        	saveResult = "{\"success\": \"false\",\"error\":\"图片格式不符合要求或图片超过1M!\"}";
		}finally{
			if(picFile != null){
				picFile.delete();
			}
		}
        request.setAttribute("saveResult", saveResult);
		return "/user/saveHeadIconResult";
	}
	
	/**
	 * 保存用户头像
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/userexpend/saveUserAvatar.do")
	public String saveUserAvatar(HttpServletRequest request,HttpServletResponse response) {
		// 判断用户是否登录
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if(user == null){
			return "redirect:/forward.do?param=login";
		}else{
			String x1 = request.getParameter("x1");
			String y1 = request.getParameter("y1");
			String width1 = request.getParameter("w");
			String height1 = request.getParameter("h");
			String imgName = request.getParameter("imgname");
			String imgPath = request.getParameter("imgpath");
			File headImgFile = null;
	        try{
	        	int startX = Integer.parseInt(x1);
	        	int startY = Integer.parseInt(y1);
	        	int width = Integer.parseInt(width1);  
	        	int height = Integer.parseInt(height1);
	        	String imagepath="/jsp/headicon/";
	        	
	            String path = request.getRealPath(imagepath);
	            
	            headImgFile = ImgUtil.getImg(imgPath, path + System.getProperties().getProperty("file.separator") + imgName);
	            BufferedImage bufferedImage = null;
	        	bufferedImage = ImageIO.read(headImgFile);
              
	        	int realwidth = bufferedImage.getWidth();
	        	int realheight = bufferedImage.getHeight();
	        	
	        	int endX = startX + width - 1;
	        	int endY = startY + height - 1;
	        	
	        	if (startX == -1) {
	        		startX = 0;
	        	}

	        	if (startY == -1) {
	        		startY = 0;
	        	}

	        	if (endX == -1) {
	        		endX = realwidth - 1;
	        	}

	        	if (endY == -1) {
	        		endY = realheight - 1;
	        	}

	        	BufferedImage result = new BufferedImage(endX - startX + 1, endY - startY + 1, 4);
	        	for (int y = startY; y < endY; y++) {
	        		for (int x = startX; x < endX; x++) {
	        			int rgb = bufferedImage.getRGB(x, y);
	        			result.setRGB(x - startX, y - startY, rgb);
	        		}
	        	}
	            //新图片名称
	            String newImgName = StringUtils.formatPicName(imgName);
	            String endName = imgName.substring(imgName.lastIndexOf(".")+1);
	            
	            //将图片保存服务器
	            File picFile = new File(path + System.getProperties().getProperty("file.separator") + newImgName);
	            String headIconPath = "";
	            //保存新图片 
	            ImageIO.write(result, endName, picFile);
            
				try{
					Map<String,Object> mapRet = ImgUtil.sendImg(imagepath, picFile,"120_120");
					if(mapRet!=null){
						headIconPath = mapRet.get("fileUrl_120_120").toString();
					}else{
						headIconPath = "";
					}
				}catch(Exception ex){
					return "";
				}finally{
					picFile.delete();
				}
	            
				//保存头像
				UserExpand userExpand = new UserExpand();
				userExpand.setUserId(user.getId());
				userExpand.setAvatar(headIconPath);
				userExpandService.updateUserAvatar(userExpand);
				
				request.setAttribute("SAVE_HEADICON_SUCCESS", true);
	        } catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("SAVE_HEADICON_SUCCESS", false);
			}finally{
				if(headImgFile!=null){
					headImgFile.delete();
				}
	        }
		}
		return "/user/useraccount";
	}
	
	/**
	 * 
	 * @date Apr 24, 2012
	 * @description 是否含有敏感词
	 * @param word
	 * @return
	 * @throws
	 */
	@RequestMapping("/userexpend/containsSensitiveWord.do")
	public String containsSersitiveWord(HttpServletRequest request,HttpServletResponse response){
		String word = request.getParameter("word");
		String flag = "1";
		try {
			if(StringUtils.validNull(word)){
				if(SensitivewordFilterService.getSingletonInstance().containsWord(word)){
					flag = "0";
				}
			}
			response.setContentType("text/json; charset=UTF-8");
			response.setHeader("progma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			
			response.getWriter().write(flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}