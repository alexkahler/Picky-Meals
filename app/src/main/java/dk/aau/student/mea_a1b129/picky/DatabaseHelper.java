package dk.aau.student.mea_a1b129.picky;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
Database helper to create and initiate connections to app database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Dishit.db";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Ingredient.TABLE_NAME + " (" +
                        Ingredient.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Ingredient.KEY_NAME + " TEXT, " +
                        Ingredient.KEY_CATEGORY + " TEXT, " +
                        Ingredient.KEY_DESCRIPTION + " TEXT)"
        );
        db.execSQL(
                "CREATE TABLE " + Dinner.TABLE_NAME + " (" +
                        Dinner.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        Dinner.KEY_NAME + " TEXT, " +
                        Dinner.KEY_DESCRIPTION + " TEXT, " +
                        Dinner.KEY_CUISINE + " TEXT, " +
                        Dinner.KEY_RATING + " INTEGER, " +
                        Dinner.KEY_INGREDIENTS_ID + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getDATABASE_VERSION() {
        return DATABASE_VERSION;
    }
}
