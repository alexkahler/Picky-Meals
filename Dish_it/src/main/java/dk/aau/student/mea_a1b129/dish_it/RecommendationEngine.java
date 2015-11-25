package dk.aau.student.mea_a1b129.dish_it;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Recommendation engine to suggest meals based on user history and preferences.
 * @author Aleksander Kähler, Group B129, Aalborg University
 */
class RecommendationEngine {

    /*
    Class variables:
    Repositories
    DinnerList
    Last recommendation generated: - save in UserSharePrefs?


    Preferences input:
    Meal variables:
    - When eaten
    - Meal rating

    User variables:
    - Variedness meals
    - Price
    - Available Ingredients
     */

    private static final String TAG = RecommendationEngine.class.getSimpleName();
    private IngredientRepository ir;
    private DinnerRepository dr;
    private static int recommendedDinner;
    private static final int RATING_TOTAL = 5;
    private int ratingWeight = 2;
    private int variedWeight = 1;
    private static final int variedLimit = 7;
    private static List<Integer> previouslyRecommended = new ArrayList<>();

    /**
     * Constructor for the RecommendationEngine.
     */
    public RecommendationEngine(IngredientRepository ir, DinnerRepository dr) {
        this.ir = ir;
        this.dr = dr;
    }

    public int getDinnerRecommendation() {
        return recommendedDinner;
    }

    public void generateNewRecommendation() {
        List<Dinner> dinnerList = dr.getDinnerList();
        Calendar dateLimit = Calendar.getInstance();
        dateLimit.add(Calendar.DATE, -Math.abs(variedLimit));
        double dinnerScore = 0;
        for(Dinner d : dinnerList) {
            float ratingScore = (d.getRating() / RATING_TOTAL) * ratingWeight;
            double variedScore = ((double)(Calendar.getInstance().getTime().getTime() - d.getDate().getTime()) /
                    (Calendar.getInstance().getTime().getTime() - dateLimit.getTime().getTime())) * variedWeight;
            double score = ratingScore + variedScore;
            if (score >= dinnerScore && !previouslyRecommended.contains(d.getDinnerID())) {
                recommendedDinner = d.getDinnerID();
                dinnerScore = score;
                Log.d(TAG, "New recommendation: " + d.getName());
            }
        }
        previouslyRecommended.add(recommendedDinner);
        if(previouslyRecommended.size() == dinnerList.size()) {
            previouslyRecommended.clear();
        }
    }

    public void resetEngine() {
        previouslyRecommended.clear();
    }
}
