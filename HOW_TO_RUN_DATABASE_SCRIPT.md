# How to Run database-init.sql

This guide shows you multiple ways to execute the database initialization script.

## Method 1: MySQL Command Line (Recommended)

### Step 1: Open MySQL Command Line
- **Windows**: Open Command Prompt or PowerShell
- **Mac/Linux**: Open Terminal

### Step 2: Navigate to Project Directory
```bash
cd C:\Users\amarj\Desktop\Eshopee\eshopee
```

### Step 3: Run the Script
```bash
mysql -u root -ptest123 < database-init.sql
```

**Important Notes**:
- No space between `-p` and password: `-ptest123` (not `-p test123`)
- If MySQL is not in your PATH, use full path:
  ```bash
  "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -ptest123 < database-init.sql
  ```

### Step 4: Verify Success
```bash
mysql -u root -ptest123 -e "USE retailx; SELECT * FROM roles;"
```

You should see:
```
+---------+-----------+
| role_id | role_name |
+---------+-----------+
|     101 | admin     |
|     102 | user      |
+---------+-----------+
```

---

## Method 2: MySQL Workbench (GUI Method)

### Step 1: Open MySQL Workbench
- Launch MySQL Workbench application

### Step 2: Connect to MySQL Server
- Click on your local MySQL connection
- Enter password: `test123` (if prompted)

### Step 3: Open SQL Script
- Go to **File** → **Open SQL Script**
- Navigate to: `C:\Users\amarj\Desktop\Eshopee\eshopee\database-init.sql`
- Click **Open**

### Step 4: Execute Script
- Click the **Execute** button (⚡ lightning bolt icon) or press `Ctrl+Shift+Enter`
- Or go to **Query** → **Execute (All or Selection)**

### Step 5: Verify
- In the left panel, refresh the **Schemas** section
- You should see `retailx` database
- Expand `retailx` → `Tables` → `roles`
- Right-click `roles` → **Select Rows - Limit 1000**
- You should see 2 rows: admin (101) and user (102)

---

## Method 3: MySQL Command Line (Interactive)

### Step 1: Login to MySQL
```bash
mysql -u root -ptest123
```

### Step 2: Copy and Paste SQL Commands
Copy the entire content of `database-init.sql` and paste it into the MySQL prompt, then press Enter.

### Step 3: Verify
```sql
USE retailx;
SELECT * FROM roles;
```

### Step 4: Exit MySQL
```sql
EXIT;
```

---

## Method 4: Using Command Line with Source Command

### Step 1: Login to MySQL
```bash
mysql -u root -ptest123
```

### Step 2: Run Source Command
```sql
SOURCE C:/Users/amarj/Desktop/Eshopee/eshopee/database-init.sql
```

**Note**: Use forward slashes `/` even on Windows, or use `\\` for backslashes

### Step 3: Verify
```sql
USE retailx;
SHOW TABLES;
SELECT * FROM roles;
```

---

## Method 5: PowerShell (Windows)

### Step 1: Open PowerShell
- Right-click Start Menu → **Windows PowerShell** or **Terminal**

### Step 2: Navigate to Project
```powershell
cd C:\Users\amarj\Desktop\Eshopee\eshopee
```

### Step 3: Run Script
```powershell
Get-Content database-init.sql | mysql -u root -ptest123
```

---

## Troubleshooting

### Error: "mysql: command not found"
**Solution**: MySQL is not in your system PATH.

**Option A**: Use full path to mysql.exe
```bash
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -ptest123 < database-init.sql
```

**Option B**: Add MySQL to PATH
1. Find MySQL installation: Usually `C:\Program Files\MySQL\MySQL Server 8.0\bin`
2. Add to System Environment Variables → PATH

### Error: "Access denied for user 'root'"
**Solution**: 
- Check password is correct: `test123`
- Try: `mysql -u root -p` (will prompt for password)
- If still fails, reset MySQL root password

### Error: "Unknown database 'retailx'"
**Solution**: This is normal if database doesn't exist. The script will create it. If error persists, check:
- MySQL server is running
- You have CREATE DATABASE privileges

### Error: "Table 'roles' already exists"
**Solution**: This is OK! The script uses `CREATE TABLE IF NOT EXISTS`, so it won't fail. The `ON DUPLICATE KEY UPDATE` in INSERT statements also prevents errors on re-runs.

---

## What the Script Does

1. **Creates database** `retailx` (if it doesn't exist)
2. **Switches to** `retailx` database
3. **Creates** `roles` table (if it doesn't exist)
4. **Inserts** two roles:
   - Role ID 101: `admin`
   - Role ID 102: `user`

## After Running the Script

✅ Database `retailx` is created  
✅ Table `roles` is created  
✅ Two roles are inserted (admin and user)  
✅ Ready to run the Spring Boot application  

The application will automatically create all other tables (users, products, orders, etc.) when you start it because of `spring.jpa.hibernate.ddl-auto=update` in the configuration.

---

## Quick Verification Commands

After running the script, verify everything worked:

```bash
# Check database exists
mysql -u root -ptest123 -e "SHOW DATABASES LIKE 'retailx';"

# Check roles table exists and has data
mysql -u root -ptest123 -e "USE retailx; SELECT * FROM roles;"

# Count roles (should be 2)
mysql -u root -ptest123 -e "USE retailx; SELECT COUNT(*) FROM roles;"
```

Expected output for last command:
```
+----------+
| COUNT(*) |
+----------+
|        2 |
+----------+
```

---

## Next Steps

After successfully running the script:

1. ✅ Database is ready
2. ✅ Roles are initialized
3. 🚀 Start your Spring Boot application:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
4. ✅ Application will create remaining tables automatically
5. ✅ You can now register users and use the API

---

**Need Help?** If you encounter any issues, check:
- MySQL server is running
- Username and password are correct
- You have necessary privileges
- File path is correct

