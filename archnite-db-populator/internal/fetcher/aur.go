package fetcher

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"

	"github.com/OmarAshraf-02/archnite/archnite-db-populator/internal/models"
)

func FetchAurPackages() ([]models.AurPackage, error) {
	const (
		aurDumpURL = "https://aur.archlinux.org/packages-meta-ext-v1.json.gz"
		dumpPath   = "./aur-dump.json"
	)

	if err := downloadAurDump(dumpPath, aurDumpURL); err != nil {
		return nil, fmt.Errorf("aur dump download failed: %w", err)
	}

	packages, err := processAurDump(dumpPath)
	if err != nil {
		return nil, fmt.Errorf("aur dump processing failed: %w", err)
	}

	if err := os.Remove(dumpPath); err != nil {
		log.Printf("Warning: Failed to clean up AUR dump file: %v", err)
	}

	return packages, nil
}

func downloadAurDump(filePath string, url string) error {
	resp, err := http.Get(url)
	if err != nil {
		return fmt.Errorf("HTTP request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}

	outFile, err := os.Create(filePath)
	if err != nil {
		return fmt.Errorf("file creation failed: %w", err)
	}
	defer outFile.Close()

	if _, err := io.Copy(outFile, resp.Body); err != nil {
		return fmt.Errorf("file write failed: %w", err)
	}

	return nil
}

func processAurDump(filePath string) ([]models.AurPackage, error) {
	pkgDump, err := os.Open(filePath)
	if err != nil {
		return nil, fmt.Errorf("file open failed: %w", err)
	}
	defer pkgDump.Close()

	var packages []models.AurPackage
	decoder := json.NewDecoder(pkgDump)
	if err := decoder.Decode(&packages); err != nil {
		return nil, fmt.Errorf("JSON decode failed: %w", err)
	}

	return packages, nil
}
