package main

import (
	"context"
	"fmt"
	"log"
	"time"

	"github.com/OmarAshraf-02/archnite/archnite-db-populator/internal/config"
	"github.com/OmarAshraf-02/archnite/archnite-db-populator/internal/database"
	"github.com/OmarAshraf-02/archnite/archnite-db-populator/internal/fetcher"

	"github.com/jackc/pgx/v5/pgxpool"
)

func main() {
	config.LoadEnv()

	ctx := context.Background()
	dbPool, err := database.NewPool(ctx, config.DBConnectionString())
	if err != nil {
		log.Fatal(err)
	}
	defer dbPool.Close()

	if err := database.InitializeSchema(dbPool); err != nil {
		log.Fatal(err)
	}

	log.Println("Starting initial update...")
	if err := runUpdate(ctx, dbPool); err != nil {
		log.Fatalf("Initial update failed: %v", err)
	}

	ticker := time.NewTicker(6 * time.Hour)
	defer ticker.Stop()

	for range ticker.C {
		log.Println("Starting scheduled update...")
		if err := runUpdate(ctx, dbPool); err != nil {
			log.Printf("Update failed: %v", err)
		}
	}
}

func runUpdate(ctx context.Context, dbPool *pgxpool.Pool) error {
	log.Println("Fetching Arch packages...")
	archPkgs, err := fetcher.FetchArchPackages(ctx)
	if err != nil {
		return fmt.Errorf("error fetching Arch packages: %w", err)
	}
	log.Printf("Fetched %d Arch packages", len(archPkgs))

	log.Println("Fetching AUR packages...")
	aurPkgs, err := fetcher.FetchAurPackages()
	if err != nil {
		return fmt.Errorf("error fetching AUR packages: %w", err)
	}
	log.Printf("Fetched %d AUR packages", len(aurPkgs))

	log.Println("Updating database...")
	if err := database.UpdatePackages(ctx, dbPool, archPkgs, aurPkgs); err != nil {
		return fmt.Errorf("error updating database: %w", err)
	}
	log.Println("Database updated successfully!")

	return nil
}
