package com.wazabe.bebus;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity {

    GoogleApiClient mGoogleApiClient;
    public DrawerLayout drawerLayout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d("CVE", "onConnected: " + connectionHint);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d("CVE", "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d("CVE", "onConnectionFailed: " + result);
                    }
                })
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment f = null;
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("openDrawer", false).apply();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_close:
                        f = new OverviewFragment();
                        menuItem.setChecked(true);
                        break;
                    case R.id.navigation_item_search:
                        f = new SearchFragment();
                        menuItem.setChecked(true);
                        break;
                    case R.id.navigation_item_fav:
                        //f = new FavFragment();
                        //menuItem.setChecked(true);
                        break;
                    case R.id.navigation_item_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
                if(f!=null){
                    transaction.replace(R.id.content_frame, f);
                    transaction.commit();
                }

                drawerLayout.closeDrawers();
                //Need to find a better way that relying on the position!!!
                return true;
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_frame, new OverviewFragment());
        transaction.commit();

        if (PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("openDrawer", true))
            drawerLayout.openDrawer(GravityCompat.START);

    }

    public void sendData(String s, String title, String desc) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/PATH");

        // Add data to the request
        putDataMapRequest.getDataMap().putString("KEY_DATA", System.currentTimeMillis() + ";" + s);
        putDataMapRequest.getDataMap().putString("KEY_TITLE", title);
        putDataMapRequest.getDataMap().putString("KEY_DESC", desc);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        Log.d("CVE", "SEND: "
                + s);
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.d("CVE", "putDataItem status: "
                                + dataItemResult.getStatus().toString());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        try {//TODO remove try catch when option menu will be available
            SearchView searchView = ((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search)));
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        super.onDestroy();
    }
}
