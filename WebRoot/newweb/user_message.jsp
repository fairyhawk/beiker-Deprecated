<%@ page language="java"  pageEncoding="utf-8"%>
<%@ include file="/jsp/base.jsp"%>
<script type="text/javascript">
document.getElementById("usermessageul").class = "index_login_success";
</script>
<c:if test="${newuserid eq null}">
<li><div class="left">已有帐号？请点此登录</div><div class="right"><a href="<%=contextPath%>/forward.do?param=login" target="_blank" title="登录千品网" class="link_3"></a></div></li>
<li><div class="left">没有帐号？请点此注册</div><div class="right"><a href="<%=contextPath%>/forward.do?param=regist" target="_blank" title="注册成千品网会员" class="link_4"></a></div></li>
</c:if>
<c:if test="${newuserid ne null}">
<li class="su_1">
   	<div class="left"><a href="<%=contextPath%>/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNUSEED" target="_blank" class="link_2">未使用：(<font class="fontred">${unusedTrxorderCount}</font>)</a></div>
   	<div class="right"><a href="<%=contextPath%>/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNCOMMENT" target="_blank" class="link_2">未评价：(<font class="fontred">${unCommentCount}</font>)</a></div>
</li>
<li class="su_2">
   	<div class="left"><a href="<%=contextPath%>/ucenter/showPurse.do?qryType=PURSE_ACTHISTROTY" target="_blank" class="link_2">余额：<font class="fontred">${balance}</font>元</a></div>
   	<div class="right"><a href="<%=contextPath%>/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNUSEED" target="_blank" class="link_1">去我的千品&gt;</a></div>
</li>
</c:if>