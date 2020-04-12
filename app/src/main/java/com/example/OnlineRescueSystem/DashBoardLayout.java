package com.example.OnlineRescueSystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.OnlineRescueSystem.Model.Callinfo;
import com.example.OnlineRescueSystem.Model.Registration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
public class DashBoardLayout extends AppCompatActivity implements View.OnClickListener{

    private View leftLowerViewForMap;
    private static final int Request_Call = 1;
    public String accidentType ="";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser mUser;
    private static final String TAG = "DashBoardLayout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

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


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accidentImageAndLableCardViewID:
                open("RoadAccident case");
                break;

            case R.id.fireImageAndLableCardViewID:
                open("Fire case");
                break;
            case R.id.medicalImageAndLableCardViewID:
                open("Medical case ");
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
            case (R.id.callIcon):
                open("Simple call");
                break;
            case (R.id.rightLoweViewForCall):
                open("Simple call");
                break;
        }
    }


    public void open(String type) {
        accidentType = type;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make a call to ::Rescue 1122::");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

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
        callIntent.setData(Uri.parse("tel:03451012867"));

        if (ContextCompat.checkSelfPermission(DashBoardLayout.this,android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoardLayout.this, new String[]{Manifest.permission.CALL_PHONE},Request_Call); {

            }
        }else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:03451012867")));
        }
    }

    public void onMap(View view){

        Intent intent = new Intent(DashBoardLayout.this,MapsActivity.class);
        intent.putExtra("Accident Type",accidentType);
        startActivity(intent);
        // don't finish here
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
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuthListener != null){
            mAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

}