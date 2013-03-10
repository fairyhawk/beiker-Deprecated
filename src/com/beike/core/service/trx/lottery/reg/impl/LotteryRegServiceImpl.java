package com.beike.core.service.trx.lottery.reg.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.lottery.reg.LotteryReg;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.LotteryRegException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;
import com.beike.core.service.trx.lottery.reg.LotteryRegService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.lottery.reg.LotteryRegDao;
import com.beike.util.DateUtils;
import com.beike.util.StringUtils;
import com.beike.util.TrxConstant;

/**
 * 千品注册指定概率抽奖
 * 
 * @author jianjun.huo
 * 
 */
@Service("lotteryRegService")
public class LotteryRegServiceImpl implements LotteryRegService
{

	@Autowired
	private LotteryRegDao lotteryRegDao;

	@Autowired
	private TrxSoaService trxSoaService;

	private Log logger =LogFactory.getLog(LotteryRegServiceImpl.class);

	@Override
	public LotteryReg processLotteryReg(Long userId) throws VmAccountException, AccountException, StaleObjectStateException, LotteryRegException
	{

		// 中过奖的用户不能再抽奖
		LotteryReg lotteryRegQry = lotteryRegDao.findByUserId(userId);
		if (null != lotteryRegQry)
		{
			throw new LotteryRegException(BaseException.LOTTERY_REG_USER_EXIS);
		}

		// 注册时间 和当前时间 超过24小时不可以抽奖,
		Map<String,Object>  useMap =trxSoaService.findUserInfoById(userId);
		Date registDate =(Date) useMap.get("createDate");
		Long isavalible=Long.parseLong(useMap.get("isavalible").toString());
		Date endDate = DateUtils.toDate(DateUtils.getNextDaytoSen(DateUtils.dateToStr(registDate,"yyyy-MM-dd HH:mm:ss"), "1"), "yyyy-MM-dd HH:mm:ss");
		
		logger.info("++++++++userId:"+userId+"++++registDate:"+registDate+"++++++++endDate:"+endDate+"++++++++++++++++++++++++++");
		if (new Date().after(endDate))
		{
			logger.debug("++++++++userId:"+userId+"+++++LOTTERY_REG_GAP_TIMEOUT+++++");
			throw new LotteryRegException(BaseException.LOTTERY_REG_GAP_TIMEOUT);
		}
		
		if(isavalible!=1){
			throw new LotteryRegException(BaseException.LOTTERY_REG_MOBILE_BINGING);
		}

		String lotteryInitVlaue = "";// 中奖初值
		boolean isLotteryInit = false;// 是否中奖初始值
		int random = (int) (Math.random() * 100);

		Set<String> keySet = TrxConstant.lotteryRegMap.keySet();
		for (String item : keySet)
		{
			String lotRange = TrxConstant.lotteryRegMap.get(item);
			int startNum = Integer.parseInt(lotRange.split("-")[0]);
			int endNum = Integer.parseInt(lotRange.split("-")[1]);

			if (startNum <= random && random <= endNum)
			{
				isLotteryInit = true;
				lotteryInitVlaue = item;// 中奖后赋予中奖值,指定写死1
			}
		}

		// 添加抽奖记录
		LotteryReg lotteryReg = null;
		int lotteryTotal = lotteryRegDao.findLotteryRegTotal();

		if (!isLotteryInit || lotteryTotal >= TrxConstant.LOTTERYREG_TOTAL)
		{
			lotteryInitVlaue = "";
		}

		lotteryReg = new LotteryReg(userId, new Date(), isLotteryInit, String.valueOf(lotteryInitVlaue));
		lotteryRegDao.addLotteryReg(lotteryReg);
		
		
//		if (lotteryInitVlaue > 0)
//		{
//			// 将抽奖获得千品币 存入 虚拟账户
//			VmAccountParamInfo vmAccountParamInfo = new VmAccountParamInfo();
//			String amount = String.valueOf(lotteryInitVlaue);
//			String requestId = guidGenerator.gainCode("DIS");
//			vmAccountParamInfo.setVmAccountId(TrxConstant.LOTTERY_REG_VM_ID);// 虚拟款项ID
//			vmAccountParamInfo.setAmount(amount);
//			vmAccountParamInfo.setRequestId(requestId);
//			vmAccountParamInfo.setUserId(String.valueOf(userId));
//			vmAccountParamInfo.setOperatorId("0");
//			vmAccountParamInfo.setActHistoryType(ActHistoryType.VMDIS);
//			vmAccountParamInfo.setDescription("");
//			vmAccountService.dispatchVm(vmAccountParamInfo);
//		}
		return lotteryReg;
	}

	@Override
	public List<LotteryReg> findLotteryRegList(int recordNumber)
	{
		List<LotteryReg> lotteryRegList = lotteryRegDao.findLotteryRegList(recordNumber);
		if(lotteryRegList!=null && lotteryRegList.size()>0){
		for (LotteryReg lorg : lotteryRegList)
		{
			Map<String,Object>  useMap =trxSoaService.findUserInfoById(lorg.getUserId());

				if (null != useMap && null != useMap.get("email") && StringUtils.validNull(useMap.get("email").toString()))
				{
					String email = useMap.get("email").toString();
					String[] arrayEmail = email.split("@");
					String emailName = arrayEmail[0];
					if (emailName.length() > 5)
					{
						emailName = emailName.substring(0, 5) + "****";
					} else
					{
						emailName = emailName + "****";
					}
					lorg.setUserEmail(emailName);
				}
		}
		}
		return lotteryRegList;
	}

}
