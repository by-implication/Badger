# --- !Ups

CREATE TABLE users (
  user_id serial PRIMARY KEY,
  user_handle text NOT NULL UNIQUE,
  user_email text NOT NULL UNIQUE,
  user_password text NOT NULL
);;

INSERT INTO users (user_id, user_handle, user_email, user_password) VALUES
	(DEFAULT, 'user1', 'email1@budget.ph', 'password'),
	(DEFAULT, 'user2', 'email2@budget.ph', 'password'),
	(DEFAULT, 'user3', 'email3@budget.ph', 'password')
;;

# --- !Downs

DROP TABLE IF EXISTS users;;
