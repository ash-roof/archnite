package main

import (
	"archnite-db-populator/internal/arch"
	"context"
	"fmt"
	"os"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

func main() {
	var err error
	defer func() {
		if err != nil {
			fmt.Printf("main exited with error: %v\n", err)
		}
	}()

	file, err := os.ReadFile("schema.sql")
	if err != nil {
		fmt.Printf("error loading schema.sql: %v\n", err)
		os.Exit(1)
	}
	initDbSql := string(file)
	fmt.Println(initDbSql)

	url := "postgres://postgres:secretpass@localhost:5432/archnitedb"
	dbpool, err := pgxpool.New(context.Background(), url)
	if err != nil {
		fmt.Fprintf(os.Stderr, "unable to create connection pool: %v\n", err)
		os.Exit(1)
	}
	defer dbpool.Close()

	initTag, err := dbpool.Exec(context.Background(), initDbSql)
	if err != nil {
		fmt.Printf("error initializing db: %v\n", err)
		return
	}
	fmt.Println(initTag)

	packages, err := arch.LoadPackages()
	if err != nil {
		fmt.Printf("error loading packages: %v\n", err)
		return
	}

	updateDbTx, err := dbpool.Begin(context.Background())
	if err != nil {
		fmt.Printf("could not begin transaction: %v\n", err)
		return
	}
	defer func() {
		handleTransactionError(err, updateDbTx)
	}()

	deleteTag, err := updateDbTx.Exec(context.Background(), "DELETE FROM arch_packages")
	if err != nil {
		fmt.Printf("error removing old packages from db: %v\n", err)
		return
	}
	fmt.Println(deleteTag)

	copyCount, err := copyPackagesToDb(updateDbTx, packages)
	if err != nil {
		fmt.Printf("error copying packages to db: %v\n", err)
		return
	}
	fmt.Printf("Copied %d rows to db\n", copyCount)
}

func copyPackagesToDb(tx pgx.Tx, packages []arch.ArchPackage) (int64, error) {
	copyCount, err := tx.CopyFrom(
		context.Background(),
		pgx.Identifier{"arch_packages"},
		[]string{"architecture", "package_name", "description", "last_update", "url"},
		pgx.CopyFromSlice(len(packages), func(i int) ([]any, error) {
			return []any{
				packages[i].Architecture,
				packages[i].Name,
				packages[i].Description,
				packages[i].LastUpdate,
				packages[i].Url,
			}, nil
		}),
	)
	if err != nil {
		fmt.Printf("error during CopyFrom: %v\n", err)
		return 0, err
	}
	return copyCount, nil
}

func handleTransactionError(err error, tx pgx.Tx) {
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
