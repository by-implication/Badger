# --- !Ups

CREATE TABLE users (
  user_id serial PRIMARY KEY,
  user_handle text NOT NULL UNIQUE,
  user_email text NOT NULL UNIQUE,
  user_password text NOT NULL
);;

INSERT INTO users (user_id, user_handle, user_email, user_password) VALUES
	(DEFAULT, 'user1', 'email1@budget.ph', '$2a$06$A3DWi9zLqGCtVNX2yVBOcuxaZ3lSvRVJySQ8o/ZAExTTSymuBMqI2'),
	(DEFAULT, 'user2', 'email2@budget.ph', '$2a$06$A3DWi9zLqGCtVNX2yVBOcuxaZ3lSvRVJySQ8o/ZAExTTSymuBMqI2'),
	(DEFAULT, 'user3', 'email3@budget.ph', '$2a$06$A3DWi9zLqGCtVNX2yVBOcuxaZ3lSvRVJySQ8o/ZAExTTSymuBMqI2')
;;

# --- !Downs

DROP TABLE IF EXISTS users;;
