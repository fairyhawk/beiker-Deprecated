package com.beike.dao.seo;

import java.util.List;
import java.util.Map;

import com.beike.entity.seo.MerchantTag;
import com.beike.entity.seo.GoodsTag;
import com.beike.entity.seo.CouponTag;

/**
 * SEO 的 DAO 层
 * 
 * @author zx.liu
 */
public interface SeoDao {

	/**
	 * 根据商品标签来获取商品的ID
	 */
	public Long findGoodsId(String tagEnname);

	/**
	 * 根据优惠券标签获取优惠券的ID
	 */
	public Long findCouponId(String tagEnname);

	/**
	 * 根据品牌标签来获取品牌的ID
	 */
	public Long findMerchantId(String tagEnname);

	public String findTagId(String tagEnname);

	public String findRegionId(String tagEnname);

	/**
	 * 根据商品标签来获取商品的Tag信息
	 * 
	 * @param tagEnname
	 * @return
	 */
	public GoodsTag findGoodsTag(String tagEnname);

	/**
	 * 根据优惠券标签来获取优惠券的Tag信息
	 * 
	 * @param tagEnname
	 * @return
	 */
	public CouponTag findCouponTag(String tagEnname);

	/**
	 * 根据品牌标签来获取品牌的Tag信息
	 * 
	 * @param tagEnname
	 * @return
	 */
	public MerchantTag findMerchantTag(String tagEnname);

	public Map<String,Object> getTagENName(String tagId);

	public Map<String,Object> getRegionENName(String tagId);
	
	public String findRegionId(String tagEnname, Long cityid, Long parentId);

	/**
	 * 
	 * janwen
	 * @param tag_en_name
	 * @param parentid
	 * @return 特色标签英文名
	 *
	 */
	public List<Map> getFeatureTag(String tag_en_name,Long parentid);

}
