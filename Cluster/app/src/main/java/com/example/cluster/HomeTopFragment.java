package com.example.cluster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomeTopFragment extends Fragment {

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    FirebaseAuth auth;
    private List<Event> interestedEventList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EventAdapter mAdapter;
    String eID;

    public HomeTopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_top, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_top);

        mAdapter = new EventAdapter(interestedEventList);
        // make sure clicking an event sends you to the inspect event activity
        mAdapter.setClickListener(new EventAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Event e = interestedEventList.get(position);
                startActivity(new Intent(getActivity(), InspectEventActivity.class)
                        .putExtra("docPath", e.getDocPath())
                );
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Event e = interestedEventList.get(position);
                e.star();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

//        populateInterested();
        return v;
        // Inflate the layout for this fragment

    }

    private void populateInterested() {
        DocumentReference dr = db.collection("users/" + auth.getUid() + "/events").document("interested");

        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String eventPath;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        for (Map.Entry<String, Object> e : document.getData().entrySet()) {
                            eventPath = document.getDocumentReference(e.getKey()).getPath();
                            eID = e.getKey();
                            db.document(eventPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (doc.exists()) {
                                            Event e = new Event(doc.getString("Title"),
                                                    doc.getString("Desc"),
                                                    doc.getTimestamp("Start"),
                                                    doc.getTimestamp("End"),
                                                    doc.getString("Loc"),
                                                    doc.getString("creator"),
                                                    0,
                                                    doc.getReference().getPath());
                                            interestedEventList.add(e);
                                            mAdapter.eventFullAdd(e);
                                            mAdapter.eventFullSort();
                                            Collections.sort(interestedEventList);
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            // tries to find a removed event, remove event from list
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put(eID, FieldValue.delete());
                                            db.document("users/" + auth.getUid() + "/events/interested").update(updates);
                                        }
                                    } else {
                                        Log.d(TAG, "Task failed with exception: ", task.getException());
                                    }
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(interestedEventList.size() == 0) {
            populateInterested();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        interestedEventList.clear();
    }
}