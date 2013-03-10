package com.beike.core.service.trx.card.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.TrxResponseData;
import com.beike.common.bean.trx.VmAccountParamInfo;
import com.beike.common.entity.card.Card;
import com.beike.common.entity.trx.Account;
import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.BizType;
import com.beike.common.enums.trx.CardStatus;
import com.beike.common.enums.trx.ReqChannel;
import com.beike.common.exception.AccountException;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.CardException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.common.exception.VmAccountException;
import com.beike.common.guid.GuidGenerator;
import com.beike.core.service.trx.card.CardService;
import com.beike.core.service.trx.vm.VmAccountService;
import com.beike.dao.card.CardDao;
import com.beike.dao.trx.AccountDao;
import com.beike.util.DateUtils;
import com.beike.util.Des3Encryption;
import com.beike.util.EnumUtil;
import com.beike.util.FileUtils;
import com.beike.util.PropertyUtil;

/**
 * @Title:CardServiceImpl.java
 * @Package com.beike.core.service.trx.card.impl
 * @Description: 千品卡服务类实现类
 * @date May 9, 2011 6:13:14 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("cardService")
public class CardServiceImpl implements CardService {

	private static Log logger = LogFactory.getLog(CardServiceImpl.class);
	PropertyUtil propertyUtil = PropertyUtil.getInstance("project");
	public String cardDesKeyFilePath = propertyUtil
			.getProperty("card_triple_des_key_file");

	public static String cardDesKeySbf = new String();
	@Autowired
	private CardDao cardDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private VmAccountService vmAccountService;
	@Resource(name = "guidGeneratorService")
	private GuidGenerator guidGenerator;

	@Override
	public TrxResponseData queryByCardNo(String cardNo, String cardPwd) throws CardException
	{

		logger.info("++++++++++++++cardNo=" + cardNo + ";cardPwd" + cardPwd + "+++++++");
		String cardDesKeyStr = getCardKey();// 获取解密密钥

		String cardPwdByDes = Des3Encryption.encode(cardDesKeyStr, cardPwd);// 加密

		logger.info("++++++++++++++cardNo=" + cardNo + ";cardPwdDes" + cardPwdByDes + "+++++++");

		Card card = cardDao.findByCardNo(cardNo, cardPwdByDes);

		if (card == null)
		{
			throw new CardException(BaseException.CARD_NO_PWD_INVALID);// 卡密不符合
		}

		Map<String, String> cardMap = checkCardInfo(card);
		
		TrxResponseData  trxResponseData = new TrxResponseData();
		trxResponseData.setCardValue(cardMap.get("cardValue"));
		trxResponseData.setLoseDate(cardMap.get("loseDate"));
		
		return trxResponseData;
	}

	@Override
	public TrxResponseData topupByCardInfoAndUserId(String cardNo, String cardPwd, Long userId, ReqChannel cardTopupChannel) throws CardException, StaleObjectStateException, AccountException, VmAccountException
	{
		logger.info("++++++++++++++cardNo=" + cardNo + ";cardPwd" + cardPwd + "+++++++");
		List<Account> accountList = accountDao.findByUserId(userId);

		for (Account item : accountList)
		{
			if (!AccountStatus.ACTIVE.equals(item.getAccountStatus()) || !(AccountType.VC.equals(item.getAccountType()) || AccountType.CASH.equals(item.getAccountType())))
			{
				throw new AccountException(BaseException.ACCOUNT_NOT_FOUND);
			}
		}

		String cardDesKeyStr = getCardKey();// 获取解密密钥

		String cardPwdByDes = Des3Encryption.encode(cardDesKeyStr, cardPwd);// 加密

		logger.info("++++++++++++++cardNo=" + cardNo + ";cardPwdDes" + cardPwdByDes + "+++++++");

		Card card = cardDao.findByCardNo(cardNo, cardPwdByDes);
		if (card == null)
		{
			throw new CardException(BaseException.CARD_NO_PWD_INVALID);
		}
		 checkCardInfo(card);// 卡信息校验 

		// 当满足状态是激活时进行UPDATE
		card.setUserId(userId);
		card.setCardStatus(CardStatus.USED);
		card.setUpdateDate(new Date());
		card.setTopupChannel(EnumUtil.transEnumToString(cardTopupChannel));

		cardDao.updateCard(card);

		// 下发操作
		String requestId = guidGenerator.gainCode("DIS");
		VmAccountParamInfo vmAccountParamInfo = new VmAccountParamInfo();
		vmAccountParamInfo.setVmAccountId(card.getVmAccountId().toString());
		vmAccountParamInfo.setAmount(String.valueOf(card.getCardValue()));
		vmAccountParamInfo.setRequestId(requestId);
		vmAccountParamInfo.setUserId(userId.toString());
		vmAccountParamInfo.setOperatorId("0");
		vmAccountParamInfo.setActHistoryType(ActHistoryType.VMDIS);
		vmAccountParamInfo.setBizType(BizType.CARDLOAD);
		int result = vmAccountService.dispatchVmForCard(vmAccountParamInfo);

		if (result != 1)
		{
			throw new CardException(BaseException.CARD_STATUS_INVALID);

		}
		// 查询出当前账户余额
		double actAmount = accountDao.findBalanceByStatus(userId, AccountStatus.ACTIVE);

		logger.info("+++++cardNo" + card.getCardNo() + "cardPwd:" + card.getCardPwd() + "topup Success!  By userId+" + userId + "+++++++");

		
		TrxResponseData  trxResponseData = new TrxResponseData();
		trxResponseData.setCardValue(String.valueOf(card.getCardValue()));
		trxResponseData.setBalance(actAmount);
		return trxResponseData;
	}

	/**
	 * 卡检查共同子方法
	 * 
	 * @param card
	 * @return
	 * @throws CardException
	 */
	public Map<String, String> checkCardInfo(Card card) throws CardException {
		String cardNo = card.getCardNo();
		String cardPwd = card.getCardPwd();
		String cardValue = String.valueOf(card.getCardValue());
		String vmAccountId = String.valueOf(card.getVmAccountId());
		Map<String, String> cardMap = new HashMap<String, String>();
		logger.info("++++++++++++++cardNo=" + cardNo + ";cardPwd" + cardPwd
				+ ";card_value=" + card.getCardValue() + ";card_status="
				+ EnumUtil.transEnumToString(card.getCardStatus())
				+ "vm_account_id:" + vmAccountId + "+++++++");

		if (CardStatus.ACTIVE.equals(card.getCardStatus())) {

			String loseDateStr = DateUtils.toString(card.getLoseDate(),
					"yyyy-MM-dd");
			// cardMap.put("cardNo", cardNo);
			cardMap.put("loseDate", loseDateStr);
			cardMap.put("cardValue", cardValue);

			logger.info("++++++++loseDate:" + loseDateStr + "++++++");

		} else if (CardStatus.USED.equals(card.getCardStatus())) {
			throw new CardException(BaseException.CARD_HAS_USED);// 卡被使用
		} else if (CardStatus.TIMEOUT.equals(card.getCardStatus())) {
			throw new CardException(BaseException.CARD_HAS_EXPIRED);// 卡过期
		} else {
			throw new CardException(BaseException.CARD_STATUS_INVALID);
		}

		return cardMap;

	}

	/**
	 * 获取千品卡卡密加密/解密Key
	 * 
	 * @return
	 * @throws CardException
	 */

	public String getCardKey() throws CardException {

		String cardDesKeyStr = cardDesKeySbf;
		String newGetKey = "";
		if (cardDesKeyStr.length() == 0) {
			// 到配置文件里去取
			newGetKey = FileUtils.getFileContent(cardDesKeyFilePath);
			// 若还为空，则抛异常
			int newGetKeyCount = newGetKey.length();

			if (newGetKeyCount == 0) {
				logger
						.debug("++++++++++++++I am  gain key size 0 :++++++++++newGetKeyCount:"
								+ newGetKeyCount + "++++++++");
				throw new CardException(BaseException.CADR_DES_KEY_NOT_FOUND);

			} else if (newGetKeyCount < 24) { // 不足24位前面补0

				StringBuffer keySb = new StringBuffer();
				int gapKeyCount = 24 - newGetKeyCount;// 补0位数
				for (int i = 0; i < gapKeyCount; i++) {
					keySb.append("0");// 前面补0
				}
				logger.info("++++++++++++++I am adding '0':" + keySb.toString()
						+ "++++++++++newGetKeyCount:" + newGetKeyCount
						+ "++++++++");
				keySb.append(newGetKey);
				newGetKey = keySb.toString();

			} else {// 超出或者等于24位，截取前24位

				newGetKey = newGetKey.substring(0, 24);
				logger
						.info("++++++++++++++I am  cutting subStr [0-24):++++++++++newGetKeyCount:"
								+ newGetKeyCount + "++++++++");
			}

			cardDesKeySbf = newGetKey;
		}

		return cardDesKeySbf;

	}

}
