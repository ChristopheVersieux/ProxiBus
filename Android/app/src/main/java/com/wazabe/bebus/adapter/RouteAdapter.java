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

import com.amulyakhare.textdrawable.TextDrawable;
import com.wazabe.bebus.BusInfoActivity;
import com.wazabe.bebus.OverviewFragment;
import com.wazabe.bebus.R;
import com.wazabe.bebus.bo.Direction;
import com.wazabe.bebus.bo.MyRoute;
import com.wazabe.bebus.bo.Route;
import com.wazabe.bebus.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    protected List<MyRoute> mData = new ArrayList<>();
    Route clicked;
    OverviewFragment f;

    public RouteAdapter(List<MyRoute> data, OverviewFragment f) {
        this.mData = data;
        this.f = f;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_route, viewGroup, false);

        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder routeViewHolder, int position) {
        final MyRoute myRoutes = mData.get(position);
        final Route route=myRoutes.routes.get(0);

        final ArrayList<String> titlesArray = new ArrayList<>();
        final ArrayList<String> dirsArray = new ArrayList<>();
        final ArrayList<String> stopArray = new ArrayList<>();

        for (Route aRoute : myRoutes.routes) {
            for (Direction aDirection : aRoute.directions) {
                titlesArray.add(aDirection.trip_headsign);
                dirsArray.add(aDirection.direction);
                stopArray.add(aRoute.stop_id);

            }
        }
        final String[] titles = titlesArray.toArray(new String[titlesArray.size()]);

        routeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(R.string.pick_route)
                        .setItems(titles, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                RouteAdapter.this.clicked = route;
                                startDetailActivity(view, titles[which], dirsArray.get(which),stopArray.get(which));
                            }
                        });
                builder.create().show();

            }
        });

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(6) /* thickness in px */
                .textColor(Color.parseColor("#" + route.route_text_color))
                .fontSize(50) /* size in px */
                .bold()
                .toUpperCase()
                .endConfig()
                .buildRoundRect(route.route_short_name, Color.parseColor("#" + route.route_color), 16);
        routeViewHolder.image.setImageDrawable(drawable);
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;

        public RouteViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image_view);
        }
    }

    Context c;

    public void startDetailActivity(View view, String trip_headsign, String direction, String stop_id) {
        c = view.getContext();
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        int cx = (location[0] + (view.getWidth() / 2));
        int cy = location[1] - (Utils.getStatusBarHeight(c));

        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(c).edit();
        ed.putInt("x", cx);
        ed.putInt("y", cy);
        ed.apply();


        if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            f.showRevealEffect(cx, cy, revealAnimationListener);
        } else {
            Intent intent = new Intent(c, BusInfoActivity.class).putExtra("route_short_name", clicked.route_short_name).putExtra("stop_id", stop_id).putExtra("route_id", clicked.route_id).putExtra("route_color", clicked.route_color).putExtra("route_text_color", clicked.route_text_color).putExtra("direction", direction).putExtra("trip_headsign", trip_headsign);
            c.startActivity(intent);
        }
    }

    Animator.AnimatorListener revealAnimationListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {

            Intent i = new Intent(c, BusInfoActivity.class).putExtra("route_id", clicked.route_id);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            c.startActivity(i);
            ((Activity) c).overridePendingTransition(0, 0);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

}
