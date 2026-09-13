<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Product - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>
<div class="form-card">
    <h1>Edit Product</h1>

    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>

    <c:if test="${not empty product}">
    <form method="post" action="${pageContext.request.contextPath}/seller/edit-product">
        <input type="hidden" name="id" value="${product.id}">

        <label for="name">Product Name</label>
        <input type="text" id="name" name="name" value="${product.name}" required>

        <label for="description">Description</label>
        <textarea id="description" name="description" rows="3">${product.description}</textarea>

        <label for="price">Price (&#8377;)</label>
        <input type="number" id="price" name="price" step="0.01" min="0.01" value="${product.price}" required>

        <label for="stockQty">Stock Quantity</label>
        <input type="number" id="stockQty" name="stockQty" min="0" value="${product.stockQty}" required>

        <label for="category">Category</label>
        <input type="text" id="category" name="category" value="${product.category}" required>

        <label for="imageUrl">Image URL</label>
        <input type="text" id="imageUrl" name="imageUrl" value="${product.imageUrl}">

        <button type="submit">Save Changes</button>
    </form>
    </c:if>

    <p class="muted"><a href="${pageContext.request.contextPath}/seller/products">&larr; Back to my products</a></p>
</div>
</body>
</html>
