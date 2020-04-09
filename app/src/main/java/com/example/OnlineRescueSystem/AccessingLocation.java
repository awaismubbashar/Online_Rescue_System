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

public class AccessingLocation extends FragmentActivity implements OnMapReadyCallback  {

    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int Repeat_call = 101;
    DatabaseReference callerDatabase;
    public double logitude = 0;
    public double latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessing_location);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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

                    Toast.makeText(getApplicationContext(),mlocation.getLatitude()+" "+mlocation.getLongitude(),
                            Toast.LENGTH_LONG).show();
//                     finish();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(AccessingLocation.this);
                }else{
                    Toast.makeText(getApplicationContext(),"nothing ",
                            Toast.LENGTH_LONG).show();
                }

//                Intent changeIntent1 = new Intent(AccessingLocation.this, AccidentType.class);
//                    changeIntent1.putExtra("longitude",""+logitude);
//                    changeIntent1.putExtra("latitude",""+latitude);
//                AccessingLocation.this.startActivity(changeIntent1);

            }

        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(mlocation.getLatitude(),mlocation.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("KRK");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
        //googleMap.addMarker(markerOptions);
        googleMap.addMarker(new MarkerOptions().position(latLng).title("marker1"));

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
}
