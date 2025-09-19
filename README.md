# FinSight: Intelligent Personal Finance & Expense Management

**FinSight** is a robust, full-featured personal expense tracking application engineered to provide users with deep insights into their financial landscape. It empowers users to meticulously manage their expenses, create and adhere to budgets, collaborate on group expenditures, and visualize their spending patterns through a dynamic and intuitive dashboard.

---

## 🚀 Key Features

* **Secure User Authentication**: Employs a secure, token-based authentication system using **JSON Web Tokens (JWT)**, ensuring that user data is always protected.

* **Comprehensive Expense Management**:
    * **CRUD Operations**: Full support for creating, retrieving, updating, and deleting expenses.
    * **Bulk Operations**: Add multiple expenses in a single request for enhanced efficiency.
    * **Advanced Filtering**: Dynamically filter expenses based on a variety of criteria, including category, date range, and keywords, using a flexible specification-based filtering system.
    * **CSV Export**: Seamlessly export expense data to a CSV file for offline analysis or record-keeping.

* **Intelligent Budgeting**:
    * **Categorical Budgeting**: Set monthly budgets for specific expense categories to gain granular control over spending.
    * **Budget Summaries**: Monitor spending against your budgets with detailed summaries that show your progress and remaining funds.

* **Automated Recurring Expenses**:
    * **Set It and Forget It**: Schedule recurring expenses (such as subscriptions, rent, or bills) to be logged automatically, ensuring you never miss a payment.
    * **Scheduled Job Processing**: A daily scheduled job (`@Scheduled`) runs to automatically generate expenses from your recurring templates.

* **Collaborative Group Expense Management**:
    * **Shared Expense Pools**: Create groups to manage shared expenses with friends, family, or colleagues.
    * **Member Management**: Easily add or remove members and track who has paid for what.
    * **Transparent Tracking**: All group expenses are clearly attributed to the paying member, providing a transparent view of group finances.

* **Insightful Dashboard & Analytics**:
    * **At-a-Glance Summary**: A powerful dashboard provides a comprehensive overview of your financial health, including total spending for the current and previous months.
    * **Visualized Spending**: Analyze your spending habits with clear visualizations of expenses by category.
    * **Trend Analysis**: Track your spending trends over time with monthly summaries, helping you to identify patterns and make informed financial decisions.

---

## 🛠️ Technologies & Architecture

This project is built on a modern, robust technology stack, following best practices for building scalable and maintainable backend systems.

* **Backend**:
    * **Java 21**: Leveraging the latest features of the Java language.
    * **Spring Boot 3**: For building robust, production-grade RESTful APIs.
* **Data Management**:
    * **Spring Data JPA & Hibernate**: For powerful and efficient object-relational mapping.
    * **H2 In-Memory Database**: For a lightweight and fast development environment.
* **Security**:
    * **Spring Security**: For comprehensive and customizable authentication and access control.
    * **JWT (JSON Web Tokens)**: For secure, stateless authentication.
* **API & Documentation**:
    * **RESTful Architecture**: A clean and consistent RESTful API design.
* **Build & Dependency Management**:
    * **Maven**: For efficient project building and dependency management.
* **Code Quality & Utilities**:
    * **Lombok**: To eliminate boilerplate code and improve readability.
    * **ModelMapper**: For intelligent and flexible object mapping between DTOs and entities.
    * **OpenCSV**: For robust CSV file generation.

---

## 🏛️ Architectural Overview

FinSight is designed with a classic **layered architecture**, promoting a clean separation of concerns and making the application easy to test, maintain, and scale.

* **Controller Layer**: The entry point for all API requests, responsible for handling HTTP requests and responses and delegating business logic to the service layer.
* **Service Layer**: Contains the core business logic of the application. It is divided into interfaces (`abstractclass`) and implementations (`implclass`) to promote loose coupling and easy testing.
* **Repository Layer**: The data access layer, built using Spring Data JPA. It provides a clean abstraction over the database, allowing for easy data manipulation.
* **Domain Model**: A rich domain model with JPA entities (`model`) that accurately represent the core concepts of the application.
* **DTO Layer**: A dedicated layer of Data Transfer Objects (`payload`) to ensure a clean separation between the internal domain model and the external API, preventing data leakage and providing a stable API contract.

