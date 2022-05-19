create table if not exists post(
	id serial primary key,
	name text,
	link text unique,
	description text,
	created_date timestamp
);
delete from post;
select * from post;
