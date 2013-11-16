# --- !Ups

CREATE TABLE gaa_na(
  dpt_cd text,
  dpt_dsc text,
  agy_type text,
  owner_cd text,
  owner_dsc text,
  fpap_cd text,
  fpap_dsc text,
  area_cd text,
  area_dsc text,
  ps integer,
  mooe integer,
  co integer,
  net integer,
  year integer,
  type text
);;

COPY gaa_na FROM '/haha-absolute-path/gaa_na.csv' DELIMITER ',' CSV ENCODING 'ISO_8859_9';

# --- !Downs

DROP TABLE IF EXISTS gaa_na;;
