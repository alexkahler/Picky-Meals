BEGIN TRANSACTION;
CREATE TABLE MealsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, cuisine TEXT, rating FLOAT, ingredients_id TEXT, date DATE, price DOUBLE);
INSERT INTO `MealsTable` (_id,name,description,cuisine,rating,ingredients_id,date) VALUES (1,'Steak','Man food','Le french',5,'1,2,101,143','16-11-2015','65');
INSERT INTO `MealsTable` (_id,name,description,cuisine,rating,ingredients_id,date) VALUES (2,'Chicken & Potatoes','Healthy','',4,'140,37,2,11,61,115','16-11-2015','45');
INSERT INTO `MealsTable` (_id,name,description,cuisine,rating,ingredients_id,date) VALUES (3,'Beefsteak mince','Meat & potatoes','',4,'1,70,67,149','16-11-2015','38');
INSERT INTO `MealsTable` (_id,name,description,cuisine,rating,ingredients_id,date) VALUES (4,'Cutlets','Pork','',3,'11,12,37,101,110,142','16-11-2015','43');
INSERT INTO `MealsTable` (_id,name,description,cuisine,rating,ingredients_id,date) VALUES (5,'Lasagne','Italian','',4,'143,1,32,3,70,146,152,100','16-11-2015','35');
INSERT INTO `MealsTable` (_id,name,description,cuisine,rating,ingredients_id,date) VALUES (6,'Meatballs','Meatballs & potatoes','',5,'1,95,70,145,147,112','16-11-2015','30');
INSERT INTO `MealsTable` (_id,name,description,cuisine,rating,ingredients_id,date) VALUES (7,'Chopped Steak','Meat','',4,'147,143,1,101,70','16-11-2015','49');
INSERT INTO `MealsTable` (_id,name,description,cuisine,rating,ingredients_id,date) VALUES (8,'Pizza','Italian','',5,'147,1,67,94,100,3','16-11-2015','55');
COMMIT;