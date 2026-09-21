# YogaMart

A multi-seller e-commerce marketplace for health & wellness essentials — first aid, health monitoring devices, supplements, and personal protection. Built for Anna University R2025, Semester 3 (Java Servlets / JDBC track).

**Live demo:** https://yogamart.onrender.com/yogamart/
*(Free-tier hosting — the app may take ~50 seconds to wake up on first load after inactivity, and demo data resets on redeploy.)*

**Demo accounts:**
| Role | Email | Password |
|---|---|---|
| Admin | admin@yogamart.local | Admin@123 |
| Seller | seller@yogamart.local | Seller@123 |
| Buyer | *Register your own — Buyer signup is open* | — |

---

## About YogaMart

Sellers list health & wellness products. Buyers browse, search, add to cart, and check out. An admin oversees users, orders, and listings. YogaMart is intentionally built as a real, working transactional system — not a static mockup — with atomic database transactions, session-based authentication, and role-based access control throughout.

## Features

| Status | Feature |
|---|---|
| ✅ | User registration & login (Buyer, Seller roles; seeded Admin) |
| ✅ | Seller: add / edit / delete product listings |
| ✅ | Buyer: browse, search, and filter products by category |
| ✅ | Shopping cart: add, update quantity, remove |
| ✅ | Checkout via mock payment confirmation |
| ✅ | Order history (buyer's past orders, seller's incoming orders) |
| ✅ | Admin: view all users/orders, moderate (remove) listings |
| 🔲 | Product reviews & star ratings *(planned — gated to completed orders)* |
| 🔲 | Wishlist, order status workflow, seller sales dashboard, AI chatbot *(optional, post-MVP)* |

## Tech Stack

| Component | Choice |
|---|---|
| JDK | 17 |
| Servlet container | Apache Tomcat 9.0.x |
| Build tool | Maven |
| Database | H2 (embedded for local dev, file-based for deployment) |
| Password hashing | jBCrypt |
| View layer | JSP + JSTL, vanilla JS (no frontend framework) |
| Logging | SLF4J + Logback |
| Deployment | Docker (multi-stage build) → Render |

## Architecture

Layered MVC over Servlets, per the assignment spec:
