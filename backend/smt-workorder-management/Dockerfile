# ==================== 阶段 1：Maven 构建 ====================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# 先复制 pom.xml，利用 Docker 缓存层加速构建
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并打包（跳过测试加速构建）
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==================== 阶段 2：运行时镜像（Debian/glibc） ====================
# eclipse-temurin:21-jre 基于 Ubuntu LTS，使用 glibc（不是 Alpine 的 musl）
# 与 Debian 12 完全兼容，且是官方维护的 OpenJDK 镜像
FROM eclipse-temurin:21-jre

WORKDIR /app

# 设置时区为 Asia/Shanghai
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 从构建阶段复制 jar 包
COPY --from=build /build/target/*.jar app.jar

# 复制 RSA 密钥文件
COPY keys ./keys

# 创建日志目录
RUN mkdir -p /app/logs

EXPOSE 8080

# -Duser.timezone 确保 JVM 内部也使用正确时区
ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "app.jar"]