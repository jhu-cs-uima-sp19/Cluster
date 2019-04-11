package com.example.cluster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ImageButton btnSettings;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_manage:
                    loadFragment(new ManageFragment());
                    return true;
                case R.id.navigation_interested:
                    loadFragment(new InterestedFragment());
                    return true;
                case R.id.navigation_find:
                    loadFragment(new FindFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent(); //get intent passed to it
        int fragId = intent.getIntExtra("fragId", 1);//get data passed in intent

        switch (fragId){
            case 0: loadFragment(new ManageFragment());
                    break;
            case 1: loadFragment(new InterestedFragment());
                    break;
            case 2: loadFragment(new FindFragment());
                    break;
            default: loadFragment(new InterestedFragment());
        }
        //loading the default fragment
        //loadFragment(new ManageFragment());

        auth = FirebaseAuth.getInstance();
        btnSettings = (ImageButton) findViewById(R.id.temp_settings_button);

        // if we're already logged in go to the main activity
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

}