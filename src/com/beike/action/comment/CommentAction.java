package com.beike.action.comment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.beike.action.user.BaseUserAction;
import com.beike.biz.service.trx.OrderFilmService;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.entity.goods.Goods;
import com.beike.entity.merchant.BranchProfile;
import com.beike.entity.user.User;
import com.beike.form.MerchantForm;
import com.beike.form.OrderEvaluationForm;
import com.beike.page.Pager;
import com.beike.page.PagerHelper;
import com.beike.service.comment.CommentService;
import com.beike.service.goods.GoodsService;
import com.beike.service.merchant.ShopsBaoService;
import com.beike.util.BeanUtils;
import com.beike.util.Constant;
import com.beike.util.ImageZipUtil;
import com.beike.util.NullDigitalCheck;
import com.beike.util.PicUtils;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;
import com.beike.util.img.ImgUtil;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;

@Controller
public class CommentAction extends BaseUserAction {

	@Autowired
	private CommentService commentService;
	@Autowired
	private ShopsBaoService shopsBaoService;

	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	@Autowired
	private OrderFilmService orderFilmService;
	
	private static Log log = LogFactory.getLog(CommentAction.class);
	
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();

	/**
	 * 评论图片预览
	 * @param request
	 * @param response
	 * @return
	 */
	/**
	 * ajax上传头像
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping("/comment/uploadphoto.do")
	public String getNewUserAvatar(@RequestParam("qqfile") MultipartFile photo,
			HttpServletRequest request,HttpServletResponse response) {
        String saveResult = "";
        File picFile = null;
        String imagepath="/jsp/commentphoto/";
        try {
        	String filename = photo.getOriginalFilename();
            String path = request.getRealPath(imagepath);
            String prizePicStr = StringUtils.formatPicName(filename);
            //图片大小5m
            if(PicUtils.validatorPic(photo, 5*1024)){
            	ImageZipUtil.resizeImage(photo.getInputStream(),path,prizePicStr,600,600);
                
                picFile = new File(path + System.getProperties().getProperty("file.separator") + prizePicStr);
                String imgPath = "";
                String imaName = "";
                Map<String,Object> mapRet = ImgUtil.sendImg(imagepath, picFile);
    			if(mapRet!=null){
    				imgPath = mapRet.get("fileUrl").toString();
    				imaName = mapRet.get("fileName").toString();
    			}else{
    				imgPath = "";
    			}
                saveResult = "{\"success\": \"true\",\"filename\":\"" + imaName + "\",\"filepath\":\"" + imgPath+ "\"}";
            }else{
            	 saveResult = "{\"success\": \"false\",\"error\":\"文件大小不能超过5M!\"}";
            	 request.setAttribute("saveResult", saveResult);
         		return "/user/saveHeadIconResult";
            }
            
        } catch (FileNotFoundException ex) {
            saveResult = "{\"success\": \"false\",\"error\":\"文件不存在!\"}";
        } catch (IOException ex) {
            saveResult = "{\"success\": \"false\",\"error\":\"上传失败，请重试!\"}";
        }catch(Exception ex){
			return saveResult;
		}finally{
			try {
				picFile.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				saveResult = "{\"success\": \"false\",\"error\":\"上传失败，请重试!\"}";
			}
		}
        request.setAttribute("saveResult", saveResult);
		return "/user/saveHeadIconResult";
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return 从订单中心跳转到评论页面 10:07:20 AM janwen
	 * 
	 */
	@RequestMapping("/comment/gotocomment.do")
	public String gotoCommentPage(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			User user = SingletonLoginUtils.getMemcacheUser(request);
			//0 为普通订单  1为点菜   2为网票网订单 add by ljp 20121218
			String bizType = request.getParameter("bizType");
			if("2".equals(bizType)){
				throw new IllegalArgumentException();
			}
			String goodsid = request.getParameter("goodsid");
			String trx_order_id = request.getParameter("trx_order_id");
			// 订单id
			String id = request.getParameter("id");
			// 批量处理
			String batchStr = request.getParameter("batch");
			List<Object> digitalParams = new ArrayList<Object>();
			List<Object> nullParams = new ArrayList<Object>();
			digitalParams.add(goodsid);
			digitalParams.add(trx_order_id);
			nullParams.add(goodsid);
			nullParams.add(trx_order_id);
			nullParams.add(batchStr);
			boolean batch = batchStr.equals("yes") ? true : false;
			if (!batch) {
				nullParams.add(id);
				digitalParams.add(id);
			}
			if (user == null || !NullDigitalCheck.checkParamNull(nullParams)
					|| !NullDigitalCheck.checkDigital(digitalParams)) {
				return "redirect:/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNCOMMENT";
			}
			MerchantForm mf = shopsBaoService.getCommMerchantDetail(goodsid, trx_order_id);
			Map map = commentService.gotoCommentPage(batch, new Long(
					trx_order_id), new Long(id), new Long(goodsid));
			if (map == null) {
				return "redirect:/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNCOMMENT";
			}
			Goods goods = goodsService.findById(new Long(goodsid));
			if(goods.getIsMenu()==1){
				Long subGuestId = trxorderGoodsService.findById(Long.parseLong(id)).getSubGuestId();
				request.setAttribute("subGuestId", subGuestId);
			}
			if(goods.getIsMenu()==2){
				Long filmId=orderFilmService.queryCinemaIdByTrxGoodsId(Long.parseLong(id));
				request.setAttribute("filmId", filmId);
			}
			
			request.setAttribute("info", map);
			request.setAttribute("trx_order_id", trx_order_id);
			request.setAttribute("brandinfo", mf);
			request.setAttribute("goods",goods);
			request.setAttribute("batch", batchStr);
			request.setAttribute("id", id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/comment/addComment";
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return 添加评论 10:06:50 AM janwen
	 * 
	 */

	@RequestMapping("/comment/addcomment.do")
	public String addComment(HttpServletRequest request,
			HttpServletResponse response) {
		
		List<Object> digitalParams = new ArrayList<Object>();
		List<Object> nullparams = new ArrayList<Object>();
		String goodsid = request.getParameter("goodsid");
		String merchantid = request.getParameter("merchantid");
		String trx_order_id = request.getParameter("trx_order_id");
		try {
		// 订单id
		String id = request.getParameter("id");
		String comment = request.getParameter("comment");
		String phototurls = request.getParameter("photourls");
		// 很好、满意、差 best,better,bad
		String rate = request.getParameter("rate");
		String batchStr = request.getParameter("batch");
		digitalParams.add(goodsid);
		digitalParams.add(merchantid);
		digitalParams.add(trx_order_id);

		nullparams.add(goodsid);
		nullparams.add(merchantid);
		nullparams.add(trx_order_id);
		nullparams.add(comment);
		nullparams.add(rate);
		nullparams.add(batchStr);
   
		User user = SingletonLoginUtils.getMemcacheUser(request);
		boolean batch = batchStr.equals("yes") ? true : false;
		if (!batch) {
			digitalParams.add(id);
			nullparams.add(id);
		}

	
			if (user == null || !NullDigitalCheck.checkParamNull(nullparams)
					|| !NullDigitalCheck.checkDigital(digitalParams)) {
				return "redirect:/forward.do?param=index.index";
			}
			// 图片上传处理
			List<String> urlList = null;
			if(phototurls != null && !"".equals(phototurls)){
				String[] urls = phototurls.split(",");
				urlList = Arrays.asList(urls);
			}
			
			boolean return_result = false;

			// 订单合法性校验
			if (commentService.isvalid(new Long(trx_order_id),
					new Long(goodsid), user.getId())) {
		/**判断是否点餐虚拟商品，获取评价推荐商品Id start*/
				Goods goodsInfo = goodsService.findById(Long.parseLong(goodsid));
				List<Long> recGoodsList = new ArrayList<Long>();
				if(goodsInfo.getIsMenu()==1||goodsInfo.getIsMenu()==2){
					Map<String, Long> mapCity = (Map<String, Long>) memCacheService.get("CITY_CATLOG");
					if (mapCity == null) {
						mapCity = BeanUtils.getCity(request, "regionCatlogDao");
						memCacheService.set("CITY_CATLOG", mapCity);
					}
					String city = CityUtils.getCity(request, response);
					if (city == null || "".equals(city)) {
						city = "beijing";
					}
					Long cityid = null;
					if (mapCity != null) {
						cityid = mapCity.get(city.trim());
					}
					Long recommendCatlogId=10100L;
					if(goodsInfo.getIsMenu()==2){
						recommendCatlogId=10200L;
					}
					
					recGoodsList = goodsService.getRecommendGoodsIds(null, null, recommendCatlogId, cityid, 4L, null);
				}else{
					recGoodsList = commentService.getRecGoodsid(new Long(goodsid));
				}
		/**推荐商品end*/		
				if (rate.equals(NullDigitalCheck.RATE_BEST_ENUM)) {
					return_result = commentService.addComment(0, Long
							.parseLong(id), batch, 0, comment, Long
							.parseLong(merchantid), user.getId(), Long
							.parseLong(goodsid), Long.parseLong(trx_order_id),
							urlList, 1, 0, 0);
				} else if (rate.equals(NullDigitalCheck.RATE_BETTER_ENUM)) {
					return_result = commentService.addComment(0, Long
							.parseLong(id), batch, 1, comment, Long
							.parseLong(merchantid), user.getId(), Long
							.parseLong(goodsid), Long.parseLong(trx_order_id),
							urlList, 0, 1, 0);
				} else if (rate.equals(NullDigitalCheck.RATE_BAD_ENUM)) {
					return_result = commentService.addComment(0, Long
							.parseLong(id), batch, 2, comment, Long
							.parseLong(merchantid), user.getId(), Long
							.parseLong(goodsid), Long.parseLong(trx_order_id),
							urlList, 0, 0, 1);
				} else {
					request.setAttribute("lstTuijianGoodsForm", goodsService.getGoodsFormByChildId(recGoodsList));
					return "/comment/commentError";
				}
				if (return_result) {
					
					request.setAttribute("lstTuijianGoodsForm", goodsService.getGoodsFormByChildId(recGoodsList));
					
					//刷新消息缓存 add by qiaowb 2012-05-03
					String msgQueue = WebUtils.getCookieValue("USER_MSG_QUEUE", request);
					//创建cookie
					if(msgQueue != null && (msgQueue.startsWith("1_") && user!=null)){
						String[] aryMsgQueue = org.apache.commons.lang.StringUtils.split(msgQueue,"|");
						if(aryMsgQueue!=null && aryMsgQueue.length>0 && aryMsgQueue[0].startsWith("1_")){
							int unCommentCount = trxorderGoodsService.findCountByUserId(user.getId(),Constant.TRX_GOODS_UNCOMMENT);
							if(unCommentCount>0){
								aryMsgQueue[0] = "1_" + unCommentCount;
							}else{
								aryMsgQueue[0] = "";
							}
						}
						StringBuilder bufMsg = new StringBuilder();
						for(String tempMsg : aryMsgQueue){
							if(org.apache.commons.lang.StringUtils.isNotEmpty(tempMsg)){
								bufMsg.append(tempMsg).append("|");
							}
						}
						msgQueue = bufMsg.toString();
						if(!"".equals(msgQueue)){
							msgQueue = msgQueue.substring(0, msgQueue.length()-1);
						}else{
							msgQueue = "|";
						}
						Cookie cookie = WebUtils.cookie("USER_MSG_QUEUE", msgQueue, 60 * 60 * 24);
						response.addCookie(cookie);
					}
					return "/comment/commentSuccess";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "/comment/commentError";
		}
		request.setAttribute("lstTuijianGoodsForm", goodsService
				.getGoodsFormByChildId(commentService.getRecGoodsid(new Long(
						goodsid))));
		return "/comment/commentError";
	}

	/**
	 * 查询品牌评价信息
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/evaluate/getMerchantEvaluate.do")
	public Object getMerchantEvalutateInfo(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {

		super.setCookieUrl(request, response);

		String merchantid = request.getParameter("merchantid");

		if (merchantid.equals("")) {
			request.setAttribute("ERRMSG", "没有找到商家!");
			return new ModelAndView("redirect:../404.html");
		}

		User user = SingletonLoginUtils.getMemcacheUser(request);
		Long merID = Long.parseLong(merchantid);
		List<OrderEvaluationForm> merchantEvaluate = null;
		MerchantForm        	  merchantProfile  = null;

		// 当前页
		String currentPage = request.getParameter("cpage");
		if (currentPage == null || "".equals(currentPage)) {
			currentPage = "1";
		}
		
		String score = request.getParameter("score");
		//非0-2之间的数,默认查询所有的评价信息 0很好1满意2差
		int thescore = -1;
		if(score != null && score != ""){
			try{
				thescore = Integer.parseInt(score);
			}catch(NumberFormatException e){
				e.printStackTrace();
				request.setAttribute("ERRMSG", "没有找到相关评价!");
				return new ModelAndView("redirect:../404.html");
			}
		}
		
		Long userId = 0l;
		if(user!=null){
			userId = user.getId();
		}
		int totalCount = commentService.getEvaluateMerchantCount(userId, merID,thescore);
		Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),
				totalCount, 5);
		List<Long> merlist = commentService.getEvaluateMerchantID(userId, merID,
				pager,thescore);

		if (merlist == null)
			return null;
		
		//商家评价信息
		merchantEvaluate= commentService.getEvaluationInfoByIds(merlist);
		
		//商家属性信息	
		merchantProfile = shopsBaoService.getMerchantDetailById(merID);
		merchantProfile.setId(merID.toString());//商家Id
		
		request.setAttribute("merchantEvaluate",merchantEvaluate);
		request.setAttribute("merchantprofile",merchantProfile);
		request.setAttribute("pager",pager);

		
		return "/brand/merchantComment";

	}
	/**
	 * 查询分店评价信息
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/evaluate/getBrandEvaluate.do")
	public Object getBrandEvalutateInfo(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {

		super.setCookieUrl(request, response);

		String brandid = request.getParameter("brandid");

		if (brandid.equals("")) {
			request.setAttribute("ERRMSG", "没有找到分店!");
			return new ModelAndView("redirect:../404.html");
		}

		User user = SingletonLoginUtils.getMemcacheUser(request);
		Long bid = Long.parseLong(brandid);

		// 当前页
		String currentPage = request.getParameter("cpage");
		List<OrderEvaluationForm> brandForm = null;
		BranchProfile         branchProfile = null;

		if (currentPage == null || "".equals(currentPage)) {
			currentPage = "1";
		}
		
		String score = request.getParameter("score");
		//非0-2之间的数,默认查询所有的评价信息 0很好1满意2差
		int thescore = -1;
		if(score != null && score != ""){
			try{
				thescore = Integer.parseInt(score);
			}catch(NumberFormatException e){
				e.printStackTrace();
				request.setAttribute("ERRMSG", "没有找到相关评价!");
				return new ModelAndView("redirect:../404.html");
			}
		}
		Long userId = 0l;
		if(user!=null){
			userId = user.getId();
		}
		int totalCount = commentService.getEvaluateBrandCount(userId, bid, thescore);
		Pager pager = PagerHelper.getPager(Integer.parseInt(currentPage),
				totalCount, 5);
		List<Long> brandlist = commentService.getEvaluateBrandID(userId, bid,pager,thescore);

		if (brandlist == null)
			return null;

		brandForm     = commentService.getEvaluationInfoByIds(brandlist);
		branchProfile = commentService.getAllEvaluateForBrand(bid);
		request.setAttribute("brandlist", brandForm);
		request.setAttribute("brandProfile", branchProfile);
		request.setAttribute("pager", pager);
		return "/brand/brandComment";
	}
}
