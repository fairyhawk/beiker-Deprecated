package com.beike.service.background.brand.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.dao.background.brand.BrandDao;
import com.beike.entity.background.brand.Brand;
import com.beike.form.background.brand.BrandForm;
import com.beike.service.background.brand.BrandService;

/**
 * Title : 	BrandServiceImpl
 * <p/>
 * Description	:后台品牌服务实现类
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-5-30   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-30  
 */
@Service("brandService")
public class BrandServiceImpl implements BrandService {

	/*
	 * @see com.beike.service.background.brand.BrandService#addBrand(com.beike.form.background.brand.BrandForm)
	 */
	public String addBrand(BrandForm brandForm) throws Exception {
		String result = null;
		result = brandDao.addBrand(brandForm);
		return result;
	}
	
	/*
	 * @see com.beike.service.background.brand.BrandService#queryBrandByConditions(com.beike.form.background.brand.BrandForm, int, int)
	 */
	public List<Brand> queryBrandByConditions(BrandForm brandForm,
			int startRow, int pageSize) throws Exception {
		List<Brand> brandList = null;
		brandList = brandDao.queryBrandByConditions(brandForm, startRow, pageSize);
		return brandList;
	}
	
	/*
	 * @see com.beike.service.background.brand.BrandService#queryBrandCountByConditions(com.beike.form.background.brand.BrandForm)
	 */
	public int queryBrandCountByConditions(BrandForm brandForm)
			throws Exception {
		int totalRows = 0;
		totalRows = brandDao.queryBrandCountByConditions(brandForm);
		return totalRows;
	}
	
	/*
	 * @see com.beike.service.background.brand.BrandService#queryBrandById(java.lang.String)
	 */
	public Brand queryBrandById(String brandId) throws Exception {
		Brand brand = null;
		brand = brandDao.queryBrandById(brandId);
		return brand;
	}

	/*
	 * @see com.beike.service.background.brand.BrandService#editBrand(com.beike.form.background.brand.BrandForm)
	 */
	public String editBrand(BrandForm brandForm) throws Exception {
		String result = null;
		result = brandDao.editBrand(brandForm);
		return result;
	}
	
	/*
	 * @see com.beike.service.background.brand.BrandService#validatorBrand(com.beike.form.background.brand.BrandForm)
	 */
	public boolean validatorBrand(BrandForm brandForm) throws Exception {
		boolean flag = false;
		flag = brandDao.validatorBrand(brandForm);
		return flag;
	}

	@Resource(name = "brandDao")
	private BrandDao brandDao;

}
