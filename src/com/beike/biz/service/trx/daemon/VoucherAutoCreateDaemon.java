package com.beike.biz.service.trx.daemon;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.enums.trx.VoucherStatus;
import com.beike.core.service.trx.VoucherService;
import com.beike.util.DateUtils;
import com.beike.util.TrxConstant;

/**
 * @Title: VoucherAutoCreateDaemonService.java
 * @Package com.beike.biz.service.trx
 * @Description: 凭证定时生成
 * @date May 30, 2011 6:25:08 AM
 * @author wh.cheng
 * @version v1.0
 */
@Service("voucherAutoCreateDaemon")
public class VoucherAutoCreateDaemon {

	private final Log logger = LogFactory.getLog(VoucherAutoCreateDaemon.class);
	private static int memCurCount; //内存计数器
	@Autowired
	private VoucherService voucherService;

	public void generateVoucher() {
		
		logger.info("++++++++++++++++start day generate voucher++++at date:"+DateUtils.getStringTodayto());
		int curCount = voucherService.checkVoucherCount(VoucherStatus.INIT,
				null);
		if (curCount < TrxConstant.VOUCHER_AUTO_CREATE_TOTAL_THRES) {// 当可用库存少于此总量阀值时进行生成(不含被预取且为init的部分)
			//凭证数生成方式：根据数据库计数与单日阀值比对（预留：以便快速回滚）
			/*while (true) {
				
				if (checkCountByDBConut(TrxConstant.VOUCHER_AUTO_CREATE_DAY_THRES)) {// 当日生成的凭证达到单日阀值，则停止生成(不含被预取且为init的部分)

					return;
				}
			
				voucherService.createVoucher();
			}
			*/
			
			//凭证数生成方式：根据内存计数方式与单日阀值比对
			while (true) {

				if (checkCountByMemCount(TrxConstant.VOUCHER_AUTO_CREATE_DAY_THRES)) {// 当日生成的凭证达到单日阀值，则停止生成(不含被预取且为init的部分)
         
					return;
				}

			}
			
			
		}
		logger.info("++++++++++++++++finished day generate voucher++++at date:"+DateUtils.getStringTodayto());
	}
	/**
	 * 单个凭证生成以数据库计数比对单日阀值
	 * @param maxCount
	 * @return   是否单日生成完成
	 */
	public boolean checkCountByDBConut(int maxCount) {
		
		Date current = new Date();
		boolean result = false;
		
		//新增单日阀值数据库查询耗时日志
		logger.info("+++++++++++start create item voucher qry day thres in DB+++++at date:"+DateUtils.getStringTodayto());
		int curCount = voucherService.checkVoucherCount(VoucherStatus.INIT,current);
		logger.info("+++++++++++finished create item voucher qry day in DB thres curCount:"+curCount+" +++++at date:"+DateUtils.getStringTodayto());
		if (maxCount <= curCount) {

			result = true;
		}
		
		return result;

	}
	
	/**
	 * 单个凭证生成以内存计数比对单日阀值
	 * @param maxCount
	 * @return 是否单日生成完成
	 */
	public boolean checkCountByMemCount(int maxCount) {
	
		boolean result = false;
		Long voucerId=voucherService.createVoucher();
		if(voucerId.intValue()>0){//如果单个生成成功，内存计数器则加1
			
			memCurCount++;
		}
		//但单日数量不足则生成
		if (maxCount <= memCurCount ) {
 
			result = true;
		}
		logger.info("+++++++++++finished create item voucher by memCurCount:"+memCurCount+"++++++at date:"+DateUtils.getStringTodayto());
		return result;

	}
	

	public VoucherService getVoucherService() {
		return voucherService;
	}

	public void setVoucherService(VoucherService voucherService) {
		this.voucherService = voucherService;
	}

}
