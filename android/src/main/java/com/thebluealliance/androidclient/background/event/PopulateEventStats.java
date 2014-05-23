package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.ListViewAdapter;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.ListItem;
import com.thebluealliance.androidclient.datatypes.StatsListElement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 *  Populates a ListView adapter with stats from an FRC event.
 *
 *  @author Phil Lopreiato
 *  @author Bryce Matsuda
 *  @author Nathan Walters
 *
 *  @version 5/21/2014
 */
public class PopulateEventStats extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey;
    private ArrayList<ListItem> teams;

    public PopulateEventStats(Fragment f) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];

        teams = new ArrayList<>();

        DecimalFormat displayFormat = new DecimalFormat("#.##");

        try {
            // Download data from TBA with the given event key and sort it by stat type.
            APIResponse<JsonObject> response = DataManager.getEventStats(activity, eventKey);
            JsonObject stats = response.getData();
            ArrayList<Map.Entry<String, JsonElement>>
                    opr = new ArrayList<>(),
                    dpr = new ArrayList<>(),
                    ccwm = new ArrayList<>();
            if(stats.has("oprs")) {
                opr.addAll(stats.get("oprs").getAsJsonObject().entrySet());
            }
            if(stats.has("dprs")) {
                dpr.addAll(stats.get("dprs").getAsJsonObject().entrySet());
            }
            if(stats.has("ccwms")) {
                ccwm.addAll(stats.get("ccwms").getAsJsonObject().entrySet());
            }

            for (int i = 0; i < opr.size(); i++) {
                String statsString = "OPR: " + displayFormat.format(opr.get(i).getValue().getAsDouble())
                        + ", DPR: " + displayFormat.format(dpr.get(i).getValue().getAsDouble())
                        + ", CCWM: " + displayFormat.format(ccwm.get(i).getValue().getAsDouble());
                String teamKey = "frc" + opr.get(i).getKey();
                teams.add(new StatsListElement(teamKey, Integer.parseInt(opr.get(i).getKey()), "", "", statsString));
                //TODO the blank fields above are team name and location
            }
            return response.getCode();
        } catch (DataManager.NoDataException e) {
            Log.w(Constants.LOG_TAG, "unable to load event stats");
            return APIResponse.CODE.NODATA;
        }
    }

    @Override
    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && activity != null) {
            ListViewAdapter adapter = new ListViewAdapter(activity, teams);
            ListView stats = (ListView) view.findViewById(R.id.list);
            stats.setAdapter(adapter);

            if (code == APIResponse.CODE.OFFLINECACHE /* && event is current */) {
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
}
