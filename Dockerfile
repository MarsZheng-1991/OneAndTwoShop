# =========================================
# 1. Build Stage - Compile all services
# =========================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy root pom + all modules
COPY pom.xml .
COPY common-lib ./common-lib
COPY user-service ./user-service
COPY product-service ./product-service
COPY order-service ./order-service
COPY notification-service ./notification-service
COPY gateway-service ./gateway-service

# Build all microservices (common-lib first)
RUN mvn -q -e -B -DskipTests clean package

# =========================================
# 2. Runtime Image for product-service
# =========================================
FROM eclipse-temurin:17-jre-jammy AS product
WORKDIR /app
COPY --from=builder /app/product-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# =========================================
# 3. Runtime Image for order-service
# =========================================
FROM eclipse-temurin:17-jre-jammy AS order
WORKDIR /app
COPY --from=builder /app/order-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# =========================================
# 4. Runtime Image for user-service
# =========================================
FROM eclipse-temurin:17-jre-jammy AS user
WORKDIR /app
COPY --from=builder /app/user-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# =========================================
# 5. Runtime Image for gateway-service
# =========================================
FROM eclipse-temurin:17-jre-jammy AS gateway
WORKDIR /app
COPY --from=builder /app/gateway-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]