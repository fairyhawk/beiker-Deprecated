package com.beike.biz.service.trx.daemon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.vm.SubAccount;
import com.beike.common.enums.trx.AccountStatus;
import com.beike.common.enums.trx.NotifyType;
import com.beike.core.service.trx.AccountService;
import com.beike.core.service.trx.notify.AccountNotifyService;
import com.beike.dao.notify.AccountNotifyRecordDao;
import com.beike.dao.vm.SubAccountDao;
import com.beike.entity.notify.AccountNotifyRecord;
import com.beike.util.Amount;
import com.beike.util.DateUtils;
import com.beike.util.EnumUtil;
import com.beike.util.ListUtils;
import com.beike.util.TrxConstant;

/**
 * @Title: AccountNotifyDaemon.java
 * @Package com.beike.biz.service.trx
 * @Description: 余额过期数据准备及短信通知定时入口
 * @date 2 13, 2012 6:25:08 AM
 * @author wh.cheng
 * @version v1.0
 */
@Service("accountNotifyDaemon")
public class AccountNotifyDaemon {

	private final Log logger = LogFactory.getLog(AccountNotifyDaemon.class);
	@Autowired
	private AccountNotifyService accountNotifyService;
	@Autowired
	private AccountNotifyRecordDao accountNotifyRecordDao;
	@Autowired
	private SubAccountDao subAccountDao;
	@Autowired
	private AccountService accountService;

	/**
	 * 执行数据准备
	 */
	public void executeNotifyPrepare() {

		logger.info("++++++++executeNotifyPrepare start+++++++++++");

		List<Map<String, Object>> list = accountNotifyService
				.qryAllLoseAccount();

		if (list == null || list.size() == 0) {
			logger.info("++++++++executeNotifyPrepare->size=0 +++++++++++");
			return;
		}
		for (Map<String, Object> map : list) {
			
			Long id = (Long) map.get("id");// 虚拟款项ID
			
			//虚拟款项id为94的不做过期短信提醒     ---------------------818一周年发钱活动
			if(id==94){
				continue;
			}
			
			String notifyType = (String) map.get("notifyType");// 数据准备通知类型：30天或者3天
			
			Map<Integer,Integer> mapInt = new HashMap<Integer,Integer>();//获取10个子虚拟款项表的数量值
			int vmCounts = 0;//总数量
			int maxCount = 0;//单个子虚拟款项最大值
			for(int i=0;i<10;i++){
			int vmCount = subAccountDao.findByVmAccountIdCount(id,i);
			mapInt.put(i, vmCount);
			vmCounts = vmCounts+vmCount;
			if(vmCount>maxCount){
				maxCount = vmCount;
			}
			}
			logger.info("++++++++++++mapInt="+mapInt);
			if(vmCounts==0){//如果总数量为0直接跳出循环处理下个过期虚拟款项
				continue;
			}
			
			int daemonLength = TrxConstant.DAENON_LENGTH/10;
			int length = (maxCount+daemonLength)/daemonLength;//获取本日本虚拟款项循环次数
			logger.info("++++qryAllLoseTrxOrder+++++++date="+new Date()+"+++++++vmAccountid="+id+" count="+length+"+++++++++++++++");
			
			List<SubAccount> subList = new ArrayList<SubAccount>();
			//组装需要执行list并对其进行系统处理
			for(int i=0;i<length;i++){
				int startCount = i*daemonLength;//起步值
				 Set<Integer> key = mapInt.keySet();
				// 组装需要执行subList
			        for (Iterator<Integer> it = key.iterator(); it.hasNext();) {
			        	Integer s = it.next();//虚拟款项表顺序
			        	Integer loseCount = mapInt.get(s);//虚拟款项表对应数量
			        	if(loseCount>startCount){
			        		int endCount = loseCount-startCount>daemonLength?daemonLength:loseCount-startCount;
			        		List<SubAccount> listSub = subAccountDao.findByVmAccountId(id,s,startCount,endCount);
			        		logger.info("++++qryAllLoseTrxOrder++++listSub="+listSub);
			        		subList.addAll(listSub);
			        	}
			        }
			        
			        
			        //查询冻结账户信息
			        StringBuffer accountIdStr = new StringBuffer();
			        for(SubAccount sub : subList){
			        	Long accountId = sub.getAccountId();
			        	accountIdStr.append(accountId);
			        	accountIdStr.append(",");
			        }
			        accountIdStr.deleteCharAt(accountIdStr.length()-1);
			        //查询冻结账户Id List
			        List<Long>  actList =  accountService.findActIdListByIdAndStatus(accountIdStr.toString(), AccountStatus.INACTIVE.name());
			        logger.info("++++++INACTIVE++++++++actList="+actList);
				
			     // 对subList进行系统处理
			if (subList != null) {
				for (SubAccount sub : subList) {

					Long accountId = sub.getAccountId();
					//如果当前账户状态是冻结状态，则跳出。
					if(actList!=null&&actList.size()>0){
						if(actList.contains(accountId)){
							continue;
						}
					}
					
					Long subAccountId = sub.getId();
					Long userId = sub.getUserId();
					double loseBalance = sub.getBalance();
					Date lostDate = sub.getLoseDate();

					AccountNotifyRecord anr = new AccountNotifyRecord(
							accountId, userId, subAccountId, loseBalance,
							false, EnumUtil.transStringToEnum(NotifyType.class,
									notifyType), lostDate);
					accountNotifyService.processNotifyPrepareDate(anr);

					logger.info("++++++++executeNotifyPrepare accountId:"
							+ accountId + "+++subAccountId:" + subAccountId
							+ "+++++userId:" + userId + "++ success!++");

				}

			}
			subList.clear();
			}
			
		}

		logger.info("++++++++executeNotifyPrepare end+++++++++++");

	}

