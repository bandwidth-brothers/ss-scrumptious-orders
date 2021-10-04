FROM maven:latest
COPY target/scrumptious_orders-0.0.1-SNAPSHOT.jar /home/order-backend.jar
ENTRYPOINT java -jar /home/order-backend.jar
