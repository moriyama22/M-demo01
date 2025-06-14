# ---- ビルドフェーズ ----
FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

# ---- 実行フェーズ（軽量化）----
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# *.jar を指定することでバージョン名が変わっても対応できる
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
