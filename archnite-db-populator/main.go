package main

import (
	"context"
	"fmt"
	"os"

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

	var greeting string
	err = dbpool.QueryRow(context.Background(), "select 'Hello, world!'").Scan(&greeting)
	if err != nil {
		fmt.Fprintf(os.Stderr, "QueryRow failed: %v\n", err)
		os.Exit(1)
	}

	fmt.Println(greeting)
}
