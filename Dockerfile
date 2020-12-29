FROM maven:3-jdk-8 as builder
WORKDIR /project
COPY . .
RUN mvn package

FROM java:8-alpine
WORKDIR /app
COPY --from=builder /project/target/gestionepagamenti-0.0.1-SNAPSHOT.jar ./gestionepagamenti.jar

ENTRYPOINT ["/bin/sh", "-c"]
CMD ["java -jar gestionepagamenti.jar"]