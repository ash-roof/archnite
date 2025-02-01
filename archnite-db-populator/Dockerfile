# syntax=docker/dockerfile:1

FROM golang:1.23 AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -o archnite-db-populator .

FROM builder AS tester
RUN go test -v ./...

FROM alpine:3.20

WORKDIR /app

COPY --from=builder /app/archnite-db-populator .

COPY --from=builder /app/internal/arch/arch_packages.sql ./internal/arch/arch_packages.sql
COPY --from=builder /app/internal/aur/aur_packages.sql ./internal/aur/aur_packages.sql

CMD ["./archnite-db-populator"]
