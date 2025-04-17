# Stage 1: Dependencies
FROM eclipse-temurin:21-jdk-alpine AS deps
WORKDIR /app

# Configure Gradle
ENV GRADLE_USER_HOME=/cache
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true"va

# Install build dependencies
RUN apk add --no-cache curl wget

# Download Gradle distribution with retry logic
RUN mkdir -p /opt/gradle && \
    for i in {1..3}; do \
        wget --timeout=60 --tries=3 https://services.gradle.org/distributions/gradle-8.5-bin.zip -O gradle.zip && break || sleep 15; \
    done && \
    unzip gradle.zip -d /opt/gradle && \
    rm gradle.zip

ENV PATH="/opt/gradle/gradle-8.5/bin:${PATH}"

# Copy build configuration files
COPY gradle gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./

# Configure Gradle wrapper to use downloaded distribution
RUN sed -i 's/distributionUrl.*/distributionUrl=file:\/opt\/gradle\/gradle-8.5-bin.zip/' gradle/wrapper/gradle-wrapper.properties

# Download dependencies with retry logic
RUN chmod +x gradlew && \
    for i in {1..3}; do \
        ./gradlew dependencies --no-daemon --refresh-dependencies --stacktrace --console=plain && break || sleep 15; \
    done

# Stage 2: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy gradle files and cache from deps stage
COPY --from=deps /cache /root/.gradle
COPY --from=deps /opt/gradle /opt/gradle
ENV PATH="/opt/gradle/gradle-8.5/bin:${PATH}"

COPY gradle gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY src src/

# Build application
RUN chmod +x gradlew && \
    ./gradlew build --no-daemon --stacktrace --console=plain

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine

# Add necessary runtime packages
RUN apk add --no-cache tzdata && \
    addgroup -S appgroup && \
    adduser -S appuser -G appgroup

WORKDIR /app

# Copy application jar
COPY --from=build --chown=appuser:appgroup /app/build/libs/*.jar app.jar

# Configure JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75 \
               -XX:+OptimizeStringConcat \
               -XX:+UseStringDeduplication \
               -Dfile.encoding=UTF-8 \
               -Duser.timezone=UTC \
               -Djava.security.egd=file:/dev/./urandom"

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]