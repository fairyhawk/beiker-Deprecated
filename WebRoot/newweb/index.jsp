<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/jsp/base.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>千品网 – 全国优质的本地服务商城,吃喝玩乐一网打尽,团购,秒杀,免费优惠券</title>
<meta name="keywords" content="千品网,千品团,本地服务,商城,团购,秒杀,优惠券" />
<meta name="description" content="千品网不是团购网站！千品网是中国优质的本地服务商城，为您精选吃喝玩乐各类商家提供的本地服务商品，提供在线购买、秒杀、团购、优惠券下载等服务。千品网，随时随地都有折儿。" />
<link href="<%=contextPath%>/newweb/css_a/base.css" type="text/css" rel="stylesheet" />
<link href="<%=contextPath%>/newweb/css_a/index.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="js_a/jquery-1.4.2.min.js"></script>
<script type="text/javascript" src="js_a/global.js"></script>

<script type="text/javascript">
$(function(){
	$("#usermessageul").load("<%=contextPath%>/index/getUserLoginMessage.do");
})
</script>

</head>

<body>
<!--    header begin   --> 
<jsp:include page="/newweb/include/header.jsp"></jsp:include>
<!--    header end   --> 
<!--    content begin   -->
<div class="layout">
  <ul class="index_main">
    <li class="index_left">
      <dl class="index_fenlei">
        <dt class="fenlei_1"><a href="#" target="_blank" title="美食类">美食类</a></dt>
        <dd class="line_bottom">
        <a href="#" target="_blank">北京菜</a>　<a href="#" target="_blank">东北菜</a>　<a href="#" target="_blank">川菜</a>　<a href="#" target="_blank">湘菜</a>
        <a href="#" target="_blank">新疆菜</a>　<a href="#" target="_blank">地方菜</a>　<a href="#" target="_blank">粤菜</a>　<a href="#" target="_blank">火锅</a>
        <a href="#" target="_blank">自助餐</a>　<a href="#" target="_blank">日韩料理</a>　<a href="#" target="_blank">小吃快餐</a>
        <a href="#" target="_blank">咖啡厅</a>　<a href="#" target="_blank">面包甜点</a>　<a href="#" target="_blank">其他</a></dd>
        <dt class="fenlei_2"><a href="#" target="_blank" title="休闲娱乐">休闲娱乐</a></dt>
        <dd class="line_bottom"><a href="#" target="_blank">运动健身</a>　<a href="#" target="_blank">酒吧</a>　<a href="#" target="_blank">茶馆</a>　<a href="#" target="_blank">电影</a>
        <a href="#" target="_blank">展览演出</a>　<a href="#" target="_blank">洗浴</a>　<a href="#" target="_blank">养生</a>　<a href="#" target="_blank">其他</a> </dd>
        <dt class="fenlei_3"><a href="#" target="_blank" title="丽人类">丽人类</a></dt>
        <dd class="line_bottom"><a href="#" target="_blank">美发</a>　<a href="#" target="_blank">美容/SPA</a>　<a href="#" target="_blank">瑜伽/舞蹈</a>
        <a href="#" target="_blank">美甲</a>　<a href="#" target="_blank">纤体瘦身</a>　<a href="#" target="_blank">其他</a> </dd>
        <dt class="fenlei_4"><a href="#" target="_blank" title="生活服务">生活服务</a></dt>
        <dd class="line_bottom"><a href="#" target="_blank">摄影</a>　<a href="#" target="_blank">酒店旅馆</a>　<a href="#" target="_blank">教育/培训</a>
        <a href="#" target="_blank">宠物</a>　<a href="#" target="_blank">汽车服务</a>　<a href="#" target="_blank">其他</a> </dd>
        <dt class="fenlei_5"><a href="#" target="_blank" title="商圈">商圈</a></dt>
        <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/hotcircle.html"></jsp:include>
      </dl>
    </li>
    <li class="index_center">
        <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/hotpicimglink.html"></jsp:include>
        <div class="index_jingxuan"><a href="#" target="_blank" class="link_1">更多精选品牌&gt;</a></div>
        <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/recommendBrand.html"></jsp:include>
    </li>
    <li class="index_right">
    	<ul id="usermessageul" class="index_login"></ul>
        <dl class="index_news clr">
        	<dt><a href="#" target="_blank" title="千品快讯">千品快讯</a></dt>
            <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/rightrecommendmessage.html"></jsp:include>
        </dl>
        <dl class="qianpin_commitment mt10">
        	<dt><a href="#" target="_blank" title="千品承诺">千品承诺</a></dt>
            <dd class="mt10">
            	<a href="#" target="_blank" title="千品帮您千挑万选商户">千品帮您千挑万选商户</a>
                <a href="#" target="_blank" title="所售商品随时">所售商品随时</a>
                <a href="#" target="_blank" title="消费后可获千品返现">消费后可获千品返现</a>
            </dd>
        </dl>
    </li>
  </ul>

	<dl class="index_product">
	    <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/hottitlelinkone.html"></jsp:include>
        <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/onefloorpicture.html"></jsp:include>
	    <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/mainlistone.html"></jsp:include>
    </dl>

	<dl class="index_product">
	    <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/hottitlelinktwo.html"></jsp:include>
        <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/twofloorpicture.html"></jsp:include>
        <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/mainlisttwo.html"></jsp:include>
    </dl>

	<dl class="index_product">
	    <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/hottitlelinkthree.html"></jsp:include>
        <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/threefloorpicture.html"></jsp:include>
        <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/mainlistthree.html"></jsp:include>
    </dl>


	<dl class="index_product">
	   <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/hottitlelinkfour.html"></jsp:include>
       <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/fourfloorpicture.html"></jsp:include>
       <jsp:include page="/jsp/templates/${QIANPIN_CITY}/include/mainlistfour.html"></jsp:include>
    </dl>
</div>
<script type="text/javascript" src="js_a/indexFunc.js"></script>
<!--    content end   --> 
<!--    footer begin   --> 

<jsp:include page="/newweb/include/footer.jsp"></jsp:include>
<!--    footer end   -->
</body>
</html>
