<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="page" uri="/WEB-INF/pagetag.tld"%>
<%@ include file="/jsp/base.jsp"%>
<%@page import="com.beike.util.StaticDomain" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>【${merchantForm.merchantname}】现金券,优惠券,团购商品,【${merchantForm.merchantname}】${merchantForm.addr},商铺宝-千品网</title>
<meta name="keywords" content="【${merchantForm.merchantname}】,现金券,优惠券,团购商品" />
<meta name="description" content="【${merchantForm.merchantname}】,千品网商铺宝频道提供【${merchantForm.merchantname}】现金券,优惠券,团购商品,地址,电话信息。进入【${merchantForm.merchantname}】页面，查看更多详情。" />
<link href="<%=contextPath%>/newweb/css_a/base.css" type="text/css" rel="stylesheet" />
<link href="<%=contextPath%>/newweb/css_a/shangpu.css" type="text/css" rel="stylesheet" />

<script src="<%=contextPath%>/newweb/js_a/prototype.js" type="text/javascript"></script>
<script src="<%=contextPath%>/newweb/js_a/scriptaculous.js?load=effects,builder" type="text/javascript"></script>
<script src="<%=contextPath%>/newweb/js_a/lightbox.js" type="text/javascript"></script>
</head>

<body>
<!--    header begin   --> 
<jsp:include page="/newweb/include/min-header.jsp"></jsp:include>
<!--    header end   --> 
<div class="layout">
	<ul class="shangpu_main pt10">
    	<li class="shangpu_left">
        	<dl class="shangpu_big_bor">
            	<dt class="font14px bold">店长推荐</dt>
                <dd>
                	<ul class="dianzhang_tuijian">
                		<c:if test="${STATIC_URL=='false'}">
                    	<li class="tuijian_pic"><a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${topGoods.goodsId}" target="_blank"><img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${topGoods.logo1}" width="288" height="172" alt="" /></a></li>
                        <li class="tuijian_name font14px bold">
                  		<a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${topGoods.goodsId}" target="_blank">${topGoods.goodsname}</a>
						</c:if>
						<c:if test="${STATIC_URL=='true'}">
						<li class="tuijian_pic"><a href="<%=contextPath %>/goods/${topGoods.goodsId}.html" target="_blank"><img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${topGoods.logo1}" width="288" height="172" alt="" /></a></li>
                        <li class="tuijian_name font14px bold">
						<a href="<%=contextPath %>/goods/${topGoods.goodsId}.html" target="_blank">${topGoods.goodsname}</a>
						</c:if>
                        </li>
                        <li class="tuijian_price font333">
                        	<div class="tj_p_1">原价<br /><font class="font999 arial font18px">&yen;${topGoods.sourcePrice}</font></div>
                        	<div class="tj_p_2">折扣<br /><font class="fontred arial font18px">${topGoods.discount}</font></div>
                        	<div class="tj_p_3">节省<br /><font class="fontgreen arial font18px">&yen;${savePrice}</font></div>
                        	<div class="tj_p_4">现价：<font class="fontred arial font30px">&yen;${topGoods.currentPrice}</font></div>
                        	<div class="tj_p_5">返现：<font class="fontred arial">&yen;${topGoods.rebatePrice}</font>　　已有<font class="fontred arial">${SALES_COUNT}</font>人购买　　朝阳区CBD</div>
                            <div class="tj_p_6">
	                        <c:if test="${STATIC_URL=='false'}">
	                  		<a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${topGoods.goodsId}" target="_blank">立即购买</a>
							</c:if>
							<c:if test="${STATIC_URL=='true'}">
							<a href="<%=contextPath%>/goods/${topGoods.goodsId}.html" target="_blank">立即购买</a>
							</c:if>
                            </div>
                        </li>
                        <c:if test="${topGoods.isTop eq '1' && merchantForm.ownercontent ne null && not empty merchantForm.ownercontent}">
                        <li class="dianzhangshuo">
                        <p><font class="fontred">店长说</font>：${merchantForm.ownercontent}</p>
                        </li>
                        </c:if>
                    </ul>
                
                </dd>
            </dl>

        	<dl class="shangpu_big_bor mt10">
            	<dt>
            	<div class="left font14px bold">现金券</div>
            	<div class="right">
            	<c:if test="${STATIC_URL=='false'}">
            	<a href="<%=contextPath%>/shangpubao/shopsbaoGoodsList.do?merchantId=${merchantForm.id}&couponCash=1" target="_blank" class="link_1">更多&gt;</a>
            	</c:if>
                <c:if test="${STATIC_URL=='true'}">
                <a href="<%=contextPath%>/shangpubao/${merchantForm.id}-xjq.html" target="_blank" class="link_1">更多&gt;</a>
                </c:if>
            	</div>
            	</dt>
                <dd>
                	<div class="xianjinquan_cont">
                		<c:if test="${hundredcoupon ne null && hundredcoupon.goodsid ne null}">
						<ul class="xianjinquan_100">
                            <li class="xjq_name font18px">
                            <c:if test="${STATIC_URL=='false'}">
                            <a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${hundredcoupon.goodsid}" target="_blank">${hundredcoupon.goodsname}</a>
                            </c:if>
                            <c:if test="${STATIC_URL=='true'}">
                            <a href="<%=contextPath%>/goods/${hundredcoupon.goodsid}.html" target="_blank">${hundredcoupon.goodsname}</a>
                            </c:if>
                            </li>
                            <li class="xjq_price">现价：<font class="arial font30px fontred">&yen;${hundredcoupon.currentPrice}</font></li>
                            <li class="xjq_fanxian">现在购买返现：<font class="arial fontred font14px">&yen;${hundredcoupon.rebatePrice}</font></li>
                            <li class="xjq_add">${hundredcoupon.mainRegion}(${hundredcoupon.subsetRegion})</li>
                            <li class="xjq_look">
                            <c:if test="${STATIC_URL=='false'}">
                            <a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${hundredcoupon.goodsid}" target="_blank" class="link_1">去看看&gt;</a>
                            </c:if>
                            <c:if test="${STATIC_URL=='true'}">
							<a href="<%=contextPath%>/goods/${hundredcoupon.goodsid}.html" target="_blank" class="link_1">去看看&gt;</a>
                            </c:if>
                            </li>
                        </ul>
                		</c:if>
						<c:if test="${fiftycoupon ne null && fiftycoupon.goodsid ne null}">
                        <ul class="xianjinquan_50">
                            <li class="xjq_name font18px">
                            <c:if test="${STATIC_URL=='false'}">
                            <a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${fiftycoupon.goodsid}" target="_blank">${fiftycoupon.goodsname}</a>
                            </c:if>
                            <c:if test="${STATIC_URL=='true'}">
                            <a href="<%=contextPath%>/goods/${fiftycoupon.goodsid}.html" target="_blank">${fiftycoupon.goodsname}</a>
                            </c:if>
                            </li>
                            <li class="xjq_price">现价：<font class="arial font30px fontred">&yen;${fiftycoupon.currentPrice}</font></li>
                            <li class="xjq_fanxian">现在购买返现：<font class="arial fontred font14px">&yen;${fiftycoupon.rebatePrice}</font></li>
                            <li class="xjq_add">${fiftycoupon.mainRegion}(${fiftycoupon.subsetRegion})</li>
                            <li class="xjq_look">
							<c:if test="${STATIC_URL=='false'}">
                            <a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${fiftycoupon.goodsid}" target="_blank" class="link_1">去看看&gt;</a>
                            </c:if>
                            <c:if test="${STATIC_URL=='true'}">
							<a href="<%=contextPath%>/goods/${fiftycoupon.goodsid}.html" target="_blank" class="link_1">去看看&gt;</a>
                            </c:if>
                            </li>
                        </ul>
                        </c:if>
                        <c:if test="${twentycoupon ne null && twentycoupon.goodsid ne null}">
                        <ul class="xianjinquan_20 xjq_none_b_dot">
                            <li class="xjq_name font18px">
                            <c:if test="${STATIC_URL=='false'}">
                            <a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${twentycoupon.goodsid}" target="_blank">${twentycoupon.goodsname}</a>
                            </c:if>
                            <c:if test="${STATIC_URL=='true'}">
                            <a href="<%=contextPath%>/goods/${twentycoupon.goodsid}.html" target="_blank">${twentycoupon.goodsname}</a>
                            </c:if>
                            </li>
                            <li class="xjq_price">现价：<font class="arial font30px fontred">&yen;${twentycoupon.currentPrice}</font></li>
                            <li class="xjq_fanxian">现在购买返现：<font class="arial fontred font14px">&yen;${twentycoupon.rebatePrice}</font></li>
                            <li class="xjq_add">${twentycoupon.mainRegion}(${twentycoupon.subsetRegion})</li>
                            <li class="xjq_look">
							<c:if test="${STATIC_URL=='false'}">
                            <a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${twentycoupon.goodsid}" target="_blank" class="link_1">去看看&gt;</a>
                            </c:if>
                            <c:if test="${STATIC_URL=='true'}">
							<a href="<%=contextPath%>/goods/${twentycoupon.goodsid}.html" target="_blank" class="link_1">去看看&gt;</a>
                            </c:if>
                            </li>
                        </ul>
                        </c:if>
                	</div>
                </dd>
            </dl>
            
			<c:if test="${SHOP_ENVIROMENT ne null && fn:length(SHOP_ENVIROMENT)>0}">
			<dl class="shangpu_big_bor mt10">
            	<dt class="font14px bold">店面环境</dt>
                <dd class="cont_2">
                	<c:forEach items="${SHOP_ENVIROMENT}" var="baoLogo">
						<ul class="sp_tab_cont">
                    	<li><a href="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${baoLogo[1]}" target="_blank" class="link_1" rel="lightbox[roadtrip]" title="${baoLogo[0]}">
                    	<img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${baoLogo[1]}" width="158" height="102" alt="${baoLogo[0]}" /></a></li>
                        <li class="sp_b_c">${baoLogo[0]}</li>
                    </ul>
                	</c:forEach>
                </dd>
            </dl>
			</c:if>
			
        	<dl class="shangpu_big_bor mt10">
            	<dt>
            	<div class="left font14px bold">在售商品</div>
            	<c:if test="${pager.totalPages>1}">
            	<c:if test="${STATIC_URL=='false'}">
            	<div class="right"><a href="<%=contextPath%>/shangpubao/shopsbaoGoodsList.do?merchantId=${merchantForm.id}" target="_blank" class="link_1">更多&gt;</a></div>
            	</c:if>
	    		<c:if test="${STATIC_URL=='true'}">
	    		<div class="right"><a href="<%=contextPath%>/shangpubao/${merchantForm.id}-shp.html" target="_blank" class="link_1">更多&gt;</a></div>
	    		</c:if>
            	</c:if>
            	</dt>
            	<c:forEach items="${listGoodsForm}" var="goodsForm" varStatus="lf">
            	<c:if test="${(lf.count-1)%3==0}">
            	<dd class="cont_1">
            	</c:if>
					<ul class="shangpu_product_list">
				    <c:if test="${STATIC_URL=='false'}">
		    			<li class="list_photo"><a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${goodsForm.goodsId}" target="_blank"><img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${goodsForm.listlogo}" width="218" height="129" alt="" /></a></li>
		                <li class="list_name"><a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${goodsForm.goodsId}" target="_blank">${goodsForm.goodsname}</a></li>
		    		</c:if>
		    		<c:if test="${STATIC_URL=='true'}">
		    			<li class="list_photo"><a href="<%=contextPath%>/goods/${goodsForm.goodsId}.html" target="_blank"><img src="<%=StaticDomain.getDomain("")%><%=contextPath %>/jsp/uploadimages/${goodsForm.listlogo}" width="218" height="129" alt="" /></a></li>
		                <li class="list_name"><a href="<%=contextPath%>/goods/${goodsForm.goodsId}.html" target="_blank">${goodsForm.goodsname}</a></li>
		    		</c:if>
                        <li class="list_price">
                            <div class="left">现价:<font class="arial font18px fontred">&yen;${goodsForm.currentPrice}</font></div>
                            <div class="right">原价:${goodsForm.sourcePrice}元</div>
                        </li>
                        <li class="list_address">
                            <div class="left">已有${goodsForm.salescount}人购买</div>
                            <div class="address_info">
								<c:set value="${goodsForm.mapRegion[goodsForm.goodsId]}" var="t"></c:set>
			            		<c:choose>
			            			<c:when test="${fn:length(t)==1}">
			           					<c:forEach items="${t}" var="t">
			           						${t}
			           					</c:forEach>
			            			</c:when>
			            			<c:otherwise>
			            				${fn:length(t)}家分店
			            			</c:otherwise>
			            		</c:choose>
							</div>
                        </li>
                    </ul>
				<c:if test="${lf.count%3==0 || lf.count==fn:length(listGoodsForm)}">
            	</dd>
            	</c:if>
            	</c:forEach>
            </dl>
        	<dl class="shangpu_big_bor mt10">
            	<dt class="font14px bold">关于${merchantForm.merchantname}</dt>
                <dd>
                	<div class="guanyu_pic"><img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${merchantForm.logo2}" width="213" height="127" alt="" /></div>
                    <p class="guanyu_info font14px">${merchantForm.merchantintroduction}</p>
                </dd>
            </dl>
        </li>
        <li class="shangpu_right">
   	  		<dl class="shangpu_right_bor">
            	<dt class="font14px bold">分店信息</dt>
                <dd>
					<img src="/newweb/images_a/pic_13.jpg" width="218" height="466" alt="" />
   	  			</dd>
            </dl>
            
            <c:if test="${listCouponForm ne null && fn:length(listCouponForm)>0}">
            <dl class="shangpu_right_bor mt10">
	            <dt>
	            <div class="left font14px bold">优惠券</div>
	            <c:if test="${fn:length(listCouponForm)>6}">
	            <c:if test="${STATIC_URL=='false'}">
	            <div class="right"><a href="<%=contextPath%>/shangpubao/shopsbaoCouponList.do?merchantId=${merchantForm.id}" target="_blank" class="link_1">更多&gt;</a></div>
	            </c:if>
				<c:if test="${STATIC_URL=='true'}">
				<div class="right"><a href="<%=contextPath%>/shangpubao/${merchantForm.id}-yhq.html" target="_blank" class="link_1">更多&gt;</a></div>
				</c:if>
	            </c:if>
	            </dt>
                <dd class="cont_1">
                	<c:forEach items="${listCouponForm}" var="couponForm" varStatus="lf">
                		<c:if test="${lf.count<7}">
                		<div class="sp_youhuiquan">
           				<c:if test="${STATIC_URL=='false'}">
           					<a href="<%=contextPath%>/coupon/getCouponById.do?couponid=${couponForm.couponid}" target="_blank">${couponForm.couponName}</a>
						</c:if>
						<c:if test="${STATIC_URL=='true'}">
							<a href="<%=contextPath%>/coupon/${couponForm.couponid}.html" target="_blank">${couponForm.couponName}</a>
						</c:if>
                		<br/>
                		 下载次数：${couponForm.downcount}</div>
                		</c:if>
                	</c:forEach>
  			  	</dd>
          	</dl>
            </c:if>

            <c:if test="${merchantForm.salescountent ne null && not empty merchantForm.salescountent}">
            <dl class="shangpu_right_bor mt10">
            	<dt class="font14px bold">消费者说</dt>
                <dd class="cont_1">
                	${merchantForm.salescountent}
   	  			</dd>
            </dl>
            </c:if>
        </li>
    </ul>
</div>
<!--    footer begin   --> 
<jsp:include page="/newweb/include/footer.jsp"></jsp:include>
<!--    footer end   -->
</body>
</html>