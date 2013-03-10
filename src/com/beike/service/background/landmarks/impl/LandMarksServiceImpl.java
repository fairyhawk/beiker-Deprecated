package com.beike.service.background.landmarks.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.dao.background.landmarks.LandMarksDao;
import com.beike.entity.background.landmarks.LandMarks;
import com.beike.form.background.landmarks.LandMarksForm;
import com.beike.service.background.landmarks.LandMarksService;
/**
 * Title : 	LandMarksServiceImpl
 * <p/>
 * Description	:地标服务实现类
 * <p/>
 * CopyRight : CopyRight (c) 2011
 * </P>
 * Company : Sinobo
 * </P>
 * JDK Version Used	: JDK 5.0 +
 * <p/>
 * Modification History		:
 * <p/>
 * <pre>NO.    Date    Modified By    Why & What is modified</pre>
 * <pre>1     2011-06-08   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-08  
 */
@Service("landMarksService")
public class LandMarksServiceImpl implements LandMarksService {
	
	/*
	 * @see com.beike.service.background.landmarks.LandMarksService#queryLandMarks()
	 */
	public List<LandMarks> queryLandMarks(LandMarksForm landMarksForm) throws Exception {
		List<LandMarks> landMarks = null;
		landMarks = landMarksDao.queryLandMarks(landMarksForm);
		return landMarks;
	}
	
	@Resource(name="landMarksDao")
	private LandMarksDao landMarksDao;

}
