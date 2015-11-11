package dk.aau.student.mea_a1b129.picky;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_home_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //New dinner suggestion button.
        Button newSuggestionButton = (Button) findViewById(R.id.home_dinner_new_suggestion_button);
        newSuggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewDinnerSuggestion();
            }
        });

        //Floating action button.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() { //TODO: Add dinner to log when FAB is clicked.
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddDinnerActivity.class);
                startActivity(i);
            }
        });

        //Open and close navigation menu.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //populateDB();
        getNewDinnerSuggestion();
    }

    private void getNewDinnerSuggestion() {
        // Make the repositories.
        DinnerRepository dr = new DinnerRepository(this.getApplicationContext());
        IngredientRepository ir = new IngredientRepository(this.getApplicationContext());

        //Find the views
        TextView dinnerTitle = (TextView) findViewById(R.id.home_dinner_title);
        TextView dinnerDescription = (TextView) findViewById(R.id.home_dinner_description);
        RatingBar dinnerRating = (RatingBar) findViewById(R.id.home_dinner_rating);
        TextView dinnerCategory = (TextView) findViewById(R.id.home_dinner_category);

        //Get a random suggestion TODO: Make Algorithm class for Dinner suggestions.
        List<Dinner> allDinners = dr.getDinnerList();
        Dinner d;
        if(allDinners == null) {
            d = new Dinner();
            d.setName("No dinners in your log :(");
        }
        else {
            int currentlySuggestedDinnerID = allDinners.get(new Random().nextInt(allDinners.size())).getDinnerID();
            Log.d("HomeActivity", "DinnerID suggested: " + currentlySuggestedDinnerID);
            d = dr.getDinner(currentlySuggestedDinnerID);
        }
        dinnerTitle.setText(d.getName());
        dinnerDescription.setText(d.getDescription());
        dinnerRating.setRating(d.getRating());
        dinnerCategory.setText(d.getCuisine());

        List<Integer> ingredientIDList = d.getIngredientID();
        List<Ingredient> ingredientList = new ArrayList<>();
        //Find the ingredients by ingredientsID. TODO: This should probably be handled by IngredientRepository or the DinnerRepository.
        for(int i : ingredientIDList) {
            ingredientList.add(ir.getIngredient(i));
        }
        IngredientGridAdapter iga = new IngredientGridAdapter(this, ingredientList);
        GridView gridView = (GridView) findViewById(R.id.home_dinner_gridview);
        gridView.setAdapter(iga);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    //TODO move onNavigationItemSelected into own super class (or interface?) and implement all activities from super class menu.
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //Intent intent = new Intent(this, HomeActivity.class);
            //startActivity(intent);
        } else if (id == R.id.nav_dinner_history) {
            Intent intent = new Intent(this, DinnerHistory.class);
            startActivity(intent);
        } else if (id == R.id.nav_dinner_plan) {
            //TODO: Make dinner plan Activity
            Toast.makeText(getApplicationContext(), "Coming soon.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mda15@student.aau.dk"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Picky Feedback");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if (id == R.id.nav_preferences) {
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_settings) {
            //TODO: Make settings Activity
            Toast.makeText(getApplicationContext(), "Coming soon.", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void populateDB() {
        DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
        ContentValues cv = new ContentValues();

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2015, 10, 8); //yyyy, MM, dd
        cv.put(Dinner.KEY_NAME, "Sandwich");
        cv.put(Dinner.KEY_DESCRIPTION, "Make me a sandwich" );
        cv.put(Dinner.KEY_INGREDIENTS_ID, "1, 2, 3" );
        cv.put(Dinner.KEY_RATING, 2);
        cv.put(Dinner.KEY_CUISINE, "Waifu food" );
        cv.put(Dinner.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(cal.getTime()));
        dbHelper.getWritableDatabase().insert(Dinner.TABLE_NAME, null, cv);

        cv.clear();
        cal.clear();
        cal.set(2015, 11, 9); //yyyy, MM, dd
        cv.put(Dinner.KEY_NAME, "Bacon Cheese Burger");
        cv.put(Dinner.KEY_DESCRIPTION, "I can haz cheez?" );
        cv.put(Dinner.KEY_INGREDIENTS_ID, "2, 3, 1" );
        cv.put(Dinner.KEY_RATING, 3);
        cv.put(Dinner.KEY_CUISINE, "Cat food" );
        cv.put(Dinner.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(cal.getTime()));
        dbHelper.getWritableDatabase().insert(Dinner.TABLE_NAME, null, cv);

        cv.clear();
        cal.clear();
        cv.put(Dinner.KEY_NAME, "Steak" );
        cv.put(Dinner.KEY_DESCRIPTION, "Man food");
        cv.put(Dinner.KEY_INGREDIENTS_ID, "1");
        cv.put(Dinner.KEY_RATING, 5);
        cv.put(Dinner.KEY_CUISINE, "Le french");
        cv.put(Dinner.KEY_DATE, new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(new Date()));
        dbHelper.getWritableDatabase().insert(Dinner.TABLE_NAME, null, cv);

        cv.clear();
        cv.put(Ingredient.KEY_NAME, "Chili");
        cv.put(Ingredient.KEY_DESCRIPTION, "Hot as my waifu");
        cv.put(Ingredient.KEY_CATEGORY, "Spice");
        dbHelper.getWritableDatabase().insert(Ingredient.TABLE_NAME, null, cv);


        cv.clear();
        cv.put(Ingredient.KEY_NAME, "Onion");
        cv.put(Ingredient.KEY_DESCRIPTION, "Makes you sad :(");
        cv.put(Ingredient.KEY_CATEGORY, "Vegetable");
        dbHelper.getWritableDatabase().insert(Ingredient.TABLE_NAME, null, cv);

        cv.clear();
        cv.put(Ingredient.KEY_NAME, "Potato");
        cv.put(Ingredient.KEY_DESCRIPTION, "Stuff the Irish likes");
        cv.put(Ingredient.KEY_CATEGORY, "Vegetable");
        dbHelper.getWritableDatabase().insert(Ingredient.TABLE_NAME, null, cv);

        cv.clear();
        System.out.println("Database populated");
    }
}
