-- 1. Create Profile Table
CREATE TABLE profile (
                         id VARCHAR(36) PRIMARY KEY, -- Matches String id with UUID generator
                         name VARCHAR(255),
                         surname VARCHAR(255),
                         username VARCHAR(255) NOT NULL UNIQUE,
                         temp_username VARCHAR(255),
                         password VARCHAR(255) NOT NULL,
                         photo_id VARCHAR(36),
                         status VARCHAR(50) DEFAULT 'ACTIVE',
                         visible BOOLEAN DEFAULT true,
                         created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- 2. Create Profile Role Table
CREATE TABLE profile_role (
                              id VARCHAR(36) PRIMARY KEY,
                              profile_id VARCHAR(36) NOT NULL,
                              roles VARCHAR(255), -- Enumerated as STRING
                              created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
                              CONSTRAINT fk_profile_role_profile
                                  FOREIGN KEY (profile_id)
                                      REFERENCES profile(id)
                                      ON DELETE CASCADE
);