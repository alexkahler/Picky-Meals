package dk.aau.student.mea_a1b129.dish_it;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
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
 * Dinner repository to lookup Dinners in the Database.
 */
class DinnerRepository {

    private DatabaseHelper dbHelper;
    private List<Dinner> dinnerList;
    private static final String TAG = DinnerRepository.class.getSimpleName();

    /**
     * Constructor for DinnerRepository. When initialized the class updates the latest list of dinners from the database.
     *
     * @param context Context of the application
     * @see Context
     */
    public DinnerRepository(Context context) {
        //Get the current instance of our Database helper.
        dbHelper = DatabaseHelper.getInstance(context);
        //Make a new Dinner list, where we'll save our Dinners.
        dinnerList = new ArrayList<>();
        updateDinnerList();
    }

    /**
     * Insert a new Dinner into the database.
     *
     * @param name          Name of the meal - must not be null
     * @param description   description of the meal
     * @param cuisine       which kind cuisine or category the meal belongs to
     * @param ingredientsID the ID of the ingredients - if null a new Ingredient will be made in the database
     * @param rating        the meals rating
     * @return returns true if the Dinner was successful inserted into the Database
     * @see Dinner
     */
    public boolean insertDinner(@NonNull String name, @Nullable String description, @Nullable String cuisine, @NonNull List<Integer> ingredientsID, float rating, @NonNull Date date, double price) {
        //ContentValues will be used to input our Dinner into the database.
        ContentValues cv = new ContentValues();
        //If the name is null, then stop and return null.
        if (name == null) {
            return false;
        }
        cv.put(Dinner.KEY_NAME, name);
        cv.put(Dinner.KEY_DESCRIPTION, description);
        cv.put(Dinner.KEY_CUISINE, cuisine);
        //Stop if the list of Ingredients are null.
        if (ingredientsID == null) {
            return false;
        }
        cv.put(Dinner.KEY_INGREDIENTS_ID, ingredientsID.toString());
        cv.put(Dinner.KEY_RATING, rating);
        //Stop if the date is null.
        if (date == null) {
            return false;
        }
        cv.put(Dinner.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(date));
        cv.put(Dinner.KEY_PRICE, price);
        Log.d("SQL", "DinnerRepository, insertDinner() Accessing database");
        //Get the database with write-access and insert the ContentValues into the SQLite database, in the table.
        if (dbHelper.getWritableDatabase().insert(Dinner.TABLE_NAME, null, cv) == -1) {
            //If an error was returned (-1), then return false.
            dbHelper.close();
            return false;
        }
        //Close our connection to the database helper.
        dbHelper.close();
        //Return true, so our caller knows that the insert was successful.
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
    public boolean updateDinner(@NonNull int id, @Nullable String name, @Nullable String description, @Nullable String category, @Nullable List<Integer> ingredientID, float rating, @Nullable Date date, @Nullable double price) {
        //Make a ContentValues variable, in which will input our information, which we'll later use to insert into the database.
        ContentValues cv = new ContentValues();
        //If the ID is greater than 1 (the database doesn't allow ID's less than one)
        if (id >= -1) {
            //Go through a test whether any parameter is null, if it is, then don't add the value to our ContentValues.
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
            //We can't test primitive types for null.
            cv.put(Dinner.KEY_PRICE, price);
            cv.put(Dinner.KEY_RATING, rating);
            if (dbHelper.getWritableDatabase().update(Dinner.TABLE_NAME, cv, Dinner.KEY_ID + " = ?", new String[]{id + ""}) >= 1) {
                //If we were successful in updating a row in the database return true
                return true;
            }
        }
        //Return false, if we weren't successful in updating a row, or if the ID was less than 0.
        return false;
    }

    /**
     * Delete a Dinner from the Database with the given ID.
     *
     * @param dinnerID the ID of the dinner to be deleted from the database.
     * @return returns true if dinner was successfully deleted, otherwise false.
     * @see Dinner
     */
    public boolean deleteDinner(@NonNull int dinnerID) {
        //While we delete the dinner from the database, we test how many rows were affected by the deletion.
        if (dbHelper.getWritableDatabase().delete(Dinner.TABLE_NAME, Dinner.KEY_ID + " = ?",
                new String[]{Integer.toString(dinnerID)}) >= 1) {
            //if more than 0 rows were deleted, we return true and close our database.
            dbHelper.close();
            return true;
        }
        // we return false, if no rows were affected.
        dbHelper.close();
        return false;

    }

    /**
     * Get Dinner with the specified ID at the database.
     * @param dinnerID the ID of the Dinner to be returned.
     * @return the Dinner at the ID. If no Dinner is found at the specified ID, then it will return null.
     * @see Dinner
     */
    public Dinner getDinner(@NonNull int dinnerID) {
        //Open the database with read access.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d("SQL", "Dinner Repository: getDinner() Accessing database");
        //Save the results from the SQL query in a Cursor object.
        Cursor results = db.rawQuery("SELECT * FROM " + Dinner.TABLE_NAME + " WHERE " + Dinner.KEY_ID + " = ?", new String[]{Integer.toString(dinnerID)});
        Log.d("DinnerRepository", "getDinner: Doing a query");
        /*We do 2 things here:
            1. We move to the first result in the Cursor
            2. When we move, we also test if the Cursor is empty. */
        if (results.moveToFirst()) {
            //Make a Dinner variable in which we'll save the information and return later.
            Dinner d = new Dinner();
            //Do-loop to iterate through the Cursor and save the results.
            do {
                //Set everything in the Dinner object. TODO: This should probably be done through the Dinner constructor.
                d.setName(results.getString(results.getColumnIndex(Dinner.KEY_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.KEY_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.KEY_CUISINE)));
                d.setRating(results.getFloat(results.getColumnIndex(Dinner.KEY_RATING)));
                d.setPrice(results.getDouble(results.getColumnIndex(Dinner.KEY_PRICE)));
                //Now we get the Ingredients as a String. We split this string at all commas (",") and remove all square brackets. This we save in a String Array.
                String[] raw = results.getString(results.getColumnIndex(Dinner.KEY_INGREDIENTS_ID)).replaceAll("[^1-9,]", "").split(",");
                //For each String in the array
                for (String s : raw) {
                    try {
                        //We try to parse the int value from the String and add the Ingredient to our Dinner.
                        d.addIngredients(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        //If something should fail during parsing - we catch the error here, so it doesn't crash.
                        Log.e(TAG, "Couldn't parseInt at Dinner.getDinner " + e.getMessage());
                    }
                }
            }
            //We do this loop, while we can still move to the next result in the Cursor.
            while (results.moveToNext());
            //Once we're out of the loop, we close our Cursor and our database.
            results.close();
            dbHelper.close();
            //Return our Dinner
            return d;
        } else {
            //If there are no results, we close everything and return null.
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
        //Open our database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //Do a query where we select all columns and rows from Meals table in the database.
        Cursor results = db.rawQuery("SELECT * FROM " + Dinner.TABLE_NAME, null);
        //If there are any results, we move to the first result.
        if (results.moveToFirst()) {
            //We Initialize our list, which we'll return later
            dinnerList = new ArrayList<>();
            //Now we iterate through our results with a do-loop.
            do {
                //Make a temporary variable, which we'll save to the list later.
                Dinner d = new Dinner();
                try {
                    //We try to set our Dinner ID by reading the String and converting it to an int value.
                    d.setDinnerID(Integer.parseInt(results.getString(results.getColumnIndex(Dinner.KEY_ID))));
                } catch (NumberFormatException e) {
                    Log.e("DinnerRepository", "Couldn't format number at DinnerRepository.getDinnerList: " + e.toString());
                    break; //ID must be set otherwise break from loop.
                }
                //Set everything from each column into the Dinner object.
                d.setName(results.getString(results.getColumnIndex(Dinner.KEY_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.KEY_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.KEY_CUISINE)));
                d.setRating(results.getFloat(results.getColumnIndex(Dinner.KEY_RATING)));
                d.setPrice(results.getDouble(results.getColumnIndex(Dinner.KEY_PRICE)));
                try {
                    //Try to parse the date from the results and save the date as a Date object, with the specified formatting.
                    d.setDate(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK"))
                            .parse(results.getString(results.getColumnIndex(Dinner.KEY_DATE))));
                } catch (ParseException e) {
                    Log.e("ParseException", "Couldn't parse String to Date-object in DinnerRepository.getDinnerList()");
                }
                //Get all of the ingredients, remove square brackets then split the String at the "," - save this split string in an array.
                String[] raw = results.getString(results.getColumnIndex(Dinner.KEY_INGREDIENTS_ID)).replaceAll("[^1-9,]", "").split(",");
                //For each String in the array...
                for (String s : raw) {
                    //... We try to parse the ID of the ingredient and save it to our Dinner.
                    try {
                        d.addIngredients(Integer.parseInt(s)); //TODO: Somethings is throwing NumberFormatException here...
                    } catch (NumberFormatException e) {
                        Log.e("DinnerRepository", "Couldn't parseInt at Dinner.getDinnerList " + e.toString());
                        e.printStackTrace();
                    }
                }
                //Add the Dinner to our list
                dinnerList.add(d);
            }
            //We do this loop, which we can still move to the next result in our Cursor.
            while (results.moveToNext());
        }
        //Close our results, database connection and our database helper to free up resources.
        results.close();
        db.close();
        dbHelper.close();
    }
}
