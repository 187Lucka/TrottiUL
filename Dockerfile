FROM maven:3.9.11-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/application.jar .
COPY --from=build /app/target/lib/ ./lib/
EXPOSE 8080
CMD ["java", "-cp", "application.jar:lib/*", "ca.ulaval.trotti_ul.TrottiUlApplication"]
