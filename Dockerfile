# 1-bosqich: Build (Java 25 ishlatamiz)
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# Loyiha fayllarini nusxalash
COPY . .

# Gradle-ga ruxsat berish va build qilish
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# 2-bosqich: Run
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Build bosqichidan jar faylni nusxalab olish
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]