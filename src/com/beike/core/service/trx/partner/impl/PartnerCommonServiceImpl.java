package com.beike.core.service.trx.partner.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.common.bean.trx.partner.PartnerInfo;
import com.beike.core.service.trx.partner.PartnerCommonService;
import com.beike.dao.trx.partner.PartnerDao;
import com.beike.entity.partner.Partner;
import com.beike.util.TrxConstant;
import com.beike.util.cache.cachedriver.service.MemCacheService;
import com.beike.util.cache.cachedriver.service.impl.MemCacheServiceImpl;

/**
 * @Title: PartnerService.java
 * @Package  com.beike.core.service.trx.parter
 * @Description: 合作分销商API交易相关基础Service
 * @date 5 30, 2012 2:16:47 PM
 * @author wh.cheng
 * @version v1.0
 */
@Service("partnerCommonService")
public class PartnerCommonServiceImpl implements  PartnerCommonService {

	private final Log logger = LogFactory.getLog(PartnerCommonServiceImpl.class);
	private final MemCacheService memCacheService = MemCacheServiceImpl.getInstance();
	@Autowired
	private PartnerDao partnerDao;



	
	/**
	 * 根据分销商号partnerNo查询有效分销商信息
	 * @author zhaofeilong
	 */
	@Override
	public PartnerInfo qryAvaParterByNoInMem(String partnerNo) {
		StringBuilder partnerKeySb=new StringBuilder();
		partnerKeySb.append(TrxConstant.PARTNER_AVA_BY_PARTNERNO_CACHE_KEY);
		partnerKeySb.append(partnerNo);
		PartnerInfo  partnerInfoInMem=(PartnerInfo) memCacheService.get(partnerKeySb.toString());
		
		//如果缓存里没有，从库里去取，然后再放一次
		if(partnerInfoInMem==null){
			List<Partner> partnerList=partnerDao.findByPartnerNoAndAva(partnerNo, 1L);
			if(partnerList!=null&&partnerList.size()>0){
				
				Partner partnerInDB=partnerList.get(0);
				 partnerInfoInMem=new PartnerInfo(partnerInDB.getKeyValue(),partnerInDB.getUserId(),
						partnerInDB.getPartnerName(),partnerInDB.getIsAvailable(),partnerInDB.getTrxExpress(),partnerInDB.getApiType(),partnerInDB.getSubName(),partnerInDB.getSmsExpress(),partnerInDB.getIp(),partnerInDB.getSessianKey(),partnerInDB.getDescription(),partnerInDB.getPartnerNo(),partnerInDB.getNoticeKeyValue());
				
				memCacheService.set(partnerKeySb.toString(), partnerInfoInMem, TrxConstant.PARTNER_AVA_BY_PARTNERNO_CACHE_TIMEOUT);
			}
 		
		}
		
		return partnerInfoInMem;
	}
	/**
	 * 跟据分销商编号查询其下所有分销商信息
	 * @param partnerNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PartnerInfo> qryAllPartnerByNoInMem(String partnerNo){

		StringBuilder partnerKeySb=new StringBuilder();
		partnerKeySb.append(TrxConstant.PARTNER_ALL_CACHE_KEY);
		partnerKeySb.append(partnerNo);
		List<PartnerInfo>  partnerInfoListInfoInMem=(List<PartnerInfo>) memCacheService.get(partnerKeySb.toString());
		
		//如果缓存里没有，从库里去取，然后再放一次
		if(partnerInfoListInfoInMem==null){
			List<Partner> partnerListInDB=partnerDao.findAllByPartnerNo(partnerNo);
			if(partnerListInDB!=null&&partnerListInDB.size()>0){
				partnerInfoListInfoInMem=new ArrayList<PartnerInfo>();
				
				for(Partner parter:partnerListInDB){
					
					PartnerInfo	 partnerInfoInMem=new PartnerInfo(parter.getKeyValue(),parter.getUserId(),
							parter.getPartnerName(),parter.getIsAvailable(),parter.getTrxExpress(),parter.getApiType(),parter.getSubName(),parter.getSmsExpress(),parter.getIp(),parter.getSessianKey(),parter.getDescription(),parter.getPartnerNo(),parter.getNoticeKeyValue());
					
					partnerInfoListInfoInMem.add(partnerInfoInMem);
				}
				 
				memCacheService.set(partnerKeySb.toString(), partnerInfoListInfoInMem, TrxConstant.PARTNER_ALL_CACHE_TIMEOUT);
			}
 		
		}
		return partnerInfoListInfoInMem;
	
		
	}
	
	/**
	 * 跟据分销商编号查询其下所有分销商User_id信息
	 * @param partnerNo
	 * @return
	 */
	public List<Long> qryAllUserIdByNoInMem(String partnerNo){
		
		List<Long> userIdList=new ArrayList<Long>();
		List<PartnerInfo>   partnerInfoList=qryAllPartnerByNoInMem(partnerNo);
		if(partnerInfoList!=null){
			
			for(PartnerInfo  partnerInfo:partnerInfoList){
				
				userIdList.add(partnerInfo.getUserId());
			}
		}
		return userIdList;
	}
	
