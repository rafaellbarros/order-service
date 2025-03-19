# Etapa de Construção
FROM gradle:8.4.0-jdk21 AS build
WORKDIR /app

# Copiar apenas os arquivos necessários para cache eficiente
COPY --chown=gradle:gradle build.gradle settings.gradle gradlew ./
COPY --chown=gradle:gradle gradle ./gradle
RUN chmod +x gradlew

# Executar dependências primeiro para cache
RUN ./gradlew dependencies --no-daemon

# Copiar código-fonte após o cache de dependências
COPY --chown=gradle:gradle src ./src

# Build otimizado
RUN ./gradlew clean build -Pstg --no-daemon

# Etapa Final (Imagem Leve)
FROM gcr.io/distroless/java21:latest AS runtime
WORKDIR /app

# Copiar JAR gerado
COPY --from=build /app/build/libs/order-api.jar /app/app.jar

EXPOSE 8080
# Iniciar a aplicação corretamente
CMD ["/usr/bin/java", "-jar", "/app/app.jar"]
