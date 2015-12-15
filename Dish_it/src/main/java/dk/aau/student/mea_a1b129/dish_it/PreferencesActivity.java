package dk.aau.student.mea_a1b129.dish_it;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * @author Aleksander Kähler, Group B129, Aalborg University
 */
public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.preferences_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.preferences_fragment_container, new PreferencesFrag())
                .commit();
    }

    /**
     * Preferences Fragment to handle preferences.xml.
     */
    public static class PreferencesFrag extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public PreferencesFrag() {
            super();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_main);
            initializeSummary(getPreferenceScreen());
        }

        /**
         * Make sure that we unregister any listeners if the user goes to another activity.
         */
        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        /**
         * If we're resuming the activity then just re-register the PreferenceChangeListener.
         */
        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            initializeSummary(findPreference(key));
        }

        private void initializeSummary(Preference preference) {
            if(preference instanceof PreferenceGroup) {
                PreferenceGroup prefGroup = (PreferenceGroup) preference;
                for(int i = 0; i < prefGroup.getPreferenceCount(); i++) {
                    initializeSummary(prefGroup.getPreference(i));
                }
            }
            else if(preference instanceof EditTextPreference) {
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                preference.setSummary(editTextPreference.getText());
            }
        }
    }
}
