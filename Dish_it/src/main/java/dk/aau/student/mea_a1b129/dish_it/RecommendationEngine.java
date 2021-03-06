package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Recommendation engine to suggest meals based on user history and preferences.
 * @author Aleksander Kähler, Group B129, Aalborg University
 */
class RecommendationEngine {

    private static final String TAG = RecommendationEngine.class.getSimpleName();
    private IngredientRepository ir;
    private DinnerRepository dr;
    private static int recommendedDinner = -1;
    private static final int RATING_TOTAL = 5;
    private int ratingWeight = 2;
    private boolean ratingEnabled;
    private int variedWeight = 0;
    private static final int variedLimit = 0;
    private boolean variedEnabled;
    private static List<Integer> previouslyRecommended = new ArrayList<>();

    /**
     * Constructor for the RecommendationEngine.
     * @param ir IngredientRepository (not implemented yet)
     * @param dr DinnerRepository which will feed Dinners to the RecommendationEngine
     */
    public RecommendationEngine(@NonNull Context context, @NonNull IngredientRepository ir, @NonNull DinnerRepository dr) {
        this.ir = ir;
        this.dr = dr;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        ratingEnabled = sp.getBoolean("ratingKey", true);
        variedEnabled = sp.getBoolean("variedKey", true);
    }

    /**
     * Return the currently recommended Dinner without generating a new recommendation.
     * If this method is run before the generateNewRecommendation() in a new instance, then it will generate a new recommendation before returning the ID.
     * @return int ID of the recommended Dinner
     * @see Dinner
     */
    public int getDinnerRecommendation() {
        //If this is called - without this instance not having generated a recommendation yet, then make a new recommendation first, and then return that number.
        if(recommendedDinner == -1) {
            return generateNewRecommendation();
        }
        //Else just return the int value without generating a new Dinner ID.
        return recommendedDinner;
    }

    /**
     * Generate a new Dinner recommendation based on various factors, herein ratings, history, variedness of the dinner, etc.
     * The Dinner ID is saved in the instance variable of the class. Use getDinnerRecommendation to get the latest generated Dinner ID without generating a new ID.
     * @return int ID of the recommended Dinner.
     * @see Dinner
     */
    public int generateNewRecommendation() {
        List<Dinner> dinnerList = dr.getDinnerList();
        Calendar dateLimit = Calendar.getInstance();

        //make sure the limit is a negative number via. Math.abs
        dateLimit.add(Calendar.DATE, -Math.abs(variedLimit));

        //set highest dinner score to 0 before we begin.
        double dinnerScore = 0;
        double ratingScore = 0;
        double variedScore = 0;

        //iterate through all of the dinners and figure out, which dinner has the highest Dinner Score.
        for(Dinner d : dinnerList) {
            if(ratingEnabled) {
                ratingScore = (((double)d.getRating()/RATING_TOTAL)*ratingWeight);
            }
            if(variedEnabled) {
                variedScore = ((double)(Calendar.getInstance().getTime().getTime() - d.getDate().getTime()) /
                        (Calendar.getInstance().getTime().getTime() - dateLimit.getTime().getTime())) * variedWeight;
            }

            //Add all of the scores together for a final score.
            double score = ratingScore + variedScore;

            //If this was the highest score then save the dinner as our current recommended dinner.
            if (score > dinnerScore && !previouslyRecommended.contains(d.getDinnerID())) {
                recommendedDinner = d.getDinnerID();

                //Assign our new top-score to Dinner Score.
                dinnerScore = score;
            }
        }
        //Now that we're through the For-loop and have found a recommended dinner - we add this to our backlog of recommended dinners, so we don't keep recommending it.
        previouslyRecommended.add(recommendedDinner);
        //However - if we don't have anymore dinners left to take from - clear the backlog and start from the beginning.
        if(previouslyRecommended.size() == dinnerList.size()) {
            previouslyRecommended.clear();
        }
        return recommendedDinner;
    }

    /**
     * Reset the RecommendationEngine and all recommendations and start from the beginning.
     * Be sure to call generateNewRecommendation after this as there will be no Dinner Recommendations after this.
     */
    public void resetEngine() {
        previouslyRecommended.clear();
    }
}