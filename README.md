## Hibernate GUI Demo Application

### Overview
Simple Java application that demonstrates the use of Hibernate for ORM (Object-Relational Mapping) to interact with a MySQL database. The application includes a graphical user interface (GUI) built using JavaFX.

### Features
- Integrates with a MySQL database using Hibernate
- Supports basic database operations such as adding, updating, and deleting users
- Provides a user-friendly interface for interacting with the database

### Prerequisites
Before running the application, ensure you have the following:
- An empty MySQL database set up, which Hibernate will use to create the necessary tables
- JDBC driver and Hibernate library configured in your project

### Setup Instructions

1. Clone the Repository:
   
   ```bash
   git clone https://github.com/petarmilunovic/hibernate-demo.git
   cd jdbc-demo

2. Add dependencies:
- The repository already contains a pom.xml file with all necessary dependencies, including Hibernate and JavaFX
- If you're not using Maven, download the required libraries [JDBC](https://downloads.mysql.com/archives/c-j/), [Hibernate](https://hibernate.org/orm/releases/6.6/#get-it) and add JAR files to your project's classpath
  
3. Configure Database Connection:
- Create an empty MySQL database
- Update the hibernate.cfg.xml file in the resources folder with your database connection details and credentials

4. Run the Application:
- Compile and run the application from your IDE
- Use the GUI to perform CRUD operations on the database
