Spring Boot - Casssandra - Kafka - Spark SQL & Streaming

## Swagger

http://localhost:8080/swagger-ui.html

## Gradle

The './gradlew' can be changed for 'gradle' if gradle is installed and available in the PATH.
    
    ./gradlew build
    
Start the spring boot application

    ./gradlew bootRun

The service will be deployed on port 8099 as specified in application.yml:

    $http://localhost:8099
    
## Docker compose - (Cassandra)

To bring up or down the docker containers defined in `docker-compose.yml`, run

    docker-compose up
    docker-compose down

To force delete the volume, and start from scratch (removes any data stored in the database)
    
    docker-compose up --force-recreate --build
    docker-compose down --volumes --remove-orphans

## Docker

show all containers
    
    docker ps -a

stop a container
    
    docker stop <container_id>
    docker rm <container_id>

start a container
    
    docker start <container_id>
    
show all volumes
    
    docker volume ls

remove unused volumes
    
    docker volume prune
    
https://github.com/DataStax-Examples/spring-k8s-cassandra-microservices/tree/master/microservice-spring-data

https://github.com/spember/spark-cass-spring-demo

https://github.com/lankydan/spring-data-cassandra
