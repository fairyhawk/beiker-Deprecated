package com.beike.service.operation.ouzhoubei;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beike.dao.operation.ouzhoubei.EuroCupDao;
import com.beike.entity.operation.ouzhoubei.MatchInfo;
import com.beike.entity.operation.ouzhoubei.Predict;

@Service("eurocupService")
public class EuroCupServiceImpl implements EuroCupService {

	@Autowired
	private EuroCupDao cupDao;
	@Override
	public List<MatchInfo> getMatchesInfo() {
		 List<Map> matchesinfo = cupDao.getMatchesInfo();
		 List<MatchInfo> return_matches_info = new ArrayList<MatchInfo>();
		 for(int i=0;i<matchesinfo.size();i++){
			 Map map = matchesinfo.get(i);
			 MatchInfo match = new MatchInfo();
			 match.setId((Long)map.get("id"));
			 match.setMatchteams(((String)map.get("matchteams")).trim().toLowerCase());
			 match.setMatchtime((Timestamp) map.get("match_time"));
			 return_matches_info.add(match);
		 }
		 return return_matches_info;
	}
	@Override
	public List<Predict> getUserPredicts(Long userid) {
		List<Map> matches = cupDao.getUserPredicts(userid);
		List<Predict> predictedMatches = new ArrayList<Predict>();
		for(int i=0;i<matches.size();i++){
			Predict predict = new Predict();
			Map map = matches.get(i);
			predict.setMatchteams((String)map.get("matchteams"));
			predict.setPredict_score((String)map.get("predict_score"));
			predictedMatches.add(predict);
		}
		return predictedMatches;
	}
	@Override
	public Long getMatchesByUserid(Long userid, List<Long> matchids) {
		return cupDao.getMatchesByUserid(userid, matchids);
	}
	@Override
	public Long isvalidMatch(List<Long> ids) {
		return cupDao.isvalidMatch(ids);
	}
	@Override
	public int addPredict( List<Predict> predicts) {
		return cupDao.addPredict(predicts);
	}

}
