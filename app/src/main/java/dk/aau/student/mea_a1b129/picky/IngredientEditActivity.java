package dk.aau.student.mea_a1b129.picky;

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

public class IngredientEditActivity extends AppCompatActivity
        implements IngredientDialogFragment.OnFragmentInteractionListener {

    private static final String TAG = IngredientEditActivity.class.getSimpleName();
    private IngredientRepository ir;
    private IngredientListAdapter ila;
    private ExpandableListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ingredient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_ingredient_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = (ExpandableListView) findViewById(R.id.edit_ingredient_expandable_list);


        ila = new IngredientListAdapter(getApplicationContext(), Arrays.asList(Ingredient.Category.values()), updateIngredientMap(), true);
        listView.setAdapter(ila);


        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                IngredientDialogFragment idf = new IngredientDialogFragment();
                Bundle b = new Bundle();
                b.putInt("ingredientID", ((Ingredient) ila.getChild(groupPosition, childPosition)).getIngredientsID());
                idf.setArguments(b);
                idf.show(ft, "edit_ingredient_fragment");
                return true;
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long pos = listView.getExpandableListPosition(position);
                int itemType = ExpandableListView.getPackedPositionType(pos);
                int groupPos = ExpandableListView.getPackedPositionGroup(pos);
                int childPos = ExpandableListView.getPackedPositionChild(pos);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    Ingredient ingredient = (Ingredient) ila.getChild(groupPos, childPos);
                    ir.deleteIngredient(ingredient.getIngredientsID());
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.edit_ingredient_delete_success), Toast.LENGTH_SHORT).show();
                    updateAdapter();
                    return true;
                }
                return false;
            }
        });

        Button addIngredient = (Button) findViewById(R.id.edit_ingredient_add_ingredient);
        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                IngredientDialogFragment idf = new IngredientDialogFragment();
                idf.show(ft, "add_ingredient_fragment");
            }
        });
    }

    public HashMap<Ingredient.Category, List<Ingredient>> updateIngredientMap() {
        ir = new IngredientRepository(getApplicationContext());
        List<Ingredient> ingredientList = ir.getAllIngredients();
        List<Ingredient.Category> ingredientCategories = Arrays.asList(Ingredient.Category.values());
        HashMap<Ingredient.Category, List<Ingredient>> ingredientsMap = new HashMap<>();
        for (Ingredient.Category c : ingredientCategories) {
            List<Ingredient> temp = new ArrayList<>();
            for (Ingredient i : ingredientList) {
                if (c.equals(i.getCategory())) {
                    temp.add(i);
                    Log.v(TAG, "Adding ingredient: " + i.getName());
                }
            }
            ingredientsMap.put(c, temp);
        }

        return ingredientsMap;
    }

    public void updateAdapter() {
        ila = new IngredientListAdapter(getApplicationContext(), Arrays.asList(Ingredient.Category.values()), updateIngredientMap(), true);
        listView.setAdapter(ila);
        ila.notifyDataSetChanged();
    }

    @Override
    public void onFragmentInteraction(boolean newIngredientsAvailable) {
        if (newIngredientsAvailable) {
            Log.v(TAG, "New ingredients available - updating adapter");
            updateAdapter();
        }
    }
}
