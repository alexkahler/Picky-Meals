package dk.aau.student.mea_a1b129.picky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class IngredientRepository {

    private DatabaseHelper dbHelper;

    /**
     * Class construct. Initializes a new repository to save ingredients.
     * @param context Context of the app.
     * @see Context
     */
    public IngredientRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Insert a new ingredient into the Ingredients Table in the Database.
     * @param name The name of the Ingredients tag.
     * @param category the category of the ingredients, e.g. meat, vegetable, fruit.
     */
    public boolean insertIngredient(String name, String category, String description) {
        ContentValues cv = new ContentValues();
        cv.put(Ingredient.KEY_NAME, name);
        cv.put(Ingredient.KEY_CATEGORY, category);
        cv.put(Ingredient.KEY_DESCRIPTION, description);
        dbHelper.getWritableDatabase().insert(Ingredient.TABLE_NAME, null, cv);
        return true;
    }

    /**
     * Update a new ingredient into the Ingredients Table in the Database.
     * @param name The name of the Ingredients tag.
     * @param category the category of the ingredients, e.g. meat, vegetable, fruit.
     */
    public boolean updateIngredientsTag(int id, String name, String category, String description) {
        ContentValues cv = new ContentValues();
        cv.put(Ingredient.KEY_NAME, name);
        cv.put(Ingredient.KEY_CATEGORY, category);
        cv.put(Ingredient.KEY_DESCRIPTION, description);
        try {
            dbHelper.getWritableDatabase().update(Ingredient.TABLE_NAME, cv, Ingredient.KEY_ID + " = ? ", new String[]{Integer.toString(id)});
            return true;
        } catch(NumberFormatException e) {
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
     */
    public Ingredient getIngredient(int id) {
        Ingredient i = new Ingredient();
        Cursor result = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM " + Ingredient.TABLE_NAME + " WHERE " +
                Ingredient.KEY_ID + " = ?", new String[]{Integer.toString(id)});
        
        if(result.moveToFirst()) {
            i.setName(result.getString(result.getColumnIndex(Ingredient.KEY_NAME)));
            i.setCategory(result.getString(result.getColumnIndex(Ingredient.KEY_CATEGORY)));
            i.setDescription(result.getString(result.getColumnIndex(Ingredient.KEY_DESCRIPTION)));
            i.setIngredientsID(id);
            return i;
        } else {
            i.setName("Couldn't find ingredient");
            return i;
        }
    }

    /**
     * Returns all of the ingredients tags in the table as a Cursor.
     * @return ingredients returned in a Ingredients-clas list
     * @see Ingredient
     */
    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredientList = new ArrayList<>();
        Cursor results = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM " + Ingredient.TABLE_NAME, null);
        if (results.moveToFirst()) {
            do {
                Ingredient i = new Ingredient();
                try {
                    i.setIngredientsID(Integer.parseInt(results.getString(results.getColumnIndex(Ingredient.KEY_ID))));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                i.setName(results.getColumnName(results.getColumnIndex(Ingredient.KEY_NAME)));
                i.setDescription(results.getColumnName(results.getColumnIndex(Ingredient.KEY_DESCRIPTION)));
                i.setCategory(results.getColumnName(results.getColumnIndex(Ingredient.KEY_CATEGORY)));
            }
            while (results.moveToNext());
            results.close();
            return ingredientList;
        }
        else {
            results.close();
            return null;
        }
    }

    /**
     * Delete an ingredient with given ID.
     * @param id The id of the ingredient that needs to be deleted.
     */
    public Integer deleteIngredient(int id) {
        return dbHelper.getWritableDatabase().delete(Ingredient.TABLE_NAME, Ingredient.KEY_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }
}
