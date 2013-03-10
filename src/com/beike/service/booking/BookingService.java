package com.beike.service.booking;

import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.entity.booking.BookingBranch;
import com.beike.entity.booking.BookingFormVO;
import com.beike.entity.booking.BookingInfo;
import com.beike.entity.booking.BookingLog;

public interface BookingService {

	/**
	 * 订单是否合法
	 * janwen
	 * @param trxorder_ids
	 * @param userid
	 * @return
	 *
	 */
	public boolean isValidOrder(List<Long> trxgoods_ids,BookingFormVO bfv);
	
	
	/**
	 * 批量预订,订单信息
	 * janwen
	 * @param trxorderid
	 * @param tobook 实际打算预订数量
	 * @return 预订提示消息
	 *
	 */
	public Map<String,String> saveBooking(List<Long> trxgoods_ids,BookingFormVO bfv);
	
	/**
	 * 批量预订先获取trxgoods_id
	 * janwen
	 * @param trxorder_id
	 * @param goodsid
	 * @return
	 *
	 */
	public List<Long> getOrdersByTrxorderidAndGoodsid(Long userid,Long trxorder_id,Long goodsid);
	
	/**
	 * 
	 * janwen
	 * @param trxgoods_ids
	 * @param bookingForm
	 * @param status
	 * @return 组装预订完整信息
	 *
	 */
	public List<BookingInfo> populateBookingInfo(List<Long> trxgoods_ids,BookingFormVO bookingForm,String status);
	
	
	public Map<Long,Long> getGuestidByTrxGoodsid(List<Long> trxgoods_ids);
	
	/**
	 * Note:
	 * 预订申请表trxgoods_id会重复,使用trxgoods_id查询要注意
	 * janwen
	 * @param trxgoods_ids
	 * @return 预订日志
	 *
	 */
	public List<BookingLog> getScheduledForm(List<BookingInfo> bookinginfos);
	
	/**
	 * 
	 * janwen
	 * @param goodsid
	 * @return 商品对应分店信息
	 *
	 */
	public  List<BookingBranch> getBranchesInfoByGoodsid(Long goodsid);
	
	/**
	 * 
	 * janwen
	 * @param bi
	 * @return 取消预订
	 *
	 */
	public boolean cancelBooking(BookingInfo bi);
	
	/**
	 * 取消预订,供交易用（生吞事务）
	 * @param bi
	 * @return
	 */
	public boolean cancelBookingForTrx(BookingInfo bi);
	/**
	 * 
	 * janwen
	 * @param bookingid
	 * @return 预订记录
	 *
	 */
	public BookingInfo getBookingRecordByID(Long bookingid,Long userid);
	
	
	/**
	 * 发送短信
	 * janwen
	 * @param trxgoods_ids
	 *
	 */
	public void sendSMS(List<Long> trxgoods_ids);
	
	/**
	 * 
	 * janwen
	 * @param trxgoods_ids
	 * @return 需要发短信的预订
	 *
	 */
	public List<BookingInfo> getNeed2SendSMSBooking(List<Long> trxgoods_ids);
	
	/***
	 * 
	 * janwen
	 * @param branchids
	 * @return 商家电话
	 *
	 */
	public Map<Long,String> getBranchPhone(List<Long> branchids);
	
	/**
	 * 
	 * janwen
	 * @param goodsid
	 * @return 预订商品信息
	 *
	 */
	public Map getBookingGoods(Long goodsid);
	
	/**
	 * 
	 * janwen
	 * @param bi
	 * @return 重新预订
	 *
	 */
	public boolean reBooking(BookingInfo bi);
	
	/**
	 * 
	 * janwen
	 * @param bookingid
	 * @param userid
	 * @return 重新预订提示信息
	 *
	 */
	public BookingInfo showTip(Long bookingid,Long userid);
	
	/**
	 * 退款接口调用，将预定状态置为未预定
	 * @param trxGoodsId
	 * @return
	 */
	public boolean scheduled(TrxorderGoods trxGoods);
	
	/**
	 * 购买成功页预定调用
	 * @param trxGoodsId
	 * @return
	 */
	public Map findscheduledBytrxGoods(Long trxGoodsId);
	
}
