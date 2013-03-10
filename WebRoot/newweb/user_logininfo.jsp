<%@ page language="java"  pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript">
document.getElementById("shopcartcount").innerText = ${div_shopcartSummary.totalProduct};
<c:if test="${DIV_QIANPIN_USER ne null}">
	document.getElementById("myqianpin").href = "<%=request.getContextPath()%>/ucenter/showTrxGoodsOrder.do?qryType=TRX_GOODS_UNUSEED";
</c:if>
</script>

<c:if test="${DIV_QIANPIN_USER ne null}">
${DIV_QIANPIN_USER.email}，欢迎您!<a href="<%=request.getContextPath()%>/user/logout.do" class="a_blue ml5">退出</a>
</c:if>
<c:if test="${DIV_QIANPIN_USER eq null}">
欢迎您来到千品，<a href="<%=request.getContextPath()%>/forward.do?param=login" class="a_blue mr5">登录</a>|<a href="<%=request.getContextPath()%>/forward.do?param=regist" class="a_blue ml5">注册</a>
</c:if>