package com.example.cluster;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ManageFragment extends Fragment {

    private List<Event> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventAdapter mAdapter;

//    private OnFragmentInteractionListener mListener;

    public ManageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_manage, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        mAdapter = new EventAdapter(movieList);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMovieData();
        return v;
        // Inflate the layout for this fragment

    }

    private void prepareMovieData() {
        Event movie = new Event("Mad Max: Fury Road", "Action & Adventure", "2015");
        movieList.add(movie);

        movie = new Event("Inside Out", "Animation, Kids & Family", "2015");
        movieList.add(movie);

        movie = new Event("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        movieList.add(movie);

        movie = new Event("Shaun the Sheep", "Animation", "2015");
        movieList.add(movie);

        movie = new Event("The Martian", "Science Fiction & Fantasy", "2015");
        movieList.add(movie);

        movie = new Event("Mission: Impossible Rogue Nation", "Action", "2015");
        movieList.add(movie);

        movie = new Event("Up", "Animation", "2009");
        movieList.add(movie);

        movie = new Event("Inside Out", "Animation, Kids & Family", "2015");
        movieList.add(movie);

        movie = new Event("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        movieList.add(movie);

        movie = new Event("Shaun the Sheep", "Animation", "2015");
        movieList.add(movie);

        movie = new Event("The Martian", "Science Fiction & Fantasy", "2015");
        movieList.add(movie);

        movie = new Event("Mission: Impossible Rogue Nation", "Action", "2015");
        movieList.add(movie);

        movie = new Event("Up", "Animation", "2009");
        movieList.add(movie);

        mAdapter.notifyDataSetChanged();
    }
}