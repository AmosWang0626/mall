# 使用 DaoCloud 镜像加速 Docker Hub 拉取
FROM docker.m.daocloud.io/library/maven:3.8-openjdk-8-slim AS build

WORKDIR /app

# 配置阿里云 Maven 镜像源
COPY settings.xml /root/.m2/settings.xml

COPY pom.xml .
# 先下载依赖，利用 Docker 缓存层
RUN mvn dependency:go-offline -B -q

COPY src ./src
RUN mvn clean package -DskipTests -B -q

# 运行阶段
FROM docker.m.daocloud.io/library/openjdk:8-jre-slim

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
