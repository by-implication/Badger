# --- !Ups

CREATE TABLE stuff (
  id serial PRIMARY KEY,
  content TEXT
);;

# --- !Downs

DROP TABLE IF EXISTS stuff;;
