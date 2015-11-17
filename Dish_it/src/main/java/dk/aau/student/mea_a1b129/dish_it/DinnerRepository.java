package dk.aau.student.mea_a1b129.dish_it;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 *         Dinner repository to lookup dinners.
 */
class DinnerRepository {

    private DatabaseHelper dbHelper;
    private List<Dinner> dinnerList;

    /**
     * Constructor for DinnerRepository. When initialized the class updates the latest list of dinners from the database.
     *
     * @param context Context of the application
     */
    public DinnerRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        dinnerList = new ArrayList<>();
        updateDinnerList();
    }

    /**
     * Insert a new meal into the database.
     *
     * @param name          Name of the meal - must not be null
     * @param description   description of the meal
     * @param cuisine       which kind cuisine or category the meal belongs to
     * @param ingredientsID the ID of the ingredients - if null a new Ingredient will be made in the database
     * @param rating        the meals rating
     * @return true if insert was successful
     */
    public boolean insertDinner(String name, String description, String cuisine, List<Integer> ingredientsID, int rating, Date date) {
        ContentValues cv = new ContentValues();
        if (name != null) {
            cv.put(Dinner.KEY_NAME, name);
        } else {
            return false;
        }
        cv.put(Dinner.KEY_DESCRIPTION, description);
        cv.put(Dinner.KEY_CUISINE, cuisine);
        cv.put(Dinner.KEY_INGREDIENTS_ID, ingredientsID.toString());
        cv.put(Dinner.KEY_RATING, rating);
        cv.put(Dinner.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(date));
        Log.d("SQL", "DinnerRepository, insertDinner() Accessing database");
        dbHelper.getWritableDatabase().insert(Dinner.TABLE_NAME, null, cv);
        dbHelper.close();
        return true;
    }

    /**
     * Update a Dinner entry in the database with the given Dinner ID.
     *
     * @param id           the Dinner ID of the entry you want to update. ID must be greater than or equal to 0.
     * @param name         the new name, which you want to give the Dinner entry
     * @param description  the new description of the Dinner
     * @param category     the new category of the Dinner
     * @param ingredientID the new ingredients which the Dinner should have
     * @param rating       the rating of the of Dinner. If nothing is passed here - the rating will be 0. You should pass the old rating if you wish to keep the rating.
     * @param date         the new date of the Dinner.
     * @return returns true if the dinner was successfully updated.
     * @see Dinner
     */
    public boolean updateDinner(int id, @Nullable String name, @Nullable String description, @Nullable String category, @Nullable List<Integer> ingredientID, int rating, @Nullable Date date) {
        ContentValues cv = new ContentValues();
        if (id >= -1) {
            if (name != null) {
                cv.put(Dinner.KEY_NAME, name);
            }
            if (description != null) {
                cv.put(Dinner.KEY_DESCRIPTION, description);
            }
            if (category != null) {
                cv.put(Dinner.KEY_CUISINE, category);
            }
            if (ingredientID != null) {
                cv.put(Dinner.KEY_INGREDIENTS_ID, ingredientID.toString());
            }
            if (date != null) {
                cv.put(Dinner.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(date));
            }
            cv.put(Dinner.KEY_RATING, rating);
            if (dbHelper.getWritableDatabase().update(Dinner.TABLE_NAME, cv, Dinner.KEY_ID + " = ?", new String[]{id + ""}) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Delete a Dinner from the Database with the given ID.
     *
     * @param dinnerID the ID of the dinner to be deleted from the database.
     * @return returns true if dinner was successfully deleted, otherwise false.
     * @see Dinner
     */
    public boolean deleteDinner(int dinnerID) {
        if (dbHelper.getWritableDatabase().delete(Dinner.TABLE_NAME, Dinner.KEY_ID + " = ?",
                new String[]{Integer.toString(dinnerID)}) >= 1) {
            dbHelper.close();
            return true;
        }
        dbHelper.close();
        return false;

    }

    public Dinner getDinner(int dinnerID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d("SQL", "Dinner Repository: getDinner() Accessing database");
        Cursor results = db.rawQuery("SELECT * FROM " + Dinner.TABLE_NAME + " WHERE " + Dinner.KEY_ID + " = ?", new String[]{Integer.toString(dinnerID)});
        Log.d("DinnerRepository", "getDinner: Doing a query");
        if (results.moveToFirst()) {
            Dinner d = new Dinner();
            do {
                d.setName(results.getString(results.getColumnIndex(Dinner.KEY_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.KEY_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.KEY_CUISINE)));
                d.setRating(results.getInt(results.getColumnIndex(Dinner.KEY_RATING)));
                String[] raw = results.getString(results.getColumnIndex(Dinner.KEY_INGREDIENTS_ID)).replaceAll("[^1-9,]", "").split(",");
                for (String s : raw) {
                    try {
                        d.addIngredients(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        Log.e("Error", "Couldn't parseIn at Dinner.getDinner " + e.getMessage());
                    }
                }
            }
            while (results.moveToNext());
            results.close();
            dbHelper.close();
            return d;
        } else {
            results.close();
            dbHelper.close();
            return null;
        }


    }

    /**
     * Returns a list of meals - if no meals are found returns null.
     *
     * @return a List with type Dinner
     * @see List
     * @see Dinner
     */
    public List<Dinner> getDinnerList() {
        return dinnerList;
    }

    /**
     * Updates the dinner list in the DinnerRepository. Do this if you've recently added new dinners to the database and want a refresh.
     */
    private void updateDinnerList() {
        Log.d("DinnerRepository", "updateDinnerList");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + Dinner.TABLE_NAME, null);
        if (results.moveToFirst()) {
            dinnerList = new ArrayList<>();
            do {
                Dinner d = new Dinner();
                try {
                    d.setDinnerID(Integer.parseInt(results.getString(results.getColumnIndex(Dinner.KEY_ID))));
                } catch (NumberFormatException e) {
                    Log.e("DinnerRepository", "Couldn't format number at DinnerRepository.getDinnerList: " + e.toString());
                    break; //ID must be set otherwise break from loop.
                }
                d.setName(results.getString(results.getColumnIndex(Dinner.KEY_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.KEY_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.KEY_CUISINE)));
                d.setRating(results.getInt(results.getColumnIndex(Dinner.KEY_RATING)));
                try {
                    d.setDate(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK"))
                            .parse(results.getString(results.getColumnIndex(Dinner.KEY_DATE))));
                    //System.out.println("Parsed the date object form database" + format.parse(t).toString());
                } catch (ParseException e) {
                    Log.e("ParseException", "Couldn't parse String to Date-object in DinnerRepository.getDinnerList()");
                }

                String[] raw = results.getString(results.getColumnIndex(Dinner.KEY_INGREDIENTS_ID)).replaceAll("[^1-9,]", "").split(",");
                for (String s : raw) {
                    try {
                        d.addIngredients(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        Log.e("DinnerRepository", "Couldn't parseInt at Dinner.getDinnerList " + e.toString());
                    }
                }
                dinnerList.add(d);
            }
            while (results.moveToNext());
        }
        results.close();
        db.close();
        dbHelper.close();
    }
}
