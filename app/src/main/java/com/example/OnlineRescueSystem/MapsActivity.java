package com.example.OnlineRescueSystem;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.OnlineRescueSystem.Model.Callinfo;
import com.example.OnlineRescueSystem.Model.LocationInfo;
import com.example.OnlineRescueSystem.Model.Registration;
import com.example.OnlineRescueSystem.Model.UserRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap,mMap1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference myRef, myRef1, myRef2, myRef3,myRef4,activeCase,activeCase2;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static final String TAG = "MapsActivity";
    private String selectedDriver,neededEmergency,subEmail,time;
    private static double lat,log,latitude,longitude,km;
    private ProgressDialog mProgress1;
    private int hour,minute,second;
    LatLng currentLocation;
    HashMap<Double,String> hashMap;
    HashMap<String,LatLng> driverLatLong;
    LocationInfo locationInfo ;
    private String availed = "false";
    private TextView estimatedDistanceMap,estimatedTimeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkGPSStatus();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        estimatedDistanceMap = findViewById(R.id.distanceLocation);
        estimatedTimeMap = findViewById(R.id.estimatedTimeLocation);
        Calendar calendar = Calendar.getInstance();
         hour = calendar.get(Calendar.HOUR_OF_DAY);
         minute = calendar.get(Calendar.MINUTE);
         second = calendar.get(Calendar.SECOND);
//        int date = calendar.get(Calendar.DAY_OF_MONTH);
//        int month = calendar.get(Calendar.MONTH);
//        int year = calendar.get(Calendar.YEAR);;
        SimpleDateFormat format = new SimpleDateFormat();
        time = ""+calendar.getTime();

        Log.d(TAG, "onCreate: " + hour+"mm: "+minute+" ss: "+second);
        mProgress1 = new ProgressDialog(MapsActivity.this);

        hashMap = new HashMap<Double,String>();
        driverLatLong= new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        subEmail = mUser.getEmail();
        subEmail = subEmail.substring(0, subEmail.indexOf("."));

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Caller Data").child(subEmail).child("History").child(time);
        myRef4 = database.getReference("Active Case");

