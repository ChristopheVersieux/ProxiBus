package com.wazabe.bebus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.util.Charsets;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.wazabe.bebus.adapter.RouteAdapter;
import com.wazabe.bebus.bo.BusStop;
import com.wazabe.bebus.bo.Direction;
import com.wazabe.bebus.bo.MyRoute;
import com.wazabe.bebus.bo.Route;
import com.wazabe.bebus.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 201601 on 3/6/2015.
 */
public class OverviewFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final static int TIMEOUT = 3000;

    GoogleApiClient mGoogleApiClient;
    List<BusStop> list = new ArrayList<BusStop>();

    FloatingActionMenu fabMenu;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;

    boolean demo = false;

    final String SRV_DEV = "188.166.107.76";
    static final String SRV_NEXTRIDE = "api.nextride.be";
    public static final String SRV_IRAIL = "gtfs-tdt.irail.be";


    View revealView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_overview, container, false);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        revealView = getView().findViewById(R.id.reveal_view);
        getView().findViewById(R.id.fab1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFavActivity(v);
            }
        });

        buildGoogleApiClient();


        fabMenu = (FloatingActionMenu) getView().findViewById(R.id.menu);

        fab1 = (FloatingActionButton) fabMenu.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) fabMenu.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) fabMenu.findViewById(R.id.fab3);


    }

    @Override
    public void onResume() {
        super.onResume();

        readfavs();

    }

    private void readfavs() {
        File[] myFile = getActivity().getDir("cache", Context.MODE_PRIVATE).listFiles();

        if (myFile.length > 0) {
            associateFabToFile(fab1, myFile[0]);
        } else
            setTuto(fab1);

        if (myFile.length > 1) {
            associateFabToFile(fab2, myFile[1]);
        } else
            fab2.setVisibility(View.GONE);

        if (myFile.length > 2) {
            associateFabToFile(fab3, myFile[2]);
        } else
            fab3.setVisibility(View.GONE);

        int[] location = new int[2];
        getView().findViewById(R.id.fab1).getLocationOnScreen(location);

        int cx = (location[0] + (getView().findViewById(R.id.fab1).getWidth() / 2));
        int cy = location[1] - (Utils.getStatusBarHeight(this.getActivity()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            startHideRevealEffect(cx, cy);
    }

    private void setTuto(FloatingActionButton fab) {
        fab.setVisibility(View.VISIBLE);
        fab.setLabelText(getActivity().getResources().getString(R.string.add_fav));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MyDialogFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
            }
        });
    }

    private void associateFabToFile(FloatingActionButton fab, File file) {
        String fileName = file.getName().replace("-slash-", "/");

        try {
            final String route_id = fileName.split(";")[0];
            final String route_short_name = fileName.split(";")[1];
            final String route_color = fileName.split(";")[2];
            final String route_text_color = fileName.split(";")[3];
            final String direction = fileName.split(";")[4];
            final String trip_headsign = fileName.split(";")[5];
            final String stop_id = fileName.split(";")[6];

            fab.setVisibility(View.VISIBLE);
            fab.setLabelText(route_short_name + " - " + trip_headsign);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OverviewFragment.this.getActivity(), BusInfoActivity.class)
                            .putExtra("route_short_name", route_short_name)
                            .putExtra("stop_id", stop_id)
                            .putExtra("route_id", route_id)
                            .putExtra("route_color", route_color)
                            .putExtra("route_text_color", route_text_color)
                            .putExtra("direction", direction)
                            .putExtra("trip_headsign", trip_headsign);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            //Log.e("CVE", file.getName());

            if (file.delete())
                readfavs();

            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        fabMenu.close(true);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            try {//TODO Crash at rotation, need to investigate
                readApiStops(mLastLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.e("CVE", mLastLocation.getLongitude() + ";" + mLastLocation.getLatitude());
        } else {
            readApiStops(null);
            Snackbar
                    .make(getView(), R.string.location, Snackbar.LENGTH_LONG)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            getActivity().startActivity(viewIntent);
                        }
                    })
                    .show(); // Don’t forget to show!
        }

    }

    public void readApiStops(Location mLastLocation) {
        if (mLastLocation == null || PreferenceManager.getDefaultSharedPreferences(OverviewFragment.this.getActivity()).getBoolean("debug", false)) {
            mLastLocation = new Location("DEMO");
            mLastLocation.setLatitude(50.388355);
            mLastLocation.setLongitude(4.4050591332);
        }
        Log.e("CVE", "http://" + SRV_NEXTRIDE + "/search/near/lat/" + mLastLocation.getLatitude() + "/lng/" + mLastLocation.getLongitude() + ".json");
        Ion.with(getActivity()).load("http://" + SRV_NEXTRIDE + "/search/near/lat/" + mLastLocation.getLatitude() + "/lng/" + mLastLocation.getLongitude() + ".json")
                .setTimeout(TIMEOUT)
                .addHeader("Authorization", "basic " + Base64.encodeToString("partner-christophe:34n4E59wKf".getBytes(), Base64.DEFAULT))
                .asString(Charsets.UTF_8)
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                                 @Override
                                 public void onCompleted(Exception e, Response<String> result) {
                                     String response = null;

                                     if (result != null)
                                         response = result.getResult();

                                     if (response != null && result.getHeaders() != null && result.getHeaders().code() == 200) {
                                         PreferenceManager.getDefaultSharedPreferences(OverviewFragment.this.getActivity()).edit().putString("cacheSearchNear", response).apply();
                                     } else {
                                         response = PreferenceManager.getDefaultSharedPreferences(OverviewFragment.this.getActivity()).getString("cacheSearchNear", "");
                                         if (result != null && result.getHeaders() != null && result.getHeaders().code() == 500) {

                                             Snackbar
                                                     .make(getView(), R.string.serverIssue, Snackbar.LENGTH_LONG)
                                                     .setAction(android.R.string.ok, new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                         }
                                                     })
                                                     .show(); // Don’t forget to show!


                                         } else {
                                             Snackbar
                                                     .make(getView(), R.string.noInternet, Snackbar.LENGTH_LONG)
                                                     .setAction(android.R.string.ok, new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {
                                                         }
                                                     })
                                                     .show(); // Don’t forget to show!

                                         }

                                     }

                                     //Log.e("CVE",response);
                                     list = new ArrayList<>();
                                     try {
                                         //Log.e("CVE", response);
                                         JSONArray array = new JSONArray(response);
                                         for (int i = 0; i < array.length(); i++) {
                                             try {
                                                 JSONObject childJSONObject = array.getJSONObject(i);
                                                 list.add(new BusStop(childJSONObject.getString("stop_name"), childJSONObject.getString("stop_id"), childJSONObject.getLong("stop_lat"), childJSONObject.getLong("stop_lon"), childJSONObject.getLong("distance")));
                                             } catch (JSONException e1) {
                                                 e1.printStackTrace();
                                             }
                                         }
                                     } catch (JSONException e1) {
                                         e1.printStackTrace();
                                     } finally {
                                         updateUi();
                                     }
                                 }

                             }

                );


    }

    private void updateUi() {
        List<View> views = new ArrayList<>();
        views.add(getView().findViewById(R.id.closest));
        views.add(getView().findViewById(R.id.closest1));
        views.add(getView().findViewById(R.id.closest2));
        views.add(getView().findViewById(R.id.closest3));
        views.add(getView().findViewById(R.id.closest4));
        views.add(getView().findViewById(R.id.closest5));
        views.add(getView().findViewById(R.id.closest6));

        if (list.size() == 0)
            setupStop(null, views.get(0), false, true);
        else {
            int i = 0;
            for (BusStop aStop : list) {
                if (i >= views.size())
                    break;
                try {
                    setupStop(aStop, views.get(i), true, i == 0);//i <= 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
        }


    }


    private void setupStop(final BusStop stop, final View container, boolean detail, boolean sendToWatch) {

        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(OverviewFragment.this.getActivity(), LinearLayoutManager.HORIZONTAL, false);

        final RecyclerView mRecyclerView = (RecyclerView) container.findViewById(R.id.recyclerview);
        mRecyclerView.setVisibility(View.VISIBLE);
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(layoutManager);
        }

        if (stop == null) {
            //((TextView) container.findViewById(R.id.closestTitle)).setText(R.string.noGPS);
        } else {
            Log.e("CVE", "http://" + SRV_NEXTRIDE + "/stops/" + stop.stop_id + ".json");
            ((TextView) container.findViewById(R.id.closestTitle)).setText(stop.stop_name + " (" + (stop.distance >= 1000 ? (float) ((int) stop.distance / 100) / 10 + "km" : (int) stop.distance + "m") + ")");
            final String cache = PreferenceManager.getDefaultSharedPreferences(OverviewFragment.this.getActivity()).getString(stop.stop_id, "");
            if (detail) {
                if (cache.length() == 0)
                    Ion.with(getActivity()).load("http://" + SRV_NEXTRIDE + "/stops/" + stop.stop_id + ".json").setTimeout(TIMEOUT).addHeader("Authorization", "basic " + Base64.encodeToString("partner-christophe:34n4E59wKf".getBytes(), Base64.DEFAULT)).asString()
                            .setCallback(new FutureCallback<String>() {
                                             @Override
                                             public void onCompleted(Exception e, String response) {
                                                 if (response != null && response.length() > 1) {
                                                     PreferenceManager.getDefaultSharedPreferences(OverviewFragment.this.getActivity()).edit().putString(stop.stop_id, response).apply();
                                                 } else {
                                                     response = cache;
                                                 }
                                                 setupStopView(response, mRecyclerView);
                                             }

                                         }
                            );
                else
                    setupStopView(cache, mRecyclerView);

                if (sendToWatch)
                    ((MainActivity) getActivity()).sendData(cache, stop.stop_name, (stop.distance >= 1000 ? (float) ((int) stop.distance / 100) / 10 + "km" : (int) stop.distance + "m"));
            } else
                mRecyclerView.setVisibility(View.GONE);
        }
    }

    private void setupStopView(String response, RecyclerView mRecyclerView) { //Log.e("CVE",response);
        ArrayList<MyRoute> myRoutes = new ArrayList<>();
        String stop_id = "";
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
                            ArrayList<Direction> directions = new ArrayList<>();
                            for (int k = 0; k < directionJSONArray.length(); k++) {
                                JSONObject directionJSONObject = directionJSONArray.getJSONObject(k);
                                directions.add(new Direction(directionJSONObject.getString("direction"), directionJSONObject.getString("trip_headsign")));

                            }

                            //Log.e("CVE", "***  " +aJSONObject.getString("route_short_name") );
                            boolean found = false;
                            for (MyRoute aRoute : myRoutes) {
                                if (aRoute.routes.get(0).route_short_name.contentEquals(aJSONObject.getString("route_short_name"))) {
                                    aRoute.routes.add(new Route(aJSONObject.getString("route_short_name"), aJSONObject.getString("route_id"), aJSONObject.getString("route_long_name"), aJSONObject.getString("route_color"), aJSONObject.getString("route_text_color"), directions, stop_id));
                                    found = true;
                                }

                            }
                            if (!found)
                                myRoutes.add(new MyRoute(new Route(aJSONObject.getString("route_short_name"), aJSONObject.getString("route_id"), aJSONObject.getString("route_long_name"), aJSONObject.getString("route_color"), aJSONObject.getString("route_text_color"), directions, stop_id)));
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
            PreferenceManager.getDefaultSharedPreferences(OverviewFragment.this.getActivity()).edit().putString(stop_id, "").apply();
            e1.printStackTrace();
        } finally {

            if (mRecyclerView != null && myRoutes.size() != 0)
                mRecyclerView.setAdapter(new RouteAdapter(myRoutes, OverviewFragment.this));
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        readApiStops(null);
        Snackbar
                .make(getView(), R.string.nogoogle, Snackbar.LENGTH_LONG)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .show(); // Don’t forget to show!

    }


    public void startFavActivity(View view) {

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int cx = (location[0] + (view.getWidth() / 2));
        int cy = location[1] - (Utils.getStatusBarHeight(this.getActivity()));

        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).edit();
        ed.putInt("x", cx);
        ed.putInt("y", cy);
        ed.apply();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            // Release all the Lollipop power!
            showRevealEffect(revealView, cx, cy, revealAnimationListener);
        else {
            Intent intent = new Intent(this.getActivity(), FavActivity.class);
            startActivity(intent);
        }
    }


    Animator.AnimatorListener revealAnimationListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {

            Intent i = new Intent(OverviewFragment.this.getActivity(), FavActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            getActivity().overridePendingTransition(0, 0);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    public static void showRevealEffect(final View v, int centerX, int centerY, Animator.AnimatorListener lis) {

        v.setVisibility(View.VISIBLE);

        int height = v.getHeight();

        Animator anim = ViewAnimationUtils.createCircularReveal(
                v, centerX, centerY, 0, height);

        anim.setDuration(350);

        anim.addListener(lis);
        anim.start();
    }

    public static void hideRevealEffect(final View v, int centerX, int centerY, int initialRadius) {
        //v.setVisibility(View.VISIBLE);

        Animator anim = ViewAnimationUtils.createCircularReveal(
                v, centerX, centerY, initialRadius, 0);

        anim.setDuration(350);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.GONE);
            }
        });

        anim.start();
    }


    private void startHideRevealEffect(final int cx, final int cy) {

        if (revealView.isAttachedToWindow())
            hideRevealEffect(revealView, cx, cy, revealView.getHeight() > revealView.getHeight() ? revealView.getHeight() : revealView.getHeight());
        // Show the unReveal effect when the view is attached to the window
        revealView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

                // Get the accent color
                //TypedValue outValue = new TypedValue();
                //getActivity().getTheme().resolveAttribute(android.R.attr.colorPrimary, outValue, true);
                //revealView.setBackgroundColor(outValue.data);
                hideRevealEffect(revealView, cx, cy, 1920);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
            }
        });

    }

    public void showRevealEffect(int cx, int cy, Animator.AnimatorListener revealAnimationListener) {
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).edit();
        ed.putInt("x", cx);
        ed.putInt("y", cy);
        ed.apply();
        showRevealEffect(revealView, cx, cy, revealAnimationListener);
    }

    public static class MyDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.tuto_fav)
                    .setTitle(R.string.add_fav);
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public boolean isMenuOpened() {
        return fabMenu.isOpened();
    }

    public void closeMenu() {
        fabMenu.close(true);
    }
}
