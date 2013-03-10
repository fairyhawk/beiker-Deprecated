package com.beike.core.service.trx.lottery.full;

import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.lottery.full.FullLottery;
import com.beike.common.exception.FullLotteryException;
import com.beike.entity.goods.Goods;

/**
 * 满额指定概率抽奖
 * 
 * @author jianjun.huo
 * 
 */
public interface FullLotteryService
{

	/**
	 * 查询指定记录条数的抽奖信息列表
	 * 
	 * @param number
	 * @return
	 */
	public List<FullLottery> findFullLotteryList(int recordNumber);

	/**
	 * 处理抽奖
	 * 
	 * @param userId
	 * @return
	 */
	public FullLottery processFullLottery(Long userId, String localCity, Long lotteryType) throws FullLotteryException, Exception;

	/**
	 * 查询用户可抽奖次数
	 * 
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int findTrxorderGoodsAndFullLottery(Long userId, String startDate, String endDate);

	/**
	 * 创建活动订单接口
	 * 
	 * @param userId
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> processCreateTrxOrder(Long userId, Long goodsId) throws Exception;

	/**
	 * 查询 参与抽奖的人数
	 * 
	 * @return
	 */
	public long getFullLotteryTotal();

	/**
	 * 根据城市查询分站的奖项商品
	 * 
	 * @return
	 */
	public List<Goods> findLotteryGoodsList(String localCityName);

	/**
	 * 判断商品是否下线,是否在活动时间内, 如果不满足,则随机换个满足条件的goodsId
	 */
	public Long getGoodsIdForLottery(String localCityName, Long goodsId);

	/**
	 * 查询奖品的商品名称
	 * 
	 * @param GoodsId
	 * @return
	 */
	public String getGoodsNameForLottery(Long goodsId);

	/**
	 * 抽奖图片显示情况的判断
	 * 
	 * @param prizeSize
	 * @return
	 */
	public int getFlagForLotteryButton(int prizeSize);

	/**
	 * 获取js时间控件的时间
	 * 
	 * @param prizeSize
	 * @return
	 */
	public long getNowEndGapTimeForLottery(int prizeSize);

}
