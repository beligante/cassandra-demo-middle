CREATE KEYSPACE demo WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3};

CREATE TABLE demo.users (
	user_id uuid,
	email text,
	first_name text,
	last_name text,
	since timestamp,
	PRIMARY KEY (user_id));

CREATE TABLE demo.user_credentials (
	user_id uuid,
	email text,
	password text,
	PRIMARY KEY (email));
	
CREATE TABLE demo.photos (
	photo_id uuid,
	user_id uuid,
	name text,
	description text,
	bytes blob,
	tags set<text>,
	added_date timestamp,
	PRIMARY KEY (photo_id));
	
CREATE TABLE demo.thumbnail (
	photo_id uuid,
	bytes blob,
	PRIMARY KEY (photo_id));

CREATE TABLE demo.photos_by_user_id (
	user_id uuid,
	added_date timestamp,
	photo_id uuid,
	photo_name text,
	PRIMARY KEY (user_id, added_date, photo_id));
	
CREATE TABLE demo.latest_photos (
	ddmmyyyy text,
	added_date timestamp,
	photo_id uuid,
	photo_name text,
	PRIMARY KEY (ddmmyyyy, added_date, photo_id));
	
CREATE TABLE demo.photo_rank (
	photo_id uuid,
	stars varchar,
	votes counter,
	PRIMARY KEY(stars, photo_id));