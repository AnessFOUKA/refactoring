FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app
COPY . .
RUN mvn -B -pl transport-legacy -am package -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre

WORKDIR /app
COPY --from=build /app/transport-legacy/target/classes ./classes
CMD ["java", "-cp", "classes", "fr.efrei.transport.Main"]