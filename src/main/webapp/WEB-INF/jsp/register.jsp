<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - YogaMart</title>
    <style>
        body { font-family: system-ui, sans-serif; max-width: 400px; margin: 60px auto; padding: 0 20px; }
        h1 { color: #2c7a4b; }
        label { display: block; margin-top: 12px; font-weight: 600; }
        input, select { width: 100%; padding: 8px; margin-top: 4px; box-sizing: border-box; }
        button { margin-top: 20px; padding: 10px 20px; background: #2c7a4b; color: white; border: none; cursor: pointer; }
        .error { color: #b00020; margin-top: 12px; }
    </style>
</head>
<body>
<h1>Create Account</h1>

<c:if test="${not empty error}">
    <p class="error">${error}</p>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/register">
    <label for="name">Name</label>
    <input type="text" id="name" name="name" required>

    <label for="email">Email</label>
    <input type="email" id="email" name="email" required>

    <label for="password">Password (min 8 characters)</label>
    <input type="password" id="password" name="password" minlength="8" required>

    <label for="role">Account type</label>
    <select id="role" name="role">
        <option value="BUYER">Buyer</option>
        <option value="SELLER">Seller</option>
    </select>

    <button type="submit">Register</button>
</form>

<p>Already have an account? <a href="${pageContext.request.contextPath}/login">Log in</a>.</p>
</body>
</html>
