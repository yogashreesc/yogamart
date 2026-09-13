<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - YogaMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/site.css">
</head>
<body>
<div class="form-card">
    <div class="logo-header">
        <img class="logo" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="YogaMart">
        <h1>Create Account</h1>
    </div>
    <p class="tagline">Join YogaMart as a buyer or seller.</p>

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
</div>
</body>
</html>
