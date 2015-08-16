package com.wazabe.bebus.utils;

import android.content.Context;

import com.readystatesoftware.sqliteasset.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 201601 on 15-Jun-15.
 */
public class StationDatabase extends SQLiteAssetHelper {
    private static final String DEBUG_TAG = "StationDatabase";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "bebus";

    public static final String TABLE_STATIONS = "stops";
    public static final String ID = "_id";
    public static final String COL_STATION_ID = "stop_id";
    public static final String COL_CODE = "stop_code";
    public static final String COL_NAME = "stop_name";
    public static final String COL_LAT = "stop_lat";
    public static final String COL_LNG = "stop_lng";

    public static final String COL_STOP_NAME= "stop_name";
    public static final String COL_PARENT= "parent_station";
    public static final String COL_STOP_ID= "_id";

    Context c;

    public StationDatabase(Context context) {
        super(context, DB_NAME + ".db", null, DB_VERSION);
        this.c = context;
    }

}