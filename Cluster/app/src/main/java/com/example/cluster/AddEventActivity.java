package com.example.cluster;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AddEventActivity extends AppCompatActivity {
    boolean newuser = true;

    private static final DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private static final DateFormat stf = new SimpleDateFormat("HH:mm");
    private static final int MAX_TITLE = 50;
    private static final int MAX_LOC = 200;
    private static final int MAX_DESC = 1000;
    FirebaseAuth auth;
    FirebaseFirestore db;
    TextView title, location, description;
    Button btnStartTime, btnEndTime, btnStartDate, btnEndDate;
    FloatingActionButton fabDelete, fabSave;
    int startMinute, startHour, startDay, startMonth, startYear,
        endMinute, endHour, endDay, endMonth, endYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // if we're not logged in go to login activity
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(AddEventActivity.this, LoginActivity.class));
            finish();
        }

        isNewUser(); //check if new user path has been created yet

        fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fabSave = (FloatingActionButton) findViewById(R.id.fab_save);

        title = (TextView) findViewById(R.id.title);
        location = (TextView) findViewById(R.id.location);
        description = (TextView) findViewById(R.id.description);

        btnStartTime = (Button) findViewById(R.id.btn_start_time);
        btnStartDate = (Button) findViewById(R.id.btn_start_date);
        btnEndTime = (Button) findViewById(R.id.btn_end_time);
        btnEndDate = (Button) findViewById(R.id.btn_end_date);

        Date today = new Date();
        btnStartTime.setText(stf.format(today));
        btnStartDate.setText(sdf.format(today));
        btnEndTime.setText(stf.format(today));
        btnEndDate.setText(sdf.format(today));

        // need to make sure these aren't null
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        startMinute = c.MINUTE;
        endMinute = c.MINUTE;
        startHour = c.HOUR_OF_DAY;
        endHour = c.HOUR_OF_DAY;
        startDay = c.DATE;
        endDay = c.DATE;
        startMonth = c.MONTH;
        endMonth = c.MONTH;
        startYear = c.YEAR;
        endYear = c.YEAR;

        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                                btnStartTime.setText(String.format("%02d:%02d", hourOfDay, minutes));
                                startHour = hourOfDay;
                                startMinute = minutes;
                            }
                        }, 0, 0, false);
                timePickerDialog.show();
            }
        });

        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                                btnEndTime.setText(String.format("%02d:%02d", hourOfDay, minutes));
                                endHour = hourOfDay;
                                endMinute = minutes;
                            }
                        }, 0, 0, false);
                timePickerDialog.show();
            }
        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                btnStartDate.setText(String.format("%02d/%02d/%04d", month+1, dayOfMonth, year));
                                startYear = year;
                                startMonth = month;
                                startDay = dayOfMonth;
                            }
                        }, 2019, 4, 0);
                datePickerDialog.show();
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                btnEndDate.setText(String.format("%02d/%02d/%04d", month+1, dayOfMonth, year));
                                endYear = year;
                                endMonth = month;
                                endDay = dayOfMonth;
                            }
                        }, 2019, 4, 0);
                datePickerDialog.show();
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddEventActivity.this, android.app.AlertDialog.THEME_TRADITIONAL)
                        .setTitle(R.string.delete_event)
                        .setMessage(R.string.delete_event_confirm)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.not_sure, null)
                        .show();
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // welcome to the funky java date zone
                //convert start and end to timestamps
                Calendar c = Calendar.getInstance();

                c.clear();
                c.set(Calendar.YEAR, startYear);
                c.set(Calendar.MONTH, startMonth);
                c.set(Calendar.DATE, startDay);
                c.set(Calendar.HOUR_OF_DAY, startHour);
                c.set(Calendar.MINUTE, startMinute);
                Timestamp tsStart = new Timestamp(c.getTime());

                c.clear();
                c.set(Calendar.YEAR, endYear);
                c.set(Calendar.MONTH, endMonth);
                c.set(Calendar.DATE, endDay);
                c.set(Calendar.HOUR_OF_DAY, endHour);
                c.set(Calendar.MINUTE, endMinute);
                Timestamp tsEnd = new Timestamp(c.getTime());

                Timestamp tsNow = new Timestamp(new Date());

                //this is gonna be rough
                //check that all the fields are valid
                //title, loc, and desc all fulfill length requirements
                if (title.getText().toString().trim().equals("")) {
                    title.setError(getText(R.string.no_input_error));
                } else if (title.getText().toString().trim().length() > MAX_TITLE) {
                    title.setError(getText(R.string.length_error));
                } else if (location.getText().toString().trim().equals("")) {
                    location.setError(getText(R.string.no_input_error));
                } else if (location.getText().toString().trim().length() > MAX_LOC) {
                    location.setError(getText(R.string.length_error));
                } else if (description.getText().toString().trim().equals("")) {
                    description.setError(getText(R.string.no_input_error));
                } else if (description.getText().toString().trim().length() > MAX_DESC) {
                    description.setError(getText(R.string.length_error));
                //time hasn't already passed
                } else if (tsStart.compareTo(tsNow) < 0) {
                    Toast.makeText(AddEventActivity.this, R.string.time_passed_error, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "START: " +tsStart + " NOW: " + tsNow);
                //ends after it starts
                } else if (tsStart.compareTo(tsEnd) >= 0) {
                    Toast.makeText(AddEventActivity.this, R.string.negative_duration_error, Toast.LENGTH_SHORT).show();
                } else {
                    //add it to firestore with correct mapping and correct document reference path to user
                    //right now let's just do example-country because we don't need it to be that fancy for sprint 1
                    // Create a new user in firestore db with uid and email
                    final DocumentReference userReference = db.collection("users/").document(auth.getUid()); //user path
                    CollectionReference createdEvent = db.collection("events/country/" + "example-country/"); //event path

                    Map<String, Object> event = new HashMap<>();
                    event.put("Title", title.getText().toString().trim());
                    event.put("Loc", location.getText().toString().trim());
                    event.put("Desc", description.getText().toString().trim());
                    event.put("Start", tsStart);
                    event.put("End", tsEnd);
                    event.put("creator", auth.getUid()); //store id of event owner
                    event.put("stars", 0);

                    // Add a new document with a generated ID
                    createdEvent.add(event)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Map<String, Object> createdEvent = new HashMap<>();
                                    createdEvent.put(documentReference.getId(), documentReference);
                                    //add the document reference path to the user's "created" events
                                    if(newuser){
                                        userReference.collection("events").document("created")
                                                .set(createdEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) { //New Task Upon Completion
                                                Toast.makeText(AddEventActivity.this, "Event Created",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        userReference.collection("events").document("created")
                                                .update(createdEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) { //New Task Upon Completion
                                                Toast.makeText(AddEventActivity.this, "Event Created",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddEventActivity.this, "Event Creation Failed",
                                            Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                    finish();
                }
            }
        });
    }

    private void isNewUser() {
        DocumentReference dr = db.collection("users/").document(auth.getUid());
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String eventPath;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                     newuser = false;
                    }

    }

}}); newuser = true;}
}