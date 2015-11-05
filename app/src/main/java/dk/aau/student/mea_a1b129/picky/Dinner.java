package dk.aau.student.mea_a1b129.picky;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/*
Dinner class for dinner objects to populate the app.
 */
public class Dinner {
    public static final String DB_TABLE_NAME = "MealsTable";
    public static final String DB_ID = "_id";
    public static final String DB_NAME = "name";
    public static final String DB_DESCRIPTION = "description";
    public static final String DB_CUISINE = "cuisine";
    public static final String DB_INGREDIENTS_ID = "ingredients_id";

    private int dinnerID;
    private String name;
    private String description;
    private String cuisine;
    private List<Integer> ingredients = new ArrayList<>();


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

    public void addIngredients(int newIngredientID) {
        ingredients.add(newIngredientID);
    }

    public boolean removeIngredients(int ingredientsID) {
        if(ingredients.contains(ingredientsID)) {
            ingredients.remove(ingredientsID);
            return true;
        } else {
            return false;
        }
    }

    public List<Integer> getIngredients() {
        return ingredients;
    }
}
