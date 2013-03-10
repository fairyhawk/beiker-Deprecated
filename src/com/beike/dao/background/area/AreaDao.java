package com.beike.dao.background.area;

import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.entity.background.area.Area;

/**
 * 
 * Title : 	AreaDao
 * <p/>
 * Description	: 地市访问数据接口
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
 * <pre>1     2011-5-31    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-5-31
 */
public interface AreaDao extends GenericDao<Area,Long> {

	/**
	 * Description : 根据条件查询地市级联关系
	 * @param area
	 * @return
	 * @throws Exception
	 */
	public List<Area> queryArea(Area area) throws Exception;
	
	/**
	 * Description : 查询地市，以K，V形式保存
	 * @return
	 * @throws Exception
	 */
	public Map<Integer,String> queryAreaMap() throws Exception;
	
	/**
	 * 查询上线城市列表
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> queryOnlineArea() throws Exception;
	
	/** 
	 * @date 2012-5-18
	 * @description:查询所有上线城市信息
	 * @return
	 * @throws Exception List<Area>
	 * @throws 
	 */
	public List<Area> getOnlineArea() throws Exception;
}
