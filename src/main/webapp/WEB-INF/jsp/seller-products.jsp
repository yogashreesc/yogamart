<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Products - YogaMart</title>
    <style>
        body { font-family: system-ui, sans-serif; max-width: 900px; margin: 30px auto; padding: 0 20px; }
        header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #2c7a4b; padding-bottom: 10px; }
        h1 { color: #2c7a4b; margin: 0; }
        table { width: 100%; border-collapse: collapse; margin-top: 24px; }
        th, td { text-align: left; padding: 10px; border-bottom: 1px solid #ddd; }
        th { color: #555; font-size: 0.85em; text-transform: uppercase; }
        .price { color: #2c7a4b; font-weight: 700; }
        a.action { margin-right: 10px; text-decoration: none; color: #2c7a4b; }
        a.delete { color: #b00020; }
        .error { color: #b00020; margin-top: 12px; }
        a.add-link { display: inline-block; margin-top: 16px; }
    </style>
</head>
<body>
<header>
    <h1>My Products</h1>
    <div>
        Signed in as <strong><c:out value="${sessionScope.userName}"/></strong>
        (<c:out value="${sessionScope.userRole}"/>) &middot;
        <a href="${pageContext.request.contextPath}/products">Browse</a> &middot;
        <a class="logout" href="${pageContext.request.contextPath}/logout">Log out</a>
    </div>
</header>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<a class="add-link" href="${pageContext.request.contextPath}/seller/add-product">+ Add New Product</a>

<table>
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
                    <button type="submit" class="action delete" style="background:none;border:none;cursor:pointer;padding:0;font:inherit;">Delete</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${empty products}">
    <p>You haven't listed any products yet.</p>
</c:if>
</body>
</html>
