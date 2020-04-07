package com.example.OnlineRescueSystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        final MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//        MenuItem main = menu.findItem(R.id.icon_only);
//        main.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Intent intent = new Intent(ProfileActivity.this, RegisterActivity.class);
//                startActivity(intent);
//                return false;
//            }
//        });
//        return true;
//    }
}
