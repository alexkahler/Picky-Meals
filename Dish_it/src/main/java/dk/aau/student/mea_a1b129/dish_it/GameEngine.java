package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * GameEngine to keep track of XP, Levels and Badges.
 * TODO: Add achievements achievements to GameEngine
 */
 class GameEngine {

    private long currentLevel;
    private long currentExperience;
    private HashMap<ProgressionType, Achievement> achievements = new HashMap<>();
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
        //Save our context for later use.
        this.context = context;
        //Get saved states from earlier upon initialization.
        sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        //Get the current level and experience from our Shared Preferences.
        currentLevel = sp.getLong("currentLevel", 1); //We return level 1, if the level was not found
        currentExperience = sp.getLong("currentExperience", 0); //We return 0, if the amount of experience was not found.

        //Now we go though each type of progression possibility and save our Achievements status to a HashMap
        for(ProgressionType pt : ProgressionType.values()) {
            achievements.put(pt, new Achievement(sp.getInt(pt.name(), 0), pt));
        }
    }

    /**
     * Get current level of the user.
     * @return current level as a long
     */
    public long getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Add experience to the GameEngine. Level is automatically calculated from experience.
     * Remember to use getCurrentLevel() to receive the latest level after adding experience.
     * @param experience amount of experience to add.
     * @return the current experience after added experience.
     */
    public long addExperience(long experience) {
        //Add new XP to our old XP.
        this.currentExperience = currentExperience + experience;
        calculateLevel();

        //Save our experience and level to SharePreferences.
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("currentLevel", currentLevel);
        editor.putLong("currentExperience", currentExperience);
        editor.apply();
        return currentExperience;
    }

    /**
     * Returns the amount of experience in the level.
     * @return the current amount of experience
     */
    public double getCurrentExperience() {
        return currentExperience;
    }

    /**
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
            Toast.makeText(context, "Level Up! You reached level " + currentLevel, Toast.LENGTH_LONG).show();
            //Run it again - in case we have too much XP compared to our level.
            calculateLevel();
        }
    }

    /**
     * Get the amount of experience necessary for the next level up.
     * @return experience amount as double
     */
    public double experienceForNextLevel() {
        //We use a Quadratic exponential function to calculate our XP requirements; f(x)=ax^2-ax
        // where a is the base-experience modifier, x is our current level and f(x) is the XP result.
        return (BASE_EXPERIENCE * Math.pow((currentLevel + 1), 2)) - (BASE_EXPERIENCE * (currentLevel +1));
    }

    /**
     * Track progression of activity in app.
     * @param progressionType the enum value of the progression type
     * @param progressionUpdate the value to add to the progression. Should always be 1.
     * @see dk.aau.student.mea_a1b129.dish_it.GameEngine.ProgressionType
     * @see dk.aau.student.mea_a1b129.dish_it.GameEngine.BadgeType
     */
    public void trackProgression(@NonNull ProgressionType progressionType, int progressionUpdate) {
        //Get the editor, so we can edit our save data.
        SharedPreferences.Editor editor = sp.edit();
        //Update our progression in the HasMap.
        achievements.get(progressionType).updateProgression(progressionUpdate);
        //Save the progression.
        editor.putInt(progressionType.name(), achievements.get(progressionType).getCurrentProgression());
        editor.apply();
    }

    /**
     * Get a List of the Achievements.
     * @return Achievements as a List
     */
    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements.values());
    }

    /**
     * Inner class for Achievements.
     */
    class Achievement {

        private int currentProgression;
        private ProgressionType progressionType;
        private HashMap<BadgeType, Boolean> badges = new HashMap<>();

        /**
         * Achievements constructor.
         * @param currentProgression the current progression in the type of achievement.
         * @param type the type of achievement.
         * @see dk.aau.student.mea_a1b129.dish_it.GameEngine.ProgressionType
         */
        public Achievement(int currentProgression, ProgressionType type) {
            //Save our parameter values to our instance variables.
            this.currentProgression = currentProgression;
            this.progressionType = type;
            //Now we set the type of badges this Achievement is related to.
            switch(progressionType) {
                case DINNERS_ADDED:
                    badges.put(BadgeType.First_Dinner, checkAchieved(BadgeType.First_Dinner, currentProgression));
                    badges.put(BadgeType.Fifth_Dinner, checkAchieved(BadgeType.Fifth_Dinner, currentProgression));
                    badges.put(BadgeType.Tenth_Dinner, checkAchieved(BadgeType.Tenth_Dinner, currentProgression));
                    break;
                case DINNERS_DELETED:
                    badges.put(BadgeType.Mass_Deletion, checkAchieved(BadgeType.Mass_Deletion, currentProgression));
                    break;
                case DINNERS_SUGGESTED:
                    badges.put(BadgeType.First_Suggestion, checkAchieved(BadgeType.First_Suggestion, currentProgression));
                    badges.put(BadgeType.Fifth_Suggestion, checkAchieved(BadgeType.Fifth_Suggestion, currentProgression));
                    badges.put(BadgeType.Tenth_Suggestion, checkAchieved(BadgeType.Tenth_Suggestion, currentProgression));
                    break;
                case APP_OPENED:
                    badges.put(BadgeType.New_User, checkAchieved(BadgeType.New_User, currentProgression));
            }
        }

        /**
         * Update the progression in the Achievement
         * @param p the amount to be added to the progression.
         */
        protected void updateProgression(int p) {
            currentProgression += p;
            //Iterate through the badge types in our achievement..
            for(BadgeType bt : badges.keySet()) {
                //If our current progression matches the goal of a badge.
                if(currentProgression == bt.getBadgeGoal()) {
                    //Then make a toast and notify the user.
                    Toast.makeText(context, context.getString(R.string.game_engine_new_badge) + " " + bt.toString() + "!", Toast.LENGTH_LONG).show();
                    badges.put(bt, checkAchieved(bt, currentProgression));

                }
            }
        }

        /**
         * Get the current progression.
         * @return the current progression as an int
         */
        protected int getCurrentProgression() {
            return currentProgression;
        }

        /**
         * Get the types of badges this Achievement is related to
         * @return the Badges as a HashMap, where KeySet is the BadgeType and Value is the boolean status of whether it has been achieved.
         * @see dk.aau.student.mea_a1b129.dish_it.GameEngine.BadgeType
         */
        protected HashMap<BadgeType, Boolean> getBadges() {
            return badges;
        }

        /**
         * Check whether or not a specific Badge has been achieved with the current progression.
         * @param bt the BadgeType to check
         * @param currentProgression the current progression of the achievement.
         * @return true if the badge has been achieved.
         * @see dk.aau.student.mea_a1b129.dish_it.GameEngine.BadgeType
         */
        private Boolean checkAchieved(@NonNull BadgeType bt, int currentProgression) {
            if (currentProgression >= bt.getBadgeGoal()) {
                return true;
            }
            return false;
        }
    }

    /**
     * Enum types of progression possibilities.
     */
    public enum ProgressionType {
        DINNERS_ADDED,
        DINNERS_SUGGESTED,
        DINNERS_DELETED,
        APP_OPENED
    }

    /**
     * The types of badges that can be earned as an Enum.
     */
    public enum BadgeType {

        First_Dinner(R.string.game_engine_first_dinner, R.drawable.first_dinner_badge, 1),
        Fifth_Dinner(R.string.game_engine_fifth_dinner, R.drawable.fifth_dinner_badge, 5),
        Tenth_Dinner(R.string.game_engine_tenth_dinner, R.drawable.tenth_dinner_badge, 10),
        First_Suggestion(R.string.game_engine_first_suggestion, R.drawable.first_suggestion_badge, 1),
        Fifth_Suggestion(R.string.game_engine_fifth_suggestion, R.drawable.fifth_suggestions_badge, 5),
        Tenth_Suggestion(R.string.game_engine_tenth_suggestion, R.drawable.tenth_suggestions_badge, 10),
        New_User(R.string.game_engine_new_user, R.drawable.new_user_badge, 1),
        Mass_Deletion(R.string.game_engine_mass_deletionist, R.drawable.deletion_badge, 10);

        private final int resourceID;
        private final int badgeImageID;
        private final int badgeGoal;

        /**
         * Constructor for the Enum class.
         * @param stringID the int value of the String resource.
         * @param badgeID the int value of the drawable image resource.
         * @param badgeGoal the int value of the goal, which signifies completion.
         */
        BadgeType(int stringID, int badgeID, int badgeGoal) {
            resourceID = stringID;
            this.badgeImageID = badgeID;
            this.badgeGoal = badgeGoal;
        }

        /**
         * Get the drawable image resource ID.
         * @return the int value of the resource ID.
         */
        public int getBadgeImageID() {
            return badgeImageID;
        }

        /**
         * Get the goal for the enum Badge.
         * @return the int value of the goal.
         */
        public int getBadgeGoal() {
            return badgeGoal;
        }

        /**
         * Get the String value of the BadgeType.
         * @return the String value from the resource ID attached to the Enum value.
         */
        @Override
        public String toString() {
            return HomeActivity.getContext().getString(resourceID);
        }
    }


    /**
     * Interface for listeners to notify experience change.
     */
    public interface ExperienceChangeable {
        void updateExperience(double currentExperience, double experienceForNextLevel, long currentLevel);
    }
}
