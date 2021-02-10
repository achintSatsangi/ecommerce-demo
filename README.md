## Technical Overview
- Spring boot
- Java 11
- Mysql running on docker
- Liquibase for database version management (Application creates and maintains the DB)
- Open API spec (Swagger support)
- Builds on Github actions 

## Building the app locally
The build uses in-memory databases to run its tests so it doesn't require anything for building as such

```
mvn clean install
```

## Run the app locally
This requires an locally / remotely running mysql instance and put the details in application.yaml in main resources.

```
mvn spring-boot:run
```

## Local URL

[http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/](http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/)


# Things TODO

- Write unit tests for ApiDelegate methods
- Move user validation to a proper filter
- Revisit the hibernate wiring of domain objects
- Refactor OrderApiDelegateImpl
