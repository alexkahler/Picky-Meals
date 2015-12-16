package dk.aau.student.mea_a1b129.dish_it;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
Dinner class for dinner objects to populate the app.
TODO: Make a constructor method for Class.
TODO: Make cuisine a database dependent selection.
 */

/**
 * Dinner class, which holds each individual dinner.
 * @author Aleksander Kähler, Group B129, Aalborg University
 */
public class Dinner implements Comparable {

    //These public static final Strings are used to refer to each table column in the database.
    public static final String TABLE_NAME = "MealsTable";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CUISINE = "cuisine";
    public static final String KEY_INGREDIENTS_ID = "ingredients_id";
    public static final String KEY_RATING = "rating";
    public static final String KEY_DATE = "date";
    public static final String KEY_PRICE = "price";
    private final List<Integer> ingredientID = new ArrayList<>();
    private int dinnerID;
    private String name;
    private String description;
    private String cuisine;
    private float rating;
    private Date date;
    private double price;

    //Empty constructor
    public Dinner() {
    }

    /**
     * Get the dinner ID of the current dinner.
     * @return
     */
    public int getDinnerID() {
        return dinnerID;
    }

    /**
     * Set the dinner ID of the Dinner
     * @param id the ID of the dinner to be set.
     */
    public void setDinnerID(@NonNull int id) {
        dinnerID = id;
    }

    /**
     * Get the name of the Dinner.
     * @return the name as a String
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the Dinner
     * @param newName the new name of the Dinner.
     */
    public void setName(@NonNull String newName) {
        name = newName;
    }

    /**
     * Get the description of the Dinner
     * @return the description of the Dinner as a String.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the Dinner
     * @param newDescription the new description to be set.
     */
    public void setDescription(@Nullable String newDescription) {
        description = newDescription;
    }

    /**
     * Get the cuisine of the Dinner.
     * @return the cuisine as a String.
     */
    public String getCuisine() {
        return cuisine;
    }

    /**
     * Set the cuisine of the Dinner.
     * @param newCuisine the new cuisine to be set, as a String.
     */
    public void setCuisine(@Nullable String newCuisine) {
        cuisine = newCuisine;
    }

    /**
     * Get the current rating of the Dinner
     * @return the rating as a float.
     */
    public float getRating() {
        return rating;
    }

    /**
     * Set the rating of the Dinner
     * @param rating the rating as a float.
     */
    public void setRating(@NonNull float rating) {
        this.rating = rating;
    }

    /**
     * Add an Ingredient to the Dinner.
     * @param newIngredientID the ID of the Ingredient to be added.
     * @see Ingredient
     */
    public void addIngredients(@NonNull int newIngredientID) {
        ingredientID.add(newIngredientID);
    }

    /**
     * Remove an Ingredient from the Ingredient.
     * @param ingredientsID the ID of the Ingredient to be removed.
     * @return returns true if the ingredient was successfully removed.
     * @see Ingredient
     */
    public boolean removeIngredients(@NonNull int ingredientsID) {
        //Check to see if the list of Ingredients contain the ID.
        if (ingredientID.contains(ingredientsID)) {
            ingredientID.remove(ingredientsID);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a list of all the Ingredients in the Dinner
     * @return a List of Ingredients
     * @see List
     */
    public List<Integer> getIngredientID() {
        return ingredientID;
    }

    /**
     * Get the date of the Dinner
     * @return the date of the Dinner as a Date-object
     * @see Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the Date of the Dinner
     * @param date the date to be set
     * @see Date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get the price of the Dinner
     * @return the price as a double
     */
    public double getPrice() {
        return price;
    }

    /**
     * Set the price of the Dinner
     * @param price the price as a double
     */
    public void setPrice(@NonNull double price) {
        this.price = price;
    }

    /**
     * Compares one Dinner to another and returns which is newer.
     * Implemented method from Comparable-interface
     *
     * @param another Dinner object.
     * @return an int < 0 if this Date is less than the specified Date, 0 if they are equal, and an int > 0 if this Date is greater
     * @throws ClassCastException
     */
    @Override
    public int compareTo(@NonNull Object another) throws ClassCastException {
        Dinner d = (Dinner) another;
        return d.getDate().compareTo(getDate());
    }
}
