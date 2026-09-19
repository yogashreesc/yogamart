<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Checkout - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <h1 class="section-title" style="margin-top:28px;">Order Summary</h1>
    <p class="section-sub">Review your order before confirming — no real payment is taken (mock checkout).</p>

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
        <c:forEach var="item" items="${cartItems}">
            <tr>
                <td>
                    <div class="cart-product-cell">
                        <img src="${item.imageUrl}" alt="${item.productName}">
                        <span><c:out value="${item.productName}"/></span>
                    </div>
                </td>
                <td>&#8377;<c:out value="${item.price}"/></td>
                <td><c:out value="${item.quantity}"/></td>
                <td>&#8377;<c:out value="${item.lineTotal}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="cart-summary">
        <div class="cart-total-line">Total: <span>&#8377;<c:out value="${cartTotal}"/></span></div>
        <form method="post" action="${pageContext.request.contextPath}/checkout/confirm">
            <button type="submit" class="btn-checkout">Confirm Order (Mock Payment)</button>
        </form>
    </div>

    <p class="muted"><a href="${pageContext.request.contextPath}/cart">&larr; Back to cart</a></p>
</div>
</body>
</html>