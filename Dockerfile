# 后端 Dockerfile
# 使用多阶段构建，减小镜像体积

# 第一阶段：构建阶段
FROM maven:3.8.6-openjdk-8-slim AS build

# 设置工作目录
WORKDIR /app

# 复制pom.xml文件
COPY pom.xml .

# 下载依赖（利用Docker缓存层）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段
FROM openjdk:8-jre-slim

# 设置工作目录
WORKDIR /app

# 安装必要的工具（使用国内镜像源加速）
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/security.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 从构建阶段复制JAR文件
COPY --from=build /app/target/*.jar app.jar

# 创建上传目录
RUN mkdir -p /app/uploads && \
    chmod 777 /app/uploads

# 暴露端口（应用运行在 9000 端口）
EXPOSE 9001

# 设置JVM参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

