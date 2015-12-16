package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * Database helper to create and initiate connections to app database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Dishit.db";
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static int DATABASE_VERSION = 1;
    private static DatabaseHelper sInstance;
    private final Context context;

    /**
     * Constructor
     * @param context The current application Context
     * @see Context
     */
    private DatabaseHelper(Context context) {
        //Mandatory call to super class
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Save our context for later use
        this.context = context;
        onUpgrade(getWritableDatabase(), getDatabaseVersion(), HomeActivity.getCurrentVersion());
    }

    /**
     * Synchronized method, which makes sure, that only 1 instance of DatabaseHelper is made.
     * @param context
     * @return the current instance of the DatabaseHelper
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeSQLScript(db, "ingredients.sql");
        executeSQLScript(db, "meals.sql");
        Log.v(TAG, "onCreate executed SQL");
    }

    /**
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Check if the version is newer.
        if (newVersion > oldVersion) {
            //Now we switch on the version number, and do an incremental upgrade.
            switch (oldVersion) {
                case 1: {
                    //executeSQLScript(db, "upgrade_v2.sql");
                    //DATABASE_VERSION = 2;
                }
                case 2: {
                    //Run next upgrade script;
                    break;
                }
            }
        }
    }

    /**
     * Helper method to execute .sql scripts from the assets.
     * @param database the SQL database, to execute script in.
     * @param script the script filename to execute.
     */
    private void executeSQLScript(SQLiteDatabase database, String script) {
        //Make a output, from where we'll read the SQL commands.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //We make a simple array to save each SQL script line in.
        byte buffer[] = new byte[1024];
        //Used as a counter for the length of the entire script.
        int length;
        InputStream inputStream;
        try {
            //We open the script into our input stream.
            inputStream = context.getAssets().open(script);
            //While the length is not at the end (-1)
            while ((length = inputStream.read(buffer)) != -1) {
                //Write our input to our output stream.
                outputStream.write(buffer, 0, length);
            }
            //Remember to close our streams, to avoid memory leaks.
            outputStream.close();
            inputStream.close();
            //Now we save the output stream into an array and split at each ";", which determines the end of a SQL command.
            String[] createScript = outputStream.toString().split(";");
            //Iterate through the createScript array and do something with String s
            for (String s : createScript) {
                //We make sure to trim our String from any codepoints (/ = >)
                String sqlStatement = s.trim();
                //If the length is longer than 0 characters
                if (sqlStatement.length() > 0) {
                    //Run the extended method "execSQL" with our trimmed sql statement and appending ";".
                    database.execSQL(sqlStatement + ";");
                }
            }
            //In case any errors were thrown.
        } catch (IOException e) {
            Log.e(TAG, "Unknown IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            Log.e(TAG, "Wrong SQL file mate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //This is to get our database version.
    private int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}
