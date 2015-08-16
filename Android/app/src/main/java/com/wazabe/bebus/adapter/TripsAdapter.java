package com.wazabe.bebus.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wazabe.bebus.R;
import com.wazabe.bebus.RouteInfoActivity;
import com.wazabe.bebus.bo.Trip;

import java.util.ArrayList;
import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripViewHolder> {

    protected List<Trip> mData = new ArrayList<>();
    String route_desc;
    String route_id;
    String direction;
    String route_color;
    String route_text_color;

    public TripsAdapter(List<Trip> data, String route_desc, String route_id, String direction, String route_color, String route_text_color) {
        this.mData = data;
        this.route_id = route_id;
        this.direction = direction;
        this.route_color = route_color;
        this.route_text_color = route_text_color;
        this.route_desc = route_desc;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_trip, viewGroup, false);

        return new TripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TripViewHolder routeViewHolder, int position) {
        final Trip myTrip = mData.get(position);

        routeViewHolder.title.setText(myTrip.trip.departure_time);
        routeViewHolder.detail.setText(myTrip.trip.trip_headsign);
        routeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RouteInfoActivity.class)
                        .putExtra("route_short_name", route_desc)
                        .putExtra("route_id", route_id)
                        .putExtra("route_color", route_color)
                        .putExtra("route_text_color", route_text_color)
                        .putExtra("direction", direction)
                        .putExtra("trip_headsign", myTrip.trip.trip_headsign);
                v.getContext().startActivity(intent);
            }
        });
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        protected TextView title;
        protected TextView detail;

        public TripViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            detail = (TextView) itemView.findViewById(R.id.desc);
        }
    }


}
