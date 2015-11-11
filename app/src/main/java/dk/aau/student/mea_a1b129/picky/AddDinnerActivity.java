package dk.aau.student.mea_a1b129.picky;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddDinnerActivity extends AppCompatActivity {

    private EditText dinnerName, dinnerCategory, dinnerDescription;
    private Calendar dinnerDate;
    private TextView dateText;
    private RatingBar dinnerRating;
    private Button chooseDate, saveButton, cancelButton;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dinner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_dinner_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewsByID();
        assignListeners();
    }

    private void findViewsByID() {
        dinnerName = (EditText) findViewById(R.id.add_dinner_edit_dinner_name);
        dinnerCategory = (EditText) findViewById(R.id.add_dinner_edit_category);
        dinnerDescription = (EditText) findViewById(R.id.add_dinner_edit_dinner_description);
        dinnerRating = (RatingBar) findViewById(R.id.add_dinner_edit_rating);
        dateText = (TextView) findViewById(R.id.add_dinner_date_chosen);
        dinnerDate = Calendar.getInstance();
        dateText.setText(new SimpleDateFormat("dd-MM-yyyy", new Locale("da", "DK"))
                .format(dinnerDate.getTime()));
        chooseDate = (Button) findViewById(R.id.add_dinner_choose_date_button);
        saveButton = (Button) findViewById(R.id.add_dinner_save_button);
        cancelButton = (Button) findViewById(R.id.add_dinner_cancel_button);
    }

    private void assignListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DinnerRepository dr = new DinnerRepository(getApplicationContext());
                if(dinnerName.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please input a name for the dinner", Toast.LENGTH_SHORT).show();
                } else {
                    dr.insertDinner(
                            dinnerName.getText().toString(),
                            dinnerDescription.getText().toString(),
                            dinnerCategory.getText().toString(), new ArrayList<Integer>(Arrays.asList(1, 2, 3)),
                            Math.round(dinnerRating.getRating()),
                            dinnerDate.getTime()
                    );
                    Toast.makeText(getApplicationContext(), "Dinner saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
}
