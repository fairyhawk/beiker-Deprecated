package com.beike.action.operation.ouzhoubei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beike.action.user.BaseUserAction;
import com.beike.entity.operation.ouzhoubei.Predict;
import com.beike.entity.user.User;
import com.beike.service.operation.ouzhoubei.EuroCupService;
import com.beike.util.ipparser.CityUtils;
import com.beike.util.json.JSONArray;
import com.beike.util.json.JSONException;
import com.beike.util.json.JSONObject;
import com.beike.util.singletonlogin.SingletonLoginUtils;

@Controller
public class EuroCupAction extends BaseUserAction {

	static final Log logger = LogFactory.getLog(EuroCupAction.class);
	static final String regex = "^[1-9]{1,2}$|^0$";
	
	@Autowired
	private EuroCupService cupService;
	@RequestMapping("/huodong/ouzhoubei.do")
	public String gotoPredict(HttpServletRequest request,
			HttpServletResponse response) throws JSONException {
		super.setCookieUrl(request, response);
		User user = SingletonLoginUtils.getMemcacheUser(request);
		String predict_json = request.getParameter("predicts");
		
		
		try {
			boolean islogin = false;
			if(user != null){
				String error_message = handlRequest(predict_json, user.getId());
				islogin = true;
				request.setAttribute("message", error_message);
				request.setAttribute("islogin", "yes");
				request.setAttribute("mypredicts", cupService.getUserPredicts(user.getId()));
			}else if(user == null && predict_json  != null && !"".equals(predict_json)){
				return "redirect:/forward.do?param=login";
			}
			String city = CityUtils.getCity(request, response);
			request.setAttribute("matchesinfo", cupService.getMatchesInfo());
			return "../huodong/ozhoubei/" + city;
		} catch (Exception e) {
			return "redirect:../500.html";
		}
	}
	
	
	private String handlRequest(String predict_json,Long userid) throws JSONException{
		List<Predict> predicts = new ArrayList<Predict>();
		List<Long> match_id = new ArrayList<Long>();
		Map<String,String> id_score_map = new HashMap<String, String>();
		String error_message = null;
		JSONObject  json = null;
		boolean score_valid = true;
		if(predict_json != null && !"".equals(predict_json)){
			json = new JSONObject(predict_json);
			JSONArray array = json.getJSONArray("predicts");
			for(int i=0;i<array.length();i++){
				JSONObject o = array.getJSONObject(i);
				if(id_score_map.get(o.get("id").toString()) != null){
					if(Pattern.matches(regex,o.get("predict_score").toString())){
						id_score_map.put(o.get("id").toString(), id_score_map.get(o.get("id").toString())+ ":" + o.get("predict_score").toString());
						Predict p = new Predict();
						p.setUserid(userid);
						p.setMatch_id(o.getLong("id"));
						p.setPredict_score(id_score_map.get(o.get("id").toString()));
						predicts.add(p);
					}else{
						score_valid = false;
						error_message = "黑客吧,这数据都放进来了";
						break;
					}
				}else{
					if(Pattern.matches(regex,o.get("predict_score").toString())){
						id_score_map.put(o.get("id").toString(),o.get("predict_score").toString());
						match_id.add(o.getLong("id"));
					}else{
						score_valid = false;
						error_message = "黑客吧,这数据都放进来了";
						break;
					}
				}
			}
		}
		if(json != null && score_valid && match_id.size() > 0){
			//验证是否参加过
			if(cupService.getMatchesByUserid(userid, match_id) > 0){
				error_message = "亲啊,你已经参与过本次竞猜了,等下一场吧";
			}else if(cupService.isvalidMatch(match_id) != match_id.size()){
				error_message = "亲啊,比赛都开始了,回家看球去吧,等下场比赛再来参与吧";
			}else{
				int results = cupService.addPredict(predicts);
				if(results ==  predicts.size()){
					error_message = "亲,竞猜成功,别忘了24小时之内在千品网下单啊";
				}
			}
		}
		
	//	error_message = "亲啊,你是怎么操作的,怎么会跑到这来了";
		return error_message;
	}
	
}
