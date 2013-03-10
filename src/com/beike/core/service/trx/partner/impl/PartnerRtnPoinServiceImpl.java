package com.beike.core.service.trx.partner.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.common.enums.trx.RtnPointRule;
import com.beike.core.service.trx.partner.PartnerRtnPoinService;
import com.beike.core.service.trx.soa.proxy.TrxSoaService;
import com.beike.dao.trx.partner.PartnerRtnPointDao;
import com.beike.entity.partner.PartnerRtnPoint;
@Service(value = "partnerRtnPoinService")
public class PartnerRtnPoinServiceImpl implements PartnerRtnPoinService{
	@Autowired
	private PartnerRtnPointDao partnerRtnPointDao;
	@Autowired
	private TrxSoaService trxSoaService;

	@SuppressWarnings("unchecked")
	public String processRtnPoin(TrxorderGoods tg,String partnerNo,String outReqId,double configAmount,String configTagType) throws Exception{

		String rtnPointType = "1";
		//查询商品是否已经插入结算返点表 
		Map<String,String> condition = new HashMap<String, String>();
		condition.put("trxGoodsId",tg.getId().toString());
			List<Object>   partnerList = partnerRtnPointDao.queryPartnerRtnPointByCondition(condition);
			if(partnerList!=null&&partnerList.size()>0){
				Map<String,Object> prp = (Map<String,Object>)partnerList.get(0);
			rtnPointType = prp.get("rtn_point_type").toString();
			return rtnPointType;
			}
		
			Map<Long, String> tagMap = trxSoaService.findTagByIdName(tg.getGoodsId());
			String tagStr = tagMap.get(tg.getGoodsId());
			if(tagStr!=null&&!"".equals(tagStr)){
				String[] tagArray = tagStr.split("-");
				String tagId = tagArray[0].trim();
				String tagName = tagArray[1];
				
				PartnerRtnPoint partnerRtnPoint = new PartnerRtnPoint();
				partnerRtnPoint.setCreateDate(new Date());
				partnerRtnPoint.setModifyDate(new Date());
				partnerRtnPoint.setOutRequestId(outReqId);
				partnerRtnPoint.setPartnerNo(partnerNo);
				partnerRtnPoint.setRtnPointType(Integer.valueOf(rtnPointType));
				partnerRtnPoint.setTagId(Long.valueOf(tagId));
				partnerRtnPoint.setTagName(tagName);
				partnerRtnPoint.setTrxGoodsId(tg.getId());
				partnerRtnPoint.setTrxGoodsSn(tg.getTrxGoodsSn());
				partnerRtnPoint.setTrxOrderId(tg.getTrxorderId());
				partnerRtnPoint.setVoucherId(tg.getVoucherId());

				if(tg.getPayPrice()<configAmount){
					rtnPointType = "0";
					partnerRtnPoint.setRtnPointRule(RtnPointRule.AMOUNT_15);
				}else if(tagId.equals(configTagType)){
					rtnPointType = "0"; 
					partnerRtnPoint.setRtnPointRule(RtnPointRule.TAG_SEC_LIMIT);
				}else{
					rtnPointType = "1";
				}
				partnerRtnPointDao.addPartnerRtnPoint(partnerRtnPoint);
				return rtnPointType;
			
			}
		
		return rtnPointType;
	
	}
}
