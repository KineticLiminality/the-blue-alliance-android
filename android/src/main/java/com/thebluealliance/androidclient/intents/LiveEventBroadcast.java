package com.thebluealliance.androidclient.intents;

import android.content.Intent;

import com.thebluealliance.androidclient.listitems.EventListElement;
import com.thebluealliance.androidclient.listitems.MatchListElement;

/**
 * File created by phil on 7/18/14.
 */
public class LiveEventBroadcast extends Intent {

    public static final String ACTION = "com.thebluealliance.androidclient.LIVE_MATCH",
    NEXT_MATCH = "next",
    LAST_MATCH = "last",
    EVENT = "event";

    private LiveEventBroadcast(){
        super();
        setAction(ACTION);
    }

    public LiveEventBroadcast(MatchListElement nextMatch, MatchListElement lastMatch) {
        this();
        if(nextMatch != null) {
            putExtra(NEXT_MATCH, nextMatch);
        }
        if(lastMatch != null) {
            putExtra(LAST_MATCH, lastMatch);
        }
    }

    public LiveEventBroadcast(EventListElement event){
        this();
        if(event != null){
            putExtra(EVENT, event);
        }
    }
}
