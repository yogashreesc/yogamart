<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<div class="form-card">
    <div class="logo-header">
        <img class="logo" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="YogaMart">
        <h1>YogaMart</h1>
    </div>
    <p class="tagline">Health essentials marketplace — log in to continue.</p>

    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>
    <c:if test="${not empty success}">
        <p class="success">${success}</p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Log In</button>
    </form>

    <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a>.</p>

    <p class="muted">
        Demo accounts — Seller: seller@yogamart.local / Seller@123 &middot;
        Admin: admin@yogamart.local / Admin@123
    </p>
</div>
</body>
</html>
