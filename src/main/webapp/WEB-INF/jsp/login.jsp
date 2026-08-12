<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - YogaMart</title>
    <style>
        body { font-family: system-ui, sans-serif; max-width: 400px; margin: 60px auto; padding: 0 20px; }
        h1 { color: #2c7a4b; }
        label { display: block; margin-top: 12px; font-weight: 600; }
        input { width: 100%; padding: 8px; margin-top: 4px; box-sizing: border-box; }
        button { margin-top: 20px; padding: 10px 20px; background: #2c7a4b; color: white; border: none; cursor: pointer; }
        .error { color: #b00020; margin-top: 12px; }
        .success { color: #2c7a4b; margin-top: 12px; }
        .muted { font-size: 0.85em; color: #666; margin-top: 20px; }
    </style>
</head>
<body>
<h1>YogaMart</h1>
<p>Health essentials marketplace — log in to continue.</p>

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
</body>
</html>
