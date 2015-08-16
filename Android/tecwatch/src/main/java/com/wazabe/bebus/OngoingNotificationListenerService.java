package com.wazabe.bebus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.wazabe.bebus.bo.Direction;
import com.wazabe.bebus.bo.MyRoute;
import com.wazabe.bebus.bo.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by versieuxchristophe on 07/05/15.
 */
public class OngoingNotificationListenerService extends WearableListenerService {

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        if (!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e("CVE", "Service failed to connect to GoogleApiClient.");
                return;
            }
        }

        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                ArrayList<Route> myRoutes = new ArrayList<>();
                if (true) {
                    // Get the data out of the event
                    DataMapItem dataMapItem =
                            DataMapItem.fromDataItem(event.getDataItem());
                    final String response = dataMapItem.getDataMap().getString("KEY_DATA").split(";")[1];
                    final String title = dataMapItem.getDataMap().getString("KEY_TITLE");
                    final String desc = dataMapItem.getDataMap().getString("KEY_DESC");

                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject childJSONObject = array.getJSONObject(i);
                                JSONObject stopObject = childJSONObject.getJSONObject("stop");

                                String stop_id = stopObject.getString("stop_id");
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
                                        myRoutes.add(new Route(aJSONObject.getString("route_short_name"), aJSONObject.getString("route_id"), aJSONObject.getString("route_long_name"), aJSONObject.getString("route_color"), aJSONObject.getString("route_text_color"), directions, stop_id));
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
                        e1.printStackTrace();
                    } finally {

                    }
                    List extras = new ArrayList();

                    ArrayList<Route> al2 = new ArrayList<Route>(myRoutes.subList(5, myRoutes.size()));
                    for (Route aRoute : al2) {
                        TextDrawable drawable = TextDrawable.builder()
                                .beginConfig()
                                .textColor(Color.parseColor("#" + aRoute.route_text_color))
                                .fontSize(70) /* size in px */
                                .bold()
                                .toUpperCase()
                                .endConfig()
                                .buildRoundRect(aRoute.route_short_name, Color.parseColor("#" + aRoute.route_color), 16);

                        int width = drawable.getIntrinsicWidth();
                        width = width > 0 ? width : 320;
                        int height = drawable.getIntrinsicHeight();
                        height = height > 0 ? height : 320;
                        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        int color = Color.parseColor("#" + aRoute.route_color);
                        Paint paint = new Paint();
                        paint.setColor(color);
                        Rect rect = new Rect(0, 0, 320, 320);
                        canvas.drawRect(rect, paint);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight() * 4 / 8);
                        drawable.draw(canvas);

                        Intent viewIntent = new Intent(getApplicationContext(), MainActivity.class);
                        String toSend = aRoute.directions.get(0).trip_headsign;
                        Log.d("CVE",toSend);
                        viewIntent.putExtra("KEY", toSend);
                        viewIntent.putExtra("COLOR", aRoute.route_color);

                        PendingIntent displayendingIntent = PendingIntent.getActivity(getApplicationContext(), aRoute.directions.get(0).hashCode(),viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        Notification aNotif = new NotificationCompat.Builder(getApplicationContext())
                                .setLargeIcon(bitmap)
                                .extend(new NotificationCompat.WearableExtender()
                                        .setDisplayIntent(displayendingIntent)
                                        .setCustomSizePreset(Notification.WearableExtender.SIZE_MEDIUM))
                                .setSmallIcon(R.mipmap.ic_launcher).build();

                        extras.add(aNotif);


                    }


                    NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this)
                            .setContentTitle(title)
                            .setContentText(desc)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.bg))
                            .setSmallIcon(R.mipmap.ic_launcher);

                    Notification notification = builder1
                            .extend(new NotificationCompat.WearableExtender()
                                    .addPages(extras))
                            .build();

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(0, notification);
                }
            } else {
                Log.d("CVE", "Unrecognized path: ");
            }
        }
    }
}


