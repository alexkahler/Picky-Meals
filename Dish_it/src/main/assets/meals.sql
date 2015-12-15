BEGIN TRANSACTION;
CREATE TABLE MealsTable (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, cuisine TEXT, rating INTEGER, ingredients_id TEXT, date DATE, price DOUBLE);
COMMIT;
