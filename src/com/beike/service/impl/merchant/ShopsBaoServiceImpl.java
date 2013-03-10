package com.beike.service.impl.merchant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.beike.common.entity.trx.TrxorderGoods;
import com.beike.dao.GenericDao;
import com.beike.dao.comment.CommentDao;
import com.beike.dao.coupon.CouponDao;
import com.beike.dao.goods.GoodsDao;
import com.beike.dao.merchant.MerchantDao;
import com.beike.dao.merchant.ShopsBaoDao;
import com.beike.dao.trx.TrxorderGoodsDao;
import com.beike.entity.goods.Goods;
import com.beike.form.CashCouponForm;
import com.beike.form.CouponForm;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.merchant.ShopsBaoService;

/**      
 * project:beiker  
 * Title:
 * Description:商铺宝ServiceImpl
 * Copyright:Copyright (c) 2011
 * Company:Sinobo
 * @author qiaowb  
 * @date Oct 31, 2011 5:32:15 PM     
 * @version 1.0
 */
@Service("shopsBaoService")
public class ShopsBaoServiceImpl extends GenericServiceImpl implements ShopsBaoService {		
	@Resource(name = "shopsBaoDao")
	private ShopsBaoDao shopsBaoDao;
	
	@Resource(name = "couponDao")
	private CouponDao couponDao;
	
	@Resource(name = "goodsDao")
	private GoodsDao goodsDao;
	
	@Resource(name = "commentDao")
	private CommentDao commentDao;
	
	@Resource(name = "trxorderGoodsDao")
	private TrxorderGoodsDao trxorderGoodsDao;
	
	@Resource(name = "merchantDao")
	private MerchantDao merchantDao;
	
	public GenericDao getDao() {
		return shopsBaoDao;
	}

	public Object findById(Serializable id) {
		return null;
	}


	/* (non-Javadoc)
	 * @see com.beike.service.merchant.ShopsBaoService#getMerchantDetailById(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public MerchantForm getMerchantDetailById(Long merchantId) {
		MerchantForm merchantForm = shopsBaoDao.getMerchantDetailById(merchantId);
		if(merchantForm!=null){
			
			List catloglist = commentDao.getCatlogGoodByBrandid(merchantId);
			if(catloglist != null && catloglist.size() > 0){
				Set<String>  catlog = new LinkedHashSet<String>();
				//存一级、二级分类
				Map<String,Set<String>> catlogMap = new LinkedHashMap<String,Set<String>>();
				for(Object ox : catloglist){
					Map mx = (Map) ox;
					String tagname = (String) mx.get("tagname");
					String tagextname = (String) mx.get("tagextname");
					
					Set<String> secondCatlog = catlogMap.get(tagname);
					if(secondCatlog == null || secondCatlog.size()==0)
						secondCatlog = new LinkedHashSet<String>();
					
					secondCatlog.add(tagextname);
					catlogMap.put(tagname, secondCatlog);
				}
				if(catlogMap == null || catlogMap.size() ==0 || catlogMap.isEmpty())
					return null;
			
				Set<Entry<String, Set<String>>> catalogKey = catlogMap.entrySet();
				for(Entry<String, Set<String>> entry : catalogKey){
					catlog.add(entry.getKey()+":");
					String value = entry.getValue().toString();
					value = value.substring(1,value.lastIndexOf("]"));
					catlog.add(value+" ");
				}
				
				merchantForm.setMainBusiness(catlog);
			}
			merchantForm.calculateScore();
			//评价得分处理
			if(merchantForm.getAvgscores()==null||Float.parseFloat(merchantForm.getAvgscores()) <= 0.0){
				merchantForm.setAvgscores("4.0");
			}
			//得分星数
			merchantForm.setAvgscoreswidth((new Float(Float.parseFloat(merchantForm.getAvgscores()) * 20)).intValue());
		}
		return merchantForm;
	}

	
	/* (non-Javadoc)
	 * @see com.beike.service.merchant.ShopsBaoService#getCouponCount(java.lang.Long)
	 */
	public int getCouponCount(Long merchantId) {
		return couponDao.getCouponCount(merchantId);
	}

	/* (non-Javadoc)
	 * @see com.beike.service.merchant.ShopsBaoService#getCouponCountIds(java.lang.Long, com.beike.page.Pager)
	 */
	public List<Long> getCouponCountIds(Long merchantId, Pager pager) {
		return couponDao.getCouponCountIds(merchantId, pager.getStartRow(), pager.getPageSize());
	}

