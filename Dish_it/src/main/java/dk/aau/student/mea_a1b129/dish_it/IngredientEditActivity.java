package dk.aau.student.mea_a1b129.dish_it;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * Activity used to edit Ingredients.
 */
public class IngredientEditActivity extends AppCompatActivity
        implements IngredientEditFragment.OnFragmentInteractionListener {

    private static final String TAG = IngredientEditActivity.class.getSimpleName();
    private IngredientRepository ir;
    private IngredientListAdapter ila;
    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Activity layout..
        setContentView(R.layout.activity_edit_ingredient);
        //Find the toolbar and specify what it does.
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_ingredient_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            //When the back button is pressed, go back 1 page.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Find the ExpandableListView and save it as an instance variable.
        listView = (ExpandableListView) findViewById(R.id.edit_ingredient_expandable_list);
        //Make a list adapter..
        ila = new IngredientListAdapter(getApplicationContext(), Arrays.asList(Ingredient.Category.values()), updateIngredientMap(), true);
        //The adapter to the exp. list view.
        listView.setAdapter(ila);

        //Set Click listeners to the list view..
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //When a sub-item (not header) is clicked, then start a fragments transaction..
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                IngredientEditFragment idf = new IngredientEditFragment();
                //Make a bundle, in which we'll save the Ingredient ID.
                Bundle b = new Bundle();
                b.putInt("ingredientID", ((Ingredient) ila.getChild(groupPosition, childPosition)).getIngredientID());
                //Set the arguments with the bundle.
                idf.setArguments(b);
                //Finally show the fragment..
                idf.show(ft, "edit_ingredient_fragment");
                //Return true, to indicate, that we're done..
                return true;
            }
        });

        //Set listeners for what, should happen on a long-press..
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Save all of the parameters for later use..
                long pos = listView.getExpandableListPosition(position);
                int itemType = ExpandableListView.getPackedPositionType(pos);
                int groupPos = ExpandableListView.getPackedPositionGroup(pos);
                int childPos = ExpandableListView.getPackedPositionChild(pos);

                //If the long clicked item is a child - not a parent aka. header...
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    //Get the Ingredient and that position..
                    Ingredient ingredient = (Ingredient) ila.getChild(groupPos, childPos);
                    //Delete the Ingredient..
                    ir.deleteIngredient(ingredient.getIngredientID());
                    //Notify the user with a pop-up Toast.
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.edit_ingredient_delete_success), Toast.LENGTH_SHORT).show();
                    //Update the adapter with a new list..
                    updateAdapter();
                    //Indicate that the action completed successfully..
                    return true;
                }
                //The item wasn't a child - so return false..
                return false;
            }
        });

        Button addIngredient = (Button) findViewById(R.id.edit_ingredient_add_ingredient);
        //Add listener, for the Add Ingredient-button..
        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start a new fragment...
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                IngredientEditFragment idf = new IngredientEditFragment();
                idf.show(ft, "add_ingredient_fragment");
            }
        });
    }

    /**
     * Private helper method to get the latest HashMap of the Ingredients available.
     * @return a HashMap of the Ingredients.
     */
    private HashMap<Ingredient.Category, List<Ingredient>> updateIngredientMap() {
        //Make a new IngredientRepository, which will feed us with Ingredients..
        ir = new IngredientRepository(getApplicationContext());
        //Make a list, where we'll save the Ingredients temporarily..
        List<Ingredient> ingredientList = ir.getAllIngredients();
        //The HashMap that we'll return later with all of the Ingredients and their Categories..
        HashMap<Ingredient.Category, List<Ingredient>> ingredientsMap = new HashMap<>();
        //First we iterate through the list of Categories..
        for (Ingredient.Category c : Ingredient.Category.values()) {
            //Temporary list to save ingredients in..
            List<Ingredient> temp = new ArrayList<>();
            //.. Then we iterate through the list of ingredients..
            for (Ingredient i : ingredientList) {
                //If the ingredient in the list matches the category, then we add it to the temp list..
                if (c.equals(i.getCategory())) {
                    temp.add(i);
                    Log.v(TAG, "Adding ingredient: " + i.getName());
                }
            }
            //Lastly, we add the temp list to the HashMap..
            ingredientsMap.put(c, temp);
        }
        //Finally, we return the HashMap
        return ingredientsMap;
    }

    /**
     * Private helper method to update the adapter with the latest list of ingredients.
     */
    private void updateAdapter() {
        //Make a new adapter
        ila = new IngredientListAdapter(getApplicationContext(), Arrays.asList(Ingredient.Category.values()), updateIngredientMap(), true);
        //Set the adaptor to the list view.
        listView.setAdapter(ila);
        //Notify the adapter that the dataset has changed.
        ila.notifyDataSetChanged();
    }

    /**
     * Implemented interface method to listen for new ingredients from the fragment.
     * @param newIngredientsAvailable
     */
    @Override
    public void onFragmentInteraction(boolean newIngredientsAvailable) {
        if (newIngredientsAvailable) {
            Log.v(TAG, "New ingredients available - updating adapter");
            updateAdapter();
        }
    }
}
