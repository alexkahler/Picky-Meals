package dk.aau.student.mea_a1b129.picky;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * DinnerHistory activity to show the latest food logs to user.
 * TODO: Compensate for toolbar in layout.
 * TODO: Implement filter logic to ListView.
 */
public class DinnerHistory extends AppCompatActivity {

    private Calendar fromDate, toDate;
    private Button filterFromButton, filterToButton, clearFilterButton;
    private DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<Dinner> dinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner_history);

        //Find the toolbar and set back button.
        Toolbar toolbar = (Toolbar) findViewById(R.id.dinner_history_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Floating action button.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.dinner_history_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddDinnerActivity.class);
                startActivity(i);
            }
        });

        findViewsByID();
        setOnClickListeners();
        updateDinnerList();
        populateViews();
    }

    /**
     * When returning to app, refresh Dinner list and repopulate views with new content.
     */
    @Override
    protected void onStart() {
        super.onStart();
        updateAdapter(updateDinnerList());
    }

    private void findViewsByID() {
        filterFromButton = (Button) findViewById(R.id.history_filter_from_button);
        filterToButton = (Button) findViewById(R.id.history_filter_to_button);
        clearFilterButton = (Button) findViewById(R.id.history_clear_button);
        listView = (ListView) findViewById(R.id.history_list_view);
    }

    private void setOnClickListeners() {
        final Calendar calendar = Calendar.getInstance();

        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                fromDate = Calendar.getInstance();
                fromDate.set(year, monthOfYear, dayOfMonth);
                if(toDate != null) { //If statement to test if from-filter date is after to-filter date.
                    Log.d("DinnerHistory", "In if-statement onDateSet, toDate != null");
                    if(fromDate.after(toDate)) {
                        Log.d("DinnerHistory", "In if-statement onDateSet: Making fromDate toast");
                        Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_filterFrom_after_filterTo), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                filterFromButton.setText(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(fromDate.getTime()));
                updateAdapter(updateDinnerList());
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                toDate = Calendar.getInstance();
                toDate.set(year, monthOfYear, dayOfMonth);
                if(fromDate != null) {
                    Log.d("DinnerHistory", "In if-statement onDateSet: fromDate != null");
                    if (toDate.before(fromDate)) {
                        Log.d("DinnerHistory", "In if-statement onDateSet: Making toDate toast");
                        Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_filterTo_before_filterFrom), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                filterToButton.setText(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK")).format(toDate.getTime()));
                updateAdapter(updateDinnerList());
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        toDatePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());

        filterFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        filterToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        });

        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterToButton.setText(R.string.dinner_history_choose_date);
                filterFromButton.setText(R.string.dinner_history_choose_date);
                fromDate = null;
                toDate = null;
                updateAdapter(updateDinnerList());
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
        List<Dinner> list = dr.getDinnerList();
        try {
            Collections.sort(list);
        }
        catch(ClassCastException e) {
            Log.e("DinnerHistory", "Tried to Class Cast a wrong object");
        } catch(NullPointerException e) {
            Log.e("DinnerHistory", "No date in Dinner object to compare to");
        }
        for(Dinner d : list) {
            if(toDate == null) {
                Log.d("DinnerHistory", "In if toDate==null");
                if(fromDate == null) {
                    Log.d("DinnerHistory", "In if fromDate==null adding " +d.getName());
                    result.add(d.getName());
                }
                else if (d.getDate().after(fromDate.getTime())) {
                    Log.d("DinnerHistory", "In else if after(fromDate.getTime), adding " + d.getName());
                    result.add(d.getName());
                }
            }
            else if (d.getDate().before(toDate.getTime())) {
                Log.d("DinnerHistory", "In else-if before(toDate.getTime)");
                if(fromDate == null) {
                    Log.d("DinnerHistory", "In if fromDate==null " + d.getName());
                    result.add(d.getName());
                }
                else if (d.getDate().after(fromDate.getTime())) {
                    Log.d("DinnerHistory", "In else-if after(fromDate.getTime), adding " + d.getName());
                    result.add(d.getName());
                }
            }
        }
        return result;
    }

    private void updateAdapter(List<String> newData) {
        adapter.clear();
        if(newData != null) {
            for(String s : newData) {
                adapter.insert(s, adapter.getCount());
            }
        }
        adapter.notifyDataSetChanged();

    }
}
