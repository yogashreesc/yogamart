<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Product - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>
<div class="form-card">
    <h1>Add a New Product</h1>
    <p class="tagline">Listing a product on YogaMart — health &amp; wellness essentials.</p>

    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p class="success">${success}</p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/seller/add-product">
        <label for="name">Product Name</label>
        <input type="text" id="name" name="name" required>

        <label for="description">Description</label>
        <textarea id="description" name="description" rows="3"></textarea>

        <label for="price">Price (&#8377;)</label>
        <input type="number" id="price" name="price" step="0.01" min="0.01" required>

        <label for="stockQty">Stock Quantity</label>
        <input type="number" id="stockQty" name="stockQty" min="0" required>

        <label for="category">Category</label>
        <input type="text" id="category" name="category" placeholder="e.g. Health Monitoring, Supplements" required>

        <label for="imageUrl">Image URL (optional)</label>
        <input type="text" id="imageUrl" name="imageUrl" placeholder="Leave blank for a placeholder image">

        <button type="submit">Add Product</button>
    </form>

    <p class="muted"><a href="${pageContext.request.contextPath}/seller/products">&larr; Back to my products</a></p>
</div>
</body>
</html>
