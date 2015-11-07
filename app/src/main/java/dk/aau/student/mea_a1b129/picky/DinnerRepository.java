package dk.aau.student.mea_a1b129.picky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
Dinner repository to lookup dinners.
 */
public class DinnerRepository {

    private DatabaseHelper dbHelper;

    public DinnerRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Insert a new meal into the database.
     * @param name Name of the meal - must not be null
     * @param description description of the meal
     * @param cuisine which kind cuisine or category the meal belongs to
     * @param ingredientsID the ID of the ingredients - if null a new Ingredient will be made in the database
     * @param rating the meals rating
     * @return true if insert was successful
     */
    public boolean insertDinner(String name, String description, String cuisine, int ingredientsID, int rating) {
        ContentValues cv = new ContentValues();
        if(name !=null) {
            cv.put(Dinner.KEY_NAME, name); }
        else {
            return false; }
        cv.put(Dinner.KEY_DESCRIPTION, description);
        cv.put(Dinner.KEY_CUISINE, cuisine);
        cv.put(Dinner.KEY_INGREDIENTS_ID, ingredientsID);
        cv.put(Dinner.KEY_RATING, rating);
        dbHelper.getWritableDatabase().insert(Dinner.TABLE_NAME, null, cv);
        return true;
    }


    public void deleteDinner(int dinnerID) {
        dbHelper.getWritableDatabase().delete(Dinner.TABLE_NAME, Dinner.KEY_ID + " = ?",
                new String[]{Integer.toString(dinnerID)});
        dbHelper.close();
    }

    public Dinner getDinner(int dinnerID) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + Dinner.TABLE_NAME + " WHERE " + Dinner.KEY_ID + " = ?", new String[]{Integer.toString(dinnerID)});
        Log.d("DinnerRepository", "Doing a query");
        if(results.moveToFirst()) {
            Dinner d = new Dinner();
            do {
                d.setName(results.getString(results.getColumnIndex(Dinner.KEY_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.KEY_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.KEY_CUISINE)));
                d.setRating(results.getInt(results.getColumnIndex(Dinner.KEY_RATING)));
                String[] raw = results.getString(results.getColumnIndex(Dinner.KEY_INGREDIENTS_ID)).replaceAll("[^1-9,]", "").split(",");
                for(int i = 0; i < raw.length; i++) {
                    try {
                        d.addIngredients(Integer.parseInt(raw[i]));
                    } catch (NumberFormatException e) {
                        System.out.println("Couldn't parseIn at Dinner.getDinner " + e.getMessage());
                    }
                }
            }
            while (results.moveToNext());
            results.close();
            return d;
        }
        else {
            results.close();
            return null;
        }


    }

    /**
     *Returns a list of meals - if no meals are found returns null.
     */
    public List<Dinner> getAllDinners() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + Dinner.TABLE_NAME, null);
        if(results.moveToFirst()) {
            List<Dinner> dinnerList = new ArrayList<>();
            do {
                Dinner d = new Dinner();
                try {
                    d.setDinnerID(Integer.parseInt(results.getString(results.getColumnIndex(Dinner.KEY_ID)))); }
                catch(NumberFormatException e) {
                    System.out.println("Couldn't format number at DinnerRepository.getAllDinners: " + e.toString());
                    return null;
                }
                d.setName(results.getString(results.getColumnIndex(Dinner.KEY_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.KEY_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.KEY_CUISINE)));
                String[] raw = results.getString(results.getColumnIndex(Dinner.KEY_INGREDIENTS_ID)).replaceAll("[^1-9,]", "").split(",");
                for (int i = 0; i < raw.length; i++) {
                    try {
                        d.addIngredients(Integer.parseInt(raw[i]));
                    } catch(NumberFormatException e) {
                        System.out.println("Couldn't parseInt at Dinner.getAllDinners " + e.toString());
                    }
                }
                dinnerList.add(d);
            }
            while (results.moveToNext());
            results.close();
            return dinnerList;
        }
        else {
            results.close();
            return null;
        }
    }
}
