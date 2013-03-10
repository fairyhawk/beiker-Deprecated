package com.beike.core.service.trx.card;

import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.CardException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;

/**
 * @Title:CardService.java
 * @Package com.beike.core.service.trx.card
 * @Description: 千品卡服务类接口
 * @date May 9, 2011 6:13:14 PM
 * @author wh.cheng
 * @version v1.0
 */
public interface CardService {
	/**
	 * 根据卡号查询卡信息
	 * 
	 * @param cardNo
	 * @return
	 */
	public TrxResponseData queryByCardNo(String cardNo, String cardPwd)
			throws CardException;

	/**
	 * 根据卡密及用户ID充值
	 * 
	 * @param cardNo
	 * @param cardPwd
	 * @param userId
	 * @return
	 */
	public TrxResponseData topupByCardInfoAndUserId(String cardNo,
			String cardPwd, Long userId, ReqChannel  reqChannel)
			throws CardException, StaleObjectStateException, AccountException,
			 VmAccountException;
}
