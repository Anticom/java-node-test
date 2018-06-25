# Stage 1 - the build process
FROM maven as builder
WORKDIR /usr/src/app
COPY . ./
RUN mvn package

# Stage 2 - the production environment
FROM openjdk:8
WORKDIR /usr/dist/app
COPY --from=builder /usr/src/app/target/engine-test-1.0-SNAPSHOT-shaded.jar /usr/dist/app/
CMD ["java", "-jar", "engine-test-1.0-SNAPSHOT-shaded.jar"]
