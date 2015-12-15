package dk.aau.student.mea_a1b129.dish_it;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
 * @author Aleksander Kähler, Group B129, Aalborg University
 *         DinnerHistory activity to show the latest food logs to user.
 */
public class DinnerHistory extends AppCompatActivity {

    private static final String TAG = DinnerHistory.class.getSimpleName();
    private Calendar fromDate, toDate;
    private Button filterFromButton, filterToButton, clearFilterButton;
    private DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    private ListView listView;
    private DinnerListAdapter adapter;
    private DinnerRepository dr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner_history);

        //Find the toolbar and set back button.
        Toolbar toolbar = (Toolbar) findViewById(R.id.dinner_history_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
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
        populateListView();
    }

    /**
     * When returning to app, refresh Dinner list and repopulate views with new content.
     */
    @Override
    protected void onStart() {
        super.onStart();
        updateAdapter(updateDinnerList());
    }

    /*
    Find the views in the Layout XML
     */
    private void findViewsByID() {
        filterFromButton = (Button) findViewById(R.id.history_filter_from_button);
        filterToButton = (Button) findViewById(R.id.history_filter_to_button);
        clearFilterButton = (Button) findViewById(R.id.history_clear_button);
        listView = (ListView) findViewById(R.id.history_list_view);
    }

    /*
    Set on click listeners to buttons.
     */
    private void setOnClickListeners() {
        final Calendar calendar = Calendar.getInstance();

        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) { //TODO Optimize DatePickerDialogs - they slow down onCreate() process.
                fromDate = Calendar.getInstance();
                fromDate.set(year, monthOfYear, dayOfMonth);
                if (toDate != null) { //If statement to test if from-filter date is after to-filter date.
                    if (fromDate.after(toDate)) {
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
                if (fromDate != null) {
                    if (toDate.before(fromDate)) {
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

    /*
    Here we populate the main list view in the activity with meals from DinnerRepository. We use the DinnerListAdapter for this.
     */
    private void populateListView() {
        adapter = new DinnerListAdapter(this.getApplicationContext(), updateDinnerList());
        listView.setAdapter(adapter);

        //Be aware: Convoluted code ahead. Here we set a listener for long presses.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Save the selected item.
                final int pos = position;
                view.setSelected(true);

                //Build the buttons in the dialog, which we'll show in a second.
                DialogInterface.OnClickListener alert = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Test to find out which button was pressed
                        switch (which) {
                            //If it was Cancel or No
                            case DialogInterface.BUTTON_NEGATIVE: {
                                //Then we dismiss the pop-up and don't do anything
                                dialog.dismiss();
                                break;
                            }
                            //If it was Yes
                            case DialogInterface.BUTTON_POSITIVE: {
                                //Then we try to delete the selected dinner. We store the result of the deletion attempt in a boolean
                                boolean isDeleted = dr.deleteDinner(((Dinner) adapter.getItem(pos)).getDinnerID());
                                Log.i(TAG, "Dinner deleted: " + isDeleted);
                                //If the dinner was deleted properly
                                if (isDeleted) {
                                    new GameEngine(getApplicationContext()).trackProgression(GameEngine.ProgressionType.DINNERS_DELETED, 1);
                                    //We notify the DinnerListAdapter that data has changed and needs updating.
                                    updateAdapter(updateDinnerList());
                                }
                            }
                        }
                    }
                };
                //And now.. we build...
                AlertDialog.Builder builder = new AlertDialog.Builder(DinnerHistory.this);
                //Yea - why make variables, when everything can be written in 1 line of code..
                builder.setTitle(R.string.dinner_history_delete_dinner_title)
                        .setMessage(getResources().getString(R.string.dinner_history_delete_dinner_message_pt1)
                                + " " + ((Dinner) adapter.getItem(pos)).getName()
                                + " " + getResources().getString(R.string.dinner_history_delete_dinner_message_pt2))
                        .setPositiveButton(R.string.dinner_history_delete_dinner_OK, alert)
                        .setNegativeButton(R.string.dinner_history_delete_dinner_cancel, alert)
                        .show();

                //Important fucker.
                return true;
            }
        });
        //Listener for what happens when we press normally on an item.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //We start a new intent and put the DinnerID into an Extra for the AddDinnerActivity to edit.
                Intent i = new Intent(getApplicationContext(), AddDinnerActivity.class);
                i.putExtra("dinnerID", ((Dinner) adapter.getItem(position)).getDinnerID());
                startActivity(i);
            }
        });
    }

    /**
     * Update and return a list of the latest dinners.
     * @return updated list of Dinners.
     */
    private List<Dinner> updateDinnerList() {
        //Make a list to save our results
        List<Dinner> result = new ArrayList<>();
        dr = new DinnerRepository(this.getApplicationContext());
        List<Dinner> list = dr.getDinnerList();
        try {
            //Here we sort the list by using the Comparable interface.
            Collections.sort(list);
        } catch (NullPointerException e) {
            Log.e("DinnerHistory", "No date in Dinner object to compare to" + e.getMessage());
            e.printStackTrace();
        }

        //Only show Dinner between to and from dates chosen.
        //For dinner d in our list
        for (Dinner d : list) {
            if (toDate == null) {
                if (fromDate == null) {
                    result.add(d);
                } else if (d.getDate().after(fromDate.getTime())) {
                    result.add(d);
                }
            } else if (d.getDate().before(toDate.getTime())) {
                if (fromDate == null) {
                    result.add(d);
                } else if (d.getDate().after(fromDate.getTime())) {
                    result.add(d);
                }
            }
        }
        return result;
    }

    //Little helper method to update our DinnerListAdapter class.
    private void updateAdapter(List<Dinner> newData) {
        adapter.updateDinnerList(newData);
        adapter.notifyDataSetChanged();
    }
}
