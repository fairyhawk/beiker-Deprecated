package com.beike.common.catlog.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.catlog.CatlogDao;
import com.beike.dao.coupon.CouponDao;
import com.beike.entity.catlog.AbstractCatlog;
import com.beike.form.CouponForm;
import com.beike.page.Pager;

/**
 * <p>Title:优惠券 服务实现</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: Sinobo</p>
 * @date May 21, 2011
 * @author ye.tian
 * @version 1.0
 */
@Service("couponCatlogService")
public class CouponCatlogServiceImpl implements CouponCatlogService {
	
	@Resource(name="couponCatlogDao")
	private CatlogDao catLogDao;
	
	@Autowired
	private CouponDao couponDao;
	
	public CouponDao getCouponDao() {
		return couponDao;
	}

	public void setCouponDao(CouponDao couponDao) {
		this.couponDao = couponDao;
	}

	public CatlogDao getCatLogDao() {
		return catLogDao;
	}

	public void setCatLogDao(CatlogDao catLogDao) {
		this.catLogDao = catLogDao;
	}

	public List<Long> getCatlog(AbstractCatlog abstractCatlog,Pager pager) {
		int startRow=pager.getStartRow();
		return catLogDao.searchCatlog(abstractCatlog,startRow,pager.getPageSize());
	}

	public List<CouponForm> getCouponFormByPage(List<Long> listids, Pager pager) {
		if(listids==null||listids.size()==0)return null;
		StringBuilder sb=new StringBuilder();
		for (Long long1 : listids) {
			sb.append(long1);
			sb.append(",");
		}
		String course=sb.subSequence(0,sb.lastIndexOf(",")).toString();
		
		return couponDao.getCouponByIds(course);
		
	}

	public int getCatlogCount(AbstractCatlog abstractCatlog) {
		return catLogDao.searchCatlogCount(abstractCatlog);
		
	}

	public List<CouponForm> getCouponFormByIds(List<Long> listids) {
		if(listids==null||listids.size()==0)return null;
		StringBuilder sb=new StringBuilder();
		for (Long long1 : listids) {
			sb.append(long1);
			sb.append(",");
		}
		String course=sb.subSequence(0,sb.lastIndexOf(",")).toString();
		
		return couponDao.getCouponByIds(course);
		
	}

	@Override
	public List<Long> getCatlog(List<Long> validGoodsIdList, AbstractCatlog abstractCatlog, Pager pager) {
		return null;
	}

	/*@Override
	public String getCatByID(Long id) {
		return catLogDao.getCatByID(id);
	}*/


	
}
