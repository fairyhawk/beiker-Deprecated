package com.beike.wap.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.wap.dao.MRegionDao;
import com.beike.wap.entity.MRegion;
import com.beike.wap.service.MRegionService;

/**
 * Title : MRegionServiceImpl
 * <p/>
 * Description :热门地标服务实现类
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : qianpin.com </P> JDK Version Used : JDK 5.0 +
 * <p/>
 * Modification History :
 * <p/>
 * 
 * <pre>
 * NO.    Date    Modified By    Why & What is modified
 * </pre>
 * 
 * <pre>1     2011-10-10   lvjx			Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-10-10
 */
@Service("wapRegionService")
public class MRegionServiceImpl implements MRegionService {

	/*
	 * @see
	 * com.beike.wap.service.region.MRegionService#addWapHotRegion(java.util
	 * .List)
	 */
	@Override
	public int addWapHotRegion(final List<MRegion> wapRegionList)
			throws Exception {
		int flag = 0;
		flag = regionDao.addWapHotRegion(wapRegionList);
		return flag;
	}

	/*
	 * @see
	 * com.beike.wap.service.region.MRegionService#queryWapHotRegion(java.util
	 * .Date, java.lang.String)
	 */
	@Override
	public int queryWapHotRegion(Date currentDate, String typeArea)
			throws Exception {
		int result = 0;
		result = regionDao.queryWapHotRegion(currentDate, typeArea);
		return result;
	}

	/*
	 * @see
	 * com.beike.wap.service.region.MRegionService#queryWapHotRegionData(java
	 * .util.Date, java.lang.String)
	 */
	@Override
	public List<MRegion> queryWapHotRegionData(Date currentDate, String typeArea)
			throws Exception {
		List<MRegion> regionList = null;
		regionList = regionDao.queryWapHotRegionData(currentDate, typeArea);
		return regionList;
	}
	
	/*
	 * @see com.beike.wap.service.region.MRegionService#queryRegion(java.lang.String)
	 */
	@Override
	public List<MRegion> queryRegion(String regionId,String areaId) throws Exception {
		List<MRegion> regionList = null;
		regionList = regionDao.queryRegion(regionId,areaId);
		return regionList;
	}
	
	/*
	 * @see com.beike.wap.service.region.MRegionService#queryRegionInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, String> queryRegionInfo(String regionId, String areaId)
			throws Exception {
		Map<String,String> regionMap = regionDao.queryRegionInfo(regionId, areaId);
		return regionMap;
	}

	/*
	 * @see com.beike.wap.service.MRegionService#queryRegionParentId(java.lang.String)
	 */
	@Override
	public int queryRegionParentId(String regionId) throws Exception {
		int parentId = 0;
		parentId = regionDao.queryRegionParentId(regionId);
		return parentId;
	}
	
	/*
	 * @see com.beike.wap.service.MRegionService#queryMaxDate(java.lang.String)
	 */
	@Override
	public Date queryMaxDate(String cityName) throws Exception {
		Date maxDate = null;
		maxDate = regionDao.queryMaxDate(cityName);
		return maxDate;
	}

	
	@Override
	public MRegion findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Resource(name = "wapRegionDao")
	private MRegionDao regionDao;

	/*
	 * @see com.beike.wap.service.MRegionService#queryHotRegionData(java.sql.Date, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<MRegion> queryHotRegionData(java.sql.Date nowDate,
			String regionArea, String hotRegionEnName, int areaId)
			throws Exception {
		List<MRegion> regionList = null;
		regionList = regionDao.queryHotRegionData(nowDate, regionArea, hotRegionEnName, areaId);
		return regionList;
	}

	/*
	 * @see com.beike.wap.service.MRegionService#queryAreaId(java.lang.String)
	 */
	@Override
	public int queryAreaId(String areaEnName) throws Exception {
		int areaId = 0;
		areaId = regionDao.queryAreaId(areaEnName);
		return areaId;
	}



}
