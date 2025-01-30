package main

import (
	"archnite-db-populator/internal/aur"
	"fmt"
)

func main() {
	// if err := arch.Populate(); err != nil {
	// 	fmt.Printf("application exited with error: %v\n", err)
	// 	os.Exit(1)
	// }
	//
	// ticker := time.NewTicker(time.Hour * 6)
	// defer ticker.Stop()
	// for {
	// 	select {
	// 	case <-ticker.C:
	// 		if err := arch.Populate(); err != nil {
	// 			fmt.Printf("application exited with error: %v\n", err)
	// 			os.Exit(1)
	// 		}
	// 	}
	// }

	packages, err := aur.LoadAurPackages("./internal/aur/pkgdump.json")
	if err != nil {
		fmt.Println(err)
		return
	}

	for i := 0; i < len(packages); i++ {
		fmt.Println(packages[i].Name)
	}
}
