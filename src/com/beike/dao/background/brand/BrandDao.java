package com.beike.dao.background.brand;

import java.util.List;

import com.beike.dao.GenericDao;
import com.beike.entity.background.brand.Brand;
import com.beike.form.background.brand.BrandForm;
/**
 * 
 * Title : 	BrandDao
 * <p/>
 * Description	: 后台品牌访问数据接口
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
 * <pre>1     2011-5-30    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-30
 */
public interface BrandDao extends GenericDao<Brand,Long> {

	/**
	 * Description : 增加品牌
	 * @param brandForm
	 * @return
	 * @throws Exception
	 */
	public String addBrand(BrandForm brandForm) throws Exception;
	
	/**
	 * Description : 查询品牌信息
	 * @param brandForm
	 * @param startRow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public List<Brand> queryBrandByConditions(BrandForm brandForm,int startRow,int pageSize) throws Exception;
	
	/**
	 * Description : 根据查询条件查询品牌条数
	 * @param brandForm
	 * @return int
	 * @throws Exception
	 */
	public int queryBrandCountByConditions(BrandForm brandForm) throws Exception;
	
	/**
	 * Description : 根据brandId查询品牌信息
	 * @param brandId
	 * @return
	 * @throws Exception
	 */
	public Brand queryBrandById(String brandId) throws Exception;
	
	/**
	 * Description : 修改品牌接口
	 * @param brandForm 
	 * @return
	 * @throws Exception
	 */
	public String editBrand(BrandForm brandForm) throws Exception;
	
	/**
	 * Description : 校验同一地点品牌名是否重复
	 * @param brandForm
	 * @return
	 * @throws Exception
	 */
	public boolean validatorBrand(BrandForm brandForm) throws Exception;
}
