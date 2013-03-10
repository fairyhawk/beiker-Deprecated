package com.beike.service.background.area.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.dao.background.area.AreaDao;
import com.beike.entity.background.area.Area;
import com.beike.service.background.area.AreaService;
/**
 * Title : 	AreaServiceImpl
 * <p/>
 * Description	:地市服务实现类
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : SinoboGroup.com
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-5-31   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-31  
 */
@Service("areaService")
public class AreaServiceImpl implements AreaService {

	/*
	 * @see com.beike.service.background.area.AreaService#queryArea(com.beike.entity.background.area.Area)
	 */
	public List<Area> queryArea(Area area) throws Exception {
		List<Area> areaList = null;
		areaList = areaDao.queryArea(area);
		return areaList;
	}

	/*
	 * @see com.beike.service.background.area.AreaService#queryAreaMap()
	 */
	public Map<Integer, String> queryAreaMap() throws Exception {
		Map<Integer,String> areaMap = null;
		areaMap = areaDao.queryAreaMap();
		return areaMap;
	}
	
	/** 
	 * @date 2012-5-18
	 * @description:查询所有上线城市信息
	 * @return
	 * @throws Exception List<Area>
	 * @throws 
	 */
	public List<Area> queryOnlineArea() throws Exception{
		return areaDao.getOnlineArea();
	}
	@Resource(name = "areaDao")
	private AreaDao areaDao;

}
