package com.thebluealliance.androidclient.background.event;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ExpandableListView;

import com.thebluealliance.androidclient.Constants;
import com.thebluealliance.androidclient.R;
import com.thebluealliance.androidclient.activities.RefreshableHostActivity;
import com.thebluealliance.androidclient.adapters.MatchListAdapter;
import com.thebluealliance.androidclient.comparators.MatchSortByPlayOrderComparator;
import com.thebluealliance.androidclient.datafeed.DataManager;
import com.thebluealliance.androidclient.datatypes.APIResponse;
import com.thebluealliance.androidclient.datatypes.MatchGroup;
import com.thebluealliance.androidclient.models.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 *  Populates a ListView adapter with match results from an FRC event.
 *
 *  @author Phil Lopreiato
 *  @author Bryce Matsuda
 *  @author Nathan Walters
 *
 *  @version 5/21/2014
 */
public class PopulateEventResults extends AsyncTask<String, Void, APIResponse.CODE> {

    private Fragment mFragment;
    private RefreshableHostActivity activity;
    private String eventKey, teamKey;
    SparseArray<MatchGroup> groups; // holds all the matches in an event.

    public PopulateEventResults(Fragment f) {
        mFragment = f;
        activity = (RefreshableHostActivity) mFragment.getActivity();
    }

    @Override
    protected APIResponse.CODE doInBackground(String... params) {
        eventKey = params[0];
        if (params.length == 2) {
            teamKey = params[1];
        } else {
            teamKey = "";
        }
        // Initialize the groups of matches & the match sorter.
        groups = new SparseArray<>();
        MatchGroup qualMatches = new MatchGroup("Qualification Matches");
        MatchGroup quarterMatches = new MatchGroup("Quarterfinal Matches");
        MatchGroup semiMatches = new MatchGroup("Semifinal Matches");
        MatchGroup finalMatches = new MatchGroup("Finals Matches");
        MatchSortByPlayOrderComparator comparator = new MatchSortByPlayOrderComparator();
        APIResponse<HashMap<Match.TYPE, ArrayList<Match>>> response;
        try {
            // Get the results data from TBA.
            response = DataManager.getEventResults(activity, eventKey);
            HashMap<Match.TYPE, ArrayList<Match>> results = response.getData();
            // Sort out the results and add matches to their respective groups.
            Collections.sort(results.get(Match.TYPE.QUAL), comparator);
            for (Match m : results.get(Match.TYPE.QUAL)) {
                qualMatches.children.add(m);
                qualMatches.childrenKeys.add(m.getKey());
            }
            Collections.sort(results.get(Match.TYPE.QUARTER), comparator);
            for (Match m : results.get(Match.TYPE.QUARTER)) {
                quarterMatches.children.add(m);
                quarterMatches.childrenKeys.add(m.getKey());
            }
            Collections.sort(results.get(Match.TYPE.SEMI), comparator);
            for (Match m : results.get(Match.TYPE.SEMI)) {
                semiMatches.children.add(m);
                semiMatches.childrenKeys.add(m.getKey());
            }
            Collections.sort(results.get(Match.TYPE.FINAL), comparator);
            for (Match m : results.get(Match.TYPE.FINAL)) {
                finalMatches.children.add(m);
                finalMatches.childrenKeys.add(m.getKey());
            }
        } catch (DataManager.NoDataException e) {
            // Return null/an error if we cannot load the data
            Log.w(Constants.LOG_TAG, "unable to load event results");
            response = new APIResponse<>(null, APIResponse.CODE.NODATA);
        }

        // Add the match groups to the giant SparseArray, if they have matches in them.
        int numGroups = 0;
        if (qualMatches.children.size() > 0) {
            groups.append(numGroups, qualMatches);
            numGroups++;
        }
        if (quarterMatches.children.size() > 0) {
            groups.append(numGroups, quarterMatches);
            numGroups++;
        }
        if (semiMatches.children.size() > 0) {
            groups.append(numGroups, semiMatches);
            numGroups++;
        }
        if (finalMatches.children.size() > 0) {
            groups.append(numGroups, finalMatches);
        }

        return response.getCode();
    }

    protected void onPostExecute(APIResponse.CODE code) {
        View view = mFragment.getView();
        if (view != null && activity != null) {
        // Check view/activity isn't null before we initialize the ListView adapter
            MatchListAdapter adapter = new MatchListAdapter(activity, groups);
            ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.match_results);
            listView.setAdapter(adapter);

            if (code == APIResponse.CODE.OFFLINECACHE /* && event is current */) {
                //TODO only show warning for currently competing event (there's likely missing data)
                activity.showWarningMessage(activity.getString(R.string.warning_using_cached_data));
            }

            view.findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }
}
