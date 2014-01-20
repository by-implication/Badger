# --- !Ups

UPDATE categorys SET category_name = 'education' WHERE category_name = 'academia';;

CREATE INDEX leafs_leaf_dpt_dsc ON leafs(leaf_dpt_dsc);;

ALTER TABLE leafs ADD COLUMN leaf_last_activity timestamp;;

# --- !Downs

ALTER TABLE leafs DROP COLUMN IF EXISTS leaf_last_activity;;

DROP INDEX IF EXISTS leafs_leaf_dpt_dsc;;

UPDATE categorys SET category_name = 'academia' WHERE category_name = 'education';;
