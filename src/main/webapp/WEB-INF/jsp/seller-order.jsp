<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Incoming Orders - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <h1 class="section-title" style="margin-top:28px;">Incoming Orders</h1>
    <p class="section-sub">Orders containing your products, across all buyers.</p>

    <c:if test="${not empty orderLines}">
        <table class="seller-table">
            <thead>
            <tr>
                <th>Order #</th>
                <th>Date</th>
                <th>Buyer</th>
                <th>Product</th>
                <th>Qty</th>
                <th>Line Total</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="line" items="${orderLines}">
                <tr>
                    <td>#<c:out value="${line.orderId}"/></td>
                    <td><fmt:formatDate value="${line.orderDate}" pattern="dd MMM yyyy, h:mm a"/></td>
                    <td><c:out value="${line.buyerName}"/></td>
                    <td><c:out value="${line.productName}"/></td>
                    <td><c:out value="${line.quantity}"/></td>
                    <td>&#8377;<c:out value="${line.lineTotal}"/></td>
                    <td><span class="status-pill status-${line.orderStatus}"><c:out value="${line.orderStatus}"/></span></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <c:if test="${empty orderLines}">
        <div class="empty-state">
            <p>No orders yet for your products.</p>
        </div>
    </c:if>
</div>
</body>
</html>