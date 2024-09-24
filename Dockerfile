#Use maven to build the application
FROM maven:3.9.8-amazoncorretto-17-al2023 as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#Use openjdk to run the application
FROM openjdk:24-slim-bullseye
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]