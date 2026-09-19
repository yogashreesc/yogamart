<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Order Confirmed - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <div class="confirmation-banner">
        <div class="confirmation-icon">&#10003;</div>
        <h1>Order Placed Successfully</h1>
        <p>Order #<c:out value="${order.id}"/> &middot; Status: <strong><c:out value="${order.status}"/></strong></p>
    </div>

    <table class="seller-table cart-table">
        <thead>
        <tr>
            <th>Product</th>
            <th>Price</th>
            <th>Quantity</th>
            <th>Line Total</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${orderItems}">
            <tr>
                <td>
                    <div class="cart-product-cell">
                        <img src="${item.imageUrl}" alt="${item.productName}">
                        <span><c:out value="${item.productName}"/></span>
                    </div>
                </td>
                <td>&#8377;<c:out value="${item.unitPrice}"/></td>
                <td><c:out value="${item.quantity}"/></td>
                <td>&#8377;<c:out value="${item.lineTotal}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="cart-summary">
        <div class="cart-total-line">Total: <span>&#8377;<c:out value="${order.totalAmount}"/></span></div>
    </div>

    <p class="muted"><a href="${pageContext.request.contextPath}/products">&larr; Continue browsing</a></p>
</div>
</body>
</html>
