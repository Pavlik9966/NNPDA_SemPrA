--changeset db-data:1
CREATE TABLE users
(
    user_id  SERIAL PRIMARY KEY,
    username VARCHAR(256) UNIQUE NOT NULL,
    password VARCHAR(256)        NOT NULL
);

CREATE TABLE meters
(
    meter_id SERIAL PRIMARY KEY,
    name     VARCHAR(256) UNIQUE                NOT NULL,
    user_id  INTEGER REFERENCES users (user_id) NOT NULL
);

CREATE TABLE sensors
(
    sensor_id SERIAL PRIMARY KEY,
    name      VARCHAR(256) UNIQUE                  NOT NULL,
    type      VARCHAR(256)                         NOT NULL,
    meter_id  INTEGER REFERENCES meters (meter_id) NOT NULL
);