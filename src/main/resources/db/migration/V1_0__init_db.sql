create table IF NOT EXISTS socks(
    id serial primary key,
    color varchar(50) not null,
    cotton_percentage decimal not null,
    amount integer not null
);