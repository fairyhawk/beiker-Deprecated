package com.beike.service.impl.seo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.seo.SeoDao;
import com.beike.entity.catlog.RegionCatlog;
import com.beike.entity.seo.CouponTag;
import com.beike.entity.seo.GoodsTag;
import com.beike.entity.seo.MerchantTag;
import com.beike.service.seo.SeoService;

@Service("seoService")
public class SeoServiceImpl implements SeoService 
{

	@Autowired
	private SeoDao seoDao;
	
	/**
	 * 根据商品标签得到商品ID
	 */
	@Override
	public Long getGoodsId(String tagEnname) {
		if(null == tagEnname || "".equals(tagEnname.trim())){
			return null;
		}		
		return seoDao.findGoodsId(tagEnname);
	}
	
	
	/**
	 * 根据优惠券标签得到优惠券的ID
	 */
	@Override
	public Long getCouponId(String tagEnname) {
		if(null == tagEnname || "".equals(tagEnname.trim())){
			return null;
		}
		return seoDao.findCouponId(tagEnname);
	}

	
	/**
	 * 根据品牌标签得到品牌ID
	 */
	@Override
	public Long getMerchantId(String tagEnname) {
		if(null == tagEnname || "".equals(tagEnname.trim())){
			return null;
		}		
		return seoDao.findMerchantId(tagEnname);
	}

	
	public String getTagId(String tagEnname){
		if(null == tagEnname || "".equals(tagEnname.trim())){
			return null;
		}		
		return seoDao.findTagId(tagEnname);		
	}

	
	public String getRegionId(String tagEnname){
		if(null == tagEnname || "".equals(tagEnname.trim())){
			return null;
		}		
		return seoDao.findRegionId(tagEnname);
	}
	
	
	/**
	 * 根据商品标签得到商品的Tag信息 
	 * @param tagEnname
	 * @return
	 */
	@Override
	public GoodsTag getGoodsTag(String tagEnname) {
		if(null == tagEnname || "".equals(tagEnname.trim())){
			return null;
		}
		
		return seoDao.findGoodsTag(tagEnname);
	}	
	

	/**
	 * 根据优惠券标签得到优惠券的Tag信息
	 * @param tagEnname
	 * @return
	 */
	@Override
	public CouponTag getCouponTag(String tagEnname) {
		if(null == tagEnname || "".equals(tagEnname.trim())){
			return null;
		}

		return seoDao.findCouponTag(tagEnname);
	}	

	
	/**
	 * 根据品牌标签得到品牌的Tag信息
	 * @param tagEnname
	 * @return
	 */
	@Override
	public MerchantTag getMerchantTag(String tagEnname) {
		if(null == tagEnname || "".equals(tagEnname.trim())){
			return null;
		}
		
		return seoDao.findMerchantTag(tagEnname);
	}

	
	public SeoDao getSeoDao() {
		return seoDao;
	}
	public void setSeoDao(SeoDao seoDao) {
		this.seoDao = seoDao;
	}


	@Override
	public String getTagENName(String tagId) {
		if(tagId==null||tagId.trim().equals("")){
			return null;
		}
		Map<String,Object> map = seoDao.getTagENName(tagId);
		if(map!=null){
			return (String)map.get("tag_enname");
		}
		return null;
	}


	@Override
	public String getRegionENName(String tagId) {
		if(tagId==null||tagId.trim().equals("")){
			return null;
		}
		Map<String,Object> map = seoDao.getRegionENName(tagId);
		if(map!=null){
			return (String)map.get("region_enname");
		}
		return null;
	}


	@Override
	public String getRegionId(String tagEnname, Long cityid, String parentId) {
		if(null == tagEnname || "".equals(tagEnname.trim())){
			return null;
		}		
		return seoDao.findRegionId(tagEnname,cityid,Long.parseLong(parentId));
	}

	@Override
	public RegionCatlog getFeatureTag(String tag_en_name, Long parentid) {
		List<Map> tags = seoDao.getFeatureTag(tag_en_name, parentid);
		RegionCatlog rc = new RegionCatlog();
		if (tags != null && tags.size() > 0) {
			Map map = tags.get(0);
			rc.setRegion_enname((String) map.get("tag_enname"));
			rc.setCatlogid((Long) map.get("id"));
		}
		return rc;
	}

}