	@SuppressWarnings("unchecked")
	@Override
	public CashCouponForm getCashCoupon(Long merchantid, Long money) {
		List rs = new ArrayList();
		CashCouponForm cashForm = new CashCouponForm();
		String ids = shopsBaoDao.getCashCouponByGoods(merchantid, money);
		if(ids!=null && ids.length()>0 && !"".equals(ids)){
			rs     = shopsBaoDao.getCashCouponByIDAndMoney(ids, money);
		}
		if(null != rs && rs.size() >0){
			Map m = (Map) rs.get(0);
			String goodsName    = (String) m.get("goodsname");
			if(!"".equals(goodsName) && goodsName.length()>0){
				cashForm.setGoodsname(goodsName);
			}
			Long   goodsid      =  (Long) m.get("goodsid");
			if(null != goodsid){
				cashForm.setGoodsid(goodsid);
			}
			Double rebatePrice  = Double.parseDouble(m.get("rebatePrice").toString()) ;
			if(!m.get("rebatePrice").toString().equals("")){
				cashForm.setRebatePrice(rebatePrice);
			}
			Double currentPrice = Double.parseDouble(m.get("currentPrice").toString());
			if(!m.get("currentPrice").toString().equals("")){
				cashForm.setCurrentPrice(currentPrice);
			}
			
			Set<String> regionList = goodsDao.getGoodsRegion(goodsid);
			cashForm.setMapRegion(regionList);
/*			if(null != regionList && regionList.size() >0){
				if(regionList.size() == 1){
					cashForm.setRegionFlag("0");
					String region = regionList.toString();   // 朝阳(CBD)
					String region_name     = region.substring(1,region.indexOf("("));
					String region_ext_name = region.substring(region.indexOf("(")+1, region.indexOf(")"));
				    if(!"".equals(region_name)){
						cashForm.setMainRegion(region_name);
					}
					if(!"".equals(region_ext_name)){
						cashForm.setSubsetRegion(region_ext_name);
					}
				}else{
					cashForm.setRegionFlag("1");
					cashForm.setCashCouponRegionNumber(regionList.size());
				}
			}*/
			if(rs.size()>1){
				cashForm.setCashCouponFlag("1");
			}else{
				cashForm.setCashCouponFlag("0");
			}
		}
		return cashForm;
	}

	@Override
	public List<MerchantForm> getChildMerchnatById(Long merchantId) {
		List<MerchantForm> rs = shopsBaoDao.getChildMerchnatById(merchantId);
		return rs;
	}

	@Override
	public List<Long> getGoodsCountIds(String idsCourse, Pager pager) {
		return shopsBaoDao.getGoodsCountIds(idsCourse, pager.getStartRow(),pager.getPageSize());
	}

	@Override
	public List<CouponForm> getCouponListByMerchantId(Long merchantId, int top) {
		List<CouponForm> rs = shopsBaoDao.getCouponForShopBaoByMerchantId(merchantId, top);
		return rs;
	}

	@Override
	public Goods getGoodsByBrandId(Long merchantId) {
		Goods goods = goodsDao.getTopGoodsForShopBao(merchantId);
		if(goods==null){
			goods=goodsDao.getOneGoodsByForShopBao(merchantId);
		}
		return goods;
	}

	/* (non-Javadoc)
	 * @see com.beike.service.merchant.ShopsBaoService#getMerchantDetailByGoodsId(java.lang.Long)
	 */
	public MerchantForm getMerchantDetailByGoodsId(Long goodsId) {
		Map<String, String> merchantMap = goodsDao.getMerchantByGoodId(goodsId);
		return getMerchantDetailById(Long.parseLong(merchantMap.get("id")));
	}

