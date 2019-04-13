package com.example.cluster;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

    private List<Event> managedEventList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventAdapter mAdapter;

    private FloatingActionButton add;

//    private OnFragmentInteractionListener mListener;

    public ManageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_manage, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        add = (FloatingActionButton) v.findViewById(R.id.fab_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddEventActivity.class));
            }
        });

        mAdapter = new EventAdapter(managedEventList);
        // make sure clicking an event sends you to the inspect event activity
        mAdapter.setClickListener(new EventAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Event e = managedEventList.get(position);
                startActivity(new Intent(getActivity(), InspectEventActivity.class)
                        /*
                         * WE NEED TO MAKE SURE THE INSPECT EVENT ACTIVITY CAN CAUSE CHANGES TO THE EVENT, SO WE SHOULD GIVE IT THE EVENT'S ID
                         * .putExtra("id", e.getId())
                         * */);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                managedEventList.get(position).star();
            }
        });

        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepDummyEventData();
        return v;
        // Inflate the layout for this fragment

    }

    private void prepDummyEventData() {
        Event e = new Event("Mad Max: Fury Road", "Action & Adventure", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Inside Out", "Animation, Kids & Family", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Star Wars: Episode VII - The Force Awakens", "Action", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Shaun the Sheep", "Animation", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("The Martian", "Science Fiction & Fantasy", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Mission: Impossible Rogue Nation", "Action", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Up", "Animation", "2009", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Inside Out", "Animation, Kids & Family", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Star Wars: Episode VII - The Force Awakens", "Action", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Shaun the Sheep", "Animation", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("The Martian", "Science Fiction & Fantasy", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Mission: Impossible Rogue Nation", "Action", "2015", "2015", "dummyLoc");
        managedEventList.add(e);

        e = new Event("Up", "Animation", "2009", "2015", "dummyLoc");
        managedEventList.add(e);

        mAdapter.notifyDataSetChanged();
    }
}