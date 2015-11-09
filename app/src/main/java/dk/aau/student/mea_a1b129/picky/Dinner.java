package dk.aau.student.mea_a1b129.picky;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
Dinner class for dinner objects to populate the app.
TODO: Make a constructor method for Class.
 */
public class Dinner {
    public static final String TABLE_NAME = "MealsTable";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CUISINE = "cuisine";
    public static final String KEY_INGREDIENTS_ID = "ingredients_id";
    public static final String KEY_RATING = "rating";
    public static final String KEY_DATE = "date";

    private int dinnerID;
    private String name;
    private String description;
    private String cuisine;
    private int rating;
    private List<Integer> ingredientID = new ArrayList<>();
    private Date date;

    public Dinner() {

    }

    public void setDinnerID(int id) {
        dinnerID = id;
    }

    public int getDinnerID() {
        return dinnerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String newCuisine) {
        cuisine = newCuisine;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void addIngredients(int newIngredientID) {
        ingredientID.add(newIngredientID);
    }

    public boolean removeIngredients(int ingredientsID) {
        if(ingredientID.contains(ingredientsID)) {
            ingredientID.remove(ingredientsID);
            return true;
        } else {
            return false;
        }
    }

    public List<Integer> getIngredientID() {
        return ingredientID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
