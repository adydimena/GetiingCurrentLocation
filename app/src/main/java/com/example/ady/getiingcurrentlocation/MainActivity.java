package com.example.ady.getiingcurrentlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ady.getiingcurrentlocation.locationpojo.Locationgetter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private static final String TAG = "MainActivityTag";

    TextView tvCurrentLocation;

    TextView tvCurrentAddress;


    private FusedLocationProviderClient fusedLocationProviderClient;


    LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Location currentLocation;
    private List<String> zip;
    private EditText etzipinput;
    private Button btn;
    private Handler myhandler;
    private String address;
    private String lat;
    private String alt;
    private List<LocationCoord> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zip = new ArrayList<>();
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
        tvCurrentAddress = findViewById(R.id.tvCurrentAddress);






        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()){}
                   // Log.d(TAG, "onLocationResult: " + location.toString());

            }
        };
        checkPermission();
    }

    private void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            getLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Log.d(TAG, "onRequestPermissionsResult: denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "onSuccess: " + location.toString());
                        tvCurrentLocation.setText(location.getLatitude() + ":"
                                + location.getLongitude());
                        currentLocation = location;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



    }

    @SuppressLint("MissingPermission")
    private void startLocationRequests() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest
                , locationCallback, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationRequests();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationRequests();
    }

    private void stopLocationRequests() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }



    public void checkoutonmap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("magic", currentLocation);
        startActivity(intent);

    }

    public void enterZipcode(View view) {
        etzipinput = findViewById(R.id.zipcideinput);
        zip.add(etzipinput.getText().toString());
        Toast.makeText(MainActivity.this,"you entered "+ etzipinput.getText().toString(),Toast.LENGTH_LONG).show();
        etzipinput.setText("");
        if(zip.size() >= 4){
            btn = findViewById(R.id.enterZip);
            btn.setVisibility(View.INVISIBLE);
        }
    }

    public void mapdispaly(View view) {
        list = new ArrayList<>();

        myhandler = new Handler(Looper.getMainLooper());
        for (int i = 0; i <zip.size() ; i++) {
            RetrofitHelper.getmyzipLocation(zip.get(i))
                    .enqueue(new Callback<Locationgetter>() {
                        @Override
                        public void onResponse(Call<Locationgetter> call, final Response<Locationgetter> response) {
                            Log.d(TAG, "onResponse: RETROFIT"+response.body().getResults().get(0).getFormattedAddress().toString());
                            Log.d(TAG, "onResponse: size " +response.body());
                            myhandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    address = response.body().getResults().get(0).getFormattedAddress();
                                    lat= response.body().getResults().get(0).getGeometry().getLocation().getLat().toString();
                                    alt =response.body().getResults().get(0).getGeometry().getLocation().getLng().toString();

                                    list.add(new LocationCoord(address,lat,alt) );
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Locationgetter> call, Throwable t) {

                        }
                    });



            Log.d(TAG, "mapdispaly list : " + i + zip.get(i));


        }
        showClass();

        }

    private void showClass() {

            for (int j = 0; j <list.size() ; j++) {
                Log.d(TAG, "mapdispaly: list at "+ j + "address "+list.get(j).getAddress()
                        + "lat "+list.get(j).getLat()
                        + "alt "+list.get(j).getAtl()
                );
                Toast.makeText(MainActivity.this,"mapdispaly: list at "+ j + "address "+list.get(j).getAddress()
                        + "lat "+list.get(j).getLat()
                        + "alt "+list.get(j).getAtl(),Toast.LENGTH_LONG).show();

            }

    }

}
