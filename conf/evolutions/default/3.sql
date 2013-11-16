# --- !Ups

CREATE TABLE users (
  user_id serial PRIMARY KEY,
  user_handle text,
  user_email text,
  user_password text
);;

# --- !Downs

DROP TABLE IF EXISTS users;;
