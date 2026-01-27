# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21.0.9_10-jre-ubi10-minimal
WORKDIR /app

# Create uploads directory
RUN mkdir -p public/uploads

COPY --from=builder /build/target/job-portal-backend-1.0.0.jar app.jar

EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=5s --retries=5 \
    CMD curl -f http://localhost:3000/api/v1/jobs || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
