FROM gradle:8.5-jdk17-alpine AS builder
WORKDIR /app
COPY . .
RUN gradle installDist

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/install/sklep-app /app
ENV DB_URL=jdbc:mysql://127.0.0.1:3306/sklep
ENV DB_USER=root
ENV DB_PASS=haslo
CMD ["./bin/sklep-app"]
