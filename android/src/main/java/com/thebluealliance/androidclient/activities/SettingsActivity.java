package com.thebluealliance.androidclient.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.thebluealliance.androidclient.BuildConfig;
import com.thebluealliance.androidclient.R;

/**
 *
 * <p>Displays the settings for the TBA Android app.</p>
 *
 * TODO (eventually): account support, cache size
 *
 * @author Adam Corpstein
 * @author Phil Lopreiato
 * @author Bryce Matsuda
 * @author tanis7x
 * @author Nathan Walters
 *
 * @version 5/15/2014
 *
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction() // Load the settings fragment
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load link/summary strings from res/preferences.xml
            addPreferencesFromResource(R.xml.preferences);

            // Set summary/intents using loaded strings
            Preference appVersion = findPreference("app_version");
            appVersion.setSummary(BuildConfig.VERSION_NAME);

            Preference githubLink = findPreference("github_link");
            githubLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/the-blue-alliance/the-blue-alliance-android/")));

            Preference licenses = findPreference("licenses");
            licenses.setIntent(new Intent(getActivity(), OpenSourceLicensesActivity.class));

            Preference tbaLink = findPreference("tba_link");
            tbaLink.setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thebluealliance.com")));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
