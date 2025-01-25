CREATE TABLE IF NOT EXISTS arch_packages (
    id SERIAL PRIMARY KEY,
    architecture VARCHAR(10),
    package_name VARCHAR(100),
    description TEXT,
    last_update timestamptz,
    url TEXT
);

CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX IF NOT EXISTS archpkg_idx_trgm ON arch_packages USING gin((package_name) gin_trgm_ops);
