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

    FirebaseAuth auth;
    private int start = 0;

    // This takes us to the correct fragment in the MainActivity
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
                    return true;
                case R.id.navigation_interested:
                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("fragId", 1);
                    startActivity(intent);
                    return true;
                case R.id.navigation_find:
                    intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.putExtra("fragId", 2);
                    startActivity(intent);
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

        // if we're already logged in go to the main activity
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_top, new HomeTopFragment()).commit();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_bottom, new HomeBottomFragment()).commit();
        start = 1;
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
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        // This creates two new fragments with a new adapter and correct lists
        if (start == 1) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_top, new HomeTopFragment()).commit();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_bottom, new HomeBottomFragment()).commit();
        }
    }

}
