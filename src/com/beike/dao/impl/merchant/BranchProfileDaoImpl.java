package com.beike.dao.impl.merchant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;

import com.beike.dao.GenericDaoImpl;
import com.beike.dao.merchant.BranchProfileDao;
import com.beike.entity.merchant.BranchProfile;

/**
 * project:beiker Title: Description: Copyright:Copyright (c) 2011
 * Company:Sinobo
 * 
 * @author qiaowb
 * @date Mar 15, 2012 2:22:15 PM
 * @version 1.0
 */
@Repository("branchProfileDao")
public class BranchProfileDaoImpl extends GenericDaoImpl<BranchProfile, Long>
		implements BranchProfileDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.beike.dao.merchant.BranchProfileDao#getBranchProfileById(java.lang
	 * .String)
	 */
	@Override
	public List<BranchProfile> getBranchProfileById(String ids) {
		StringBuilder selSql = new StringBuilder(
				"select merchantid,branchid,well_count,satisfy_count,poor_count ")
				.append("from beiker_branch_profile where branchid in (")
				.append(ids).append(")");

		List<BranchProfile> lstBranch = null;
		lstBranch = this.getSimpleJdbcTemplate().query(selSql.toString(),
				new RowMapperImpl());
		return lstBranch;
	}

	protected class RowMapperImpl implements
			ParameterizedRowMapper<BranchProfile> {
		@Override
		public BranchProfile mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			BranchProfile profile = new BranchProfile();
			profile.setMerchantId(rs.getLong("merchantid"));
			profile.setBranchId(rs.getLong("branchid"));
			profile.setWellCount(rs.getLong("well_count"));
			profile.setSatisfyCount(rs.getLong("satisfy_count"));
			profile.setPoorCount(rs.getLong("poor_count"));
			return profile;
		}
	}

	@Override
	public BranchProfile getBranchProfileById(Long branchid) {
		StringBuilder selSql = new StringBuilder(
				"select merchantid,branchid,well_count,satisfy_count,poor_count ")
				.append("from beiker_branch_profile where branchid=?");

		BranchProfile lstBranch = (BranchProfile) getJdbcTemplate().queryForObject(selSql.toString(), new Object[]{branchid}, new RowMapperImpl());
		return lstBranch;
	}
}