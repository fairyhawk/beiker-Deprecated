package com.beike.core.service.trx.partner.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.core.service.trx.partner.PartnerBindVoucherService;
import com.beike.dao.trx.partner.PartnerBindVoucherDao;
import com.beike.entity.partner.PartnerBindVoucher;

/**   
 * @title: PartnerBindVoucherServiceImpl.java
 * @package com.beike.core.service.trx.partner.impl
 * @description: 
 * @author wangweijie  
 * @date 2012-9-6 下午08:24:35
 * @version v1.0   
 */
@Service("partnerBindVoucherService")
public class PartnerBindVoucherServiceImpl implements PartnerBindVoucherService {
	
	@Autowired
	private PartnerBindVoucherDao partnerBindVoucherDao;
	
	
	@Override
	public void savePartnerVouchers(List<PartnerBindVoucher> pbvList) {
		if(null == pbvList || pbvList.size() == 0){
			return;
		}
		for(PartnerBindVoucher pbv : pbvList){
			partnerBindVoucherDao.addPartnerVoucher(pbv);
		}
	}
	
	public Map<String,Object> queryVoucherMap(String partnerNo,String outRequestId){
		return partnerBindVoucherDao.queryVoucherBothSides(partnerNo, outRequestId);
	}

	@Override
	public List<PartnerBindVoucher> preQryInWtDBByPartnerBindVoucherList(String partherNo, String outRequestId) {
		return partnerBindVoucherDao.queryPartnerBindVoucherList(partherNo, outRequestId);
	}

	@Override
	public PartnerBindVoucher queryPartnerBindVoucher(String partnerNo,Long voucherId) {
		return partnerBindVoucherDao.queryPartnerBindVoucher(partnerNo, voucherId);
	}
	
}
