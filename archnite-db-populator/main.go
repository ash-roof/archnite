package main

import (
	"archnite-db-populator/internal/arch"
	"archnite-db-populator/internal/aur"
	"fmt"
	"log"
	"os"
	"time"

	"github.com/joho/godotenv"
)

// TODO: Refactor application logic to be more modular/reusable (create generic functions)

func main() {
	err := godotenv.Load("../.env")
	dbConnUrl := os.Getenv("GO_DBCONN_URL")

	if err != nil {
		log.Fatal("Error loading .env file")
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
