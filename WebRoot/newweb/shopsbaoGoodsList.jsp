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
<script type="text/javascript" src="<%=contextPath%>/newweb/js_a/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="<%=contextPath%>/newweb/js_a/global.js"></script>
<script type="text/javascript">
	function gopage(page){
		var cpage=document.getElementById("cpage");
		cpage.value=page;
		document.getElementById("searchForm").submit();
	}
</script>
</head>

<body>
<!--    header begin   --> 
<jsp:include page="/newweb/include/min-header.jsp"></jsp:include>
<!--    header end   --> 
<div class="layout">
	<div class="shangpu_list_nav">
	<c:if test="${STATIC_URL=='false'}">
	<a href="<%=contextPath%>/brand/showMerchant.do?merchantId=${merchantForm.id}" class="link_1">店铺首页</a>
	</c:if>
	<c:if test="${STATIC_URL=='true'}">
	<a href="<%=contextPath%>/shangpubao/${merchantForm.id}.html" class="link_1">店铺首页</a>
	</c:if>
	 &gt; 商品列表</div>
    <ul class="shangpu_sp_list">
    <c:forEach items="${listGoodsForm}" var="goodsForm" varStatus="lf">
    	<c:if test="${(lf.count-1)%4==0}">
    		<li class="list_cont">
    	</c:if>
    	
    	<c:if test="${lf.count%4==0}">
    		<ul class="shangpu_product_list none_margin">
    	</c:if>
    	<c:if test="${lf.count%4!=0}">
    		<ul class="shangpu_product_list">
    	</c:if>
    		<c:if test="${STATIC_URL=='false'}">
    			<li class="list_photo"><a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${goodsForm.goodsId}" target="_blank"><img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${goodsForm.listlogo}" width="218" height="129" alt="" /></a></li>
                <li class="list_name"><a href="<%=contextPath%>/goods/showGoodDetail.do?goodId=${goodsForm.goodsId}" target="_blank">${goodsForm.goodsname}</a></li>
    		</c:if>
    		<c:if test="${STATIC_URL=='true'}">
    			<li class="list_photo"><a href="<%=contextPath%>/goods/${goodsForm.goodsId}.html" target="_blank"><img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${goodsForm.listlogo}" width="218" height="129" alt="" /></a></li>
                <li class="list_name"><a href="<%=contextPath%>/goods/${goodsForm.goodsId}.html" target="_blank">${goodsForm.goodsname}</a></li>
    		</c:if>
                <li class="list_price">
                    <div class="left">现价:<font class="arial font18px fontred">&yen;${goodsForm.currentPrice}</font></div>
                    <div class="right">原价:${goodsForm.sourcePrice}元</div>
                </li>
                <li class="list_address">
                    <div class="left">${goodsForm.salescount}人购买</div>
                    <div class="address_info">
						<c:set value="${goodsForm.mapRegion[goodsForm.goodsId]}" var="t"></c:set>
	            		<c:choose>
	            			<c:when test="${fn:length(t)==0 || fn:length(t)==1}">
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
    	<c:if test="${lf.count%4==0 || lf.count==fn:length(listGoodsForm)}">
    	</li>
    	</c:if>
    </c:forEach>
	    <li class="shangpu_page">
	    <page:page pager="${pager}"/>
	    </li>
    </ul>

    <form id="searchForm" action="<%=contextPath %>/shangpubao/shopsbaoGoodsList.do" method="post">
    	<input type="hidden" name="merchantId" value="${merchantForm.id}"/>
    	<input type="hidden" name="cpage" value="${pager.currentPage}" id="cpage"/>
    	<input type="hidden" name="couponCash" value="${couponCash}"/>
    </form>
</div>
<!--    footer begin   --> 
<jsp:include page="/newweb/include/footer.jsp"></jsp:include>
<!--    footer end   -->
</body>
</html>