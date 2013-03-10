package com.beike.dao.trx.lottery.full.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.lottery.full.FullLottery;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.lottery.full.FullLotteryDao;
import com.beike.entity.goods.Goods;

@Repository("fullLotteryDao")
public class FullLotteryDaoImpl extends GenericDaoImpl<FullLottery, Long> implements FullLotteryDao
{

	@Override
	public FullLottery findById(Long id)
	{
		if (null == id || 0 == id.longValue())
		{
			throw new IllegalArgumentException("userId not null");
		}

		StringBuilder sql = new StringBuilder();
		sql.append(" select id, user_id, city_name ,create_date, lottery_type, is_lottery, lottery_content, description ");
		sql.append("  from beiker_full_lottery_record where id = ? ");

		FullLottery fullLottery = this.getSimpleJdbcTemplate().queryForObject(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(FullLottery.class), id);

		return fullLottery;
	}

	@Override
	public List<FullLottery> findFullLotteryList(int recordNumber)
	{
		StringBuilder sql = new StringBuilder();

		sql.append(" select id, user_id, city_name,  create_date, lottery_type, is_lottery, lottery_content, description ");
		sql.append(" from beiker_full_lottery_record where is_lottery =1 order by id desc  limit 0, ? ");

		List<FullLottery> fullLotteryList = this.getSimpleJdbcTemplate().query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(FullLottery.class), recordNumber);

		return fullLotteryList;
	}

	@Override
	public Map<String, Object> findTrxorderGoods(Long userId, String startDate, String endDate)
	{
		StringBuilder sql = new StringBuilder();

		sql.append("select sum(pay_price) as price from beiker_trxorder_goods g left join beiker_trxorder t on g.trxorder_id=t.id ");
		sql.append(" where g.trx_status in('SUCCESS','USED','COMMENTED') and '");
		sql.append(startDate);
		sql.append("'<g.create_date and '");
		sql.append(endDate);
		sql.append("'>g.create_date and t.user_id=?");
		Map<String, Object> trxorderGoodsMap = this.getSimpleJdbcTemplate().queryForMap(sql.toString(), userId);

		return trxorderGoodsMap;
	}

	@Override
	public Map<String, Object> findFullLotteryByUserId(Long userId)
	{
		StringBuilder sql = new StringBuilder();

		sql.append("select count(1) as count from beiker_full_lottery_record");
		sql.append(" where user_id=?");
		Map<String, Object> trxorderGoodsMap = this.getSimpleJdbcTemplate().queryForMap(sql.toString(), userId);

		return trxorderGoodsMap;
	}

	@Override
	public long getFullLotteryTotal()
	{
		StringBuilder sql = new StringBuilder();

		sql.append(" select count(id)  as total  from beiker_full_lottery_record ");

		long total = this.getSimpleJdbcTemplate().queryForLong(sql.toString());

		return total;
	}

	@Override
	public List<Goods> findLotteryGoodsList(String GoodsIds)
	{

		StringBuilder sql = new StringBuilder();

		sql.append(" select goodsid, goodsname, endTime, isavaliable, order_lose_date, logo2, logo3  from beiker_goods   ");
		sql.append(" where isavaliable=1 and  goodsid in  (" + GoodsIds + ")   ");

		List<Goods> goodsIdList = this.getSimpleJdbcTemplate().query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(Goods.class));

		return goodsIdList;

	}

	@Override
	public Long addFullLottery(FullLottery fullLottery)
	{

		Long userId = fullLottery.getUserId();
		Date createDate = fullLottery.getCreateDate();
		Long lotteryType = fullLottery.getLotteryType();
		Long isLottery = fullLottery.getIsLottery();
		String lotteryContent = fullLottery.getLotteryContent();
		String desc = fullLottery.getDescription();
		String cityName = fullLottery.getCityName();

		StringBuilder sql = new StringBuilder();
		sql.append(" insert into beiker_full_lottery_record( user_id, create_date,  ");
		sql.append(" lottery_type, is_lottery, lottery_content, description, city_name ) ");
		sql.append(" values ( ?, ?, ? ,?, ? ,?, ?  ) ");

		getSimpleJdbcTemplate().update(sql.toString(), userId, createDate, lotteryType, isLottery, lotteryContent, desc, cityName);
		Long fullLotteryId = getLastInsertId();
		
		return fullLotteryId;
	}


}
