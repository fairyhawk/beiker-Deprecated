package com.beike.service.background.goods;

import java.util.List;

import com.beike.entity.background.goods.Goods;
import com.beike.form.background.goods.GoodsForm;
import com.beike.form.background.top.TopForm;


/**
 * Title : 	GoodsService
 * <p/>
 * Description	:商品信息服务接口类
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
public interface GoodsService {
	
	/**
	 * Description : 新增商品
	 * @param goodsForm
	 * @return
	 * @throws Exception
	 */
	public String addGoods(GoodsForm goodsForm) throws Exception;
	
	/**
	 * Description : 查询商品
	 * @param goodsForm
	 * @param startRow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public List<Goods> queryGoods(GoodsForm goodsForm,int startRow,int pageSize) throws Exception;
	
	/**
	 * Description : 查询商品数量
	 * @param goodsForm
	 * @return
	 */
	public int queryGoodsCount(GoodsForm goodsForm);
	
	/**
	 * Description : 根据商品id查询商品
	 * @param goodsId
	 * @return
	 * @throws Exception
	 */
	public Goods queryGoodsById(String goodsId) throws Exception;
	
	/**
	 * Description : 修改商品
	 * @param goodsForm
	 * @return
	 * @throws Exception
	 */
	public String editGoods(GoodsForm goodsForm) throws Exception;
	
	/**
	 * Description : 下架商品
	 * @param goodsForm
	 * @return
	 * @throws Exception
	 */
	public String downGoods(GoodsForm goodsForm) throws Exception;
	
	
	
	/**
	 * Description : 置顶
	 * @param goodsForm
	 * @return
	 * @throws Exception
	 */
	public String editGoodsTop(GoodsForm goodsForm) throws Exception;
	
	/**
	 * Description : 修改商品置顶
	 * @param goodsForm
	 * @return
	 * @throws Exception
	 */
	public String editGoodsIsTop(GoodsForm goodsForm) throws Exception ;
	
	/**
	 * @param topForm
	 * @return
	 * @throws Exception
	 */
	public String addGoodsTop(TopForm topForm) throws Exception;
	
	/**
	 * Description : 修改top置顶
	 * @param topForm
	 * @return
	 * @throws Exception
	 */
	public String updateGoodsTop(TopForm topForm) throws Exception;
	
	/**
	 * Description : 查看置顶项是否存在
	 * @param topForm
	 * @return
	 * @throws Exception
	 */
	public boolean queryTopIsExist(int topOldGoods) throws Exception;
}
