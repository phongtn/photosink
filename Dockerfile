FROM amazoncorretto:17-alpine

LABEL author="phongtn.group1@gmail.com"

COPY ./build/libs/*.jar /service.jar

ENTRYPOINT ["java", "-jar", "/service.jar"]

EXPOSE 8081