package com.example.cluster;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class InspectEventActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore db;
    String docPath;
    Event thisEvent;
    TextView title, organizer, startTime, endTime, location, description, stars;
    ImageButton interested;
    String crUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_event);
        docPath = getIntent().getExtras().getString("docPath");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        title = (TextView) findViewById(R.id.title);
        organizer = (TextView) findViewById(R.id.organizer);
        startTime = (TextView) findViewById(R.id.starttime);
        endTime = (TextView) findViewById(R.id.endtime);
        location = (TextView) findViewById(R.id.location);
        description = (TextView) findViewById(R.id.description);
        stars = (TextView) findViewById(R.id.stars);
        interested = (ImageButton) findViewById(R.id.interested);

        // if we're not logged in go to login activity
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(InspectEventActivity.this, LoginActivity.class));
            finish();
        }

        // first we get the stars info and save that in the stars textview
        // thus we can access it outside of the task
        // then on the task completion we get the other fields
        db.document(docPath + "/public/star").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    stars.setText(doc.getLong("stars").toString());
                    db.document(docPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot doc = task.getResult();
                                thisEvent = new Event(doc.getString("Title"),
                                        doc.getString("Desc"),
                                        doc.getTimestamp("Start"),
                                        doc.getTimestamp("End"),
                                        doc.getString("Loc"),
                                        doc.getString("creator"),
                                        Long.parseLong(stars.getText().toString()),
                                        doc.getReference().getPath());
                                String infoDisplayBuilder;
                                title.setText(thisEvent.getTitle());

                                infoDisplayBuilder = getResources().getString(R.string.start_time) + thisEvent.getStartTime();
                                startTime.setText(infoDisplayBuilder);

                                infoDisplayBuilder = getResources().getString(R.string.end_time) + thisEvent.getEndTime();
                                endTime.setText(infoDisplayBuilder);

                                infoDisplayBuilder = getResources().getString(R.string.location) + thisEvent.getLocation();
                                location.setText(infoDisplayBuilder);

                                infoDisplayBuilder = getResources().getString(R.string.description) + thisEvent.getDescription();
                                description.setText(infoDisplayBuilder);

                                infoDisplayBuilder = getResources().getString(R.string.stars) + thisEvent.getStars();
                                stars.setText(infoDisplayBuilder);

                                getCreatorUserName(thisEvent.getCreator());

                                StarListener(); //listen for changes on Server End and update stars accordingly
                            }
                        }
                    });
                } else {
                    // if we can't load the data boot the user back to the screen they came from
                    Log.d(TAG, "get failed with ", task.getException());

                    Toast.makeText(InspectEventActivity.this, "Failed to Load Event Data",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        // if this event is marked interested by the user make the button have a star
        DocumentReference dr = db.collection("users/" + auth.getUid() + "/events").document("interested");
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String eventId = docPath.split("/")[docPath.split("/").length - 1];
                    DocumentSnapshot doc = task.getResult();
                    if (doc.get(eventId) != null) {
                        interested.setImageDrawable(getResources().getDrawable(R.drawable.btn_interested));
                    }
                }
            }
        });

        interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference userReference = db.collection("users/").document(auth.getUid());
                final DocumentReference interestedReference = userReference.collection("events").document("interested");
                interestedReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // I don't think we need to do a check if this task is successful--I did a bunch of testing and couldn't get it to fail
                        // case 1, user doesn't have the doc id in their interested doc
                        if (task.getResult().get(db.document(docPath).getId()) == null) {
                            Map<String, Object> interestedEvent = new HashMap<>();
                            interestedEvent.put(db.document(docPath).getId(), db.document(docPath));

                            //just in case, might not be necessary but can do no harm
                            //this is so the user doc has fields and thus isn't italic even if the user doesn't have "eventCreated" filled in
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("eventInterested", true);
                            userReference.set(newUser);

                            interestedReference.set(interestedEvent, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) { //change button to star
                                    interested.setImageDrawable(getResources().getDrawable(R.drawable.btn_interested));
                                    thisEvent.star(); //star event
                                    db.document(docPath + "/public/star").update("stars", thisEvent.getStars());
                                    //String infoDisplayBuilder = getResources().getString(R.string.stars) + thisEvent.getStars();
                                    //stars.setText(infoDisplayBuilder);
                                }
                            });
                            // case 2, user exists and does have the doc id in their interested doc
                        } else {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put(db.document(docPath).getId(), FieldValue.delete());
                            interestedReference.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    interested.setImageDrawable(getResources().getDrawable(R.drawable.btn_uninterested));
                                    thisEvent.unStar();
                                    db.document(docPath + "/public/star").update("stars", thisEvent.getStars());
                                    //String infoDisplayBuilder = getResources().getString(R.string.stars) + thisEvent.getStars();
                                    //stars.setText(infoDisplayBuilder);
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    //get username of event creator
    private void getCreatorUserName(final String crUID) {

        db.collection("users").document(crUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    crUserName = doc.getString("userName");
                    Log.d(TAG, "USERNAME Val " + crUserName);

                    if (crUserName == null || crUserName.toLowerCase().equals("null")) {
                        crUserName = crUID;
                    }
                    dispCreatorInfo();

                } else {
                    Toast.makeText(InspectEventActivity.this, "Failed to Load Creator Username",
                            Toast.LENGTH_SHORT).show();
                    crUserName = crUID;
                    dispCreatorInfo();
                }
            }
        });
    }

    //listens to backend database for changes and populates data on page REALTIME
    public void StarListener() {
        final DocumentReference docRef = db.document(docPath + "/public/star");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    thisEvent.setStars(snapshot.getLong("stars"));
                    updateStarDisp();

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }


    private void dispCreatorInfo() {
        organizer.setText(getResources().getString(R.string.organizer) + crUserName);
    }

    private void updateStarDisp() {
        String infoDisplayBuilder = getResources().getString(R.string.stars) + thisEvent.getStars();
        stars.setText(infoDisplayBuilder);
    }
}