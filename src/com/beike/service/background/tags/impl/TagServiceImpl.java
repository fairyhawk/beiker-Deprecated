package com.beike.service.background.tags.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.dao.background.tag.TagDao;
import com.beike.entity.background.tag.Tag;
import com.beike.form.background.tag.TagForm;
import com.beike.service.background.tags.TagService;
/**
 * Title : 	TagServiceImpl
 * <p/>
 * Description	:标签服务实现类
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
 * <pre>1     2011-06-03   lvjx			Created<pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-06-14  
 */
@Service("tagService")
public class TagServiceImpl implements TagService {

	/*
	 * @see com.beike.service.background.tags.TagService#queryTag(com.beike.form.background.tag.TagForm)
	 */
	public List<Tag> queryTag(TagForm tagForm) throws Exception {
		List<Tag> tagList = null;
		tagList = tagDao.queryTag(tagForm);
		return tagList;
	}
	
	/*
	 * @see com.beike.service.background.tags.TagService#queryTagMap()
	 */
	public Map<Integer, String> queryTagMap() throws Exception {
		Map<Integer,String> tagMap = null;
		tagMap = tagDao.queryTagMap();
		return tagMap;
	}
	
	@Resource(name="tagDao")
	private TagDao tagDao;

	
}
