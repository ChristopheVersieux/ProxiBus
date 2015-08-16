package com.wazabe.bebus.utils;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.wazabe.bebus.bo.BusStop;

import java.util.List;

public class MySearchProvider extends ContentProvider {
    List<BusStop> stops = null;
    private StationDatabase mDB;

    @Override
    public boolean onCreate() {
        mDB = new StationDatabase(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String countQuery = "SELECT  _id," +
                StationDatabase.COL_STOP_NAME + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1 + ","+
                StationDatabase.COL_PARENT + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_2 +","+
                StationDatabase.COL_PARENT + "  AS " + SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA +","+
                StationDatabase.COL_PARENT + "  AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA +
                " FROM " + StationDatabase.TABLE_STATIONS
                + " WHERE " + StationDatabase.COL_STOP_NAME + " LIKE '%" + uri.getLastPathSegment().replace(" ","%") + "%' AND "+StationDatabase.COL_STOP_ID +" LIKE 'TEC:%'" +
                "GROUP BY "+ StationDatabase.COL_STOP_NAME;

        Cursor cursor = mDB.getReadableDatabase().rawQuery(countQuery, null);

        return cursor;
    }



    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        sqlDB.insert(StationDatabase.TABLE_STATIONS, "", values);

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}