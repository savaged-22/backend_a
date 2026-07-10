CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE employees (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    email VARCHAR(255) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,

    job_title VARCHAR(120) NOT NULL,

    city VARCHAR(120) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_employee_email
    ON employees(email);

CREATE INDEX idx_employee_city
    ON employees(city);

CREATE INDEX idx_employee_job_title
    ON employees(job_title);