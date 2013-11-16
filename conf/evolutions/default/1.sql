# --- !Ups

CREATE TABLE stuff (
  id serial PRIMARY KEY,
  content text
);;

# --- !Downs

DROP TABLE IF EXISTS stuff;;
