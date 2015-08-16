package com.wazabe.bebus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wazabe.bebus.adapter.FavAdapter;
import com.wazabe.bebus.bo.Fav;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fav, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<Fav> favs= new ArrayList<>();
        favs.add(new Fav());
        favs.add(new Fav());
        favs.add(new Fav());
        // specify an adapter (see also next example)
        FavAdapter mAdapter = new FavAdapter(favs);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);

        /*inflater.inflate(R.menu.menu_search, menu);
        MenuItemCompat.expandActionView(menu.findItem(R.id.menu_search));
        MySearchView searchView = ((MySearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search)));
        searchView.setActivity(this.getActivity());

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));*/
    }


}
