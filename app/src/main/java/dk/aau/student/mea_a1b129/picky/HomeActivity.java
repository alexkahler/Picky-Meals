package dk.aau.student.mea_a1b129.picky;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private int currentlySuggestedDinnerID;

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        // populateDB();
        getNewDinnerSuggestion();




        /*
        TextView ingredient1 = (TextView) findViewById(R.id.home_dinner_ingredient);
        ingredient1.setText(ir.getIngredient(dr.getDinner(1).getIngredientID().get(0)).getName());

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.content_home_rlayout);
        LinearLayout llayout = (LinearLayout) findViewById(R.id.content_home_llayout);
        for(int i = 0; i <= 5; i++) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            TextView tv = new TextView(this);
            tv.setTextAppearance(this, android.R.style.TextAppearance_Large);
            tv.setText(dr.getDinner(1).getName());
            ll.addView(tv);
            llayout.addView(ll);
        }
        */
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

        //Get a random suggestion
        List<Dinner> allDinners = dr.getAllDinners();
        currentlySuggestedDinnerID = new Random().nextInt(allDinners.size()) + 1;
        dinnerTitle.setText(dr.getDinner(currentlySuggestedDinnerID).getName());
        dinnerDescription.setText(dr.getDinner(currentlySuggestedDinnerID).getDescription());
        dinnerRating.setRating(dr.getDinner(currentlySuggestedDinnerID).getRating());
        dinnerCategory.setText(dr.getDinner(currentlySuggestedDinnerID).getCuisine());


        List<Integer> ingredientIDList = dr.getDinner(currentlySuggestedDinnerID).getIngredientID();
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
    //TODO move onNavigationItemSelected into own super class and extend all activities from super class menu.
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_dinner_history) {
            Intent intent = new Intent(this, DinnerHistory.class);
            startActivity(intent);
        } else if (id == R.id.nav_dinner_plan) {
            //TODO:
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
            //TODO make preferences Activity

        } else if (id == R.id.nav_settings) {
            Toast.makeText(getApplicationContext(), "Coming soon.", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void populateDB() {
        DatabaseHelper dbHelper = new DatabaseHelper(this.getApplicationContext());
        ContentValues cv = new ContentValues();
        cv.put(Dinner.KEY_NAME, "Sandwich");
        cv.put(Dinner.KEY_DESCRIPTION, "Make me a sandwich");
        cv.put(Dinner.KEY_INGREDIENTS_ID, "1, 2, 3");
        cv.put(Dinner.KEY_RATING, 3);
        cv.put(Dinner.KEY_CUISINE, "Waifu food");
        dbHelper.getWritableDatabase().insert(Dinner.TABLE_NAME, null, cv);

        cv.clear();
        cv.put(Dinner.KEY_NAME, "Bacon Cheese Burger");
        cv.put(Dinner.KEY_DESCRIPTION, "I can haz cheez?");
        cv.put(Dinner.KEY_INGREDIENTS_ID, "2, 3, 1");
        cv.put(Dinner.KEY_RATING, 3);
        cv.put(Dinner.KEY_CUISINE, "Cat food");
        dbHelper.getWritableDatabase().insert(Dinner.TABLE_NAME, null, cv);

        cv.clear();

        cv.put(Dinner.KEY_NAME, "Steak");
        cv.put(Dinner.KEY_DESCRIPTION, "Man food");
        cv.put(Dinner.KEY_INGREDIENTS_ID, "1");
        cv.put(Dinner.KEY_RATING, 3);
        cv.put(Dinner.KEY_CUISINE, "Le french");
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
