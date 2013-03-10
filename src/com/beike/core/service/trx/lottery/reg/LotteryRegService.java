package com.beike.core.service.trx.lottery.reg;

import java.util.List;

import com.beike.common.entity.trx.lottery.reg.LotteryReg;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.LotteryRegException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;

/**
 * 千品注册指定概率抽奖
 * 
 * @author jianjun.huo
 * 
 */
public interface LotteryRegService
{

	/**
	 * 处理中奖
	 * 
	 * @param lotteryReg
	 * @return
	 */
	LotteryReg processLotteryReg(Long userId) throws VmAccountException, AccountException, StaleObjectStateException, LotteryRegException;

	/**
	 * 查询抽奖信息
	 * @param recordNumber 记录条数
	 * @return
	 */
	List<LotteryReg> findLotteryRegList(int recordNumber);

}
