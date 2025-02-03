# syntax=docker/dockerfile:1

FROM golang:1.23 AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -o archnite-db-populator .

FROM builder AS tester
RUN go test -v ./...

FROM alpine:3.20 AS final

WORKDIR /app

COPY --from=builder /app/archnite-db-populator .

COPY --from=builder /app/internal/arch/arch_packages.sql ./internal/arch/arch_packages.sql
COPY --from=builder /app/internal/aur/aur_packages.sql ./internal/aur/aur_packages.sql

ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/app" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser

RUN chown -R appuser:appuser /app && chmod +x /app/archnite-db-populator

USER appuser

CMD ["./archnite-db-populator"]
