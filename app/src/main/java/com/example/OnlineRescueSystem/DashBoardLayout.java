package com.example.OnlineRescueSystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.OnlineRescueSystem.Model.Callinfo;
import com.example.OnlineRescueSystem.Model.Registration;
import com.example.OnlineRescueSystem.Model.UserRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashBoardLayout extends AppCompatActivity implements View.OnClickListener{

    private View leftLowerViewForMap;
    private static final int Request_Call = 1;
    public String accidentType ="";
    private String subEmail;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser mUser;
    private static final String TAG = "DashBoardLayout";
    private DatabaseReference myRef,callerData;
    private FirebaseDatabase database;
    private boolean availed = false;
    private ProgressDialog mProgress;
    private String neededEmergency;
    private String selectedDriver;
    private ImageView call;
    private String phoneNumber = "tel:03048146310";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        call = findViewById(R.id.callIcon);



        mProgress = new ProgressDialog(DashBoardLayout.this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                } else {
                    startActivity(new Intent(DashBoardLayout.this,LoginScreen.class));
                }
            }
        };
        Log.d(TAG, "onDataChange: availed1");

    }//

    public void onCall(View view){
        if (availed){
            makeCall();
        }else {
            open("Simple call");
        }
    }

    @Override
    public void onClick(View v) {
        if (!availed) {
            switch (v.getId()) {
                case R.id.accidentImageAndLableCardViewID:
                    open("RoadAccident case");
                    break;

                case R.id.fireImageAndLableCardViewID:
                    open("Fire case");
                    break;
                case R.id.medicalImageAndLableCardViewID:
                    open("Medical case");
                    break;

                case R.id.crimeImageAndLableCardViewID:
                    open("Crime case");
                    break;

                case R.id.drowningImageAndLableCardViewID:
                    open("drowning case");
                    break;

                case (R.id.structureCollapseImageAndLableCardViewID):
                    open("Building collapse case");
                    break;
            }
        }else {
            Toast.makeText(this, "You are already availed service so please wait or click on call icon", Toast.LENGTH_LONG).show();
        }
    }


    public void open(final String type) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make a call to ::Rescue 1122::");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (type == "RoadAccident case" || type == "Medical case" || type == "Crime case" || type == "Simple call"){
                            neededEmergency = "Accident recovery team";
                        }else if (type == "Building collapse case"){
                            neededEmergency = "Rescue service";
                        }else if (type == "drowning case"){
                            neededEmergency = "life guard service";
                        }else if (type == "Fire case"){
                            neededEmergency = "Fire brigade";
                        }
                        accidentType = type;
                        makeCall();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DashBoardLayout.this, "You clicked No button", Toast.LENGTH_SHORT).show();
                Toast.makeText(DashBoardLayout.this, " Press Yes if you need 1122", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void makeCall() {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));

        if (ContextCompat.checkSelfPermission(DashBoardLayout.this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoardLayout.this, new String[]{Manifest.permission.CALL_PHONE},Request_Call); {

            }
        }else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber)));
        }
    }

    public void onMap(View view){
        Log.d(TAG, "acc: "+accidentType);
        if (!availed){
            Intent intent = new Intent(DashBoardLayout.this,MapsActivity.class);
            intent.putExtra("Accident",accidentType);
            intent.putExtra("availed","false");
            intent.putExtra("neededEmergency",neededEmergency);
            startActivity(intent);
    }
        else {
            Intent intent = new Intent(DashBoardLayout.this,MapsActivity.class);
            Log.d(TAG, "onMap: "+availed);
            intent.putExtra("availed","true");
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Request_Call){
            if(grantResults.length>0&& grantResults[0]== PackageManager.PERMISSION_GRANTED){
                makeCall();
            }else {
                Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT);

            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_profile:

                Intent intent3 = new Intent(DashBoardLayout.this,ProfileActivity.class);
                startActivity(intent3);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgress.setMessage("please wait...");
        mProgress.show();

        Log.d(TAG, "onDataChange:t:");
        subEmail = mUser.getEmail();
        subEmail = subEmail.substring(0, subEmail.indexOf("."));
        mAuth.addAuthStateListener(firebaseAuthListener);

        if (firebaseAuthListener != null) {
            mAuth.removeAuthStateListener(firebaseAuthListener);

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Active Case").child(subEmail);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        UserRequest userRequest1 = dataSnapshot.getValue(UserRequest.class);

                        selectedDriver = userRequest1.getEmail();
                        availed = true;
                        callerData = database.getReference("Caller Data").child(selectedDriver).child("profile detail").child("wese");

                        callerData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Registration registration = dataSnapshot.getValue(Registration.class);
                                phoneNumber = "tel:"+(registration.getPhoneNumber());

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        mProgress.dismiss();
                        Log.d(TAG, "onDataChange: "+selectedDriver);
                    } else {
                        Log.d(TAG, "onDataChange: not availed");
                        mProgress.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DashBoardLayout.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}

