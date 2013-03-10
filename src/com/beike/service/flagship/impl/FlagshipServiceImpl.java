package com.beike.service.flagship.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.beike.dao.flagship.FlagshipDao;
import com.beike.dao.merchant.BranchProfileDao;
import com.beike.entity.flagship.Flagship;
import com.beike.entity.merchant.BranchProfile;
import com.beike.form.GoodsForm;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
import com.beike.service.flagship.FlagshipService;
import com.beike.service.goods.GoodsService;
import com.beike.util.DateUtils;

/**
 * @ClassName: FlagshipServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Grace Guo guoqingcun@gmail.com
 * @date 2013-1-16 下午3:23:38
 *
 */
@Service("flagshipService")
public class FlagshipServiceImpl implements FlagshipService {

	@Autowired
	private FlagshipDao flagshipDao;
	
	@Autowired
	private GoodsService goodsService;
	
	@Autowired
	private BranchProfileDao branchProfileDao;
	
	public void setFlagshipDao(FlagshipDao flagshipDao) {
		this.flagshipDao = flagshipDao;
	}
	/**
	 * 获取旗舰店
	 */
	@Override
	public Flagship getFlagshipByRealmName(String realmName,Boolean isPreview) throws Exception {
		// TODO Auto-generated method stub
		return flagshipDao.getFlagshipByRealmName(realmName,isPreview);
	}
	
