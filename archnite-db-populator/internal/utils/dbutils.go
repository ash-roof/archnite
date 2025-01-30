package utils

import (
	"context"
	"fmt"
	"os"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

func LoadSchema(path string) (string, error) {
	file, err := os.ReadFile(path)
	if err != nil {
		return "", fmt.Errorf("error loading %s: %w\n", path, err)
	}
	return string(file), nil
}

func InitDbPool(dbConnUrl string) (*pgxpool.Pool, error) {
	dbpool, err := pgxpool.New(context.Background(), dbConnUrl)
	if err != nil {
		return nil, fmt.Errorf("unable to create connection pool: %w\n", err)
	}
	return dbpool, nil
}

func ExecuteSchema(dbpool *pgxpool.Pool, schema string) error {
	_, err := dbpool.Exec(context.Background(), schema)
	if err != nil {
		return fmt.Errorf("error executing schema: %w", err)
	}
	return nil
}

func HandleTransactionError(err error, tx pgx.Tx) {
	if err != nil {
		if rbErr := tx.Rollback(context.Background()); rbErr != nil {
			fmt.Printf("error rolling back transaction: %v\n", rbErr)
		} else {
			fmt.Println("transaction rolled back succesfully")
		}
	} else {
		if commitErr := tx.Commit(context.Background()); commitErr != nil {
			fmt.Printf("error committing transaction: %v\n", commitErr)
		} else {
			fmt.Println("transaction committed succesfully")
		}
	}
}
