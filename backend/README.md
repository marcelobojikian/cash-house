# About
The server can be easily run on any operating system such as Linux or Windows, for more information visit [here](https://github.com/marcelobojikian/cash-house/tree/master/backend).

## Dependencies
    1. Download and Install [Java](https://www.oracle.com/java/) 1.8 or above ( recommended )
    2. Download and Install [Maven](https://maven.apache.org/) 3.6+ ( recommended )
    
## How to Install
    1. git clone https://github.com/marcelobojikian/cash-house.git
    2. cd backend
    3. mvn package
    4. java -cp target/backend.jar br.com.housecash.backend.App

## How to Use
When executing the project it is possible to view the documentation of the resources that can be consumed by the link http://localhost:8080/swagger-ui.html, see an example:

<p align="center">
 <img src="images/Swagger2.png" width="600" height="500">
</p>

## Tools and Technologies to be used
* Use Maven for dependency management.
* Spring Oauth2, Spring Security, JPA. 
* h2database for test.

## Features

* **Dashboard**: The user can access other accounts that were invited. Users can only execute transaction command. 
* **Flatmates**: Each user can manage your roommates (permission denied when invited).
* **Cashier**: Each user can manage your cashiers (permission denied when invited).

## Issues

If face an issue, please notify it [here](https://github.com/marcelobojikian/cash-house/issues) as a new issue.

## License

Cash House is licensed under The MIT License (MIT). Which means that you can use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software. But you always need to state that Cash House is the original author of this template.

Project is developed and maintained by Marcelo Nogueira Bojikian

