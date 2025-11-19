# RetailX - Complete Postman Testing Guide

This guide provides step-by-step instructions for testing all API endpoints with exact JSON payloads.

---

## Table of Contents
1. [Postman Setup](#postman-setup)
2. [Testing Flow Overview](#testing-flow-overview)
3. [Step-by-Step API Testing](#step-by-step-api-testing)
4. [Complete Request Examples](#complete-request-examples)
5. [Troubleshooting](#troubleshooting)

---

## Postman Setup

### Step 1: Create Environment Variables

1. Open Postman
2. Click **Environments** (left sidebar) → **+** (Create Environment)
3. Name: `RetailX Local`
4. Add these variables:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `base_url` | `http://localhost:8080/api` | `http://localhost:8080/api` |
| `jwt_token` | (leave empty) | (leave empty) |
| `user_email` | (leave empty) | (leave empty) |
| `user_id` | (leave empty) | (leave empty) |
| `cart_id` | (leave empty) | (leave empty) |
| `category_id` | (leave empty) | (leave empty) |
| `product_id` | (leave empty) | (leave empty) |
| `order_id` | (leave empty) | (leave empty) |

5. Click **Save**

### Step 2: Select Environment

- In the top-right corner, select **RetailX Local** from the environment dropdown

---

## Testing Flow Overview

**Recommended Order:**
1. Health Check (verify server is running)
2. Register User (create account)
3. Login (get JWT token)
4. Create Category (Admin - need admin user first)
5. Add Product (Admin)
6. Get Products (Public - no auth needed)
7. Add to Cart (User)
8. Get Cart (User)
9. Place Order (User)
10. Get Orders (User)

---

## Step-by-Step API Testing

### 1. Health Check (Public - No Auth)

**Purpose**: Verify server is running

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/health`
- **Headers**: None required
- **Body**: None

**Expected Response:**
```json
{
  "status": "UP"
}
```

**Status Code**: `200 OK`

---

### 2. Register New User

**Purpose**: Create a new user account

**Request Setup:**
- **Method**: `POST`
- **URL**: `{{base_url}}/register`
- **Headers**:
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "mobileNumber": "1234567890",
  "email": "john.doe@example.com",
  "password": "password123",
  "address": {
    "street": "123 Main Street",
    "buildingName": "Apartment 4B",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "pincode": "10001"
  }
}
```

**Expected Response:**
```
Token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Status Code**: `201 Created`

**Important Notes:**
- Copy the token from response (you'll need it for authenticated requests)
- Save `email` to environment variable: `user_email = john.doe@example.com`
- The response contains the JWT token directly

**Postman Script (Auto-save token):**
In the **Tests** tab, add:
```javascript
if (pm.response.code === 201) {
    var responseText = pm.response.text();
    var token = responseText.split("Token : ")[1];
    pm.environment.set("jwt_token", token);
    var requestBody = JSON.parse(pm.request.body.raw);
    pm.environment.set("user_email", requestBody.email);
    console.log("Token saved:", token);
    console.log("User email saved:", requestBody.email);
}
```

**⚠️ Important: By default, all registered users get "user" role (not admin)**

**To make a user an Admin, you have two options:**

**Option 1: Update Database After Registration (Recommended)**
1. Register the user normally (as shown above)
2. Note the user's email (e.g., `john.doe@example.com`)
3. Connect to MySQL and run:
```sql
USE retailx;

-- Find the user_id by email
SELECT user_id, email FROM users WHERE email = 'john.doe@example.com';

-- Update the role to admin (replace USER_ID with actual user_id from above query)
-- First, remove the user role (102)
DELETE FROM user_role WHERE user_id = USER_ID AND role_id = 102;

-- Then add admin role (101)
INSERT INTO user_role (user_id, role_id) VALUES (USER_ID, 101);
```

**Option 2: Direct SQL Insert (For First Admin)**
```sql
USE retailx;

-- First, register normally via API, then get the user_id
-- Or create user directly in database (password must be BCrypt encoded)
-- Then assign admin role:
INSERT INTO user_role (user_id, role_id) VALUES (1, 101);
-- Replace 1 with your user_id
```

**After updating to admin:**
- Logout and login again to get new JWT token with admin authority
- The new token will have "admin" authority and can access admin endpoints

**Quick Check:**
To verify a user's role:
```sql
SELECT u.email, r.role_name 
FROM users u
JOIN user_role ur ON u.user_id = ur.user_id
JOIN roles r ON ur.role_id = r.role_id
WHERE u.email = 'john.doe@example.com';
```

---

### 3. Login

**Purpose**: Get JWT token for authenticated requests

**Request Setup:**
- **Method**: `POST`
- **URL**: `{{base_url}}/login`
- **Headers**:
  - `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "jwt-token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Status Code**: `200 OK`

**Postman Script (Auto-save token):**
In the **Tests** tab, add:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("jwt_token", jsonData["jwt-token"]);
    console.log("Token saved:", jsonData["jwt-token"]);
}
```

---

### 4. Create Category (Admin Only)

**Purpose**: Create a product category

**Request Setup:**
- **Method**: `POST`
- **URL**: `{{base_url}}/admin/category`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer {{jwt_token}}`
- **Body** (raw JSON):
```json
{
  "categoryName": "Electronics"
}
```

**Expected Response:**
```json
{
  "categoryId": 1,
  "categoryName": "Electronics"
}
```

**Status Code**: `201 Created`

**Postman Script (Save category ID):**
```javascript
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set("category_id", jsonData.categoryId);
    console.log("Category ID saved:", jsonData.categoryId);
}
```

**Alternative Categories to Create:**
```json
{"categoryName": "Clothing"}
{"categoryName": "Books"}
{"categoryName": "Home & Kitchen"}
{"categoryName": "Sports & Outdoors"}
```

---

### 5. Get All Categories (Public)

**Purpose**: View all available categories

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/public/categories?pageNumber=0&pageSize=10&sortBy=categoryId&sortOrder=asc`
- **Headers**: None required
- **Body**: None

**Query Parameters:**
- `pageNumber`: `0` (default)
- `pageSize`: `10` (default: 5)
- `sortBy`: `categoryId` (default)
- `sortOrder`: `asc` or `desc` (default: asc)

**Expected Response:**
```json
{
  "content": [
    {
      "categoryId": 1,
      "categoryName": "Electronics"
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "lastPage": true
}
```

**Status Code**: `302 Found` (treated as 200 OK)

---

### 6. Add Product (Admin Only)

**Purpose**: Add a product to a category

**Request Setup:**
- **Method**: `POST`
- **URL**: `{{base_url}}/admin/categories/{{category_id}}/product`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer {{jwt_token}}`
- **Body** (raw JSON):
```json
{
  "productName": "iPhone 15 Pro",
  "description": "Latest iPhone with A17 Pro chip, 256GB storage, Titanium design",
  "quantity": 50,
  "price": 999.99,
  "discount": 10.0,
  "specialPrice": 899.99
}
```

**Expected Response:**
```json
{
  "productId": 1,
  "productName": "iPhone 15 Pro",
  "description": "Latest iPhone with A17 Pro chip, 256GB storage, Titanium design",
  "image": null,
  "quantity": 50,
  "price": 999.99,
  "discount": 10.0,
  "specialPrice": 899.99,
  "category": {
    "categoryId": 1,
    "categoryName": "Electronics"
  }
}
```

**Status Code**: `201 Created`

**Postman Script (Save product ID):**
```javascript
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set("product_id", jsonData.productId);
    console.log("Product ID saved:", jsonData.productId);
}
```

**More Product Examples:**

**Laptop:**
```json
{
  "productName": "MacBook Pro 16-inch",
  "description": "M3 Max chip, 36GB RAM, 1TB SSD, Liquid Retina XDR display",
  "quantity": 25,
  "price": 3499.99,
  "discount": 5.0,
  "specialPrice": 3324.99
}
```

**T-Shirt:**
```json
{
  "productName": "Cotton T-Shirt",
  "description": "100% organic cotton, comfortable fit, multiple colors available",
  "quantity": 100,
  "price": 29.99,
  "discount": 15.0,
  "specialPrice": 25.49
}
```

---

### 7. Get All Products (Public)

**Purpose**: Browse all products

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/public/products?pageNumber=0&pageSize=10&sortBy=productId&sortOrder=asc`
- **Headers**: None required
- **Body**: None

**Expected Response:**
```json
{
  "content": [
    {
      "productId": 1,
      "productName": "iPhone 15 Pro",
      "description": "Latest iPhone with A17 Pro chip...",
      "image": null,
      "quantity": 50,
      "price": 999.99,
      "discount": 10.0,
      "specialPrice": 899.99,
      "category": {
        "categoryId": 1,
        "categoryName": "Electronics"
      }
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "lastPage": true
}
```

**Status Code**: `302 Found`

---

### 8. Search Products by Keyword (Public)

**Purpose**: Search products by name/description

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/public/products/keyword/iPhone?pageNumber=0&pageSize=10`
- **Headers**: None required
- **Body**: None

**Replace `iPhone` with any keyword**

**Expected Response:** Same format as Get All Products

---

### 9. Get Products by Category (Public)

**Purpose**: View products in a specific category

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/public/categories/{{category_id}}/products?pageNumber=0&pageSize=10`
- **Headers**: None required
- **Body**: None

**Expected Response:** Same format as Get All Products

---

### 10. Get User Profile

**Purpose**: Get user details (need user ID from registration response or get it from database)

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/public/users/{{user_id}}`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**Note**: You need to get `user_id` from registration response or from database. Alternatively, you can get it from the user list endpoint.

**Expected Response:**
```json
{
  "userId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "mobileNumber": "1234567890",
  "email": "john.doe@example.com",
  "roles": [
    {
      "roleId": 102,
      "roleName": "user"
    }
  ],
  "address": {
    "addressId": 1,
    "street": "123 Main Street",
    "buildingName": "Apartment 4B",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "pincode": "10001"
  },
  "cart": {
    "cartId": 1,
    "totalPrice": 0.0,
    "products": []
  }
}
```

**Status Code**: `302 Found`

**Postman Script (Save cart ID and user ID):**
```javascript
if (pm.response.code === 302) {
    var jsonData = pm.response.json();
    pm.environment.set("user_id", jsonData.userId);
    if (jsonData.cart && jsonData.cart.cartId) {
        pm.environment.set("cart_id", jsonData.cart.cartId);
    }
    console.log("User ID:", jsonData.userId, "Cart ID:", jsonData.cart?.cartId);
}
```

---

### 11. Add Product to Cart

**Purpose**: Add a product to shopping cart

**Request Setup:**
- **Method**: `POST`
- **URL**: `{{base_url}}/public/carts/{{cart_id}}/products/{{product_id}}/quantity/2`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**URL Parameters:**
- `{{cart_id}}`: Your cart ID (from user profile)
- `{{product_id}}`: Product ID to add
- `quantity`: Number of items (e.g., `2`)

**Expected Response:**
```json
{
  "cartId": 1,
  "totalPrice": 1799.98,
  "products": [
    {
      "cartItemId": 1,
      "product": {
        "productId": 1,
        "productName": "iPhone 15 Pro",
        "description": "Latest iPhone...",
        "price": 999.99,
        "discount": 10.0,
        "specialPrice": 899.99
      },
      "quantity": 2,
      "discount": 10.0,
      "productPrice": 899.99
    }
  ]
}
```

**Status Code**: `201 Created`

---

### 12. Get Cart Details

**Purpose**: View current cart contents

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/public/users/{{user_email}}/carts/{{cart_id}}`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**Expected Response:** Same format as Add to Cart response

**Status Code**: `302 Found`

---

### 13. Update Product Quantity in Cart

**Purpose**: Change quantity of a product in cart

**Request Setup:**
- **Method**: `PUT`
- **URL**: `{{base_url}}/public/carts/{{cart_id}}/products/{{product_id}}/quantity/3`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**Expected Response:** Updated cart with new quantity

**Status Code**: `200 OK`

---

### 14. Remove Product from Cart

**Purpose**: Remove a product from cart

**Request Setup:**
- **Method**: `DELETE`
- **URL**: `{{base_url}}/public/carts/{{cart_id}}/product/{{product_id}}`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**Expected Response:**
```
"Product removed from cart successfully"
```

**Status Code**: `200 OK`

---

### 15. Place Order

**Purpose**: Create an order from cart items

**Request Setup:**
- **Method**: `POST`
- **URL**: `{{base_url}}/public/users/{{user_email}}/carts/{{cart_id}}/payments/CREDIT_CARD/order`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**Payment Methods:**
- `CREDIT_CARD`
- `DEBIT_CARD`
- `UPI`
- `CASH_ON_DELIVERY`

**Expected Response:**
```json
{
  "orderId": 1,
  "email": "john.doe@example.com",
  "orderDate": "2025-11-13",
  "totalAmount": 1799.98,
  "orderStatus": "Order Placed",
  "orderItems": [
    {
      "orderItemId": 1,
      "product": {
        "productId": 1,
        "productName": "iPhone 15 Pro",
        "price": 999.99,
        "specialPrice": 899.99
      },
      "quantity": 2,
      "discount": 10.0,
      "orderedProductPrice": 899.99
    }
  ],
  "payment": {
    "paymentId": 1,
    "paymentMethod": "CREDIT_CARD"
  }
}
```

**Status Code**: `201 Created`

**Postman Script (Save order ID):**
```javascript
if (pm.response.code === 201) {
    var jsonData = pm.response.json();
    pm.environment.set("order_id", jsonData.orderId);
    console.log("Order ID saved:", jsonData.orderId);
}
```

---

### 16. Get User Orders

**Purpose**: View all orders for a user

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/public/users/{{user_email}}/orders`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**Expected Response:**
```json
[
  {
    "orderId": 1,
    "email": "john.doe@example.com",
    "orderDate": "2025-11-13",
    "totalAmount": 1799.98,
    "orderStatus": "Order Placed",
    "orderItems": [...],
    "payment": {...}
  }
]
```

**Status Code**: `302 Found`

---

### 17. Get Order Details

**Purpose**: View specific order details

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/public/users/{{user_email}}/orders/{{order_id}}`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**Expected Response:** Single order object (same format as Place Order response)

**Status Code**: `302 Found`

---

### 18. Update Order Status (Admin Only)

**Purpose**: Change order status (Admin only)

**Request Setup:**
- **Method**: `PUT`
- **URL**: `{{base_url}}/admin/users/{{user_email}}/orders/{{order_id}}/orderStatus/Delivered`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}` (Admin token required)
- **Body**: None

**Order Status Values:**
- `Order Placed`
- `Processing`
- `Shipped`
- `Delivered`
- `Cancelled`

**Expected Response:** Updated order object

**Status Code**: `200 OK`

---

### 19. Get All Users (Admin Only)

**Purpose**: View all registered users

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/admin/users?pageNumber=0&pageSize=10&sortBy=userId&sortOrder=asc`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}` (Admin token required)
- **Body**: None

**Expected Response:**
```json
{
  "content": [
    {
      "userId": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      ...
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 1,
  "totalPages": 1,
  "lastPage": true
}
```

**Status Code**: `302 Found`

---

### 20. Get All Orders (Admin Only)

**Purpose**: View all orders in the system

**Request Setup:**
- **Method**: `GET`
- **URL**: `{{base_url}}/admin/orders?pageNumber=0&pageSize=10&sortBy=totalAmount&sortOrder=asc`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}` (Admin token required)
- **Body**: None

**Expected Response:** Paginated list of orders

**Status Code**: `302 Found`

---

### 21. Update Product (Admin Only)

**Purpose**: Modify product details

**Request Setup:**
- **Method**: `PUT`
- **URL**: `{{base_url}}/admin/products/{{product_id}}`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer {{jwt_token}}`
- **Body** (raw JSON):
```json
{
  "productName": "iPhone 15 Pro Max",
  "description": "Updated description with more details",
  "quantity": 75,
  "price": 1099.99,
  "discount": 12.0,
  "specialPrice": 967.99
}
```

**Expected Response:** Updated product object

**Status Code**: `200 OK`

---

### 22. Delete Product (Admin Only)

**Purpose**: Remove a product

**Request Setup:**
- **Method**: `DELETE`
- **URL**: `{{base_url}}/admin/products/{{product_id}}`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**Expected Response:**
```
"Product deleted successfully"
```

**Status Code**: `200 OK`

---

### 23. Update Category (Admin Only)

**Purpose**: Modify category name

**Request Setup:**
- **Method**: `PUT`
- **URL**: `{{base_url}}/admin/categories/{{category_id}}`
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer {{jwt_token}}`
- **Body** (raw JSON):
```json
{
  "categoryName": "Electronics & Gadgets"
}
```

**Expected Response:** Updated category object

**Status Code**: `200 OK`

---

### 24. Delete Category (Admin Only)

**Purpose**: Remove a category

**Request Setup:**
- **Method**: `DELETE`
- **URL**: `{{base_url}}/admin/categories/{{category_id}}`
- **Headers**:
  - `Authorization: Bearer {{jwt_token}}`
- **Body**: None

**Expected Response:**
```
"Category deleted successfully"
```

**Status Code**: `200 OK`

---

## Complete Request Examples

### Quick Test Collection

**1. Register User:**
```
POST http://localhost:8080/api/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "mobileNumber": "1234567890",
  "email": "john.doe@example.com",
  "password": "password123",
  "address": {
    "street": "123 Main Street",
    "buildingName": "Apartment 4B",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "pincode": "10001"
  }
}
```

**2. Login:**
```
POST http://localhost:8080/api/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**3. Create Category:**
```
POST http://localhost:8080/api/admin/category
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE

{
  "categoryName": "Electronics"
}
```

**4. Add Product:**
```
POST http://localhost:8080/api/admin/categories/1/product
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE

{
  "productName": "iPhone 15 Pro",
  "description": "Latest iPhone with A17 Pro chip, 256GB storage",
  "quantity": 50,
  "price": 999.99,
  "discount": 10.0,
  "specialPrice": 899.99
}
```

**5. Add to Cart:**
```
POST http://localhost:8080/api/public/carts/1/products/1/quantity/2
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

**6. Place Order:**
```
POST http://localhost:8080/api/public/users/john.doe@example.com/carts/1/payments/CREDIT_CARD/order
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

---

## Creating Admin User - Complete Guide

**Important**: The registration endpoint always assigns the "user" role (ID: 102) by default. To create an admin user, you need to manually update the database after registration.

### Method 1: Register Then Update Role (Recommended)

**Step 1: Register User Normally**
Use the registration endpoint as shown in section 2 above. This creates a user with "user" role.

**Step 2: Find User ID**
```sql
USE retailx;
SELECT user_id, email, first_name, last_name 
FROM users 
WHERE email = 'your-email@example.com';
```

**Step 3: Update Role to Admin**
```sql
USE retailx;

-- Remove user role (102) and add admin role (101)
-- Replace USER_ID with the actual user_id from Step 2

-- Option A: Replace user role with admin role
DELETE FROM user_role WHERE user_id = USER_ID;
INSERT INTO user_role (user_id, role_id) VALUES (USER_ID, 101);

-- Option B: Add admin role while keeping user role (user will have both roles)
INSERT INTO user_role (user_id, role_id) VALUES (USER_ID, 101)
ON DUPLICATE KEY UPDATE role_id = 101;
```

**Step 4: Verify Role**
```sql
SELECT u.email, u.first_name, r.role_name, r.role_id
FROM users u
JOIN user_role ur ON u.user_id = ur.user_id
JOIN roles r ON ur.role_id = r.role_id
WHERE u.email = 'your-email@example.com';
```

**Step 5: Get New Token**
- Logout (or wait for token to expire)
- Login again using the login endpoint
- The new JWT token will contain "admin" authority
- Use this token for admin endpoints

### Method 2: Create Admin User Directly in Database

**Note**: This method requires BCrypt password encoding. It's easier to register via API first.

```sql
USE retailx;

-- First, register via API to get properly encoded password
-- Then update the role, OR:

-- If you want to create user directly, you need BCrypt encoded password
-- Example BCrypt hash for password "admin123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- (Use online BCrypt generator or register first)

-- After user exists, assign admin role:
INSERT INTO user_role (user_id, role_id) 
SELECT user_id, 101 FROM users WHERE email = 'admin@example.com';
```

### Quick Admin Setup Script

**Complete SQL script to make existing user admin:**
```sql
USE retailx;

-- Replace 'john.doe@example.com' with your actual email
SET @user_email = 'john.doe@example.com';

-- Get user_id
SET @user_id = (SELECT user_id FROM users WHERE email = @user_email);

-- Remove user role, add admin role
DELETE FROM user_role WHERE user_id = @user_id;
INSERT INTO user_role (user_id, role_id) VALUES (@user_id, 101);

-- Verify
SELECT u.email, r.role_name 
FROM users u
JOIN user_role ur ON u.user_id = ur.user_id
JOIN roles r ON ur.role_id = r.role_id
WHERE u.email = @user_email;
```

### Testing Admin Access

After updating role and getting new token:

1. **Test Admin Endpoint:**
```
GET http://localhost:8080/api/admin/users
Authorization: Bearer YOUR_NEW_ADMIN_TOKEN
```

2. **Check Token Authorities:**
The JWT token should contain `"authorities": "admin"` in its payload.
You can decode it at https://jwt.io to verify.

### Common Issues

**Issue**: Still getting 403 Forbidden after updating role
**Solution**: 
- Make sure you logged in again to get a new token
- Old tokens don't have admin authority
- Verify role in database: `SELECT * FROM user_role WHERE user_id = YOUR_USER_ID;`

**Issue**: User has both user and admin roles
**Solution**: This is fine! User can access both user and admin endpoints. If you want only admin:
```sql
DELETE FROM user_role WHERE user_id = USER_ID AND role_id = 102;
```

---

## Postman Collection Import

### Create Collection Manually:

1. Click **Collections** → **+** (New Collection)
2. Name: `RetailX API`
3. Add each request as a folder:
   - **Authentication** (Register, Login)
   - **Categories** (Create, Get, Update, Delete)
   - **Products** (Add, Get, Search, Update, Delete)
   - **Cart** (Add, Get, Update, Delete)
   - **Orders** (Place, Get, Update Status)
   - **Users** (Get Profile, Get All)

### Use Environment Variables:

In URL fields, use: `{{base_url}}/register`
In Headers, use: `Bearer {{jwt_token}}`

---

## Troubleshooting

### Issue 1: 401 Unauthorized
**Solution**: 
- Check JWT token is set in environment
- Ensure token is in format: `Bearer <token>` (with space)
- Token might be expired (24 hours), login again

### Issue 2: 403 Forbidden
**Solution**: 
- Endpoint requires admin role
- Check user has correct role in database
- Use admin token

### Issue 3: 404 Not Found
**Solution**: 
- Check URL is correct
- Verify IDs exist (category_id, product_id, etc.)
- Check server is running on port 8080

### Issue 4: 400 Bad Request
**Solution**: 
- Check JSON format is valid
- Verify required fields are present
- Check validation rules (name length, email format, etc.)

### Issue 5: Token Not Saving
**Solution**: 
- Add Postman scripts in Tests tab
- Check environment is selected
- Verify script syntax is correct

---

## Validation Rules Reference

**User Registration:**
- `firstName`: 5-20 characters, letters only
- `lastName`: 5-20 characters, letters only
- `mobileNumber`: Exactly 10 digits
- `email`: Valid email format
- `password`: Any string
- Address fields: Minimum length requirements

**Product:**
- `productName`: Minimum 3 characters
- `description`: Minimum 6 characters
- `quantity`: Integer
- `price`, `discount`, `specialPrice`: Double

**Category:**
- `categoryName`: Minimum 5 characters

---

## Quick Test Checklist

- [ ] Health check returns "UP"
- [ ] Can register new user
- [ ] Can login and get token
- [ ] Token is saved to environment
- [ ] Can create category (with token)
- [ ] Can add product (with token)
- [ ] Can browse products (no token needed)
- [ ] Can add to cart (with token)
- [ ] Can view cart (with token)
- [ ] Can place order (with token)
- [ ] Can view orders (with token)

---

## Sample Data Sets

### Complete User Registration Examples:

**User 1:**
```json
{
  "firstName": "Alice",
  "lastName": "Johnson",
  "mobileNumber": "9876543210",
  "email": "alice@example.com",
  "password": "secure123",
  "address": {
    "street": "456 Oak Avenue",
    "buildingName": "Suite 200",
    "city": "Los Angeles",
    "state": "CA",
    "country": "USA",
    "pincode": "90001"
  }
}
```

**User 2:**
```json
{
  "firstName": "Bob",
  "lastName": "Smith",
  "mobileNumber": "5551234567",
  "email": "bob@example.com",
  "password": "mypassword",
  "address": {
    "street": "789 Pine Road",
    "buildingName": "Building C",
    "city": "Chicago",
    "state": "IL",
    "country": "USA",
    "pincode": "60601"
  }
}
```

### Product Examples:

**Electronics:**
```json
{
  "productName": "Samsung Galaxy S24",
  "description": "Latest Android flagship with AI features, 256GB, 8GB RAM",
  "quantity": 30,
  "price": 899.99,
  "discount": 8.0,
  "specialPrice": 827.99
}
```

**Clothing:**
```json
{
  "productName": "Denim Jeans",
  "description": "Classic fit, 100% cotton, multiple sizes available",
  "quantity": 150,
  "price": 49.99,
  "discount": 20.0,
  "specialPrice": 39.99
}
```

---

**Happy Testing!** 🚀

For detailed API documentation, visit: `http://localhost:8080/swagger-ui.html`

