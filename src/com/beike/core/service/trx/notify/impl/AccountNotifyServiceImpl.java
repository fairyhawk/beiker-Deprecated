package com.beike.core.service.trx.notify.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.Account;
import com.beike.common.entity.vm.SubAccount;
import com.beike.common.enums.trx.AccountType;
import com.beike.common.enums.trx.NotifyType;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.core.service.trx.notify.AccountNotifyService;
import com.beike.dao.notify.AccountNotifyRecordDao;
import com.beike.dao.trx.AccountDao;
import com.beike.dao.trx.soa.proxy.UserSoaDao;
import com.beike.dao.vm.SubAccountDao;
import com.beike.dao.vm.VmAccountDao;
import com.beike.entity.common.Sms;
import com.beike.entity.notify.AccountNotifyRecord;
import com.beike.form.SmsInfo;
import com.beike.service.common.SmsService;
import com.beike.util.Amount;
import com.beike.util.Constant;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.StringUtils;

/**
 * @Title: AccountNotifyServiceImpl.java
 * @Package com.beike.core.service.trx.notify.impl
 * @Description: 帐户余额过期提醒服务实现类
 * @author wh.cheng@sinobogroup.com
 * @date 13,2, 2012 6:00:18 PM
 * @version V1.0
 */
@Service("accountNotifyService")
public class AccountNotifyServiceImpl implements AccountNotifyService {

	@Autowired
	private VmAccountDao vmAccountDao;
	@Autowired
	private SubAccountDao subAccountDao;
	@Autowired
	private AccountNotifyRecordDao accountNotifyRecordDao;
	@Autowired
	private UserSoaDao userSoaDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private SmsService smsService;

	private final Log logger = LogFactory
			.getLog(AccountNotifyServiceImpl.class);

	public List<Map<String, Object>> qryAllLoseAccount() {
		String strDate = DateUtils.getStringDate();

		String loseToNow30DateStr = DateUtils.getNextDay(strDate, "30");// 当前时间后推30天
		String loseToNow3DateStr = DateUtils.getNextDay(strDate, "3");// 当前时间后推3天

		Date loseToNow3Date = DateUtils.toDate(loseToNow3DateStr,
				"yyyy-MM-dd HH:mm:ss");
		Date loseToNow30Date = DateUtils.toDate(loseToNow30DateStr,
				"yyyy-MM-dd HH:mm:ss");

		logger.info("++++++++strDate:" + strDate + "loseToNow30DateStr:"
				+ loseToNow30DateStr + "+++loseToNow3DateStr:"
				+ loseToNow3DateStr + "+++++++++");

		List<Map<String, Object>> list = vmAccountDao.findLoseDate(
				loseToNow30Date, loseToNow3Date);

		if (list != null && list.size() > 0) {

			for (Map<String, Object> map : list) {

				Date lostDate = (Date) map.get("lose_date");

				String sqlDate = DateUtils.toString(lostDate,
						"yyyy-MM-dd HH:mm:ss");
				String twoDay = DateUtils.getTwoDay(sqlDate, strDate);// 两个日期间隔天数

				if ("30".equals(twoDay)) {
					map.put("notifyType", EnumUtil
							.transEnumToString(NotifyType.THIRTY));

				} else if ("3".equals(twoDay)) {
					map.put("notifyType", EnumUtil
							.transEnumToString(NotifyType.THREE));

				}

			}

		}
		return list;
	}

	public void processNotifyPrepareDate(AccountNotifyRecord anr) {
		AccountNotifyRecord accountnr = accountNotifyRecordDao.findByS(anr
				.getAccountId(), anr.getSubAccountId(), anr.getNotifyType());
		Long userId = anr.getUserId();
		Map<String, Object> userMap = userSoaDao.findMobileById(userId);
		String mobile = "";// 手机号
		if (userMap != null) {
			mobile = (String) userMap.get("mobile");
		}

		if (accountnr == null && mobile != null && mobile.length() > 0) {
			accountNotifyRecordDao.addAccountNotifyRecord(anr);
		}
	}

