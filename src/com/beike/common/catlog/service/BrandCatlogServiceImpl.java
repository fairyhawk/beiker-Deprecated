package com.beike.common.catlog.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.catlog.CatlogDao;
import com.beike.dao.merchant.MerchantDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.form.MerchantForm;
import com.beike.page.Pager;

/**
 * <p>Title:品牌类别Service</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 26, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("brandCatlogService")
public class BrandCatlogServiceImpl implements BrandCatlogService {
	
	@Resource(name="brandCatlogDao")
	private CatlogDao catlogDao;
	
	@Autowired
	private MerchantDao merchantDao;
	
	public MerchantDao getMerchantDao() {
		return merchantDao;
	}

	public void setMerchantDao(MerchantDao merchantDao) {
		this.merchantDao = merchantDao;
	}

	public CatlogDao getCatlogDao() {
		return catlogDao;
	}

	public void setCatlogDao(CatlogDao catlogDao) {
		this.catlogDao = catlogDao;
	}

	public List<Long> getCatlog(AbstractCatlog abstractCatlog,Pager pager) {
		int startRow=pager.getStartRow();
		return catlogDao.searchCatlog(abstractCatlog,startRow,pager.getPageSize());
	}



	public int getCatlogCount(AbstractCatlog abstractCatlog) {
		return catlogDao.searchCatlogCount(abstractCatlog);
		
	}

	public List<MerchantForm> getGoodsFromIds(List<Long> listids) {
		
		if(listids==null||listids.size()==0)return null;
		StringBuilder sb=new StringBuilder();
		for (Long long1 : listids) {
			sb.append(long1);
			sb.append(",");
		}
		String course=sb.subSequence(0,sb.lastIndexOf(",")).toString();
		return merchantDao.getMerchantByIds(course);
		
	}

	@Override
	public boolean checkMerchantStatus(Long merchantid) {
		return merchantDao.checkMerchantStatus(merchantid);
	}

	@Override
	public List<Long> getCatlog(List<Long> validGoodsIdList, AbstractCatlog abstractCatlog, Pager pager) {
		return null;
	}

	
	

}
