# 多阶段构建 — JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# 配置阿里云 Maven 镜像源
COPY settings.xml /root/.m2/settings.xml

COPY pom.xml .
# 先下载依赖，利用 Docker 缓存层
RUN mvn dependency:go-offline -B -q

COPY src ./src
RUN mvn clean package -DskipTests -B -q

# 运行阶段 — JRE 21 Alpine (轻量)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
