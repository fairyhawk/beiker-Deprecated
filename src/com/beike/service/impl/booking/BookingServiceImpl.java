package com.beike.service.impl.booking;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.TrxorderGoodsService;
import com.beike.dao.booking.BookingDao;
import com.beike.dao.trx.TrxOrderDao;
import com.beike.entity.booking.BookingBranch;
import com.beike.entity.booking.BookingFormVO;
import com.beike.entity.booking.BookingInfo;
import com.beike.entity.booking.BookingLog;
import com.beike.entity.common.Sms;
import com.beike.form.SmsInfo;
import com.beike.service.booking.BookingService;
import com.beike.service.common.SmsService;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;

@Service("bookingService")
public class BookingServiceImpl implements BookingService {
   
	@Autowired
	private BookingDao bookingDao;
    @Autowired
	private TrxorderGoodsService trxorderGoodsService;
    @Autowired
    private TrxOrderDao trxOrderDao;
	@Override
	public boolean isValidOrder(List<Long> trxgoods_ids,BookingFormVO bfv) {
		boolean valid = false;
		if(trxgoods_ids != null && trxgoods_ids.size() > 0){
			Long results = bookingDao.isValidOrder(trxgoods_ids, bfv);
		    if(results > 0 && bfv.getTobook() <= results){
		    	 results = bookingDao.isBooked(trxgoods_ids);
		    	 //都预订了
		    	 valid = results >= trxgoods_ids.size() ? false : true;
		    }
		    //最后入住时间不能低于当前时间
		    if(valid && bfv != null && bfv.getScheduled_consumption_datetime() != null){
		    	try {
					valid = DateUtils.isAfterToday(bfv.getScheduled_consumption_datetime());
				} catch (ParseException e) {
					valid = false;
				}
		    }
		    
		    if(valid && bfv != null && bfv.getBranch_id() != null){
		    	Long count = bookingDao.getBranchByBranchid(bfv.getBranch_id());
				valid = count == 1 ? true : false;
		    }
		   
		}
		
		return valid;
	}


	@Override
	public List<Long> getOrdersByTrxorderidAndGoodsid(Long userid,Long trxorder_id,
			Long goodsid) {
		List<Map> trxgoods_ids = bookingDao.getOrdersByTrxorderidAndGoodsid(userid,trxorder_id, goodsid);
		List<Long> return_trxgoods_ids = new ArrayList<Long>();
		for(int i=0;i<trxgoods_ids.size();i++){
			Map map = trxgoods_ids.get(i);
			return_trxgoods_ids.add((Long)map.get("id"));
		}
		return return_trxgoods_ids;
	}


