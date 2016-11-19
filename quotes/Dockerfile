FROM ofayau/ejre:8-jre

MAINTAINER Magnus Larsson <magnus.larsson.ml@gmail.com>

EXPOSE 8080

ADD ./build/libs/*.jar app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
