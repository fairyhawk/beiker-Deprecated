package com.beike.action.booking;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.beike.action.user.BaseUserAction;
import com.beike.entity.booking.BookingBranch;
import com.beike.entity.booking.BookingFormVO;
import com.beike.entity.booking.BookingInfo;
import com.beike.entity.user.User;
import com.beike.service.booking.BookingService;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.json.JSONArray;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;
import com.beike.util.singletonlogin.SingletonLoginUtils;


@Controller("bookingAction")
public class BookingAction extends BaseUserAction {
    
	@Autowired
	private BookingService bookingService;
	
	static final Log logger = LogFactory.getLog(BookingAction.class);
	static final String HANDLE_RESULT = "handleresult";
	static final String HANDLE_SUCCESS = "success";
	static final String HANDLE_FAIL = "fail";

	
	
	
	/**
	 * 批量预订提交
	 * janwen
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ParseException 
	 * @throws JSONException 
	 *
	 */
	@RequestMapping(value="/booking/saveBatchBooking.do",method = RequestMethod.POST)  
    public void saveBatchBooking(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException, JSONException{
		User user = SingletonLoginUtils.getMemcacheUser(request);
		JSONObject response_json = new JSONObject();
		String error_message = "预订失败,请检查订单信息!";
		String trxorder_id = request.getParameter("trxorder_id");
		String goodsid = request.getParameter("goodsid");
		String tobook = request.getParameter("tobook");
		String branchid = request.getParameter("branchid");
		String person = request.getParameter("person");
		String phone = request.getParameter("phone");
		String scheduled_consumption_datetime = request.getParameter("scheduled_consumption_datetime");
		String message = request.getParameter("message");
		Map<String,String> results = new HashMap<String, String>();
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		try {
			message = StringUtils.neNullAndDigital(message, true,1000) ? message : "";
			if(user != null && StringUtils.neNullAndDigital(trxorder_id, false,null) && StringUtils.neNullAndDigital(goodsid, false,null)
					&& StringUtils.neNullAndDigital(tobook, false,null) && StringUtils.neNullAndDigital(branchid, false,null)
					&& StringUtils.neNullAndDigital(person, true,15) && StringUtils.neNullAndDigital(phone, false,11)
					&& StringUtils.neNullAndDigital(scheduled_consumption_datetime, true,29)
					&& DateUtils.isAfterToday(scheduled_consumption_datetime)){
				BookingFormVO bfv = new BookingFormVO();
				bfv.setTobook(new Integer(tobook));
				bfv.setBranch_id(new Long(branchid));
				bfv.setMessage(message);
				bfv.setPhone(phone);
				bfv.setPerson(person);
				bfv.setGoods_id(new Long(goodsid));
				bfv.setTrxorder_id(new Long(trxorder_id));
				bfv.setScheduled_consumption_datetime(scheduled_consumption_datetime);
				bfv.setCreateucid(user.getId());
				List<Long> trxgoods_ids = bookingService.getOrdersByTrxorderidAndGoodsid(user.getId(),new Long(trxorder_id), new Long(goodsid));
				if(bookingService.isValidOrder(trxgoods_ids, bfv)){
					 results = bookingService.saveBooking(trxgoods_ids, bfv);
					 response_json.put("response_json", results.get("result_message").toString());
					 response_json.put(HANDLE_RESULT, results.get("handle_result").toString());
					 bookingService.sendSMS(trxgoods_ids);
				}else{
					 response_json.put("response_json", error_message);
					 response_json.put(HANDLE_RESULT, HANDLE_FAIL);
				}
				
			}else{
				response_json.put("response_json", error_message);
				response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			}
			
			
			response.getWriter().write(response_json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response_json.put("response_json", error_message);
			response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			response.getWriter().write(response_json.toString());
		}
	}
	
	/**
	 * 单个预订申请提交
	 * janwen
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ParseException
	 * @throws JSONException
	 *
	 */
	@RequestMapping(value="/booking/saveBooking.do",method = RequestMethod.POST)  
    public void saveBooking(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException, JSONException{
		User user = SingletonLoginUtils.getMemcacheUser(request);
		JSONObject response_json = new JSONObject();
		String trxgoods_id = request.getParameter("trxgoods_id");
		String goodsid = request.getParameter("goodsid");
		String branchid = request.getParameter("branchid");
		String person = request.getParameter("person");
		String phone = request.getParameter("phone");
		String scheduled_consumption_datetime = request.getParameter("scheduled_consumption_datetime");
		String message = request.getParameter("message");
		String error_message = "预订失败,请检查订单信息!";
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		Map<String,String> results = new HashMap<String, String>();
		try {
			message = StringUtils.neNullAndDigital(message, true,1000) ? message : "";
			if(user != null && StringUtils.neNullAndDigital(trxgoods_id, false,null) && StringUtils.neNullAndDigital(goodsid, false,null)
					 && StringUtils.neNullAndDigital(branchid, false,null)
					&& StringUtils.neNullAndDigital(person, true,15) && StringUtils.neNullAndDigital(phone, false,11)
					&& StringUtils.neNullAndDigital(scheduled_consumption_datetime, true,20)
					&& DateUtils.isAfterToday(scheduled_consumption_datetime)){
				BookingFormVO bfv = new BookingFormVO();
				
				bfv.setGoods_id(new Long(goodsid));
				bfv.setBranch_id(new Long(branchid));
				bfv.setMessage(message);
				bfv.setPhone(phone);
				bfv.setCreateucid(user.getId());
				bfv.setPerson(person);
				bfv.setScheduled_consumption_datetime(scheduled_consumption_datetime);
				List<Long> trxgoods_ids = new ArrayList<Long>();
				trxgoods_ids.add(new Long(trxgoods_id));
				if(bookingService.isValidOrder(trxgoods_ids, bfv)){
					 results = bookingService.saveBooking(trxgoods_ids, bfv);
					 response_json.put(HANDLE_RESULT, results.get("handle_result").toString());
					 response_json.put("response_json", results.get("result_message").toString());
					 bookingService.sendSMS(trxgoods_ids);
				}else{
					response_json.put("response_json", error_message);
					response_json.put(HANDLE_RESULT, HANDLE_FAIL);
				}
			   
			}else{
				response_json.put("response_json", error_message);
				response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			}
			
			response.getWriter().write(response_json.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			response_json.put("response_json", error_message);
			response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			response.getWriter().write(response_json.toString());
		}
	}
	
	/**
	 * 批量预订填写申请单
	 * janwen
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws JSONException 
	 *
	 */
	@RequestMapping(value="/booking/showBookingformBatch.do",method = RequestMethod.POST)  
    public void showBookingformBatch(HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException{
		User user = SingletonLoginUtils.getMemcacheUser(request);
		JSONObject response_json = new JSONObject();
		String trxorder_id = request.getParameter("trxorder_id");
		String goodsid = request.getParameter("goodsid");
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		try {
			if (user != null && StringUtils.neNullAndDigital(trxorder_id, false,null)
					&& StringUtils.neNullAndDigital(goodsid, false,null)) {
				List<Long> trxgoods_ids = bookingService
						.getOrdersByTrxorderidAndGoodsid(user.getId(),new Long(trxorder_id),
								new Long(goodsid));
				BookingFormVO bfv = new BookingFormVO();
				bfv.setCreateucid(user.getId());
				if (bookingService.isValidOrder(trxgoods_ids, bfv)) {
					Map g = bookingService.getBookingGoods(new Long(goodsid));
					List<BookingBranch> branches = bookingService.getBranchesInfoByGoodsid(new Long(goodsid));
					if(branches != null && branches.size() > 0){
						response_json.put("goodsinfo", new JSONObject(g));
						response_json.put("branches", new JSONArray(convert2JsonObject(branches)));
						response_json.put("orderTotal", trxgoods_ids.size());
						response_json.put(HANDLE_RESULT, HANDLE_SUCCESS);
					}else{
						response_json.put(HANDLE_RESULT, HANDLE_FAIL);
					}
				}else{
					response_json.put(HANDLE_RESULT, HANDLE_FAIL);
				}
			}else{
				response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			}
			response.getWriter().write(response_json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			response.getWriter().write(response_json.toString());
		}
	}
	
	
   private static List<JSONObject> convert2JsonObject(List<BookingBranch> branches){
		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
		for(int i=0;i<branches.size();i++){
			jsonObjects.add(new JSONObject(branches.get(i)));
		}
		return jsonObjects;
	}
	/**
	 * 单个预订填写申请单
	 * janwen
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws JSONException 
	 *
	 */
	@RequestMapping(value="/booking/showBookingform.do",method = RequestMethod.POST)  
    public void showBookingform(HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException{
		User user = SingletonLoginUtils.getMemcacheUser(request);
		JSONObject response_json = new JSONObject();
		String trxgoods_id = request.getParameter("trxgoods_id");
		String goodsid = request.getParameter("goodsid");
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		try {
			if (user != null && StringUtils.neNullAndDigital(trxgoods_id, false,null)
					&& StringUtils.neNullAndDigital(goodsid, false,null)) {
				List<Long> trxgoods_ids = new ArrayList<Long>();
				trxgoods_ids.add(new Long(trxgoods_id));
				BookingFormVO bfv = new BookingFormVO();
				bfv.setCreateucid(user.getId());
				if (bookingService.isValidOrder(trxgoods_ids, bfv)) {
					Map g = bookingService.getBookingGoods(new Long(goodsid));
					List<BookingBranch> branches = bookingService.getBranchesInfoByGoodsid(new Long(goodsid));
					if(branches != null && branches.size() > 0){
						response_json.put("goodsinfo", new JSONObject(g));
						response_json.put("branches", new JSONArray(convert2JsonObject(branches)));
						response_json.put(HANDLE_RESULT, HANDLE_SUCCESS);
					}else{
						response_json.put(HANDLE_RESULT, HANDLE_FAIL);
					}
					
				}else{
					response_json.put(HANDLE_RESULT, HANDLE_FAIL);
				}
			}else{
				response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			}
			response.getWriter().write(response_json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			response.getWriter().write(response_json.toString());
		}
	}
	
	/**
	 * 取消预订
	 * janwen
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws JSONException
	 *
	 */
	@RequestMapping(value="/booking/cacelBooking.do",method = RequestMethod.POST)  
    public void cancelBooking(HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException{
		JSONObject json = new JSONObject();
		String bookingid = request.getParameter("bookingid");
		User user = SingletonLoginUtils.getMemcacheUser(request);
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		try {
			if(user != null && StringUtils.neNullAndDigital(bookingid, false,null)){
				BookingInfo bi = bookingService.getBookingRecordByID(new Long(bookingid),user.getId());
				if(bi != null && bookingService.cancelBooking(bi)){
					json.put(HANDLE_RESULT, HANDLE_SUCCESS);
				}else{
					json.put(HANDLE_RESULT, HANDLE_FAIL);
				}
			}else{
				json.put(HANDLE_RESULT,HANDLE_FAIL);
			}
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			json.put(HANDLE_RESULT,HANDLE_FAIL);
			response.getWriter().write(json.toString());
		}
	}
	
	@RequestMapping(value="/booking/getBookingRecord.do",method = RequestMethod.POST)  
    public void getBookingRecord(HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException{
	    String bookingid = request.getParameter("bookingid");
	    JSONObject json = new JSONObject();
	    User user = SingletonLoginUtils.getMemcacheUser(request);
	    response.setContentType("application/json;charset=UTF-8");
	    response.setCharacterEncoding("utf-8");
	    try {
			if(user != null && StringUtils.neNullAndDigital(bookingid, false,null)){
				BookingInfo bi = bookingService.getBookingRecordByID(new Long(bookingid),user.getId());
				if(bi != null){
					List<BookingBranch> branches = bookingService.getBranchesInfoByGoodsid(bi.getGoods_id());
					json.put("branches",new JSONArray(convert2JsonObject(branches)));
					json.put("bookingInfo", new JSONObject(bi));
					json.put(HANDLE_RESULT,HANDLE_SUCCESS);
				}else{
					json.put(HANDLE_RESULT,HANDLE_FAIL);
				}
			}else{
				json.put(HANDLE_RESULT,HANDLE_FAIL);
			}
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			json.put(HANDLE_RESULT,HANDLE_FAIL);
			response.getWriter().write(json.toString());
		}
	}
	
	
	
	@RequestMapping(value="/booking/showTip.do",method = RequestMethod.POST)  
    public void showTip(HttpServletRequest request,HttpServletResponse response) throws IOException, JSONException{
	    String bookingid = request.getParameter("bookingid");
	    JSONObject json = new JSONObject();
	    User user = SingletonLoginUtils.getMemcacheUser(request);
	    response.setContentType("application/json;charset=UTF-8");
	    response.setCharacterEncoding("utf-8");
	    try {
			if(user != null && StringUtils.neNullAndDigital(bookingid, false,null)){
				BookingInfo bi = bookingService.showTip(new Long(bookingid),user.getId());
				if(bi != null){
					json.put("bookingInfo", new JSONObject(bi));
					json.put(HANDLE_RESULT,HANDLE_SUCCESS);
				}else{
					json.put(HANDLE_RESULT,HANDLE_FAIL);
				}
			}else{
				json.put(HANDLE_RESULT,HANDLE_FAIL);
			}
			response.getWriter().write(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			json.put(HANDLE_RESULT,HANDLE_FAIL);
			response.getWriter().write(json.toString());
		}
	}
	
	/**
	 * 重新预订提交
	 * janwen
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ParseException
	 * @throws JSONException
	 *
	 */
	@RequestMapping(value="/booking/reBooking.do",method = RequestMethod.POST)  
    public void reBooking(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException, JSONException{
		User user = SingletonLoginUtils.getMemcacheUser(request);
		JSONObject response_json = new JSONObject();
		String trxgoods_id = request.getParameter("trxgoods_id");
		String goodsid = request.getParameter("goodsid");
		String branchid = request.getParameter("branchid");
		String person = request.getParameter("person");
		String phone = request.getParameter("phone");
		String bookedid =  request.getParameter("bookingid");
		String scheduled_consumption_datetime = request.getParameter("scheduled_consumption_datetime");
		String message = request.getParameter("message");
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("utf-8");
		String error_message = "预订失败,请检查订单信息!";
		Map<String,String> results = new HashMap<String, String>();
		try {
			message = StringUtils.neNullAndDigital(message, true,1000) ? message : "";
			if(user != null && StringUtils.neNullAndDigital(trxgoods_id, false,null) && StringUtils.neNullAndDigital(goodsid, false,null)
					 && StringUtils.neNullAndDigital(branchid, false,null)
					&& StringUtils.neNullAndDigital(person, true,15) && StringUtils.neNullAndDigital(phone, false,11)
					&& StringUtils.neNullAndDigital(scheduled_consumption_datetime, true,20)
					&& StringUtils.neNullAndDigital(bookedid, false,null)
					&& DateUtils.isAfterToday(scheduled_consumption_datetime)){
				BookingFormVO bfv = new BookingFormVO();
				bfv.setBranch_id(new Long(branchid));
				bfv.setMessage(message);
				bfv.setPhone(phone);
				bfv.setPerson(person);
				bfv.setCreateucid(user.getId());
				bfv.setScheduled_consumption_datetime(scheduled_consumption_datetime);
				List<Long> trxgoods_ids = new ArrayList<Long>();
				trxgoods_ids.add(new Long(trxgoods_id));
				if(bookingService.isValidOrder(trxgoods_ids, bfv)){
					 BookingInfo bi = bookingService.getBookingRecordByID(new Long(bookedid), user.getId());
					 if(bi != null){
						 bi.setCreateucid(user.getId());
						 bfv.setGoods_id(bi.getGoods_id());
						 if(bookingService.reBooking(bi)){
							 results = bookingService.saveBooking(trxgoods_ids, bfv);
							 response_json.put(HANDLE_RESULT, results.get("handle_result").toString());
							 response_json.put("response_json", results.get("result_message").toString()) ;
							 bookingService.sendSMS(trxgoods_ids);
						 }else{
							 response_json.put(HANDLE_RESULT, HANDLE_FAIL);
							 response_json.put("response_json", error_message) ;
						 }
					 }else{
						 response_json.put(HANDLE_RESULT, HANDLE_FAIL);
						 response_json.put("response_json", error_message) ;
					 }
					
				}else{
					response_json.put("response_json", error_message);
					response_json.put(HANDLE_RESULT, HANDLE_FAIL);
				}
			   
			}else{
				response_json.put("response_json", error_message);
				response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			}
			
			response.getWriter().write(response_json.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			response_json.put("response_json", error_message);
			response_json.put(HANDLE_RESULT, HANDLE_FAIL);
			response.getWriter().write(response_json.toString());
		}
	}
}
