# ---- ビルドフェーズ ----
FROM gradle:8.7.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# ---- 実行フェーズ（軽量化）----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/demo01-0.0.1-SNAPSHOT.jar app.jar

# ポート番号の指定（適宜変更）
EXPOSE 8080

# 起動コマンド
ENTRYPOINT ["java", "-jar", "app.jar"]
