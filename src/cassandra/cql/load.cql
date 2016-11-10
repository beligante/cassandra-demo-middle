CREATE KEYSPACE demo WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3};

CREATE TABLE IF NOT EXISTS demo.users (
	user_id uuid,
	email text,
	first_name text,
	last_name text,
	since timestamp,
	PRIMARY KEY (user_id));

CREATE TABLE IF NOT EXISTS demo.user_credentials (
	user_id uuid,
	email text,
	password text,
	PRIMARY KEY (email));

CREATE TABLE IF NOT EXISTS demo.photos (
	photo_id uuid,
	user_id uuid,
	name text,
	description text,
	bytes blob,
	tags set<text>,
	added_date timestamp,
	PRIMARY KEY (photo_id));

CREATE TABLE IF NOT EXISTS demo.thumbnail (
	photo_id uuid,
	bytes blob,
	PRIMARY KEY (photo_id));

CREATE TABLE IF NOT EXISTS demo.photos_by_user_id (
	user_id uuid,
	added_date timestamp,
	photo_id uuid,
	photo_name text,
	PRIMARY KEY (user_id, added_date, photo_id));

CREATE TABLE IF NOT EXISTS demo.latest_photos (
	ddmmyyyy text,
	added_date timestamp,
	photo_id uuid,
	photo_name text,
	PRIMARY KEY (ddmmyyyy, added_date, photo_id)
) WITH CLUSTERING ORDER BY (added_date DESC, photo_id ASC);

CREATE TABLE IF NOT EXISTS demo.photo_rank (
	photo_id uuid,
	stars varchar,
	votes counter,
	PRIMARY KEY(stars, photo_id));

CREATE TABLE IF NOT EXISTS demo.session (
	session_id uuid,
	user_id uuid,
	PRIMARY KEY(session_id));