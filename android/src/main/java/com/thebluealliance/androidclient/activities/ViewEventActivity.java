package com.thebluealliance.androidclient.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.adapters.ViewEventFragmentPagerAdapter;
import com.thebluealliance.androidclient.datafeed.ConnectionDetector;

/**
 * <p>Activity that displays information about a FRC event</p>
 *
 * @author Phil Lopreiato
 * @author Nathan Walters
 *
 * @version 5/19/2014
 */
public class ViewEventActivity extends RefreshableHostActivity {

    private String mEventKey; // key that represents the event loaded in this activity
    private TextView warningMessage; // text view that represents the warning message to be displayed
    private ViewPager pager; // pager that represents the sliding tabs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        // Get the event key from the previous activity
        if (getIntent().getExtras() != null) {
            mEventKey = getIntent().getExtras().getString("eventKey", "");
        }

        // Load and then initially hide the warning message
        warningMessage = (TextView) findViewById(R.id.warning_container);
        hideWarningMessage();

        // Load pager sliding tabs and nav bar.
        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new ViewEventFragmentPagerAdapter(getSupportFragmentManager(), mEventKey));

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        setupActionBar();

        // Display warning message if device is not connected to the internet
        if (!ConnectionDetector.isConnectedToInternet(this)) {
            showWarningMessage(getString(R.string.warning_unable_to_load));
        }
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(false);
        encourageLearning(false);
    }

    /**
     * Loads action bar settings and title.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // The title is empty now; the EventInfoFragment will set the appropriate title
        // once it is loaded.
        setActionBarTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public ViewPager getPager(){
        return pager;
    }

    @Override
    public void showWarningMessage(String message) {
        warningMessage.setText(message);
        warningMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWarningMessage() {
        warningMessage.setVisibility(View.GONE);
    }
}
