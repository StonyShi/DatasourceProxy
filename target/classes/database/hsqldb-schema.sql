
CREATE TABLE IF NOT EXISTS users (
    id int not null IDENTITY,
    name varchar(80),
    birthday TIMESTAMP,
    constraint pk_users primary key (id)
);
