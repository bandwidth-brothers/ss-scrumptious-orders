FROM maven:latest
COPY target/ss-scrumptious-orders-0.0.1-SNAPSHOT.jar /home/order-backend.jar
ENTRYPOINT java -jar /home/order-backend.jar