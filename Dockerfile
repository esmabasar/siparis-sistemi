# 1. AŞAMA: Maven ile proje build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Proje dosyalarını kopyala
COPY . .

# Testleri atlayarak jar üret
RUN mvn -ntp clean package -DskipTests

# 2. AŞAMA: Sadece JRE ile hafif imaj
FROM eclipse-temurin:17-jre
WORKDIR /app

# Build aşamasında oluşan jar dosyasını kopyala
COPY --from=build /app/target/siparis-0.0.1-SNAPSHOT.jar app.jar

# Render PORT env değişkeni gönderiyor, Spring de bunu kullanacak
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
