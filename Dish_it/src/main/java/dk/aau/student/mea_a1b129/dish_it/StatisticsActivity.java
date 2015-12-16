package dk.aau.student.mea_a1b129.dish_it;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 * Statistic Activity to show various statistics about the user, like badget, logged meals, etc.
 */
public class StatisticsActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.statistics_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Find our grid from the layout and save it in a variable.
        GridView badgesGrid = (GridView) findViewById(R.id.statistics_badges_grid);
        GameEngine ge = new GameEngine(context);
        //Set the adapter of the grid.
        BadgeGridAdapter adapter = new BadgeGridAdapter(context, ge.getAchievements());
        badgesGrid.setAdapter(adapter);
    }
}
