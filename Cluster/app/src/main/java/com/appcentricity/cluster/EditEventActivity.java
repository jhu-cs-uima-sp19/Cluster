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
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class EditEventActivity extends AppCompatActivity {

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
    String docPath;
    Timestamp tsStart, tsEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        docPath = getIntent().getExtras().getString("docPath");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // if we're not logged in go to login activity
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(EditEventActivity.this, LoginActivity.class));
            finish();
        }

        fabDelete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fabSave = (FloatingActionButton) findViewById(R.id.fab_save);

        title = (TextView) findViewById(R.id.title);
        location = (TextView) findViewById(R.id.location);
        description = (TextView) findViewById(R.id.description);

        btnStartTime = (Button) findViewById(R.id.btn_start_time);
        btnStartDate = (Button) findViewById(R.id.btn_start_date);
        btnEndTime = (Button) findViewById(R.id.btn_end_time);
        btnEndDate = (Button) findViewById(R.id.btn_end_date);

        db.document(docPath).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    title.setText(doc.getString("Title"));
                    description.setText(doc.getString("Desc"));
                    tsStart = doc.getTimestamp("Start");
                    tsEnd = doc.getTimestamp("End");
                    location.setText(doc.getString("Loc"));

                    // now do date processing
                    Date startDate = tsStart.toDate();
                    Date endDate = tsEnd.toDate();
                    btnStartTime.setText(stf.format(startDate));
                    btnStartDate.setText(sdf.format(startDate));
                    btnEndTime.setText(stf.format(endDate));
                    btnEndDate.setText(sdf.format(endDate));

                    Calendar c = Calendar.getInstance();
                    c.setTime(startDate);
                    startMinute = c.get(Calendar.MINUTE);
                    startHour = c.get(Calendar.HOUR_OF_DAY);
                    startDay = c.get(Calendar.DATE);
                    startMonth = c.get(Calendar.MONTH);
                    startYear = c.get(Calendar.YEAR);

                    c.setTime(endDate);
                    endMinute = c.get(Calendar.MINUTE);
                    endHour = c.get(Calendar.HOUR_OF_DAY);
                    endDay = c.get(Calendar.DATE);
                    endMonth = c.get(Calendar.MONTH);
                    endYear = c.get(Calendar.YEAR);

                } else {
                    // if we can't load the data boot the user back to the screen they came from
                    Log.d(TAG, "get failed with ", task.getException());

                    Toast.makeText(EditEventActivity.this, "Failed to Load Event Data",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        btnStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEventActivity.this, R.style.DialogTheme,
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
                                }
                                startHour = hourOfDay;
                                startMinute = minutes;
                            }
                        }, startHour, startMinute, false);
                timePickerDialog.show();
            }
        });

        btnEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEventActivity.this, R.style.DialogTheme,
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
                                }
                                endHour = hourOfDay;
                                endMinute = minutes;
                            }
                        }, endHour, endMinute, false);
                timePickerDialog.show();
            }
        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this, R.style.DialogTheme,
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this, R.style.DialogTheme,
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
                new AlertDialog.Builder(EditEventActivity.this, R.style.DialogTheme)
                        .setTitle(R.string.delete_event)
                        .setMessage(R.string.delete_event_confirm)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String eID = db.document(docPath).getId();
                                //Remove the document from the events database
                                //NOT DELETING SUBDOCUMENT, NOT SURE WHY, DELETE TESTS ARE FAILING IN THE DATABASE RULES SIMULATOR
                                db.document(docPath +"/public/star").delete();
                                db.document(docPath).delete();
                                Log.d(TAG, "DELETED DOCUMENT AT " + docPath);

                                //Remove the document from the user's created events list
                                Map<String, Object> updates = new HashMap<>();
                                updates.put(eID, FieldValue.delete());
                                db.document("users/" + auth.getUid() + "/events/created").update(updates);

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
                    Toast.makeText(EditEventActivity.this, R.string.time_passed_error, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "START: " +tsStart + " NOW: " + tsNow);
                    //ends after it starts
                } else if (tsStart.compareTo(tsEnd) >= 0) {
                    Toast.makeText(EditEventActivity.this, R.string.negative_duration_error, Toast.LENGTH_SHORT).show();
                } else {
                    //add it to firestore with correct mapping and correct document reference path to user
                    //right now let's just do example-country because we don't need it to be that fancy for sprint 1
                    // Create a new user in firestore db with uid and email
                    CollectionReference createdEvent = db.collection("events/country/" + "example-country/"); //event path

                    final Map<String, Object> event = new HashMap<>();
                    event.put("Title", title.getText().toString().trim());
                    event.put("Loc", location.getText().toString().trim());
                    event.put("Desc", description.getText().toString().trim());
                    event.put("Start", tsStart);
                    event.put("End", tsEnd);

                    // update event with inputted information
                    db.document(docPath).set(event, SetOptions.merge());
                    finish();
                }
            }
        });
    }
}