	/** 
	 * @description:通过商品Id和交易Id查询商家信息
	 * @param goodsId
	 * @param trx_order_id
	 * @return MerchantForm
	 * @throws 
	 */
	public MerchantForm getCommMerchantDetail(String goodsId,String trx_order_id){
		Goods goods = goodsDao.getGoodsDaoById(Long.parseLong(goodsId));
		Long merchantId = 0L;
		if(goods==null||goods.getIsMenu()==2) return null;
		if(null != goods && goods.getIsMenu()==1){//点餐商品是虚拟的关联不到分店Id单独查
			List<TrxorderGoods> list = trxorderGoodsDao.findByTrxId(Long.parseLong(trx_order_id));
			if(null != list && list.size() > 0){
				Long branchId = list.get(0).getSubGuestId();
				merchantId = merchantDao.getMerchantById(branchId).getParentId();
			}
		}else{
			merchantId = Long.parseLong(goodsDao.getMerchantByGoodId(Long.parseLong(goodsId)).get("id"));
		}
		return getMerchantDetailById(merchantId);
	}
	@Override
	public MerchantForm getShangpubaoDetailById(Long merchantId) {
		MerchantForm merchantForm = shopsBaoDao.getMerchantDetailById(merchantId);
		MerchantForm merchantForm2 = shopsBaoDao.getShangpubaoDetailById(merchantId);
		if(merchantForm2!=null){
			List<String[]> listMerchantbaoLogo = new ArrayList<String[]>();
			String strLogo = merchantForm2.getMerchantbaoLogo1();
			if(StringUtils.isNotEmpty(strLogo)){
				String[] aryLogo1 = StringUtils.split(strLogo,"|");
				if(aryLogo1!=null && aryLogo1.length == 1){
					aryLogo1 = new String[]{"",aryLogo1[0]};
				}
				if(aryLogo1!=null && aryLogo1.length==2){
					listMerchantbaoLogo.add(aryLogo1);
				}
			}
			
			strLogo = merchantForm2.getMerchantbaoLogo2();
			if(StringUtils.isNotEmpty(strLogo)){
				String[] aryLogo2 = StringUtils.split(strLogo,"|");
				if(aryLogo2!=null && aryLogo2.length == 1){
					aryLogo2 = new String[]{"",aryLogo2[0]};
				}
				if(aryLogo2!=null && aryLogo2.length==2){
					listMerchantbaoLogo.add(aryLogo2);
				}
			}
			
			strLogo = merchantForm2.getMerchantbaoLogo3();
			if(StringUtils.isNotEmpty(strLogo)){
				String[] aryLogo3 = StringUtils.split(strLogo,"|");
				if(aryLogo3!=null && aryLogo3.length == 1){
					aryLogo3 = new String[]{"",aryLogo3[0]};
				}
				if(aryLogo3!=null && aryLogo3.length==2){
					listMerchantbaoLogo.add(aryLogo3);
				}
			}
			
			strLogo = merchantForm2.getMerchantbaoLogo4();
			if(StringUtils.isNotEmpty(strLogo)){
				String[] aryLogo4 = StringUtils.split(strLogo,"|");
				if(aryLogo4!=null && aryLogo4.length == 1){
					aryLogo4 = new String[]{"",aryLogo4[0]};
				}
				if(aryLogo4!=null && aryLogo4.length==2){
					listMerchantbaoLogo.add(aryLogo4);
				}
			}
			
			strLogo = merchantForm2.getMerchantbaoLogo5();
			if(StringUtils.isNotEmpty(strLogo)){
				String[] aryLogo5= StringUtils.split(strLogo,"|");
				if(aryLogo5!=null && aryLogo5.length == 1){
					aryLogo5 = new String[]{"",aryLogo5[0]};
				}
				if(aryLogo5!=null && aryLogo5.length==2){
					listMerchantbaoLogo.add(aryLogo5);
				}
			}
			
			strLogo = merchantForm2.getMerchantbaoLogo6();
			if(StringUtils.isNotEmpty(strLogo)){
				String[] aryLogo6 = StringUtils.split(strLogo,"|");
				if(aryLogo6!=null && aryLogo6.length == 1){
					aryLogo6 = new String[]{"",aryLogo6[0]};
				}
				if(aryLogo6!=null && aryLogo6.length==2){
					listMerchantbaoLogo.add(aryLogo6);
				}
			}
			
			strLogo = merchantForm2.getMerchantbaoLogo7();
			if(StringUtils.isNotEmpty(strLogo)){
				String[] aryLogo7 = StringUtils.split(strLogo,"|");
				if(aryLogo7!=null && aryLogo7.length == 1){
					aryLogo7 = new String[]{"",aryLogo7[0]};
				}
				if(aryLogo7!=null && aryLogo7.length==2){
					listMerchantbaoLogo.add(aryLogo7);
				}
			}
			
			strLogo = merchantForm2.getMerchantbaoLogo8();
			if(StringUtils.isNotEmpty(strLogo)){
				String[] aryLogo8 = StringUtils.split(strLogo,"|");
				if(aryLogo8!=null && aryLogo8.length == 1){
					aryLogo8 = new String[]{"",aryLogo8[0]};
				}
				if(aryLogo8!=null && aryLogo8.length==2){
					listMerchantbaoLogo.add(aryLogo8);
				}
			}
			
			merchantForm.setListMerchantbaoLogo(listMerchantbaoLogo);
			merchantForm.setBaoTitleLogo(merchantForm2.getBaoTitleLogo());
		}
		
		if(merchantForm!=null){
			//地址为空时，查询分店5家地域
			if(StringUtils.isEmpty(merchantForm.getAddr())){
				merchantForm.setAddr(shopsBaoDao.getMerchantRegionById(merchantId));
			}
			
			merchantForm.calculateScore();
			//评价得分处理
			if(Float.parseFloat(merchantForm.getAvgscores()) <= 0.0){
				merchantForm.setAvgscores("4.0");
			}
			//得分星数
			merchantForm.setAvgscoreswidth((new Float(Float.parseFloat(merchantForm.getAvgscores()) * 20)).intValue());
		}
		return merchantForm;
	}

	@Override
	public int getGoodsIdTotalCount(String idsCourse) {
		
		return shopsBaoDao.getGoodsIdTotalCount(idsCourse);
	}
}
