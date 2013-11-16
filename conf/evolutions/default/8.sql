# --- !Ups

CREATE TABLE clicks (
	click_id serial PRIMARY KEY,
  user_id int NOT NULL REFERENCES users,
  leaf_id int NOT NULL REFERENCES leafs,
  click_lat numeric(10,7) NOT NULL,
  click_lng numeric(10,7) NOT NULL,
  UNIQUE(user_id, leaf_id)
);;

# --- !Downs

DROP TABLE IF EXISTS clicks;;
