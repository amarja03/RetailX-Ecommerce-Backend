# RetailX - Postman Quick Reference Card

## Base URL
```
http://localhost:8080/api
```

## Authentication Header
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## Essential Endpoints

### 1. Register User
```
POST /api/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "mobileNumber": "1234567890",
  "email": "john@example.com",
  "password": "password123",
  "address": {
    "street": "123 Main St",
    "buildingName": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "pincode": "10001"
  }
}
```

### 2. Login
```
POST /api/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

### 2a. Make User Admin (After Registration)
```
Run in MySQL:
USE retailx;
SELECT user_id FROM users WHERE email = 'john@example.com';
DELETE FROM user_role WHERE user_id = USER_ID;
INSERT INTO user_role (user_id, role_id) VALUES (USER_ID, 101);
-- Then login again to get admin token
```

### 3. Create Category (Admin)
```
POST /api/admin/category
Authorization: Bearer TOKEN
Content-Type: application/json

{
  "categoryName": "Electronics"
}
```

### 4. Add Product (Admin)
```
POST /api/admin/categories/1/product
Authorization: Bearer TOKEN
Content-Type: application/json

{
  "productName": "iPhone 15",
  "description": "Latest iPhone model",
  "quantity": 50,
  "price": 999.99,
  "discount": 10.0,
  "specialPrice": 899.99
}
```

### 5. Get Products (Public)
```
GET /api/public/products?pageNumber=0&pageSize=10
```

### 6. Add to Cart
```
POST /api/public/carts/1/products/1/quantity/2
Authorization: Bearer TOKEN
```

### 7. Place Order
```
POST /api/public/users/john@example.com/carts/1/payments/CREDIT_CARD/order
Authorization: Bearer TOKEN
```

---

## Payment Methods
- `CREDIT_CARD`
- `DEBIT_CARD`
- `UPI`
- `CASH_ON_DELIVERY`

---

## Common Status Codes
- `200 OK` - Success
- `201 Created` - Resource created
- `302 Found` - Success (used for GET)
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Missing/invalid token
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found

---

**Full Guide**: See `POSTMAN_TESTING_GUIDE.md`

