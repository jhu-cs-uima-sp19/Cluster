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
import android.text.format.Time;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEventActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    TextView title, location, description;
    Button btnStartTime, btnEndTime, btnStartDate, btnEndDate;
    FloatingActionButton fabDelete, fabSave;
    int startMinute, startHour, startDay, startMonth, startYear,
        endMinute, endHour, endDay, endMonth, endYear;

    private static final DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private static final DateFormat stf = new SimpleDateFormat("HH:mm");

    private static final int MAX_TITLE = 50;
    private static final int MAX_LOC = 200;
    private static final int MAX_DESC = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

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
                                btnStartDate.setText(String.format("%02d/%02d/%04d", month, dayOfMonth, year));
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
                                btnEndDate.setText(String.format("%02d/%02d/%04d", month, dayOfMonth, year));
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
                } else if (tsStart.compareTo(tsNow) > 0) {
                    Toast.makeText(AddEventActivity.this, R.string.time_passed_error, Toast.LENGTH_SHORT).show();
                //ends after it starts
                } else if (tsStart.compareTo(tsEnd) >= 0) {
                    Toast.makeText(AddEventActivity.this, R.string.negative_duration_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddEventActivity.this, "SUCCESS!", Toast.LENGTH_SHORT).show();
                }
                //add it to firestore with correct mapping and correct document reference path to user
                    //right now let's just do example-country because we don't need it to be that fancy for sprint 1
                    //convert start and end to timestamps
                //add the document reference path to the user's "created" events
            }
        });
    }
}