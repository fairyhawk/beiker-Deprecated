package com.beike.service.miaosha;

import java.util.List;
import java.util.Map;

import com.beike.entity.miaosha.MiaoSha;
import com.beike.page.Pager;
import com.beike.service.GenericService;

/**      
 * project:beiker  
 * Title:秒杀Service
 * Description:
 * Copyright:Copyright (c) 2012
 * Company:Sinobo
 * @author qiaowb  
 * @date Jul 31, 2012 3:15:24 PM     
 * @version 1.0
 */
public interface MiaoShaService extends GenericService<MiaoSha, Long> {
	/**
	 * 通过秒杀ID获取秒杀信息
	 * @param msId
	 * @return
	 */
	public MiaoSha getMiaoShaById(Long msId);
	
	/**
	 * 秒杀右侧秒杀进行时数据
	 * @param areaId
	 * @param count
	 * @return
	 */
	public List<MiaoSha> getMiaoShaListByAreaId(Long areaId, int count);
	
	/**
	 * 获取城市秒杀数量
	 * @param areaId
	 * @param status
	 * @return
	 */
	public int getMiaoShaCount(Long areaId, int status);
	
	/**
	 * 获取城市分页秒杀集合
	 * @param areaId
	 * @param status
	 * @param pager
	 * @return
	 */
	public List<Long> getMiaoShaIdsByPage(Long areaId, int status, Pager pager);
	
	/**
	 * 通过秒杀集合获取秒杀集合信息
	 * @param lstMsIds
	 * @return
	 */
	public List<MiaoSha> getMiaoShaListByIds(List<Long> lstMsIds);
	
	/**
	 * 查询首页显示中的秒杀
	 * @param areaId
	 * @return
	 */
	public List<Long> getIndexMiaoShaByCityId(Long areaId);
	
	/**
	 * 获取即将开始的秒杀，短信通知使用
	 * @return
	 */
	public List<Map<String,Object>> getNextBeginMiaoShaIDs(String timeS, String timeE);
}
