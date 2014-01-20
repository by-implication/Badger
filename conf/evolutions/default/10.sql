# --- !Ups

UPDATE categorys SET category_name = 'education' WHERE category_name = 'academia';;

# --- !Downs

UPDATE categorys SET category_name = 'academia' WHERE category_name = 'education';;
