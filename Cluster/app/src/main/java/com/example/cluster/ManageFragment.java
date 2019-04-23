package com.example.cluster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ManageFragment extends Fragment {

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    FirebaseAuth auth;
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
        View v = inflater.inflate(R.layout.fragment_manage, container, false);
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
                        .putExtra("docPath", e.getDocPath())
                );
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Event e = managedEventList.get(position);
                e.star();
            }
        });

        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        populateManaged();
        return v;
        // Inflate the layout for this fragment

    }

    private void populateManaged() {
        DocumentReference dr = db.collection("users/" + auth.getUid() + "/events").document("created");

        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String eventPath;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        for (Map.Entry<String, Object> e : document.getData().entrySet()) {
                            eventPath = document.getDocumentReference(e.getKey()).getPath();
                            db.document(eventPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot doc = task.getResult();
                                        Event e = new Event(doc.getString("Title"),
                                                doc.getString("Desc"),
                                                doc.getTimestamp("Start"),
                                                doc.getTimestamp("End"),
                                                doc.getString("Loc"),
                                                doc.getString("creator"),
                                                0,
                                                doc.getReference().getPath());
                                        managedEventList.add(e);
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
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
        add.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        add.hide();
    }
}