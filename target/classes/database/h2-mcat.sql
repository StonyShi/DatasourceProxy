CREATE TABLE IF NOT EXISTS cat (
    id int not null IDENTITY,
    name varchar(80),
    create_date TIMESTAMP,
    constraint pk_cat_id primary key (id)
);
