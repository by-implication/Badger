# --- !Ups

CREATE TABLE leafs(
  leaf_dpt_cd text,
  leaf_dpt_dsc text,
  leaf_agy_type text,
  leaf_owner_cd text,
  leaf_owner_dsc text,
  leaf_fpap_cd text,
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

COPY leafs FROM '/haha-absolute-path/gaa_na.csv' DELIMITER ',' CSV ENCODING 'ISO_8859_9';

ALTER TABLE leafs
	ADD leaf_id serial PRIMARY KEY,
	ADD leaf_stars int NOT NULL DEFAULT 0,
	ADD leaf_ratings int NOT NULL DEFAULT 0
;;

CREATE INDEX leafs_leaf_area_dsc ON leafs(leaf_area_dsc);;
CREATE INDEX leafs_leaf_ps ON leafs(leaf_ps);;
CREATE INDEX leafs_leaf_mooe ON leafs(leaf_mooe);;
CREATE INDEX leafs_leaf_co ON leafs(leaf_co);;

# --- !Downs

DROP TABLE IF EXISTS leafs;;
