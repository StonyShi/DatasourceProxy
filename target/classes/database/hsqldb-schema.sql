
CREATE TABLE IF NOT EXISTS users (
    id int not null IDENTITY,
    name varchar(80),
    birthday TIMESTAMP,
    constraint pk_users primary key (id)
);

CREATE TABLE IF NOT EXISTS orders (
    id int not null IDENTITY,
    order_no varchar(80),
    create_date TIMESTAMP,
    constraint pk_orders primary key (id)
);