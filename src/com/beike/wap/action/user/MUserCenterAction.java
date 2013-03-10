package com.beike.wap.action.user;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.beike.action.pay.PurseAction;
import com.beike.action.pay.hessianclient.TrxHessianServiceGateWay;
import com.beike.common.enums.trx.TrxStatus;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.entity.user.User;
import com.beike.util.Constant;
import com.beike.util.RandomNumberUtils;
import com.beike.util.RequestUtil;
import com.beike.util.StringUtils;
import com.beike.util.singletonlogin.SingletonLoginUtils;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MMerchantEvaluation;
import com.beike.wap.entity.MTrxorderGoods;
import com.beike.wap.entity.MVoucher;
import com.beike.wap.service.MGoodsService;
import com.beike.wap.service.MMerchantEvaluationService;
import com.beike.wap.service.MTrxorderGoodsService;
import com.beike.wap.service.MVoucherService;

/**
 * 我的千品用户中心action
 * @author k.w
 *
 */
@Controller
@RequestMapping("/wap/mUserCenter.do")
public class MUserCenterAction extends MBaseUserAction {

	private static Log logger = LogFactory.getLog(PurseAction.class);

	@Autowired
	private MTrxorderGoodsService mTrxorderGoodsService;
	
	@Autowired
	private MVoucherService mVoucherService;
	
	@Autowired
	private MMerchantEvaluationService mMerchantEvaluationService;
	
	@Autowired
	private MGoodsService mGoodsService;

	@Resource(name = "wapClient.trxHessianServiceGateWay")
	private TrxHessianServiceGateWay trxHessianServiceGateWay;
	
	@Autowired
	private TrxorderGoodsService trxorderGoodsService;
	
