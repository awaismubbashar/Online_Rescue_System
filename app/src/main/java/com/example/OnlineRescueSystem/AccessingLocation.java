package com.example.OnlineRescueSystem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.core.Tag;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;

import java.io.IOException;

public class AccessingLocation extends FragmentActivity implements OnMapReadyCallback  {

    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Repeat_call = 101;
    DatabaseReference callerDatabase;
    public double logitude = 0;
    public double latitude = 0;
    private static final String TAG = "AccessingLocation";
    private static final String apiKey = "AIzaSyDsYcy8KYfunpmE4RClhpnVgSI3AppwMBs";

    private TextView estimatedDistance,estimatedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessing_location);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        estimatedDistance = findViewById(R.id.estimatedTimeAcc);
        estimatedDistance = findViewById(R.id.estimateddistanceAcc);

        GetLastLocation();
    }

    public void GetLastLocation(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Repeat_call);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){

                    mlocation = location;
                    latitude = mlocation.getLatitude();
                    logitude = mlocation.getLongitude();

                    LatLng origin = new LatLng(latitude,logitude);

// chk
// accurate distance measured by following method
                    double lat1 = 31.180263;
                    double log1 = 74.094517;

                    double mi = distance(latitude,logitude,lat1,log1);
                    double km =mi / 0.62137;
                    Log.d(TAG, "km: "+km);
                    Toast.makeText(AccessingLocation.this,"lat"+latitude,Toast.LENGTH_LONG).show();
// chk

                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(AccessingLocation.this);
                    Toast.makeText(getApplicationContext(),"Your location is",Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(getApplicationContext(),"your location service is off",
                            Toast.LENGTH_LONG).show();
                }
            }

        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(mlocation.getLatitude(),mlocation.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("KRK");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
        //googleMap.addMarker(markerOptions);
        googleMap.addMarker(new MarkerOptions().position(latLng).title("location"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Repeat_call:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    GetLastLocation();
                }
                break;
        }
    }

    //chk2
    // to get time
    public String getDurationForRoute(String origin, String destination) throws InterruptedException, ApiException, IOException {
    // - We need a context to access the API
    GeoApiContext geoApiContext = new GeoApiContext.Builder()
            .apiKey(apiKey)
            .build();

    // - Perform the actual request
    DirectionsResult directionsResult = DirectionsApi.newRequest(geoApiContext)
            .mode(TravelMode.DRIVING)
            .origin(origin)
            .destination(destination).await();

    // - Parse the result
    DirectionsRoute route = directionsResult.routes[0];
    DirectionsLeg leg = route.legs[0];
    Duration duration = leg.duration;
    return duration.humanReadable;
};
    //chk2


    //chk  (for measure distance  )
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

    //chk


}
