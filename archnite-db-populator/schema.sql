create table arch_packages
(
    id SERIAL primary key,
    architecture VARCHAR(10),
    name VARCHAR(100),
    description TEXT,
    last_update timestamptz,
    url TEXT
);

CREATE EXTENSION pg_trgm;

CREATE INDEX archpkg_idx_trgm ON arch_packages USING gin((name) gin_trgm_ops);

SELECT name, description
FROM arch_packages
WHERE name LIKE '%go%'
ORDER BY similarity(name, 'go') DESC;
