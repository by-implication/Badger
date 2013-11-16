# --- !Ups

CREATE TABLE ratings (
	rating_id serial PRIMARY KEY,
  user_id int NOT NULL REFERENCES users,
  leaf_id int NOT NULL REFERENCES leafs,
  rating_stars int NOT NULL,
  UNIQUE(user_id, leaf_id)
);;

# --- !Downs

DROP TABLE IF EXISTS ratings;;
