FROM eclipse-temurin:21-jdk-jammy

WORKDIR /usr/jvexpress

COPY . .

RUN ./gradlew build

EXPOSE 8089

CMD ["./gradlew","app:run"]
