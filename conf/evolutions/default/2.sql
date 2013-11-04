# --- !Ups

INSERT INTO stuff VALUES (1, 'bleh');;

# --- !Downs

DELETE FROM stuff WHERE id = 1 AND content = 'bleh';;
