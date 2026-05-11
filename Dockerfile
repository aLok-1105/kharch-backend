FROM eclipse-temurin:22-jdk
ADD target/*.jar kharch.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","kharch.jar"]