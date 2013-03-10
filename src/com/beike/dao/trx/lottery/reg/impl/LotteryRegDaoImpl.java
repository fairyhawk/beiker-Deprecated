package com.beike.dao.trx.lottery.reg.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.trx.lottery.reg.LotteryReg;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.trx.lottery.reg.LotteryRegDao;

@Repository("lotteryRegDao")
public class LotteryRegDaoImpl extends GenericDaoImpl<LotteryReg, Long> implements LotteryRegDao
{

	@Override
	public LotteryReg findById(Long id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("lotteryId not null");
		}
		String sql = "SELECT id,user_id,create_date,is_lottery,lottery_content,description FROM beiker_reg_lottery_record where id=?";
		List<LotteryReg> lotteryRegList = getSimpleJdbcTemplate().query(sql, new RowMapperImpl(), id);
		if (lotteryRegList.size() > 0)
		{
			return lotteryRegList.get(0);
		}
		return null;
	}

	@Override
	public LotteryReg findByUserId(Long userId)
	{
		if (userId == null ||  userId.intValue()==0)
		{
			throw new IllegalArgumentException("userId not null");
		}
		String sql = "SELECT id,user_id,create_date,is_lottery,lottery_content,description  FROM beiker_reg_lottery_record where user_id=? ";
		List<LotteryReg> lotteryRegList = getSimpleJdbcTemplate().query(sql, new RowMapperImpl(), userId);
		if (lotteryRegList!=null && lotteryRegList.size() > 0)
		{
			return lotteryRegList.get(0);
		}
		return null;
	}

	public class RowMapperImpl implements ParameterizedRowMapper<LotteryReg>
	{
		@Override
		public LotteryReg mapRow(ResultSet rs, int num) throws SQLException
		{
			LotteryReg lotteryReg = new LotteryReg();
			lotteryReg.setId(rs.getLong("id"));
			lotteryReg.setUserId(rs.getLong("user_id"));
			lotteryReg.setCreateDate(rs.getTimestamp("create_date"));
			lotteryReg.setIsLottery(rs.getBoolean("is_lottery"));
			lotteryReg.setLotteryContent(rs.getString("lottery_content"));
			lotteryReg.setDescription(rs.getString("description"));

			return lotteryReg;
		}
	}

	@Override
	public Long addLotteryReg(LotteryReg lotteryReg)
	{
		if (lotteryReg == null || lotteryReg.getUserId() == null || lotteryReg.getUserId() <= 0)
		{
			throw new IllegalArgumentException("lotteryReg object nou null");
		} else
		{
			StringBuffer sb = new StringBuffer();

			sb.append("insert into beiker_reg_lottery_record(user_id,create_date,is_lottery,lottery_content,description) ");
			sb.append(" value(?,?,?,?,?)");
			getSimpleJdbcTemplate().update(sb.toString(), lotteryReg.getUserId(), lotteryReg.getCreateDate(), lotteryReg.getIsLottery(), lotteryReg.getLotteryContent(), lotteryReg.getDescription());

		}
		Long lotteryRegId = getLastInsertId();
		return lotteryRegId;
	}

	@Override
	public List<LotteryReg> findLotteryRegList(int recordNumber)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT id,user_id,create_date,is_lottery,lottery_content,description FROM beiker_reg_lottery_record   where is_lottery=1 order by create_date desc limit 0, ? ");
		List<LotteryReg> lotteryRegList = getSimpleJdbcTemplate().query(sb.toString(), new RowMapperImpl(),recordNumber);

		return lotteryRegList;
	}

	@Override
	public int findLotteryRegTotal()
	{
		String sql = "select count(id) as total from beiker_reg_lottery_record  where  is_lottery=1" ;
		int  total = this.getSimpleJdbcTemplate().queryForInt(sql);
		return total;
	}

}
