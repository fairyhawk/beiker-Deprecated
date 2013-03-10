package com.beike.dao.background.tag;

import java.util.List;
import java.util.Map;

import com.beike.dao.GenericDao;
import com.beike.entity.background.tag.Tag;
import com.beike.form.background.tag.TagForm;
/**
 * 
 * Title : 	TagDao
 * <p/>
 * Description	: 标签信息访问数据接口
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
 * @version 1.0.0.2011-06-03
 */
public interface TagDao extends GenericDao<Tag,Long> {

	/**
	 * Description : 查询分类标签
	 * @param tagForm
	 * @return java.util.List<Tag>
	 * @throws Exception
	 */
	public List<Tag> queryTag(TagForm tagForm) throws Exception;
	

	/**
	 * Description : 查询类型标签，以key-value形式保存在Map中
	 * @return
	 * @throws Exception
	 */
	public Map<Integer,String> queryTagMap() throws Exception ;
	
}
