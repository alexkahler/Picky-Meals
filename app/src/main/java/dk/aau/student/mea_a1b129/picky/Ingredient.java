package dk.aau.student.mea_a1b129.picky;

//TODO: Write javadoc for class and methods
public class Ingredient {

    public static final String TABLE_NAME = "IngredientsTable";
    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY = "category";

    private String name;
    private String description;
    private int ingredientsID;
    private String category;

    public Ingredient() {

    }

    public Ingredient(String name, String description, String category, int id) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.ingredientsID = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Set the name for the ingredient
     * @param name Name of the ingredient
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the description of the ingredient
     * @return Description returned as a String.
     * @see String
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getIngredientsID() {
        return ingredientsID;
    }

    public void setIngredientsID(int ingredientsID) {
        this.ingredientsID = ingredientsID;
    }
}
