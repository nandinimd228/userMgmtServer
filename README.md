# User Management Server
This project is a User Management Server built with Spring Boot, providing RESTful APIs for user account management.

## Description
The User Management Server allows for creating, updating, retrieving, and deleting user accounts. It utilizes JWT for authentication and supports Swagger for API documentation.

## Technologies Used
- Spring Boot
- Spring Security
- JPA
- H2 Database
- JWT
- Swagger
- JUnit & Mockito for testing

## Clone the repo
git clone "https://github.com/nandinimd228/userManagementServer.git"

### Prerequisites
- Java 17
- Maven 4.0.0

### Test the Application
Use command **mvn test.** The test reports are available under /src/main/target/site/jacoco. Run the index.html file to view the test results.
![image](https://github.com/user-attachments/assets/99b0b9f6-0b0d-48fd-8e5f-afbc581659ca)


### Run the Application locally
Use command **mvn spring-boot:run.** Access the application in http://localhost:8080

### API Testing
Postman collections are available to test the APIs. Additionally, Swagger is integrated for API documentation. Access the Swagger UI at the following link:
http://localhost:8080/swagger-ui.html

