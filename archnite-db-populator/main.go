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
	if os.Getenv("GO_DBCONN_URL") == "" {
		envPath := filepath.Join("..", ".env.local")
		if err := godotenv.Load(envPath); err != nil {
			log.Println("Info: No .env.local file found. Assuming environment variables are already set.")
		}
	}

	dbConnUrl := os.Getenv("GO_DBCONN_URL")
	if dbConnUrl == "" {
		log.Fatal("Error: GO_DBCONN_URL environment variable is not set. Ensure it's provided via environment variables or .env.local in the repo root")
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
