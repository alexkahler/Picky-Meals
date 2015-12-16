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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * Main HomeActivity - this is the first class that the user will land on after app initialization.
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
    private DinnerRepository dr;
    private IngredientRepository ir;
    private TextView dinnerTitle, dinnerDescription, userLevel;
    private ProgressBar experienceBar;
    private RatingBar dinnerRating;
    private TextView dinnerCategory;
    private Calendar lastLogin;

    /**
     * Static method to return the Context of the application.
     * @return the Application Context
     * @see Context
     */
    public static Context getContext() {
        return context;
    }

    /**
     * The current version of the app.
     * @return the current version as int
     */
    public static int getCurrentVersion() {
        return CURRENT_VERSION;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_home_activity);
        context = getApplicationContext();

        //Get the username from the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        USERNAME = preferences.getString("username", "Anonymous");

        populateViews();
        populateNavigationDrawer();
        getNewDinnerSuggestion();

        try {
            //Here we try to get the time from the last login. If there is no set time, then lastLogin is set to null and SimpleDateFormat will throw an exception
            lastLogin.setTime(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).parse(preferences.getString("lastLogin", null)));
        } catch (Exception e ) {
            //Any thrown Exception is caught here and we'll set the LastLogin date to today's date.
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
            //Run the interface and notify the experience change. We do this while adding 50XP.
            updateExperience(ge.addExperience(50), ge.experienceForNextLevel(), ge.getCurrentLevel());
            //Then we make a notification to notify the user of the XP.
            Toast.makeText(context, "First login today! +50XP", Toast.LENGTH_SHORT).show();
            //Track the progression to our GameEngine.
            ge.trackProgression(GameEngine.ProgressionType.APP_OPENED, 1);
            //Get ready to update the date in our preferences with todays date.
            SharedPreferences.Editor editor = preferences.edit();
            //Insert the date as a String with the format dd-MM-yyyy
            editor.putString("lastLogin", new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(currentCalendar));
            //Apply the preference changes.
            editor.apply();
        }
    }

    /**
     * Inherited super method.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Private helper method to find all Views in layout and assign them to instance variables.
     */
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
            public void onClick(View view) { //TODO: Make a sub-FAB to select if it's a new dinner or updating with the recommended dinner.
                if (re.getDinnerRecommendation() > 0) { //check if there's a dinner suggested.
                    Intent i = new Intent(context, AddDinnerActivity.class);
                    i.putExtra("dinnerID", re.getDinnerRecommendation());
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
        ge = new GameEngine(context);
    }

    /**
     * Private helper method to populate the Navigation Drawer.
     */
    private void populateNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Open and close navigation menu.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Define the toggle.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //The drawer will react to the toggle.
        drawer.setDrawerListener(toggle);
        //Sync the indicator (toggle) to the drawer.
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Find the header layout and inflate it into the navigation drawer.
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home_activity);

        //Find the view for the user level label
        userLevel = (TextView) headerLayout.findViewById(R.id.nav_header_level_text);
        experienceBar = (ProgressBar) headerLayout.findViewById(R.id.nav_header_experience);
        TextView username = (TextView) headerLayout.findViewById(R.id.nav_header_name_title);
        username.setText(USERNAME);
        //Update the userLevel and experienceBar by notifying our listener.
        updateExperience(ge.getCurrentExperience(), ge.experienceForNextLevel(), ge.getCurrentLevel());
    }

    /**
     * Private helper method to get a new dinner suggestion from the RecommendationEngine
     */
    private void getNewDinnerSuggestion() {
        //Test if our dinnerlist is null or empty.
        if (dr.getDinnerList() == null || dr.getDinnerList().isEmpty()) {
            //If so - just set our text in our dinner title to "No Dinner found"
            dinnerTitle.setText(getResources().getText(R.string.home_dinner_no_dinner_found));
        } else {
            //We generate a new recommendation in the engine.
            //Get the dinner with the dinnerID
            Dinner d = dr.getDinner(re.generateNewRecommendation());
            //Set the Dinner content to the views.
            dinnerTitle.setText(d.getName());
            dinnerDescription.setText(d.getDescription());
            dinnerRating.setRating(d.getRating());
            dinnerCategory.setText(d.getCuisine());
            //Find the ingredients by ingredientsID. TODO: This should probably be handled by IngredientRepository or the DinnerRepository.
            List<Integer> ingredientIDList = d.getIngredientID();
            List<Ingredient> ingredientList = new ArrayList<>();
            //Iterate through the list with Ingredient IDs and add them to our ingredient list.
            for (int i : ingredientIDList) {
                ingredientList.add(ir.getIngredient(i));
            }
            //Make a new adapter for our grid with ingredients.
            IngredientGridAdapter iga = new IngredientGridAdapter(this, ingredientList);
            GridView gridView = (GridView) findViewById(R.id.home_dinner_gridview);
            //Set the adapter, which manages how our ingredients are displayed to our grid.
            gridView.setAdapter(iga);
        }
    }

    @Override
    public void onBackPressed() {
        //If the drawer is opened, then we just close it.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //Otherwise we finish our Activity and close the app.
        else {
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

        //If the Settings option was clicked, then make a new Intent which will send the user to the Preferences page.
        if (id == R.id.action_settings) {
            Intent i = new Intent(context, PreferencesActivity.class);
            startActivity(i);
            return true;
        }
        //If the refresh button was pressed, then make a new dinner suggestion.
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
            //If we were at some point to have a navigation drawer in every activity, then we'll use this. But for now we just close the drawer if this is pressed.
        } else if (id == R.id.nav_dinner_history) {
            //Make a new Intent, which will send the user to our Dinner History
            Intent intent = new Intent(this, DinnerHistory.class);
            //Start this Activity with our intent.
            startActivity(intent);
        } else if (id == R.id.nav_dinner_plan) {
            //Coming soon... So for now, we just display a message to the user.
            //TODO: Make dinner plan Activity
            Toast.makeText(getApplicationContext(), "Coming soon.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_statistics) {
            //Make a new Intent saying that we want to use the StatisticsActivity.
            Intent intent = new Intent(this, StatisticsActivity.class);
            //Now start the activity with our intent.
            startActivity(intent);
        } else if (id == R.id.nav_feedback) {
            //New empty intent
            Intent intent = new Intent();
            //With the send action
            intent.setAction(Intent.ACTION_SEND);
            //The type of send should be an email message
            intent.setType("message/rfc822");
            //The email "To" header should be to mda15@student.aau.dk
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mda15@student.aau.dk"});
            //The email "Subject" should state "Dish'it Feedback"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Dish'it Feedback");
            //Check if there's an application on the system which can receive our email intent.
            if (intent.resolveActivity(getPackageManager()) != null) {
                //If there is something that can do this - then make user choose and start the activity.
                startActivity(intent);
            }
        } else if (id == R.id.nav_preferences) {
            //Make a new intent which starts the PreferenceActivity.
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_settings) {
            //Coming soon - so for now just show a message to the user.
            //TODO: Make settings Activity
            Toast.makeText(getApplicationContext(), "Coming soon.", Toast.LENGTH_SHORT).show();
        }
        //After this, we just close the drawer.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *
     * @param currentExperience
     * @param experienceForNextLevel
     * @param currentLevel
     */
    @Override
    public void updateExperience(double currentExperience, double experienceForNextLevel, long currentLevel) {
        //Here we set the text view and the progress bar to indicate the user level and XP progress till the next level.
        userLevel.setText(getResources().getString(R.string.nav_header_level_prefix) + " " + currentLevel);
        experienceBar.setMax((int)experienceForNextLevel);
        experienceBar.setProgress((int)currentExperience);
    }
}