package dk.aau.student.mea_a1b129.dish_it;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * IngredientRepository to store all of the ingredients used in recipes.
 */
class IngredientRepository {
    private static final String TAG = "IngredientRepository";
    private final DatabaseHelper dbHelper;

    /**
     * Class construct. Initializes a new repository to save ingredients.
     * @param context Context of the app.
     * @see Context
     */
    public IngredientRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    /**
     * Insert a new ingredient into the Ingredients Table in the Database.
     * @param name The name of the Ingredients tag.
     * @param category the category of the ingredients, e.g. meat, vegetable, fruit.
     * @see Ingredient
     */
    public boolean insertIngredient(String name, Ingredient.Category category) {
        ContentValues cv = new ContentValues();
        cv.put(Ingredient.KEY_NAME, name);
        cv.put(Ingredient.KEY_CATEGORY, category.name());
        Log.i("SQL", "IngredientRepository: insertIngredient() Accessing database");
        dbHelper.getWritableDatabase().insert(Ingredient.TABLE_NAME, null, cv);
        return true;
    }

    /**
     * Update a new ingredient into the Ingredients Table in the Database.
     * @param name The name of the Ingredients tag.
     * @param category the category of the ingredients, e.g. meat, vegetable, fruit.
     * @see Ingredient
     */
    public boolean updateIngredient(int id, String name, Ingredient.Category category) {
        ContentValues cv = new ContentValues();
        cv.put(Ingredient.KEY_NAME, name);
        cv.put(Ingredient.KEY_CATEGORY, category.name());
        try {
            Log.i(TAG, "SQL: UpdateIngredientsTag() Accessing database");
            dbHelper.getWritableDatabase().update(Ingredient.TABLE_NAME, cv, Ingredient.KEY_ID + " = ? ", new String[]{Integer.toString(id)});
            return true;
        } catch(NumberFormatException e) {
            Log.e(TAG, "Couldn't parse String to number in updateIngredientsTag" + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            dbHelper.close();
        }
    }

    /**
     * Returns a specific ingredient at id.
     * @param id the id of the ingredient to return.
     * @return returns ingredient at the specified id in the database as Cursor-object.
     * @see Cursor
     * @see Ingredient
     */
    public Ingredient getIngredient(int id) {
        Ingredient i = new Ingredient();
        Log.i("SQL", "IngredientRepository: getIngredient() Accessing database");
        Cursor result = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Ingredient.TABLE_NAME + " WHERE " +
                Ingredient.KEY_ID + " = ?", new String[]{Integer.toString(id)});
        
        if(result.moveToFirst()) {
            i.setName(result.getString(result.getColumnIndex(Ingredient.KEY_NAME)));
            i.setCategory(result.getString(result.getColumnIndex(Ingredient.KEY_CATEGORY)));
            i.setIngredientsID(id);
            result.close();
            return i;
        } else {
            i.setName("Unknown ingredient");
            i.setCategory(Ingredient.Category.Other.name());
            result.close();
            return i;
        }
    }

    /**
     * Returns all of the ingredients tags in the table as a Cursor.
     * @return ingredients returned in a Ingredients-class list
     * @see Ingredient
     */
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredientList = new ArrayList<>();
        Log.i("SQL", "IngredientRepository: getAllIngredients() Accessing database");
        Cursor results = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + Ingredient.TABLE_NAME, null);
        if (results.moveToFirst()) {
            do {
                Ingredient i = new Ingredient();
                try {
                    Log.v(TAG, "IngredientID " + Integer.parseInt(results.getString(results.getColumnIndex(Ingredient.KEY_ID))));
                    i.setIngredientsID(Integer.parseInt(results.getString(results.getColumnIndex(Ingredient.KEY_ID))));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                i.setName(results.getString(results.getColumnIndex(Ingredient.KEY_NAME)));
                i.setCategory(results.getString(results.getColumnIndex(Ingredient.KEY_CATEGORY)));
                ingredientList.add(i);
            }
            while (results.moveToNext());
            results.close();
            return ingredientList;
        }
        else {
            results.close();
            ingredientList.add(new Ingredient("No ingredients available :(", Ingredient.Category.Other));
            return ingredientList;
        }
    }

    /**
     * Delete an ingredient with given ID.
     * @param id The id of the ingredient that needs to be deleted.
     * @return the amount of rows affected by the deletion.
     */
    public Integer deleteIngredient(int id) {
        Log.i(TAG, "IngredientRepository: deleteIngredient() Accessing database " + this.toString());
        return dbHelper.getWritableDatabase().delete(Ingredient.TABLE_NAME, Ingredient.KEY_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }
}
