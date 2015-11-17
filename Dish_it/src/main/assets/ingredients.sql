BEGIN TRANSACTION;
CREATE TABLE IngredientsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, category TEXT);
INSERT INTO `IngredientsTable` (_id,name,category) VALUES (1,'Onion','Vegetable');
INSERT INTO `IngredientsTable` (_id,name,category) VALUES (2,'Potato','Vegetable');
COMMIT;
