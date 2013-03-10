package com.beike.wap.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.util.Constant;
import com.beike.util.ImageZipUtil;
import com.beike.util.PropertyUtil;
import com.beike.wap.dao.MCouponDetailDao;
import com.beike.wap.entity.MCoupon;
import com.beike.wap.service.MCouponDetailService;

@Service("mCouponDetailService")
public class MCouponDetailServiceImpl implements MCouponDetailService{
	
	private static Log log = LogFactory.getLog(MCouponDetailServiceImpl.class);
	
	@Autowired
	private MCouponDetailDao mCouponDetailDao;
	
	private static final PropertyUtil propertyUtil = PropertyUtil
	.getInstance(Constant.WAP_PATH);
	
	@Override
	public MCoupon findById(Long id) {
		MCoupon coupon = mCouponDetailDao.findById(id);
		
		if(coupon == null)
		{
			return null;
		}
		String detail = coupon.getCouponDetailLogo();
		if(detail == null || detail.trim().equals(""))
		{
			return coupon;
		}
		
		String detailArr[] = detail.split("/");
		String fileName = detailArr[detailArr.length - 1];
		coupon.setDetailLogoName(fileName);
		String tempPath = detail.substring(0, detail.indexOf(fileName)).trim();
		
		File zipFile = new File(propertyUtil.getProperty("ZIP_UPLOADIMAGES_PATH") + detail);
		if(zipFile.exists())
		{
			coupon.setCoupon_wap_url(Constant.WAP_URL_FIELD + "/jsp/wap/uploadimages/"+ detail);
		}
		else
		{
			File logoFile = new File(propertyUtil.getProperty("OLD_UPLOADIMAGES_PATH") + detail);
			InputStream input = null;
			try {
				input = new FileInputStream(logoFile);
				ImageZipUtil.resizeWapImage(input, 
						propertyUtil.getProperty("ZIP_UPLOADIMAGES_PATH") + tempPath, 
						fileName, 
						290,
						0);
				coupon.setCoupon_wap_url(Constant.WAP_URL_FIELD + "/jsp/wap/uploadimages/"+detail);
			} catch (FileNotFoundException e) {
				log.info("\nzip logo file FileNotFoundException " +
						"\ncoupon id is "+coupon.getId());
				coupon.setCoupon_wap_url(Constant.WAP_URL_FIELD + "/jsp/uploadimages/"+detail);
				e.printStackTrace();
				return coupon;
			} catch (IOException e) {
				log.info("\nzip logo file IOException " +
						"\ncoupon id is "+coupon.getId());
				coupon.setCoupon_wap_url(Constant.WAP_URL_FIELD + "/jsp/uploadimages/"+detail);
				e.printStackTrace();
				return coupon;
			}finally{
				if(input != null)
				{
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return coupon;	 
	}

	@Override
	public List<MCoupon> queryCouponByBrandId(long brandId) {
		return mCouponDetailDao.findCouponByBrandId(brandId);
	}

	@Override
	public List<MCoupon> queryCouponByIdS(String ids) {
		if(ids == null || ids.equals(""))
		{
			return null;
		}
		return mCouponDetailDao.findCouponByIds(ids);
	}
}
