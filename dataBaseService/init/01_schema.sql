--  Runs automatically the FIRST time the container starts.

-- NOTE: AVOID USING "VALUE" AS A COLUMN NAME, IT SEEMS LIKE A RESERVED WORD
-- MORE INFO IN https://www.postgresql.org/docs/current/sql-keywords-appendix.html
-- Signed: Pedro Lopes

BEGIN;

CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE NOT NULL,
    code VARCHAR(3) UNIQUE NOT NULL
);

-- Create table for Metrics
CREATE TABLE IF NOT EXISTS metrics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    label VARCHAR(50) NOT NULL,
    unit VARCHAR(30)
);
-- Create table for Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(30) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(50),
    country_id BIGINT REFERENCES locations(id) ON DELETE SET NULL
);

-- Auxiliar table for user metrics
CREATE TABLE IF NOT EXISTS user_metrics (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    metric_id BIGINT REFERENCES metrics(id) ON DELETE CASCADE,
    metric_value SMALLINT
    
);

-- Auxiliar table for user favorite locations
CREATE TABLE IF NOT EXISTS user_favorite_locations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    location_id BIGINT REFERENCES locations(id) ON DELETE CASCADE
);

-- Create table for Country Metrics
CREATE TABLE IF NOT EXISTS location_metrics (
    id BIGSERIAL PRIMARY KEY,
    location_id BIGINT REFERENCES locations(id) ON DELETE CASCADE,
    metric_id BIGINT REFERENCES metrics(id) ON DELETE CASCADE,
    metric_value DOUBLE PRECISION,
    year INTEGER NOT NULL
);

-- CREATE TABLE IF NOT EXISTS users (
--     id BIGSERIAL PRIMARY KEY,
--     username VARCHAR(50) UNIQUE NOT NULL,
--     email VARCHAR(100) UNIQUE NOT NULL,
--     active BOOLEAN NOT NULL DEFAULT TRUE,
--     country INTEGER,
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP,
--     deleted_at TIMESTAMP
-- );

-- Create table for Forum Posts
CREATE TABLE IF NOT EXISTS forum_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

COMMIT;

-- NOTE: ter em atenção o uso de SERIAL/SMALLSERIAL ou INTEGER/SMALLINT
