# ---------- Stage 1: build (Maven + JDK21) ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copia los pom primero para aprovechar caché de dependencias
COPY pom.xml .
# Descarga dependencias (sin código aún) para cachear
RUN mvn -B -q -DskipTests dependency:go-offline

# Copia el código fuente
COPY src ./src

# Compila y empaqueta (sin tests por rapidez en CI; cámbialo a tu gusto)
RUN mvn -B -DskipTests clean package

# ---------- Stage 2: runtime (JDK21) ----------
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia el JAR desde el stage builder
ARG JAR_NAME=catalog-service-0.0.1-SNAPSHOT.jar
COPY --from=builder /app/target/${JAR_NAME} app.jar

# Puerto de la app (configurable por env)
EXPOSE 8081

# Variables por defecto (puedes sobreescribir en compose/k8s)
ENV SERVER_PORT=8081

ENTRYPOINT ["java","-jar","app.jar"]

