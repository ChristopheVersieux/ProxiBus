package com.wazabe.bebus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.poliveira.parallaxrecycleradapter.ParallaxRecyclerAdapter;
import com.wazabe.bebus.bo.Bus;
import com.wazabe.bebus.bo.Routes;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by versieuxchristophe on 07/03/15.
 */
public class RouteInfoActivity extends AppCompatActivity  implements OnMapReadyCallback{

    String route_color = "";
    String route_text_color = "";
    String route_short_name = "";
    String route_id = "";
    String direction = "";
    String trip_headsign = "";
    File myFile;
    boolean fromCache;
    MenuItem starMenu;
    Bus bus;
    Toolbar mToolbar;
    MapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_map);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getMap().getUiSettings().setMapToolbarEnabled(false);
        mapFragment.getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });


        route_short_name = getIntent().getExtras().getString("route_short_name");
        route_color = getIntent().getExtras().getString("route_color");
        route_text_color = getIntent().getExtras().getString("route_text_color");
        direction = getIntent().getExtras().getString("direction");
        route_id = getIntent().getExtras().getString("route_id");
        trip_headsign = getIntent().getExtras().getString("trip_headsign");

        int routeColor = Color.parseColor("#" + route_color);

        findViewById(R.id.toolbar).setBackgroundColor(routeColor);

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

        mToolbar = ((Toolbar) findViewById(R.id.toolbar));
        mToolbar.setTitleTextColor(Color.parseColor("#" + route_text_color));
        setTitle(route_short_name + " - " + trip_headsign);


        myFile = new File(getDir("cache", Context.MODE_PRIVATE), ("ROUTE:" + ";" + route_short_name + ";" + route_color + ";" + route_text_color + ";" + direction + ";" + route_id).replace("/", "-slash-"));
        myFile.getParentFile().mkdirs();
        fromCache = myFile.exists();




    }

    private void doMySearch() {
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(RouteInfoActivity.this, LinearLayoutManager.VERTICAL, false);
        final RecyclerView mRecyclerView = (RecyclerView) (RouteInfoActivity.this.findViewById(R.id.recyclerview));
        mRecyclerView.setLayoutManager(layoutManager);

        String url = "http://" + OverviewFragment.SRV_NEXTRIDE + "/routes/" + route_id + "/direction/" + direction + ".json";
        Log.e("CVE", url);
        Ion.with(this).load(url).setTimeout(OverviewFragment.TIMEOUT).addHeader("Authorization", "basic " + Base64.encodeToString("partner-christophe:34n4E59wKf".getBytes(), Base64.DEFAULT))
                .as(new TypeToken<Routes>() {
                })
                .setCallback(new FutureCallback<Routes>() {
                                 @Override
                                 public void onCompleted(Exception ex, final Routes response) {
                                     try {
                                         final ArrayList<Routes.Aroute.Stop> content = response.route.stops;
                                         mRecyclerView.setHasFixedSize(true);
                                         LinearLayoutManager llm = new LinearLayoutManager(RouteInfoActivity.this);
                                         llm.setOrientation(LinearLayoutManager.VERTICAL);
                                         mRecyclerView.setLayoutManager(llm);
                                         mRecyclerView.setAdapter(new StopAdapter(content));

                                         mapFragment.getMapAsync(new OnMapReadyCallback() {
                                             @Override
                                             public void onMapReady(GoogleMap googleMap) {

                                                 googleMap.getUiSettings().setMapToolbarEnabled(false);
                                                 googleMap.setPadding(0, mToolbar.getHeight(), 0, 0);

                                                 //PolylineOptions options = new PolylineOptions().width(8).color(Color.BLUE).geodesic(true);
                                                 LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                                                 for (Routes.Aroute.Stop aStop : response.route.stops) {
                                                     LatLng point = new LatLng(aStop.stop_lat, aStop.stop_lon);
                                                     //options.add(point);
                                                     googleMap.addMarker(
                                                             new MarkerOptions()
                                                                     .position(point)
                                                                     .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                                                     .title(aStop.stop_name)
                                                     );
                                                     boundsBuilder.include(point);
                                                 }
                                                 LatLngBounds bounds = boundsBuilder.build();
                                                 googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
                                                 googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                                     @Override
                                                     public void onInfoWindowClick(Marker marker) {

                                                         Intent intent = new Intent(Intent.ACTION_VIEW);
                                                         intent.setData(Uri.parse("geo:0,0?q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "(" + marker.getTitle() + ")"));
                                                         if (intent.resolveActivity(getPackageManager()) != null) {
                                                             startActivity(intent);
                                                         } else {
                                                             Snackbar
                                                                     .make(findViewById(R.id.root), R.string.noMap, Snackbar.LENGTH_LONG)
                                                                     .setAction(R.string.ok, new View.OnClickListener() {
                                                                         @Override
                                                                         public void onClick(View v) {

                                                                         }
                                                                     })
                                                                     .show(); // Don’t forget to show!
                                                         }
                                                     }
                                                 });
                                                 //googleMap.addPolyline(options);


                                             }
                                         });

                                     } catch (Exception e) {
                                         Log.d("CVE", "Exception " + e.getMessage());
                                         e.printStackTrace();
                                     }
                                 }

                             }
                );
    }

    public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {

        private ArrayList<Routes.Aroute.Stop> list;

        public StopAdapter(ArrayList<Routes.Aroute.Stop> pList) {
            this.list = pList;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onBindViewHolder(StopViewHolder viewHolder, int i) {
            final Routes.Aroute.Stop myStop = list.get(i);
            viewHolder.text.setText(myStop.stop_name);
        }

        @Override
        public StopViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.row_stop, viewGroup, false);

            return new StopViewHolder(itemView);
        }


        public class StopViewHolder extends RecyclerView.ViewHolder {
            protected TextView text;

            public StopViewHolder(View v) {
                super(v);
                text =  (TextView) v.findViewById(R.id.title);
            }
        }
    }

    public static String formatDate(Date yourDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(yourDate);
    }


    /*  @Override
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
  */
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        doMySearch();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
