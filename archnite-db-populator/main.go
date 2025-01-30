package main

import "archnite-db-populator/internal/aur"

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
	//https://aur.archlinux.org/packages-meta-ext-v1.json.gz

	aur.DownloadFile("./internal/aur/pkgdump.json", "https://aur.archlinux.org/packages-meta-ext-v1.json.gz")
}
