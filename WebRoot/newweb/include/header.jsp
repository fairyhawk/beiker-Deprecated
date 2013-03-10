<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.beike.util.StaticDomain" %>
<%@ include file="/jsp/base.jsp"%>
<%
	String qian_pin_city_request = (String)request.getAttribute(CityUtils.CITY_COOKIENAME);
	if(qian_pin_city_request==null||qian_pin_city_request.trim().equals("")){
		qian_pin_city_request = (String)session.getAttribute(CityUtils.CITY_COOKIENAME);
	}
	if(qian_pin_city_request==null){
		qian_pin_city_request = "";
	}
%>
<%
	List<RegionCatlog> listRegion=regionMap.get(0L);
	int regionSize=listRegion.size();
%>
<!-- 获取用户、购物车信息 -->
<script type="text/javascript">
$(function(){
	$("#userlongin_dd").load("<%=contextPath%>/header/getUserLoginInfoDiv.do");
})
</script>
<div class="header">
  <div class="header_con">
    <dl class="header_left">
      <dt class="left"><a href="/"><img src="images_a/qianpin_logo.jpg" width="100" height="50" /></a></dt>
      <dd id="headerCityIcon" class="cbg_shanghai">
        <h3 id="currentCity"></h3>
        <p onclick="blockUIOpen('#chooseCity')">切换城市</p>
      </dd>
      <dd class="pt30 pl10" id="userlongin_dd"></dd>
    </dl>
    <ul class="header_nav">
      <li><a id="myqianpin" href="/">我的千品</a></li>
      <li class="header_shopcart"><a href="<%=contextPath %>/shopcart/shopcart.do?command=queryShopCart">购物车<span id="shopcartcount">0</span>件</a></li>
      <li><a href="#">帮助中心</a></li>
	  <li class="topnav_wrap" id="topnavWrap"><a href="#"  id="topNav">网站导航</a>
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
  </div>
  <div class="navigation">
    <ul>
      <li class="nav_list left">全部商品分类</li>
      <li class="nav_a left">
      <c:if test="${STATIC_URL=='false'}">
       	<a href="">首页</a>
      	<a href="<%=contextPath%>/forward.do?param=index.brand_zone">品牌专区</a>
		<a href="<%=contextPath%>/forward.do?param=index.food" class="nav_hot">美食<em></em></a>
		<a href="<%=contextPath%>/forward.do?param=index.entertainment">休闲娱乐</a>
		<a href="<%=contextPath%>/forward.do?param=index.beauty">丽人</a>
		<a href="<%=contextPath%>/forward.do?param=index.life">生活服务</a>
      </c:if>
      <c:if test="${STATIC_URL=='true'}">
        <a href="">首页</a>
      	<a href="<%=contextPath %>/ppzq.html">品牌专区</a>
		<a href="<%=contextPath%>/ms.html" class="nav_hot">美食<em></em></a>
		<a href="<%=contextPath%>/xxyl.html">休闲娱乐</a>
		<a href="<%=contextPath%>/lr.html">丽人</a>
		<a href="<%=contextPath%>/shfw.html">生活服务</a>
      </c:if>
      </li>
      <li class="nav_search">
        <form action="<%=contextPath%>/search/searchGoods.do" method="get" id="searchLuceneform">
        <input type="text" id="navSearch" value="${keyword}"/><button type="submit" id="navSearchBtn">搜索</button>
       	<input type="hidden" name="lucenecpage" value="1" id="lucenecpage"/>
      	<input type="hidden" name="keyword" id="searchText" value="${keyword}"/>
      	</form>
      </li>
    </ul>
  </div>
</div>

<script type="text/javascript">var qian_pin_city_request= '<%=qian_pin_city_request%>';</script>
<script type="text/javascript" src="<%=StaticDomain.getDomain("chooseCity.js")%><%=contextPath%>/jsp/js/chooseCity.js"></script>
<!--选择城市 begin-->
<div class="chooseCityBg hidden" id="chooseCity">
  <dl class="choose_city">
    <dt><a href="javascript:blockUIClose()"></a>选择城市</dt>
    <dd>
      <ul id="chooseCityList">
        <li><span class="fourcity" id="beijing">北京</span></li>
        <li><span class="fourcity" id="shenzhen">深圳</span></li>
        <li><span class="fourcity" id="shanghai">上海</span></li>
        <li><span class="fourcity" id="guangzhou">广州</span></li>
        <li><span class="fourcity" id="changsha">长沙</span></li>
        <li><span class="fourcity" id="jinan">济南</span></li>
        <li>重庆</li>
        <li>西安</li>
        <li>杭州</li>
        <li>天津</li>
        <li>青岛</li>
        <li>苏州</li>
        <li>南京</li>
        <li>福州</li>
        <li>厦门</li>
        <li>郑州</li>
        <li>武汉</li>
        <li>沈阳</li>
        <li>淄博</li>
        <li>石家庄</li>
        <li>合肥</li>
        <li>贵州</li>
        <li>成都</li>
        <li>香港</li>
      </ul>
    </dd>
  </dl>
  <div class="choosCityAlpha"></div>
</div>
<!--选择城市 end-->
<script>
	var listCount=<%=regionSize%>;
	var current_nav_param='${param.param}';
	
	if(current_nav_param == ''){
		var temp = '${param.catlog}';
		if(temp != ''){
			switch(temp){
				case 'meishi':
					current_nav_param = 'index.food';
					break;
				case 'xiuxianyule':
					current_nav_param = 'index.entertainment';
					break;
				case 'liren':
					current_nav_param = 'index.beauty';
					break;
				case 'shenghuofuwu':
					current_nav_param = 'index.life';
					break;
				default:
			}
		}
	}
	var current_nav_count=1;
	if(current_nav_param!=null){
		switch(current_nav_param)
	   {
	   case 'index.index':
	     current_nav_count=1;
	     break
	   case 'index.food':
	      current_nav_count=2;
	     break
	   case 'index.entertainment':
	      current_nav_count=3;
	     break
	   case 'index.beauty':
	      current_nav_count=4;
	     break  
	   case 'index.life':
	      current_nav_count=5;
	     break
	   case 'index.coupon_zone':
	      current_nav_count=6;
	     break  
	   case 'index.brand_zone':
	      current_nav_count=7;
	     break  
	   default:
	     current_nav_count=1;
	   }
	   $("#cn"+current_nav_count).addClass("current_nav");
	   
	   for(var i=1;i<current_nav_count;i++){
	   		  $("#cn"+i).removeClass("current_nav");
	   }
	   
	    for(var j=current_nav_count;j<current_nav_count;j++){
	   		  $("#cn"+i).removeClass("current_nav");
	   }
	}
</script>
<!-- 搜索js -->
<script type="text/javascript">
    $('#searchLuceneform').submit(function(){
        var _val = $('#navSearch').val();
        if (_val == '' || _val == '输入搜索内容') {
			$('#searchText').val('');
			$('#lucenecpage').val('1');
            return true;
        }else {
            $('#lucenecpage').val('1');
			$('#searchText').val(encodeURIComponent($("#navSearch").val().substring(0,30)));
            return true;
        }
    });
</script>