# ベースイメージ
FROM gradle:8.7.0-jdk21 AS builder

WORKDIR /app

# 依存関係をキャッシュ
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle
RUN chmod +x gradlew
RUN ./gradlew build -x test || true

# アプリコードコピー
COPY . /app
RUN ./gradlew build -x test

# 実行用イメージ（軽量化）
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/demo01-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
