<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Products - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <h1 class="section-title" style="margin-top:28px;">My Products</h1>

    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>

    <a class="btn add-link" href="${pageContext.request.contextPath}/seller/add-product">+ Add New Product</a>

    <table class="seller-table">
        <thead>
        <tr>
            <th>Name</th>
            <th>Category</th>
            <th>Price</th>
            <th>Stock</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${products}">
            <tr>
                <td><c:out value="${p.name}"/></td>
                <td><c:out value="${p.category}"/></td>
                <td class="price">&#8377;<c:out value="${p.price}"/></td>
                <td><c:out value="${p.stockQty}"/></td>
                <td>
                    <a class="action" href="${pageContext.request.contextPath}/seller/edit-product?id=${p.id}">Edit</a>
                    <form action="${pageContext.request.contextPath}/seller/delete-product" method="post" style="display:inline"
                          onsubmit="return confirm('Delete this product? This cannot be undone.');">
                        <input type="hidden" name="id" value="${p.id}">
                        <button type="submit" class="action delete-link" style="background:none;border:none;cursor:pointer;padding:0;font:inherit;margin-top:0;">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty products}">
        <div class="empty-state">
            <p>You haven't listed any products yet.</p>
        </div>
    </c:if>
</div>
</body>
</html>
