package com.example.OnlineRescueSystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.example.OnlineRescueSystem.Model.Callinfo;
import com.example.OnlineRescueSystem.Model.Registration;
import com.example.OnlineRescueSystem.Model.UserRequest;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
//import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.ErrorListener;

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
    private ImageView mOutputText;

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
        sendFCMPush();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, "msg"+token);
                        //Toast.makeText(DashBoardLayout.this, "recieved", Toast.LENGTH_SHORT).show();
                    }
                });

//        mOutputText=findViewById(R.id.callIcon);
//
//        if (getIntent() != null && getIntent().hasExtra("key1")) {
//            mOutputText.setText("");
//
//            for (String key : getIntent().getExtras().keySet()) {
//                Log.d(TAG, "onCreate: Key: " + key + " Data: " + getIntent().getExtras().getString(key));
//                mOutputText.append(getIntent().getExtras().getString(key) + "\n");
//            }
//
//        }

    }// end of onCreate

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

    private void sendFCMPush() {

        final String Legacy_SERVER_KEY = "AAAANP5gGHA:APA91bFwye7sitBprCkqgXENmgMhsSdudtRmB4u6yqObSbSUP90SOIMpEGsY24tnpkGH7p7QEvI8g6oJhO3vC6QAEo0ksMz8j9adOeckLM6egaws-rmcSaTdPmNHAHPTw04aX4AJp6yW";
        String msg = "you are selected for rescue service. Please go to your map to view your destination";
        String title = "Rescue request";
        String token = "ep2y0GfjNys:APA91bHb14p1SEfuXJE9Kr0eLSZMcLvX4LinkowDYuC9atGwkSeXhKkRW0WTBxOHXjNfXPC3nSkyPCT9EyWB_hoCmvMwp59T73dENGpLhOcM4jyVgP51FvBJskRPMaQKe1PtAn-K7-8q";

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();

            objData.put("body", msg);
            objData.put("title", title);
            objData.put("sound",R.raw.sirena);
            objData.put("icon", R.mipmap.ambulance_small); //   icon_name image must be there in drawable
            objData.put("tag", token);
            objData.put("priority", "high");

            dataobjData = new JSONObject();
            dataobjData.put("text", msg);
            dataobjData.put("title", title);

            obj.put("to", token);
            //obj.put("priority", "high");

            obj.put("notification", objData);
            obj.put("data", dataobjData);
            Log.e("!_@rj@_@@_PASS:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("!_@@_SUCESS", response + "");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }
}

