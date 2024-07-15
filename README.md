# payments-api

This project is a possible solution for "Api de transferência" test case from Itaú Unibanco.
The author is Juliana Saotome.

## OS Packages required

- Open JDK 17
- Docker

## How to run the app

### From Docker

1. Build the Docker image `docker image build -t payments-api .`
    - You may need to enable Docker: `sudo systemctl start docker`
2. Run the image `docker run -p 8080:8080 payments-api`

### From local machine

`mvn spring-boot:run`

## How to run the tests

`mvn test`

### About the concurrency test in integration tests

Concurrency test performed by class `ProcessPaymentConcurrentTest.java` may be
impacting `TransactionControllerV1IntegrationTest.java`.
Therefore, test should be run individually:  
`mvn test -Dtest="TransactionControllerV1IntegrationTest"`
