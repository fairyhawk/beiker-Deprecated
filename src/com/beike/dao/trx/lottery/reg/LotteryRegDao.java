package com.beike.dao.trx.lottery.reg;

import java.util.List;

import com.beike.common.entity.trx.lottery.reg.LotteryReg;
import com.beike.dao.GenericDao;

/**
 * 千品注册指定概率抽奖
 * @author jianjun.huo
 *
 */
public interface LotteryRegDao extends GenericDao<LotteryReg, Long>
{
   
	 LotteryReg findById(Long id);
	
	 LotteryReg findByUserId(Long userId);
	
	 Long addLotteryReg (LotteryReg lotteryReg);
	
	 List<LotteryReg> findLotteryRegList(int recordNumber);
	 
	 /**
	  * 查询已经中奖的记录数
	  * @return
	  */
	 int  findLotteryRegTotal () ;

}
