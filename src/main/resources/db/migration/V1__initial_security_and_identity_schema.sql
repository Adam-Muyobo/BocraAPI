-- Creates the core BOCRA identity and auth token tables managed by Flyway.
CREATE TABLE users (
    uuid CHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    username VARCHAR(100) NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    account_status VARCHAR(30) NOT NULL,
    is_enabled BIT NOT NULL,
    is_account_non_locked BIT NOT NULL,
    is_credentials_non_expired BIT NOT NULL,
    is_account_non_expired BIT NOT NULL,
    email_verified_at TIMESTAMP(6) NULL,
    last_login_at TIMESTAMP(6) NULL,
    PRIMARY KEY (uuid),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username)
);

CREATE TABLE persons (
    uuid CHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    forenames VARCHAR(120) NOT NULL,
    surname VARCHAR(120) NOT NULL,
    middle_names VARCHAR(150) NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(30) NOT NULL,
    nationality VARCHAR(120) NOT NULL,
    national_id_type VARCHAR(40) NOT NULL,
    national_id_number VARCHAR(120) NOT NULL,
    passport_number VARCHAR(120) NULL,
    phone_number VARCHAR(30) NOT NULL,
    alternate_phone_number VARCHAR(30) NULL,
    residential_address_line1 VARCHAR(255) NOT NULL,
    residential_address_line2 VARCHAR(255) NULL,
    city VARCHAR(120) NOT NULL,
    district VARCHAR(120) NOT NULL,
    country VARCHAR(120) NOT NULL,
    postal_code VARCHAR(30) NULL,
    occupation VARCHAR(120) NULL,
    organization_name VARCHAR(180) NULL,
    profile_photo_url VARCHAR(500) NULL,
    user_uuid CHAR(36) NOT NULL,
    PRIMARY KEY (uuid),
    CONSTRAINT uk_persons_user_uuid UNIQUE (user_uuid),
    CONSTRAINT fk_persons_user_uuid FOREIGN KEY (user_uuid) REFERENCES users (uuid)
);

CREATE TABLE refresh_tokens (
    uuid CHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    revoked_at TIMESTAMP(6) NULL,
    replaced_by_token_uuid CHAR(36) NULL,
    user_uuid CHAR(36) NOT NULL,
    PRIMARY KEY (uuid),
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_user_uuid FOREIGN KEY (user_uuid) REFERENCES users (uuid)
);

CREATE INDEX idx_refresh_tokens_user_uuid ON refresh_tokens (user_uuid);

CREATE TABLE email_verification_tokens (
    uuid CHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    token_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMP(6) NOT NULL,
    consumed_at TIMESTAMP(6) NULL,
    user_uuid CHAR(36) NOT NULL,
    PRIMARY KEY (uuid),
    CONSTRAINT uk_email_verification_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_email_verification_tokens_user_uuid FOREIGN KEY (user_uuid) REFERENCES users (uuid)
);

CREATE INDEX idx_email_verification_tokens_user_uuid ON email_verification_tokens (user_uuid);
