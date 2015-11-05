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

    /*
    Insert a new meal into the database.
    */
    public boolean insertDinner(String name, String cuisine, String ingredientsID) {
        ContentValues cv = new ContentValues();
        cv.put(Dinner.DB_NAME, name);
        cv.put(Dinner.DB_CUISINE, cuisine);
        cv.put(Dinner.DB_INGREDIENTS_ID, ingredientsID);
        dbHelper.getWritableDatabase().insert(Dinner.DB_TABLE_NAME, null, cv);
        dbHelper.close();
        return true;
    }

    public void deleteDinner(int dinnerID) {
        dbHelper.getWritableDatabase().delete(Dinner.DB_TABLE_NAME, Dinner.DB_ID + "=?",
                new String[]{Integer.toString(dinnerID)});
        dbHelper.close();
    }

    public Dinner getDinner(int dinnerID) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor results = db.query(
                Dinner.DB_TABLE_NAME,
                new String[]{Dinner.DB_NAME, Dinner.DB_DESCRIPTION, Dinner.DB_CUISINE,
                        Dinner.DB_INGREDIENTS_ID},Dinner.DB_ID, new String[]{Integer.toString(dinnerID)},
                null, null, null, null);
        if(results.moveToFirst()) {
            Dinner d = new Dinner();
            do {
                d.setName(results.getString(results.getColumnIndex(Dinner.DB_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.DB_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.DB_CUISINE)));
                String[] raw = results.getString(results.getColumnIndex(Dinner.DB_INGREDIENTS_ID)).replaceAll("\\]|\\[", "").split(",");
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

    /*
    Returns a list of meals - if no meals are found returns null.
     */
    public List<Dinner> getAllDinners() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor results = db.query(Dinner.DB_TABLE_NAME, new String[]{Dinner.DB_ID, Dinner.DB_NAME, Dinner.DB_DESCRIPTION, Dinner.DB_CUISINE, Dinner.DB_INGREDIENTS_ID}, null, null, null, null, null);
        if(results.moveToFirst()) {
            List<Dinner> dinnerList = new ArrayList<>();
            do {
                Dinner d = new Dinner();
                try { d.setDinnerID(Integer.parseInt(results.getString(results.getColumnIndex(Dinner.DB_ID)))); } catch(Exception e) {e.printStackTrace();}
                d.setName(results.getString(results.getColumnIndex(Dinner.DB_NAME)));
                d.setDescription(results.getString(results.getColumnIndex(Dinner.DB_DESCRIPTION)));
                d.setCuisine(results.getString(results.getColumnIndex(Dinner.DB_CUISINE)));
                String[] raw = results.getString(results.getColumnIndex(Dinner.DB_INGREDIENTS_ID)).replaceAll("\\]|\\[", "").split(",");
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
