CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  TEXT,
    requestor_id BIGINT REFERENCES users (id),
    created      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_requestor FOREIGN KEY (requestor_id) references users (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                            NOT NULL,
    description  TEXT,
    is_available BOOLEAN,
    owner_id     BIGINT REFERENCES users (id),
    request_id   BIGINT REFERENCES requests (id),
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_item_owner FOREIGN KEY (owner_id) references users (id),
    CONSTRAINT fk_request FOREIGN KEY (request_id) references requests (id)
);

create table if not exists bookings
(
    id         bigint generated by default as identity not null,
    start_date timestamp without time zone             not null,
    end_date   timestamp without time zone             not null,
    item_id    bigint references items (id)            not null,
    booker_id  bigint references users (id)            not null,
    status     varchar(255)                            not null,
    constraint pk_booking primary key (id),
    constraint fk_item foreign key (item_id) references items (id),
    constraint fk_booker foreign key (booker_id) references users (id)
);

create table if not exists comments
(
    id        bigint generated by default as identity not null,
    comment   text,
    item_id   bigint references items (id)            not null,
    author_id bigint references users (id)            not null,
    created   timestamp without time zone             not null,
    constraint pk_comments primary key (id),
    constraint fk_item_id foreign key (item_id) references items (id),
    constraint fk_author foreign key (author_id) references users (id)

);





