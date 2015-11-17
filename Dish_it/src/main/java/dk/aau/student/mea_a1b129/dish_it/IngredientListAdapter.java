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
 */
class IngredientListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "ILA";
    private final Context context;
    private HashMap<Ingredient.Category, List<Ingredient>> ingredientItems;
    private List<Ingredient.Category> ingredientCategories;
    private ArrayList<Ingredient> chosenIngredients;
    private boolean inEditMode = false;

    /**
     * Edit-mode constructor
     *
     * @param context              context of the app
     * @param ingredientCategories
     * @param ingredients
     * @param inEditMode           - if set to true, a TextView will be displayed with ingredients
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
        final Ingredient ingredient = (Ingredient) getChild(groupPosition, childPosition);

        if (inEditMode) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.ingredient_list_text_item, null);
            }

            TextView ingredientItem = (TextView) convertView.findViewById(R.id.ingredient_list_text_item);
            ingredientItem.setText(ingredient.getName());
        } else {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.ingredient_list_checkbox_item, null);
            }

            final CheckBox ingredientItem = (CheckBox) convertView
                    .findViewById(R.id.ingredient_list_item_checkbox);
            for (Ingredient i : chosenIngredients) {
                if (i.getIngredientsID() == ingredient.getIngredientsID()) {
                    ingredientItem.setChecked(true);
                }
            }

            ingredientItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                    if (checked) {
                        chosenIngredients.add(ingredient);
                    } else {
                        Log.d(TAG, "Removing ingredient " + ingredient.getName() + " Size before: " + chosenIngredients.size());
                        for (int i = 0; i < chosenIngredients.size(); i++) {
                            if (ingredient.getIngredientsID() == chosenIngredients.get(i).getIngredientsID()) {
                                chosenIngredients.remove(i);
                            }
                        }
                        Log.d(TAG, "Size after: " + chosenIngredients.size());
                    }
                }
            });
            ingredientItem.setText(ingredient.getName());
        }
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
        String categoryTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater
                    .inflate(R.layout.ingredient_list_group, null);

        }
        TextView ingredientCategoryHeader = (TextView) convertView
                .findViewById(R.id.ingredient_list_group_text);
        ingredientCategoryHeader.setTypeface(null, Typeface.BOLD);
        ingredientCategoryHeader.setText(categoryTitle);
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

    public ArrayList<Ingredient> getChosenIngredients() {
        return chosenIngredients;
    }
}
