CREATE SCHEMA IF NOT EXISTS fm;

USE fm;

CREATE TABLE IF NOT EXISTS system_type (
    id   INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL
);


CREATE TABLE IF NOT EXISTS ne_site (
    id             INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(128) NOT NULL,
    system_type_id INT UNSIGNED NOT NULL,
    request_limit  INT UNSIGNED DEFAULT 500,
    time_limit     INT UNSIGNED DEFAULT 1,
    time_unit      ENUM ('MICROS', 'MILLIS', 'SECONDS', 'MINUTES', 'HOURS', 'DAYS', 'MONTHS', 'YEARS'),
    FOREIGN KEY (system_type_id) REFERENCES system_type (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS ne (
    id      INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(50)  NOT NULL UNIQUE,
    ip      VARCHAR(15)  NOT NULL UNIQUE,
    site_id INT UNSIGNED NOT NULL,

    INDEX (ip),
    FOREIGN KEY (site_id) REFERENCES ne_site (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS event_category (
    id           INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    code         VARCHAR(40) COMMENT 'pre-defined',
    site_id      INT UNSIGNED  NOT NULL,
    name         VARCHAR(128)  NOT NULL,
    message      VARCHAR(1000) NULL,
    ems_severity ENUM ('NORMAL', 'WARNING', 'MINOR', 'MAJOR', 'CRITICAL'),
    timeout      INT UNSIGNED  NULL,
    is_fault     BOOLEAN       NOT NULL,

    UNIQUE (code, site_id),
    FOREIGN KEY (site_id) REFERENCES ne_site (id) ON DELETE CASCADE,
    FULLTEXT (name, message)
);


CREATE TABLE IF NOT EXISTS fault (
    ne_id                      INT UNSIGNED NOT NULL,
    event_id                   INT UNSIGNED,
    event_code                 VARCHAR(40),
    severity                   ENUM ('NORMAL', 'WARNING', 'MINOR', 'MAJOR', 'CRITICAL'),
    ems_severity               ENUM ('NORMAL', 'WARNING', 'MINOR', 'MAJOR', 'CRITICAL'),
    sequence                   INT UNSIGNED NOT NULL,
    interface                  VARCHAR(254) NULL,
    source_type                VARCHAR(50),
    specific_problem           VARCHAR(512) NULL,
    isolated                   BOOL                  DEFAULT FALSE,
    isolation_history          TEXT         NULL,

    # FM API
    acked_by                   VARCHAR(50)  NULL,
    cleared_by                 VARCHAR(50)  NULL,
    ack_instant                DATETIME(6)  NULL,
    clear_instant              DATETIME(6)  NULL,

    # TIME LOG
    initial_instant            DATETIME(6)  NOT NULL DEFAULT NOW(6),
    trigger_instant            DATETIME(6)  NOT NULL DEFAULT NOW(6),
    insert_instant             DATETIME(6)  NOT NULL DEFAULT NOW(6),
    update_instant             DATETIME(6)  NOT NULL DEFAULT NOW(6),

    # VES COMMON HEADERS
    event_name                 VARCHAR(60),
    event_type                 VARCHAR(40),
    internal_header_fields     TEXT,
    nfc_naming_code            VARCHAR(40),
    nf_naming_code             VARCHAR(40),
    nf_vendor_name             VARCHAR(40),
    priority                   ENUM ('PRIORITY_NOT_PROVIDED', 'HIGH', 'MEDIUM', 'NORMAL', 'LOW'),
    reporting_entity_id        VARCHAR(40),
    reporting_entity_name      VARCHAR(60),
    source_id                  VARCHAR(40),
    source_name                VARCHAR(60),
    stnd_defined_namespace     VARCHAR(40),
    version                    VARCHAR(10),
    ves_event_listener_version VARCHAR(10),

    # VES EVENT FAULT FIELDS
    alarm_condition            VARCHAR(40),
    event_category             VARCHAR(60),
    fault_fields_version       VARCHAR(10),
    vf_status                  VARCHAR(40),
    additional_info            TEXT         NULL,

    PRIMARY KEY (ne_id, event_id, initial_instant),
    FOREIGN KEY (ne_id) REFERENCES ne (id) ON DELETE CASCADE,
    FULLTEXT (specific_problem)
);

CREATE TABLE IF NOT EXISTS event (
    ne_id                       INT UNSIGNED NOT NULL,
    event_code                  VARCHAR(40),
    severity                    ENUM ('NORMAL', 'WARNING', 'MINOR', 'MAJOR', 'CRITICAL'),
    ems_severity                ENUM ('NORMAL', 'WARNING', 'MINOR', 'MAJOR', 'CRITICAL'),
    sequence                    INT UNSIGNED NOT NULL,
    isolated                    BOOL         NOT NULL DEFAULT FALSE,
    isolation_history           TEXT         NULL,

    # TIME LOG
    initial_instant             DATETIME(6)  NOT NULL DEFAULT NOW(6),
    trigger_instant             DATETIME(6)  NOT NULL DEFAULT NOW(6),
    insert_instant              DATETIME(6)  NOT NULL DEFAULT NOW(6),
    update_instant              DATETIME(6)  NOT NULL DEFAULT NOW(6),

    # VES COMMON HEADERS
    event_id                    VARCHAR(40),
    event_name                  VARCHAR(60),
    event_type                  VARCHAR(40),
    internal_header_fields      TEXT,
    nfc_naming_code             VARCHAR(40),
    nf_naming_code              VARCHAR(40),
    nf_vendor_name              VARCHAR(40),
    priority                    ENUM ('PRIORITY_NOT_PROVIDED', 'HIGH', 'MEDIUM', 'NORMAL', 'LOW'),
    reporting_entity_id         VARCHAR(40),
    reporting_entity_name       VARCHAR(60),
    source_id                   VARCHAR(40),
    source_name                 VARCHAR(60),
    stnd_defined_namespace      VARCHAR(40),
    version                     VARCHAR(10),
    ves_event_listener_version  VARCHAR(10),

    additional_info             TEXT,
    array_of_named_hashmap      TEXT,
    change_contact              TEXT,
    change_identifier           TEXT,
    change_type                 TEXT,
    old_state                   TEXT,
    new_state                   TEXT,
    notification_fields_version TEXT,
    state_interface             TEXT,

    PRIMARY KEY (ne_id, event_id, initial_instant),
    FOREIGN KEY (ne_id) REFERENCES ne (id) ON DELETE CASCADE,
    FULLTEXT (additional_info)
);


CREATE TABLE IF NOT EXISTS user (
    id           VARCHAR(50) PRIMARY KEY,
    full_name    NVARCHAR(150),
    phone_number VARCHAR(11) NOT NULL,
    email        VARCHAR(45) NOT NULL
);


CREATE TABLE IF NOT EXISTS event_rule (
    id             INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(50) NOT NULL,
    type           VARCHAR(30),
    enabled        BOOLEAN DEFAULT TRUE,
    effective_time TEXT,
    `condition`    VARCHAR(15000) COMMENT 'JSON composite condition'
);


CREATE TABLE IF NOT EXISTS user_notification (
    rule_id INT UNSIGNED,
    user_id VARCHAR(50) NOT NULL,
    active  BOOLEAN DEFAULT TRUE,
    method  ENUM ('ALL', 'SMS', 'MAIL'),

    PRIMARY KEY (rule_id, user_id),
    FOREIGN KEY (rule_id) REFERENCES event_rule (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS param_code (
    `key`       VARCHAR(50) PRIMARY KEY,
    value       VARCHAR(200),
    description VARCHAR(200)
);


CREATE TABLE IF NOT EXISTS schedule_config (
    id            INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    cron_exp      VARCHAR(50),
    ne_list       VARCHAR(250),
    device_type   VARCHAR(50),
    stop_on_error BOOLEAN DEFAULT TRUE,
    command_list  TEXT
);
