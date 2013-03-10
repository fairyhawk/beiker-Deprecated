package com.beike.dao.impl.shopcart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.miaosha.MiaoShaDao;
import com.beike.dao.shopcart.ShopCartDao;
import com.beike.entity.miaosha.MiaoSha;
import com.beike.entity.shopcart.ShopCart;
import com.beike.entity.shopcart.ShopItem;
import com.beike.util.DateUtils;

@Repository("shopCartDao")
public class ShopCartDaoImpl extends GenericDaoImpl<ShopCart, Long> implements ShopCartDao
{
	@Autowired
	private MiaoShaDao miaoshaDao;
	
	@Override
	public boolean addShopItem(ShopItem shopItem, String userid,String miaoshaId) {
		addShopItem(shopItem.getGoodsid().toString(), shopItem.getMerchantid().toString(), userid,miaoshaId);
		return true;
	}

	@Override
	public boolean updateShopItem(String goodsid, String count, String userid,String miaoshaId) {		
		String sql = "update beiker_shopcart set buy_count =? where goodsid=? and userid=? and miaoshaid=?";
		Long buyCount = Long.valueOf(count);
		if(buyCount.longValue()>999){
			buyCount = 999L;
		}
		getJdbcTemplate().update(sql, new Object[] { buyCount, goodsid, userid ,miaoshaId});
		return true;
	}

	public static void main(String[] args) {
		String str = "|【四川北路】仅1元乐...|【四川北路】仅9.9...|【四川北路】仅1元乐...|【四川北路】仅1元乐...|交通银行支付宝2012082339770141" +
				"|【四川北路】仅9.9..." +
				"|【周年庆派送红包】全场通用无限制，有效期截止到2012.08.26；逾期作废；不可提现。|| ";
		String[] strArray = str.split("\\|");
		System.out.println(strArray.length);
		for(int i=0;i<strArray.length;i++){
		System.out.println(strArray[i]);
		}
	}
	
