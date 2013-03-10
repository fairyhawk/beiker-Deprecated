package com.beike.wap.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.wap.dao.MTagDao;
import com.beike.wap.entity.MTag;
import com.beike.wap.service.MTagService;
/**
 * Title : MTagServiceImpl
 * <p/>
 * Description :分类信息服务实现类
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
 * <pre>1     2011-10-17   lvjx			Created
 * 
 * <pre>
 * <p/>
 *
 * @author  lvjx
 * @version 1.0.0.2011-10-17
 */
@Service("wapTagService")
public class MTagServiceImpl implements MTagService {

	/*
	 * @see com.beike.wap.service.tag.MTagService#queryTagByParendId(int)
	 */
	@Override
	public List<MTag> queryTagByParendId(int parentId) throws Exception {
		List<MTag> tagList = null;
		tagList = tagDao.queryTagByParendId(parentId);
		return tagList;
	}
	
	/*
	 * @see com.beike.wap.service.tag.MTagService#queryTagByParentIdInfo(int)
	 */
	@Override
	public Map<String, String> queryTagByParentIdInfo(int parentId)
			throws Exception {
		Map<String,String> tagMap = null;
		tagMap = tagDao.queryTagByParentIdInfo(parentId);
		return tagMap;
	}

	/*
	 * @see com.beike.service.GenericService#findById(java.io.Serializable)
	 */
	@Override
	public MTag findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Resource(name = "wapTagDao")
	private MTagDao tagDao;


}
