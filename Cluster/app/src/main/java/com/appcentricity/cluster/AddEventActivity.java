package com.appcentricity.cluster;

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
import com.google.firebase.firestore.SetOptions;

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
    private static final DateFormat stf = new SimpleDateFormat("h:mm a");
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
        startMinute = c.get(Calendar.MINUTE);
        endMinute = c.get(Calendar.MINUTE);
        startHour = c.get(Calendar.HOUR_OF_DAY);
        endHour = c.get(Calendar.HOUR_OF_DAY);
        startDay = c.get(Calendar.DATE);
        endDay = c.get(Calendar.DATE);
        startMonth = c.get(Calendar.MONTH);
        endMonth = c.get(Calendar.MONTH);
        startYear = c.get(Calendar.YEAR);
        endYear = c.get(Calendar.YEAR);

        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, R.style.DialogTheme,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                                if (hourOfDay >= 1 && hourOfDay < 12) {
                                    btnStartTime.setText(String.format("%02d:%02d AM", hourOfDay, minutes));
                                } else if (hourOfDay > 12) {
                                    btnStartTime.setText(String.format("%02d:%02d PM", hourOfDay - 12, minutes));
                                } else if (hourOfDay == 12) {
                                    btnStartTime.setText(String.format("12:%02d PM", minutes));
                                } else {
                                    btnStartTime.setText(String.format("12:%02d AM", minutes));
                                }                                startHour = hourOfDay;
                                startMinute = minutes;
                            }
                        }, startHour, startMinute, false);
                timePickerDialog.show();
            }
        });

        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, R.style.DialogTheme,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                                if (hourOfDay >= 1 && hourOfDay < 12) {
                                    btnEndTime.setText(String.format("%02d:%02d AM", hourOfDay, minutes));
                                } else if (hourOfDay > 12) {
                                    btnEndTime.setText(String.format("%02d:%02d PM", hourOfDay - 12, minutes));
                                } else if (hourOfDay == 12) {
                                    btnEndTime.setText(String.format("12:%02d PM", minutes));
                                } else {
                                    btnEndTime.setText(String.format("12:%02d AM", minutes));
                                }                                endHour = hourOfDay;
                                endMinute = minutes;
                            }
                        }, endHour, endMinute, false);
                timePickerDialog.show();
            }
        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                btnStartDate.setText(String.format("%02d/%02d/%04d", month+1, dayOfMonth, year));
                                startYear = year;
                                startMonth = month;
                                startDay = dayOfMonth;
                            }
                        }, startYear, startMonth, startDay);
                datePickerDialog.show();
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                btnEndDate.setText(String.format("%02d/%02d/%04d", month+1, dayOfMonth, year));
                                endYear = year;
                                endMonth = month;
                                endDay = dayOfMonth;
                            }
                        }, endYear, endMonth, endDay);
                datePickerDialog.show();
            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddEventActivity.this,  R.style.DialogTheme)
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

                    final Map<String, Object> event = new HashMap<>();
                    event.put("Title", title.getText().toString().trim());
                    event.put("Loc", location.getText().toString().trim());
                    event.put("Desc", description.getText().toString().trim());
                    event.put("Start", tsStart);
                    event.put("End", tsEnd);
                    event.put("creator", auth.getUid()); //store id of event owner

                    // Add a new document with a generated ID
                    createdEvent.add(event)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Map<String, Object> createdEvent = new HashMap<>();
                                    createdEvent.put(documentReference.getId(), documentReference);
                                    //add the document reference path to the user's "created" events
                                    if(newuser){
                                        Map<String, Object> newUser = new HashMap<>();
                                        newUser.put("eventCreated", true);
                                        userReference.update(newUser);
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
                                                .set(createdEvent, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) { //New Task Upon Completion
                                                Toast.makeText(AddEventActivity.this, "Event Created",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    userReference.collection("events").document("interested").set(createdEvent, SetOptions.merge());
                                    Map<String, Object> stars = new HashMap<>();
                                    stars.put("stars", 1);
                                    documentReference.collection("public").document("star").set(stars);
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

    //check if path has been created yet. if not use boolean to enact a set instead of update fcn
    private void isNewUser() {
        DocumentReference dr = db.collection("users/").document(auth.getUid());
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        newuser = false;
                        try {
                            boolean value = document.getBoolean("eventCreated");
                            newuser = !value;
                        } catch (Exception e) {
                            newuser = true;
                        }
                    }
                }
            }
        });
    }
}