# YogaMart

Multi-seller marketplace web app — health & wellness essentials. Built for
Anna University R2025 Semester 3 Capstone (Java / Servlets / JDBC track).

## Tech stack

| Component | Choice |
|---|---|
| JDK | 17 |
| Servlet container | Tomcat 9.0.x (`javax.servlet.*`) |
| Build tool | Maven |
| Database | H2 (embedded for dev, server mode for deployment) |
| Password hashing | jBCrypt |
| View layer | JSP + JSTL |
| Testing | JUnit 5 + Mockito |
| Logging | SLF4J + Logback |

## Setup — local

```bash
git clone <this-repo>
cd yogamart
mvn clean package
# deploy target/yogamart.war to Tomcat 9's webapps/ folder, or run via
# a Maven Tomcat plugin / your IDE's Tomcat integration
```

On first startup, `AppContextListener` creates the H2 schema, seeds two
accounts and six demo products automatically. No manual DB setup needed
for local development.

**Seed accounts:**
| Role | Email | Password |
|---|---|---|
| Admin | admin@yogamart.local | Admin@123 |
| Seller | seller@yogamart.local | Seller@123 |

Buyers self-register via the Register page.

## What's done (MVP Review — Aug 10)

- [x] Project skeleton (Maven, Tomcat-ready WAR, package layout per spec Section 2)
- [x] DB schema (all 6 tables from Section 4) + seed data
- [x] Registration + login (F1), bcrypt-hashed passwords, session-based auth,
      session ID regenerated on login, 30-min explicit timeout
- [x] Browse/search products (F3) — one complete end-to-end journey:
      register → log in → view seeded product catalog → search by keyword
- [x] Layered architecture: controller → service → dao, DAO-only SQL,
      PreparedStatement everywhere, try-with-resources throughout
- [x] Custom checked exceptions (ValidationException, DuplicateEmailException,
      AuthenticationException)

## Known tech debt (tracked, not hidden)

- **Connection pooling:** currently uses H2's `JdbcDataSource` directly
  instead of HikariCP. The Service/DAO layers depend only on
  `javax.sql.DataSource`, so swapping in HikariCP is a single-file change
  confined to `AppContextListener` — planned for Week 3.
- **AuthFilter:** session checks are currently inline in `ProductServlet`;
  moves to a dedicated filter once more protected routes exist.
- **F2, F4–F8, all optional features:** not yet started — see the execution
  timeline in the spec (Weeks 3–11).

## Design patterns in use so far

- **DAO** — `UserDAO`/`ProductDAO` interfaces, JDBC implementations
- **Front Controller (partial)** — per-resource servlets (`AuthServlet`,
  `ProductServlet`), converging further as routes grow
- Singleton, Factory, Strategy, Builder — planned for Weeks 5, 11–13 per
  the roadmap; not yet applicable to the current feature surface
