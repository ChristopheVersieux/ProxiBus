package com.wazabe.bebus.utils;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import com.wazabe.bebus.bo.BusStop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by versieuxchristophe on 07/03/15.
 */
public class Utils {
    public static List<BusStop> readApiStops(Context c, Location mLastLocation) {
        String response = PreferenceManager.getDefaultSharedPreferences(c).getString("cacheSearchNear", "");
        List<BusStop> list = new ArrayList<>();
        try {
            //Log.e("CVE", response);
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                //Toast.makeText(OverviewFragment.this.getActivity(), "" + array.length(), Toast.LENGTH_LONG).show();
                try {
                    JSONObject childJSONObject = array.getJSONObject(i);
                    list.add(new BusStop(childJSONObject.getString("stop_name"), childJSONObject.getString("stop_id"), childJSONObject.getLong("stop_lat"), childJSONObject.getLong("stop_lon"), childJSONObject.getLong("distance")));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    //Toast.makeText(OverviewFragment.this.getActivity(), e1.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        } finally {
           return list;
        }
       /* BufferedReader reader = null;
        List<BusStop> list = new ArrayList<>();
        Location loc = new Location("");
        try {
            reader = new BufferedReader(
                    new InputStreamReader(c.getAssets().open("stops.txt")));
            //Skip first line
            reader.readLine();
            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();

            while (mLine != null) {
                String[] array = mLine.split(",");
                try {
                    loc.setLatitude(Float.valueOf(array[3]));
                    loc.setLongitude(Float.valueOf(array[4]));
                    if (mLastLocation == null)
                        list.add(new BusStop(array[2], array[0], Float.valueOf(array[3]), Float.valueOf(array[4]), 0));
                    else
                        list.add(new BusStop(array[2], array[0], Float.valueOf(array[3]), Float.valueOf(array[4]), mLastLocation.distanceTo(loc)));
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }
                mLine = reader.readLine();
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        Collections.sort(list);
        return list;*/
    }

    public static int getStatusBarHeight(Context c) {
        int result = 0;
        int resourceId = c.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = c.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}
