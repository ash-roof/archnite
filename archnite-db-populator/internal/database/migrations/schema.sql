CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS packages (
    id SERIAL PRIMARY KEY,
    package_name TEXT NOT NULL,
    description TEXT NOT NULL,
    last_update TIMESTAMPTZ NOT NULL,
    url TEXT NOT NULL,
    is_aur BOOLEAN NOT NULL,
    architecture VARCHAR(8)
        CHECK (
            (is_aur = FALSE AND architecture IN ('any', 'x86_64'))
            OR (is_aur = TRUE AND architecture IS NULL)
        )
);

CREATE INDEX IF NOT EXISTS idx_packages_name_trgm ON packages USING gin (lower(package_name) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_official_packages_name ON packages (package_name)
    WHERE is_aur = FALSE;

CREATE INDEX IF NOT EXISTS idx_aur_packages_name ON packages (package_name)
    WHERE is_aur = TRUE;