	/**
	 * 根据user_id 检查是否归属该分销商
	 * @param partnerNo
	 * @param sourceUserId
	 * @return
	 */
	public   boolean  checkIsUIdBelongPNo(String partnerNo, Long sourceUserId){
		List<PartnerInfo>  PartnerInfoList=qryAllPartnerByNoInMem(partnerNo);//从缓存中读取
		if(PartnerInfoList!=null){
			
			for(PartnerInfo partnerInfo:PartnerInfoList){
				
				if(partnerInfo.getUserId().intValue()==sourceUserId.intValue()){//如果匹配
					
					return true;
				}
			}
		}
		
		return false;
		
	}
	
	
	/**
	 * 根据user_id 查询归属该分销商编号所对应的所有的分销商信息
	 * @param userId
	 * @return
	 */
	public List<PartnerInfo> qryAllParterByUserIdInMem(Long userId){
		
		
		List<PartnerInfo>  partnerInfoListInfoInMem=getAllPartnerInfo();//获取所有分销商信息
		List<PartnerInfo>  resultPartnerInfoList=new ArrayList<PartnerInfo>();
		
				//筛选该user_id下对应的partnerInfo
		if(partnerInfoListInfoInMem!=null&&partnerInfoListInfoInMem.size()>0){
				
				for(PartnerInfo parterInfo:partnerInfoListInfoInMem){
					
					if(parterInfo.getUserId().intValue()==userId){
						
						resultPartnerInfoList.add(parterInfo);
					}
				}
				 
				
			}
		
		logger.info("+++userId:"+userId+"++++resultPartnerInfoList:"+resultPartnerInfoList+"++++");

		return resultPartnerInfoList;
	
		
	}
	
	/**
	 * 根据user_id  查询有效的分销商信息
	 * @param userId
	 * @return
	 */
	public PartnerInfo qryAvaParterByUserIdInMem(Long userId){
		
		List<PartnerInfo>  partnerInfoListInfoInMem=	qryAllParterByUserIdInMem(userId);
		if(partnerInfoListInfoInMem!=null&& partnerInfoListInfoInMem.size()>0){
			for(PartnerInfo partnerInfo:partnerInfoListInfoInMem){
				
				if(partnerInfo.getIsAvailable().intValue()==1){
					logger.info("+++++++userId:"+userId+"+++AvaParterByUserIdInMem->partnerInfo:"+partnerInfo+"++++++");
					return partnerInfo;
					
				}
				
			}
			
		}
		logger.info("+++++++userId:"+userId+"+++AvaParterByUserIdInMem->partnerInfo:null++++++");
		return null;
		
	}
	
	
	/**
	 * 根据user_id 查询相应分销商信息。理论上一个user_id下有且只有一条分销商信息。
	 * @param userId
	 * @return
	 */
	public PartnerInfo qryParterByUserIdInMem(Long userId){
		
		List<PartnerInfo>  partnerInfoListInfoInMem=	qryAllParterByUserIdInMem(userId);
		if(partnerInfoListInfoInMem!=null&& partnerInfoListInfoInMem.size()>0){
			
			return partnerInfoListInfoInMem.get(0);
		}
		
		return null;
		
	}
	
	
	


	/**
	 * 获取所有的分销商信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PartnerInfo>  getAllPartnerInfo(){
		
		StringBuilder partnerKeySb=new StringBuilder();
		partnerKeySb.append(TrxConstant.ALL_PARTNER_CACHE_KEY);
		List<PartnerInfo>  partnerInfoListInfoInMem=(List<PartnerInfo>) memCacheService.get(partnerKeySb.toString());
		List<PartnerInfo>  partnerInfoListInfoInNewMem=new ArrayList<PartnerInfo>();
		
		//如果缓存里没有，从库里取，然后再放入mem
		if(partnerInfoListInfoInMem==null){
			logger.info("++++++++++++++++partnerInfoListInfoInMem: is null!++++++");
			List<Partner>  partnerListInDB=partnerDao.findAll();
			if(partnerListInDB!=null&&partnerListInDB.size()>0){// 库里存在数据时才放入mem
				
				for(Partner parter:partnerListInDB){
				
						PartnerInfo	 partnerInfoInMem=new PartnerInfo(parter.getKeyValue(),parter.getUserId(),
								parter.getPartnerName(),parter.getIsAvailable(),parter.getTrxExpress(),parter.getApiType(),parter.getSubName(),parter.getSmsExpress(),parter.getIp(),parter.getSessianKey(),parter.getDescription(),parter.getPartnerNo(),parter.getNoticeKeyValue());
						
						partnerInfoListInfoInNewMem.add(partnerInfoInMem);
						
					}
				memCacheService.set(partnerKeySb.toString(), partnerInfoListInfoInNewMem, TrxConstant.ALL_PARTNER_CACHE_TIMEOUT);
				
			}
			logger.info("++++++++++++++++partnerInfoListInfoInNewMem:"+partnerInfoListInfoInNewMem+"++++++");
			return partnerInfoListInfoInNewMem;
		}else{
			logger.info("++++++++++++++++partnerInfoListInfoInMem:"+partnerInfoListInfoInMem+"++++++");
			return partnerInfoListInfoInMem;
		   
		}
		
	}
	
}
