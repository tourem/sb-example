FROM openjdk:8u121-jre-alpine
ARG JAR_FILE
COPY ${JAR_FILE} batch-agregat-journalier.jar
# ENTRYPOINT allows you to configure a container that will run as an executable
ENTRYPOINT exec java $JAVA_OPTS -jar /batch-agregat-journalier.jar