# MSA Ecommerce
- [MSA Ecommerce] API Gateway
![apigateway](https://user-images.githubusercontent.com/42602972/165477345-dea2e100-d6d6-4bc2-96df-1d6b9183198c.png)

## 프로젝트 개발 구성
- Java 8
- Spring Boot(+Maven) 2.6.4
- Spring Cloud 2021.0.1
  - Gateway 3.1.1
  - Eureka-client 3.1.1
  - Config 3.1.1
  - Bus-amqp(RabbitMq) 3.1.1
- JWT jwtt 0.9.1
- Spring Actuator 2.6.4
 

## 프로젝트 서버 구성
- IP : localhost
- PORT : 8888
- RabbitMq : localhost:5672

## URL
- **[H2 Console]**
  - [User Link](http://localhost:8000/user-service/h2-console)
  - [Order Link](http://localhost:8000/order-service/h2-console)
  - [Catalog Link](http://localhost:8000/catalog-service/h2-console)
------------
- **[User API]**
  - [GET health-check (JWT X)]
    - http://localhost:8000/user-service/health-check
  - [POST createUser (JWT X)]
    - http://localhost:8000/user-service/users
      - Request Body(json)
        - email
        - name
        - pwd
  - [POST login (JWT X)]
    - http://localhost:8000/user-service/login
      - Request Body(json)
        - email
        - pwd
      - **Response Header(token)**
        - JWT token
  - [GET getUsers (JWT O)]
    - http://localhost:8000/user-service/users?size=10&page=1
      - **Request Authorization(Bearer Token)** 
        - JWT token
  - [GET getUser (JWT O)]
    - http://localhost:8000/user-service/users/user
      - **Request Authorization(Bearer Token)**
        - JWT token
------------
- **[Catalog API]**
  - [GET health-check (JWT X)]
    - http://localhost:8000/catalog-service/health-check
  - [GET getCatalogs (JWT O)]
    - http://localhost:8000/catalog-service/catalogs?size=10&page=0
      - **Request Authorization(Bearer Token)**
        - JWT token
------------
- **[Order API]**
  - [GET health-check (JWT X)]
    - http://localhost:8000/order-service/health-check
  - [POST createOrder (JWT O)]
    - http://localhost:8000/order-service/orders
      - **Request Authorization(Bearer Token)**
        - JWT token
      - Request Body(json) <= **[Catalog API] getCatalogs 참조** 
        - productId
        - qty

## 추가 정리
- **구동 서버**
  - **[중요]** 비동기 서버 이기 때문에 Embedded tomcat 아닌 Netty 서버가 구동됨
