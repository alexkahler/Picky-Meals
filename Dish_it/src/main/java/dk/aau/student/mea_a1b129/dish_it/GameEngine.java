package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
        this.context = context;
        //Get saved states from earlier upon initialization.
        sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        currentLevel = sp.getLong("currentLevel", 1);
        currentExperience = sp.getLong("currentExperience", 0);

        for(ProgressionType pt : ProgressionType.values()) {
            achievements.put(pt, new Achievement(sp.getInt(pt.name(), 0), pt));
        }
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
    public void trackProgression(ProgressionType progressionType, int progressionUpdate) {
        SharedPreferences.Editor editor = sp.edit();
        achievements.get(progressionType).updateProgression(progressionUpdate);
        editor.putInt(progressionType.name(), achievements.get(progressionType).getCurrentProgression());
        editor.apply();
    }

    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements.values());
    }

    /**
     * Inner class for Achievements.
     */
    public class Achievement {

        private int currentProgression;
        private ProgressionType progressionType;
        private HashMap<BadgeType, Boolean> badges = new HashMap<>();

        public Achievement(int currentProgression, ProgressionType type) {
            this.currentProgression = currentProgression;
            this.progressionType = type;
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

        protected void updateProgression(int p) {
            currentProgression += p;
            for(BadgeType bt : badges.keySet()) {
                if(currentProgression == bt.getBadgeGoal()) {
                    Toast.makeText(context, context.getString(R.string.game_engine_new_badge) + " " + bt.toString() + "!", Toast.LENGTH_LONG).show();
                }
            }
        }

        protected int getCurrentProgression() {
            return currentProgression;
        }

        protected ProgressionType getProgressionType() {
            return progressionType;
        }

        protected HashMap<BadgeType, Boolean> getBadges() {
            return badges;
        }

        protected Boolean isAchieved(BadgeType badge) {
            return checkAchieved(badge, currentProgression);
        }

        private Boolean checkAchieved(BadgeType bt, int currentProgression) {
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
     * Enum badge types.
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

        BadgeType(int stringID, int badgeID, int badgeGoal) {
            resourceID = stringID;
            this.badgeImageID = badgeID;
            this.badgeGoal = badgeGoal;
        }

        public int getBadgeImageID() {
            return badgeImageID;
        }

        public int getBadgeGoal() {
            return badgeGoal;
        }

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
