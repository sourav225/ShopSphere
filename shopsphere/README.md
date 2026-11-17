# ShopSphere – Full-Stack E-Commerce Platform

React 18 + Material UI + RTK Query  ·  Spring Boot 3 REST API  ·  PostgreSQL  ·  Redis  ·  JWT + RBAC

## Run it
```bash
docker compose up -d                 # PostgreSQL :5432 + Redis :6379
cd backend && mvn spring-boot:run    # API on :8080 (Java 17+)
cd frontend && npm install && npm run dev   # UI on :5173 (proxies /api to :8080)
```
Seeded admin: `admin@shopsphere.com` / `Admin@123` (change before deploying). Sample products are seeded on first start.

## Architecture
- **Auth/RBAC**: `/api/auth/register|login` return a JWT; `JwtAuthFilter` sets the role; `SecurityConfig` makes
  `GET /api/products/**` public, `/api/products/**` writes and `/api/admin/**` ADMIN-only, everything else authenticated.
- **CQRS-inspired split**
  - Write side (`ProductCommandService`, `OrderCommandService`): `@Transactional(isolation = SERIALIZABLE)` on PostgreSQL,
    stock is decremented inside the checkout transaction (no overselling), caches are evicted after writes.
  - Read side (`ProductQueryService`, `OrderQueryService`): `readOnly` transactions, catalog queries cached in Redis (`@Cacheable`, 10 min TTL).
  - Serialization failures surface as HTTP 409 "please retry" (`GlobalExceptionHandler`).
- **Orders & payments**: cart -> `POST /api/orders` (PENDING/PENDING) -> `POST /api/orders/{id}/pay?success=true|false`
  (mock gateway) -> PAID/FAILED. Admins move orders through CONFIRMED/SHIPPED/DELIVERED/CANCELLED.
- **Admin dashboard**: stats (paid revenue, orders, products, users, orders by status), product CRUD (soft delete), order status management.

## API summary
| Method | Path | Access |
|---|---|---|
| POST | /api/auth/register, /api/auth/login | public |
| GET | /api/products?q=&category=&page=&size= , /api/products/{id} | public |
| POST/PUT/DELETE | /api/products[/{id}] | ADMIN |
| GET/POST/DELETE | /api/cart, /api/cart/{productId} | user |
| POST/GET | /api/orders, POST /api/orders/{id}/pay | user |
| GET/PATCH | /api/admin/stats, /api/admin/orders, /api/admin/orders/{id}/status | ADMIN |
