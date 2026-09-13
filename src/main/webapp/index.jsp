<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session.getAttribute("userId") != null) {
        response.sendRedirect(request.getContextPath() + "/products");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>YogaMart — Health Essentials, Delivered</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/site.css">
</head>
<body>
<header class="landing-header">
    <div class="wrap bar">
        <span class="brand"><img class="logo" src="<%= request.getContextPath() %>/assets/img/logo.png" alt="YogaMart">YogaMart</span>
        <div class="landing-actions">
            <a class="btn-outline" href="<%= request.getContextPath() %>/login">Log In</a>
            <a class="btn-solid" href="<%= request.getContextPath() %>/register">Register</a>
        </div>
    </div>
</header>

<div class="wrap">
    <section class="landing-hero">
        <span class="float-icon i1">&#129657;</span>
        <span class="float-icon i2">&#128138;</span>
        <span class="float-icon i3">&#10084;&#65039;</span>
        <span class="float-icon i4">&#129530;</span>

        <h1>Better Health. Delivered Smarter.</h1>
        <p class="sub">
            YogaMart is a multi-seller marketplace for health &amp; wellness essentials —
            first aid, health monitoring devices, supplements, and personal protection —
            from sellers you can browse, search, and buy from directly.
        </p>
        <div class="cta-row">
            <a class="btn-solid" href="<%= request.getContextPath() %>/register">Get Started</a>
            <a class="btn-outline" href="<%= request.getContextPath() %>/login">I have an account</a>
        </div>
    </section>

    <div class="feature-grid">
        <div class="feature-card">
            <div class="icon">&#128269;</div>
            <h3>Search &amp; Filter</h3>
            <p>Find what you need fast — search by keyword or filter by health category.</p>
        </div>
        <div class="feature-card">
            <div class="icon">&#127978;</div>
            <h3>Multi-Seller Marketplace</h3>
            <p>Independent sellers list and manage their own health &amp; wellness products.</p>
        </div>
        <div class="feature-card">
            <div class="icon">&#128274;</div>
            <h3>Secure Accounts</h3>
            <p>Password-hashed, session-based accounts for buyers, sellers, and admins.</p>
        </div>
        <div class="feature-card">
            <div class="icon">&#11088;</div>
            <h3>Built for Trust</h3>
            <p>Clear product listings with real pricing, stock, and category information.</p>
        </div>
    </div>

    <p class="landing-footer">YogaMart — a student capstone project. &copy; 2026.</p>
</div>
</body>
</html>
