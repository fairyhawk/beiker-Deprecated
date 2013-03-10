package com.beike.core.service.trx.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.biz.service.trx.OrderFilmService;
import com.beike.common.entity.trx.AccountHistory;
import com.beike.common.entity.trx.Payment;
import com.beike.common.entity.trx.RefundRecord;
import com.beike.common.entity.trx.TrxOrder;
import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.ActHistoryType;
import com.beike.common.enums.trx.BizType;
import com.beike.common.enums.trx.PaymentType;
import com.beike.core.service.trx.AccountHistoryService;
import com.beike.dao.trx.AccountHistoryDao;
import com.beike.dao.trx.PaymentDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.dao.trx.soa.proxy.GoodsSoaDao;
import com.beike.util.BankInfoUtil;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;

@Service("accountHistoryService")
public class AccountHistoryServiceImpl implements AccountHistoryService {

	private static Log logger = LogFactory
			.getLog(AccountHistoryServiceImpl.class);
	@Autowired
	private PaymentDao paymentDao;

	@Autowired
	private TrxorderGoodsDao trxorderGoodsDao;
	@Autowired
	private AccountHistoryDao accountHistoryDao;
	@Autowired
	private GoodsSoaDao goodsSoaDao;
	@Autowired
	private OrderFilmService orderFilmService;

	/**
	 * 根据交易Id和交易类型获取payment对象
	 */
	public Payment findPaymentByUserIdAndType(Long trxId,
			PaymentType paymentType) {
		Payment payment = paymentDao.findByTrxIdAndType(trxId, paymentType);
		return payment;
	}

	public List<TrxorderGoods> findTxGoodsByTrxOrderId(Long trxId) {
		List<TrxorderGoods> tgLisg = trxorderGoodsDao.findByTrxId(trxId);
		return tgLisg;
	}

	public List<TrxorderGoods> findRabateByTrxId(Long trxId) {
		List<TrxorderGoods> resList = accountHistoryDao
				.findRabateByTrxId(trxId);
		return resList;
	}

	public List<RefundRecord> getRefundDetailByTreOrderId(Long trxOrderId) {
		List<RefundRecord> rsList = accountHistoryDao
				.findRefundInfoByTrxOrderId(trxOrderId);
		return rsList;
	}

	public List<TrxorderGoods> findGoodsIdByTrxOrderId(Long trxOrderId) {
		List<TrxorderGoods> rsList = trxorderGoodsDao.findByTrxId(trxOrderId);
		
		return rsList;
	}

	public List<TrxorderGoods> findGoodsById(long id) {
		List<TrxorderGoods> rsList = accountHistoryDao.findGoodsById(id);
		return rsList;
	}

