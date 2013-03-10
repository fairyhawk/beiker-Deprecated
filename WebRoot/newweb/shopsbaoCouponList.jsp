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
	<div class="shangpu_list_nav">${merchantForm.merchantname} &gt; 全部优惠券</div>
    <div class="shangpu_yhq_list">
        <c:forEach items="${listCouponForm}" var="couponForm" varStatus="lf">
    	<c:if test="${lf.count%4==0}">
    		<dl class="sp_yhq_list_cont none_margin">
    	</c:if>
    	<c:if test="${lf.count%4!=0}">
    		<dl class="sp_yhq_list_cont">
    	</c:if>
			<dt class="bold">
			<c:if test="${STATIC_URL=='false'}">
			<a href="<%=contextPath%>/coupon/getCouponById.do?couponid=${couponForm.couponid}" target="_blank">${couponForm.couponName}</a>
			</c:if>
			<c:if test="${STATIC_URL=='true'}">
			<a href="<%=contextPath%>/coupon/${couponForm.couponid}.html" target="_blank">${couponForm.couponName}</a>
			</c:if>
			</dt>
            <dd>
                <div class="title">
                <c:if test="${STATIC_URL=='false'}">
                <a href="<%=contextPath%>/coupon/getCouponById.do?couponid=${couponForm.couponid}" target="_blank">
                </c:if>
                <c:if test="${STATIC_URL=='true'}">
                <a href="<%=contextPath%>/coupon/${couponForm.couponid}.html" target="_blank">
                </c:if>
                <img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${couponForm.couponlogo}" width="215" height="128" alt=""/>
                </a>
                </div>
            	<ul>
                    <li class="yhq_time">有效时间：<font class="fontred">${couponForm.endDate}</font></li>
                    <li class="yhq_down"><font class="left">下载次数：${couponForm.downcount}</font>
                    <div class="yhq_address_info">
						<c:set value="${couponForm.mapRegion[couponForm.couponid]}" var="t"></c:set>
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
            </dd>
    	</dl>
    	</c:forEach>   
    </div>
	<page:page pager="${pager}"/>
    <form id="searchForm" action="<%=contextPath%>/shangpubao/shopsbaoCouponList.do" method="post">
    	<input type="hidden" name="merchantId" value="${merchantForm.id}"/>
    	<input type="hidden" name="cpage" value="${pager.currentPage}" id="cpage"/>
    </form>
</div>
<!--    footer begin   --> 
<jsp:include page="/newweb/include/footer.jsp"></jsp:include>
<!--    footer end   -->
</body>
</html>
