package com.beike.dao.card;

import com.beike.common.entity.card.Card;
import com.beike.common.exception.StaleObjectStateException;
import com.beike.dao.GenericDao;

/**
 * @author yurenli
 * 卡密记录Dao
 * 2011-12-16 10:38:02
 */
public interface CardDao extends GenericDao<Card, Long>{
	
	/**
	 *  根据主键查询
	 * @param cardNo
	 * @return
	 */
	public Card findById(Long id);
	/**
	 *  根据卡号查询
	 * @param cardNo
	 * @return
	 */
	public Card findByCardNo(String cardNo,String cardPwd);
	/**
	 * 根据卡号，卡密信息更新
	 * @param cardNo
	 * @param cardPwd
	 * @param userId
	 * @param version
	 */
	public void updateBycardNoorcardPwd(String cardNo,String cardPwd,Long userId,Long version) throws StaleObjectStateException;
	
	/**
	 * 数据插入
	 * @param card
	 * @return
	 */
	public Long insertCard(Card card);
	/**
	 * 数据更新
	 * @param card
	 * @return
	 */
	public void updateCard(Card card)throws StaleObjectStateException;
	
}
