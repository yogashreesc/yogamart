<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>All Orders - YogaMart Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <h1 class="section-title" style="margin-top:28px;">All Orders</h1>

    <c:if test="${not empty orders}">
        <table class="seller-table">
            <thead>
            <tr>
                <th>Order #</th>
                <th>Buyer</th>
                <th>Date</th>
                <th>Status</th>
                <th>Total</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="o" items="${orders}">
                <tr>
                    <td>#<c:out value="${o.orderId}"/></td>
                    <td><c:out value="${o.buyerName}"/></td>
                    <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy, h:mm a"/></td>
                    <td><span class="status-pill status-${o.status}"><c:out value="${o.status}"/></span></td>
                    <td>&#8377;<c:out value="${o.totalAmount}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <c:if test="${empty orders}">
        <div class="empty-state">
            <p>No orders have been placed yet.</p>
        </div>
    </c:if>
</div>
</body>
</html>
