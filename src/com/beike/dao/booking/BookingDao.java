package com.beike.dao.booking;

import java.util.List;
import java.util.Map;

import com.beike.entity.booking.BookingFormVO;
import com.beike.entity.booking.BookingInfo;
import com.beike.entity.booking.BookingLog;

public interface BookingDao {

	/**
	 * 订单是否合法
	 * janwen
	 * @param trxgoods_ids
	 * @param userid
	 * @return 
	 *
	 */
	public Long isValidOrder(List<Long> trxgoods_ids,BookingFormVO bfv);
	
	/**
	 * 批量预订,订单信息
	 * janwen
	 * @param trxorder_id
	 *
	 * @return
	 *
	 */
	public List<Map> getOrdersByTrxorderidAndGoodsid(Long userid,Long trxorder_id,Long goodsid);
	
	/**
	 * 
	 * janwen
	 * @param bfv
	 * @return 剩余预订数量
	 *
	 */
	public Long getBookedTotal(BookingFormVO bfv);
	
	/**
	 * 保存预订信息
	 * janwen
	 * @param bookingInfo
	 * @return 
	 *
	 */
	public int saveBookingInfo(List<BookingInfo> bookingInfos);
	
	
	
	/**
	 * 单个提交
	 * janwen
	 * @param bookingInfo
	 * @return id,供日志表使用
	 *
	 */
	public Long saveBookingInfo(BookingInfo bookingInfo);
	
	/**
	 * 保存预订日志
	 * janwen
	 * @param bookinglog
	 * @return
	 *
	 */
	public int saveBookingLoginfo(List<BookingLog> bookinglogs);
	
	
	/**
	 * 获取guestid
	 * janwen
	 * @param trxgoods_id
	 * @return
	 *
	 */
	public List<Map> getGuestidByTrxGoodsid(List<Long> trxgoods_id);
	
	/**
	 * 
	 * janwen
	 * @param trxgoods_id
	 * @return 预订记录
	 *
	 */
	public List<Map> getScheduledForm(List<Long> trxgoods_ids);
	
	
	/**
	 * 
	 * janwen
	 * @param goodsid
	 * @return 商品对应分店
	 *
	 */
	public List<Map> getBranchInfoByGoodsid(Long goodsid);
	
	/**
	 * 
	 * janwen
	 * @param goodsid
	 * @return 可预订商品
	 *
	 */
	public List<Map> getAvailableGoods(Long goodsid);
	
	/**
	 * 
	 * janwen
	 * @param bi
	 * @return 取消预订
	 *
	 */
	public int cancelBooking(BookingInfo bi);
	
	
	/**
	 * 
	 * janwen
	 * @param bookingid
	 * @return 
	 *
	 */
	public List<Map> getBookingRecordByID(Long bookingid,Long userid);
	
	
	public List<Map> getBookingRecordByTrxgoodsID(List<Long> trxgoods_ids);
	
	/**
	 * 
	 * janwen
	 * @param trxgoods_ids
	 * @return 预订过的订单
	 *
	 */
	public Long isBooked(List<Long> trxgoods_ids);
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 分店运行的每日可预订总量
	 *
	 */
	public Long getAvailableBookingTotal(Long branchid);
	
	/**
	 * 
	 * janwen
	 * @param bookedid
	 * @return 重新预订
	 *
	 */
	public int reBooking(BookingInfo bi);
	
	/**
	 * 
	 * janwen
	 * @param branchid
	 * @return 是否存在改分店
	 *
	 */
	public Long getBranchByBranchid(Long branchid);
	
	/**
	 * 
	 * janwen
	 * @param branchids
	 * @return 商家接受短信手机号
	 *
	 */
	public List<Map> getBranchPhone(List<Long> branchids);
	
	/**
	 * 
	 * janwen
	 * @param goodsid
	 * @return 预订商品信息
	 *
	 */
	public List<Map> getBookingGoods(Long goodsid);
	
	/**
	 * 
	 * janwen
	 * @param userid
	 * @param goodsid
	 * @return 用户已预订trxgoods_id
	 *
	 */
	public List<Long> getUserBookedTrxgoodsID(Long userid,Long goodsid);
	
	/**
	 * 
	 * janwen
	 * @param bookingid
	 * @param userid
	 * @return 重新预订提示信息
	 *
	 */
	public List<Map> showTip(Long bookingid,Long userid);
	
	/**
	 * 我的订单展示
	 * @param trxgoods_id
	 * @return
	 */
	public Map findBytrxgoodsId(String trxgoods_id) ;
	
}