	@Override
	public boolean removeShopItem(String goodsID, String userid,String miaoshaId) {
		String sql = "delete from beiker_shopcart where goodsid=? and userid=? and miaoshaid=?";
		int result = getSimpleJdbcTemplate().update(sql, goodsID, userid,miaoshaId);
		if (result > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 购买成功后根据表主键IN删除购物车
	 * 
	 * @param goodsIdStr
	 *            add by wenhua.cheng
	 */
	@Override
	public void removeShopCartBySuc(String goodsIdStr) {

		if ("".equals(goodsIdStr) || goodsIdStr == null) {
			throw new IllegalArgumentException("goodsIdStr is null");

		}
		
		String[] goodsIdStrs = goodsIdStr.split("\\|");
		String goodsIds =  goodsIdStrs[0];
		String goodsUserid = goodsIdStrs[1];
		StringBuffer sb = new StringBuffer();

		sb.append("delete from  beiker_shopcart where goodsid in (");
		sb.append(goodsIds);
		sb.append(") and userid=");
		sb.append(goodsUserid);
		sb.append(" and miaoshaid=0");
		getJdbcTemplate().update(sb.toString());

	}

	/**
	 * 购买成功后根据表秒杀ID删除购物车
	 * 
	 * @param goodsIdStr
	 *            add by wenhua.cheng
	 */
	@Override
	public void removeShopCartBySucMiaosha(String miaoshaIdStr) {

		if ("".equals(miaoshaIdStr) || miaoshaIdStr == null) {
			throw new IllegalArgumentException("goodsIdStr is null");

		}
		
		String[] miaoshaIdStrs = miaoshaIdStr.split("\\|");
		String miaoshaId =  miaoshaIdStrs[0];
		String goodsUserid = miaoshaIdStrs[1];
		StringBuffer sb = new StringBuffer();

		sb.append("delete from  beiker_shopcart where miaoshaid in (");
		sb.append(miaoshaId);
		sb.append(") and userid=");
		sb.append(goodsUserid);
		getJdbcTemplate().update(sb.toString());

	}
	@Override
	public List<ShopCart> queryShopCartByUserID(String userID) 
	{
		String sql = "select shopcartid,merchantid,goodsid,userid,buy_count,addtime,miaoshaid from beiker_shopcart where userid=? order by addtime";
		List<ShopItem> shopItemList = getSimpleJdbcTemplate().query(sql,
				ParameterizedBeanPropertyRowMapper.newInstance(ShopItem.class),
				userID);
		ShopItem shopItemTemp = null;
		ShopCart goodsInfo = null;
		List<ShopCart> shopCartList = new ArrayList<ShopCart>();
		for (Iterator<ShopItem> iterator = shopItemList.iterator(); iterator
				.hasNext();) {
			shopItemTemp = iterator.next();
			Long miaoshaId = shopItemTemp.getMiaoshaid();
			goodsInfo = findTempShopCart(shopItemTemp.getGoodsid());
			goodsInfo.setMiaoshaid(miaoshaId);
			if(miaoshaId!=0){
				MiaoSha miaoSha = miaoshaDao.getMiaoShaById(miaoshaId);
				goodsInfo.setGoodsName(miaoSha.getMsTitle());
				goodsInfo.setEndTime(miaoSha.getMsEndTime());
				goodsInfo.setMaxCount(Long.valueOf(miaoSha.getMsMaxCount()));
				goodsInfo.setUserBuyCount(Long.valueOf(miaoSha.getMsSingleCount()));
				goodsInfo.setSalesCount(Long.valueOf(miaoSha.getMsSaleCount()));
				goodsInfo.setGoodsCurrentPrice(miaoSha.getMsPayPrice());
				 int msStatus = miaoSha.getMsStatus();
			Date startTime = new Date(miaoSha.getMsStartTime().getTime());
			Date endTime =new Date(miaoSha.getMsEndTime().getTime());
			boolean booDate = DateUtils.betweenBeginAndEnd(new Date(),startTime,endTime);
				 if(msStatus==1&&booDate){
					 goodsInfo.setIsavaliable(1);
				 }else {
					 goodsInfo.setIsavaliable(0);
				 }
				 }
			ShopCart shopCart = new ShopCart();
			try {
				BeanUtils.copyProperties(shopCart, goodsInfo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			shopCart.setBuyCount(shopItemTemp.getBuy_count());
			shopCart.setMerchantId(shopItemTemp.getMerchantid());
			shopCart.setAddTime(shopItemTemp.getAddtime());
			shopCart.setMerchantName(findMerchantName(shopItemTemp.getMerchantid()));
			shopCart.setUserId(shopItemTemp.getUserid());
			if(miaoshaId==0){
			shopCart.setSalesCount(findGoodSalesCount(shopItemTemp.getGoodsid()));
			//获取个人可购买数量上限
			Long userBuyCount = findGoodsSingleCount(shopItemTemp.getGoodsid());
			shopCart.setUserBuyCount(userBuyCount);
			shopCart.setMiaoshaid(0L);
			}

			shopCartList.add(shopCart);
		}
		return shopCartList;
	}

	@Override
	public List<ShopItem> queryTempShopCart(String goodsID) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据 ShopItem中的merchantId 来获取 ShopCart 中的merchantname 信息 Add by zx.liu
	 */
	public String findMerchantName(Long merchantId) {

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT merchantname FROM beiker_merchant WHERE merchantId=? " );

		// String merchantName = (String) this.getJdbcTemplate().queryForObject(sql.toString(), String.class);		
		List<Map<String, Object>> merList = this.getSimpleJdbcTemplate().queryForList(sql.toString(), merchantId);
		if(null == merList || merList.size()==0){
			return "";
		}
		return merList.get(0).get("merchantname").toString();
	}


	/**
	 * 根据 ShopItem中的goodsId 来获取 ShopCart 中的profilevalue 信息 Add by zx.liu
	 */
	public Long findGoodSalesCount(Long goodsId) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT sales_count FROM beiker_goods_profile WHERE goodsid=? " );
		
		Long salesCount = this.getSimpleJdbcTemplate().queryForLong(sql.toString(), goodsId);		
		if(null == salesCount){
			return 0L;
		}
		return  salesCount;
	}	
	
	/**
	 * 根据 ShopItem中的goodsId 来获取 ShopCart
	 */
	public Long findGoodsSingleCount(Long goodsId) {

		String sql1 = "select goods_single_count from beiker_goods where goodsid="+goodsId;
		Long singleCount = this.getSimpleJdbcTemplate().queryForLong(sql1.toString());
		if(null == singleCount){
			singleCount= 0L;
		}
		return  singleCount;
	}	
	
	
	/**
	 * 根据 ShopItem中的goodsId 来获取 ShopCart 中商品相关的信息 Add by zx.liu
	 */
	public ShopCart findTempShopCart(Long goodsId) {

		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT goodsid, goodsname, currentPrice, rebatePrice, isavaliable, logo4, maxcount, endtime,goods_single_count ");
		sql.append(" FROM beiker_goods WHERE goodsid=? ");

		List<ShopCart> listCart = this.getSimpleJdbcTemplate().query(
				sql.toString(), new RowMapImpl(), goodsId);
		if (null == listCart || listCart.size() == 0) {
			return null;
		}
		return listCart.get(0);

	}
	// add by zx.liu
	// 实现ParameterizedRowMapper接口
	public class RowMapImpl implements ParameterizedRowMapper<ShopCart> {
		public ShopCart mapRow(ResultSet rs, int num) throws SQLException {
			ShopCart shopCart = new ShopCart();
			shopCart.setGoodsId(rs.getLong("goodsid"));
			shopCart.setGoodsName(rs.getString("goodsname"));
			shopCart.setGoodsCurrentPrice(rs.getDouble("currentPrice"));
			shopCart.setGoodsRebatePrice(rs.getBigDecimal("rebatePrice"));
			shopCart.setLogo4(rs.getString("logo4"));
			shopCart.setIsavaliable(rs.getInt("isavaliable"));
			shopCart.setMaxCount(rs.getLong("maxcount"));
			shopCart.setEndTime(rs.getTimestamp("endtime"));
			shopCart.setUserBuyCount(rs.getLong("goods_single_count"));
			
			return shopCart;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ShopItem isshopItemExists(String goodsid, String merchantID,
			String userid,String miaoshaId) {
		
		String sql = "select shopcartid,merchantid,goodsid,userid,buy_count,addtime,miaoshaid from beiker_shopcart where goodsid=? and merchantid=? and userid=? and miaoshaid=?";

		List shopItemList = null;
		shopItemList = getSimpleJdbcTemplate().query(sql,
				ParameterizedBeanPropertyRowMapper.newInstance(ShopItem.class),
				new Object[] { goodsid, merchantID, userid ,miaoshaId});
		if (shopItemList.size() > 0) {
			return (ShopItem) shopItemList.get(0);
		}
		return null;
	}

	@Override
	public boolean addShopItem(String goodsid, String merchantid, String userid,String miaoshaId) {
		return addShopItem(goodsid, merchantid, 1L, userid,miaoshaId);
	}


	/**
	 * 补充Dao的定义,用户登录时添加Cookie的购物信息到数据库
	 * 
	 * Add by zx.liu
	 * 
	 */
	public  boolean appendShopItem(ShopItem shopItem) {
		if(null == shopItem){
			return false;
		}
		String sql = null;
		ShopItem tempShopItem = null;
		// 登陆后添加产品至购物车
		// 当前产品存在,增加数量
		tempShopItem = isshopItemExists(shopItem.getGoodsid().toString(), shopItem.getMerchantid().toString(), shopItem.getUserid().toString(),shopItem.getMiaoshaid().toString());

		if (tempShopItem != null && 0 != tempShopItem.getAddtime().compareTo(shopItem.getAddtime())) {			
			sql = "update beiker_shopcart set buy_count=?,addtime=? where goodsid=? and userid=?";
			Long buyCount = shopItem.getBuy_count()+tempShopItem.getBuy_count();
			if(buyCount.longValue()>999){
				buyCount = 999L;
			}			
			getJdbcTemplate().update(sql, new Object[] { buyCount, shopItem.getAddtime(), shopItem.getGoodsid(), shopItem.getUserid()});			
			return true;
		} else if(tempShopItem == null){
			// 当前产品不在购物车,新添加商品
			
			sql = "insert into beiker_shopcart(merchantid,goodsid,userid,buy_count,addtime,miaoshaid) values(?,?,?,?,?,?)";
			getJdbcTemplate().update(sql, new Object[] { shopItem.getMerchantid(), shopItem.getGoodsid(), shopItem.getUserid(), shopItem.getBuy_count(), shopItem.getAddtime() ,shopItem.getMiaoshaid()});
			return true;
		}else{
			return false;
		}

	}	
	
	
	
	@Override
	public void removeBatchGoods(String[] goodsid, String userid,String[] miaoshaId) {
		int length = goodsid.length;
		for (int i = 0; i < length; i++) {
			removeShopItem(goodsid[i], userid,miaoshaId[i]);
		}

	}

	
	@Override
	public List<ShopCart> queryShopCartByMerchantID(String merchantID) {
		String sql = "select shopcartid,merchantid,goodsid,userid,buy_count,addtime from beiker_shopcart where merchantid=?";
		ShopCart goodsInfo = null;
		List<ShopCart> returnShopcartList = new ArrayList<ShopCart>();
		List<ShopCart> shopcartList = getSimpleJdbcTemplate().query(sql,
				ParameterizedBeanPropertyRowMapper.newInstance(ShopCart.class),
				new Object[] { merchantID });
		for (Iterator<ShopCart> iterator = shopcartList.iterator(); iterator
				.hasNext();) {
			ShopCart shopCart = iterator.next();
			try {
				goodsInfo = findTempShopCart(shopCart.getGoodsId());
				shopCart.setGoodsName(goodsInfo.getGoodsName());
				shopCart.setIsavaliable(goodsInfo.getIsavaliable());
				shopCart.setGoodsCurrentPrice(goodsInfo.getGoodsCurrentPrice());
				shopCart.setGoodsRebatePrice(goodsInfo.getGoodsRebatePrice());
				returnShopcartList.add(shopCart);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnShopcartList;
	}

	/* (non-Javadoc)
	 * @see com.beike.dao.shopcart.ShopCartDao#addShopItem(java.lang.String, java.lang.String, java.lang.Long, java.lang.String)
	 */
	public  boolean addShopItem(String goodsid, String merchantId,
			Long buyCount, String userid,String miaoshaId) {
		String sql;
		ShopItem shopItem;
		// 登陆后添加产品至购物车
		if (goodsid != null && merchantId != null) {
			// 当前产品存在,增加数量
			shopItem = isshopItemExists(goodsid, merchantId, userid,miaoshaId);
			if (shopItem != null) {
				sql = "update beiker_shopcart set buy_count=?,addtime=? where goodsid=? and userid=? and miaoshaid=?";
				buyCount = shopItem.getBuy_count()+buyCount;
				if(buyCount.longValue()>999){
					buyCount = 999L;
				}								
				getJdbcTemplate().update(sql, new Object[] { buyCount, new Date(), goodsid, userid,miaoshaId});
				return true;
			} else {
				// 当前产品不在购物车,新添加商品
				sql = "insert into beiker_shopcart(merchantid,goodsid,userid,buy_count,addtime,miaoshaid) values(?,?,?,?,?,?)";
				getJdbcTemplate().update(
						sql,
						new Object[] { merchantId, goodsid, userid, buyCount,
								new Date(),miaoshaId });
				return true;
			}
		}
		return false;
	}

	public  String addShopItemNew(String goodsid, String merchantId,
			Long buyCount, String userid,String miaoshaId,Long limitCount) {
		logger.info("++++++++++goodsid="+goodsid+"+++merchantId="+merchantId+"+++buyCount="+buyCount+"+++userid="+userid+"+++miaoshaId="+miaoshaId+"+++limitCount="+limitCount);
		String sql;
		ShopItem shopItem;
		String payLimitCount = "SUCCESS";
		Long byCount = 0L;
		// 登陆后添加产品至购物车
		if (goodsid != null && merchantId != null) {
			// 当前产品存在,增加数量
			shopItem = isshopItemExists(goodsid, merchantId, userid,miaoshaId);
			if (shopItem != null) {
			if(buyCount.longValue() + shopItem.getBuy_count()<limitCount){
				if(buyCount.longValue() + shopItem.getBuy_count() > 999 ){
					byCount=999L; //商品ID重复则加一,倘若数量超过999L则设为999L个 	
				} else {
					byCount = buyCount + shopItem.getBuy_count(); // 商品ID重复则数量加1
				}
				}else{
					if(limitCount > 999 ){
						byCount = 999L; //商品ID重复则加一,倘若数量超过999L则设为999L个 	
					}else{
						byCount = limitCount; // 
					}
					payLimitCount = "limitError";
				}
			}else{
				if(buyCount.longValue()<limitCount){
					if(buyCount.longValue()> 999 ){
						byCount=999L; //商品ID重复则加一,倘若数量超过999L则设为999L个 	
					} else {
						byCount = buyCount ; 
					}
					}else{
						if(limitCount > 999 ){
							byCount = 999L; //商品ID重复则加一,倘若数量超过999L则设为999L个 	
						}else{
							byCount = limitCount; // 
						}
						payLimitCount = "limitError";
					}
			}
			if (shopItem != null) {
				sql = "update beiker_shopcart set buy_count=?,addtime=? where goodsid=? and userid=? and miaoshaid=?";
				
				getJdbcTemplate().update(sql, new Object[] { byCount, new Date(), goodsid, userid,miaoshaId});
				return payLimitCount;
			} else {
				// 当前产品不在购物车,新添加商品
				sql = "insert into beiker_shopcart(merchantid,goodsid,userid,buy_count,addtime,miaoshaid) values(?,?,?,?,?,?)";
				getJdbcTemplate().update(
						sql,
						new Object[] { merchantId, goodsid, userid, byCount,
								new Date(),miaoshaId });
				return payLimitCount;
			}
		}
		return payLimitCount;
	}
	@Override
	public List<ShopItem> queryShopCartInfoByUserID(Long userID,Long pageSize,Long pageoffset )
	{
		
		String sql = "select shopcartid,merchantid,goodsid,userid,buy_count,addtime from beiker_shopcart where userid=? limit ?, ? ";
		List<ShopItem> shopItemList = getSimpleJdbcTemplate().query(sql, ParameterizedBeanPropertyRowMapper.newInstance(ShopItem.class), userID, pageoffset, pageSize);
		
		return shopItemList;
	}
	@Override
	public long   queryShopCartCountByUserID(Long userID )
	{
		
		String sql = "select count(shopcartid ) from beiker_shopcart where userid=? ";
		long  count = getSimpleJdbcTemplate().queryForLong(sql, userID);
		return count;
	}
	
	@Override
	public  Map<String, Object> queryShopCartById(String shopcartId)
	{
		  String sql ="select shopcartid,merchantid,goodsid,userid,buy_count,addtime from beiker_shopcart where shopcartid=?";  
		  Map<String, Object> resultMap = this.getSimpleJdbcTemplate().queryForMap(sql, shopcartId);
		  
		return resultMap;
	}

	@Override
	public void removeShopItemByids(String shoppingCartIds, Long userid)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("delete from beiker_shopcart where  userid=? and  shopcartid in ( ") ;
		sql.append(shoppingCartIds);
		sql.append(" ) ") ;
		
	    getSimpleJdbcTemplate().update(sql.toString(), userid);
		
	}

	@Override
	public int getShopItemCount(Long userId, Long goodsId)
	{
		String sql = " select count(shopcartid) as total  from beiker_shopcart where buy_count>0 and  userid=? and goodsid=? ";
		Map<String, Object> resultMap = this.getSimpleJdbcTemplate().queryForMap(sql, userId, goodsId);
		Long total = (Long) resultMap.get("total");
		return total.intValue();
	}
	
	
}
