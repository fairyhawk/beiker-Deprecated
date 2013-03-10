package com.beike.dao.mobile.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.mobile.LuceneMobileAssistDao;
import com.beike.util.StringUtils;


@Repository("appSearcAssistDao")
public class LuceneMobileAssistDaoImpl extends GenericDaoImpl implements LuceneMobileAssistDao{

	
	@Override
	public List<Map<String, Object>> getBranchOfBrand(List<Long> brandids) {
		String sql = "SELECT COUNT(branch.parentid) total,branch.parentid merchantid FROM beiker_merchant branch WHERE branch.parentId IN(:brandids) AND branch.parentId !=0 GROUP BY branch.parentid";
		MapSqlParameterSource args = new MapSqlParameterSource("brandids",brandids);
		return getSimpleJdbcTemplate().queryForList(sql,args);
	}
	
	
	
	@Override
	public List<Map<String,Object>> getBrandofGoods(List<Long> goodsids) {
		
		String sql = "SELECT bgm.merchantid FROM beiker_goods_merchant bgm JOIN beiker_merchant brand ON brand.merchantid=bgm.merchantid WHERE bgm.goodsid in(:goodsids) and  brand.parentId=0";
		
		MapSqlParameterSource args = new MapSqlParameterSource("goodsids", goodsids);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}
	
	
	@Override
	public Map<Long,String> getCityEnName() {
		String sql = "SELECT ba.area_en_name,ba.area_id FROM beiker_area ba";
		List area_en_name = getJdbcTemplate().queryForList(sql);
		Map<Long,String> map = new HashMap<Long, String>();
		
		for(int i=0;i<area_en_name.size();i++){
			map.put((Long)((Map)area_en_name.get(i)).get("area_id"), (String)((Map)area_en_name.get(i)).get("area_en_name"));
		}
		return map;
	}