	@Override
	public int getFlagShipTotalCountForCity(Long cityId) {
		return flagshipDao.getFlagShipTotalCountForCity(cityId);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<Flagship> getFlagShipInfo(Long cityId, Pager pager) {
		
		List flist = flagshipDao.getFlagShipInfo(cityId,pager.getStartRow(),pager.getPageSize());
		
		if(flist == null || flist.size() ==0 )
			return null;
		
		List<Flagship> rslist = new LinkedList<Flagship>();
		
		for(Object obj:flist){
			Map map = (Map) obj;
			int brandId    =  (Integer) map.get("brand_id");
			String realName =  (String) map.get("realm_name");
			
			int merVirtualCount = (Integer)map.get("virtualcount");
			int merSaleCount    = (Integer) map.get("mc_sale_count");
			String mcLogo2      = (String) map.get("mc_logo2");
			Long wellCount = (Long) map.get("mc_well_count");
			Long satiCount = (Long) map.get("mc_satisfy_count");
			Long poorCount = (Long) map.get("mc_poor_count");
			String mername = (String) map.get("merchantname");
			
			MerchantForm merForm = new MerchantForm();
			merForm.setId(String.valueOf(brandId));
			merForm.setLogo2(mcLogo2);
			merForm.setSalescount(String.valueOf(merSaleCount));
			merForm.setVirtualCount(merVirtualCount);
			merForm.setWellCount(wellCount);
			merForm.setSatisfyCount(satiCount);
			merForm.setPoorCount(poorCount);
			merForm.setMerchantname(mername);
			
			if(merForm != null){
				merForm.calculateScore();
			}
			
			Flagship flag = new Flagship();
			flag.setBrandId(Long.parseLong(String.valueOf(brandId)));
			flag.setRealmName(realName);
			flag.setMerchant(merForm);
			
			rslist.add(flag);
		}
		
		//查询旗舰店的推荐商品
		rslist = getTopGoodWithFlagShip(rslist);
		
		return rslist;
	}
	@Override
	public List<MerchantForm> getBranchsById(String branchs, Pager pager) {
		List<MerchantForm> lstBranch = flagshipDao.getChildMerchnatById(branchs,pager);
		//查询分店满意率
		if(lstBranch!=null && lstBranch.size()>0){
			StringBuilder sb = new StringBuilder();
			for (MerchantForm merchantForm : lstBranch) {
				sb.append(merchantForm.getId());
				sb.append(",");
			}
			String sbid = sb.substring(0, sb.lastIndexOf(","));
			List<BranchProfile> lstProfile = branchProfileDao.getBranchProfileById(sbid);
			Map<String,BranchProfile> progileMap = new HashMap<String,BranchProfile>();
			if(lstProfile!=null && lstProfile.size()>0){
				for (BranchProfile profile : lstProfile) {
					profile.calculateScore();
					progileMap.put(String.valueOf(profile.getBranchId()), profile);
				}
			}
			//店铺满意率计算
			for (MerchantForm merchantForm : lstBranch) {
				BranchProfile profile = progileMap.get(merchantForm.getId());
				if(profile!=null){
					merchantForm.setMcScore(0L);
					merchantForm.setWellCount(profile.getWellCount());
					merchantForm.setSatisfyCount(profile.getSatisfyCount());
					merchantForm.setPoorCount(profile.getPoorCount());
					merchantForm.setSatisfyRate(profile.getSatisfyRate());
					merchantForm.setPartWellRate(profile.getPartWellRate());
					merchantForm.setPartSatisfyRate(profile.getPartSatisfyRate());
					merchantForm.setPartPoorRate(profile.getPartPoorRate());
				}else{
					merchantForm.setMcScore(0L);
					merchantForm.setWellCount(0L);
					merchantForm.setSatisfyCount(0L);
					merchantForm.setPoorCount(0L);
					merchantForm.setSatisfyRate(-1);
					merchantForm.setPartWellRate(-1);
					merchantForm.setPartSatisfyRate(-1);
					merchantForm.setPartPoorRate(-1);
				}
			}
		}
		return lstBranch;
	}
	public List<Flagship> getTopGoodWithFlagShip(List<Flagship> rslist){
		
		List<Long> brandIdlist = new LinkedList<Long>();
		List<GoodsForm> topGoodsList = new LinkedList<GoodsForm>();
		List<GoodsForm> salelist = new LinkedList<GoodsForm>();
		
		for(Flagship flag:rslist){
			brandIdlist.add(flag.getBrandId());
		}
		
		topGoodsList = goodsService.getTopGoodsWithFlagShip(brandIdlist);
		
		//品牌旗舰店如果无推荐商品、查询品牌下销量最大的商品
		if(topGoodsList == null || topGoodsList.size() == 0 || topGoodsList.size()<rslist.size()){
			List<Long> noneTopGoodMer = new LinkedList<Long>();
			
			if(topGoodsList != null && topGoodsList.size()>0){
				for(GoodsForm gForm:topGoodsList){
					Long merchantId = gForm.getMerchantId();
					if(brandIdlist.indexOf(merchantId)!=-1)
							brandIdlist.remove(merchantId);
				}
				noneTopGoodMer.addAll(brandIdlist);//无推荐商品的品牌ID
			}else{
				noneTopGoodMer.addAll(brandIdlist);
			}
			
		    salelist = goodsService.getMaxSaleCountWithFlagShip(noneTopGoodMer);
		    if(topGoodsList == null || topGoodsList.size() ==0)
		    	topGoodsList = new LinkedList<GoodsForm>(); 
		    if(salelist != null && salelist.size() >0)
		    	topGoodsList.addAll(salelist);
		}
		
		if(topGoodsList == null || topGoodsList.size() ==0 )
			return null;
		
		Map<Long,GoodsForm> goodsMap = new LinkedHashMap<Long,GoodsForm>();
		for(GoodsForm form:topGoodsList){
			goodsMap.put(form.getMerchantId(),form);
		}
		
		List<Flagship> allFlagShip = new LinkedList<Flagship>();
		for(Flagship flag:rslist){
			Long merchantid = flag.getBrandId();
			GoodsForm form = goodsMap.get(merchantid);
			//*** 如果Form==null 证明该品牌下目前没有可售商品、页面不显示该品牌、此情况会导致分页个数不一致***
			if(form != null){
				flag.setTopGoods(form);
				allFlagShip.add(flag);
			}
		}
		
		return allFlagShip;
	}
	@SuppressWarnings("rawtypes")
	@Override
	public Flagship getFlagshipByMerchantId(Long merchantId) {
		
		List flist = flagshipDao.getFlagshipByMerchantId(merchantId);
		
		if(flist == null || flist.size() ==0)
			return null;
		
		Map map = (Map) flist.get(0);
		String realName =  (String) map.get("realm_name");
		Integer brandid = (Integer) map.get("brand_id");
		
		Flagship ship = new Flagship();
		ship.setBrandId(Long.valueOf(String.valueOf(brandid)));
		ship.setRealmName(realName);
		
		return ship;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<String> getOfferContentByMerchantId(Long merchantId) {
		
		String nowTime = DateUtils.getNowTime();
		
		List offerlist = flagshipDao.getOfferContentByMerchantId(merchantId, nowTime);
		
		if(offerlist == null || offerlist.size() == 0)
			 return null;
		
		List<String> contentlist = new LinkedList<String>();
		
		for(Object obj:offerlist){
			Map map = (Map) obj;
			String offerContent = (String) map.get("offers_contents");
			
			if(StringUtils.isNotBlank(offerContent))
				contentlist.add(offerContent);
		}
		
		return contentlist;
	}
	
}
