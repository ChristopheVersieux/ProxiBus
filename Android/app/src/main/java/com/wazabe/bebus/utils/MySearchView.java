package com.wazabe.bebus.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;

public class MySearchView extends SearchView {

    Activity activity;
    public MySearchView(Context context) {
        super(context);
    }

    @Override
    public void onActionViewCollapsed() {
        try {
            activity.finishAfterTransition();
        } catch (Error e) {
            activity.finish();
        }

    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
