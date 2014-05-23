package com.thebluealliance.androidclient.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.background.match.PopulateMatchInfo;

/**
 * <p>Activity that displays information about an FRC match</p>
 *
 * @author Nathan Walters
 */
public class ViewMatchActivity extends RefreshableHostActivity {

    public static final String MATCH_KEY = "match_key";

    private static final String VIDEO_FRAGMENT_TAG = "videoFragment";

    private String mMatchKey; // key representing a unique FRC match

    /**
     * Creates a new match activity.
     * @param context activity currently running
     * @param matchKey key representing an FRC match
     * @return newly created match activity
     */
    public static Intent newInstance(Context context, String matchKey) {
        Intent intent = new Intent(context, ViewMatchActivity.class);
        intent.putExtra(MATCH_KEY, matchKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_match);
        setupActionBar();

        mMatchKey = getIntent().getStringExtra(MATCH_KEY); // get match key from previous activity
        if (mMatchKey == null) { // null check
            throw new IllegalArgumentException("ViewMatchActivity must be created with a match key!");
        }

        new PopulateMatchInfo(this).execute(mMatchKey); // get match info using the match key
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
    }

    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarTitle("Match");
    }

    @Override
    public void showWarningMessage(String message) {

    }

    @Override
    public void hideWarningMessage() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