	/**
	 * 执行数据发送
	 */
	@SuppressWarnings("unchecked")
	public void executeNotify() {

		logger.info("++++++++executeNotify  start+++++++++++");
		boolean isNotify = false;
		Map<String, AccountNotifyRecord> anrMap = new HashMap<String, AccountNotifyRecord>();
		// 查询出AccountNotifyRecord表所有没有通知的数据
		int notifyCount = accountNotifyRecordDao.findCountByIsNotify(isNotify);//未通知的总条数
		
		int leng = 0;
		int daemonLength = TrxConstant.DAENON_LENGTH;
		if(notifyCount>0){
			leng = (notifyCount+daemonLength)/daemonLength;
		
		}else{
			return;
		}
		for(int i=0;i<leng;i++){
			int start = i*daemonLength;
			List<AccountNotifyRecord> anrList = accountNotifyRecordDao
			.findByIsNotify(isNotify,start,daemonLength);
		
		if (anrList == null || anrList.size() == 0) {
			logger.info("++++++++accountNotifyDate->size is 0+++++++++++");
			return;
		}

		for (AccountNotifyRecord anr : anrList) {
			Long id = anr.getId();
			
			//00:00:00金额和前一天金额合并
			Date loseDate = anr.getLoseDate();
			String loseDateStr = DateUtils.dateToStrLong(loseDate);
			loseDate = loseDateStr.indexOf("00:00:00") > 0 ? DateUtils
					.toDate(DateUtils.getNextDay(loseDateStr, "-1"),
							"yyyy-MM-dd HH:mm:ss") : loseDate;
			String str = anr.getAccountId() + anr.getNotifyType().name()
					+ DateUtils.toString(loseDate, "yyyy-MM-dd");

			List<Long> idList = new ArrayList<Long>();
			idList.add(id);
			anr.setIdList(idList);// 填充过渡IDList
			anr.setLoseDate(loseDate);//处理后的过期通知时间
			if (anrMap.containsKey(str)) {

				AccountNotifyRecord an = anrMap.get(str);
				an.setLoseBalance(Amount.add(anr.getLoseBalance(), an
						.getLoseBalance()));
				an.setIdList(ListUtils.union(anr.getIdList(), an.getIdList()));// List并集

				anrMap.put(str, an);

			} else {
				anrMap.put(str, anr);
			}
		}
		logger.info("+++++++++++++++++++++++++++++++++++++" + anrMap.size());

		Set<String> key = anrMap.keySet();

		for (String item : key) {
			accountNotifyService.processNotifySms(anrMap.get(item));
		}

		logger.info("++++++++executeNotify  end+++++++++++");
		}
	}
	
}
