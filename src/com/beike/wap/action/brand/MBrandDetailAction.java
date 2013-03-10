package com.beike.wap.action.brand;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.beike.wap.action.user.MBaseUserAction;
import com.beike.wap.entity.MCoupon;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MMerchant;
import com.beike.wap.service.MCouponDetailService;
import com.beike.wap.service.MGoodsService;
import com.beike.wap.service.MMerchantService;

/**
 * Title : BrandIndexAction
 * <p/>
 * Description :品牌信息Action
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
 * <pre>1     2011-09-23    lvjx            Created
 * 
 * <pre>
 * <p/>
 *
 * @author  k.w
 * @version 1.0.0.2011-09-23
 */
@Controller
@RequestMapping("/wap/brand/brandDetailController.do")
public class MBrandDetailAction extends MBaseUserAction {
	
	/** 日志 */
	private static Log log = LogFactory.getLog(MBrandDetailAction.class);
	
	@Autowired
	private MMerchantService mMerchantService;
	
	@Autowired
	private MCouponDetailService mCouponDetailService;
	
	@Resource(name = "wapGoodsService")
	private MGoodsService mGoodsService;
	
	@RequestMapping(params = "method=showBrandDetail")
	public Object showBrandDetailAction(HttpServletRequest request, HttpServletResponse response, ModelMap model)
	{
		super.setCookieUrl(request, response);
		String brand_str = request.getParameter("brandId");
		
		List<MGoods> goodsList = null;
		List<MCoupon> couponList = null;
		List<MMerchant> branchList = null;
		MMerchant brand = null;
		if(brand_str != null)
		{
			brand_str = brand_str.trim();
		}
		long brandId = 0;
		try{
			brandId = Long.parseLong(brand_str);
			brand = mMerchantService.getBrandById(brandId);
		}catch (Exception e) {
			log.info("query brand error, id is + " + brand_str);
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		
		try {
			goodsList = mGoodsService.queryGoodsByBrandId(brandId);
		} catch (Exception e) {
			log.info("query goods by brand id error, brand id is " + brandId);
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}

		try {
			couponList = mCouponDetailService.queryCouponByBrandId(brandId);
		} catch (Exception e) {
			log.info("query coupon by brand error, the brand id is "+ brandId);
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}

		try{
			branchList = mMerchantService.getBranchByBrandId(brandId);
		}catch (Exception e) {
			log.info("query branch by brand id error, the brand id is " + brandId);
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		
		if(goodsList != null && goodsList.size() != 0)
		{
			model.addAttribute("mFirstGoods", goodsList.remove(0));
		}else
		{
			model.addAttribute("mFirstGoods", null);
		}
		
		List<MMerchant> bList = new ArrayList<MMerchant>();
		String branchNum = null;
		if(branchList != null && branchList.size() > 5)
		{
			for(int i = 0; i < 5; i++)
			{
				bList.add(branchList.get(i));
			}
			branchNum = String.valueOf(branchList.size() - 5);
		}else
		{
			bList = branchList;
		}
		
		model.addAttribute("mBrand", brand);
		model.addAttribute("mGoodsList", goodsList);
		model.addAttribute("mCouponList", couponList);
		model.addAttribute("mBranchList", bList);
		model.addAttribute("branchNum", branchNum);
		
		return "wap/details/brandDetail";
	}
}
