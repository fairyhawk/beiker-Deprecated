package com.beike.service.impl.merchant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.action.pay.PayInfoParam;
import com.beike.dao.GenericDao;
import com.beike.dao.merchant.BranchProfileDao;
import com.beike.dao.merchant.MerchantDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.entity.merchant.BranchProfile;
import com.beike.entity.merchant.Merchant;
import com.beike.entity.merchant.MerchantProfileType;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;
import com.beike.service.impl.GenericServiceImpl;
import com.beike.service.merchant.MerchantService;

/**
 * <p>
 * Title:商户服务
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * <p>
 * Company: Sinobo
 * </p>
 * 
 * @date May 17, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("merchantService")
public class MerchantServiceImpl extends GenericServiceImpl implements
		MerchantService {

	@Autowired
	private MerchantDao merchantDao;
	
	@Autowired
	private BranchProfileDao branchProfileDao;
	
	@Override
	public GenericDao getDao() {
		return merchantDao;

	}

	public void addMerchant(MerchantForm form) {
		// TODO:增加商户扩展信息

		// 增加商户
		merchantDao.addMerchant(form);
	}

	public Merchant getMerchantById(Long merchantId) {
		return merchantDao.getMerchantById(merchantId);

	}

	public Object findById(Serializable id) {
		return null;
	}

	public MerchantDao getMerchantDao() {
		return merchantDao;
	}

	public void setMerchantDao(MerchantDao merchantDao) {
		this.merchantDao = merchantDao;
	}

	public Map<String, String> getMerchantEvaluationScores(Long merchantId) {
		return merchantDao.getEvaluationAvgScoreByMerchantId(merchantId);

	}

	public String getGoodsMerchantLogo(Long merchantId) {

		MerchantProfileType mf = merchantDao
				.getMerchantProfileTypeByMerchantId(merchantId,"mc_logo1");

		if (mf == null)
			return null;

		return mf.getPropertyvalue();

	}

	public String getAvgEvationScores(Long merchantId) {
		MerchantProfileType mf = merchantDao
				.getMerchantProfileTypeByMerchantId(merchantId,"mc_avg_scores");
		if (mf == null)
			return null;
		return mf.getPropertyvalue();
	}

	public String getEvationCount(Long merchantId) {
		MerchantProfileType mf = merchantDao
				.getMerchantProfileTypeByMerchantId(merchantId,"mc_evaliation_count");
		if (mf == null)
			return null;
		return mf.getPropertyvalue();

	}

	public String getMerchantSalesCount(Long merchantId) 
	{
		MerchantProfileType mf = merchantDao.getMerchantProfileTypeByMerchantId(merchantId, "mc_sale_count");
		if (mf == null) {
			return null;
		}
		return mf.getPropertyvalue();

	}

	public String getFixTel(Long merchantId) {
		MerchantProfileType mf = merchantDao
				.getMerchantProfileTypeByMerchantId(merchantId,"mc_fix_tel");
		if (mf == null)
			return null;
		return mf.getPropertyvalue();

	}

	/**
	 * 根据商品ID获得预约电话 add by wenhua.cheng
	 * 
	 * @param merchantId
	 * @return
	 */
	public String getFixTelByGoodsId(Long goodsId) {

		MerchantProfileType mf = merchantDao.getMerchantProfileTypeByGoodsId(goodsId, "mc_fix_tel");
		if (mf == null)
			return null;
		return mf.getPropertyvalue();

	}

	/**
	 * 根据商品ID查询商家 add by wenhua.cheng
	 * 
	 * @param merchantId
	 * @return
	 */
	public Map<String, String> getMerchantByGoodsId(Long goodsId) {
		return merchantDao.getMerchantByGoodsId(goodsId);

	}
	
	/**
	 * 根据商品ID查询商家分店信息 add by renli.yu
	 * 
	 * @param merchantId
	 * @return
	 */
	public List<PayInfoParam> getMerchantsByGoodsId(Long goodsId) {
		return merchantDao.getMerchantsByGoodsId(goodsId);

	}
	
	public MerchantForm getMerchantFormById(Long merchantId) {
		return merchantDao.getMerchantDetailById(merchantId);

	}

	public List<MerchantForm> getChildMerchnatById(Long merchantId, Pager pager) {
		List<MerchantForm> lstBranch = merchantDao.getChildMerchnatById(merchantId,
				pager.getStartRow(), pager.getPageSize());
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

	public List<MerchantForm> getChildMerchnatById(Long merchantId) {
		return merchantDao.getChildMerchnatById(merchantId);
	}

	public int getChildMerchantCount(Long merchantId) {
		return merchantDao.getChildMerchantCount(merchantId);
	}

	public List<MerchantForm> getMerchantFormByCouponId(String couponId,
			Pager pager) {
		return merchantDao.getMerchantFormByCouponId(
				Integer.parseInt(couponId), pager.getStartRow(), pager
						.getPageSize());
	}

	public int getMerchantFormByCouponCount(Integer couponId) {
		return merchantDao.getMerchantFormByCouponCount(couponId);

	}
	


	
	/**
	 * 补充方法：
	 * 根据品牌ID, 来获取该品牌下商品的虚拟购买次数
	 * 
	 * 
	 * Add by zx.liu
	 */
	public Long getMerchantVirtualCount (Long merchantId){
		
		Long merVirtualCount = merchantDao.getMerchantVirtualCountById(merchantId);
		if(null == merVirtualCount){
			return 0L;
		}		
		return merVirtualCount;
	}

	@Override
	public int getBrandCatlogCount(AbstractCatlog abstractCatlog) {
		return merchantDao.searchBrandCatlogCount(abstractCatlog);
	}

	@Override
	public List<Long> getBrandCatlog(AbstractCatlog abstractCatlog, Pager pager) {
		int startRow=pager.getStartRow();
		return merchantDao.searchBrandCatlog(abstractCatlog,startRow,pager.getPageSize());
	}

	@Override
	public Long getMerchantIdByDomainName(String domainName) {
		return merchantDao.getMerchantIdByDomainName(domainName);
		
	}

	@Override
	public List<MerchantForm> getDiancaiChildBranchid(Long branchid) {
		return merchantDao.getDiancaiChildMerchant(branchid);
	}

	/**
	 * 根据商家 ID查询商家分店信息 add by ljp
	 * 
	 * @param merchantId
	 * @return
	 * @date 20121128
	 */
	public List<PayInfoParam> getMerchantsByMerchantId(Long merchantId) {
		List<MerchantForm> mfs = merchantDao.getDiancaiChildMerchant(merchantId);
		if (mfs == null || mfs.size() == 0)
			return null;
		List<PayInfoParam> pipList = new ArrayList<PayInfoParam>();
		for (int i = 0; i < mfs.size(); i++) {
			MerchantForm mf = (MerchantForm) mfs.get(i);
			PayInfoParam pip = new PayInfoParam();
			pip.setMertOrdTel(mf.getTel());
			pip.setMerchantAddr(mf.getAddr());
			pip.setMerchantName(mf.getMerchantname());
			pipList.add(pip);
		}
		return pipList;
	}
	
}
