package com.example.OnlineRescueSystem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.OnlineRescueSystem.Model.Callinfo;
import com.example.OnlineRescueSystem.Model.LocationInfo;
import com.example.OnlineRescueSystem.Model.Registration;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference myRef;
    private DatabaseReference myRef1;
    private DatabaseReference myRef2;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private static final String TAG = "MapsActivity";
    String selectedDriver,neededEmergency;
    private double lat,log,latitude,longitude,km;
    private ProgressDialog mProgress1;
    String subEmail;

    HashMap<Double,String> hashMap;
    HashMap<String,LatLng> driverLatLong;
    LocationInfo locationInfo ;

    private TextView estimatedDistanceMap,estimatedTimeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        estimatedDistanceMap = findViewById(R.id.distanceLocation);
        estimatedTimeMap = findViewById(R.id.estimatedTimeLocation);

        mProgress1 = new ProgressDialog(MapsActivity.this);

        hashMap = new HashMap<Double,String>();
        driverLatLong= new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        subEmail = mUser.getEmail();
        subEmail = subEmail.substring(0, subEmail.indexOf("."));
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Caller Data").child(subEmail).child("quaerty");


        // this is how to delete child from firebase
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

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat = location.getLatitude();
                log = location.getLongitude();
                LatLng currentLocation = new LatLng(lat, log);

                if (lat > 20.0 && log > 20.0) {

                    Intent intent = getIntent();
                    String accident1 = intent.getStringExtra("Accident");
                    neededEmergency = intent.getStringExtra("neededEmergency");
                    Log.d(TAG, "accident tpe: " + accident1);


                    if (!accident1.isEmpty()) {

                        String time = String.valueOf(java.lang.System.currentTimeMillis());
                        Callinfo callinfo1 = new Callinfo("" + lat, "" + log, "" + accident1, "" + time);

                        double lat1 = 31.180263;
                        double log1 = 74.094517;


                        //user location
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("you are here"));
                        onStartt();
                        //user marker

                        locationManager.removeUpdates(locationListener);

                    } else if (lat > 20 && log > 20) {
                        Log.d(TAG, "acci else2 ");
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("you are here with no request"));
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
        mMap.clear();
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                try {
                    locationManager.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

    }

    protected void onStartt() {
        super.onStart();
        mProgress1.setMessage("selecting driver please wait...");
        mProgress1.show();



        myRef1 = database.getReference(""+neededEmergency);
        myRef1.addValueEventListener(new ValueEventListener() {
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

                   LocationInfo driverInfo = new LocationInfo(""+latitude,""+longitude);
                   LocationInfo userInfo = new LocationInfo(""+lat,""+log);
                   myRef2 = database.getReference(neededEmergency).child(selectedDriver);

                   //myRef2.child(selectedDriver).setValue(driverInfo);
                   myRef2.child(subEmail).setValue(userInfo);

                    LatLng driverLocation = driverLatLong.get(selectedDriver);
                    Log.d(TAG, "onLocationChanged6: "+driverLocation);

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(driverLocation));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLocation, 14));
                    mMap.addMarker(new MarkerOptions().position(driverLocation).title("driver is here"));
                    estimatedDistanceMap.setText(new DecimalFormat("##.####").format(km) + " km");
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

                }else {
                    Toast.makeText(MapsActivity.this,"Try later! There is no driver available..",Toast.LENGTH_LONG).show();
                }
                mProgress1.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(MapsActivity.this,"Error"+databaseError.toException(),Toast.LENGTH_LONG).show();
            }
        });
    }

}

