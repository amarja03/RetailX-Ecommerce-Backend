-- RetailX Database Initialization Script
-- Run this script in MySQL to initialize the required roles

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS retailx;

-- Use the database
USE retailx;

-- Create roles table if it doesn't exist (required before inserting data)
CREATE TABLE IF NOT EXISTS roles (
    role_id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

-- Insert default roles (required for the application to work)
-- Role ID 101 = Admin, Role ID 102 = User
-- ON DUPLICATE KEY UPDATE ensures script can be run multiple times safely
INSERT INTO roles (role_id, role_name) VALUES (101, 'admin') 
ON DUPLICATE KEY UPDATE role_name = 'admin';

INSERT INTO roles (role_id, role_name) VALUES (102, 'user') 
ON DUPLICATE KEY UPDATE role_name = 'user';

-- Note: The application will automatically create all other tables using Hibernate
-- when you run the application with spring.jpa.hibernate.ddl-auto=update
-- However, roles table is created here to ensure roles exist before first user registration


select * from users;
select * from user_role;

USE retailx;

-- Find the user_id by email
SELECT user_id, email FROM users WHERE email = 'amarja@gmail.com';

-- Update the role to admin (replace USER_ID with actual user_id from above query)
-- First, remove the user role (102)
DELETE FROM user_role WHERE user_id = USER_ID AND role_id = 102;

-- Then add admin role (101)
INSERT INTO user_role (user_id, role_id) VALUES (1, 101);
select * from categories;