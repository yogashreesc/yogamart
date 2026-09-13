<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Products - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <section class="hero">
        <span class="float-icon i1">&#129657;</span>
        <span class="float-icon i2">&#128138;</span>
        <span class="float-icon i3">&#10084;&#65039;</span>
        <span class="float-icon i4">&#129530;</span>

        <h1>Better Health. Delivered Smarter.</h1>
        <p class="tagline">Health &amp; wellness essentials, all in one place.</p>

        <form class="search" method="get" action="${pageContext.request.contextPath}/products">
            <input type="text" name="q" placeholder="Search medicines, devices, wellness products..." value="${param.q}">
            <c:if test="${not empty param.category}">
                <input type="hidden" name="category" value="${param.category}">
            </c:if>
            <button type="submit">Search</button>
        </form>

        <div class="category-row">
            <a class="category-chip" data-category=""
               href="${pageContext.request.contextPath}/products<c:if test='${not empty param.q}'>?q=${param.q}</c:if>">All</a>
            <a class="category-chip" data-category="Health Monitoring"
               href="${pageContext.request.contextPath}/products?category=Health+Monitoring">&#129657; Health Monitoring</a>
            <a class="category-chip" data-category="First Aid"
               href="${pageContext.request.contextPath}/products?category=First+Aid">&#129656; First Aid</a>
            <a class="category-chip" data-category="Supplements"
               href="${pageContext.request.contextPath}/products?category=Supplements">&#128138; Supplements</a>
            <a class="category-chip" data-category="Personal Protection"
               href="${pageContext.request.contextPath}/products?category=Personal+Protection">&#128567; Personal Protection</a>
        </div>
    </section>

    <h2 class="section-title">
        <c:choose>
            <c:when test="${not empty param.category}">${param.category}</c:when>
            <c:otherwise>All Products</c:otherwise>
        </c:choose>
    </h2>
    <p class="section-sub"><c:out value="${fn:length(products)}"/> product(s) found</p>

    <div class="grid">
        <c:forEach var="p" items="${products}">
            <div class="card">
                <div class="thumb">
                    <img src="${p.imageUrl}" alt="${fn:escapeXml(p.name)}">
                </div>
                <h3><c:out value="${p.name}"/></h3>
                <div class="category"><c:out value="${p.category}"/></div>
                <div class="price">&#8377;<c:out value="${p.price}"/></div>
                <div class="stock">Stock: <c:out value="${p.stockQty}"/></div>
            </div>
        </c:forEach>
    </div>

    <c:if test="${empty products}">
        <div class="empty-state">
            <p>No products found. Try a different search or category.</p>
        </div>
    </c:if>
</div>

<script src="${pageContext.request.contextPath}/assets/js/site.js"></script>
</body>
</html>
