## Technical Overview
- Spring boot
- Java 11
- Mysql running on docker
- Liquibase for database version management (Application creates and maintains the DB)
- Open API spec (Swagger support)
- Builds on Github actions 

## Building the app locally

```
mvn clean install
```

## Run the app locally

```
mvn spring-boot:run
```

## Local URL

[http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/](http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/)


# Things TODO

- Write unit tests for ApiDelegate methods
- Move user validation to a proper filter
- Revisit the hibernate wiring of domain objects