package dk.aau.student.mea_a1b129.picky;

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
public class IngredientListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "ILA";
    private Context context;
    private HashMap<Ingredient.Category, List<Ingredient>> ingredientItems;
    private List<Ingredient.Category> ingredientCategories;
    private ArrayList<Ingredient> chosenIngredients;
    private HashMap<Ingredient.Category, ArrayList<Boolean>> checkedState = new HashMap<>();

    public IngredientListAdapter() {

    }

    public IngredientListAdapter(Context context,
                                 List<Ingredient.Category> ingredientCategories,
                                 HashMap<Ingredient.Category, List<Ingredient>> ingredients,
                                 ArrayList<Ingredient> chosenIngredients,
                                 HashMap<Ingredient.Category, ArrayList<Boolean>> previousCheckedState) {
        this.context = context;
        this.ingredientCategories = ingredientCategories;
        this.ingredientItems = ingredients;
        this.chosenIngredients = chosenIngredients;
        if (previousCheckedState == null || previousCheckedState.isEmpty()) {
            for (Ingredient.Category c : ingredientCategories) {
                ArrayList<Boolean> temp = new ArrayList<>();
                if (ingredients.get(c) != null && ingredients.get(c).size() > 0) {
                    for (int j = 0; j < ingredients.get(c).size(); j++) {
                        temp.add(false);
                    }
                }
                checkedState.put(c, temp);
            }
        } else {
            checkedState.putAll(previousCheckedState);
        }
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ingredient_list_item, null);
        }

        final CheckBox ingredientItem = (CheckBox) convertView
                .findViewById(R.id.ingredient_list_item_checkbox);

        if (checkedState.get(ingredient.getCategory()).get(childPosition)) {
            Log.d(TAG, "Trying to set setChecked on " + ingredient.getName());
            ingredientItem.setChecked(true);
        } else {
            ingredientItem.setChecked(false);
        }

        ingredientItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                if (checked) {
                    checkedState.get(ingredient.getCategory()).set(childPosition, true);
                    chosenIngredients.add(ingredient);
                } else {
                    checkedState.get(ingredient.getCategory()).set(childPosition, false);
                    Log.d(TAG, "Removing ingredient " + ingredient.getName() + " Size before: " + chosenIngredients.size());
                    for (int i = 0; i < chosenIngredients.size(); i++) {
                        if (ingredient.getIngredientsID() == chosenIngredients.get(i).getIngredientsID()) {
                            chosenIngredients.remove(i);
                        }
                    }
                    Log.d(TAG, "Size aftter: " + chosenIngredients.size());
                }
                //buttonView.setChecked(checked);
            }
        });
        ingredientItem.setText(ingredient.getName());

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

    public void setChosenIngredients(ArrayList<Ingredient> chosenIngredients) {
        this.chosenIngredients = chosenIngredients;
    }

    public HashMap<Ingredient.Category, ArrayList<Boolean>> getCheckedState() {
        return checkedState;
    }

    public void setCheckedState(HashMap<Ingredient.Category, ArrayList<Boolean>> checkedState) {
        this.checkedState = checkedState;
    }
}
