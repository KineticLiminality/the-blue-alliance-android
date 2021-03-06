package com.thebluealliance.androidclient.listeners;

import android.content.Context;
import android.view.View;

import com.thebluealliance.androidclient.activities.TeamAtEventActivity;
import com.thebluealliance.androidclient.activities.ViewTeamActivity;
import com.thebluealliance.androidclient.helpers.EventHelper;
import com.thebluealliance.androidclient.helpers.TeamHelper;

/**
 * Created by phil on 7/8/14.
 */
public class TeamAtEventClickListener implements View.OnClickListener {

    private Context c;

    public TeamAtEventClickListener(Context c) {
        super();
        this.c = c;
    }

    @Override
    public void onClick(View v) {
        String tag = v.getTag().toString();
        String teamKey, eventKey;
        if(tag.contains("@")) {
            teamKey = tag.split("@")[0];
            eventKey =tag.split("@")[1];
        }else{
            teamKey = tag;
            eventKey = "";
        }
        if (TeamHelper.validateTeamKey(teamKey) ^ TeamHelper.validateMultiTeamKey(teamKey)) {
            if (TeamHelper.validateMultiTeamKey(teamKey)) {
                // Take out extra letter at end to make team key valid.
                teamKey = teamKey.substring(0, teamKey.length() - 1);
            }
            if(EventHelper.validateEventKey(eventKey)) {
                c.startActivity(TeamAtEventActivity.newInstance(c, eventKey, teamKey));
            }else{
                //if we don't pass a valid event key, just open up the team activity
                c.startActivity(ViewTeamActivity.newInstance(c, teamKey));
            }
        }
    }
}
