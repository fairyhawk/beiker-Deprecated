package com.beike.service.background.landmarks;

import java.util.List;

import com.beike.entity.background.landmarks.LandMarks;
import com.beike.form.background.landmarks.LandMarksForm;

/**
 * Title : 	LandMarksService
 * <p/>
 * Description	: 地标服务接口类
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
 * <pre>1     2011-06-08    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-08  
 */
public interface LandMarksService {
	
	/**
	 * Description : 查询地标
	 * @return
	 * @throws Exception
	 */
	public List<LandMarks> queryLandMarks(LandMarksForm landMarksForm) throws Exception;

}
