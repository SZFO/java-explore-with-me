DROP TABLE IF EXISTS HITS CASCADE;

CREATE TABLE IF NOT EXISTS HITS
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app       VARCHAR                                 NOT NULL,
    uri       VARCHAR                                 NOT NULL,
    ip        VARCHAR                                 NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE
);