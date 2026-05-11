<div align="center">
  <img width="150" alt="icon" src="https://github.com/user-attachments/assets/b19303d2-e919-4115-acea-8fc7eb003bce" />
</div>

# Kharch Backend Application

This is a Spring Boot application named "Kharch". It provides backend services for managing expenses and budgets. The application uses a PostgreSQL database and utilizes JSON Web Tokens (JWT) for authentication.

## Prerequisites

To run this application, ensure you have the following installed on your system:
- **Java 21** or higher
- **Maven** (optional, as the Maven Wrapper `mvnw` is included in the project)
- **PostgreSQL** database
- **Docker** (optional, if you want to run the application in a container)

## Environment Setup

The application relies on environment variables for its configuration, specifically for database connections and security keys.

Ensure you have a `.env` file in the root directory of the project. It should contain the following variables:

```env
DB_URL=jdbc:postgresql://<db_host>:<db_port>/<database>?user=<db_username>&password=<db_password>
DB_USERNAME=<your_database_username>
DB_PASSWORD=<your_database_password>
JWT_SECRET=<your_jwt_secret_key>
SERVER_PORT=8081
```

*(Note: If a `.env` file already exists, ensure the connection details and secrets match your current development environment.)*

## Running the Application Locally

You can run the application directly on your host machine using the Maven Wrapper provided in the project directory.

### Option 1: Using the Spring Boot Maven Plugin

This is the easiest way to run the application during development. Open your terminal in the project root directory and execute:

**On Windows:**
```cmd
mvnw.cmd spring-boot:run
```

**On macOS/Linux:**
```bash
./mvnw spring-boot:run
```

### Option 2: Building and Running the Executable JAR

If you want to package the application and run it as a standalone Java application:

1. **Build the JAR file** by running the Maven `package` goal:

   **On Windows:**
   ```cmd
   mvnw.cmd clean package -DskipTests
   ```

   **On macOS/Linux:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. **Run the generated JAR file** located in the `target` directory:

   ```bash
   java -jar target/kharch-1.0.1.jar
   ```

The application will start and listen for incoming HTTP requests on port `8081` (or the port specified by `SERVER_PORT`).

## Running the Application via Docker

A `Dockerfile` is included to easily containerize the application.

1. **Build the executable JAR file first** (the `Dockerfile` copies the JAR from the `target/` directory):
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. **Build the Docker Image**:
   ```bash
   docker build -t kharch-backend .
   ```

3. **Run the Docker Container** (mapping the ports and passing the `.env` file):
   ```bash
   docker run -d -p 8081:8081 --env-file .env --name kharch-app kharch-backend
   ```

## Tech Stack
- **Java**: 21
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT Authentication

