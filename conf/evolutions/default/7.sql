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

ALTER TABLE leafs ADD leaf_id serial PRIMARY KEY;

# --- !Downs

DROP TABLE IF EXISTS leafs;;
