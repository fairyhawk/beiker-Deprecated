package com.beike.wap.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.beike.util.Constant;
import com.beike.util.ImageZipUtil;
import com.beike.util.PropertyUtil;
import com.beike.util.RandomNumberUtils;
import com.beike.util.StringUtils;
import com.beike.wap.dao.MMerchantDao;
import com.beike.wap.entity.MMerchant;
import com.beike.wap.entity.MMerchantProfileType;
import com.beike.wap.service.MMerchantService;

@Repository("mMerchantService")
public class MMerchantServiceImpl implements MMerchantService {

	private static Log log = LogFactory.getLog(MMerchantServiceImpl.class);
	
	private static final PropertyUtil propertyUtil = PropertyUtil
	.getInstance(Constant.WAP_PATH);
	
	@Override
	public MMerchant getBrandById(long brandId) {
		MMerchant brand = mMerchantDao.getBrandById(brandId);
		if(brand == null)
		{
			return null;
		}
		String scores = brand.getAvgscores();
		int resultScore = RandomNumberUtils.mulScore(scores);
		brand.setAvgscores(String.valueOf(resultScore));
		
		String detail = brand.getLogoTitle();
		if(detail == null || detail.trim().equals(""))
		{
			return brand;
		}
		
		String detailArr[] = detail.split("/");
		String fileName = detailArr[detailArr.length - 1];
		brand.setLogoTitleName(fileName);
		String tempPath = detail.substring(0, detail.indexOf(fileName)).trim();

		File zipFile = new File(propertyUtil.getProperty("ZIP_UPLOADIMAGES_PATH") + detail);
		if(zipFile.exists())
		{
			brand.setZipLogoTitle(Constant.WAP_URL_FIELD + "/jsp/wap/uploadimages/" + detail);
			return brand;
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
				brand.setZipLogoTitle(Constant.WAP_URL_FIELD + "/jsp/wap/uploadimages/" + detail);
				return brand;
			} catch (FileNotFoundException e) {
				log.info("\nzip the title logo file of brand FileNotFoundException " +
						"\nbrand id is "+brand.getMerchantid());
				brand.setZipLogoTitle(Constant.WAP_URL_FIELD + "/jsp/uploadimages/" + detail);
				e.printStackTrace();
				return brand;
			} catch (IOException e) {
				log.info("\nzip the title logo file of brand IOException " +
						"\nbrand id is "+brand.getMerchantid());
				brand.setZipLogoTitle(Constant.WAP_URL_FIELD + "/jsp/uploadimages/" + detail);
				e.printStackTrace();
				return brand;
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
	}
	
	/*
	 * @see com.beike.wap.service.MMerchantService#getAvgEvationScores(java.lang.Long)
	 */
	public String getAvgEvationScores(Long merchantId) {
		MMerchantProfileType mf = mMerchantDao.getMerchantAvgEvationScoresByMerchantId(merchantId);
		if (mf == null)
			return null;
		return mf.getPropertyvalue();
	}

	/*
	 * @see com.beike.wap.service.MMerchantService#getGoodsMerchantLogo(java.lang.Long)
	 */
	public String getGoodsMerchantLogo(Long merchantId) {

		MMerchantProfileType mf = mMerchantDao.getMerchantLogoByMerchantId(merchantId);

		if (mf == null)
			return null;

		return mf.getPropertyvalue();

	}

	@Override
	public List<MMerchant> getBranchByBrandId(long brandId) {
		return mMerchantDao.getBranchIdByParentId(brandId);
	}
	@Override
	public List<MMerchant> getBrandByIds(String brandIds) {
		if(!StringUtils.validNull(brandIds))
		{
			return null;
		}
		return mMerchantDao.getBrandListByIds(brandIds);
	}
	
	@Resource(name = "mMerchantDao") 
	private MMerchantDao mMerchantDao;

	
	/*
	 * @see com.beike.wap.service.MMerchantService#getBrandByGoodId(java.lang.String)
	 */
	@Override
	public MMerchant getBrandByGoodId(String goodsId) {
		MMerchant merchant = null;
		merchant = mMerchantDao.getBrandByGoodId(goodsId);
		return merchant;
	}

}
