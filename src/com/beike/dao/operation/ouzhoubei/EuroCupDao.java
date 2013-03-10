package com.beike.dao.operation.ouzhoubei;

import java.util.List;
import java.util.Map;

import com.beike.entity.operation.ouzhoubei.Predict;

public interface EuroCupDao {

	/**
	 * 
	 * janwen
	 * @param userid
	 * @return 用户参与过的竞猜
	 *
	 */
	public List<Map> getUserPredicts(Long userid);
	
	
	
	/**
	 * 
	 * janwen
	 * @param userid
	 * @param matchid
	 * @return 用户是否已经参与当前比赛
	 *
	 */
	public Long getMatchesByUserid(Long userid,List<Long> matchids);
	
	
	/**
	 * 
	 * janwen
	 * @return 当前可以竞猜的比赛
	 *
	 */
	public  List<Map> getMatchesInfo();
	/**
	 * 
	 * janwen
	 * @param ids
	 * @return 竞猜比赛,比赛时间在当前时间之后
	 *
	 */
	public Long  isvalidMatch(List<Long> ids);
	
    /**
     * 
     * janwen
     * @param userid
     * @param id_score(
     * @return 添加竞猜数据
     *
     */
	public int addPredict(List<Predict> predicts);
}
