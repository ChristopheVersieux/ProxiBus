package com.wazabe.bebus.utils;

import android.content.Context;
import android.location.Location;

import com.wazabe.bebus.bo.BusStop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by versieuxchristophe on 07/03/15.
 */
public class UtilsTest {
    public static List<BusStop> readApiStops(Context c, Location mLastLocation) {
        BufferedReader reader = null;
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
        return list;
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
