FROM openjdk:16-jdk-alpine
ARG APIJAR_FILE=./lms-api/build/libs/app.jar
COPY ${APIJAR_FILE} ./app.jar
#ENV TZ=Asia/Seoul
ENTRYPOINT ["java","-jar","./app.jar"]