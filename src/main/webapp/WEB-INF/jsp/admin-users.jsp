<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>All Users - YogaMart Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<jsp:include page="_nav.jsp"/>

<div class="wrap">
    <h1 class="section-title" style="margin-top:28px;">All Users</h1>
    <p class="section-sub"><c:out value="${fn:length(users)}"/> user(s)</p>

    <table class="seller-table">
        <thead>
        <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Role</th>
            <th>Joined</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${users}">
            <tr>
                <td><c:out value="${u.name}"/></td>
                <td><c:out value="${u.email}"/></td>
                <td><span class="pill-role"><c:out value="${u.role}"/></span></td>
                <td><fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
