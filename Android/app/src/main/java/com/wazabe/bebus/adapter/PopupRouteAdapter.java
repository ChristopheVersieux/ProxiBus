package com.wazabe.bebus.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wazabe.bebus.R;
import com.wazabe.bebus.bo.PopupRoute;

import java.util.ArrayList;

/**
 * Created by 201601 on 4/22/2015.
 */
public class PopupRouteAdapter extends ArrayAdapter<PopupRoute> {
    private final Activity context;

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    public PopupRouteAdapter(Activity context, ArrayList<PopupRoute> routes) {
        super(context, R.layout.row_popup_route, routes);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row_popup_route, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.text);
            viewHolder.image = (ImageView) rowView
                    .findViewById(R.id.image_view);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        PopupRoute item = getItem(position);

        holder.text.setText(item.trip_headsign);

        return rowView;
    }
}
