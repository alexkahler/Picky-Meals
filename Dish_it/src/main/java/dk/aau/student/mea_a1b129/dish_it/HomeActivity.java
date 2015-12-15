package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GameEngine.ExperienceChangeable {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int CURRENT_VERSION = 1;
    private static String USERNAME = null;

    private static Context context;
    private static RecommendationEngine re;
    private static GameEngine ge;
    private int currentlySuggestedDinnerID;
    private DinnerRepository dr;
    private IngredientRepository ir;
    private TextView dinnerTitle, dinnerDescription, userLevel;
    private ProgressBar experienceBar;
    private RatingBar dinnerRating;
    private TextView dinnerCategory;
    private Calendar lastLogin;


    public static Context getContext() {
        return context;
    }

    public static int getCurrentVersion() {
        return CURRENT_VERSION;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_home_activity);
        context = getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        USERNAME = preferences.getString("username", "Anonymous");


        ge = new GameEngine(context);
        populateViews();
        populateNavigationDrawer();
        getNewDinnerSuggestion();

        try {

            lastLogin.setTime(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).parse(preferences.getString("lastLogin", null)));
        } catch (Exception e ) {
            lastLogin = Calendar.getInstance(new Locale("da", "DK"));
            Log.e(TAG, "Couldn't parse last login date from string - maybe there wasn't one? " + e.getMessage());
        }

        //Create a new calendar with today's date and clear the time from it.
        Calendar currentCalendar = Calendar.getInstance(new Locale("da", "DK"));
        currentCalendar.clear(Calendar.HOUR);
        currentCalendar.clear(Calendar.MINUTE);
        currentCalendar.clear(Calendar.SECOND);
        currentCalendar.clear(Calendar.MILLISECOND);
        //Compare the dates and see if today's date is after last login.
        if(currentCalendar.after(lastLogin)) {
            ge.addExperience(50);
            //Run the interface.
            updateExperience(ge.getCurrentExperience(), ge.experienceForNextLevel(), ge.getCurrentLevel());
            Toast.makeText(context, "First login today! +50XP", Toast.LENGTH_SHORT).show();
            ge.trackProgression(GameEngine.ProgressionType.APP_OPENED, 1);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("lastLogin", new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(Calendar.getInstance().getTime()));
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void populateViews() {
        //New dinner suggestion button.
        Button newSuggestionButton = (Button) findViewById(R.id.home_dinner_new_suggestion_button);
        newSuggestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewDinnerSuggestion();
                ge.trackProgression(GameEngine.ProgressionType.DINNERS_SUGGESTED, 1);
            }
        });

        //Floating action button.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentlySuggestedDinnerID > 0) { //check if there's a dinner suggested.
                    Intent i = new Intent(context, AddDinnerActivity.class);
                    i.putExtra("dinnerID", currentlySuggestedDinnerID);
                    startActivity(i);
                }
            }
        });

        //Find the views
        dinnerTitle = (TextView) findViewById(R.id.home_dinner_title);
        dinnerDescription = (TextView) findViewById(R.id.home_dinner_description);
        dinnerRating = (RatingBar) findViewById(R.id.home_dinner_rating);
        dinnerCategory = (TextView) findViewById(R.id.home_dinner_category);

        dr = new DinnerRepository(context);
        ir = new IngredientRepository(context);
        re = new RecommendationEngine(context, ir, dr);

    }

    private void populateNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Open and close navigation menu.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home_activity);

        userLevel = (TextView) headerLayout.findViewById(R.id.nav_header_level_text);
        experienceBar = (ProgressBar) headerLayout.findViewById(R.id.nav_header_experience);
        TextView username = (TextView) headerLayout.findViewById(R.id.nav_header_name_title);
        username.setText(USERNAME);
        updateExperience(ge.getCurrentExperience(), ge.experienceForNextLevel(), ge.getCurrentLevel());
    }


    private void getNewDinnerSuggestion() {
        if (dr.getDinnerList() == null || dr.getDinnerList().isEmpty()) {
            dinnerTitle.setText(getResources().getText(R.string.home_dinner_no_dinner_found));
        } else {
            re.generateNewRecommendation();
            currentlySuggestedDinnerID = re.getDinnerRecommendation();
            Log.i(TAG, "DinnerID suggested: " + currentlySuggestedDinnerID);
            Dinner d = dr.getDinner(currentlySuggestedDinnerID);
            //Set the Dinner context to views.
            dinnerTitle.setText(d.getName());
            dinnerDescription.setText(d.getDescription());
            dinnerRating.setRating(d.getRating());
            dinnerCategory.setText(d.getCuisine());

            //Find the ingredients by ingredientsID. TODO: This should probably be handled by IngredientRepository or the DinnerRepository.
            List<Integer> ingredientIDList = d.getIngredientID();
            List<Ingredient> ingredientList = new ArrayList<>();
            for (int i : ingredientIDList) {
                ingredientList.add(ir.getIngredient(i));
            }
            IngredientGridAdapter iga = new IngredientGridAdapter(this, ingredientList);
            GridView gridView = (GridView) findViewById(R.id.home_dinner_gridview);
            gridView.setAdapter(iga);
        }
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
            Intent i = new Intent(context, PreferencesActivity.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_refresh) {
            re.resetEngine();
            getNewDinnerSuggestion();
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

    @Override
    public void updateExperience(double currentExperience, double experienceForNextLevel, long currentLevel) {

        userLevel.setText(getResources().getString(R.string.nav_header_level_prefix) + " " + currentLevel);
        experienceBar.setMax((int)experienceForNextLevel);
        experienceBar.setProgress((int)currentExperience);
    }
}