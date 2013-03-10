package com.beike.dao.flagship;

import java.util.List;
import com.beike.dao.GenericDao;
import com.beike.entity.flagship.Flagship;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
/**
 * @ClassName: FlagshipDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Grace Guo guoqingcun@gmail.com
 * @date 2013-1-16 下午3:28:06
 *
 */
public interface FlagshipDao extends GenericDao<Flagship, Long>{

	public Flagship getFlagshipByRealmName(String realmName,Boolean isPreview) throws Exception;
	
	/**
	 * 
	 * @Title: getFlagShipTotalCountForCity
	 * @Description: 查询某个城市下品牌旗舰店的总数
	 * @param 
	 * @return int
	 * @author wenjie.mai
	 */
	public int getFlagShipTotalCountForCity(Long cityId);
	
	/**
	 * 
	 * @Title: getFlagShipInfo
	 * @Description: 分页查询旗舰店信息
	 * @param 
	 * @return List<Flagship>
	 * @author wenjie.mai
	 */
	public List getFlagShipInfo(Long cityId,int start,int end);

	public List<MerchantForm> getChildMerchnatById(String branchs, Pager pager);
	
	/**
	 * 
	 * @Title: getFlagshipByMerchantId
	 * @Description: 通过品牌ID查询旗舰店信息
	 * @param 
	 * @return Flagship
	 * @author wenjie.mai
	 */
	public List getFlagshipByMerchantId(Long merchantId);
	
	/**
	 * 
	 * @Title: getOfferContentByMerchantId
	 * @Description: 查询旗舰店优惠信息
	 * @param 
	 * @return List
	 * @author wenjie.mai
	 */
	public List getOfferContentByMerchantId(Long merchantId,String nowTime);
}
