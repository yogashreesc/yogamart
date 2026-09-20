<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>All Products - YogaMart Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <h1 class="section-title" style="margin-top:28px;">Moderate Listings</h1>
    <p class="section-sub">All products across every seller. Remove anything inappropriate.</p>

    <table class="seller-table">
        <thead>
        <tr>
            <th>Name</th>
            <th>Category</th>
            <th>Price</th>
            <th>Stock</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${products}">
            <tr>
                <td><c:out value="${p.name}"/></td>
                <td><c:out value="${p.category}"/></td>
                <td>&#8377;<c:out value="${p.price}"/></td>
                <td><c:out value="${p.stockQty}"/></td>
                <td>
                    <form action="${pageContext.request.contextPath}/admin/products/delete" method="post"
                          onsubmit="return confirm('Remove this listing? This cannot be undone.');">
                        <input type="hidden" name="id" value="${p.id}">
                        <button type="submit" class="action delete-link" style="background:none;border:none;cursor:pointer;padding:0;font:inherit;margin-top:0;">Remove</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
