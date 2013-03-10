package com.beike.service.background.goods.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.beike.dao.background.goods.GoodsDao;
import com.beike.entity.background.goods.Goods;
import com.beike.form.background.goods.GoodsForm;
import com.beike.form.background.top.TopForm;
import com.beike.service.background.goods.GoodsService;
import com.beike.util.StringUtils;
/**
 * Title : 	GoodsServiceImpl
 * <p/>
 * Description	:商品服务实现类
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
@Service("bgGoodsService")
public class GoodsServiceImpl implements GoodsService {

	/*
	 * @see com.beike.service.background.goods.GoodsService#addGoods(com.beike.form.background.goods.GoodsForm)
	 */
	public String addGoods(GoodsForm goodsForm) throws Exception {
		String result = null;
		result = goodsDao.addGoods(goodsForm);
		return result;
	}
	
	/*
	 * @see com.beike.service.background.goods.GoodsService#queryGoods(com.beike.form.background.goods.GoodsForm)
	 */
	public List<Goods> queryGoods(GoodsForm goodsForm,int startRow,int pageSize) throws Exception {
		List<Goods> goodsList = null;
		goodsList = goodsDao.queryGoods(goodsForm,startRow,pageSize);
		return goodsList;
	}
	
	/*
	 * @see com.beike.service.background.goods.GoodsService#queryGoodsCount(com.beike.form.background.goods.GoodsForm)
	 */
	public int queryGoodsCount(GoodsForm goodsForm) {
		int count = 0;
		count = goodsDao.queryGoodsCount(goodsForm);
		return count;
	}

	
	/*
	 * @see com.beike.service.background.goods.GoodsService#queryGoodsById(java.lang.String)
	 */
	public Goods queryGoodsById(String goodsId) throws Exception {
		Goods goods = null;
		goods = goodsDao.queryGoodsById(goodsId);
		return goods;
	}
	
	/*
	 * @see com.beike.service.background.goods.GoodsService#editGoods(com.beike.form.background.goods.GoodsForm)
	 */
	public String editGoods(GoodsForm goodsForm) throws Exception {
		String result = null;
		result = goodsDao.editGoods(goodsForm);
		return result;
	}
	
	/*
	 * @see com.beike.service.background.goods.GoodsService#downGoods(com.beike.form.background.goods.GoodsForm)
	 */
	public String downGoods(GoodsForm goodsForm) throws Exception {
		String result = null;
		result = goodsDao.downGoods(goodsForm);
		return result;
	}
	
	/*
	 * @see com.beike.service.background.goods.GoodsService#isTopGoods(com.beike.form.background.top.GoodsForm)
	 */
	public String editGoodsTop(GoodsForm goodsForm) throws Exception {
		String result = null;
		TopForm topForm = null;
		String goodsId = goodsDao.queryGoodsTop(goodsForm);
		if(StringUtils.validNull(goodsId)){
			if(Integer.parseInt(goodsId)<=0){
				result = goodsDao.editGoodsIsTop(goodsForm);
				topForm = new TopForm();
				topForm.setTopOldGoodsId(goodsForm.getGoodsId());
				topForm.setTopNewGoodsId(goodsForm.getGoodsId());
				topForm.setTopStatus("0");
				goodsDao.addGoodsTop(topForm);
			}else{
				GoodsForm form = new GoodsForm();
				form.setGoodsId(Integer.parseInt(goodsId));
				form.setGoodsIsTop("0");
				result = goodsDao.editGoodsIsTop(form);//首先将原先置顶的修改为不置顶
				//boolean flag = goodsDao.queryTopIsExist(goodsId);
				
				topForm = new TopForm();
				if(StringUtils.validNull(result)&&Integer.parseInt(result)>0){
					result = goodsDao.editGoodsIsTop(goodsForm);//置顶当前要置顶的内容
				}
				topForm.setTopOldGoodsId(Integer.parseInt(goodsId));
				topForm.setTopNewGoodsId(goodsForm.getGoodsId());
				topForm.setTopStatus("0");
				//if(flag){
					//result = goodsDao.updateGoodsTop(topForm);
				goodsDao.addGoodsTop(topForm);
				/*}else{
					topForm.setTopOldGoodsId(goodsId);
					topForm.setTopNewGoodsId(goodsForm.getGoodsId());
					topForm.setTopStatus("0");
					result = goodsDao.addGoodsTop(topForm);
				}*/
			}
		}
		return result;
	}
	
	/*
	 * @see com.beike.service.background.goods.GoodsService#addGoodsTop(com.beike.form.background.top.TopForm)
	 */
	public String addGoodsTop(TopForm topForm) throws Exception {
		String result = "";
		result = goodsDao.addGoodsTop(topForm);
		return result;
	}

	/*
	 * @see com.beike.service.background.goods.GoodsService#editGoodsIsTop(com.beike.form.background.goods.GoodsForm)
	 */
	public String editGoodsIsTop(GoodsForm goodsForm) throws Exception {
		String result = "";
		result = goodsDao.editGoodsIsTop(goodsForm);
		return result;
	}

	/*
	 * @see com.beike.service.background.goods.GoodsService#queryTopIsExist(int)
	 */
	public boolean queryTopIsExist(int topOldGoods) throws Exception {
		boolean flag = false;
		flag = goodsDao.queryTopIsExist(topOldGoods);
		return flag;
	}

	/*
	 * @see com.beike.service.background.goods.GoodsService#updateGoodsTop(com.beike.form.background.top.TopForm)
	 */
	public String updateGoodsTop(TopForm topForm) throws Exception {
		String result = "";
		result = goodsDao.updateGoodsTop(topForm);
		return result;
	}
	
	@Resource(name="bgGoodsDao")
	private GoodsDao goodsDao;

	@Resource(name="bgGoodsService")
	private GoodsService goodsService;

}
