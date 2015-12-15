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
 * TODO: Implement statistics activity
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

        GridView badgesGrid = (GridView) findViewById(R.id.statistics_badges_grid);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //badgesGrid.setNestedScrollingEnabled(true);
        }
        GameEngine ge = new GameEngine(context);
        BadgeGridAdapter adapter = new BadgeGridAdapter(context, ge.getAchievements());
        badgesGrid.setAdapter(adapter);
    }
}
