DROP TABLE IF EXISTS compilation_events;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS compilations;
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
    category_id BIGINT NOT NULL,
    initiator_id BIGINT NOT NULL,
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

    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_event_user FOREIGN KEY (initiator_id) REFERENCES users(id)
);

CREATE TABLE requests (
    id BIGSERIAL PRIMARY KEY,
    created TIMESTAMP NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT uq_request UNIQUE (requester_id, event_id),
    CONSTRAINT fk_request_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_request_user FOREIGN KEY (requester_id) REFERENCES users(id)
);

CREATE TABLE compilations (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    pinned BOOLEAN NOT NULL
);

CREATE TABLE compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    CONSTRAINT fk_comp_event_comp FOREIGN KEY (compilation_id) REFERENCES compilations(id),
    CONSTRAINT fk_comp_event_event FOREIGN KEY (event_id) REFERENCES events(id)
);