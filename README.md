# University Management System (ATBMCQ-16)

This is a JavaFX-based University Management System for managing students, courses, and user roles with Oracle Database integration. The application supports different user roles (Admin, Staff, Lecturer, Student) and provides CRUD operations for students and subjects, as well as role-based access control.

## Features

- **User Authentication**: Login with Oracle DB credentials; role-based dashboards for Admin and Client users.
- **Student Management**: Add, update, delete, and view student information.
- **Course Management**: Add, update, delete, and view course information (with role-based permissions).
- **Role Management**: Admin can manage users and roles.
- **Privilege Management**: Admin can manage database privileges directly from the application, including granting and revoking privileges for users and roles.

- **Modern JavaFX UI**: Responsive and user-friendly interface.

## Prerequisites

- **Java 17+** (or compatible with your JavaFX version)
- **Maven** (for dependency management and building)
- **Oracle Database 19c+** (with pluggable database support)
- **Oracle JDBC Driver** (ojdbc8.jar or newer)

## Database Setup

1. **Create the Pluggable Database and Admin User**
   - Run the SQL script at `src/main/resources/db/CSDL.sql` as a SYS user to create the pluggable database and admin user:
     ```sql
     -- Example: In SQL*Plus or SQLcl
     @src/main/resources/db/CSDL.sql
     ```
2. **Create Schema and Sample Data**

   - Connect as `ATBMCQ_ADMIN` and run:
     ```sql
     @src/main/resources/db/PH2_SCHEMA.sql
     @src/main/resources/db/DATA.sql
     ```
   - This will create tables, roles, users, and insert sample data.

3. **(Optional) Run Additional Procedures**
   - If needed, run other scripts in the `db/` folder for stored procedures or extra setup.

## How to Build and Run

### 1. Build the Project

- Open a terminal in the project root and run:
  ```sh
  mvn clean package
  ```
- This will generate a JAR file in the `target/` directory.

### 2. Run the Application

- Make sure your Oracle database is running and accessible at `localhost:1521/ATBMCQ_16_CSDL`.
- Run the application with:
  ```sh
  java -jar target/university_management_system-1.0-SNAPSHOT.jar
  ```
- If you need to specify the JavaFX modules, use:
  ```sh
  java --module-path <path-to-javafx-lib> --add-modules javafx.controls,javafx.fxml -jar target/university_management_system-1.0-SNAPSHOT.jar
  ```

### 3. Login

- Use the credentials created in the SQL scripts (e.g., `ATBMCQ_ADMIN`/`123` or `USER1`/`123`).
- The application will detect your role and show the appropriate dashboard.

## Admin Dashboard

The Admin side of the application is designed for database administrators. It provides a user-friendly interface for managing users, roles, and privileges in the Oracle database, allowing DBAs to perform essential tasks without needing to install Oracle SQL Developer or similar database tools.

## Notes

- **Database Connection**: The app connects to Oracle using the credentials you provide at login.
- **JDBC Driver**: Ensure the Oracle JDBC driver is available in your classpath or Maven dependencies.
- **JavaFX**: If using a JDK without JavaFX bundled, download JavaFX SDK and set the `--module-path` accordingly.

## Project Structure

- `src/main/java/atbmhttt/atbmcq_16/` - Main application code
- `src/main/resources/db/` - SQL scripts for database setup
- `src/main/resources/images/` - App icons and images

## Troubleshooting

- **Login Issues**: Check Oracle DB is running and credentials are correct.
- **JDBC Errors**: Ensure the correct JDBC driver is present.
- **JavaFX Errors**: Make sure JavaFX modules are available to your JVM.

## Contact

For further details, see the code and comments in the respective Java and SQL files. Or if you need more support, please contact the project maintainers:

- [botchigo](https://github.com/botchigo)
- [duongduchuy24](https://github.com/duongduchuy24)
- [CatHuyuH24](https://github.com/CatHuyuH24)

_Note: We may be slow to answer but will do so as soon as possible._