---

## 🚀 Getting Started

### Prerequisites

* **JDK 21** or newer
* **Maven 3.9** or newer
* A modern IDE such as **IntelliJ IDEA** or **VS Code**

### Installation & Execution

1.  **Clone the Repository**:
    ```bash
    git clone [https://github.com/harry121199/finsight.git](https://github.com/harry121199/finsight.git)
    cd finsight/Backend/ExpenseTracker
    ```

2.  **Build with Maven**:
    ```bash
    mvn clean install
    ```

3.  **Run the Application**:
    ```bash
    mvn spring-boot:run
    ```

The FinSight API will be up and running on `http://localhost:8080`.

### Database Access

* FinSight uses an **H2 in-memory database**, which requires no external setup.
* The **H2 console** is enabled for easy database inspection and can be accessed at: `http://localhost:8080/h2-console`
    * **JDBC URL**: `jdbc:h2:mem:expensedb`
    * **Username**: `sa`
    * **Password**: `sa`

---

## 🔮 Future Enhancements

* **Frontend Integration**: Develop a modern frontend using a framework like React or Angular to provide a rich user experience.
* **Data Visualization**: Enhance the dashboard with more advanced data visualizations, such as interactive charts and graphs.
* **Third-Party Integrations**: Integrate with financial institutions to automatically import transaction data.
* **Mobile Application**: Develop a native mobile application for iOS and Android for on-the-go expense tracking.
* **Machine Learning**: Implement machine learning models to provide personalized financial advice and spending predictions.

---
## 🌐 Future Architecture: Microservices

As FinSight grows in complexity and user base, transitioning from a monolithic architecture to a **microservices architecture** will be crucial for scalability, resilience, and maintainability. This will involve breaking down the application into smaller, independently deployable services, each responsible for a specific business capability.

### Proposed Microservices

* **Authentication Service**: A dedicated service for managing user authentication, registration, and JWT generation.
* **User Profile Service**: Manages user profiles, including personal information and preferences.
* **Expense Management Service**: Handles all aspects of expense tracking, including individual and recurring expenses.
* **Budgeting Service**: Manages the creation, tracking, and analysis of budgets.
* **Group Service**: Manages group expenses, members, and shared financial data.
* **Analytics & Reporting Service**: Powers the dashboard and generates financial reports and insights.
* **Notification Service**: Sends notifications to users about budget alerts, upcoming recurring expenses, and other important events.

### Microservices Ecosystem Technologies

* **API Gateway**: A single entry point for all client requests, routing them to the appropriate microservice. **Spring Cloud Gateway** would be an excellent choice.
* **Service Discovery**: Allows services to find and communicate with each other dynamically. **Eureka** or **Consul** can be used for this purpose.
* **Configuration Management**: Centralizes configuration for all microservices. **Spring Cloud Config** provides a robust solution.
* **Inter-Service Communication**:
    * **Synchronous**: REST APIs or gRPC for direct service-to-service communication.
    * **Asynchronous**: A message broker like **RabbitMQ** or **Apache Kafka** for event-driven communication, promoting loose coupling and resilience.
* **Containerization & Orchestration**: **Docker** for containerizing each microservice and **Kubernetes** for orchestrating and managing the containerized applications, ensuring high availability and scalability.
* **Monitoring & Logging**: A centralized logging and monitoring solution, such as the **ELK Stack (Elasticsearch, Logstash, Kibana)** or **Prometheus and Grafana**, to provide visibility into the health and performance of the microservices ecosystem.

This microservices approach will enable FinSight to evolve into a highly scalable, resilient, and flexible platform, capable of supporting a large and active user base.
