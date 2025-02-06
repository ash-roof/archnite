package aur

import (
	"archnite-db-populator/internal/utils"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type AurPackage struct {
	Name        string `json:"Name"`
	Description string `json:"Description"`
	Url         string `json:"URL"`
	LastUpdate  int64  `json:"LastModified"`
}

func Populate(dbConnUrl string) error {
	initDbSql, err := utils.LoadSchema("./internal/aur/aur_packages.sql")
	if err != nil {
		return fmt.Errorf("failed to load schema: %w", err)
	}

	if err := downloadFile("./internal/aur/pkgdump.json", "https://aur.archlinux.org/packages-meta-ext-v1.json.gz"); err != nil {
		return err
	}

	dbpool, err := utils.InitDbPool(dbConnUrl)
	if err != nil {
		return fmt.Errorf("failed to initialize database connection: %w", err)
	}
	defer dbpool.Close()

	if err := utils.ExecuteSchema(dbpool, initDbSql); err != nil {
		return fmt.Errorf("failed to initialize database schema: %w", err)
	}

	packages, err := loadAurPackages("./internal/aur/pkgdump.json")
	if err != nil {
		return err
	}

	if err := updateDatabase(dbpool, packages); err != nil {
		return fmt.Errorf("failed to update database: %w", err)
	}

	return nil
}

func updateDatabase(dbpool *pgxpool.Pool, packages []AurPackage) error {
	tx, err := dbpool.Begin(context.Background())
	if err != nil {
		return fmt.Errorf("could not begin transaction: %w", err)
	}
	defer func() {
		utils.HandleTransactionError(err, tx)
	}()

	deleteTag, err := tx.Exec(context.Background(), "TRUNCATE TABLE aur_packages RESTART IDENTITY")
	if err != nil {
		return fmt.Errorf("error deleting old packages: %w", err)
	}
	fmt.Print(deleteTag)
	fmt.Println(" aur_packages")

	copyCount, err := copyPackagesToDb(tx, packages)
	if err != nil {
		return fmt.Errorf("error copying packages to aur_packages: %w", err)
	}
	fmt.Printf("COPY %d rows to aur_packages\n", copyCount)

	return nil
}

func copyPackagesToDb(tx pgx.Tx, packages []AurPackage) (int64, error) {
	copyCount, err := tx.CopyFrom(
		context.Background(),
		pgx.Identifier{"aur_packages"},
		[]string{"package_name", "description", "last_update", "url"},
		pgx.CopyFromSlice(len(packages), func(i int) ([]any, error) {
			return []any{
				packages[i].Name,
				packages[i].Description,
				time.Unix(packages[i].LastUpdate, 0).UTC(),
				packages[i].Url,
			}, nil
		}),
	)
	if err != nil {
		return 0, fmt.Errorf("error during CopyFrom: %w", err)
	}
	return copyCount, nil
}

func downloadFile(filepath string, url string) (err error) {
	file, err := os.Create(filepath)
	if err != nil {
		return fmt.Errorf("error creating file: %w", err)
	}
	defer file.Close()

	resp, err := http.Get(url)
	if err != nil {
		return fmt.Errorf("error fetching data: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("bad status: %s", resp.Status)
	}

	_, err = io.Copy(file, resp.Body)
	if err != nil {
		return fmt.Errorf("error copying data to file: %w", err)
	}

	return nil
}

func loadAurPackages(path string) ([]AurPackage, error) {
	pkgDump, err := os.Open(path)
	if err != nil {
		return []AurPackage{}, fmt.Errorf("error reading %s: %w", path, err)
	}
	defer pkgDump.Close()

	var packages []AurPackage
	if err = json.NewDecoder(pkgDump).Decode(&packages); err != nil {
		return []AurPackage{}, fmt.Errorf("error parsing %s: %w", path, err)
	}

	return packages, nil
}
