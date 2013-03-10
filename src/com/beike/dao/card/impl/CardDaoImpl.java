package com.beike.dao.card.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.beike.common.entity.card.Card;
import com.beike.common.enums.trx.CardStatus;
import com.beike.common.enums.trx.CardType;
import com.beike.common.exception.BaseException;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDaoImpl;
import com.beike.dao.card.CardDao;
import com.beike.util.EnumUtil;

/**
 * @author yurenli
 * 卡密记录表
 *2011-12-15 16:59:16
 */
@Repository("cardDao")
public class CardDaoImpl extends GenericDaoImpl<Card, Long> implements CardDao{
	
	@Override
	public Card findById(Long id) {

		if (id == null) {
			return null;
		} else {
			String sql = "select id,create_date,card_no,card_pwd,card_value,card_type,card_status,bacth_id,order_id,topup_channel" +
					",update_date,lose_date,user_id,vm_account_id,biz_id,version,description from beiker_card where id = ?";
			List<Card> cardList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), id);
			if (cardList.size() > 0) {
				return cardList.get(0);
			}
			return null;
		}

	}

	
	@Override 
	public Card findByCardNo(String cardNo,String cardPwd) {

		if (cardNo == null ||cardPwd == null) {
			return null;
		} else {
			String sql = "select id,create_date,card_no,card_pwd,card_value,card_type,card_status,bacth_id,order_id,topup_channel" +
					",update_date,lose_date,user_id,vm_account_id,biz_id,version,description from beiker_card where card_no=? and card_pwd=?";
			List<Card> cardList = getSimpleJdbcTemplate().query(sql,
					new RowMapperImpl(), cardNo,cardPwd);
			if (cardList.size() > 0) {
				return cardList.get(0);
			}
			return null;
		}

	}

	@Override
	public void updateBycardNoorcardPwd(String cardNo,String cardPwd,Long userId,Long version) throws StaleObjectStateException{
		
		if("".equals(cardNo)||"".equals(cardPwd)){
			return;
		}
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append("update  beiker_card set card_status=?,update_date=?,user_id=?");
		sqlSb.append(",version=? where card_no=? and card_pwd=? and version=?");
		
		int count = getSimpleJdbcTemplate().update(sqlSb.toString(),CardStatus.USED.name(),new Date(),userId,version+1L,cardNo,cardPwd,version);

		if(count==0){
			throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR); 
		}
		
	}
	
	@Override
	public void updateCard(Card card) throws StaleObjectStateException {
		if (card == null || card.getId() == null) {
			return;
		} else {
			StringBuilder sqlSb = new StringBuilder();
			sqlSb.append("update  beiker_card set create_date=?,card_no=?,card_pwd=?,card_value=?,card_type=?,card_status=?,");
			sqlSb.append("bacth_id=?,order_id=?,topup_channel=?,update_date=?,lose_date=?,user_id=?,vm_account_id=?" +
					",biz_id=?,version=?,description=? where id=? and version=?");
			
			int count = getSimpleJdbcTemplate().update(sqlSb.toString(),card.getCreateDate()
					,card.getCardNo(),card.getCardPwd(),card.getCardValue(),card.getCardType().name(),
					card.getCardStatus().name(),card.getBacthId(),card.getOrderId(),card.getTopupChannel(),
					card.getUpdateDate(),card.getLoseDate(),card.getUserId(),card.getVmAccountId(),
					card.getBizId(),card.getVersion()+1L,card.getDescription(),
					card.getId(),card.getVersion()
					);

			if(count==0){
				throw new StaleObjectStateException(BaseException.OPTIMISTIC_LOCK_ERROR); 
			}
		}
	}
	@Override
	public Long insertCard(final Card card) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (card == null) {
			throw new IllegalArgumentException("card not null");
		}
		final String istSql = "insert into beiker_card(create_date,card_no,card_pwd,card_value,card_type,card_status,bacth_id,order_id,topup_channel" +
				",update_date,lose_date,user_id,vm_account_id,biz_id,version,description) value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		this.getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(
						istSql,
						new String[] { "create_date", "card_no",
								"card_pwd", "card_value", "card_type","card_status","bacth_id","order_id",
								"topup_channel","update_date","lose_date","user_id","vm_account_id","biz_id","version","description"});

				ps.setTimestamp(1,new java.sql.Timestamp((card.getCreateDate()).getTime()));
				ps.setString(2,card.getCardNo());
				ps.setString(3,card.getCardPwd());
				ps.setInt(4, card.getCardValue());
				ps.setString(5,card.getCardType().name());
				ps.setString(6,card.getCardStatus().name());
				ps.setLong(7,card.getBacthId());
				ps.setLong(8,card.getOrderId());
				ps.setString(9,card.getTopupChannel());
				ps.setTimestamp(10,new java.sql.Timestamp((card.getUpdateDate()).getTime()));
				ps.setTimestamp(11, new java.sql.Timestamp((card.getLoseDate()).getTime()));
				ps.setLong(12,card.getUserId());
				ps.setLong(13,card.getVmAccountId());
				ps.setLong(14,card.getBizId());
				ps.setLong(15,card.getVersion());
				ps.setString(16,card.getDescription());
				return ps;
			}

		}, keyHolder);
		Long cardId = keyHolder.getKey().longValue();
		return cardId;
	}
	
	/*
	 * `create_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `card_no` varchar(16) NOT NULL DEFAULT '' COMMENT '卡号',
  `card_pwd` varchar(16) NOT NULL DEFAULT '' COMMENT '卡密',
  `card_value` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '面值(冗余)',
  `card_type` char(10) NOT NULL DEFAULT '' COMMENT '卡类型(冗余)',
  `card_status` char(10) NOT NULL DEFAULT '' COMMENT '卡状态：待印刷入库、已印刷入库、已发放未激活、已发放已激活、已使用、已过期、已废弃',
  `bacth_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '所属批次',
  `order_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '所属购卡订单',
  `topup_channel` char(10) NOT NULL DEFAULT '' COMMENT '充值渠道',
  `update_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `lose_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间(冗余)',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `vm_account_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '所属虚拟款项ID(冗余)',
  `biz_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '业务ID',
  `version` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `description` char(50) NOT NULL DEFAULT '' COMMENT '备注信息',
	 */
	public class RowMapperImpl implements ParameterizedRowMapper<Card> {
		@Override
		public Card mapRow(ResultSet rs, int num) throws SQLException {
			Card card = new Card();
			card.setId(rs.getLong("id"));
			card.setCreateDate(rs.getTimestamp("create_date"));
			card.setCardNo(rs.getString("card_no"));
			card.setCardPwd(rs.getString("card_pwd"));
			card.setCardValue(rs.getInt("card_value"));
			card.setCardType(EnumUtil.transStringToEnum(CardType.class,rs.getString("card_type")));
			card.setCardStatus(EnumUtil.transStringToEnum(CardStatus.class,rs.getString("card_status")));
			card.setBacthId(rs.getLong("bacth_id"));
			card.setOrderId(rs.getLong("order_id"));
			card.setTopupChannel(rs.getString("topup_channel"));
			card.setUpdateDate(rs.getTimestamp("update_date"));
			card.setLoseDate(rs.getTimestamp("lose_date"));
			card.setUserId(rs.getLong("user_id"));
			card.setVmAccountId(rs.getLong("vm_account_id"));
			card.setBizId(rs.getLong("biz_id"));
			card.setVersion(rs.getLong("version"));
			card.setDescription(rs.getString("description"));
			return card;

		}
	}

}
