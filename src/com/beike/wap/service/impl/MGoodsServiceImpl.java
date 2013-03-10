package com.beike.wap.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.beike.util.Constant;
import com.beike.util.ImageZipUtil;
import com.beike.util.PropertyUtil;
import com.beike.wap.dao.MGoodsDao;
import com.beike.wap.entity.MGoods;
import com.beike.wap.entity.MGoodsCatlog;
import com.beike.wap.service.MGoodsService;

/**
 * Title : GoodsServiceImpl
 * <p/>
 * Description :商品服务实现类
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : qianpin.com </P> JDK Version Used : JDK 5.0 +
 * <p/>
 * Modification History :
 * <p/>
 * 
 * <pre>
 * NO.    Date    Modified By    Why & What is modified
 * </pre>
 * 
 * <pre>1     2011-09-20   lvjx			Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-09-20
 */
@Service("wapGoodsService")
public class MGoodsServiceImpl implements MGoodsService {
	
	private static final PropertyUtil propertyUtil = PropertyUtil
	.getInstance(Constant.WAP_PATH);
	/*
	 * @see com.beike.wap.service.goods.GoodsService#queryIndexShowMes(int, int,
	 * int, java.util.Date)
	 */
	public List<MGoods> queryIndexShowMes(int typeType, int typeFloor,
			int typePage, Date currentDate, String typeArea) throws Exception {
		List<MGoods> goodsList = null;
		goodsList = goodsDao.queryIndexShowMes(typeType, typeFloor, typePage,
				currentDate, typeArea);
		return goodsList;
	}

	/*
	 * @see com.beike.wap.service.goods.MGoodsService#queryDetailShowMes(int)
	 */
	@Override
	public MGoods queryDetailShowMes(int goodsId) throws Exception {
		MGoods mGoods = null;
		mGoods = goodsDao.queryDetailShowMes(goodsId);
		return mGoods;
	}

	/*
	 * @see com.beike.wap.service.goods.MGoodsService#getMerchantById(int,
	 * java.util.Date, java.lang.String)
	 */
	@Override
	public MGoods getMerchantById(int goodsId)
			throws Exception {
		MGoods mGoods = null;
		mGoods = goodsDao.getMerchantById(goodsId);
		return mGoods;
	}

	/*
	 * @see com.beike.wap.service.goods.MGoodsService#getBranchById(int,
	 * java.util.Date, java.lang.String)
	 */
	@Override
	public List<MGoods> getBranchById(int goodsId) throws Exception {
		List<MGoods> goodsList = null;
		goodsList = goodsDao.getBranchById(goodsId);
		return goodsList;
	}
	
	/*
	 * @see com.beike.wap.service.goods.MGoodsService#queryGoodsIds(com.beike.wap.entity.GoodsCatlog)
	 */
	@Override
	public List<Long> queryGoodsIds(int page,MGoodsCatlog goodsCatlog) throws Exception {
		List<Long> goodsIdList = null;
		goodsIdList = goodsDao.queryGoodsIds(page,goodsCatlog);
		return goodsIdList;
	}
	
	/*
	 * @see com.beike.wap.service.goods.MGoodsService#queryGoodsIdsSum(com.beike.wap.entity.GoodsCatlog)
	 */
	@Override
	public int queryGoodsIdsSum(MGoodsCatlog goodsCatlog) throws Exception {
		int sum = 0;
		sum = goodsDao.queryGoodsIdsSum(goodsCatlog);
		return sum;
	}
	
	/*
	 * @see com.beike.wap.service.goods.MGoodsService#queryGoodsInfo(java.lang.String)
	 */
	@Override
	public List<MGoods> queryGoodsInfo(List<Long> goodsIds) throws Exception {
		List<MGoods> goodsList = null;
		StringBuilder goodsId = new StringBuilder("");
		for(Long goodId : goodsIds){
			if(null==goodsId || goodsId.length()==0){
				goodsId.append(goodId);
			}else{
				goodsId.append(",").append(goodId);
			}
		}
		goodsList = goodsDao.queryGoodsInfo(goodsId.toString());
		return goodsList;
	}
	
	/*
	 * @see com.beike.wap.service.MGoodsService#addShopItem(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addShopItem(String goodsid, String merchantId, int buySum,String userid)
			throws Exception {
		boolean flag = false; 
		flag = goodsDao.addShopItem(goodsid, merchantId, buySum,userid);
		return flag;
	}

	@Override
	public MGoods findById(Long id) {
		MGoods goods = null;
		try {
			goods = goodsDao.findById(id);
		} catch (Exception e) {
			log.info("find goods by id have an exception, id is " + id);
			e.printStackTrace();
			return null;
		}
		return goods;
	}

	@Resource(name = "wapGoodsDao")
	private MGoodsDao goodsDao;

	private static Log log = LogFactory.getLog(MGoodsServiceImpl.class);
	
	@Override
	public List<MGoods> queryGoodsByBrandId(long brandId) throws Exception {
		
		List<Long> goodsIdList = goodsDao.queryGoodsIdByBrandId(brandId);
		
		if(goodsIdList == null){
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		for(Long id : goodsIdList){
			sb.append(id).append(",");
		}
		
		if(sb.length() != 0){
			sb = sb.delete(sb.length() - 1, sb.length());
		}
		
		 List<MGoods> list = goodsDao.queryGoodsByBrandId(sb.toString());
		 if(list == null)
		 {
			 return list;
		 }
		 MGoods goods = list.get(0);
		 if(goods == null)
		 {
			 return null;
		 }
		 String detail = goods.getLogo1();
		 
		 String detailArr[] = detail.split("/");
		 String fileName = detailArr[detailArr.length - 1];
		 goods.setLogo1(fileName);
		 String tempPath = detail.substring(0, detail.indexOf(fileName)).trim();
		 InputStream input = null;
		 try {
			File zipFile = new File(propertyUtil.getProperty("ZIP_UPLOADIMAGES_PATH") + detail);
			if(zipFile.exists())
			{
				goods.setZipLogo1(Constant.WAP_URL_FIELD + "/jsp/uploadimages/" + detail);
			}
			else
			{
				File logoFile = new File(propertyUtil.getProperty("OLD_UPLOADIMAGES_PATH") + detail);
				input = new FileInputStream(logoFile);
				ImageZipUtil.resizeWapImage(input, 
						propertyUtil.getProperty("ZIP_UPLOADIMAGES_PATH") + tempPath, 
						fileName, 
						290, 
						0);
				goods.setZipLogo1(Constant.WAP_URL_FIELD + "/jsp/wap/uploadimages/" + detail);
			}
		 } catch (FileNotFoundException e) {
			log.info("\nzip goods logo1 file FileNotFoundException " +
				"\ngooods id is "+goods.getGoodsId());
			goods.setZipLogo1(Constant.WAP_URL_FIELD + "/jsp/uploadimages/" + detail);
			e.printStackTrace();
		 }finally{
			 if(input != null)
			 {
				 input.close();
			 }
		 }
		 return list;
	}

	/*
	 * @see com.beike.wap.service.MGoodsService#queryMaxDate(int, int, int, java.lang.String)
	 */
	@Override
	public Date queryMaxDate(int typeType, int typeFloor, int typePage,
			String typeArea) throws Exception {
		Date maxDate = null;
		maxDate = goodsDao.queryMaxDate(typeType, typeFloor, typePage, typeArea);
		return maxDate;
	}

}
