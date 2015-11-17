BEGIN TRANSACTION;
CREATE TABLE MealsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, cuisine TEXT, rating INTEGER, ingredients_id TEXT, date DATE);
INSERT INTO `MealsTable` (_id,name,description,cuisine,rating,ingredients_id,date) VALUES (1,'Steak','Man food','Le french',5,'1, 2','');
COMMIT;
