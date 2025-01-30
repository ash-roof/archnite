package main

import (
	"archnite-db-populator/internal/arch"
	"archnite-db-populator/internal/aur"
	"fmt"
	"os"
	"time"
)

// TODO: Refactor application logic to be more modular/reusable (create generic functions)

func main() {
	if err := aur.Populate(); err != nil {
		fmt.Printf("application exited with error: %v\n", err)
		os.Exit(1)
	}
	if err := arch.Populate(); err != nil {
		fmt.Printf("application exited with error: %v\n", err)
		os.Exit(1)
	}

	ticker := time.NewTicker(time.Hour * 6)
	defer ticker.Stop()
	for {
		select {
		case <-ticker.C:
			if err := aur.Populate(); err != nil {
				fmt.Printf("application exited with error: %v\n", err)
				os.Exit(1)
			}
			if err := arch.Populate(); err != nil {
				fmt.Printf("application exited with error: %v\n", err)
				os.Exit(1)
			}
		}
	}
}
