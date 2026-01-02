# -------- build stage --------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first for better caching
COPY pom.xml ./
COPY src ./src

# Build a runnable jar (skip tests for CI speed; you can remove -DskipTests if you want)
RUN mvn -B clean package -DskipTests

# -------- run stage --------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create non-root user (good practice)
RUN useradd -r -u 10001 appuser
USER appuser

# Copy the built jar (adjust if your jar name differs)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

