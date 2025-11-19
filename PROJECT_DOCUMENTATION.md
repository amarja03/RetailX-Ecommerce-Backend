# RetailX - Complete Project Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Project Setup & Execution](#project-setup--execution)
4. [Database Architecture](#database-architecture)
5. [Project Structure](#project-structure)
6. [Detailed Component Explanation](#detailed-component-explanation)
7. [API Endpoints & Postman Guide](#api-endpoints--postman-guide)
8. [Security Implementation](#security-implementation)
9. [Project Flow](#project-flow)
10. [Database Queries & Relationships](#database-queries--relationships)

---

## Project Overview

**RetailX** is a full-stack e-commerce REST API built with Spring Boot. It provides complete functionality for:
- User registration and authentication (JWT-based)
- Product and category management
- Shopping cart operations
- Order processing and management
- Payment handling
- Address management
- Role-based access control (Admin/User)

---

## Technology Stack

### Backend Framework
- **Spring Boot 3.3.0** - Main framework
- **Java 17** - Programming language

### Database
- **MySQL 8.0+** - Relational database
- **Hibernate/JPA** - ORM (Object-Relational Mapping)

### Security
- **Spring Security** - Authentication & Authorization
- **JWT (JSON Web Tokens)** - Token-based authentication
- **BCrypt** - Password encryption

### Additional Libraries
- **Lombok** - Reduces boilerplate code
- **ModelMapper** - Object-to-object mapping
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **Jakarta Validation** - Input validation

---

## Project Setup & Execution

### Prerequisites
1. **Java 17** installed
2. **MySQL Server** running (username: `root`, password: `test123`)
3. **Maven** (included via Maven Wrapper)
4. **Postman** (for API testing)

### Step-by-Step Setup

#### 1. Database Setup
```sql
-- Open MySQL Command Line or MySQL Workbench
-- Run the database-init.sql file:
mysql -u root -ptest123 < database-init.sql

-- OR manually execute:
CREATE DATABASE IF NOT EXISTS retailx;
USE retailx;

-- Create roles table first (required before inserting data)
CREATE TABLE IF NOT EXISTS roles (
    role_id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

-- Insert default roles (ON DUPLICATE KEY UPDATE allows safe re-runs)
INSERT INTO roles (role_id, role_name) VALUES (101, 'admin') 
ON DUPLICATE KEY UPDATE role_name = 'admin';

INSERT INTO roles (role_id, role_name) VALUES (102, 'user') 
ON DUPLICATE KEY UPDATE role_name = 'user';
```

#### 2. Configuration Check
- Verify `src/main/resources/application-dev.properties`:
  - Database URL: `jdbc:mysql://localhost:3306/retailx`
  - Username: `root`
  - Password: `test123`

#### 3. Build the Project
```bash
# Windows (PowerShell)
.\mvnw.cmd clean install

# Or if Maven is installed globally
mvn clean install
```

#### 4. Run the Application
```bash
# Using Maven Wrapper
.\mvnw.cmd spring-boot:run

# Or using Maven
mvn spring-boot:run

# Or run the main class: EShopeeApplication.java
```

#### 5. Verify Application is Running
- Open browser: `http://localhost:8080/api/health`
- Should return: `{"status":"UP"}`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Database Architecture

### Database Schema Overview

The application uses **10 main tables** with relationships:

#### 1. **users** Table
```sql
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20) NOT NULL,
    mobile_number VARCHAR(10) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
```
**Purpose**: Stores user account information

#### 2. **roles** Table
```sql
CREATE TABLE roles (
    role_id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);
```
**Purpose**: Defines user roles (admin=101, user=102)

#### 3. **user_role** Table (Join Table)
```sql
CREATE TABLE user_role (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);
```
**Purpose**: Many-to-Many relationship between Users and Roles

#### 4. **categories** Table
```sql
CREATE TABLE categories (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(255) NOT NULL
);
```
**Purpose**: Product categories (e.g., Electronics, Clothing)

#### 5. **products** Table
```sql
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    description TEXT NOT NULL,
    quantity INT,
    price DOUBLE,
    discount DOUBLE,
    special_price DOUBLE,
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
```
**Purpose**: Product catalog with pricing and inventory

#### 6. **addresses** Table
```sql
CREATE TABLE addresses (
    address_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    street VARCHAR(255) NOT NULL,
    building_name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    pincode VARCHAR(10) NOT NULL
);
```
**Purpose**: Shipping addresses

#### 7. **user_address** Table (Join Table)
```sql
CREATE TABLE user_address (
    user_id BIGINT,
    address_id BIGINT,
    PRIMARY KEY (user_id, address_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (address_id) REFERENCES addresses(address_id)
);
```
**Purpose**: Many-to-Many relationship (Users can have multiple addresses)

#### 8. **carts** Table
```sql
CREATE TABLE carts (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE,
    total_price DOUBLE DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```
**Purpose**: Shopping cart (One cart per user)

#### 9. **cart_items** Table
```sql
CREATE TABLE cart_items (
    cart_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id BIGINT,
    product_id BIGINT,
    quantity INT,
    discount DOUBLE,
    product_price DOUBLE,
    FOREIGN KEY (cart_id) REFERENCES carts(cart_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
```
**Purpose**: Items in shopping cart

#### 10. **orders** Table
```sql
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    order_date DATE,
    payment_id BIGINT,
    total_amount DOUBLE,
    order_status VARCHAR(50),
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id)
);
```
**Purpose**: Order records

#### 11. **order_items** Table
```sql
CREATE TABLE order_items (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
    product_id BIGINT,
    quantity INT,
    discount DOUBLE,
    ordered_product_price DOUBLE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);
```
**Purpose**: Items in each order

#### 12. **payments** Table
```sql
CREATE TABLE payments (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_method VARCHAR(50) NOT NULL
);
```
**Purpose**: Payment information

### Database Relationships (ER Diagram Concept)

```
Users (1) ──────── (1) Cart
Users (M) ──────── (M) Roles (via user_role)
Users (M) ──────── (M) Addresses (via user_address)
Categories (1) ──── (M) Products
Products (1) ───── (M) CartItems
Products (1) ───── (M) OrderItems
Cart (1) ───────── (M) CartItems
Orders (1) ─────── (M) OrderItems
Orders (1) ─────── (1) Payment
```

---

## Project Structure

```
src/main/java/com/example/eshopee/
├── config/              # Configuration classes
│   ├── AppConstants.java
│   ├── ProjectConfig.java
│   ├── SwaggerConfig.java
│   ├── EShopeeUserDetailsService.java
│   └── EShopeeUsernamePwdAuthenticationProvider.java
├── controllers/         # REST API endpoints
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ProductController.java
│   ├── CategoryController.java
│   ├── CartController.java
│   ├── OrderController.java
│   ├── AddressController.java
│   └── HealthCheckController.java
├── entites/            # JPA Entity classes (Database tables)
│   ├── User.java
│   ├── Role.java
│   ├── Product.java
│   ├── Category.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Payment.java
│   └── Address.java
├── repositories/       # Data Access Layer (JPA Repositories)
│   ├── UserRepo.java
│   ├── RoleRepo.java
│   ├── ProductRepo.java
│   ├── CategoryRepo.java
│   ├── CartRepo.java
│   ├── CartItemRepo.java
│   ├── OrderRepo.java
│   ├── OrderItemRepo.java
│   ├── PaymentRepo.java
│   └── AddressRepo.java
├── services/          # Business Logic Layer
│   ├── UserService.java (interface)
│   ├── UserServiceImpl.java
│   ├── ProductService.java
│   ├── ProductServiceImpl.java
│   ├── CategoryService.java
│   ├── CategoryServiceImpl.java
│   ├── CartService.java
│   ├── CartServiceImpl.java
│   ├── OrderService.java
│   ├── OrderServiceImpl.java
│   ├── AddressService.java
│   ├── AddressServiceImpl.java
│   └── FileService.java
├── payloads/          # DTOs (Data Transfer Objects)
│   ├── UserDTO.java
│   ├── ProductDTO.java
│   ├── CategoryDTO.java
│   ├── CartDTO.java
│   ├── OrderDTO.java
│   └── ... (Response classes)
├── exceptions/        # Custom Exception classes
│   ├── APIException.java
│   ├── ResourceNotFoundException.java
│   ├── UserNotFoundException.java
│   └── MyGlobalExceptionHandler.java
├── filter/           # Security Filters
│   ├── JWTUtil.java
│   └── JWTTokenValidatorFilter.java
└── EShopeeApplication.java  # Main Application Class
```

---

## Detailed Component Explanation

### 1. Main Application Class

**File**: `EShopeeApplication.java`

```java
@SpringBootApplication
@EnableWebSecurity
@EnableScheduling
public class EShopeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(EShopeeApplication.class, args);
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
```

**Annotations Explained**:
- `@SpringBootApplication`: 
  - Combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`
  - Marks this as the main Spring Boot application
  - Enables auto-configuration and component scanning
  
- `@EnableWebSecurity`: 
  - Enables Spring Security for the application
  - Allows custom security configuration
  
- `@EnableScheduling`: 
  - Enables scheduled tasks (if any)
  
- `@Bean`: 
  - Creates a Spring-managed bean
  - `ModelMapper` is used to convert Entity objects to DTOs and vice versa

---

### 2. Configuration Classes

#### A. AppConstants.java
**Purpose**: Centralized constants

```java
public class AppConstants {
    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "5";
    public static final Long ADMIN_ID = 101L;
    public static final Long USER_ID = 102L;
    public static final long JWT_TOKEN_VALIDITY = 86400000; // 24 hours
    public static final String[] PUBLIC_URLS = { "/swagger-ui.html", ... };
    public static final String[] USER_URLS = { "/api/public/**" };
    public static final String[] ADMIN_URLS = { "/api/admin/**" };
    public static final String JWT_SECRET_DEFAULT = "...";
}
```

**Why**: Avoids magic numbers/strings, makes changes easier

#### B. ProjectConfig.java
**Purpose**: Security configuration

```java
@Configuration
public class ProjectConfig {
    
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) {
        // Configures security rules
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(...) {
        // Custom authentication manager
    }
}
```

**Key Methods**:
- `SecurityFilterChain`: Defines which URLs are public/protected
- `PasswordEncoder`: BCrypt for password hashing
- `AuthenticationManager`: Handles login authentication

**Security Rules**:
- Public URLs: Registration, Login, Swagger, Health check
- User URLs: Public product browsing
- Admin URLs: Product/Category management

#### C. SwaggerConfig.java
**Purpose**: API documentation setup

**Access**: `http://localhost:8080/swagger-ui.html`

---

### 3. Entity Classes (Database Tables)

#### A. User Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[a-zA-Z]*$")
    private String firstName;
    
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", ...)
    private Set<Role> roles;
    
    @OneToOne(mappedBy = "user")
    private Cart cart;
}
```

**Annotations Explained**:
- `@Entity`: Marks class as JPA entity (maps to database table)
- `@Table(name = "users")`: Specifies table name
- `@Id`: Primary key
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Auto-increment
- `@Size`: Validation - min/max length
- `@Pattern`: Regex validation
- `@Email`: Email format validation
- `@Column(unique = true)`: Database unique constraint
- `@ManyToMany`: Many users can have many roles
- `@JoinTable`: Specifies join table name
- `@OneToOne`: One user has one cart
- `@FetchType.EAGER`: Load related data immediately

#### B. Product Entity

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToMany(mappedBy = "product")
    private List<CartItem> products;
}
```

**Relationships**:
- Many Products belong to One Category (`@ManyToOne`)
- One Product can be in Many CartItems (`@OneToMany`)

#### C. Cart Entity

```java
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "cart", orphanRemoval = true)
    private List<CartItem> cartItems;
    
    private Double totalPrice = 0.0;
}
```

**Key Points**:
- `orphanRemoval = true`: If cart is deleted, cart items are deleted too
- One-to-One with User (each user has one cart)

#### D. Order Entity

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    
    @Email
    @Column(nullable = false)
    private String email;
    
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
    
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
    
    private LocalDate orderDate;
    private Double totalAmount;
    private String orderStatus;
}
```

---

### 4. Repository Layer (Data Access)

**Purpose**: Interface between application and database

#### Example: UserRepo.java

```java
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u JOIN FETCH u.addresses a WHERE a.addressId = ?1")
    List<User> findByAddress(Long addressId);
}
```

**Annotations**:
- `@Repository`: Marks as Spring Data repository
- `extends JpaRepository<User, Long>`: 
  - Provides CRUD operations automatically
  - First parameter: Entity type
  - Second parameter: Primary key type

**Methods**:
- `findByEmail()`: Spring Data JPA automatically generates SQL query
- `@Query`: Custom JPQL (Java Persistence Query Language) query
- `JOIN FETCH`: Eagerly loads addresses to avoid N+1 problem

**Available Methods (from JpaRepository)**:
- `save(entity)` - Insert or Update
- `findById(id)` - Find by primary key
- `findAll()` - Get all records
- `deleteById(id)` - Delete record
- `count()` - Count records

---

### 5. Service Layer (Business Logic)

**Pattern**: Interface + Implementation

#### Example: UserService Interface

```java
public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    UserDTO getUserById(Long userId);
    UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    UserDTO updateUser(Long userId, UserDTO userDTO);
    String deleteUser(Long userId);
}
```

#### UserServiceImpl Implementation

```java
@Transactional
@Service
class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        // 1. Convert DTO to Entity
        User user = modelMapper.map(userDTO, User.class);
        
        // 2. Create cart for user
        Cart cart = new Cart();
        user.setCart(cart);
        
        // 3. Assign default role (USER)
        Role role = roleRepo.findById(AppConstants.USER_ID).get();
        user.getRoles().add(role);
        
        // 4. Handle address
        Address address = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(...);
        if (address == null) {
            address = new Address(...);
        }
        user.setAddresses(List.of(address));
        
        // 5. Save user
        User registeredUser = userRepo.save(user);
        
        // 6. Convert Entity back to DTO
        return modelMapper.map(registeredUser, UserDTO.class);
    }
}
```

**Annotations**:
- `@Service`: Marks as Spring service (business logic layer)
- `@Transactional`: 
  - Ensures database operations are atomic
  - If any operation fails, all changes are rolled back
- `@Autowired`: Spring dependency injection (automatically provides dependencies)

**ModelMapper**: Converts between Entity and DTO
- Entity: Database representation (has all JPA annotations)
- DTO: Data Transfer Object (clean data for API responses)

**Why DTOs?**:
- Hide sensitive data (passwords)
- Control what data is exposed
- Prevent circular references in JSON

---

### 6. Controller Layer (REST API)

**Purpose**: Handle HTTP requests and responses

#### Example: ProductController.java

```java
@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(
        @Valid @RequestBody Product product, 
        @PathVariable Long categoryId) {
        
        ProductDTO savedProduct = productService.addProduct(categoryId, product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }
    
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
        @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
        @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
        @RequestParam(name = "sortBy", defaultValue = "productId") String sortBy,
        @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder) {
        
        ProductResponse productResponse = productService.getAllProducts(
            pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }
}
```

**Annotations Explained**:
- `@RestController`: 
  - Combines `@Controller` + `@ResponseBody`
  - Automatically converts return values to JSON
  
- `@RequestMapping("/api")`: 
  - Base URL for all endpoints in this controller
  - Full URL: `http://localhost:8080/api/...`
  
- `@PostMapping("/admin/categories/{categoryId}/product")`: 
  - HTTP POST method
  - `{categoryId}` is a path variable
  
- `@GetMapping("/public/products")`: 
  - HTTP GET method
  - Public endpoint (no authentication required)
  
- `@PathVariable`: 
  - Extracts value from URL path
  - Example: `/api/admin/categories/5/product` → `categoryId = 5`
  
- `@RequestParam`: 
  - Extracts query parameters
  - Example: `/api/public/products?pageNumber=0&pageSize=10`
  
- `@RequestBody`: 
  - Converts JSON request body to Java object
  
- `@Valid`: 
  - Triggers validation annotations (@Size, @Email, etc.)
  
- `@Autowired`: 
  - Injects ProductService dependency
  
- `ResponseEntity<T>`: 
  - Wraps response with HTTP status code
  - `HttpStatus.CREATED` = 201
  - `HttpStatus.FOUND` = 302 (but used as 200 here)

**HTTP Methods**:
- `GET`: Retrieve data
- `POST`: Create new resource
- `PUT`: Update existing resource
- `DELETE`: Remove resource

---

### 7. Security Implementation

#### A. JWT (JSON Web Token) Flow

**1. Login Process**:
```
User → POST /api/login → AuthController
     → AuthenticationManager validates credentials
     → If valid → JWTUtil.generateToken()
     → Returns JWT token
```

**2. Token Generation** (JWTUtil.java):

```java
@Component
public class JWTUtil {
    public String generateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String authority = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        
        String secret = AppConstants.JWT_SECRET_DEFAULT;
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
            .issuer("RetailX")
            .subject("JWT Token")
            .claim("username", email)
            .claim("authorities", authority)
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + AppConstants.JWT_TOKEN_VALIDITY))
            .signWith(secretKey)
            .compact();
    }
}
```

**Token Structure**:
- **Header**: Algorithm and token type
- **Payload**: 
  - `username`: User email
  - `authorities`: User roles (admin/user)
  - `issuedAt`: Creation time
  - `expiration`: Expiry time (24 hours)
- **Signature**: Signed with secret key

**3. Token Validation** (JWTTokenValidatorFilter.java):

```java
public class JWTTokenValidatorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) {
        String jwt = request.getHeader("Authorization");
        
        if (jwt != null) {
            // 1. Extract token from header
            // 2. Verify signature with secret key
            // 3. Extract username and authorities
            // 4. Set authentication in SecurityContext
        }
        
        filterChain.doFilter(request, response);
    }
}
```

**How it works**:
- Filter runs before every request
- Extracts JWT from `Authorization` header
- Validates token signature
- Extracts user info and sets authentication
- Allows request to proceed

#### B. Password Encryption

**BCrypt Password Encoder**:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Usage:
String encodedPassword = passwordEncoder.encode("plainPassword");
boolean matches = passwordEncoder.matches("plainPassword", encodedPassword);
```

**Why BCrypt?**:
- One-way hashing (cannot decrypt)
- Salted (each hash is unique)
- Slow (prevents brute force attacks)

---

### 8. Exception Handling

**Global Exception Handler** (MyGlobalExceptionHandler.java):

```java
@RestControllerAdvice
public class MyGlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFoundException(
        ResourceNotFoundException ex) {
        String message = ex.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> handleAPIException(APIException ex) {
        String message = ex.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
```

**Annotations**:
- `@RestControllerAdvice`: Global exception handler for all controllers
- `@ExceptionHandler`: Handles specific exception types

**Custom Exceptions**:
- `ResourceNotFoundException`: When entity not found
- `APIException`: General API errors
- `UserNotFoundException`: User-specific errors

---

## API Endpoints & Postman Guide

### Base URL
```
http://localhost:8080/api
```

### Authentication Flow

#### 1. Register New User
**Endpoint**: `POST /api/register`

**Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "mobileNumber": "1234567890",
  "email": "john@example.com",
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

**Response** (201 Created):
```
Token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Postman Steps**:
1. Create new request
2. Method: POST
3. URL: `http://localhost:8080/api/register`
4. Headers tab → Add: `Content-Type: application/json`
5. Body tab → Select "raw" → Select "JSON"
6. Paste JSON body
7. Click "Send"

---

#### 2. Login
**Endpoint**: `POST /api/login`

**Request Body**:
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response** (200 OK):
```json
{
  "jwt-token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Important**: Copy this token for authenticated requests!

---

### Using JWT Token in Postman

**Method 1: Authorization Header**
1. In Postman request, go to "Authorization" tab
2. Type: Select "Bearer Token"
3. Token: Paste your JWT token

**Method 2: Manual Header**
1. Go to "Headers" tab
2. Add header:
   - Key: `Authorization`
   - Value: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

---

### Complete API Endpoints

#### Authentication Endpoints (Public)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/register` | Register new user | No |
| POST | `/api/login` | User login | No |
| GET | `/api/health` | Health check | No |

#### Category Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/admin/category` | Create category | Admin |
| GET | `/api/public/categories` | Get all categories | No |
| PUT | `/api/admin/categories/{categoryId}` | Update category | Admin |
| DELETE | `/api/admin/categories/{categoryId}` | Delete category | Admin |

**Example: Create Category** (Admin only):
```
POST http://localhost:8080/api/admin/category
Headers: Authorization: Bearer <admin_token>
Body:
{
  "categoryName": "Electronics"
}
```

**Example: Get All Categories** (Public):
```
GET http://localhost:8080/api/public/categories?pageNumber=0&pageSize=10&sortBy=categoryId&sortOrder=asc
```

#### Product Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/admin/categories/{categoryId}/product` | Add product | Admin |
| GET | `/api/public/products` | Get all products | No |
| GET | `/api/public/categories/{categoryId}/products` | Get products by category | No |
| GET | `/api/public/products/keyword/{keyword}` | Search products | No |
| PUT | `/api/admin/products/{productId}` | Update product | Admin |
| PUT | `/api/admin/products/{productId}/image` | Update product image | Admin |
| DELETE | `/api/admin/products/{productId}` | Delete product | Admin |

**Example: Add Product** (Admin):
```
POST http://localhost:8080/api/admin/categories/1/product
Headers: Authorization: Bearer <admin_token>
Body:
{
  "productName": "iPhone 15",
  "description": "Latest iPhone with advanced features",
  "quantity": 50,
  "price": 999.99,
  "discount": 10.0,
  "specialPrice": 899.99
}
```

**Example: Search Products**:
```
GET http://localhost:8080/api/public/products/keyword/phone?pageNumber=0&pageSize=5
```

#### Cart Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/public/carts/{cartId}/products/{productId}/quantity/{quantity}` | Add to cart | User/Admin |
| GET | `/api/public/users/{emailId}/carts/{cartId}` | Get cart | User/Admin |
| PUT | `/api/public/carts/{cartId}/products/{productId}/quantity/{quantity}` | Update quantity | User/Admin |
| DELETE | `/api/public/carts/{cartId}/product/{productId}` | Remove from cart | User/Admin |
| GET | `/api/admin/carts` | Get all carts (Admin) | Admin |

**Example: Add Product to Cart**:
```
POST http://localhost:8080/api/public/carts/1/products/5/quantity/2
Headers: Authorization: Bearer <user_token>
```

**Note**: Get `cartId` from user profile after registration.

#### Order Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/public/users/{emailId}/carts/{cartId}/payments/{paymentMethod}/order` | Place order | User/Admin |
| GET | `/api/public/users/{emailId}/orders` | Get user orders | User/Admin |
| GET | `/api/public/users/{emailId}/orders/{orderId}` | Get order details | User/Admin |
| GET | `/api/admin/orders` | Get all orders | Admin |
| PUT | `/api/admin/users/{emailId}/orders/{orderId}/orderStatus/{orderStatus}` | Update order status | Admin |

**Example: Place Order**:
```
POST http://localhost:8080/api/public/users/john@example.com/carts/1/payments/CREDIT_CARD/order
Headers: Authorization: Bearer <user_token>
```

**Payment Methods**: `CREDIT_CARD`, `DEBIT_CARD`, `UPI`, `CASH_ON_DELIVERY`

#### User Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/admin/users` | Get all users | Admin |
| GET | `/api/public/users/{userId}` | Get user by ID | User/Admin |
| PUT | `/api/public/users/{userId}` | Update user | User/Admin |
| DELETE | `/api/admin/users/{userId}` | Delete user | Admin |

---

### Postman Collection Setup

**Step 1: Create Environment**
1. Click "Environments" → "Create Environment"
2. Name: "RetailX Local"
3. Add variables:
   - `base_url`: `http://localhost:8080/api`
   - `jwt_token`: (leave empty, will be set after login)

**Step 2: Create Collection**
1. Click "Collections" → "New Collection"
2. Name: "RetailX API"

**Step 3: Add Requests**
1. Right-click collection → "Add Request"
2. Name each request appropriately
3. Use `{{base_url}}` in URLs
4. For authenticated requests, use `{{jwt_token}}` in Authorization header

**Step 4: Auto-save Token**
1. Create "Login" request
2. In "Tests" tab, add:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("jwt_token", jsonData["jwt-token"]);
}
```
3. After login, token is automatically saved to environment

---

## Project Flow

### Complete User Journey

#### 1. Registration Flow
```
User → POST /api/register
     → AuthController.registerHandler()
     → Password encoded with BCrypt
     → UserService.registerUser()
     → UserRepo.save() → Database
     → Cart created automatically
     → Role assigned (USER)
     → JWT token generated
     → Token returned to user
```

#### 2. Login Flow
```
User → POST /api/login (email + password)
     → AuthController.loginHandler()
     → AuthenticationManager.authenticate()
     → EShopeeUsernamePwdAuthenticationProvider
     → Loads user from database
     → Compares password (BCrypt)
     → If valid → JWT token generated
     → Token returned
```

#### 3. Browse Products Flow
```
User → GET /api/public/products
     → ProductController.getAllProducts()
     → ProductService.getAllProducts()
     → ProductRepo.findAll() with pagination
     → Products converted to DTOs
     → JSON response returned
```

#### 4. Add to Cart Flow
```
User → POST /api/public/carts/{cartId}/products/{productId}/quantity/{quantity}
     → CartController.addProductToCart()
     → CartService.addProductToCart()
     → Validates cart and product exist
     → Creates/updates CartItem
     → Updates cart totalPrice
     → CartRepo.save()
     → Returns updated cart
```

#### 5. Place Order Flow
```
User → POST /api/public/users/{emailId}/carts/{cartId}/payments/{paymentMethod}/order
     → OrderController.orderProducts()
     → OrderService.placeOrder()
     → Loads cart and cart items
     → Creates Order entity
     → Creates OrderItems from CartItems
     → Creates Payment entity
     → Calculates total amount
     → Saves Order, OrderItems, Payment
     → Clears cart (deletes cart items)
     → Returns order details
```

#### 6. Admin Operations Flow
```
Admin → GET /api/admin/products
      → ProductController (requires "admin" authority)
      → JWTTokenValidatorFilter validates token
      → Extracts authorities from token
      → Checks if "admin" authority exists
      → If yes → Request proceeds
      → If no → 403 Forbidden
```

---

## Database Queries & Relationships

### Important SQL Queries for Understanding

#### 1. Find User with Roles
```sql
SELECT u.*, r.role_name
FROM users u
JOIN user_role ur ON u.user_id = ur.user_id
JOIN roles r ON ur.role_id = r.role_id
WHERE u.email = 'john@example.com';
```

**What this shows**:
- User details
- All roles assigned to user
- Many-to-Many relationship via `user_role` table

#### 2. Get User Cart with Items
```sql
SELECT c.*, ci.*, p.product_name, p.price
FROM carts c
JOIN cart_items ci ON c.cart_id = ci.cart_id
JOIN products p ON ci.product_id = p.product_id
WHERE c.user_id = 1;
```

**What this shows**:
- Cart details
- All items in cart
- Product information for each item
- One-to-Many: Cart → CartItems

#### 3. Get Order with Items and Payment
```sql
SELECT o.*, oi.*, p.product_name, pay.payment_method
FROM orders o
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.product_id
JOIN payments pay ON o.payment_id = pay.payment_id
WHERE o.email = 'john@example.com';
```

**What this shows**:
- Order details
- All items in order
- Product information
- Payment method
- Complete order history

#### 4. Products by Category
```sql
SELECT p.*, c.category_name
FROM products p
JOIN categories c ON p.category_id = c.category_id
WHERE c.category_id = 1;
```

**What this shows**:
- All products in a category
- Many-to-One: Products → Category

#### 5. User Addresses
```sql
SELECT u.email, a.*
FROM users u
JOIN user_address ua ON u.user_id = ua.user_id
JOIN addresses a ON ua.address_id = a.address_id
WHERE u.user_id = 1;
```

**What this shows**:
- User can have multiple addresses
- Many-to-Many relationship

### Database Relationships Summary

1. **User ↔ Role**: Many-to-Many
   - One user can have multiple roles
   - One role can be assigned to multiple users
   - Join table: `user_role`

2. **User ↔ Cart**: One-to-One
   - Each user has exactly one cart
   - Each cart belongs to one user

3. **User ↔ Address**: Many-to-Many
   - User can have multiple addresses
   - Address can belong to multiple users (shared addresses)
   - Join table: `user_address`

4. **Category ↔ Product**: One-to-Many
   - One category has many products
   - One product belongs to one category

5. **Product ↔ CartItem**: One-to-Many
   - One product can be in multiple cart items
   - One cart item references one product

6. **Cart ↔ CartItem**: One-to-Many
   - One cart has many cart items
   - One cart item belongs to one cart

7. **Order ↔ OrderItem**: One-to-Many
   - One order has many order items
   - One order item belongs to one order

8. **Order ↔ Payment**: One-to-One
   - One order has one payment
   - One payment belongs to one order

9. **Product ↔ OrderItem**: One-to-Many
   - One product can be in multiple order items
   - One order item references one product

---

## Key Concepts for SQL Developer Role

### 1. Database Design Patterns

**Normalization**:
- Tables are normalized to avoid data redundancy
- Foreign keys maintain referential integrity
- Join tables handle many-to-many relationships

**Indexes** (Implicit):
- Primary keys are automatically indexed
- Foreign keys should be indexed for performance
- Unique constraints create indexes

### 2. JPA/Hibernate Queries

**JPQL (Java Persistence Query Language)**:
```java
@Query("SELECT u FROM User u JOIN FETCH u.addresses WHERE u.email = ?1")
User findByEmailWithAddresses(String email);
```

**Native SQL**:
```java
@Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
User findByEmailNative(String email);
```

**Method Name Queries**:
```java
// Spring Data JPA automatically generates:
Optional<User> findByEmail(String email);
List<User> findByFirstNameAndLastName(String firstName, String lastName);
```

### 3. Transaction Management

**@Transactional**:
- Ensures ACID properties
- All operations succeed or all fail
- Prevents partial updates

**Example**:
```java
@Transactional
public OrderDTO placeOrder(...) {
    // If any step fails, entire transaction rolls back
    Order order = new Order();
    orderRepo.save(order);
    
    Payment payment = new Payment();
    paymentRepo.save(payment);
    
    // If this fails, order and payment are rolled back
    orderItemRepo.save(orderItem);
}
```

### 4. Pagination & Sorting

**Spring Data JPA Pagination**:
```java
Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
Page<Product> products = productRepo.findAll(pageable);
```

**SQL Equivalent**:
```sql
SELECT * FROM products 
ORDER BY product_id ASC 
LIMIT 5 OFFSET 0;
```

---

## Common Issues & Solutions

### Issue 1: Database Connection Error
**Error**: `Communications link failure`

**Solution**:
1. Check MySQL is running: `mysql -u root -ptest123`
2. Verify database exists: `SHOW DATABASES;`
3. Check connection URL in `application-dev.properties`
4. Ensure MySQL port is 3306

### Issue 2: Roles Not Found
**Error**: `No value present` when registering user

**Solution**:
1. Run `database-init.sql` script
2. Verify roles exist: `SELECT * FROM roles;`
3. Should have role_id 101 (admin) and 102 (user)

### Issue 3: JWT Token Invalid
**Error**: `Invalid Token`

**Solution**:
1. Ensure token is in `Authorization` header
2. Format: `Bearer <token>` (with space)
3. Token expires after 24 hours, login again
4. Check token hasn't been tampered with

### Issue 4: 403 Forbidden
**Error**: Access denied

**Solution**:
1. User doesn't have required role
2. Check user roles in database
3. Ensure JWT token contains correct authorities
4. Verify endpoint requires correct role (admin vs user)

### Issue 5: Validation Errors
**Error**: `Validation failed`

**Solution**:
1. Check request body matches entity validation rules
2. Email must be valid format
3. Names must be 5-20 characters, letters only
4. Mobile number must be exactly 10 digits

---

## Testing Checklist

### Before Interview

1. ✅ Application starts without errors
2. ✅ Database connection successful
3. ✅ Can register new user
4. ✅ Can login and get JWT token
5. ✅ Can browse products (public endpoint)
6. ✅ Can add products to cart (with token)
7. ✅ Can place order (with token)
8. ✅ Admin endpoints work (with admin token)
9. ✅ Swagger UI accessible
10. ✅ Health check returns UP

### Postman Test Flow

1. **Register User** → Get token
2. **Login** → Get new token
3. **Get All Categories** (Public)
4. **Create Category** (Admin - need admin user)
5. **Add Product** (Admin)
6. **Get All Products** (Public)
7. **Add to Cart** (User)
8. **Get Cart** (User)
9. **Place Order** (User)
10. **Get Orders** (User)

---

## Summary

**RetailX** is a production-ready e-commerce API demonstrating:
- RESTful API design
- JWT-based authentication
- Role-based access control
- Database relationships (1:1, 1:M, M:M)
- Pagination and sorting
- Exception handling
- Input validation
- Clean architecture (Controller → Service → Repository)

**Key Technologies**:
- Spring Boot 3.3.0
- MySQL 8.0
- JWT Security
- JPA/Hibernate
- Maven

**Database Skills Demonstrated**:
- Entity relationships
- Foreign keys
- Join tables
- Query optimization
- Transaction management
- Data normalization

This project showcases both backend development and database design skills, perfect for a SQL Developer role interview!

---

## Quick Reference

**Start Application**: `.\mvnw.cmd spring-boot:run`

**Base URL**: `http://localhost:8080/api`

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

**Health Check**: `http://localhost:8080/api/health`

**Database**: MySQL on `localhost:3306`, database: `retailx`, user: `root`, password: `test123`

**JWT Token Validity**: 24 hours (86400000 milliseconds)

**Default Pagination**: Page 0, Size 5

---

**Good luck with your interview!** 🚀

