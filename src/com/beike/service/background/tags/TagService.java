package com.beike.service.background.tags;

import java.util.List;
import java.util.Map;

import com.beike.entity.background.tag.Tag;
import com.beike.form.background.tag.TagForm;

/**
 * Title : 	TagService
 * <p/>
 * Description	:标签信息服务接口类
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
 * <pre>1     2011-06-03    lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-14  
 */
public interface TagService {

	/**
	 * Description : 查询分类标签
	 * @param tagForm
	 * @return java.util.List<Tag>
	 * @throws Exception
	 */
	public List<Tag> queryTag(TagForm tagForm) throws Exception;
	
	/**
	 * Description : 查询类型标签，以key-value形式保存在Map中 改方法将来需要修改，涉及到城市多的话，
	 * 就需要将城市id带过来，然后查询该城市下地标；目前没有修改的原因是因为表中没有地区字段
	 * @return
	 * @throws Exception
	 */
	public Map<Integer,String> queryTagMap() throws Exception ;
	
	
	
}
