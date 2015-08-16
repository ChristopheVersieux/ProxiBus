package com.wazabe.bebus;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SettingsActivity extends PreferenceActivity {

    private Toolbar mActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        try {
            mActionBar.setTitle(getString(R.string.app_name) + " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            mActionBar.setTitle(getString(R.string.app_name));
        }

        float[] hsv = new float[3];
        Color.colorToHSV(Color.DKGRAY, hsv);
        hsv[2] *= 0.6f;
        int statusColor = Color.HSVToColor(hsv);
        try {//If not Lollipop
            getWindow().setStatusBarColor(statusColor);
        } catch (Error e) {
            e.printStackTrace();
        }
        mActionBar.setBackgroundColor(Color.DKGRAY);
        mActionBar.setTitleTextColor(Color.WHITE);
        mActionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void onResume() {
        super.onResume();
    }


    @Override
    public void setContentView(int layoutResID) {
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.settings_activity, new LinearLayout(this), false);

        mActionBar = (Toolbar) contentView.findViewById(R.id.action_bar);
        mActionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);

        getWindow().setContentView(contentView);
    }
}
