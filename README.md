# RetailX - E-Commerce Platform

A full-stack e-commerce REST API built with Spring Boot 3.3.0 and MySQL.

## Quick Start

### Prerequisites
- Java 17
- MySQL Server (username: `root`, password: `test123`)
- Maven (or use included Maven Wrapper)

### Setup Steps

1. **Initialize Database**
   ```sql
   mysql -u root -ptest123 < database-init.sql
   ```

2. **Run Application**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

3. **Verify**
   - Health Check: http://localhost:8080/api/health
   - Swagger UI: http://localhost:8080/swagger-ui.html

## Configuration

- **Database**: MySQL on `localhost:3306`
- **Database Name**: `retailx`
- **Username**: `root`
- **Password**: `test123`
- **Port**: `8080`

Configuration files:
- `src/main/resources/application-dev.properties` (Active)
- `src/main/resources/application-prod.properties`

## Documentation

- **Complete Project Documentation**: `PROJECT_DOCUMENTATION.md` - Everything about the project
- **Postman Testing Guide**: `POSTMAN_TESTING_GUIDE.md` - Step-by-step API testing with exact JSON payloads
- **Postman Quick Reference**: `POSTMAN_QUICK_REFERENCE.md` - Quick reference card for common endpoints
- **Database Setup**: `HOW_TO_RUN_DATABASE_SCRIPT.md` - Database initialization guide

## Key Features

- ✅ User Registration & Authentication (JWT)
- ✅ Role-Based Access Control (Admin/User)
- ✅ Product & Category Management
- ✅ Shopping Cart
- ✅ Order Processing
- ✅ Payment Handling
- ✅ Address Management
- ✅ Pagination & Sorting
- ✅ Swagger API Documentation

## Project Structure

```
src/main/java/com/example/eshopee/
├── config/          # Configuration classes
├── controllers/     # REST API endpoints
├── entites/         # JPA entities (database tables)
├── repositories/    # Data access layer
├── services/        # Business logic
├── payloads/        # DTOs
├── exceptions/      # Exception handling
└── filter/          # Security filters
```

## Testing with Postman

1. Register: `POST /api/register`
2. Login: `POST /api/login` (save JWT token)
3. Use token in `Authorization: Bearer <token>` header for protected endpoints

See `PROJECT_DOCUMENTATION.md` for complete Postman guide.

## Database Schema

- **users** - User accounts
- **roles** - User roles (admin/user)
- **categories** - Product categories
- **products** - Product catalog
- **carts** - Shopping carts
- **cart_items** - Cart items
- **orders** - Orders
- **order_items** - Order items
- **payments** - Payment information
- **addresses** - User addresses

## Important Notes

- Default roles must exist: `admin` (ID: 101) and `user` (ID: 102)
- JWT tokens expire after 24 hours
- Database tables are auto-created by Hibernate on first run
- All passwords are encrypted with BCrypt

## Support

For detailed explanations of every component, method, annotation, and database relationship, see `PROJECT_DOCUMENTATION.md`.

---

**Built with Spring Boot 3.3.0 | Java 17 | MySQL 8.0**

