package dk.aau.student.mea_a1b129.picky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
     * TODO: Add @param and @return to documentation.
     */
    public boolean insertDinner(String name, String description, String cuisine, String ingredientsID, int rating) {
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
        dbHelper.close();
        return true;
    }


    public void deleteDinner(int dinnerID) {
        dbHelper.getWritableDatabase().delete(Dinner.TABLE_NAME, Dinner.KEY_ID + " = ?",
                new String[]{Integer.toString(dinnerID)});
        dbHelper.close();
    }

    public Dinner getDinner(int dinnerID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + Dinner.TABLE_NAME + " WHERE " + Dinner.KEY_ID + " = ?", new String[]{Integer.toString(dinnerID)});
        if(results.moveToFirst()) {
            System.out.println("In getDinner if-statement");
            Dinner d = new Dinner();
            do {
                System.out.println("Trying to set Dinner-class");
                d.setName(results.getString(results.getColumnIndex(Dinner.KEY_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.KEY_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.KEY_CUISINE)));
                d.setRating(results.getInt(results.getColumnIndex(Dinner.KEY_RATING)));
                String[] raw = results.getString(results.getColumnIndex(Dinner.KEY_INGREDIENTS_ID)).replaceAll("\\]|\\[|\\s", "").split(",");
                for(int i = 0; i < raw.length; i++) {

                    d.addIngredients(Integer.parseInt(raw[i]));
                }
            }
            while (results.moveToNext());
            db.close();
            return d;
        }
        else {
            db.close();
            return null;
        }


    }

    /**
     *Returns a list of meals - if no meals are found returns null.
     */
    public List<Dinner> getAllDinners() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor results = db.query(Dinner.TABLE_NAME, new String[]{Dinner.KEY_ID, Dinner.KEY_NAME, Dinner.KEY_DESCRIPTION, Dinner.KEY_CUISINE, Dinner.KEY_INGREDIENTS_ID}, null, null, null, null, null);
        if(results.moveToFirst()) {
            List<Dinner> dinnerList = new ArrayList<>();
            do {
                Dinner d = new Dinner();
                //TODO: Change use constructor instead of individual methods.
                try { d.setDinnerID(Integer.parseInt(results.getString(results.getColumnIndex(Dinner.KEY_ID)))); } catch(Exception e) {e.printStackTrace();}
                d.setName(results.getString(results.getColumnIndex(Dinner.KEY_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.KEY_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.KEY_CUISINE)));
                String[] raw = results.getString(results.getColumnIndex(Dinner.KEY_INGREDIENTS_ID)).replaceAll("\\]|\\[", "").split(",");
                for (int i = 0; i < raw.length; i++) {
                    d.addIngredients(Integer.parseInt(raw[i]));
                }
                dinnerList.add(d);
            }
            while (results.moveToNext());
            db.close();
            return dinnerList;
        }
        else {
            db.close();
            return null;
        }
    }
}
