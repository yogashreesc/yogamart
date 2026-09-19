<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Cart - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <h1 class="section-title" style="margin-top:28px;">My Cart</h1>

    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>

    <c:if test="${not empty cartItems}">
        <table class="seller-table cart-table">
            <thead>
            <tr>
                <th>Product</th>
                <th>Price</th>
                <th>Quantity</th>
                <th>Line Total</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${cartItems}">
                <tr>
                    <td>
                        <div class="cart-product-cell">
                            <img src="${item.imageUrl}" alt="${item.productName}">
                            <a href="${pageContext.request.contextPath}/products/view?id=${item.productId}"><c:out value="${item.productName}"/></a>
                        </div>
                    </td>
                    <td>&#8377;<c:out value="${item.price}"/></td>
                    <td>
                        <form action="${pageContext.request.contextPath}/cart/update" method="post" class="qty-form">
                            <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                            <input type="number" name="quantity" value="${item.quantity}" min="1" max="${item.availableStock}">
                            <button type="submit" class="btn-small">Update</button>
                        </form>
                    </td>
                    <td>&#8377;<c:out value="${item.lineTotal}"/></td>
                    <td>
                        <form action="${pageContext.request.contextPath}/cart/remove" method="post"
                              onsubmit="return confirm('Remove this item from your cart?');">
                            <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                            <button type="submit" class="action delete-link" style="background:none;border:none;cursor:pointer;padding:0;font:inherit;margin-top:0;">Remove</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <div class="cart-summary">
            <div class="cart-total-line">Total: <span>&#8377;<c:out value="${cartTotal}"/></span></div>
            <a class="btn-checkout" href="${pageContext.request.contextPath}/checkout">Proceed to Checkout</a>
        </div>
    </c:if>

    <c:if test="${empty cartItems}">
        <div class="empty-state">
            <p>Your cart is empty.</p>
            <a class="btn" href="${pageContext.request.contextPath}/products">Browse Products</a>
        </div>
    </c:if>
</div>
</body>
</html>
