package dk.aau.student.mea_a1b129.dish_it;

import android.support.annotation.NonNull;


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
    private int ingredientID;
    private Category category;

    /**
     * Empty constructor
     */
    public Ingredient() {
    }

    /**
     * Ingredient constructor.
     *
     * @param name     name of the ingredient - must not be null
     * @param category the category of the ingredient, specified in an enum value.
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
     *
     * @param name Name of the ingredient
     */
    public void setName(@NonNull String name) {
        if (name == null || name.equals("")) {
            return;
        }
        this.name = name;
    }

    /**
     * Get the Category of the Ingredient.
     * @return Ingredient.Category of the Ingredient
     * @see dk.aau.student.mea_a1b129.dish_it.Ingredient.Category
     */
    public Ingredient.Category getCategory() {
        return category;
    }

    /**
     * Set the Category of the Category.
     * @param category the Category of the Ingredient
     * @see dk.aau.student.mea_a1b129.dish_it.Ingredient.Category
     */
    public void setCategory(@NonNull Category category) {
        this.category = category;
    }

    /**
     * Get the Ingredient ID
     * @return int ID of the Ingredient
     */
    public int getIngredientID() {
        return ingredientID;
    }

    /**
     * Set the ID of the Ingredient.
     * @param ingredientID the int value of the ID, that the Ingredient should have.
     */
    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    /**
     * Overridden toString method, to show the name value of the Ingredient.
     * @return
     */
    @Override
    public String toString() {
        return name + ", " + category.toString();
    }

    /**
     * Ingredient Categories as enum type.
     */
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

        //The String resource ID of the Category name.
        private final int resourceID;

        /**
         * Ingredient Category.
         *
         * @param id the Android String Resource ID which points to the string in the Category
         * @see dk.aau.student.mea_a1b129.dish_it.R.string
         */
        Category(int id) {
            this.resourceID = id;
        }

        /**
         * Overridden toString method to show the String in the Android resource ID
         * @return
         */
        @Override
        public String toString() {
            return HomeActivity.getContext().getString(resourceID);
        }
    }
}
