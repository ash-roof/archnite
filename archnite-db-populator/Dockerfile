# syntax=docker/dockerfile:1

FROM golang:1.23 AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -o bin/populator ./cmd/populator/main.go

FROM alpine:3.20 AS final

WORKDIR /app

COPY --from=builder /app/bin/populator /app/populator
COPY --from=builder /app/internal/database/migrations/schema.sql /app/internal/database/migrations/schema.sql

ARG UID=10001
RUN adduser --disabled-password --gecos "" --home "/app" --shell "/sbin/nologin" --no-create-home --uid "${UID}" appuser \
    && chown -R appuser:appuser /app \
    && chmod +x /app/populator

USER appuser

CMD ["./populator"]
