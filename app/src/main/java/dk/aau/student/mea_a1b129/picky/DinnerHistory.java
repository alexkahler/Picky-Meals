package dk.aau.student.mea_a1b129.picky;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * DinnerHistory activity to show the latest food logs to user.
 * TODO: Compensate for toolbar in layout.
 * TODO: Implement filter logic to ListView.
 */
public class DinnerHistory extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

    private Calendar fromDate, toDate;
    private boolean isFilterFromActive = false;
    private boolean isFilterToActive = false;
    private Button filterFromButton, filterToButton, clearFilterButton;
    private DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<Dinner> dinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_dinner_history_activity);

        //Find the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.dinner_history_toolbar);
        setSupportActionBar(toolbar);

        //Open and close part navigation menu.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toDate = Calendar.getInstance();
        fromDate = Calendar.getInstance();

        findViewsByID();
        setOnClickListeners();
        updateDinnerList();
        populateViews();

    }

    /*
    Handle what happens when the back button is pressed. If the app-drawer is open - then close it
    otherwise go back a step in the program.
    */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_dinner_history) {
            //log food to diary
        } else if (id == R.id.nav_dinner_plan) {
            Toast.makeText(getApplicationContext(), "Coming soon.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_statistics) {
            Intent intent = new Intent(this, StatisticsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("message/rfc822" );
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mda15@student.aau.dk"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Picky Feedback" );
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else if (id == R.id.nav_preferences) {


        } else if (id == R.id.nav_settings) {
            Toast.makeText(getApplicationContext(), "Coming soon.", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void findViewsByID() {
        filterFromButton = (Button) findViewById(R.id.history_filter_from_button);
        filterToButton = (Button) findViewById(R.id.history_filter_to_button);
        clearFilterButton = (Button) findViewById(R.id.history_clear_button);
        listView = (ListView) findViewById(R.id.history_list_view);
    }

    private void setOnClickListeners() {
        Calendar calendar = Calendar.getInstance();

        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) { //TODO: Implement logic: DateSet cannot be after currentDate or fromDate.
                fromDate = Calendar.getInstance();
                fromDate.set(year, monthOfYear, dayOfMonth);
                filterFromButton.setText(new SimpleDateFormat("yyyy-MM-dd" ).format(fromDate.getTime()));
                updateDinnerList();
                adapter.notifyDataSetChanged();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) { //TODO: Implement logic: DateSet cannot be before fromDate and after currentDate.
                toDate = Calendar.getInstance();
                toDate.set(year, monthOfYear, dayOfMonth);
                filterToButton.setText(new SimpleDateFormat("yyyy-MM-dd" ).format(toDate.getTime())); //TODO: Change the SimpleDateFormat
                updateDinnerList();
                adapter.notifyDataSetChanged();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        filterFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFilterFromActive = true;
                fromDatePickerDialog.show();
            }
        });

        filterToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFilterToActive = true;
                toDatePickerDialog.show();
            }
        });

        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterToButton.setText(R.string.dinner_history_choose_date);
                isFilterToActive = false;
                filterFromButton.setText(R.string.dinner_history_choose_date);
                isFilterFromActive = false;
                fromDate.clear();
                toDate.clear();
                Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateViews() { //TODO: Make custom ArrayAdapter for ListView

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, updateDinnerList());
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //TODO: Implement edit dinner logic.
            }
        });

    }

    private List<String> updateDinnerList() {
        List<String> result = new ArrayList<>();
        DinnerRepository dr = new DinnerRepository(this.getApplicationContext());
        List<Dinner> list = dr.getAllDinners();
        for(Dinner d : list) {
                if(d.getDate().before(toDate.getTime())) {

                        if (d.getDate().after(fromDate.getTime())) {
                            result.add(d.getName());
                        }

                }
        }
        result.add("No more meals.");
        return result;
    }
}
