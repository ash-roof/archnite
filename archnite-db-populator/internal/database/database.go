package database

import (
	"context"
	"fmt"
	"log"
	"os"
	"time"

	"github.com/OmarAshraf-02/archnite/archnite-db-populator/internal/models"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

func NewPool(ctx context.Context, connStr string) (*pgxpool.Pool, error) {
	config, err := pgxpool.ParseConfig(connStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse connection string: %w", err)
	}

	pool, err := pgxpool.NewWithConfig(ctx, config)
	if err != nil {
		return nil, fmt.Errorf("failed to create connection pool: %w", err)
	}

	return pool, nil
}

func InitializeSchema(pool *pgxpool.Pool) error {
	schema, err := os.ReadFile("./internal/database/migrations/schema.sql")
	if err != nil {
		return fmt.Errorf("error reading schema file: %w", err)
	}

	_, err = pool.Exec(context.Background(), string(schema))
	return err
}

func UpdatePackages(ctx context.Context, pool *pgxpool.Pool, archPkgs []models.ArchPackage, aurPkgs []models.AurPackage) error {
	tx, err := pool.Begin(ctx)
	if err != nil {
		return fmt.Errorf("failed to begin transaction: %w", err)
	}
	defer tx.Rollback(ctx)

	if _, err = tx.Exec(ctx, "TRUNCATE TABLE packages RESTART IDENTITY"); err != nil {
		return fmt.Errorf("failed to truncate table: %w", err)
	}

	if len(archPkgs) > 0 {
		archCount, err := tx.CopyFrom(
			ctx,
			pgx.Identifier{"packages"},
			[]string{"package_name", "description", "last_update", "url", "architecture", "is_aur"},
			pgx.CopyFromSlice(len(archPkgs), func(i int) ([]any, error) {
				pkg := archPkgs[i]
				return []any{
					pkg.Name,
					pkg.Description,
					pkg.LastUpdate,
					pkg.Url,
					pkg.Architecture,
					false, // is_aur
				}, nil
			}),
		)
		if err != nil {
			return fmt.Errorf("failed to insert Arch packages: %w", err)
		}
		log.Printf("Inserted %d Arch packages", archCount)
	}

	if len(aurPkgs) > 0 {
		aurCount, err := tx.CopyFrom(
			ctx,
			pgx.Identifier{"packages"},
			[]string{"package_name", "description", "last_update", "url", "architecture", "is_aur"},
			pgx.CopyFromSlice(len(aurPkgs), func(i int) ([]any, error) {
				pkg := aurPkgs[i]
				return []any{
					pkg.Name,
					pkg.Description,
					time.Unix(pkg.LastUpdate, 0).UTC(), // Convert UNIX timestamp
					pkg.Url,
					nil,  // architecture
					true, // is_aur
				}, nil
			}),
		)
		if err != nil {
			return fmt.Errorf("failed to insert AUR packages: %w", err)
		}
		log.Printf("Inserted %d AUR packages", aurCount)
	}

	if err := tx.Commit(ctx); err != nil {
		return fmt.Errorf("failed to commit transaction: %w", err)
	}

	return nil
}
