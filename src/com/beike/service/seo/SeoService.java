package com.beike.service.seo;

import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.seo.CouponTag;
import com.beike.entity.seo.GoodsTag;
import com.beike.entity.seo.MerchantTag;

/**
 * SEO 的 Service 层
 * 
 * @author zx.liu
 */
public interface SeoService {

	/**
	 * 根据商品标签得到商品ID
	 */
	public Long getGoodsId(String tagEnname);

	
	/**
	 * 根据优惠券标签得到优惠券的ID
	 */
	public Long getCouponId(String tagEnname);

	
	/**
	 * 根据品牌标签得到品牌ID
	 */
	public Long getMerchantId(String tagEnname);
	

	
	public String getTagId(String tagEnname);

	
	public String getRegionId(String tagEnname);	
	
	
	public String getTagENName(String tagId);

	
	public String getRegionENName(String tagId);	

     /**
      * 
      * janwen
      * @param tag_en_name
      * @param parentid
      * @return 特色标签英文名
      *
      */
	public RegionCatlog getFeatureTag(String tag_en_name,Long parentid);
	
	/**
	 * 根据商品标签得到商品的Tag信息 
	 * @param tagEnname
	 * @return
	 */
	public GoodsTag getGoodsTag(String tagEnname);
	
	
	/**
	 * 根据优惠券标签得到优惠券的Tag信息
	 * @param tagEnname
	 * @return
	 */
	public CouponTag getCouponTag(String tagEnname);	

			
	/**
	 * 根据品牌标签得到品牌的Tag信息
	 * @param tagEnname
	 * @return
	 */
	public MerchantTag getMerchantTag(String tagEnname);
	

	
	//////////////////////////////////////
	/**
	 * 统一调用的方法
	 */
	//////////////////////////////
	
	public String getRegionId(String tagEnname,Long cityid,String parentId);
	

}
