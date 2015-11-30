package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * GameEngine to keep track of XP, Levels and Badges.
 */
 class GameEngine {

    private static long currentLevel;
    private static long currentExperience;
    private Context context;
    private SharedPreferences sp;
    private static final int BASE_EXPERIENCE = 100;
    private static final String TAG = GameEngine.class.getSimpleName();

    /**
     * Constructor for GameEngine. Set Level to 1 and XP to 0 on initialization if no SharedPreferences are found.
     * @param context Application Context
     * @see Context
     */
    public GameEngine(Context context) {
        this.context = context;
        //Get saved states from earlier upon initialization.
        sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        currentLevel = sp.getLong("currentLevel", 1);
        currentExperience = sp.getLong("currentExperience", 0);
    }

    /**
     * Get current level of the app.
     * @return current level as long
     */
    public long getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Add experience to game engine. Level is automatically calculated from experience. Use getCurrentLevel after to receive the latest level.
     * @param experience amount of experience to add.
     */
    public void addExperience(long experience) {
        //Add new XP to our old XP.
        this.currentExperience = currentExperience + experience;
        calculateLevel();

        //Save our experience and level to SharePreferences.
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("currentLevel", currentLevel);
        editor.putLong("currentExperience", currentExperience);
        editor.apply();
    }

    /**
     * Returns the amount of experience in the level.
     * @return the current amount of experience
     */
    public double getCurrentExperience() {
        return currentExperience;
    }

    /*
     * Calculates the level from experience. If experience exceeds the threshold for the next level,
     * then level will go up by one. Function is recursive. All experience and level changes are automatically saved to SharePreferences.
     */
    private void calculateLevel() {
        // If currentExperience is greater than what is required for nextlevel - then level up.
        if(currentExperience >= experienceForNextLevel()) {
            //Subtract the experience that is too much and save the remainder.
            currentExperience = currentExperience - (long)experienceForNextLevel();
            //Level up!
            currentLevel++;
            //Give user a notice
            Toast.makeText(context, "Level Up! You reached level " + currentLevel, Toast.LENGTH_SHORT).show();
            //Run it again - in case we have too much XP compared to our level.
            calculateLevel();
        }
    }

    /**
     * Get the amount of experience necessary for the next level up.
     * @return experience amount as double
     */
    public double experienceForNextLevel() {
        //We use a Quadratic exponential function to calculate our XP requirements; f(x)=ax^2-ax where a is the base-experience modifier, x is our current level and f(x) is the XP result.
        return (BASE_EXPERIENCE * Math.pow((currentLevel + 1), 2)) - (BASE_EXPERIENCE * (currentLevel +1));
    }

    /**
     * Interface for listeners to notify on experience change.
     */
    public interface ExperienceChangeable {
        void updateExperience(double currentExperience, double experienceForNextLevel, long currentLevel);
    }
}
