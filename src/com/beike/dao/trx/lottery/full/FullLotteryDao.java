package com.beike.dao.trx.lottery.full;

import java.util.List;
import java.util.Map;

import com.beike.common.entity.trx.lottery.full.FullLottery;
import com.beike.entity.goods.Goods;

/**
 * 满额指定概率抽奖
 * 
 * @author jianjun.huo
 * 
 */
public interface FullLotteryDao
{

	/**
	 * 查询满额抽奖实体
	 * 
	 * @param id
	 * @return
	 */
	FullLottery findById(Long id);

	/**
	 * 查询指定条数的抽奖列表
	 * 
	 * @param recordNumber
	 * @return
	 */
	List<FullLottery> findFullLotteryList(int recordNumber);

	/**
	 * 查询用户在运营活动中的消费金额
	 * 
	 * @param userId
	 * @return
	 */
	Map<String, Object> findTrxorderGoods(Long userId, String startDate, String endDate);

	/**
	 *根据userId查询出
	 * 
	 * @param userId
	 * @return
	 */
	Map<String, Object> findFullLotteryByUserId(Long userId);

	/**
	 * 查询参与抽奖的人数
	 * 
	 * @return
	 */
	long getFullLotteryTotal();

	/**
	 * 查询未下线,可使用的商品
	 * 
	 * @param GoodsIds
	 * @return
	 */
	List<Goods> findLotteryGoodsList(String GoodsIds);

	/**
	 * 添加 抽奖记录
	 * 
	 * @param fullLottery
	 */
	Long addFullLottery(FullLottery fullLottery);
	
	
}
