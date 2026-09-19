<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${product.name}"/> - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <div class="detail-layout">
        <div class="detail-image">
            <img src="${product.imageUrl}" alt="${product.name}">
        </div>
        <div class="detail-info">
            <div class="category"><c:out value="${product.category}"/></div>
            <h1><c:out value="${product.name}"/></h1>
            <p class="detail-description"><c:out value="${product.description}"/></p>
            <div class="detail-price">&#8377;<c:out value="${product.price}"/></div>

            <c:choose>
                <c:when test="${product.stockQty > 0}">
                    <p class="stock-line in-stock">&#10003; In stock (${product.stockQty} available)</p>
                </c:when>
                <c:otherwise>
                    <p class="stock-line out-of-stock">Out of stock</p>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty param.cartError}">
                <p class="error">${param.cartError}</p>
            </c:if>

            <c : if test="${product.stockQty > 0 && sessionScope.uesrRole == 'BUYER'}"
                <form method="post" action="${pageContext.request.contextPath}/cart/add" class="add-to-cart-form">
                    <input type="hidden" name="productId" value="${product.id}">
                    <label for="quantity">Quantity</label>
                    <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stockQty}">
                    <button type="submit" class="btn-add-cart">Add to Cart</button>
                </form>
            </c:if>

            <p class="muted"><a href="${pageContext.request.contextPath}/products">&larr; Back to browsing</a></p>
        </div>
    </div>
</div>
</body>
</html>
