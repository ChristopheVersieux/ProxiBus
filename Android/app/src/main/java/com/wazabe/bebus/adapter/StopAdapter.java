package com.wazabe.bebus.adapter;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.wazabe.bebus.BusInfoActivity;
import com.wazabe.bebus.OverviewFragment;
import com.wazabe.bebus.R;
import com.wazabe.bebus.bo.Direction;
import com.wazabe.bebus.bo.MyRoute;
import com.wazabe.bebus.bo.Route;
import com.wazabe.bebus.utils.Utils;
import com.wazabe.bebus.bo.Routes.Aroute.Stop;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 201601 on 4/22/2015.
 */
public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {

    protected List<Stop> mData = new ArrayList<>();

    public StopAdapter(ArrayList<Stop> data) {
        this.mData = data;

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public StopViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_stop, viewGroup, false);

        return new StopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StopViewHolder routeViewHolder, int position) {
        final Stop myStop = mData.get(position);
        routeViewHolder.text.setText(myStop.stop_name);

    }

    public static class StopViewHolder extends RecyclerView.ViewHolder {
        protected TextView text;

        public StopViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.title);
        }
    }


}
