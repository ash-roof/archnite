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
	if os.Getenv("DOCKER_ENV") != "true" {
		cwd, err := os.Getwd()
		if err != nil {
			log.Fatal("Error getting current working directory:", err)
		}
		envPath := filepath.Join(cwd, "..", ".env.local")
		err = godotenv.Load(envPath)
		if err != nil {
			log.Fatal("Error loading .env file:", err)
		}
	}

	dbConnUrl := os.Getenv("GO_DBCONN_URL")
	if dbConnUrl == "" {
		log.Fatal("GO_DBCONN_URL environment variable is not set")
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