	@RequestMapping(params = "method=showUserCenter")
	public Object showUserCenter(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
	{
		super.setCookieUrl(request, response);
		Constant.CLICK_STATE = Constant.WAP_MY_QIANPIN;
		ModelAndView mav = new ModelAndView("/wap/ucenter/myOrder");
		try {
			User user = getMemcacheUser(request);
			if (null == user) {
				return new ModelAndView("redirect:/wap/user/toUserLogin.do");
			}
			
			int currentPage = RequestUtil.getInt(request, "currentPage", 0);
			int pageSize = 5;
			String qryType = request.getParameter("qryType");

			int startPage = currentPage * 5;
			
			if(!StringUtils.validNull(qryType))
			{
				qryType = "TRX_GOODS_UNUSEED";
			}
			
			// TODO mem中取
			Long userId = 0L;
			if (user != null) {
				if (user.getId() != 0) {
					userId = user.getId();
				}
			}
			// 跟据类型查出总记录数
			int totalNums = mTrxorderGoodsService.getRecordNum(userId, Constant.TRX_GOODS_ALL);
			
			int unCommentNums = mTrxorderGoodsService.getRecordNum(userId, Constant.TRX_GOODS_UNCOMMENT);
			int unUseedNums = mTrxorderGoodsService.getRecordNum(userId, Constant.TRX_GOODS_UNUSEED);
			int pageNums = 0;
			int pSize = RandomNumberUtils.calculatePage(totalNums, 5);
			
			if(qryType.equals(Constant.TRX_GOODS_ALL))
			{
				pageNums = totalNums;
			}else if(qryType.equals(Constant.TRX_GOODS_UNCOMMENT))
			{
				pageNums = unCommentNums;
				pageSize = 10000;
				startPage = 0;
			}else if(qryType.equals(Constant.TRX_GOODS_UNUSEED))
			{
				pageNums = unUseedNums;
				pageSize = 10000;
				startPage = 0;
			}else
			{
				pageNums = 0;
				pageSize = 10000;
				startPage = 0;
			}
			
			if(pageSize == 0)
			{
				pageSize = 5;
			}
			
			List<MTrxorderGoods> trxOrderGoodsList = mTrxorderGoodsService
					.getTrxOrderInfo(userId, startPage, pageSize, qryType);
			
			modelMap.addAttribute("pageSize", pSize);
			modelMap.addAttribute("currentPage",currentPage);
			modelMap.addAttribute("resultList", trxOrderGoodsList);
			modelMap.addAttribute("unUsedCount", unUseedNums);
			modelMap.addAttribute("unCommentCount", unCommentNums);
			modelMap.addAttribute("user", user);
			modelMap.addAttribute("totalRow", totalNums);
			modelMap.addAttribute("qryType", qryType);
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(":" + e.getStackTrace());
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
	}
	
	@RequestMapping(params = "method=showUserInfo")
	public Object showUserInfo(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
	{
		ModelAndView mav = new ModelAndView("/wap/ucenter/myInfo");
		User user = SingletonLoginUtils.getMemcacheUser(request);
		if (user == null) {
			return "redirect:/wap/user/toUserLogin.do";
		}
		try {
			// TODO 调用内部余额查询接口
			Map<String, String> hessianMap = new HashMap<String, String>();
			hessianMap.put("userId", String.valueOf(user.getId()));
			hessianMap.put("reqChannel","WAP");
			Map<String, String> rspMap = trxHessianServiceGateWay
			.getActByUserId(hessianMap);
			if (null == rspMap) {
				modelMap.addAttribute("error", "账户余额获取失败！");
				return new ModelAndView("wap/buy/buyStep1");
			}
			if (!"1".equals(rspMap.get("rspCode"))) {
				modelMap.addAttribute("ERRMSG", "账户余额获取异常！");
				return new ModelAndView("wap/buy/buyStep1");
			}
			String balanceAmount = rspMap.get("balance");
			modelMap.addAttribute("balance", balanceAmount);
			modelMap.addAttribute("mobile", user.getMobile());
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
	}
	
	@RequestMapping(params = "method=showOrderDetail")
	public Object showOrderDetailAction(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
	{
		super.setCookieUrl(request, response);
		String tempId = request.getParameter("id");
		User user = getMemcacheUser(request);
		if (null == user) {
			return new ModelAndView("redirect:/wap/user/toUserLogin.do");
		}
		Long trxId = 0L;
		try {
			trxId = Long.parseLong(tempId);
		} catch (Exception e) {
			logger.info("trxorder_goods id Error, id is " + tempId);
			e.printStackTrace();
			new ModelAndView("redirect:/wap/mUserCenter.do?method=showUserCenter");
		}
		
		boolean uv = validateUserWithTrxorderId(trxId, request);
		if(!uv)
		{
			logger.info("the order where trxorder_id  = " + trxId + " can't be seen by this user");
			new ModelAndView("redirect:/wap/mUserCenter.do?method=showUserCenter");
		}
		
		MTrxorderGoods trxorderGoods = mTrxorderGoodsService.findById(trxId);
		TrxStatus status = trxorderGoods.getTrxStatus();
		if(status.equals(TrxStatus.SUCCESS))
		{
			// 尚未使用
			return new ModelAndView("redirect:/wap/mUserCenter.do?method=unUseGood&id="+trxId);
		}
		else if(status.equals(TrxStatus.USED))
		{
			return new ModelAndView("redirect:/wap/mUserCenter.do?method=showComment&id="+trxId);
			// TODO 未评价
		}else if(status.equals(TrxStatus.RECHECK) || status.equals(TrxStatus.REFUNDACCEPT)
				|| status.equals(TrxStatus.REFUNDTOACT) || status.equals(TrxStatus.REFUNDTOBANK))
		{
			// TODO 退款
			return new ModelAndView("redirect:/wap/mUserCenter.do?method=refundGood&id="+trxId+"&status="+status);
		}else if(status.equals(TrxStatus.COMMENTED))
		{
			// 已评价、已完成
			return new ModelAndView("redirect:/wap/mUserCenter.do?method=showUsedGoods&id="+trxId);
		}
		else
		{
			logger.info("--------------show order detail error ,trxorder goods status is "+status+" ,trxorder goods id is "+trxId);
			return new ModelAndView("redirect:/wap/mUserCenter.do?method=showUserCenter&id="+trxId);
		}
	}
	
	@RequestMapping(params = "method=showComment")
	public Object showComment(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
	{
		ModelAndView mav = new ModelAndView("/wap/ucenter/showComment");
		String tempId = request.getParameter("id");
		Long trxId = 0L;
		try {
			trxId = Long.parseLong(tempId);
			boolean uv = validateUserWithTrxorderId(trxId, request);
			if(!uv)
			{
				logger.info("the order where trxorder_id  = " + trxId + " can't be seen by this user");
				return new ModelAndView("redirect:/wap/mUserCenter.do?method=showUserCenter");
			}
			MTrxorderGoods trxorderGoods = mTrxorderGoodsService.findById(trxId);
			MGoods goods = mGoodsService.findById(trxorderGoods.getGoodsId());
			modelMap.addAttribute("trxorderGoods", trxorderGoods);
			modelMap.addAttribute("goods", goods);
			return mav;
		} catch (Exception e) {
			logger.info("trxorder_goods id Error, id is " + tempId);
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		
	}
	
	@RequestMapping(params = "method=addComment")
	public Object addComment(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception
	{
		int taste_ev = Integer.parseInt(request.getParameter("taste_ev"));
		int environment_ev = Integer.parseInt(request.getParameter("environment_ev"));
		int service_ev = Integer.parseInt(request.getParameter("service_ev"));
		int syn_ev = Integer.parseInt(request.getParameter("syn_ev"));
		
		double commentPoint = (taste_ev + environment_ev + service_ev + syn_ev) * 0.25;
		
		String trxGoodsId = request.getParameter("trxGoodsId");
		Long trxGoodsIdLong = new Long(trxGoodsId);
		ModelAndView mav = new ModelAndView("redirect:/wap/mUserCenter.do?method=showUsedGoods&id="+trxGoodsId);
		User user = SingletonLoginUtils.getMemcacheUser(request);
		Long userId = 0L;
		if (user != null) {
			if (user.getId() != 0) {
				userId = user.getId();
			}
		}
		
		try {
			// 用户鉴权
			boolean verifyResult = trxorderGoodsService.verifyBelong(trxGoodsIdLong, userId,true);
			if (verifyResult) {

				Map<String, String> rspMap = trxorderGoodsService.addComment(
						trxGoodsIdLong, userId, commentPoint, "");
				if (rspMap != null) {

					response.getWriter().write(rspMap.get("result"));
					return mav;
				}
			}
			else
			{
				logger.info("the order where trxorder_id  = " + trxGoodsIdLong + " can't be seen by this user");
				return new ModelAndView("redirect:/wap/mUserCenter.do?method=showUserCenter");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("+++++++++Trx-Exception:+++" + e);
			throw new Exception();
		}
		
		return mav;
	}
	
	@RequestMapping(params = "method=showUsedGoods")
	public Object showUsedGoods(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap)
	{
		ModelAndView mav = new ModelAndView("/wap/ucenter/showUsedOrder");
		String tempId = request.getParameter("id");
		Long trxId = 0L;
		try {
			trxId = Long.parseLong(tempId);
			boolean uv = validateUserWithTrxorderId(trxId, request);
			if(!uv)
			{
				logger.info("the order where trxorder_id  = " + trxId + " can't be seen by this user");
				return new ModelAndView("redirect:/wap/mUserCenter.do?method=showUserCenter");
			}
			MTrxorderGoods trxorderGoods = mTrxorderGoodsService.findById(trxId);
			MGoods goods = mGoodsService.findById(trxorderGoods.getGoodsId());
			
			MVoucher voucher = mVoucherService.findById(trxorderGoods.getVoucherId());
			MMerchantEvaluation me = mMerchantEvaluationService.findByTrxorderId(trxId);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String endDate = df.format(voucher.getConfirm_date());
			
			modelMap.addAttribute("score", me.getEvaluationscore()+"");
			modelMap.addAttribute("endDate", endDate);
			modelMap.addAttribute("trxorderGoods", trxorderGoods);
			modelMap.addAttribute("goods", goods);
		} catch (Exception e) {
			logger.info("trxorder_goods id Error, id is " + tempId);
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		return mav;
	}
	@RequestMapping(params = "method=unUseGood")
	public ModelAndView queryUnUseGoods(HttpServletRequest request,HttpServletResponse response,ModelMap modelMap){
		ModelAndView modelAndView = null;
		try{
			String id = request.getParameter("id");
//			if(!StringUtils.validNull(id)){
//				return
//			}
			Long trxId = Long.parseLong(id);
			boolean uv = validateUserWithTrxorderId(trxId, request);
			if(!uv)
			{
				logger.info("the order where trxorder_id  = " + trxId + " can't be seen by this user");
				return new ModelAndView("redirect:/wap/mUserCenter.do?method=showUserCenter");
			}
			MTrxorderGoods unUseGood =  mTrxorderGoodsService.getTrxOrderGoodsInfo(Long.valueOf(id));
			modelMap.addAttribute("UPLOAD_IMAGES_URL",
					Constant.UPLOAD_IMAGES_URL);
			String phone = "";
			if(StringUtils.validNull(unUseGood.getTel())){
				phone = unUseGood.getTel().replace("-", "");
			}
			modelMap.addAttribute("phone", phone);
			modelMap.addAttribute("unUseGood", unUseGood);
		}catch(Exception e){
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		modelAndView = new ModelAndView("wap/ucenter/unUseGood");
		return modelAndView;
	}
	
	@RequestMapping(params = "method=refundGood")
	public ModelAndView queryRefundGoods(HttpServletRequest request,HttpServletResponse response,ModelMap modelMap){
		ModelAndView modelAndView = null;
		try{
			String id = request.getParameter("id");
			String status = request.getParameter("status");
//			if(!StringUtils.validNull(id)){
//				id = "22381";
//			}
			Long trxId = Long.parseLong(id);
			boolean uv = validateUserWithTrxorderId(trxId, request);
			if(!uv)
			{
				logger.info("the order where trxorder_id  = " + trxId + " can't be seen by this user");
				new ModelAndView("redirect:/wap/mUserCenter.do?method=showUserCenter");
			}
			MTrxorderGoods refundGood =  mTrxorderGoodsService.getRefundGoodsInfo(id,status);
			modelMap.addAttribute("UPLOAD_IMAGES_URL",
					Constant.UPLOAD_IMAGES_URL);
			modelMap.addAttribute("refundGood", refundGood);
		}catch(Exception e){
			e.printStackTrace();
			return new ModelAndView("redirect:/jsp/wap/500.jsp");
		}
		modelAndView = new ModelAndView("wap/ucenter/refundGood");
		return modelAndView;
	}
	
	public boolean validateUserWithTrxorderId(Long trxorderId, HttpServletRequest request)
	{
		User user = getMemcacheUser(request);
		if(user == null)
		{
			return false;
		}
		
		boolean verifyResult = false ;
		try {
			verifyResult = trxorderGoodsService.verifyBelong(trxorderId, user.getId(),true);
		} catch (Exception e) {
			verifyResult = false;
		}
		return verifyResult;
	}
}
