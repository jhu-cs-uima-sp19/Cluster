package com.example.cluster;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class InspectEventActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String docPath;
    Event e;
    TextView title, organizer, startTime, endTime, location, description, stars;
    Button interested;

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
        interested = (Button) findViewById(R.id.interested);

        // if we're not logged in go to login activity
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(InspectEventActivity.this, LoginActivity.class));
            finish();
        }

        db.document(docPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    e = new Event(doc.getString("Title"),
                            doc.getString("Desc"),
                            doc.getTimestamp("Start"),
                            doc.getTimestamp("End"),
                            doc.getString("Loc"),
                            doc.getDocumentReference("orgId").toString(),
                            doc.getLong("stars").intValue(),
                            doc.getReference().getPath());

                    title.setText(e.getTitle());
                    startTime.setText(e.getStartTime());
                    endTime.setText(e.getEndTime());
                    location.setText(e.getLocation());
                    description.setText(e.getDescription());
                    organizer.setText(e.getOrgId());
                    stars.setText(Integer.toString(e.getStars()));
                } else {
                    // if we can't load the data boot the user back to the screen they came from
                    Log.d(TAG, "get failed with ", task.getException());
                    finish();
                }
            }
        });

        interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InspectEventActivity.this, "Not yet implemented!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
