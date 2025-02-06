package main

import (
	"archnite-db-populator/internal/arch"
	"archnite-db-populator/internal/aur"
	"fmt"
	"log"
	"os"
	"path/filepath"
	"time"

	"github.com/joho/godotenv"
)

func main() {
	// Load .env file from the root of the repository
	envPath := filepath.Join("..", ".env")
	if err := godotenv.Load(envPath); err != nil {
		log.Println("Info: No .env file found. Assuming environment variables are already set.")
	}

	var dbConnUrl string = "postgres://" + os.Getenv("POSTGRES_USER") + ":" + os.Getenv("POSTGRES_PASSWORD") + "@"
	if os.Getenv("DOCKER_COMPOSE_ENV") == "true" {
		dbConnUrl += "postgres:" + os.Getenv("POSTGRES_PORT") + "/" + os.Getenv("POSTGRES_DB")
	} else {
		dbConnUrl += "localhost:" + os.Getenv("POSTGRES_PORT") + "/" + os.Getenv("POSTGRES_DB")
	}

	if dbConnUrl == "" {
		log.Fatal("Error: Database connection URL is not set. Ensure GO_DBCONN_URL_DOCKER or GO_DBCONN_URL_LOCAL is provided in the .env file.")
	}

	if err := aur.Populate(dbConnUrl); err != nil {
		fmt.Printf("application exited with error: %v\n", err)
		os.Exit(1)
	}
	if err := arch.Populate(dbConnUrl); err != nil {
		fmt.Printf("application exited with error: %v\n", err)
		os.Exit(1)
	}

	mainTicker := time.NewTicker(time.Hour * 6)
	minutesToNextUpdate := 60 * 6
	logTicker := time.NewTicker(time.Minute)
	defer mainTicker.Stop()
	defer logTicker.Stop()
	for {
		select {
		case <-mainTicker.C:
			if err := aur.Populate(dbConnUrl); err != nil {
				fmt.Printf("application exited with error: %v\n", err)
				os.Exit(1)
			}
			if err := arch.Populate(dbConnUrl); err != nil {
				fmt.Printf("application exited with error: %v\n", err)
				os.Exit(1)
			}
		case <-logTicker.C:
			minutesToNextUpdate -= 1
			if minutesToNextUpdate <= 0 {
				minutesToNextUpdate = 60 * 6
				continue
			}
			fmt.Printf("minutes to next update: %d\n", minutesToNextUpdate)
		}
	}
}
