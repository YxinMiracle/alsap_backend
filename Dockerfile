FROM maven:3.8.1-openjdk-17

ADD alsap_backend-0.0.1-SNAPSHOT.jar /home/app.jar

EXPOSE 8763

CMD ["java", "-Dcsp.sentinel.dashboard.server=localhost:8131", "-jar", "/home/app.jar", "--spring.profiles.active=prod"]
