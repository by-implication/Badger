# --- !Ups

CREATE TABLE nodes (
  node_id serial PRIMARY KEY,
  node_parent int REFERENCES nodes(node_id),
  node_content text
);;

INSERT INTO nodes (node_id, node_parent, node_content) VALUES
	(DEFAULT, NULL, 'node1'),
	(DEFAULT, 1, 'node2'),
	(DEFAULT, 1, 'node3'),
	(DEFAULT, 2, 'node4'),
	(DEFAULT, 2, 'node5'),
	(DEFAULT, NULL, 'node6'),
	(DEFAULT, 6, 'node7'),
	(DEFAULT, 6, 'node8'),
	(DEFAULT, 6, 'node9'),
	(DEFAULT, 9, 'node10')
;;

# --- !Downs

DROP TABLE IF EXISTS nodes;;
