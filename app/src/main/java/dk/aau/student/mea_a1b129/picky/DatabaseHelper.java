package dk.aau.student.mea_a1b129.picky;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
Database helper to create and initiate connections to app database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Dishit.db";
    public static final String TAG_TABLE_NAME = "IngredientsTable";
    public static final String TAG_ID = "_id";
    public static final String TAG_NAME = "name";
    public static final String TAG_CATEGORY = "category";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TAG_TABLE_NAME + " (" +
                TAG_ID + " INTEGER PRIMARY KEY, " +
                TAG_NAME + " TEXT, " +
                TAG_CATEGORY + " TEXT)"
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

    /**
     * Insert a new ingredient into the Ingredients Table in the Database.
     * @param name The name of the Ingredients tag.
     * @param category the category of the ingredients, e.g. meat, vegetable, fruit.
     */
    public boolean insertIngredientsTag(String name, String category) {
        ContentValues cv = new ContentValues();
        cv.put(TAG_NAME, name);
        cv.put(TAG_CATEGORY, category);
        this.getWritableDatabase().insert(TAG_TABLE_NAME, null, cv);
        return true;
    }

    /**
     * Update a new ingredient into the Ingredients Table in the Database.
     * @param name The name of the Ingredients tag.
     * @param category the category of the ingredients, e.g. meat, vegetable, fruit.
     */
    public boolean updateIngredientsTag(int id, String name, String category) {
        ContentValues cv = new ContentValues();
        cv.put(TAG_NAME, name);
        cv.put(TAG_CATEGORY, category);
        this.getWritableDatabase().update(TAG_TABLE_NAME, cv, TAG_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    /**
     * Returns a specific ingredient at id.
     * @param id the id of the ingredient to return.
     * @return returns ingredient at the specified id in the database as Cursor-object.
     * @see Cursor
     */
    public Cursor getIngredient(int id) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM " + TAG_TABLE_NAME + " WHERE " +
                TAG_NAME + " =?", new String[]{Integer.toString(id)});
    }

    /*
    Returns all of the ingredients tags in the table as a Cursor.
     */
    public Cursor getAllIngredients() {
        return this.getReadableDatabase().rawQuery(
                "SELECT * FROM " + TAG_TABLE_NAME, null);
    }

    /*
    Delete an ingredient with given ID.
     */
    public Integer deleteIngredient(int id) {
        return this.getWritableDatabase().delete(TAG_TABLE_NAME, TAG_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }
}
