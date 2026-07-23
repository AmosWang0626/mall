FROM maven:3.8-openjdk-8-slim AS build

WORKDIR /app
COPY pom.xml .
# 先下载依赖，利用 Docker 缓存层
RUN mvn dependency:go-offline -B -q

COPY src ./src
RUN mvn clean package -DskipTests -B -q

# 运行阶段
FROM openjdk:8-jre-slim

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
