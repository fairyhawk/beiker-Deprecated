package com.beike.service.operation.ouzhoubei;

import java.util.List;
import java.util.Map;

import com.beike.entity.operation.ouzhoubei.MatchInfo;
import com.beike.entity.operation.ouzhoubei.Predict;

public interface EuroCupService {

	
	/**
	 * 
	 * janwen
	 * @return 当前可以竞猜的比赛
	 *
	 */
	public  List<MatchInfo> getMatchesInfo();
	
	//是否参加
	public Long getMatchesByUserid(Long userid,List<Long> matchids);
	
	//是否合法比赛
	public Long  isvalidMatch(List<Long> ids);
	/**
	 * 
	 * janwen
	 * @param userid
	 * @return 用户参与过的预测
	 *
	 */
	public List<Predict> getUserPredicts(Long userid) ;
	
	/**
	 * 保存竞猜结果
	 * janwen
	 * @param userid
	 * @param predicts
	 * @return
	 *
	 */
	public int addPredict(List<Predict> predicts);
}
