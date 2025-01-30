CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS arch_packages (
    id SERIAL PRIMARY KEY,
    architecture VARCHAR(10),
    package_name TEXT NOT NULL,
    description TEXT NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    url TEXT
);

CREATE INDEX IF NOT EXISTS archpkg_idx_trgm ON arch_packages USING gin (lower(package_name) gin_trgm_ops);
