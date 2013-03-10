package com.beike.dao.trx.partner;

import java.util.List;

import com.beike.entity.partner.Partner;

/** 
* @ClassName: PartnerDao 
* @Description: 分销商相关信息接口
* @author yurenli
* @date 2012-5-30 下午06:00:25 
* @version V1.0 
*/ 
public interface PartnerDao {
	
		/**根据Id查询分销商信息
		 * @param id
		 */
		public Partner findByid(Long id);
		
		/**根据partnerNo查询分销商信息
		 * @param partnerNo
		 */
		public List<Partner> findAllByPartnerNo(String partnerNo);
		
		/**根据partnerNo以及是否有效查询分销商信息
		 * @param partnerNo
		 * @param ailable
		 */
		public List<Partner> findByPartnerNoAndAva(String partnerNo,Long isAvailable);
		
		/**根据userId以及是否有效查询分销商信息
		 * @param userId
		 * @return
		 */
		public  List<Partner>  findByUserIdAndAva(Long userId,Long isAvailable);
		
		
		/**
		 * 根据userId  对应的 partner
		 * @author zhaofeilong
		 */
		public Partner findByUserId(Long userId);
		
		/**
		 * 查出所有的分销商信息
		 * @return
		 */
		public List<Partner> findAll();

}
