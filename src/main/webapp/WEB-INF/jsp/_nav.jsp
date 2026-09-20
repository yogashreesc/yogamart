<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="site-header">
    <div class="wrap bar">
        <a class="brand" href="${pageContext.request.contextPath}/products">
            <img class="logo" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="YogaMart">YogaMart
        </a>
        <div class="nav-links">
            <span>Hi, <strong><c:out value="${sessionScope.userName}"/></strong></span>
            <span class="pill-role"><c:out value="${sessionScope.userRole}"/></span>
            <a href="${pageContext.request.contextPath}/products">Browse</a>
            <c:if test="${sessionScope.userRole == 'BUYER'}">
                <a href="${pageContext.request.contextPath}/cart">&#128722; Cart</a>
                <a href="${pageContext.request.contextPath}/orders">My Orders</a>
            </c:if>
            <c:if test="${sessionScope.userRole == 'SELLER'}">
                <a href="${pageContext.request.contextPath}/seller/products">My Products</a>
                <a href="${pageContext.request.contextPath}/seller/orders">Incoming Orders</a>
            </c:if>
            <a class="logout" href="${pageContext.request.contextPath}/logout">Log out</a>
        </div>
    </div>
</header>