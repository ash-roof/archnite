package main

import (
	"archnite-db-populator/internal/arch"
	"fmt"
	"os"
	"time"
)

func main() {
	ticker := time.NewTicker(time.Minute * 3)
	defer ticker.Stop()

	for {
		select {
		case <-ticker.C:
			if err := arch.Populate(); err != nil {
				fmt.Printf("application exited with error: %v\n", err)
				os.Exit(1)
			}
		}
	}
}