	@Override
	public List<Map> getBrandList(List<Long> brandidList) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT bm.merchantid brandid,bm.merchantname brandname,bmp.mc_logo1 imgurl,bmp.mc_sale_count salescount,DATE_FORMAT(bgbbu.last_update_time,'%Y-%m-%d %T') updatetime FROM beiker_merchant bm JOIN beiker_merchant_profile bmp ON bm.merchantid=bmp.merchantid JOIN beiker_goods_brand_branch_updatetime bgbbu ON bgbbu.id=bm.merchantid AND bgbbu.category='brand' WHERE bmp.merchantid IN(").append(StringUtils.arrayToString(brandidList.toArray(), ",")).append(")");
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Map> getGoodsList(List<Long> goodsidList) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT bg.goodsid,bg.discount,bg.goodsname,DATE_FORMAT(bg.endTime,'%Y-%m-%d %T') endtime, CONCAT('/jsp/uploadimages/',bg.logo3) listimgurl,CONCAT('/jsp/uploadimages/',bg.logo1) imgurl,bg.goods_title title,bg.currentPrice currentprice,bg.sourcePrice sourceprice,bgp.sales_count salescount,bg.isavaliable isavailable,bg.kindlywarnings tip,bg.maxcount max, DATE_FORMAT(bgbbu.last_update_time,'%Y-%m-%d %T') updatetime,isRefund refund FROM beiker_goods bg LEFT JOIN beiker_goods_profile bgp ON bgp.goodsid=bg.goodsid LEFT JOIN beiker_goods_brand_branch_updatetime bgbbu ON bgbbu.id=bg.goodsid AND bgbbu.category='goods' WHERE bg.goodsid IN(").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")")
		.append(" order by field(bg.goodsid,").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")");
		//sql.append("SELECT bg.goodsid,bg.goodsname,CONCAT('/jsp/uploadimages/',bg.logo3) listimgurl,CONCAT('/jsp/uploadimages/',bg.logo1) imgurl,bg.goods_title title,bg.sourcePrice sourceprice,bg.isavaliable isavailable,bg.kindlywarnings tip,bg.maxcount max, DATE_FORMAT(bgbbu.last_update_time,'%Y-%m-%d %T') updatetime FROM beiker_goods bg  LEFT JOIN beiker_goods_brand_branch_updatetime bgbbu ON bgbbu.id=bg.goodsid AND bgbbu.category='goods' WHERE bg.goodsid IN(").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")");

		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Map> getGoodsBranch(boolean fir,List<Long> goodsidList) {
		StringBuilder sql = new StringBuilder();
		if(fir){
			sql.append("SELECT DISTINCT bgm.goodsid,brb.regionid storeregionid,bm.merchantname storename,bm.merchantid storeid,bm.addr storeaddr,bm.latitude coord,bm.tel,buinesstime opentime FROM beiker_goods_merchant bgm JOIN beiker_region_branch brb ON bgm.merchantid=brb.branchid JOIN beiker_merchant bm ON bm.merchantid=brb.branchid WHERE bgm.goodsid IN(").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")");
		}else{
			sql.append("SELECT DISTINCT bgm.goodsid,brb.regionextid storeregionextid,bm.merchantname storename,bm.merchantid storeid,bm.addr storeaddr,bm.latitude coord,bm.tel,buinesstime opentime FROM beiker_goods_merchant bgm JOIN beiker_region_branch brb ON bgm.merchantid=brb.branchid JOIN beiker_merchant bm ON bm.merchantid=brb.branchid WHERE bgm.goodsid IN(").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")");

		}
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Map> getGoodsBrand(boolean first,List<Long> goodsidList) {
		StringBuilder sql = new StringBuilder();
		if(first){
			sql.append("SELECT DISTINCT bm.merchantname brandname,bm.tel,bcg.tagid brandcatfir,bcg.goodid goodsid,bcg.brandid FROM beiker_catlog_good bcg JOIN beiker_merchant bm ON bcg.brandid=bm.merchantid WHERE bcg.goodid IN(").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")");
		}else{
			sql.append("SELECT DISTINCT bm.merchantname brandname,bm.tel,bcg.tagextid brandcatsec,bcg.goodid goodsid,bcg.brandid FROM beiker_catlog_good bcg JOIN beiker_merchant bm ON bcg.brandid=bm.merchantid WHERE bcg.goodid IN(").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")");
		}
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Map> getBrandBasic(List<Long> brandidList) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT bm.merchantid brandid,bm.merchantname brandname,bm.merchantintroduction brandstory,CONCAT('/jsp/uploadimages/',bmp.mc_logo1) imgurl,bmp.mc_sale_count salescount,DATE_FORMAT(bgbbu.last_update_time,'%Y-%m-%d %T') updatetime FROM beiker_merchant bm JOIN beiker_merchant_profile bmp ON bm.merchantid=bmp.merchantid JOIN beiker_goods_brand_branch_updatetime bgbbu ON bgbbu.id=bm.merchantid AND bgbbu.category='brand' WHERE bmp.merchantid IN(").append(StringUtils.arrayToString(brandidList.toArray(),",")).append(")");
		//sql.append("SELECT bm.merchantid brandid,bm.merchantname brandname,bm.merchantintroduction brandstory,CONCAT('/jsp/uploadimages/',bmp.mc_logo1) imgurl,DATE_FORMAT(bgbbu.last_update_time,'%Y-%m-%d %T') updatetime FROM beiker_merchant bm JOIN beiker_merchant_profile bmp ON bm.merchantid=bmp.merchantid JOIN beiker_goods_brand_branch_updatetime bgbbu ON bgbbu.id=bm.merchantid AND bgbbu.category='brand' WHERE bmp.merchantid IN(").append(StringUtils.arrayToString(brandidList.toArray(),",")).append(")");

		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Map> getBrandBranches(boolean fir,List<Long> brandidList) {
		
		StringBuilder sql = new StringBuilder();
		if(fir){
			sql.append("SELECT DISTINCT bm.parentId brandid,bm.merchantid storeid,bm.addr storeaddr,bm.latitude coord,bm.tel tel,bm.buinesstime opentime,bm.merchantname storename, brb.regionid storeregionid FROM beiker_merchant bm JOIN beiker_region_branch brb ON brb.branchid=bm.merchantid WHERE bm.parentId IN(").append(StringUtils.arrayToString(brandidList.toArray(), ",")).append(")");
		}else{
			sql.append("SELECT DISTINCT brb.brandid,brb.branchid storeid,brb.regionextid storeregionextid FROM beiker_region_branch brb WHERE brb.brandid IN(").append(StringUtils.arrayToString(brandidList.toArray(), ",")).append(")");
		}
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Map> getBrandCat(boolean fir,List<Long> brandidList) {
		StringBuilder sql = new StringBuilder();
		if(fir){
			sql.append("SELECT DISTINCT bcg.brandid,bcg.tagid brandcatfir FROM beiker_catlog_good bcg WHERE bcg.brandid IN(").append(StringUtils.arrayToString(brandidList.toArray(), ",")).append(")");
		}else{
			sql.append("SELECT DISTINCT bcg.brandid,bcg.tagextid brandcatsec FROM beiker_catlog_good bcg WHERE bcg.brandid IN(").append(StringUtils.arrayToString(brandidList.toArray(), ",")).append(")");

		}
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Map> getBrandPhoto(Long brandid) {
		String sql = "SELECT bsp.shb_logo1,bsp.shb_logo2,bsp.shb_logo3,bsp.shb_logo4,bsp.shb_logo5,bsp.shb_logo6,bsp.shb_logo7,bsp.shb_logo8 FROM beiker_shanghubao_profile bsp WHERE bsp.merchantid=?";
		return getJdbcTemplate().queryForList(sql, new Object[]{brandid});
	}

	@Override
	public List<Map> getBrandGoodsDeatil(List<Long> goodsidList) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT bg.goodsid,CONCAT('/jsp/uploadimages/',bg.logo3) listimgurl,CONCAT('/jsp/uploadimages/',bg.logo1) imgurl,bg.goodsname,bg.goods_title title,bg.currentPrice currentprice,bg.sourcePrice sourceprice,bgp.sales_count salescount,bg.isavaliable isavailable,DATE_FORMAT(bgbbu.last_update_time,'%Y-%m-%d %T') updatetime FROM beiker_goods bg JOIN beiker_goods_profile bgp ON bg.goodsid=bgp.goodsid JOIN beiker_goods_brand_branch_updatetime bgbbu ON bgbbu.id=bg.goodsid AND bgbbu.category='goods' WHERE bg.goodsid IN(").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")");
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Long> getBrandGoodsIDList(Long brandid) {
		String sql = "SELECT bcg.goodid FROM beiker_catlog_good bcg WHERE bcg.brandid=? AND bcg.isavaliable=1 GROUP BY bcg.goodid";
		return getJdbcTemplate().queryForList(sql, new Object[]{brandid}, Long.class);
	}
    //@FIXME janwen remove before merge
	@Override
	public List<Map<String, Object>> getindexdata() {
		String sql = "select bm.latitude,brand.merchantname,brand.merchantid from beiker_merchant bm join beiker_merchant brand on brand.merchantid=bm.parentId  where brand.merchantid=91689664 order  by brand.merchantid limit 1000;";
		List<Map<String,Object>> re = getJdbcTemplate().queryForList(sql);
		return re;
	}

	@Override
	public List<Map> getGoodsCat(boolean fir, List<Long> goodsidList) {
		StringBuilder sql = new StringBuilder();
		if(fir){
			sql.append("SELECT DISTINCT bcg.goodid goodsid,bcg.regionid storeregionid FROM beiker_catlog_good bcg WHERE bcg.goodid IN(").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")");
		}else{
			sql.append("SELECT DISTINCT bcg.goodid goodsid,bcg.regionextid storeregionextid FROM beiker_catlog_good bcg WHERE bcg.goodid IN(").append(StringUtils.arrayToString(goodsidList.toArray(), ",")).append(")");
		}
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<String> getBrandIntrByGoodsID(Long goodsid) {
		String sql = "SELECT bm.merchantintroduction FROM beiker_catlog_good bcg JOIN beiker_merchant bm ON bm.merchantid=bcg.brandid WHERE bcg.goodid=? AND bm.parentId=0 LIMIT 1";
		return getJdbcTemplate().queryForList(sql, new Object[]{goodsid}, String.class);
	}

	@Override
	public List<Map> getBranchesByBrandID(List<Long> brandids) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT bm.merchantid storeid,bm.parentId brandid FROM beiker_merchant bm RIGHT JOIN beiker_goods_merchant bgm ON bgm.merchantid=bm.merchantid WHERE bm.parentId !=0 AND bm.parentId IN(").append(StringUtils.arrayToString(brandids.toArray(), ",")).append(")");
		return getJdbcTemplate().queryForList(sql.toString());
	}

	@Override
	public List<Map<String, Object>> getGoodsSale(List<Long> querygoodsid) {
		String sql = "SELECT bgp.goodsid,(bgp.sales_count + bg.virtual_count) sale FROM beiker_goods_profile bgp JOIN beiker_goods bg ON bgp.goodsid=bg.goodsid WHERE bgp.goodsid IN(:querygoodsid) ";
		SqlParameterSource args = new MapSqlParameterSource("querygoodsid", querygoodsid);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}

	@Override
	public List<Map<String, Object>> getGoodsCurrentPrice(
			List<Long> querygoodsid) {
		String sql = "SELECT bg.goodsid,bg.currentPrice price FROM beiker_goods bg WHERE bg.goodsid IN(:querygoodsid)";
		SqlParameterSource args = new MapSqlParameterSource("querygoodsid", querygoodsid);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}

	@Override
	public List<Map<String, Object>> getBranchGoods(List<Long> querybranchid) {
		String sql = "SELECT DISTINCT bgm.merchantid branchid,bgm.goodsid FROM beiker_merchant branch JOIN beiker_goods_merchant bgm ON branch.merchantid=bgm.merchantid WHERE branch.parentId !=0";
		SqlParameterSource args = new MapSqlParameterSource("querybranchid", querybranchid);
		return getSimpleJdbcTemplate().queryForList(sql, args);
	}


}
