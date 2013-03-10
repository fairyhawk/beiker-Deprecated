package com.beike.core.service.trx.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.Voucher;
import com.beike.common.enums.trx.VoucherStatus;
import com.beike.core.service.trx.VoucherPrefetchService;
import com.beike.dao.trx.VoucherDao;
import com.beike.entity.common.Sms;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.util.TrxConstant;

/**
 * @Title: VoucherPrefetchServiceImpl.java
 * @Package com.beike.core.service.trx.impl
 * @Description: 凭证预取服务接口实现类(新起事务，独立出来使AOP生效)
 * @date 4 1, 2012 6:31:57 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("voucherPrefetchService")
public class VoucherPrefetchServiceImpl implements VoucherPrefetchService {
	@Autowired
	private SmsService smsService;
	@Autowired
	private VoucherDao voucherDao;

	private final Log logger = LogFactory
			.getLog(VoucherPrefetchServiceImpl.class);

	/**
	 * 凭证预取 新起事务. 对入口事务时间没有减少，但需加快for update以及预取后的update的事务提交时间，尽快为集群其它服务器释放DB
	 * 锁资源。 变更为需要事务 mysql for update 锁区间，否则会有死锁问题
	 * 
	 * @param prefetchCount
	 * @return
	 */
	public List<Voucher> preFetchVoucher(int prefetchCount) {
		logger.info("+++vouPrefetch ->findBatchVoucherForPre+++++  ");
		List<Voucher> voucherList = voucherDao.findBatchVoucherForPre(
				VoucherStatus.INIT, 0, prefetchCount);
		if (voucherList == null || voucherList.size() == 0) {
			// 预取失败报警
			logger.info("+++vouPrefetch ->voucherList in DB is null+++++  ");
			alertVouPrefetch("预取凭证为空");
		} else {
			int voucherListCount = voucherList.size();
			List<Long> vouIdList = new ArrayList<Long>();
			for (Voucher item : voucherList) {
				vouIdList.add(item.getId());

			}
			int result = voucherDao.updateBatchVoucherForPre(vouIdList,
					VoucherStatus.INIT, 1, 0);
			logger.info("+++vouPrefetch in DB->voucherListCount="
					+ voucherListCount + "+++result" + result + "+++++  ");
			// 预取更新不完整
			if (voucherListCount != result) {
				alertVouPrefetch("预取更新不完整");
			}

		}

		return voucherList;

	}

	/**
	 * 凭证预取报警
	 * 
	 * @param alterParam
	 */
	public void alertVouPrefetch(String alterParam) {
		try {
			String alterVouPrefetchTel = TrxConstant.alterVouPrefetchTel;
			if (alterVouPrefetchTel == null
					|| alterVouPrefetchTel.length() == 0) {

				return;
			}
			String[] alterVouPrefetchTelAry = alterVouPrefetchTel.split(",");
			int aryCount = alterVouPrefetchTelAry.length;

			// 短信参数
			Object[] smsParam = new Object[] { alterParam };

			Sms sms = smsService
					.getSmsByTitle(TrxConstant.VOUCHER_PREFETCH_ALTER_SMS_TEMPLATE);// 获取短信实体
			String template = sms.getSmscontent(); // 获取短信模板

			String contentResult = MessageFormat.format(template, smsParam);
			for (int i = 0; i < aryCount; i++) {
				String mobile = alterVouPrefetchTelAry[i];
				SmsInfo sourceBean = new SmsInfo(mobile, contentResult, "15",
						"1");

				logger.info("+++++++++++ alterVouPrefetchTel:mobile:" + mobile
						+ "+++alterParam:" + alterParam + "++++++++");

				smsService.sendSms(sourceBean);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e);

		}
	}

}
