package dk.aau.student.mea_a1b129.dish_it;

import android.support.annotation.NonNull;
import android.util.Log;


/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 *         Ingredient class to hold an ingredient used in a Dinner class.
 */
public class Ingredient {

    public static final String TABLE_NAME = "IngredientsTable";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_CATEGORY = "category";
    private static final String TAG = "Ingredient";
    private String name;
    private int ingredientsID;
    private Category category;
    public Ingredient() {
    }

    /**
     * Ingredient constructor.
     *
     * @param name        name of the ingredient - must not be null
     * @param category    the category of the ingredient, specified in an enum value.
     * @see Ingredient.Category
     */
    public Ingredient(@NonNull String name, @NonNull Ingredient.Category category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    /**
     * Set the name for the ingredient
     * @param name Name of the ingredient
     */
    public void setName(@NonNull String name) {
        if (name == null || name.equals("")) {
            return;
        }
        this.name = name;
    }

    public Ingredient.Category getCategory() {
        return category;
    }

    public void setCategory(String category) {
        try {
            this.category = Category.valueOf(category);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Couldn't find value of " + category + e.toString());
            e.printStackTrace();
        }
    }

    public int getIngredientsID() {
        return ingredientsID;
    }

    public void setIngredientsID(int ingredientsID) {
        this.ingredientsID = ingredientsID;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Category {
        Meat(R.string.enum_meat),
        Poultry(R.string.enum_poultry),
        Seafood(R.string.enum_seafood),
        Vegetable(R.string.enum_vegetable),
        Herb(R.string.enum_herb),
        Spice(R.string.enum_spice),
        Fruit(R.string.enum_fruit),
        Nuts_Seeds(R.string.enum_nuts),
        Dairy(R.string.enum_dairy),
        Cereal(R.string.enum_cereal),
        Other(R.string.enum_other),
        Oil(R.string.enum_oil);

        private final int resourceID;

        /**
         * Category of the ingredient.
         *
         * @param id the Android Resource ID which points to the string in the Category
         * @see dk.aau.student.mea_a1b129.dish_it.R.string
         */
        Category(int id) {
            this.resourceID = id;
        }

        public Category[] getAllCategories() {
            return Category.values();
        }

        @Override
        public String toString() {
            return HomeActivity.getContext().getString(resourceID);
        }
    }

}
