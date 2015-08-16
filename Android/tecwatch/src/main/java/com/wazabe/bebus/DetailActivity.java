package com.wazabe.bebus;


import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wazabe.bebus.bo.Trip;
import com.wazabe.bebus.bo.Trips;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class DetailActivity extends Activity {

    private TextView mTextView;
    private TextView remaining;

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mTextView = (TextView) findViewById(R.id.title);
        remaining = (TextView) findViewById(R.id.remaining);
        String id = getIntent().getStringExtra("KEY");
        String color = getIntent().getStringExtra("COLOR");
        mTextView.setBackgroundColor(Color.parseColor("#" +color));
        remaining.setBackgroundColor(Color.parseColor("#" +color));

        mTextView.setText("DETAIL:" + id);
        AssetManager am = getAssets();
        final String departure_time = randInt(13,20)+":"+randInt(10,59)+":00";

        try {
            //InputStream is = am.open(id + ".json");
            //Reader reader = new InputStreamReader(is, "UTF-8");
            //final Trips data = new Gson().fromJson(reader, Trips.class);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            remaining.setText("#" + System.currentTimeMillis());

                            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
                            try {

                                Date date1 = sdf1.parse(departure_time);
                                Date date2 = new Date();

                                long durationInMillis = date2.getTime() - date1.getTime();

                                int seconds = 60 - ((int) (durationInMillis / 1000) % 60);
                                int minutes = 60 - ((int) ((durationInMillis / (1000 * 60)) % 60));
                                int hours = 22 - ((int) ((durationInMillis / (1000 * 60 * 60)) % 24));
                                remaining.setText(String.format("%02d:%02d:%02d%n", hours, minutes, seconds));

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, 0, 1000);

            mTextView.setText(departure_time);

            /*LinearLayout lt = (LinearLayout) findViewById( R.id.linearlist );
            for (Trip aTrip : data.trips) {
                TextView tv = (TextView) getLayoutInflater().inflate(R.layout.row_bus, null);
                tv.setText(aTrip.trip.trip_headsign);
                lt.addView(tv);
            }*/


        } catch (Exception e) {
            mTextView.setText("HOOPS:");
            e.printStackTrace();
        }

    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}