	@Override
	public List<BookingInfo> populateBookingInfo(List<Long> trxgoods_ids,
			BookingFormVO bookingForm,String status) {
		List<BookingInfo> bookinginfos = new ArrayList<BookingInfo>();
		Map<Long,Long> guestid_map_cache = getGuestidByTrxGoodsid(trxgoods_ids);
		for(int i=0;i<trxgoods_ids.size();i++){
			BookingInfo bi = new BookingInfo();
			try {
				BeanUtils.copyProperties(bi, bookingForm);
				bi.setTrx_id(trxgoods_ids.get(i));
				bi.setGuest_id(guestid_map_cache.get(bi.getTrx_id()));
				bi.setStatus(status);
				bi.setUpdateucid(bi.getCreateucid());
				bookinginfos.add(bi);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return bookinginfos;
	}

   
	@Override
	public Map<String,String> saveBooking(List<Long> trxgoods_ids, BookingFormVO bfv) {
		Long booked = bookingDao.getBookedTotal(bfv);
		Long availableBookingTotal = bookingDao.getAvailableBookingTotal(bfv.getBranch_id());
		String result_message = "预订失败,请检查订单信息!";
		Long remaing = availableBookingTotal - booked;
		List<BookingInfo> bookingInfos = null;
		List<Long> tobookTrxgoods_ids = new ArrayList<Long>();
		Map<String,String> return_results = new HashMap<String, String>();
		String handle_result = "fail";
		if(remaing >= bfv.getTobook()){
			for(int i=0;i<bfv.getTobook();i++){
				tobookTrxgoods_ids.add(trxgoods_ids.get(i));
			}
			bookingInfos = populateBookingInfo(tobookTrxgoods_ids, bfv, "1");
			result_message =  "恭喜您,预订已成功!";
			handle_result = "success";
		}else if(remaing > 0 && remaing < bfv.getTobook()){
			for(int i=0;i<remaing;i++){
				tobookTrxgoods_ids.add(trxgoods_ids.get(i));
			}
		    bookingInfos = populateBookingInfo(tobookTrxgoods_ids, bfv, "1");
			//需要商家反馈的预订
			List<Long> temp = new ArrayList<Long>();
			for(int j=remaing.intValue();j<bfv.getTobook();j++){
				temp.add(trxgoods_ids.get(j));
			}
			List<BookingInfo> needToConfirmBooking = populateBookingInfo(temp, bfv, "0");
			bookingInfos.addAll(needToConfirmBooking);
			result_message = "您的预订" + remaing + "单已成功,还有"
					+ (bfv.getTobook() - remaing) + "单需要商家反馈.";
			handle_result = "halfsuccess";
		}else{
			for(int i=0;i<bfv.getTobook();i++){
				tobookTrxgoods_ids.add(trxgoods_ids.get(i));
			}
		    bookingInfos = populateBookingInfo(tobookTrxgoods_ids, bfv, "0");
			result_message = "您的预订已提交,请您耐心等待商家反馈.";
			handle_result = "toconfirm";
		}
		boolean bookingSuccess = true;
		for(int count =0;count<bookingInfos.size();count++){
			BookingInfo toSaveBookingInfo = bookingInfos.get(count);
			Long scheduled_id = bookingDao.saveBookingInfo(toSaveBookingInfo);
			if(scheduled_id != null){
				toSaveBookingInfo.setId(scheduled_id);
			}else{
				bookingSuccess = false;
				break;
			}
		}
		int rows = 0;
		if(bookingSuccess){
			rows = bookingDao.saveBookingLoginfo(getScheduledForm(bookingInfos));
		}
		if(rows > 0){
			return_results.put("result_message", result_message);
			return_results.put("handle_result",handle_result);
			return return_results;
		}else{
			throw new RuntimeException("保存预订信息失败,请稍后重试!");
		}
		
		
	}


	@Override
	public Map<Long, Long> getGuestidByTrxGoodsid(List<Long> trxgoods_ids) {
		List<Map> guestidsMap = bookingDao.getGuestidByTrxGoodsid(trxgoods_ids);
		Map<Long,Long> guestid_map_cache = new HashMap<Long, Long>();
		for(int i=0;i<guestidsMap.size();i++){
			Map temp = guestidsMap.get(i);
			guestid_map_cache.put((Long)temp.get("id"), (Long)temp.get("guest_id"));
		}
		return guestid_map_cache;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean scheduled(TrxorderGoods trxGoods){
		boolean boo = false;
		try{
		Map map = bookingDao.findBytrxgoodsId(trxGoods.getId().toString());
		Long trxorderId = trxGoods.getTrxorderId();
		TrxOrder trxOrder = trxOrderDao.findById(trxorderId);
		if(map!=null&&map.size()>0){//预定商品表中有预定记录做预定处理
		BookingInfo boInfo = getBookingRecordByID((Long)map.get("id"),trxOrder.getUserId());
			 boo = cancelBooking(boInfo);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return boo;
	}
	@Override
	public Map findscheduledBytrxGoods(Long trxGoodsId){
		Map map = bookingDao.findBytrxgoodsId(trxGoodsId.toString());
		return map;
	}
	
	@Override
	public List<BookingLog> getScheduledForm(List<BookingInfo> bookinginfos) {
		List<BookingLog> bookinglogs = new ArrayList<BookingLog>();
		for(int i=0;i<bookinginfos.size();i++){
			BookingInfo savedBookingInfo = bookinginfos.get(i);
				//status=1,必须插入2条日志
				if("1".equals(savedBookingInfo.getStatus())){
					BookingLog logSuccess = new BookingLog();
					logSuccess.setCreatetype(logSuccess.getCreatetype());
					logSuccess.setCreateucid(savedBookingInfo.getCreateucid());
					logSuccess.setScheduled_id(savedBookingInfo.getId());
					logSuccess.setStatus("0");
					bookinglogs.add(logSuccess);
					//多加一条日志
					BookingLog logSubmit = new BookingLog();
					logSubmit.setCreatetype(logSubmit.getCreatetype());
					logSubmit.setCreateucid(savedBookingInfo.getCreateucid());
					logSubmit.setScheduled_id(savedBookingInfo.getId());
					logSubmit.setRemark("预订量范围内,自动预订成功");
					logSubmit.setStatus("1");
					bookinglogs.add(logSubmit);
					//satus=0,3,插入一条日志
				}else if("0".equals(savedBookingInfo.getStatus()) || "3".equals(savedBookingInfo.getStatus())){
					BookingLog log = new BookingLog();
					log.setCreatetype(log.getCreatetype());
					log.setCreateucid(savedBookingInfo.getCreateucid());
					log.setScheduled_id(savedBookingInfo.getId());
					log.setStatus(savedBookingInfo.getStatus());
					bookinglogs.add(log);
				}
		}
		return bookinglogs;
	}


	@Override
	public List<BookingBranch> getBranchesInfoByGoodsid(Long goodsid) {
		List<Map> branchesmap = bookingDao.getBranchInfoByGoodsid(goodsid);
		List<BookingBranch> branches = new ArrayList<BookingBranch>();
		for(int i=0;i<branchesmap.size();i++){
			Map temp = branchesmap.get(i);
			BookingBranch branch = new BookingBranch();
			branch.setAddr((String)temp.get("addr"));
			branch.setId(temp.get("merchantid").toString());
			branch.setMerchantname((String)temp.get("merchantname"));
			branches.add(branch);
		}
		return branches;
	}





	@Override
	public boolean cancelBooking(BookingInfo bi) {
		int rows = bookingDao.cancelBooking(bi);
		if(rows == 1){
			List<BookingInfo> canceldBooking = new ArrayList<BookingInfo>();
			bi.setStatus("3");
			canceldBooking.add(bi);
			List<BookingLog> logs = getScheduledForm(canceldBooking);
			rows = bookingDao.saveBookingLoginfo(logs);
			if(rows == logs.size()){
				return true;
			}else{
				throw new RuntimeException("取消预订失败");
			}
		}else{
			throw new RuntimeException("取消预订失败");
		}
	}
	
	/**
	 * 取消预订,供交易用（生吞事务）
	 * @param bi
	 * @return
	 */
	@Override
	public boolean cancelBookingForTrx(BookingInfo bi){
		
		boolean result=false;
		try {
			if (!"2".equals(bi.getStatus()) || !"3".equals(bi.getStatus())) {
				return cancelBooking(bi);
			} else {
				return  true;
			}
		}catch(Exception e ){
			e.printStackTrace();
			return result;
		}
		
}


	@Override
	public BookingInfo getBookingRecordByID(Long bookingid,Long userid) {
		List<Map> mapList = bookingDao.getBookingRecordByID(bookingid,userid);
		BookingInfo bi = null;
		if(mapList != null && mapList.size()>0){
			Map map = mapList.get(0);
			bi = new BookingInfo();
			bi.setId((Long)map.get("id"));
			bi.setMessage((String)map.get("message"));
			bi.setPhone((String)map.get("phone"));
			bi.setPerson((String)map.get("person"));
			bi.setScheduled_consumption_datetime(DateUtils.formatDate(((Timestamp)map.get("scheduled_consumption_datetime")),"yyyy-MM-dd HH:mm:ss"));
			bi.setBranchname((String)(map.get("merchantname")));
			bi.setGoodsname((String)map.get("goods_title"));
			bi.setBranch_id(new Long(map.get("branch_id").toString()));
			bi.setTrx_id(new Long(map.get("trx_id").toString()));
			bi.setGoods_id((Long)map.get("goods_id"));
			bi.setCreateucid(new Long(map.get("createucid").toString()));
			bi.setStatus((String)map.get("status"));
			bi.setUpdateucid(userid);
			if(map.get("proposal_consumption_datetime") != null ){
				bi.setProposal_consumption_datetime(DateUtils.formatDate((Timestamp)map.get("proposal_consumption_datetime"),"yyyy-MM-dd HH:mm:ss"));
			}
		}
		return bi;
	}


	

	private static final String SMS_TYPE = "15";
	@Autowired
	private SmsService smsService;
	@Override
	public void sendSMS(List<Long> trxgoods_ids) {
		List<BookingInfo> needSendSms = getNeed2SendSMSBooking(trxgoods_ids);
		List<Long> branchids = new ArrayList<Long>();
		for(int i=0;i<needSendSms.size();i++){
			BookingInfo bi = needSendSms.get(i);
			branchids.add(bi.getBranch_id());
		}
		Map<Long, String> phone_map = getBranchPhone(branchids);
		for(int i=0;i<needSendSms.size();i++){
			BookingInfo bi = needSendSms.get(i);
			if(phone_map.containsKey(bi.getBranch_id())){
				Sms sms = null;
				Object[] param = null;
				try {
					//预订成功
					if("1".equals(bi.getStatus())){
							sms = smsService.getSmsByTitle(Constant.SMS_BOOKING_SUCCESS_MESSAGE);
							Map g = getBookingGoods(bi.getGoods_id());
							TrxorderGoods tg = trxorderGoodsService.findById(bi.getTrx_id());
							param = new Object[] { g.get("goodsTitle").toString(), tg.getTrxGoodsSn()};
					}else if("0".equals(bi.getStatus())){
						sms = smsService.getSmsByTitle(Constant.SMS_BOOKING_CONFIRMED_MESSAGE);
						TrxorderGoods tg = trxorderGoodsService.findById(bi.getTrx_id());
						param = new Object[] { tg.getTrxGoodsSn()};
					}
				} catch (BaseException e) {
					e.printStackTrace();
				}
				if (sms != null) {
					SmsInfo sourceBean = null;
					String content = "";
					String template = sms.getSmscontent();
					content = MessageFormat.format(template, param);
					String phone = (String)phone_map.get(bi.getBranch_id());
					String[] tosend = phone.split(",");
					for(int j=0;j<tosend.length;j++){
						sourceBean = new SmsInfo(tosend[j], content, SMS_TYPE, "0");
						smsService.sendSms(sourceBean);
					}
					
				}
			}
		}
	}


	@Override
	public List<BookingInfo> getNeed2SendSMSBooking(List<Long> trxgoods_ids) {
		List<Map> bookingMap = bookingDao.getBookingRecordByTrxgoodsID(trxgoods_ids);
		List<BookingInfo> bis = new ArrayList<BookingInfo>();
		for(int i=0;i<bookingMap.size();i++){
			BookingInfo bi = new BookingInfo();
			Map temp = bookingMap.get(i);
			bi.setBranch_id(new Long(temp.get("branch_id").toString()));
			bi.setGoods_id((Long)temp.get("goods_id"));
			bi.setTrx_id(new Long(temp.get("trx_id").toString()));
			bi.setStatus((String)temp.get("status"));
			bis.add(bi);
		}
		return bis;
	}


	@Override
	public Map<Long,String> getBranchPhone(List<Long> branchids) {
		Map<Long,String> branch_phone_map = new HashMap<Long,String>();
		List<Map> branchesInfo = bookingDao.getBranchPhone(branchids);
		for(int i=0;i<branchesInfo.size();i++){
			Map temp = branchesInfo.get(i);
			Long branchid = new Long(temp.get("branch_id").toString());
			if("1".equals((String)temp.get("is_send"))){
				if(branch_phone_map.containsKey(branchid)){
					branch_phone_map.put(branchid, branch_phone_map.get(branchid) + "," + (String)temp.get("phone"));
				}else{
					branch_phone_map.put(new Long(temp.get("branch_id").toString()), (String)temp.get("phone"));
				}
			}
		}
		return branch_phone_map;
	}


	@Override
	public Map getBookingGoods(Long goodsid) {
		List<Map> goodsmaplist = bookingDao.getBookingGoods(goodsid);
		if(goodsmaplist != null && goodsmaplist.size() > 0){
			return goodsmaplist.get(0);
		}
		return null;
	}


	@Override
	public boolean reBooking(BookingInfo bi) {
		
		if(bookingDao.reBooking(bi) > 0){
			return true;
		}
		return false;
	}


	@Override
	public BookingInfo showTip(Long bookingid, Long userid) {
		List<Map> mapList = bookingDao.showTip(bookingid,userid);
		BookingInfo bi = null;
		if(mapList != null && mapList.size()>0){
			Map map = mapList.get(0);
			bi = new BookingInfo();
			bi.setId((Long)map.get("id"));
			if(StringUtils.validNull((String)map.get("remark"))){
				bi.setMessage((String)map.get("remark"));
			}
			bi.setStatus((String)map.get("status"));
			if(map.get("proposal_consumption_datetime") != null ){
				bi.setProposal_consumption_datetime(DateUtils.formatDate((Timestamp)map.get("proposal_consumption_datetime"),"yyyy-MM-dd HH:mm:ss"));
			}
		}
		return bi;
	}
}
