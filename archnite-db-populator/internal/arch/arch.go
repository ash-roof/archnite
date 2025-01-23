package arch

import (
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"sync"
	"time"
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

func LoadPackages() ([]ArchPackage, error) {
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

	for i := 0; i < len(packages); i++ {
		fmt.Printf("%s\n", packages[i].Name)
	}
	fmt.Println("Time taken:", duration)
	return packages, nil
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
