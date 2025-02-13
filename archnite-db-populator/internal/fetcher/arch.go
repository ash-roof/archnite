package fetcher

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"sync"

	"github.com/OmarAshraf-02/archnite/archnite-db-populator/internal/models"
	"golang.org/x/sync/errgroup"
)

func FetchArchPackages(ctx context.Context) ([]models.ArchPackage, error) {
	firstPage, err := fetchArchPage(ctx, 1)
	if err != nil {
		return nil, err
	}

	var (
		packages = firstPage.Results
		mu       sync.Mutex
	)

	g, ctx := errgroup.WithContext(ctx)
	g.SetLimit(5)

	for page := 2; page <= firstPage.TotalPages; page++ {
		page := page
		g.Go(func() error {
			resp, err := fetchArchPage(ctx, page)
			if err != nil {
				return err
			}

			mu.Lock()
			packages = append(packages, resp.Results...)
			mu.Unlock()
			return nil
		})
	}

	if err := g.Wait(); err != nil {
		return nil, err
	}

	return packages, nil
}

func fetchArchPage(ctx context.Context, page int) (*models.ArchResponse, error) {
	req, err := http.NewRequestWithContext(ctx, "GET",
		fmt.Sprintf("https://archlinux.org/packages/search/json/?page=%d", page), nil)
	if err != nil {
		return nil, err
	}

	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	var result models.ArchResponse
	if err := json.NewDecoder(resp.Body).Decode(&result); err != nil {
		return nil, err
	}

	return &result, nil
}
