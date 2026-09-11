<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Products - YogaMart</title>
    <style>
        body { font-family: system-ui, sans-serif; max-width: 900px; margin: 30px auto; padding: 0 20px; }
        header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #2c7a4b; padding-bottom: 10px; }
        h1 { color: #2c7a4b; margin: 0; }
        .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(220px, 1fr)); gap: 16px; margin-top: 24px; }
        .card { border: 1px solid #ddd; border-radius: 8px; padding: 12px; }
        .card img { width: 100%; border-radius: 4px; }
        .card h3 { margin: 8px 0 4px; font-size: 1em; }
        .price { color: #2c7a4b; font-weight: 700; }
        .category { font-size: 0.8em; color: #888; }
        form.search { margin-top: 20px; }
        input[type=text] { padding: 8px; width: 250px; }
        button { padding: 8px 14px; background: #2c7a4b; color: white; border: none; cursor: pointer; }
        a.logout { color: #b00020; text-decoration: none; }
    </style>
</head>
<body>
<header>
    <h1>YogaMart</h1>
    <div>
        Signed in as <strong><c:out value="${sessionScope.userName}"/></strong>
        (<c:out value="${sessionScope.userRole}"/>) &middot;
        <c:if test="${sessionScope.userRole == 'SELLER' || sessionScope.userRole == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/seller/products">My Products</a> &middot;
        </c:if>
        <a class="logout" href="${pageContext.request.contextPath}/logout">Log out</a>
    </div>
</header>

<form class="search" method="get" action="${pageContext.request.contextPath}/products">
    <input type="text" name="q" placeholder="Search products..." value="${param.q}">
    <button type="submit">Search</button>
</form>

<div class="grid">
    <c:forEach var="p" items="${products}">
        <div class="card">
            <img src="${p.imageUrl}" alt="${fn:escapeXml(p.name)}">
            <h3><c:out value="${p.name}"/></h3>
            <div class="category"><c:out value="${p.category}"/></div>
            <div class="price">&#8377;<c:out value="${p.price}"/></div>
            <div>Stock: <c:out value="${p.stockQty}"/></div>
        </div>
    </c:forEach>
</div>

<c:if test="${empty products}">
    <p>No products found.</p>
</c:if>
</body>
</html>
