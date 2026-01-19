DROP TABLE IF EXISTS compilation_events;
DROP TABLE IF EXISTS compilations;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description TEXT,
    annotation TEXT,

    category_id BIGINT NOT NULL REFERENCES categories(id),
    initiator_id BIGINT NOT NULL REFERENCES users(id),

    event_date TIMESTAMP NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER NOT NULL,
    request_moderation BOOLEAN NOT NULL,

    state VARCHAR(20) NOT NULL,

    created_on TIMESTAMP NOT NULL,
    published_on TIMESTAMP,

    lat DOUBLE PRECISION,
    lon DOUBLE PRECISION,

    confirmed_requests BIGINT NOT NULL DEFAULT 0,
);

CREATE TABLE requests (
    id BIGSERIAL PRIMARY KEY,
    created TIMESTAMP NOT NULL,

    event_id BIGINT NOT NULL REFERENCES events(id),
    requester_id BIGINT NOT NULL REFERENCES users(id),

    status VARCHAR(20) NOT NULL,

    CONSTRAINT uq_request UNIQUE (requester_id, event_id)
);

CREATE TABLE compilations (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    pinned BOOLEAN NOT NULL
);

CREATE TABLE compilation_events (
    compilation_id BIGINT NOT NULL REFERENCES compilations(id),
    event_id BIGINT NOT NULL REFERENCES events(id),
    PRIMARY KEY (compilation_id, event_id)
);