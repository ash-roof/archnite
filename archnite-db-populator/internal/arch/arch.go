package arch

import (
	"archnite-db-populator/internal/utils"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"sync"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type ArchPackage struct {
	Name         string    `json:"pkgname"`
	Architecture string    `json:"arch"`
	Description  string    `json:"pkgdesc"`
	Url          string    `json:"url"`
	LastUpdate   time.Time `json:"last_update"`
}

type ArchResponse struct {
	TotalPages int           `json:"num_pages"`
	Page       int           `json:"page"`
	Results    []ArchPackage `json:"results"`
}

var (
	totalPagesCache int
	totalPagesOnce  sync.Once
	totalPagesErr   error
	cacheTimeStamp  time.Time
	cacheDuration   = time.Hour * 24
)

func Populate() error {
	initDbSql, err := utils.LoadSchema("./internal/arch/arch_packages.sql")
	if err != nil {
		return fmt.Errorf("failed to load schema: %w", err)
	}

	dbpool, err := utils.InitDbPool("postgres://postgres:secretpass@localhost:5432/archnitedb")
	if err != nil {
		return fmt.Errorf("failed to initialize database connection: %w", err)
	}
	defer dbpool.Close()

	if err := utils.ExecuteSchema(dbpool, initDbSql); err != nil {
		return fmt.Errorf("failed to initialize database schema: %w", err)
	}

	packages, err := loadArchPackages()
	if err != nil {
		return fmt.Errorf("failed to load arch packages: %w", err)
	}

	if err := updateDatabase(dbpool, packages); err != nil {
		return fmt.Errorf("failed to update arch_packages: %w", err)
	}

	return nil
}

func loadArchPackages() ([]ArchPackage, error) {
	var packages []ArchPackage
	var wg sync.WaitGroup
	var mu sync.Mutex
	var firstErr error
	var once sync.Once
	startTime := time.Now()
	stopSignal := make(chan bool)
	ticker := time.NewTicker(time.Millisecond * 1000)
	defer ticker.Stop()

	initialResponse, err := loadPageResponse(1)
	if err != nil {
		return []ArchPackage{},
			errors.New(err.Error())
	}
	packages = append(packages, initialResponse.Results...)

	for i := 2; i <= initialResponse.TotalPages; i++ {
		wg.Add(1)
		go func(page int) {
			defer wg.Done()
			select {
			case <-stopSignal:
				return
			case <-ticker.C:
				pageResponse, err := loadPageResponse(page)
				if err != nil {
					once.Do(func() {
						firstErr = fmt.Errorf("error on page %d: %w", page, err)
						close(stopSignal)
					})
					return
				}
				mu.Lock()
				defer mu.Unlock()
				packages = append(packages, pageResponse.Results...)
			}
		}(i)
	}

	wg.Wait()
	if firstErr != nil {
		return nil, firstErr
	}
	end := time.Now()
	duration := end.Sub(startTime)

	fmt.Println("Time taken:", duration)
	return packages, nil
}

func updateDatabase(dbpool *pgxpool.Pool, packages []ArchPackage) error {
	tx, err := dbpool.Begin(context.Background())
	if err != nil {
		return fmt.Errorf("could not begin transaction: %w", err)
	}
	defer func() {
		utils.HandleTransactionError(err, tx)
	}()

	deleteTag, err := tx.Exec(context.Background(), "DELETE FROM arch_packages")
	if err != nil {
		return fmt.Errorf("error deleting old packages: %w", err)
	}
	fmt.Println(deleteTag)

	copyCount, err := copyPackagesToDb(tx, packages)
	if err != nil {
		return fmt.Errorf("error copying packages to db: %w", err)
	}
	fmt.Printf("Copied %d rows to db\n", copyCount)

	return nil
}

func copyPackagesToDb(tx pgx.Tx, packages []ArchPackage) (int64, error) {
	copyCount, err := tx.CopyFrom(
		context.Background(),
		pgx.Identifier{"arch_packages"},
		[]string{"architecture", "package_name", "description", "last_update", "url"},
		pgx.CopyFromSlice(len(packages), func(i int) ([]any, error) {
			return []any{
				packages[i].Architecture,
				packages[i].Name,
				packages[i].Description,
				packages[i].LastUpdate,
				packages[i].Url,
			}, nil
		}),
	)
	if err != nil {
		return 0, fmt.Errorf("error during CopyFrom: %w", err)
	}
	return copyCount, nil
}

func loadPageResponse(page int) (ArchResponse, error) {
	if page <= 0 {
		return ArchResponse{},
			fmt.Errorf("invalid page number: %d, only positive page numbers are allowed", page)
	}

	if time.Since(cacheTimeStamp) > cacheDuration {
		totalPagesOnce = sync.Once{}
	}
	totalPagesOnce.Do(func() {
		initialResponse, err := fetchPage(1)
		if err != nil {
			totalPagesErr = fmt.Errorf("failed to fetch total pages: %w", err)
			return
		}
		totalPagesCache = initialResponse.TotalPages
		cacheTimeStamp = time.Now()
	})

	if totalPagesErr != nil {
		return ArchResponse{}, totalPagesErr
	}

	if page > totalPagesCache {
		return ArchResponse{},
			fmt.Errorf("invalid page number: %d, valid range is 1-%d", page, totalPagesCache)
	}

	return fetchPage(page)
}

func fetchPage(page int) (ArchResponse, error) {
	url := fmt.Sprintf("https://archlinux.org/packages/search/json/?page=%d", page)
	resp, err := http.Get(url)
	if err != nil {
		return ArchResponse{},
			fmt.Errorf("failed to fetch page %d: %w", page, err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return ArchResponse{},
			fmt.Errorf("failed to fetch page %d: received status code %d", page, resp.StatusCode)
	}

	var parsedResp ArchResponse
	decodeErr := json.NewDecoder(resp.Body).Decode(&parsedResp)

	if decodeErr != nil {
		return ArchResponse{},
			fmt.Errorf("failed to decode JSON response on page %d: %w", page, decodeErr)
	}

	return parsedResp, nil
}
