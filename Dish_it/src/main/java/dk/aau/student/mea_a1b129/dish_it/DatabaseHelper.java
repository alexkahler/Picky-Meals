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
 * Database helper to create and initiate connections to app database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Dishit.db";
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static int DATABASE_VERSION = 1;
    private static DatabaseHelper sInstance;
    private final Context context;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        onUpgrade(getWritableDatabase(), getDatabaseVersion(), HomeActivity.getCurrentVersion());
    }

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            switch (oldVersion) {
                case 1: {
                    //executeSQLScript(db, "upgrade_v2.sql");
                    //DATABASE_VERSION = 2;
                }
            }
        }

    }

    private void executeSQLScript(SQLiteDatabase database, String script) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buffer[] = new byte[1024];
        int length;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;

        try {
            inputStream = assetManager.open(script);
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split(";");
            for (String s : createScript) {
                String sqlStatement = s.trim();
                if (sqlStatement.length() > 0) {
                    database.execSQL(sqlStatement + ";");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Unknown IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            Log.e(TAG, "Wrong SQL file mate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}
