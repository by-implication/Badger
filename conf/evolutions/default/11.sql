# --- !Ups

CREATE INDEX leafs_leaf_dpt_dsc ON leafs(leaf_dpt_dsc);;

# --- !Downs

DROP INDEX IF EXISTS leafs_leaf_dpt_dsc;;
