package com.beike.dao.background.brand.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.background.brand.BrandDao;
import com.beike.entity.background.brand.Brand;
import com.beike.form.background.brand.BrandForm;
import com.beike.util.StringUtils;
/**
 * Title : 	BrandDaoImpl
 * <p/>
 * Description	:	后台品牌数据访问实现
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
@Repository("brandDao")
public class BrandDaoImpl extends GenericDaoImpl<Brand,Long> implements BrandDao {

	//brand_seven_refound  brand_over_refound   brand_quality  brand_banner
	/*
	 * @see com.beike.dao.background.brand.BrandDao#addBrand(com.beike.form.background.brand.BrandForm)
	 */
	public String addBrand(BrandForm brandForm) throws Exception {
		if(!StringUtils.validNull(brandForm.getBrandSevenRefound())){
			brandForm.setBrandSevenRefound("0");
		}
		if(!StringUtils.validNull(brandForm.getBrandOverRefound())){
			brandForm.setBrandOverRefound("0");
		}
		if(!StringUtils.validNull(brandForm.getBrandQuality())){
			brandForm.setBrandQuality("0");
		}
		final BrandForm form = brandForm;
		final String sql="INSERT INTO beiker_brand(brand_id,brand_country_id,brand_province_id,brand_city_id,brand_name,brand_logo,brand_coupon_logo,brand_coupon_demo_logo,brand_banner_logo,brand_introduction,brand_business_desc,brand_book_phone,brand_status,brand_seven_refound,brand_over_refound,brand_quality,brand_modify_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now())";
		int flag = this.getJdbcTemplate().update(sql,new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, form.getBrandId());
				ps.setInt(2, form.getBrandCountryId());
				ps.setInt(3, form.getBrandProvinceId());
				ps.setInt(4, form.getBrandCityId());
				ps.setString(5, form.getBrandName());
				ps.setString(6, form.getBrandLogo());
				ps.setString(7, form.getBrandCouponLogo());
				ps.setString(8, form.getBrandCouponDemoLogo());
				ps.setString(9, form.getBrandBannerLogo());
				ps.setString(10, form.getBrandIntroduction());
				ps.setString(11, form.getBrandBusinessDesc());
				ps.setString(12, form.getBrandBookPhone());
				ps.setString(13, form.getBrandStatus());
				ps.setString(14, form.getBrandSevenRefound());
				ps.setString(15, form.getBrandOverRefound());
				ps.setString(16, form.getBrandQuality());
			}
		});
		return String.valueOf(flag);
	}

	/*
	 * @see com.beike.dao.background.brand.BrandDao#queryBrandByConditions(com.beike.form.background.brand.BrandForm, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Brand> queryBrandByConditions(BrandForm brandForm,int startRow, int pageSize) throws Exception {
		List<Brand> brandList = null;
		List tempList = null;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT brand_id,brand_country_id,brand_province_id,brand_city_id,brand_name,brand_seven_refound,brand_over_refound,brand_quality FROM beiker_brand ");
		sql.append("WHERE 1=1 ");
		if(brandForm.getBrandId()>0){
			sql.append(" AND  brand_id = ").append(brandForm.getBrandId());
		}
		if(StringUtils.validNull(brandForm.getBrandName())){
			sql.append(" AND brand_name like ").append("'%"+brandForm.getBrandName()+"%' ");
		}
		sql.append(" ORDER BY brand_id DESC LIMIT ?,? "); 
		Object[] params = new Object[]{startRow,pageSize};
		int[] types = new int[]{Types.INTEGER,Types.INTEGER};
		tempList = this.getJdbcTemplate().queryForList(sql.toString(),params,types);
		if(null!=tempList&&tempList.size()>0){
			brandList = this.convertResultToObjectList(tempList);
		}
		return brandList;
	}
	
	/*
	 * @see com.beike.dao.background.brand.BrandDao#queryBrandCountByConditions(com.beike.form.background.brand.BrandForm)
	 */
	public int queryBrandCountByConditions(BrandForm brandForm)
			throws Exception {
		int totalRows = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) FROM beiker_brand WHERE 1=1 ");
		if(brandForm.getBrandId()>0){
			sql.append(" AND  brand_id = ").append(brandForm.getBrandId());
		}
		if(StringUtils.validNull(brandForm.getBrandName())){
			sql.append(" AND brand_name like ").append("'%"+brandForm.getBrandName()+"%' ");
		}
		if(brandForm.getBrandProvinceId()>0){
			sql.append(" AND brand_province_id = ").append(brandForm.getBrandProvinceId());
		}
		if(brandForm.getBrandCityId()>0){
			sql.append(" AND brand_city_id = ").append(brandForm.getBrandCityId());
		}
		totalRows = this.getJdbcTemplate().queryForInt(sql.toString());
		return totalRows;
	}
	
	/*
	 * @see com.beike.dao.background.brand.BrandDao#queryBrandById(java.lang.String)
	 */
	public Brand queryBrandById(String brandId) throws Exception {
		Brand brand = new Brand();
		if(!StringUtils.validNull(brandId)){
			return brand;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT brand_id,brand_country_id,brand_province_id,brand_city_id,brand_name,brand_logo,brand_coupon_logo,brand_coupon_demo_logo,brand_banner_logo,brand_introduction,brand_business_desc,brand_book_phone,brand_seven_refound,brand_over_refound,brand_quality ");
		sql.append("FROM beiker_brand WHERE brand_id= ? ");
		brand = this.getSimpleJdbcTemplate().queryForObject(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(Brand.class), Integer.parseInt(brandId));
		return brand;
	}
	
	/*
	 * @see com.beike.dao.background.brand.BrandDao#editBrand(com.beike.form.background.brand.BrandForm)
	 */
	public String editBrand(BrandForm brandForm) throws Exception {
		if(!StringUtils.validNull(brandForm.getBrandSevenRefound())){
			brandForm.setBrandSevenRefound("0");
		}
		if(!StringUtils.validNull(brandForm.getBrandOverRefound())){
			brandForm.setBrandOverRefound("0");
		}
		if(!StringUtils.validNull(brandForm.getBrandQuality())){
			brandForm.setBrandQuality("0");
		}
		final BrandForm form = brandForm;
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE beiker_brand SET brand_country_id=?,brand_province_id=?,brand_city_id = ? ,brand_name = ?,brand_logo = ?,brand_coupon_logo = ?,brand_coupon_demo_logo = ?,brand_banner_logo = ?, ");
		sql.append("brand_introduction = ? ,brand_business_desc = ?,brand_book_phone = ?,brand_status = ?,brand_seven_refound = ?,brand_over_refound = ?,brand_quality = ?,brand_modify_time=now() ");
		sql.append("WHERE brand_id = ? ");
		int flag = this.getJdbcTemplate().update(sql.toString(),new PreparedStatementSetter() { 
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, form.getBrandCountryId());
				ps.setInt(2, form.getBrandProvinceId());
				ps.setInt(3, form.getBrandCityId());
				ps.setString(4, form.getBrandName());
				ps.setString(5, form.getBrandLogo());
				ps.setString(6, form.getBrandCouponLogo());
				ps.setString(7, form.getBrandCouponDemoLogo());
				ps.setString(8, form.getBrandBannerLogo());
				ps.setString(9, form.getBrandIntroduction());
				ps.setString(10, form.getBrandBusinessDesc());
				ps.setString(11, form.getBrandBookPhone());
				ps.setString(12, form.getBrandStatus());
				ps.setString(13, form.getBrandSevenRefound());
				ps.setString(14, form.getBrandOverRefound());
				ps.setString(15, form.getBrandQuality());
				ps.setInt(16, form.getBrandId());
			}
		});
		return String.valueOf(flag);
	}
	
	/*
	 * @see com.beike.dao.background.brand.BrandDao#validatorBrand(com.beike.form.background.brand.BrandForm)
	 */
	public boolean validatorBrand(BrandForm brandForm) throws Exception {
		boolean flag = false;
		StringBuilder sql = new StringBuilder();
		sql.append("  SELECT COUNT(1) FROM beiker_brand WHERE brand_city_id = ? AND brand_name = ? ");
		if(StringUtils.validNull(((Number)brandForm.getBrandId()).toString())){
				sql.append(" AND brand_id != ").append(brandForm.getBrandId());
		}
		Object[] params = new Object[]{brandForm.getBrandCityId(),brandForm.getBrandName()};
		int[] types = new int[]{Types.INTEGER,Types.VARCHAR};
		int count = this.getJdbcTemplate().queryForInt(sql.toString(), params, types);
		if(count>0){
			flag = true;
		}
		return flag;
	}
	
	/**
     * 将查询结果（map组成的List）转化成具体的对象列表
     * 
     * @param results jdbcTemplate返回的查询结果
     * @return 具体的对象列表
     * @author lvjx
     */
    @SuppressWarnings("unchecked")
	private List<Brand> convertResultToObjectList(List results) throws Exception{
        List<Brand> objList = new ArrayList<Brand>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                Map result = (Map) results.get(i);
                Brand brand = this.convertResultMapToObject(result);
                objList.add(brand);
            }
        }
        return objList;
    }
    
    /**
	 * 将查询结果元素（map对象）转化为具体对象
	 * 
	 * @param result   jdbcTemplate返回的查询结果元素（map对象）
	 * @return 具体的对象类型
	 * @author lvjx
	 */
	@SuppressWarnings("unchecked")
	private Brand convertResultMapToObject(Map result) throws Exception{
		Brand obj = new Brand();
			if (result != null) {
				
				
				Long brandId = ((Number)result.get("brand_id")).longValue();
				if(null!=brandId){
					obj.setBrandId(brandId.intValue());
				}
				Long brandCountryId = ((Number)result.get("brand_country_id")).longValue();
				if(null!=brandCountryId){
					obj.setBrandCountryId(brandCountryId.intValue());
				}
				Long brandProvinceId = ((Number)result.get("brand_province_id")).longValue();
				if(null!=brandProvinceId){
					obj.setBrandProvinceId(brandProvinceId.intValue());
				}
				Long brandCityId = ((Number)result.get("brand_city_id")).longValue();
				if(null!=brandCityId){
					obj.setBrandCityId(brandCityId.intValue());
				}
				if(StringUtils.validNull((String) result.get("brand_name"))){
					obj.setBrandName(result.get("brand_name").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_logo"))){
					obj.setBrandLogo(result.get("brand_logo").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_coupon_logo"))){
					obj.setBrandCouponLogo(result.get("brand_coupon_logo").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_coupon_demo_logo"))){
					obj.setBrandCouponDemoLogo(result.get("brand_coupon_demo_logo").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_banner_logo"))){
					obj.setBrandBannerLogo(result.get("brand_banner_logo").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_introduction"))){
					obj.setBrandIntroduction(result.get("brand_introduction").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_business_desc"))){
					obj.setBrandBusinessDesc(result.get("brand_business_desc").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_book_phone"))){
					obj.setBrandBookPhone(result.get("brand_book_phone").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_seven_refound"))){
					obj.setBrandSevenRefound(result.get("brand_seven_refound").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_over_refound"))){
					obj.setBrandOverRefound(result.get("brand_over_refound").toString());
				}
				if(StringUtils.validNull((String) result.get("brand_quality"))){
					obj.setBrandOverRefound(result.get("brand_quality").toString());
				}
			}
		return obj;
	}

}
