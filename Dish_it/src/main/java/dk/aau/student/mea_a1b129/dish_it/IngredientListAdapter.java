package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * Adapter to show ingredients in an ExpandableListView
 */
class IngredientListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = IngredientListAdapter.class.getSimpleName();
    private final Context context;
    private HashMap<Ingredient.Category, List<Ingredient>> ingredientItems;
    private List<Ingredient.Category> ingredientCategories;
    private ArrayList<Ingredient> chosenIngredients;
    private boolean inEditMode = false;

    /**
     * Edit-mode constructor
     *
     * @param context the context of the app
     * @param ingredientCategories the categories of the ingredients.
     * @param ingredients the ingredients to be shown in the list
     * @param inEditMode if set to true, a TextView will be displayed with ingredients
     * @see Ingredient
     * @see dk.aau.student.mea_a1b129.dish_it.Ingredient.Category
     * @see Context
     */
    public IngredientListAdapter(Context context, List<Ingredient.Category> ingredientCategories, HashMap<Ingredient.Category, List<Ingredient>> ingredients, boolean inEditMode) {
        this.context = context;
        this.ingredientCategories = ingredientCategories;
        this.ingredientItems = ingredients;
        this.inEditMode = inEditMode;
    }

    /**
     * Checkbox mode constructor
     *
     * @param context
     * @param ingredientCategories
     * @param ingredients
     * @param chosenIngredients
     */
    public IngredientListAdapter(Context context,
                                 List<Ingredient.Category> ingredientCategories,
                                 HashMap<Ingredient.Category, List<Ingredient>> ingredients,
                                 ArrayList<Ingredient> chosenIngredients) {
        this.context = context;
        this.ingredientCategories = ingredientCategories;
        this.ingredientItems = ingredients;
        this.chosenIngredients = chosenIngredients;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ingredientItems.get(ingredientCategories.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (ingredientItems.get(ingredientCategories.get(groupPosition)) == null) {
            return 0;
        }
        return ingredientItems.get(ingredientCategories.get(groupPosition)).size();
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Save the Ingredient in a local variable for later use.
        final Ingredient ingredient = (Ingredient) getChild(groupPosition, childPosition);

        //If we're editing ingredients, then choose this layout mode
        if (inEditMode) {
            //If the convert view hasn't been set..
            if (convertView == null) {
                //Then inflate the convert view with the layout.
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.ingredient_list_text_item, null);
            }
            //Find the text view in the convertview and set it to the name of the ingredient
            TextView ingredientItem = (TextView) convertView.findViewById(R.id.ingredient_list_text_item);
            ingredientItem.setText(ingredient.getName());
        }
        //If we're not in edit mode..
        else {
            //If the convertView hasn't been set..
            if (convertView == null) {
                //.. Then inflate the convertView with layout which contains a check box item..
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.ingredient_list_checkbox_item, null);
            }
            //Find the checkbox in the convertView..
            CheckBox ingredientItem = (CheckBox) convertView
                    .findViewById(R.id.ingredient_list_item_checkbox);

            //Go through the list and add a tick to our chosen ingredients
            for (Ingredient i : chosenIngredients) {
                if (getGroup(groupPosition).equals(i.getCategory().toString()) && i.getIngredientID() == ingredient.getIngredientID()) {
                    ingredientItem.setChecked(true);
                }
            }

            //Set listeners on each checkbox item, so we're notified when a user selects an ingredient.
            ingredientItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                    //If it has been checked..
                    if (checked) {
                        //.. Then add it to our list of chosen ingredients..
                        chosenIngredients.add(ingredient);
                    } else {
                        //Else we remove the ingredient by iterating though our list of chosen ingredients and remove the ingredients, which ID's matches.
                        Log.d(TAG, "Removing ingredient " + ingredient.getName() + " Size before: " + chosenIngredients.size());
                        for (int i = 0; i < chosenIngredients.size(); i++) {
                            if (ingredient.getIngredientID() == chosenIngredients.get(i).getIngredientID()) {
                                chosenIngredients.remove(i);
                            }
                        }
                        Log.d(TAG, "Size after: " + chosenIngredients.size());
                    }
                }
            });
            //Lastly we set the text to the ingredients name.
            ingredientItem.setText(ingredient.getName());
        }
        //Return the convertView to caller method..
        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return ingredientCategories.get(groupPosition).toString();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return ingredientCategories.size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //Here we get the category title from the Group..
        String categoryTitle = (String) getGroup(groupPosition);
        //If the convertView hasn't been set, then we'll inflate it
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater
                    .inflate(R.layout.ingredient_list_group, null);

        }
        //Then we find the textview in the converView, which we'll set..
        TextView ingredientCategoryHeader = (TextView) convertView
                .findViewById(R.id.ingredient_list_group_text);
        //Set the typeface to bold
        ingredientCategoryHeader.setTypeface(null, Typeface.BOLD);
        //Set the header to category name..
        ingredientCategoryHeader.setText(categoryTitle);
        //Return the convertView to the caller method..
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Get Ingredients chosen by the user from the adapter. The List will be null, if the adapter is in edit mode.
     * @return List of Ingredients.
     * @see Ingredient
     */
    public ArrayList<Ingredient> getChosenIngredients() {
        return chosenIngredients;
    }
}
