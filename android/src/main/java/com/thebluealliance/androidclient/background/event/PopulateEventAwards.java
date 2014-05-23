package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.AwardListElement;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.models.Award;

import java.util.ArrayList;

/**
 *
 * <p>Populates an ListView adapter with the awards given at an FRC event.</p>
 *
 * @author Phil Lopreiato
 * @author Nathan Walters
 *
 * @version 5/19/2014
 */
public class PopulateEventAwards extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey;
    private ArrayList<ListItem> awards;
    private ListViewAdapter adapter;

    public PopulateEventAwards(Fragment f) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        awards = new ArrayList<>();

        APIResponse<ArrayList<Award>> response;
        try {
            // Download the event awards and store them in an array list of awards.
            response = DataManager.getEventAwards(activity, eventKey);
            ArrayList<Award> awardList = response.getData();
            for (Award a : awardList) {
                ArrayList<AwardListElement> allWinners = a.renderAll();
                awards.addAll(allWinners);
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            // Return an error if data cannot be loaded.
            Log.w(Constants.LOG_TAG, "unable to load event awards");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null) { // Make sure the view isn't null before we set up the adapter
            adapter = new ListViewAdapter(activity, awards);
            ListView rankings = (ListView) view.findViewById(R.id.list);
            rankings.setAdapter(adapter);

            // Display a warning if device is offline.
            if (code == APIResponse.CODE.OFFLINECACHE /* && event is current */) {
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }
            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
}
