FROM adoptopenjdk/openjdk11:jre-11.0.7_10-alpine AS base

COPY /target /home/target

USER 1001

EXPOSE 8085

CMD ["/bin/sh", "-c", "/opt/java/openjdk/bin/java -jar /home/target/health-core-runner.jar -Djava.net.preferIPv4Stack=true"]
