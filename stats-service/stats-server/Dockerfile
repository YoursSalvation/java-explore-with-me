FROM eclipse-temurin:21-jre

WORKDIR /app

ARG JAR_FILE=target/stats-server-*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
