CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS aur_packages (
    id SERIAL PRIMARY KEY,
    package_name TEXT NOT NULL,
    description TEXT NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    url TEXT
);

CREATE INDEX IF NOT EXISTS aurpkg_idx_trgm ON aur_packages USING gin (lower(package_name) gin_trgm_ops);
