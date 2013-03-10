<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.beike.util.StaticDomain" %>
<%@ include file="/jsp/base.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<div class="min_header">
  <dl>
    <dt class="left"><a href="#"><img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/newweb/images_a/qp_min_logo.jpg" width="60" height="29" /></a></dt>
  	<c:if test="${QIANPIN_USER ne null}">
  	<dd class="min_h_user">您好，<span class="fontccc">${QIANPIN_USER.email}!</span>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="<%=contextPath%>/user/logout.do">退出</a></dd>
    </c:if>
    <c:if test="${QIANPIN_USER eq null}">
    <dd class="min_h_user"><a href="<%=contextPath%>/forward.do?param=login">登录</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="<%=contextPath%>/forward.do?param=regist">注册</a></dd>
    </c:if>
    
    <dd class="min_h_nav"><ul class="header_nav">
        <li><a href="<%=contextPath%>/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNUSEED">我的千品</a></li>
        <li class="header_shopcart2"><a href="<%=contextPath %>/shopcart/shopcart.do?command=queryShopCart">购物车<span>${shopcartSummary.totalProduct}<b></b></span>件</a></li>
        <li><a href="#">帮助中心</a></li>
        <li class="topnav_wrap" id="topnavWrap"><a href="#"  id="topNav" style="background-position:54px -340px">网站导航</a>
        <p>
      	<c:if test="${STATIC_URL=='false'}">
      	<a href="<%=contextPath%>/forward.do?param=index.brand_zone">品牌专区</a>
      	<a href="<%=contextPath%>/forward.do?param=index.food">美食</a>
      	<a href="<%=contextPath%>/forward.do?param=index.entertainment">休闲娱乐</a>
      	<a href="<%=contextPath%>/forward.do?param=index.beauty">丽人</a>
      	<a href="<%=contextPath%>/forward.do?param=index.life">生活服务</a>
      	</c:if>
      	<c:if test="${STATIC_URL=='true'}">
      	<a href="<%=contextPath %>/ppzq.html">品牌专区</a>
      	<a href="<%=contextPath%>/ms.html">美食</a>
      	<a href="<%=contextPath%>/xxyl.html">休闲娱乐</a>
      	<a href="<%=contextPath%>/lr.html">丽人</a>
      	<a href="<%=contextPath%>/shfw.html">生活服务</a>
      	</c:if>
      	</p>
        </li>
      </ul>
    </dd>
  </dl>
</div>
<div class="sp_header">
	<div class="sp_header_con">
    <dl class="sp_header_l">
      <dt><img src="<%=StaticDomain.getDomain("")%><%=contextPath%>/jsp/uploadimages/${merchantForm.logo1}"  width="60" height="60" /></dt>
      <dd><h3>${merchantForm.merchantname}</h3></dd>
      <dd>累计销售：<span>${merchantForm.salescount+merchantForm.virtualCount}</span>&nbsp;&nbsp;&nbsp;&nbsp;评价：<span>${merchantForm.avgscores}</span></dd>
    </dl>
    <dl class="sp_header_r">
      <dt><form>
      	<input type="text" class="sp_search" /><input type="submit" value="" class="sp_search_btn" />
      </form></dt>
      <dd>
      <!-- <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/top_hotsearch.html"></jsp:include> -->
      <span>热门：</span><a href="#">国贸</a><a href="#">双井</a><a href="#">中关村</a><a href="#">上地</a><a href="#">前门</a><a href="#">亚运村</a><a href="#">牛街</a><a href="#">广安门</a>
      </dd>
    </dl>
  </div>
</div>