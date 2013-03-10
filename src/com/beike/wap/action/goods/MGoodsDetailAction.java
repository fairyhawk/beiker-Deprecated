package com.beike.wap.action.goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.util.Constant;
import com.beike.util.RandomNumberUtils;
import com.beike.util.StringUtils;
import com.beike.util.WebUtils;
import com.beike.util.htmlparse.MParseGoodsDetail;
import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.entity.MGoods;
import com.beike.wap.service.MGoodsService;
import com.beike.wap.service.MMerchantService;

/**
 * Title : GoodsAction
 * <p/>
 * Description :商品信息Action
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com </P> JDK Version Used : JDK 5.0 +
 * <p/>
 * Modification History :
 * <p/>
 * 
 * <pre>
 * NO.    Date    Modified By    Why & What is modified
 * </pre>
 * 
 * <pre>1     2011-06-14    lvjx            Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-14
 */
@Controller
@RequestMapping("/wap/goods/goodsDetailController.do")
public class MGoodsDetailAction extends MBaseUserAction {

	@RequestMapping(params = "method=queryDetailShowMes")
	public ModelAndView queryGoodsShowMes(
			HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
		super.setCookieUrl(request, response);
		try {
			String goodsId = request.getParameter("goodsIds");
			int goodsIdInt = 0;
			if(StringUtils.validNull(goodsId)){
				goodsIdInt = Integer.parseInt(goodsId);
			}
			MGoods mGoods = goodsService.queryDetailShowMes(goodsIdInt);
			MGoods merchantInfo = goodsService.getMerchantById(goodsIdInt);
			if (null != merchantInfo
					&& StringUtils.validNull(merchantInfo.getMerchantid())) {
				// 品牌LOGO
				String merchantLogo = merchantService.getGoodsMerchantLogo(Long
						.valueOf(merchantInfo.getMerchantid()));
				// 平均得分
				String evaluationscore = merchantService
						.getAvgEvationScores(Long.valueOf(merchantInfo
								.getMerchantid()));
				modelMap.addAttribute("merchantLogo", merchantLogo);
				if(!StringUtils.validNull(evaluationscore)){
					evaluationscore = "0.0";
				}
				int resultScore = RandomNumberUtils.mulScore(evaluationscore);
				modelMap.addAttribute("evaluationscore", resultScore);
			}
			List<MGoods> branchList = goodsService.getBranchById(goodsIdInt);
			List<MGoods> branchInfoList = new ArrayList<MGoods>();
			if(null!=branchList){
				if(branchList.size()>5){
					for(int i=0;i<5;i++){
						branchInfoList.add(branchList.get(i));
					}
					modelMap.addAttribute("branchSum", String.valueOf(branchList.size()-5));
				}else{
					branchInfoList.addAll(branchList);
				}
			}
			modelMap.addAttribute("UPLOAD_IMAGES_URL",
					Constant.UPLOAD_IMAGES_URL);
			modelMap.addAttribute("mGoods", mGoods);
			modelMap.addAttribute("merchantInfo", merchantInfo);
			modelMap.addAttribute("branchList", branchInfoList);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		ModelAndView modelAndView = new ModelAndView("wap/details/goods");//
		return modelAndView;
	}

	@RequestMapping(params = "method=queryGoodsDetailMes")
	public ModelAndView queryGoodsDetailMes(
			HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
		try {
			String goodsId = request.getParameter("goodsIds");
			int goodsIdInt = 0;
			if(StringUtils.validNull(goodsId)){
				goodsIdInt = Integer.parseInt(goodsId);
			}
			
			String tn = request.getParameter("tn");
			String baiduid = request.getParameter("baiduid");
			if (tn != null && !"".equals(tn) && tn.startsWith("baidutuan_") && baiduid != null && !"".equals(baiduid)) {
				Cookie cookie = WebUtils.cookie("BAIDU_REFERER_PARAM", tn + "|" + baiduid + "|" + goodsId, -1);
				response.addCookie(cookie);
			}
			
			MGoods mGoods = goodsService.queryDetailShowMes(goodsIdInt);
			Map<String,String> infoMap = MParseGoodsDetail.getGoodsIds(String.valueOf(goodsIdInt));
			modelMap.addAttribute("infoMap", infoMap);
			modelMap.addAttribute("mGoods", mGoods);
			modelMap.addAttribute("goodsId", goodsId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		ModelAndView modelAndView = new ModelAndView("wap/details/goodsDetail");
		return modelAndView;
	}

	@Resource(name = "wapGoodsService")
	private MGoodsService goodsService;

	@Resource(name = "mMerchantService")
	private MMerchantService merchantService;
}