	@Override
	public List<AccountHistory> getHistoryInfoByUserId(Long userId) {

		// 获取账户id，beiker_account表中通过userId查询
		List<String> accIdList = accountHistoryDao.findAccIdByUserId(userId);
		StringBuffer accStr = new StringBuffer();
		if (accIdList == null) {
			return null;
		}
		for (String accId : accIdList) {
			if (accId != null) {
				accStr.append(accId).append(",");
			}
		}
		if (accStr.length() != 0) {
			accStr = accStr.delete(accStr.length() - 1, accStr.length());
		}

		// 查询beiker_accounthistory表中信息
		List<AccountHistory> resultList = accountHistoryDao
				.findAccounthistoryByAccId(accStr.toString());

		List<AccountHistory> accList = null;
		if (resultList != null) {
			for (AccountHistory acchObj : resultList) {
				List<TrxOrder> toList = accountHistoryDao
						.findTrxOrderObjById(acchObj.getTrxOrderId());
				if (toList != null) {
					TrxOrder tmpObj = toList.get(0);
					acchObj.setOrdAmount(tmpObj.getOrdAmount());
					acchObj.setRequestId(tmpObj.getRequestId());
					acchObj.setDescription(tmpObj.getDescription());
					acchObj.setExternalId(tmpObj.getExternalId());
					acchObj.setTrxStatus(tmpObj.getTrxStatus().toString());
				}
			}

			long refundFlagId = 0;// 退款时，相同trxOrderId的时候，代表其trxOrderId，当trxOrderId改变时，修改为当前trxOrderId
			long trxIdFlag = 0; // 在历史表中，有交易Id相同这种情况出现，当出现这种情况时，只使用一条此Id的记录，剩余不使用
			int accFlag = 0; // 代表退款时退款数据数组的index
			long salesFlag = 0; // 购买时，同一个trx_order_id只能到beiker_trxorder_goods表中查询一次，通过此变量标志来处理
			accList = new ArrayList<AccountHistory>();
			for (AccountHistory acch : resultList) {
				long trxOrderId = acch.getTrxOrderId();
				long trxId = 0L;
				if (ActHistoryType.RABATE.equals(acch.getActHistoryType())) {
					if ("rebate".equals(acch.getDescription())) {
						trxId = 0L;
					} else {
						trxId = Long.valueOf(acch.getDescription().toString());// 12
						// 11改动为商品详情表ID
					}
				}
				ActHistoryType actHistoryType = acch.getActHistoryType();
				if (actHistoryType.equals(ActHistoryType.LOAD)) {
					Payment payment = null;
					if(trxOrderId==0){
						payment = paymentDao.findById(acch.getTrxId());
					}else{
					 payment = findPaymentByUserIdAndType(trxOrderId,
							PaymentType.PAYCASH);
					}
					if (payment != null) {
						if (payment.getPayChannel() != null) {
							payment.setPayChannelName(BankInfoUtil
									.getInstanceForBankMap().get(
											payment.getProviderType().name()+"-"+payment.getPayChannel()));
						}
						if (payment.getProviderType() != null) {
							payment.setProviderName(BankInfoUtil
									.getInstanceForPayMap().get(
											payment.getProviderType()
													.toString()));
						}
					}
					acch.setPayment(payment);
					accList.add(acch);
				} else if (actHistoryType.equals(ActHistoryType.SALES)) {
					if (salesFlag != 0 && salesFlag == trxOrderId) {
						continue;
					}

					salesFlag = trxOrderId; // 标志位赋值，当trxOrderId变化时，标志位重新赋值
					List<TrxorderGoods> togList = findGoodsIdByTrxOrderId(trxOrderId);
					if (togList != null) {
						for (TrxorderGoods trxo : togList) {
							//如果是网上选座 add by ljp 20121212
							if(trxo.getBizType() == 2){
								try {
									Long	cinemaId = orderFilmService.queryCinemaIdByTrxGoodsId(trxo.getId());
									trxo.setCinemaId(cinemaId);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							AccountHistory tmpAcch = cloneAcchObj(acch);
							List<TrxorderGoods> trxorderGoodsList = new ArrayList<TrxorderGoods>();
							trxorderGoodsList.add(trxo);
							String name = trxo.getGoodsName();
							trxo.setGoodsName(StringUtils.cutffStr(name, 10,
									"..."));
							tmpAcch.setTrxAmount(trxo.getPayPrice());
							tmpAcch.setTrxOrderGoodsList(trxorderGoodsList);
							accList.add(tmpAcch);
						}
					} else {
						logger
								.info("can't find the TrxorderGoods by trxorder_id "
										+ trxOrderId);
						acch.setTrxOrderGoodsList(togList);
						accList.add(acch);
					}
				} else if (actHistoryType.equals(ActHistoryType.REFUND)) {
					if (refundFlagId == 0 || refundFlagId != trxOrderId) {
						refundFlagId = trxOrderId;
						accFlag = 0;
					}
					// 如果trxId未发生变化且不为初始状态，继续循环
					if (trxIdFlag != 0 && trxIdFlag == trxId) {
						continue;
					}

					trxIdFlag = trxId;
					List<RefundRecord> rrList = getRefundDetailByTreOrderId(trxOrderId);
					if (rrList != null && rrList.size() > accFlag) {
						RefundRecord rr = rrList.get(accFlag);

						List<TrxorderGoods> togList = findGoodsById(rr
								.getTrxGoodsId());
						TrxorderGoods trxorderGoods = null;

						if (togList != null) {
							trxorderGoods = togList.get(0);
							//如果是网上选座 add by ljp 20121212
							if(trxorderGoods.getBizType() == 2){
								Map<String, Object> cinema = goodsSoaDao.getCinemaIdByTrxGoodsId(trxorderGoods.getId());
								trxorderGoods.setCinemaId((Long)cinema.get("cinema_id"));
							}
							trxorderGoods.setGoodsName(StringUtils.cutffStr(
									trxorderGoods.getGoodsName(), 10, "..."));
						}
						List<TrxorderGoods> trxorderGoodsList = new ArrayList<TrxorderGoods>();
						trxorderGoodsList.add(trxorderGoods);
						acch.setTrxAmount(rr.getTrxGoodsAmount());// 存放交易金额
						acch.setTrxOrderGoodsList(trxorderGoodsList);
						accList.add(acch);
						accFlag++;
					}
				} else if (actHistoryType.equals(ActHistoryType.RABATE)) {
					List<TrxorderGoods> tmpList = findRabateByTrxId(trxId);
					if (tmpList != null) {
						for (TrxorderGoods tog : tmpList) {
							String name = tog.getGoodsName();
							tog.setGoodsName(StringUtils.cutffStr(name, 10,
									"..."));
						}
					} else {
						logger.info("can't find the TrxorderGoods by ID "
								+ trxId);
					}
					acch.setTrxOrderGoodsList(tmpList);
					accList.add(acch);
				} else {
					accList.add(acch);
				}
			}
		}

		return accList;
	}

	/**
	 * 卖出商品时，因为相同对象要复制成好几份，所以使用此方法尽心对象复制
	 * 
	 * @param acch
	 *            历史交易记录对象
	 * @return 返回历史交易记录对象
	 */
	public AccountHistory cloneAcchObj(AccountHistory acch) {
		AccountHistory tmpAcch = new AccountHistory();

		tmpAcch.setId(acch.getId());
		tmpAcch.setAccountId(acch.getAccountId());
		tmpAcch.setActHistoryType(acch.getActHistoryType());
		tmpAcch.setBalance(acch.getBalance());
		tmpAcch.setBizType(acch.getBizType());
		tmpAcch.setCreateDate(acch.getCreateDate());
		tmpAcch.setDescription(acch.getDescription());
		tmpAcch.setDispaly(acch.isDispaly());
		tmpAcch.setTrxId(acch.getTrxId());
		tmpAcch.setTrxAmount(acch.getTrxAmount());
		tmpAcch.setTrxOrderId(acch.getTrxOrderId());
		return tmpAcch;

	}

	@Override
	public List<AccountHistory> listAccountHistory(Long actId) {

		String actHistoryType = "'" + ActHistoryType.INSIDEREBATE.name()
				+ "','" + ActHistoryType.RABATE.name() + "','"
				+ ActHistoryType.VMDIS.name() + "'";

		List<AccountHistory> listAccHistory = accountHistoryDao
				.findAccountIdByActType(actId, actHistoryType);
		if (listAccHistory != null) {
			int actHisListMaxIndex = listAccHistory.size() - 1;

			for (int i = actHisListMaxIndex; i >= 0; i--) {
				AccountHistory item = listAccHistory.get(i);
				ActHistoryType actHistoryTypeEnum = item.getActHistoryType();
				String actHistoryBizType = item.getBizType();
				if (ActHistoryType.VMDIS.equals(actHistoryTypeEnum)
						&& EnumUtil.transEnumToString(BizType.CARDLOAD).equals(
								actHistoryBizType)) {

					listAccHistory.remove(item);

				}

			}
		}
		if (listAccHistory != null) {
			for (int i = 0; i < listAccHistory.size(); i++) {
				AccountHistory accHistory = listAccHistory.get(i);
				if (ActHistoryType.RABATE
						.equals(accHistory.getActHistoryType())) {
					if ("rebate".equals(accHistory.getDescription())) {

					} else {
						TrxorderGoods trxOrderGoods = trxorderGoodsDao
								.findById(Long.valueOf(accHistory
										.getDescription().trim()));
						accHistory.setGoodsId(trxOrderGoods.getGoodsId());
						String name = trxOrderGoods.getGoodsName();
						accHistory.setGoodsName(StringUtils.cutffStr(name, 10,
								"..."));
					}
				}
			}
		}

		return listAccHistory;
	}
}
