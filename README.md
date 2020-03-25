# About
Cash House has the purpose of managing one or more savings cashiers, very common at shared home where all the members of the house must contribute money for specific accounts such as energy, water, electricity, food and others. The project is divided into 2 parts, the first is the java server that is responsible for managing all the transactions, flatmates and savings banks, through the server it is possible to make rest requests for all the client-side interfaces. The second part is the mobile application with a friendly interface for the to make operations such as deposit and withdrawal of money in savings cashiers.

## How to Use

### Backend
The server can be easily run on any operating system such as Linux or Windows, for more information visit [here](https://github.com/marcelobojikian/cash-house/tree/master/backend).
#### About
All search, creation, updating and removal requests are made on the server by the user interface such as mobile or web pages, all services use the Rest architecture, see more information [here](https://restfulapi.net/).
To access the server information securely, you have to use Oauth2 authorizations, commonly used as a way for Internet users to grant websites or applications access, see more information [here](https://oauth.net/2/).
##### Dependencies
    1. Download and Install [Java](https://www.oracle.com/java/) 1.7 or above ( recommended )
    2. Download and Install [Maven](https://maven.apache.org/) 3.3+ ( recommended )
##### How to Install
    1. git clone https://github.com/marcelobojikian/cash-house.git
    2. cd backend
    3. mvn package
    4. java -cp target/backend.jar br.com.housecash.backend.App
##### How to Use
When executing the project it is possible to view the documentation of the resources that can be consumed by the link http://localhost:8080/swagger-ui.html, see an example:

<p align="center">
 <img src="images/Swagger2.png" width="600" height="500">
</p>

### Frontend
The mobile application is not available for download  yet.

## Backand project

The project server contains all the services that mobile and web applications can access, such as flatmates, savings cashier and transactions. The system has the option of accessing other accounts in case the user was invited.

### Tools and Technologies to be used
* Use Maven for dependency management.
* Spring Oauth2, Spring Security, JPA. 
* h2database for the database.
* Spring Boot for the server.
* String Test Tools for testing.

### Dependencies and Software with Eclipse

    1. Download and Install JDK
    2. Download and Install Eclipse
    3. Download and Install ?? Plugin Maven for lombok ??
    4. Install Dependencies from pom.xml
    5. Right Click on /src/main/java/br/com/housecash/backend/App.java > Run
    
### Rest documentation

When executing the project it is possible to view the documentation of the resources that can be consumed by the link http://localhost:8080/swagger-ui.html

<p align="center">
 <img src="images/Swagger2.png" width="600" height="500">
</p>

## Mobile project 

In the image below, the mobile application made with react-native is consuming the server, being able to execute search functions and changes in the system's cashiers.

<p align="center">
 <img src="images/transaction.png" width="150" height="300">
 <img src="images/deposit.png" width="150" height="300">
</p>

## Features

* **Dashboard**: The user can access other accounts that were invited. Users can only execute transaction command. 
* **Flatmates**: Each user can manage your roommates (permission denied when invited).
* **Cashier**: Each user can manage your cashiers (permission denied when invited).

## Issues

If face an issue, please notify it [here](https://github.com/marcelobojikian/cash-house/issues) as a new issue.

## License

Cash House is licensed under The MIT License (MIT). Which means that you can use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software. But you always need to state that Cash House is the original author of this template.

Project is developed and maintained by Marcelo Nogueira Bojikian

