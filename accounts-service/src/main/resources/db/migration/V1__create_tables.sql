-- Flyway migration: V1__create_tables.sql
-- Description: Create tables

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(64) NOT NULL UNIQUE,
    first_name VARCHAR(64),
    last_name VARCHAR(64) NOT NULL,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    balance NUMERIC(18,5) NOT NULL CONSTRAINT chk_balance_positive CHECK(balance>=0) DEFAULT 0
);