package com.example.cluster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private TextView mTextMessage;

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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onBackPressed() { //so cannot get back in after signing out
        //do nothing
    }

}
