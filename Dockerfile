
FROM openjdk:21
#ARG JAR_FILE=out/artifacts/*.jar
COPY ./target/easy-stock-0.0.1-SNAPSHOT.jar app.jar
#COPY JAR_FILE app.jar
#COPY ./out/artifacts/*.jar app.jar
#EXPOSE 8080/tcp
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