	public void processNotifySms(AccountNotifyRecord anr) {
		String hour = DateUtils.getHour();
		int hourInt = Integer.valueOf(hour);// 发送短信时间为9:00--22:00之间
		List<Long> idList = anr.getIdList();
		StringBuilder sb = new StringBuilder();
		if (anr != null && 8 < hourInt && hourInt < 22) {
			try {
				Long userId = anr.getUserId();
				Map<String, Object> userMap = userSoaDao.findMobileById(userId);
				String mobile = "";// 手机号
				if (userMap != null) {
					mobile = (String) userMap.get("mobile");
				}
				if (mobile != null && mobile.length() > 0) {
					double balance = anr.getLoseBalance();// 过期金额
					Date loseDate = anr.getLoseDate();// 过期时间
					String loseDateStr = DateUtils.dateToStrLong(loseDate);

					String strLoseDate = DateUtils.toString(loseDate, "M月d日");

					String smsTemTitle = "";// 数据库插入模板后，在这定义个常量，如果3天30天一个模板这不用判断
					Object[] smsParam = null;// 根据3天30天组装数组
					if (NotifyType.THIRTY.equals(anr.getNotifyType())) {
						smsParam = new Object[] { strLoseDate };
						smsTemTitle = Constant.SMSACCOUNTNOTIFY_THIRTY;
					} else if (NotifyType.THREE.equals(anr.getNotifyType())) {
						smsParam = new Object[] { strLoseDate };
						smsTemTitle = Constant.SMSACCOUNTNOTIFY_THREE;
					}
					Sms sms = smsService.getSmsByTitle(smsTemTitle);

					String template = sms.getSmscontent(); // 获取短信模板
					String contentResult = MessageFormat.format(template,
							smsParam);
					SmsInfo sourceBean = new SmsInfo(mobile, contentResult,
							"15", "1");
					smsService.sendSms(sourceBean);
					// 获取短信实体

					// id有List转换为Str
					for (Long id : idList) {
						sb.append(String.valueOf(id));
						sb.append(",");
					}
					sb.deleteCharAt(sb.length() - 1);

					// 批量更新(避免相同用户过期时间金额合并时再做一次查询)
					accountNotifyRecordDao.updateAccountNotifyById(sb
							.toString());
					logger
							.info("+++loseAccountSmsNotify:accountNotifyRecordId:"
									+ sb.toString()
									+ "+++++++++userId:"
									+ userId
									+ "+++++++++mobile："
									+ mobile
									+ "+++++++loseBalance:"
									+ balance
									+ "+++++loseDateStr: "
									+ loseDateStr
									+ "+++++++++success!++++++++++");

				}
			} catch (StaleObjectStateException e) {
				e.printStackTrace();

				logger.debug("+++++++++" + e);
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug("+++++++++" + e);
			}

		}

	}

	@Override
	public List<SubAccount> getRemindAccountBalance(Long userId) {
		// 获取 vc 类型的 account 对象
		Account account = accountDao
				.findByUserIdAndType(userId, AccountType.VC);
		List<SubAccount> remindList = new ArrayList<SubAccount>();

		if (account != null && account.getBalance() > 0) {
			long accountId = account.getId();
			// 查询30天到期的余额
			String currentDate = DateUtils.getStringDate();
			Date beginDate = DateUtils.toDate(currentDate,
					"yyyy-MM-dd HH:mm:ss");
			Date currentDateFormat = DateUtils
					.toDate(currentDate, "yyyy-MM-dd");// //当前时间（精确到day）
			String endDateStr = DateUtils.getNextDay(currentDate, "30");
			Date endDate = DateUtils.toDate(endDateStr, "yyyy-MM-dd HH:mm:ss");
			Long subAccountSuffix = StringUtils.getDeliveryIdBase(accountId);
			remindList = subAccountDao.findRemindListByActId(accountId,
					subAccountSuffix + "", beginDate, endDate);
			if (null != remindList && remindList.size() > 0) {
				// remindList元素中过期时间处理
				int index = remindList.size() - 1;
				for (int i = index; i >= 0; i--) {
					SubAccount subAccount = remindList.get(i);
					Date loseDate = subAccount.getLoseDate();
					// 如果过期时间点为时分秒为00:00:00，则提示用户时，过期时间前移一天
					String loseDateStr = DateUtils.dateToStrLong(loseDate);
					loseDate = loseDateStr.indexOf("00:00:00") > 0 ? DateUtils
							.toDate(DateUtils.getNextDay(loseDateStr, "-1"),
									"yyyy-MM-dd HH:mm:ss") : loseDate;
					Date loseDateFormat = DateUtils.toDate(DateUtils.toString(
							loseDate, "yyyy-MM-dd"), "yyyy-MM-dd");// 过期时间（精确到day）
					subAccount.setLoseDate(loseDateFormat);
					if (loseDateFormat.before(currentDateFormat)) {
						// 如果过期时间在当前 时间 之前则删除元素，防止余额过期时报乐观锁过期顺延造成提示时过期时间在当前时间之前
						remindList.remove(i);
					}
				}
				remindList = mergeList(remindList);
			}
		}

		return remindList;
	}

	/**
	 * List 部分属性相同的元素合并 .
	 * 
	 * @param list
	 */
	public List<SubAccount> mergeList(List<SubAccount> list) {

		LinkedHashMap<Date, SubAccount> map = new LinkedHashMap<Date, SubAccount>();
		for (SubAccount item : list) {
			Date loseDate = item.getLoseDate();
			if (map.containsKey(loseDate)) {
				item.setBalance(Amount.cutOff(Amount.add(map.get(loseDate)
						.getBalance(), item.getBalance()), 2));
			}
			map.put(loseDate, item);
		}
		list.clear();
		list.addAll(map.values());

		return list;
	}

}