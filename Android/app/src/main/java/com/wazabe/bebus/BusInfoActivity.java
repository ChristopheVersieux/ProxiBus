package com.wazabe.bebus;

import android.animation.TimeInterpolator;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.wazabe.bebus.adapter.PopupRouteAdapter;
import com.wazabe.bebus.adapter.TripsAdapter;
import com.wazabe.bebus.bo.ATrip;
import com.wazabe.bebus.bo.Bus;
import com.wazabe.bebus.bo.DepartureIrail;
import com.wazabe.bebus.bo.Notif;
import com.wazabe.bebus.bo.PopupRoute;
import com.wazabe.bebus.bo.Trip;
import com.wazabe.bebus.bo.Trips;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by versieuxchristophe on 07/03/15.
 */
public class BusInfoActivity extends AppCompatActivity {
    String query;
    String route_color = "";
    String route_text_color = "";
    String route_short_name = "";
    String trip_headsign = "";
    String direction = "";
    String stop_id = "";
    File myFile;
    boolean fromCache;
    MenuItem starMenu;
    Bus bus;
    int currentIndex = 0;
    String nextTrip;
    String nextTrip2;

    boolean irail;

    private static final TimeInterpolator GAUGE_ANIMATION_INTERPOLATOR = new DecelerateInterpolator(2);
    private static final int MAX_LEVEL = 10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            irail=true;
            query = getIntent().getStringExtra(SearchManager.EXTRA_DATA_KEY);
            bus = new Bus(query);
            if (query == null)
                Toast.makeText(this, "NO DATA", Toast.LENGTH_LONG).show();
            else {
                query = getIntent().getDataString();//getStringExtra(SearchManager.QUERY);
               // Toast.makeText(this, "DATA OK " + query, Toast.LENGTH_LONG).show();
            }

        } else {
            query = getIntent().getExtras().getString("route_id");
            route_short_name = getIntent().getExtras().getString("route_short_name");
            route_color = getIntent().getExtras().getString("route_color");
            route_text_color = getIntent().getExtras().getString("route_text_color");
            direction = getIntent().getExtras().getString("direction");
            trip_headsign = getIntent().getExtras().getString("trip_headsign");
            stop_id = getIntent().getExtras().getString("stop_id");

            int routeColor = Color.parseColor("#" + route_color);
            int textColor = Color.parseColor("#" + route_text_color);

            findViewById(R.id.toolbar).setBackgroundColor(routeColor);
            findViewById(R.id.countdown).setBackgroundColor(routeColor);
            ((TextView) findViewById(R.id.countdown)).setTextColor(textColor);

            //Make status darker
            float[] hsv = new float[3];
            Color.colorToHSV(routeColor, hsv);
            hsv[2] *= 0.6f;
            routeColor = Color.HSVToColor(hsv);
            try {
                getWindow().setStatusBarColor(routeColor);
            } catch (Error e) {// No lollippop
                e.printStackTrace();
            }

            ((Toolbar) findViewById(R.id.toolbar)).setTitleTextColor(Color.parseColor("#" + route_text_color));
            setTitle(route_short_name + " - " + trip_headsign);

        }

        if (query == null)
            ;// finish();
        else {
            myFile = new File(getDir("cache", Context.MODE_PRIVATE), (query + ";" + route_short_name + ";" + route_color + ";" + route_text_color + ";" + direction + ";" + trip_headsign + ";" + stop_id).replace("/", "-slash-"));
            myFile.getParentFile().mkdirs();
            fromCache = myFile.exists();
        }
        if (direction.length() > 0) {
            doMySearch(query, 0);
            findViewById(R.id.recyclerview2).setVisibility(View.GONE);
            findViewById(R.id.countdown2).setVisibility(View.GONE);
        } else {
            setContentView(R.layout.activity_encours);
            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(R.string.encourstitle);
            /*stop_id=query;
            Log.e("CVE", "http://" + OverviewFragment.SRV_IRAIL + "/stops/" + stop_id + ".json");

            final String cache = PreferenceManager.getDefaultSharedPreferences(this).getString(stop_id, "");

            if (cache.length() == 0)
                Ion.with(this).load("http://" + OverviewFragment.SRV_IRAIL + "/stops/" + stop_id + ".json").setTimeout(OverviewFragment.TIMEOUT).asString()
                        .setCallback(new FutureCallback<String>() {
                                         @Override
                                         public void onCompleted(Exception e, String response) {
                                             if (response != null && response.length() > 1) {
                                                 PreferenceManager.getDefaultSharedPreferences(BusInfoActivity.this).edit().putString(stop_id, response).apply();
                                             } else {
                                                 response = cache;
                                             }
                                             setupStop(response);
                                         }
                                     }
                        );
            else
                setupStop(cache);*/

            /*direction ="0";
            doMySearch(query,0);
            direction ="1";
            doMySearch(query,1);*/
        }


    }

    private void setupStop(String response) {
        ArrayList<PopupRoute> routes = new ArrayList<>();
        String stop_id = "";
        Log.e("CVE",response);
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject childJSONObject = array.getJSONObject(i);
                    JSONObject stopObject = childJSONObject.getJSONObject("stop");

                    stop_id = stopObject.getString("stop_id");
                    //Log.e("CVE","STOP= "+response);
                    JSONArray childJSONArray = stopObject.getJSONArray("routes");
                    for (int j = 0; j < childJSONArray.length(); j++) {
                        try {
                            JSONObject aJSONObject = childJSONArray.getJSONObject(j);
                            JSONArray directionJSONArray = aJSONObject.getJSONArray("directions");

                            for (int k = 0; k < directionJSONArray.length(); k++) {
                                JSONObject directionJSONObject = directionJSONArray.getJSONObject(k);
                                routes.add(new PopupRoute("Short","ID", "route_long_nameA", "route_color","route_text_color", directionJSONObject.getString("direction"),directionJSONObject.getString("trip_headsign"),stop_id));
                            }
                            //Log.e("CVE", "***  " +aJSONObject.getString("route_short_name") );
                            boolean found = false;
                            /*for (MyRoute aRoute : myRoutes) {
                                if (aRoute.routes.get(0).route_short_name.contentEquals(aJSONObject.getString("route_short_name"))) {
                                    aRoute.routes.add(new Route(aJSONObject.getString("route_short_name"), aJSONObject.getString("route_id"), aJSONObject.getString("route_long_name"), aJSONObject.getString("route_color"), aJSONObject.getString("route_text_color"), directions, stop_id));
                                    found = true;
                                }
                            }
                            if (!found)
                                myRoutes.add(new MyRoute(new Route(aJSONObject.getString("route_short_name"), aJSONObject.getString("route_id"), aJSONObject.getString("route_long_name"), aJSONObject.getString("route_color"), aJSONObject.getString("route_text_color"), directions, stop_id)));
                    */
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    //Toast.makeText(OverviewFragment.this.getActivity(), e1.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        } catch (JSONException e1) {
            //Just in case.. If there is an error, I remove the cache that is likely corrupted.
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(stop_id, "").apply();
            e1.printStackTrace();
        } finally {
            if(routes!=null && routes.size()>0)
            new AlertDialog.Builder(this)
                    .setTitle(R.string.chose_bus)
                    .setAdapter(new PopupRouteAdapter(this,routes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    //TODO OK
                                }
                            }
                    ).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
            else
            {
               //TODO: better user feedback
              // finish();
            }
        }
    }

    private void doMySearch(final String query, final int where) {
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(BusInfoActivity.this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView mRecyclerView = (RecyclerView) (BusInfoActivity.this.findViewById(where == 0 ? R.id.recyclerview : R.id.recyclerview2));
        mRecyclerView.setLayoutManager(layoutManager);
        if (!irail && !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("irail", false)) {
            String url = "http://" + OverviewFragment.SRV_NEXTRIDE + "/trips/route/" + query + "/stop/" + stop_id + "/date/" + formatDate(new Date()) + "/direction/" + direction + ".json";
            Log.e("CVE", "FUUUUUUUU");
            Log.e("CVE", url);
            Ion.with(this).load(url).setTimeout(OverviewFragment.TIMEOUT).addHeader("Authorization", "basic " + Base64.encodeToString("partner-christophe:34n4E59wKf".getBytes(), Base64.DEFAULT))
                    .as(new TypeToken<Trips>() {
                    })
                    .setCallback(new FutureCallback<Trips>() {
                                     @Override
                                     public void onCompleted(Exception ex, final Trips response) {
                                         try {

                                             mRecyclerView.setAdapter(new TripsAdapter(response.trips,
                                                     route_short_name,
                                                     query,
                                                     direction,
                                                     route_color,
                                                     route_text_color));

                                             nextTrip = getNextTrip(response.trips);
                                             setDisplayTime(where);


                                             final TextSwitcher mSwitcher = (TextSwitcher) findViewById(R.id.textswitcher);

                                             mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

                                                 public View makeView() {
                                                     TextView myText = new TextView(BusInfoActivity.this);
                                                     myText.setGravity(Gravity.CENTER);
                                                     myText.setLines(3);
                                                     return myText;
                                                 }
                                             });

                                             Animation in = AnimationUtils.loadAnimation(BusInfoActivity.this, android.R.anim.slide_in_left);
                                             Animation out = AnimationUtils.loadAnimation(BusInfoActivity.this, android.R.anim.slide_out_right);

                                             mSwitcher.setInAnimation(in);
                                             mSwitcher.setOutAnimation(out);


                                             if (response.notifs.size() > 0) {
                                                 Notif notif = response.notifs.get(0);
                                                 mSwitcher.setText(notif.notif.content);
                                                 mSwitcher.setVisibility(View.VISIBLE);
                                             }


                                             if (response.notifs.size() > 1) {
                                                 mSwitcher.setOnClickListener(new View.OnClickListener() {

                                                     public void onClick(View v) {
                                                         // TODO Auto-generated method stub
                                                         currentIndex++;
                                                         if (currentIndex >= response.notifs.size())
                                                             currentIndex = 0;

                                                         Notif notif = response.notifs.get(currentIndex);
                                                         // If index reaches maximum reset it

                                                         mSwitcher.setText(notif.notif.content);
                                                     }
                                                 });
                                             }


                                         } catch (Exception e) {
                                             e.printStackTrace();
                                         }
                                     }

                                 }
                    );
        } else {
            String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            String url = "http://gtfs-tdt.irail.be/tec/departures/" + stop_id + "/" + date + "/00/00.json";
            Log.e("CVE", url);
            if (stop_id.length() > 0)
                Ion.with(this).load(url).setTimeout(OverviewFragment.TIMEOUT)
                        .as(new TypeToken<DepartureIrail>() {
                        })
                        .setCallback(new FutureCallback<DepartureIrail>() {
                                         @Override

                                         public void onCompleted(Exception e, DepartureIrail response) {
                                             if (response == null || e != null)
                                                 return;
                                             try {
                                                 ArrayList<Trip> list = new ArrayList<>();
                                                 for (DepartureIrail.StopTimes aStop : response.stopTimes) {
                                                     if (direction.contentEquals(aStop.stopTime.direction))
                                                         list.add(new Trip(new ATrip(aStop.stopTime.headsign, aStop.stopTime.iso8601.split("T")[1].split("\\+")[0])));
                                                 }

                                                 mRecyclerView.setAdapter(new TripsAdapter(list,
                                                         route_short_name,
                                                         query,
                                                         direction,
                                                         route_color,
                                                         route_text_color));

                                                 nextTrip = getNextTrip(list);
                                                 setDisplayTime(where);
                                             } catch (Exception e2) {
                                                 e2.printStackTrace();
                                             }
                                         }

                                     }
                        );
        }

    }

    android.os.Handler customHandler = new android.os.Handler();
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            ((TextView) findViewById(R.id.countdown)).setText(setDisplayTime(0));
            ((TextView) findViewById(R.id.countdown2)).setText(setDisplayTime(1));
            customHandler.postDelayed(this, 1000);
        }
    };

    private String setDisplayTime(int where) {

        Calendar d1;
        Calendar d2;

        try {
            d1 = Calendar.getInstance();
            //Log.e("CVE", "-1: " + d1.getTime());

            d2 = Calendar.getInstance();

            d2.set(Calendar.SECOND, 0);
            d2.set(Calendar.MINUTE, Integer.valueOf(nextTrip.split(":")[1]));
            d2.set(Calendar.HOUR_OF_DAY, Integer.valueOf(nextTrip.split(":")[0]));
            // Log.e("CVE", "-2a: " + d2.getTime());

            //in milliseconds
            long diff = d2.getTime().getTime() - d1.getTime().getTime();

            long diffSeconds = (diff / 1000 % 60);
            long diffMinutes = (diff / (60 * 1000) % 60);
            long diffHours = (diff / (60 * 60 * 1000) % 24);

            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");

            return String.format("%02d", diffHours) + ":" + String.format("%02d", diffMinutes) + ":" + String.format("%02d", diffSeconds);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nextTrip;
    }

    private String getNextTrip(ArrayList<Trip> trips) {

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String text = dateFormat.format(date);
        for (Trip aTrip : trips) {
            Log.e("CVE", "COMPARE: " + aTrip.trip.departure_time + " - " + text);
            if (text.compareTo(aTrip.trip.departure_time) < 0) {
                customHandler.postDelayed(updateTimerThread, 0);
                return aTrip.trip.departure_time;
            }

        }
        ((TextView) findViewById(R.id.countdown)).setText(R.string.notToday);
        return "";
    }

    public static String formatDate(Date yourDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(yourDate);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        starMenu = menu.add(Menu.NONE, 0, Menu.NONE, "Favori");

        starMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        if (myFile != null)
            starMenu.setIcon(myFile.exists() ? R.drawable.ic_menu_star_on
                    : R.drawable.ic_menu_star_old);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case 0:
                doFav();
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doFav() {
        if (myFile.exists())
            myFile.delete();
        else
            try {
                myFile.getParentFile().mkdirs();
                myFile.createNewFile();
                FileOutputStream f = new FileOutputStream(myFile);
                f.write(new Gson().toJson(bus, Bus.class).getBytes());
                f.flush();
                f.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        fromCache = myFile.exists();
        runOnUiThread(new Runnable() {
            public void run() {
                starMenu.setIcon(fromCache ? R.drawable.ic_menu_star_on
                        : R.drawable.ic_menu_star);
            }
        });
    }

}
