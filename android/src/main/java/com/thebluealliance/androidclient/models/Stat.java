package com.thebluealliance.androidclient.models;

import android.content.ContentValues;

import com.thebluealliance.androidclient.datatypes.StatsListElement;

import java.text.DecimalFormat;

/**
 * File created by phil on 6/3/14.
 */
public class Stat implements BasicModel{

    String teamKey, teamName, location, statString;

    public static DecimalFormat displayFormat = new DecimalFormat("#.##");

    public Stat(String teamKey, String teamName, String location, String statString){
        super();
        this.teamKey = teamKey;
        this.teamName = teamName;
        this.location = location;
        this.statString = statString;
    }

    @Override
    public StatsListElement render() {
        int teamNumber = Integer.parseInt(teamKey.substring(3));
        return new StatsListElement(teamKey, teamNumber, teamName, location, statString);
    }

    @Override
    public ContentValues getParams() {
        return null;
    }
}
