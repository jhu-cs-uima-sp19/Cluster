package com.example.cluster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private TextView mTextMessage;
    FirebaseAuth auth;
    ImageButton btnSettings;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_manage:
                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("fragId", 0);
                    startActivity(intent);
//                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_interested:
                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("fragId", 1);
                    startActivity(intent);
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_find:
                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("fragId", 2);
                    startActivity(intent);
//                    mTextMessage.setText(R.string.title_notifications);
                    return true;
                default: return false;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        btnSettings = (ImageButton) findViewById(R.id.profile_settings_home);

        // if we're already logged in go to the main activity
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        btnSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
//            }
//        });
    }

    @Override
    public void onBackPressed() { //so cannot get back in after signing out
        //do nothing
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.profile_settings_home) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivityForResult(intent, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
