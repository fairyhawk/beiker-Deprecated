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
import com.beike.common.exception.BaseException;
import com.beike.core.service.trx.vm.SubAccountService;
import com.beike.dao.vm.SubAccountDao;
import com.beike.util.TrxConstant;

/**
 * @author yurenli
 * @version 1.0
 * @date 2011-11-24 21:33:20
 */
@Service("vmCancelRecordDaemon")
public class VmCancelRecordDaemon {

	public Log logger = LogFactory.getLog(VmCancelRecordDaemon.class);
	@Autowired
	private SubAccountService subAccountService;
	@Autowired
	private SubAccountDao subAccountDao;
	

	public void executeVmCancer() {
		logger.info("++++++++++++++++start auto VmCancelRecordDaemon  time:"
				+ new Date() + "+++++++++++++++++++++");
		// 查询当日之前已过期的子账户
		
		Map<Integer,Integer> mapInt = new HashMap<Integer,Integer>();//获取10个子虚拟款项表的数量值
		int vmCounts = 0;//总数量
		int maxCount = 0;//单个子虚拟款项最大值
		for(int i=0;i<10;i++){
		int vmCount = subAccountDao.findByLoseCount(new Date(), i);
		mapInt.put(i, vmCount);
		vmCounts = vmCounts+vmCount;
		if(vmCount>maxCount){
			maxCount = vmCount;
		}
		}
		logger.info("++++++++++++++++++++VmCancelRecordDaemon="+mapInt);
		if(vmCounts==0){//如果总数量为0直接跳出循环处理下个过期虚拟款项
			return;
		}
		
		int daemonLength = TrxConstant.DAENON_LENGTH/10;
		int length = (maxCount+daemonLength)/daemonLength;//获取本日本虚拟款项循环次数
		logger.info("++++qryAllLoseTrxOrder+++++++date="+new Date()+"+++++++vmAccountid count="+length+"+++++++++++++++");
		
		
		
		//组装需要执行list并对其进行系统处理
		for(int i=0;i<length;i++){
			List<SubAccount> subList = new ArrayList<SubAccount>();
			int startCount = i*daemonLength;//起步值
			 Set<Integer> key = mapInt.keySet();
			// 组装需要执行subList
		        for (Iterator<Integer> it = key.iterator(); it.hasNext();) {
		        	Integer s = (Integer) it.next();//虚拟款项表顺序
		        	Integer loseCount = mapInt.get(s);//虚拟款项表对应数量
		        	if(loseCount>startCount){
		        		int endCount = loseCount-startCount>daemonLength?daemonLength:loseCount-startCount;
		        		List<SubAccount> listSub = subAccountDao.findByLose(new Date(),s,0,endCount);//起步值始终设置为0，因为第二次查询数据值有变化
		        		subList.addAll(listSub);
		        	}
		        }
		
		        
		     // 对subList进行系统处理
		for (SubAccount item : subList) {
			logger.info("++++++++++++++++start item expired :"
					+ "SubAccountID:" + item.getId() + "++++++++++"
					+ "AccountID:" + item.getAccountId()
					+ "++++++++++++ amount" + item.getBalance());
			try {
				subAccountService.cancelLose(item);
			} catch (BaseException e) {
				e.printStackTrace();
				logger.error(e);
			}

		}
		}
	}
	
}
