package com.example.OnlineRescueSystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.OnlineRescueSystem.Model.Person;
import com.example.OnlineRescueSystem.Model.Registration;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private static final String TAG = "ProfileActivity";

    private ChildEventListener mChildEventListener;
    private Object Key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        runCode(ProfileActivity.this);
        readData(this);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Caller Data");
        //mDatabaseReference.setValue("a new mesg");
        mDatabaseReference.keepSynced(true);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Person person =  dataSnapshot.getValue(Person.class);
                Log.d(TAG,"name"+person.getName());
                Log.d(TAG,"age"+person.getAge());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseReference.removeEventListener(mChildEventListener);
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
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Toast.makeText(ProfileActivity.this,"ssdhls",Toast.LENGTH_LONG)
//                .show();
//
//
//        mDatabaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                Toast.makeText(ProfileActivity.this,"inner",Toast.LENGTH_LONG)
//                        .show();
//
//
//                Registration registration = dataSnapshot.getValue(Registration.class);
//
//
//                Toast.makeText(ProfileActivity.this,"ss"+registration.getName(),Toast.LENGTH_LONG)
//                        .show();
//                Log.d(TAG, "Value is: " + registration.getName());
//                Log.d(TAG, "Value is: " + registration.getCNIC());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.w(TAG, "Failed to read value.", databaseError.toException());
//
//            }
//        });
//    }

    public void readData(ProfileActivity view){



    }

    public void runCode(ProfileActivity view){
        String name = "abdul";
        String age = "21";

        Person person = new Person(name,age);
        String key = mDatabaseReference.getKey();
        mDatabaseReference.child((String) Key).setValue(person);
    }

}
