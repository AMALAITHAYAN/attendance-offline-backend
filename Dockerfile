# Java 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build using Maven wrapper
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Expose Spring Boot port
EXPOSE 8080

# Run application
CMD ["sh", "-c", "java -jar target/*.jar"]