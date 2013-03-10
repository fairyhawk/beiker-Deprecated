package com.beike.wap.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.wap.dao.MWapTypeDao;
import com.beike.wap.entity.MWapType;
import com.beike.wap.service.MWapTypeService;

/**
 * Title : GoodsServiceImpl
 * <p/>
 * Description :商品服务实现类
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
 * <pre>1     2011-09-20   lvjx			Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-09-20
 */
@Service("wapTypeService")
public class MWapTypeServiceImpl implements MWapTypeService {

	/*
	 * @see com.beike.wap.service.WapTypeService#addWapType(java.util.List)
	 */
	@Override
	public int addWapType(final List<MWapType> wapTypeList) throws Exception {
		int flag = 0;
		flag = wapTypeDao.addWapType(wapTypeList);
		return flag;
	}

	/*
	 * @see com.beike.wap.service.WapTypeService#queryWapType(int, int,
	 * java.util.Date)
	 */
	@Override
	public int queryWapType(int typeType, int typePage, Date currentDate,
			String typeArea) throws Exception {
		int sum = 0;
		sum = wapTypeDao
				.queryWapType(typeType, typePage, currentDate, typeArea);
		return sum;
	}

	@Override
	public MWapType findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Resource(name = "wapTypeDao")
	public MWapTypeDao wapTypeDao;

}
