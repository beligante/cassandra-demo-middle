FROM openjdk:8u111-jdk-alpine
RUN apk add --no-cache bash gawk sed grep bc coreutils
VOLUME /tmp
ADD run.sh run.sh
ADD target/cassandra-demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["/run.sh"]
CMD ["--help"]
