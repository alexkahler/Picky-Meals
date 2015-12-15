package dk.aau.student.mea_a1b129.dish_it;

import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 *         TODO: Implement remove ingredient when tapping ingredients in ingredient grid.
 */
public class AddDinnerActivity extends AppCompatActivity implements IngredientChooseFragment.DialogDoneListener {
    private static final String TAG = "AddDinnerActivity";
    private EditText dinnerName, dinnerCategory, dinnerDescription, dinnerPrice;
    private Calendar dinnerDate;
    private TextView dateText;
    private RatingBar dinnerRating;
    private Button chooseDate, saveButton, cancelButton, chooseIngredientsButton;
    private GridView gridView;
    private DatePickerDialog datePickerDialog;
    private ArrayList<Ingredient> ingredientList = new ArrayList<>();
    private IngredientGridAdapter iga;
    private int dinnerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dinner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_dinner_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewsByID();
        assignListeners();
        updateAdapter();
        if (getIntent().hasExtra("dinnerID")) {
            DinnerRepository dr = new DinnerRepository(getApplicationContext());
            dinnerID = getIntent().getIntExtra("dinnerID", -1);
            dinnerName.setText(dr.getDinner(dinnerID).getName());
            dinnerCategory.setText(dr.getDinner(dinnerID).getCuisine());
            dinnerDescription.setText(dr.getDinner(dinnerID).getDescription());
            dinnerRating.setRating(dr.getDinner(dinnerID).getRating());
            dateText.setText(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(Calendar.getInstance().getTime()));
            dinnerPrice.setText(dr.getDinner(dinnerID).getPrice() + "");
            IngredientRepository ir = new IngredientRepository(getApplicationContext());
            for (int i : dr.getDinner(dinnerID).getIngredientID()) {
                ingredientList.add(ir.getIngredient(i));
            }
            iga.notifyDataSetChanged();
        }

    }

    /*
    Find all of the related views in the XML schema.
     */
    private void findViewsByID() {
        dinnerName = (EditText) findViewById(R.id.add_dinner_edit_dinner_name);
        dinnerCategory = (EditText) findViewById(R.id.add_dinner_edit_category);
        dinnerDescription = (EditText) findViewById(R.id.add_dinner_edit_dinner_description);
        dinnerRating = (RatingBar) findViewById(R.id.add_dinner_edit_rating);
        dateText = (TextView) findViewById(R.id.add_dinner_date_chosen);
        dinnerPrice = (EditText) findViewById(R.id.add_dinner_edit_price);
        dinnerDate = Calendar.getInstance();
        //We want to set the date to the current date
        dateText.setText(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK"))
                .format(dinnerDate.getTime()));
        chooseDate = (Button) findViewById(R.id.add_dinner_choose_date_button);
        saveButton = (Button) findViewById(R.id.add_dinner_save_button);
        cancelButton = (Button) findViewById(R.id.add_dinner_cancel_button);
        chooseIngredientsButton = (Button) findViewById(R.id.add_dinner_choose_ingredients);
        gridView = (GridView) findViewById(R.id.add_dinner_ingredient_gridview);
    }

    /*
    Here we assign listeners and actions to our buttons.
     */
    private void assignListeners() {
        //First the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DinnerRepository dr = new DinnerRepository(getApplicationContext());
                //If the user hasn't input a name then we stop and ask them to do so.
                if (dinnerName.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please input a name for the dinner", Toast.LENGTH_SHORT).show();
                } else {
                    //Go through all of the ingredients and add their respective ID's.
                    List<Integer> ingredientIDList = new ArrayList<>();
                    for (Ingredient i : ingredientList) {
                        ingredientIDList.add(i.getIngredientsID());
                    }
                    //If we're updating a dinner
                    if (dinnerID > 0) {
                        Log.d(TAG, "Updating dinner with id: " + dinnerID);
                        double price = 0;
                        try {
                            Double.parseDouble(dinnerPrice.getText().toString());
                        } catch(NumberFormatException e) {
                            Log.e(TAG, "Can't format nothing..." + e.getMessage());
                            e.printStackTrace();
                        }
                        dr.updateDinner(
                                dinnerID,
                                dinnerName.getText().toString(),
                                dinnerDescription.getText().toString(),
                                dinnerCategory.getText().toString(),
                                ingredientIDList,
                                Math.round(dinnerRating.getRating()),
                                dinnerDate.getTime(),
                                price);
                    }
                    //else make a new dinner
                    else {
                        Log.d(TAG, "Inserting dinner");
                        double price = 0;
                        try {
                            Double.parseDouble(dinnerPrice.getText().toString());
                        } catch(NumberFormatException e) {
                            Log.e(TAG, "Can't format nothing..." + e.getMessage());
                            e.printStackTrace();
                        }
                        dr.insertDinner(
                                dinnerName.getText().toString(),
                                dinnerDescription.getText().toString(),
                                dinnerCategory.getText().toString(),
                                ingredientIDList,
                                Math.round(dinnerRating.getRating()),
                                dinnerDate.getTime(),
                                price);
                        GameEngine ge = new GameEngine(getApplicationContext());
                        ge.addExperience(100);
                        ge.trackProgression(GameEngine.ProgressionType.DINNERS_ADDED, 1);
                        Toast.makeText(getApplicationContext(), getString(R.string.add_dinner_100XP), Toast.LENGTH_SHORT).show();
                    }
                    //Finish the activity and return to parent activity.
                    finish();
                }
            }
        });

        //If the cancel button was pressed, then we act as if the pressed the back button on the phone.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Here we open up the Fragment where we can choose ingredients.
        chooseIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredientChooseFragment icf = new IngredientChooseFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //We add this fragment to the backstack, so if the user presses the backbutton, only the fragment is closed.
                ft.addToBackStack(null);
                //If the ingreident is not empty, then we put the list into a bundle, so the fragment can retrieve it.
                Log.d(TAG, "In onClick method assigning ingredients to Bundle. ingredientsList: " + ingredientList);
                if (!ingredientList.isEmpty()) {
                    Log.d(TAG, "In if statement with ingredients list " + ingredientList);
                    Bundle b = new Bundle();
                    b.clear();
                    b.putSerializable("ingredientsID", ingredientList);
                    icf.setArguments(b);
                }
                //Start the fragment.
                icf.show(ft, "choose_ingredient_fragment");
            }
        });

        //This part is for showing a date picker, when the Choose Date button is pressed.
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dinnerDate.clear();
                dinnerDate.set(year, monthOfYear, dayOfMonth);
                dateText.setText(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(dinnerDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    /*
    A quick helper function to update our grid with ingredients.
     */
    private void updateAdapter() {
        iga = new IngredientGridAdapter(getApplicationContext(), ingredientList);
        gridView.setAdapter(iga);
        iga.notifyDataSetChanged();
    }

    /**
     * Interface required method from IngredientChooseFragment
     * @param state whether or not the method was successfully called.
     * @param list the new list of ingredients.
     */
    @Override
    public void onDone(boolean state, ArrayList<Ingredient> list) {
        Log.i("AddDinnerActivity", "Got list from fragment: " + list.toString());
        if (state) {
            ingredientList = new ArrayList<>();
            ingredientList = list;
            Log.d(TAG, "Updated ingredientList from fragment " + ingredientList);
            updateAdapter();
        }
    }
}