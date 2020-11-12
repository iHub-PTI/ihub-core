# iHub-Core

This is the repository for the Core of the iHub Health Ecosystem

## Prerequisites

- openjdk 11
- Apache Maven 3.6.3
- docker
- docker-compose

## Getting Started

1. **Build the jar**

   Execute the following:

   ```
   mvn clean && mvn package
   ```

2. **Build the images and start the containers**

   ```bash
   docker-compose build
   docker-compose up -d
   ```

3. **Check swagger for documentation about the API in localhost:8085/swagger-ui**

## Server Environmental Variables 

- HEALTHCORE_JDBC_PASSWORD = Password of the db for the server
- HEALTHCORE_JDBC_USER = User of the db for the server
- HEALTHCORE_JDBC_URL = Url of the postgres connection
- LIST_CORS = List of Origins allowed by CORS
- LIST_HEADERS = List of Headers allowed by CORS
- ALLOW_CORS = true or false
- FHIR_SERVER_URL = Url of the fhir server
- AUTH_SERVER_URL = Url of the keycloak server
- CLIENT_ID = Bearer client of the service
- CLIENT_CREDENTIALS_SECRET = Secret of the client
- POSTGRES_DB = Name of the DB for the server
- POSTGRES_USER = Name of the user of the DB
- POSTGRES_PASSWORD = Password for the DB



## Test connections

For testing you should change the following variables in src/test/resources/application.properties:

```
hapi-fhir-server.url=http://localhost:8080/iHub/fhir
quarkus.oidc.auth-server-url=http://localhost:8080/auth/realms/iHub/
quarkus.oidc.client-id=test-service
quarkus.oidc.credentials.secret=
```



## Authors and License

This project was created as part of the iHub COVID-19 project in collaboration between [Penguin Academy](https://penguin.academy/) and [PTI (Parque Tecnológico Itaipu Paraguay)](http://pti.org.py/).

This project is licensed under [AGPL v3](https://github.com/iHub-PTI/ihub-core/blob/main/LICENSE)