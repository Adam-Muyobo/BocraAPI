-- Refactors the initial identity schema for username-first onboarding and organization support.

ALTER TABLE users
    ADD COLUMN user_type VARCHAR(30) NOT NULL DEFAULT 'INDIVIDUAL';

ALTER TABLE users
    ADD COLUMN profile_completed BIT NOT NULL DEFAULT 0;

UPDATE users
SET username = CONCAT('user_', REPLACE(uuid, '-', ''))
WHERE username IS NULL OR TRIM(username) = '';

ALTER TABLE users
    MODIFY COLUMN username VARCHAR(100) NOT NULL;

ALTER TABLE persons
    ADD COLUMN identity_number VARCHAR(120) NULL;

UPDATE persons
SET identity_number = CASE
    WHEN passport_number IS NOT NULL AND TRIM(passport_number) <> '' THEN passport_number
    ELSE national_id_number
END;

ALTER TABLE persons
    MODIFY COLUMN identity_number VARCHAR(120) NOT NULL;

ALTER TABLE persons
    DROP COLUMN middle_names;

ALTER TABLE persons
    DROP COLUMN national_id_number;

ALTER TABLE persons
    DROP COLUMN passport_number;

CREATE TABLE organizations (
    uuid CHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    display_name VARCHAR(180) NOT NULL,
    trading_name VARCHAR(180) NULL,
    registration_number VARCHAR(120) NULL,
    tax_identifier VARCHAR(120) NULL,
    contact_email VARCHAR(150) NULL,
    contact_phone_number VARCHAR(30) NULL,
    address_line1 VARCHAR(255) NULL,
    address_line2 VARCHAR(255) NULL,
    city VARCHAR(120) NULL,
    district VARCHAR(120) NULL,
    country VARCHAR(120) NULL,
    postal_code VARCHAR(30) NULL,
    logo_url VARCHAR(500) NULL,
    owner_user_uuid CHAR(36) NOT NULL,
    PRIMARY KEY (uuid),
    CONSTRAINT uk_organizations_owner_user_uuid UNIQUE (owner_user_uuid),
    CONSTRAINT fk_organizations_owner_user_uuid FOREIGN KEY (owner_user_uuid) REFERENCES users (uuid)
);

CREATE TABLE organization_contact_people (
    uuid CHAR(36) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    forenames VARCHAR(120) NOT NULL,
    surname VARCHAR(120) NOT NULL,
    email VARCHAR(150) NULL,
    phone_number VARCHAR(30) NULL,
    job_title VARCHAR(120) NULL,
    primary_contact BIT NOT NULL,
    organization_uuid CHAR(36) NOT NULL,
    linked_user_uuid CHAR(36) NULL,
    PRIMARY KEY (uuid),
    CONSTRAINT fk_org_contact_organization_uuid FOREIGN KEY (organization_uuid) REFERENCES organizations (uuid),
    CONSTRAINT fk_org_contact_linked_user_uuid FOREIGN KEY (linked_user_uuid) REFERENCES users (uuid)
);

CREATE INDEX idx_org_contact_organization_uuid ON organization_contact_people (organization_uuid);
