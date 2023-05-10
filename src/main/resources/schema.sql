CREATE TABLE IF NOT EXISTS station
(
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    name    VARCHAR(255) NOT NULL UNIQUE,
    line_id BIGINT       NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS line
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS section
(
    id               BIGINT NOT NULL AUTO_INCREMENT,
    start_station_id BIGINT NOT NULL,
    end_station_id   BIGINT NOT NULL,
    line_id          BIGINT NOT NULL,
    distance         INT    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS subway
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    line_id    BIGINT NOT NULL,
    station_id BIGINT NOT NULL,
    PRIMARY KEY (id)
)
