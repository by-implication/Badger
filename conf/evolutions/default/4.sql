# --- !Ups

CREATE EXTENSION ltree;;

CREATE TABLE leafs(
  leaf_dpt_cd text,
  leaf_dpt_dsc text,
  leaf_agy_type text,
  leaf_owner_cd text,
  leaf_owner_dsc text,
  leaf_fpap_cd ltree,
  leaf_fpap_dsc text,
  leaf_area_cd text,
  leaf_area_dsc text,
  leaf_ps integer,
  leaf_mooe integer,
  leaf_co integer,
  leaf_net integer,
  leaf_year integer,
  leaf_kind text
);;

COPY leafs FROM 'gaa_na.csv' DELIMITER ',' CSV ENCODING 'ISO_8859_9';

ALTER TABLE leafs
	ADD leaf_id serial PRIMARY KEY,
	ADD leaf_stars int NOT NULL DEFAULT 0,
	ADD leaf_ratings int NOT NULL DEFAULT 0
;;

CREATE INDEX leafs_leaf_area_dsc ON leafs(leaf_area_dsc);;
CREATE INDEX leafs_leaf_ps ON leafs(leaf_ps);;
CREATE INDEX leafs_leaf_mooe ON leafs(leaf_mooe);;
CREATE INDEX leafs_leaf_co ON leafs(leaf_co);;
CREATE INDEX leafs_leaf_net ON leafs(leaf_net);;
CREATE INDEX leafs_leaf_year ON leafs(leaf_year);;
CREATE INDEX leafs_leaf_kind ON leafs(leaf_kind);;

# --- !Downs

DROP INDEX IF EXISTS leafs_leaf_kind;;
DROP INDEX IF EXISTS leafs_leaf_year;;
DROP INDEX IF EXISTS leafs_leaf_net;;
DROP INDEX IF EXISTS leafs_leaf_co;;
DROP INDEX IF EXISTS leafs_leaf_mooe;;
DROP INDEX IF EXISTS leafs_leaf_ps;;
DROP INDEX IF EXISTS leafs_leaf_area_dsc;;

DROP TABLE IF EXISTS leafs;;

DROP EXTENSION IF EXISTS ltree;;