//         this is how to delete child from firebase
//         myRef2 = database.getReference("Accident recovery team ").child("jazz9999@gmail").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//             @Override
//             public void onComplete(@NonNull Task<Void> task) {
//                 if (task.isSuccessful()){
//
//                     Toast.makeText(MapsActivity.this,"completed"+task,Toast.LENGTH_LONG).show();
//                 }else {
//                     Toast.makeText(MapsActivity.this,"not available",Toast.LENGTH_LONG).show();
//                 }
//
//             }
//         });
        // this is how to delete child

        Intent intent = getIntent();
        availed = intent.getStringExtra("availed");
        if (availed.equals("true")){
            onStart();
        }else
        {
            // when not availed
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    lat = location.getLatitude();
                    log = location.getLongitude();
                    currentLocation = new LatLng(lat, log);

                    if (lat > 20.0 && log > 20.0) {
                        Intent intent = getIntent();
                        String accident1 = intent.getStringExtra("Accident");
                        neededEmergency = intent.getStringExtra("neededEmergency");

                        if (!accident1.isEmpty()) {
                            currentLocation = new LatLng(lat, log);

                            Callinfo callinfo = new Callinfo("" + lat, "" + log, "" + accident1, ""+hour, ""+minute, ""+second);
                            myRef.setValue(callinfo);
                            //user location
                            mMap.clear();
                           /// mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("YOU ARE HERE"));
                            //user marker

                            onStartt();
                            locationManager.removeUpdates(locationListener);

                        } else if (lat > 20 && log > 20) {

                            mMap.clear();
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here with no request"));
                            locationManager.removeUpdates(locationListener);

                            // Log.d(TAG, "empty ");
                        }
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }// when not availed

    // distance calculation
    private double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
// distance calculation
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng newLocation = new LatLng(31.177167,74.105169);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 13));
        //mMap.addMarker(new MarkerOptions().position(newLocation).title("YOU ARE HERE"));
        ///mMap.clear();
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                try {
                    locationManager.wait(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    protected void onStartt() {
        super.onStart();
        mProgress1.setMessage("Make sure location is on...");
        mProgress1.show();

        myRef1 = database.getReference(""+neededEmergency);
        myRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange1: "+dataSnapshot);

                if (dataSnapshot.exists()){

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        locationInfo = child.getValue(LocationInfo.class);
                        latitude = Double.parseDouble(locationInfo.getLat());
                        longitude = Double.parseDouble(locationInfo.getLog());
                        LatLng latLng = new LatLng(latitude,longitude);
                        String email = child.getKey();

                        double dis = distance(lat, log, latitude, longitude);
                        km = dis / 0.62137;
                        hashMap.put(km,email);
                        driverLatLong.put(email,latLng);
                    }
                    Double miniDistanceEmail = (Collections.min(hashMap.keySet()));
                    selectedDriver = hashMap.get(miniDistanceEmail);

                    myRef1.child(""+selectedDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                myRef1.child(""+selectedDriver).removeValue();
                                UserRequest userRequest = new UserRequest(""+lat,""+log,""+selectedDriver);
                                myRef4.child(subEmail).setValue(userRequest);
                                sendFCMPush();
                                UserRequest userRequest1 = new UserRequest(""+latitude,""+longitude,""+subEmail);
                                myRef4.child(selectedDriver).setValue(userRequest1);

                            }else {
                                onStartt();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onDatachanged deleted ");
                            onStartt();
                        }
                    });

                    Log.d(TAG, "onDataChange: selected dr "+selectedDriver);

                    LocationInfo driverInfo = new LocationInfo(""+latitude,""+longitude);
                    LocationInfo userInfo = new LocationInfo(""+lat,""+log);
                    myRef2 = database.getReference("ActiveDriver").child(selectedDriver);
                    myRef2.child("user request").child(subEmail).setValue(userInfo);

                    //myRef4.child(""+subEmail).setValue(""+selectedDriver);

                    onActive();

                }else {
                    Toast.makeText(MapsActivity.this,"Try later! There is no driver available..",Toast.LENGTH_LONG).show();
//                    onActive();
                    }
                mProgress1.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MapsActivity.this,"Error"+databaseError.toException(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void estTime(){
        if (km <= 1) {
            double time1 = km + 1;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        } else if (km > 1 || km <= 2) {
            double time1 = km + 2;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        } else if (km > 2 || km <= 4) {
            double time1 = km + 3;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        } else if (km > 4 || km <= 10) {
            double time1 = km + 5;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        } else if (km > 10) {
            double time1 = km + 6;
            estimatedTimeMap.setText(new DecimalFormat("##.##").format(time1) + " min");
        }
    }

    public void onActive(){
        myRef3 = database.getReference(neededEmergency+"fuck").child(""+selectedDriver);
        Log.d(TAG, "onDataChange: ref3 "+myRef3);
        myRef3.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: "+child);
                        //locationInfo = child.getValue(LocationInfo.class);
//                        latitude = Double.parseDouble(locationInfo.getLat());
//                        loitude = Double.parseDouble(locationInfo.getLog());
                    }

                    double dis = distance(lat, log, latitude, longitude);
                    km = dis / 0.62137;
                    estimatedDistanceMap.setText(new DecimalFormat("##.####").format(km) + " km");

                    estTime();
                    LatLng driverLoc = new LatLng(latitude,longitude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(driverLoc).title("driver is here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance_small)));
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("you are here"));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgress1.setMessage("please wait...");
        mProgress1.show();
        if (availed.equals("true")) {
            activeCase = database.getReference("Active Case").child(subEmail);

            activeCase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        UserRequest userRequest1 = dataSnapshot.getValue(UserRequest.class);

                        lat = Double.parseDouble(userRequest1.getLat());
                        log = Double.parseDouble(userRequest1.getLog());
                        currentLocation = new LatLng(lat, log);
                        selectedDriver = (String) userRequest1.getEmail();
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("you are here with active request"));
                        mProgress1.dismiss();
                        activeCase2 = database.getReference("Active Case").child(""+selectedDriver);

                        activeCase2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    UserRequest userRequest1 = dataSnapshot.getValue(UserRequest.class);
                                    userRequest1.getLog();
                                    latitude = Double.parseDouble(userRequest1.getLat());
                                    longitude = Double.parseDouble(userRequest1.getLog());
                                    LatLng driverLtLng = new LatLng(latitude,longitude);
                                    mMap.clear();
                                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("you are here with active request"));
                                    mMap.addMarker(new MarkerOptions().position(driverLtLng).title("driver is here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance_small)));

                                    getRouteToMarker(lat,log,latitude,longitude);
                                    double dis = distance(lat, log, latitude, longitude);
                                    km = dis / 0.62137;
                                    estimatedDistanceMap.setText(new DecimalFormat("##.####").format(km) + " km");
                                    estTime();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        Log.d(TAG, "onDataChange: not availed");
                        mProgress1.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            mProgress1.dismiss();
        }
    }

    private void checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if ( locationManager == null ) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex){}
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex){}
        if ( !gps_enabled && !network_enabled ){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setMessage("GPS not enabled");
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        }
    }

    private void sendFCMPush() {

        final String Legacy_SERVER_KEY = "AAAANP5gGHA:APA91bFwye7sitBprCkqgXENmgMhsSdudtRmB4u6yqObSbSUP90SOIMpEGsY24tnpkGH7p7QEvI8g6oJhO3vC6QAEo0ksMz8j9adOeckLM6egaws-rmcSaTdPmNHAHPTw04aX4AJp6yW";
        String msg = "you are selected for rescue service. Please go to your map to view your destination";
        String title = "Rescue request";
        String token = "fz4ZfeHDRHGMtVGzKYwr7A:APA91bGOOmrcM6saksXSv34ybsXz7OIW5tXYSaoP9AS0xeSAgsiU6gw3ThfPn1hTOzsAwUenbGX4tk6DFZFcCABj-WYJu0MrRgoCLyzgBjviXno9CNEEEaEl1x3I3azd92GiyEKBlnKm";

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
    // for draw route between two points from SIM coder

    private void getRouteToMarker(double lat2,double log2, double latitude2, double longitude2){
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(lat2, log2),new LatLng(latitude2,longitude2))
                .key("AIzaSyDpxqq5fXUcZSaH5SS_Luj2_uRpPxnNDP0")
                .build();
        routing.execute();
    }

    private List<Polyline> polylines;
//    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    //when alternative paths needed then following code
    private static final int[] COLORS = new int[]{R.color.design_default_color_primary_dark,R.color.colorYellow,R.color.design_default_color_primary_dark,R.color.design_default_color_primary_dark,R.color.primary_dark_material_light};
    // when alternaive paths


    @Override
    public void onRoutingFailure(com.directions.route.RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error:onRouting " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
//        if(polylines.size()>0) {
//            for (Polyline poly : polylines) {
//                poly.remove();
//            }
//        }

            polylines = new ArrayList<>();
            //add route(s) to the map.
            for (int i = 0; i <route.size(); i++) {

                //In case of more than 5 alternative routes
                int colorIndex = i % COLORS.length;

                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(getResources().getColor(COLORS[colorIndex]));
                polyOptions.width(10 + i * 3);
                polyOptions.addAll(route.get(i).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylines.add(polyline);
//                Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    public void onRoutingCancelled() {

    }


}

