package com.wazabe.bebus.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wazabe.bebus.R;
import com.wazabe.bebus.bo.BusStop;
import com.wazabe.bebus.bo.Fav;

import java.util.List;

/**
 * Created by versieuxchristophe on 08/03/15.
 */
public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavViewHolder> {

    private List<Fav> favList;

    public FavAdapter(List<Fav> favList) {
        this.favList = favList;
    }

    @Override
    public int getItemCount() {
        return favList.size();
    }

    @Override
    public void onBindViewHolder(FavViewHolder favViewHolder, int i) {
        Fav fav = favList.get(i);
        favViewHolder.tvTitle.setText(fav.name);
    }

    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.row_fav, viewGroup, false);

        return new FavViewHolder(itemView);
    }

    public static class FavViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvTitle;

        public FavViewHolder(View v) {
            super(v);
            tvTitle =  (TextView) v.findViewById(R.id.tv);
        }
    }
}
