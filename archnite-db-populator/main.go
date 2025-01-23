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
	// archPackages, err := arch.LoadPackages()
	// if err != nil {
	// 	fmt.Println(err)
	// }
	// file, err := os.Create("ArchPackages.txt")
	// if err != nil {
	// 	fmt.Println("Error creating file:", err)
	// 	return
	// }
	// defer file.Close()
	//
	// for i := 0; i < len(archPackages); i++ {
	// 	_, err := fmt.Fprintf(file, "%s %s\n", archPackages[i].Name, archPackages[i].Url)
	// 	if err != nil {
	// 		fmt.Println("Error writing to file:", err)
	// 		return
	// 	}
	// }
	// fmt.Printf("Package names written to %s\n", file.Name())

	url := "postgres://postgres:secretpass@localhost:5432/archnitedb"
	dbpool, err := pgxpool.New(context.Background(), url)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Unable to create connection pool: %v\n", err)
		os.Exit(1)
	}
	defer dbpool.Close()

	packages, err := arch.LoadPackages()
	if err != nil {
		fmt.Printf("error loading packages: %v\n", err)
		return
	}

	copyCount, err := dbpool.CopyFrom(
		context.Background(),
		pgx.Identifier{"arch_packages"},
		[]string{"architecture", "name", "description", "last_update", "url"},
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

	fmt.Printf("Copied %d rows to db\n", copyCount)
}
