package com.kh618.locationto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

    // declaration variable
    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private boolean flag;
    LocationRequest locationRequest;
    private SupportMapFragment mapFragment;

    TextView tv_yourLocation, tv_goalPoint, tv_distance;
    Button getDistance;
    LatLng startPoint, endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // flag to zoom in my location when activity start
        flag = true;

        tv_distance = findViewById(R.id.tv_distance);
        tv_goalPoint = findViewById(R.id.tv_nextLocation);
        tv_yourLocation = findViewById(R.id.tv_yourLocation);
//        retrofit = new Retrofit.Builder().baseUrl(IApi.baseURL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        api= retrofit.create(IApi.class);
        getDistance = findViewById(R.id.btn_getDistance);

        // initialization end point
        endPoint = new LatLng(0.0, 0.0);

        // set map fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.fl_map);
        // check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    18);
        } else {

            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            } else {
                Toast.makeText(this, "Error 15 : map async not found", Toast.LENGTH_SHORT).show();
            }
            // check gps is enable or not
            GPSIsEnabled();
            // create google api client
            CreateGoogleApiClient();
        }

        getDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CalculationByDistance(startPoint, endPoint);
                    GetDirection(startPoint, endPoint);
                }catch (Exception e){
                    Toast.makeText(MapActivity.this, "error 18 : try open GPS or Accept location permissions \n " +
                            "if not work connect us" , Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    // api client method
    public void CreateGoogleApiClient() {
        if (apiClient == null)
            apiClient = new GoogleApiClient.Builder(this)
                    .addOnConnectionFailedListener(this).
                            addConnectionCallbacks(this).
                            addApi(LocationServices.API).
                            build();
    }

    //get direction method
    public void GetDirection(LatLng sPoint, LatLng ePoint) {
        GoogleDirection.withServerKey(getString(R.string.server_key))
                .from(sPoint)
                .to(ePoint)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Log.e("direction suc", direction.getStatus());
                            // draw red line between the two points
                            Leg leg = direction.getRouteList().get(0).getLegList().get(0);
                            ArrayList<LatLng> latLngArrayList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplicationContext(),
                                    latLngArrayList, 5, Color.RED);
                            // add lines to map and zoom in my location
                            mMap.addPolyline(polylineOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 18));
                        } else {
                            if(direction.getErrorMessage() != null)
                            Log.e("direction error", direction.getErrorMessage());
                            Toast.makeText(MapActivity.this, "can\'t get direction right now",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.e("direction filed", t.getMessage());
                        Toast.makeText(MapActivity.this, "Some thing went wrong please connect us to solve",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // when permissions accepted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 18: {
                if (ContextCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, permissions[1]) == PackageManager.PERMISSION_GRANTED
                        ) {
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(this);
                    } else {
                        Toast.makeText(this, "Error 15 : map async not found", Toast.LENGTH_SHORT).show();
                    }
                    // check gps is enable or not
                    GPSIsEnabled();
                    // create google api client
                    CreateGoogleApiClient();
                    // set location enable to get in map
                    if (mMap != null)
                        mMap.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(this, "the app will will not work if don\'t accept the permissions", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    // when map is ready method
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CreateGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // delete all marks in map
                mMap.clear();
                //change end point to the goal point
                endPoint = latLng;
                // initialization mark in the goal point
                MarkerOptions mark = new MarkerOptions().position(endPoint).title("Goal point");
                // change zoom to end point
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                // add the mark to end point
                mMap.addMarker(mark);
                Log.e("LocationChanged : lat an lang end point ", latLng.toString());
            }
        });

    }

    // calculate the distance between the points method
    public void CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);


        tv_yourLocation.setText(getString(R.string.yourLocation) + StartP.latitude + "," + StartP.longitude);
        tv_goalPoint.setText(getString(R.string.nextLocation) + EndP.latitude + "," + EndP.longitude);
        tv_distance.setText(getString(R.string.distance) + Radius * c + "Km \n" +
                getString(R.string.distance) + Radius * c * 1000 + "m");
        Log.e("LocationChanged", "in mater " + Radius * c * 1000
                + ">>>> in km :" + Radius * c);
    }

    // check gps method
    public void GPSIsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            // create dialog if gps disable ask to enable it
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS is disable ,enable it ?")
                    .setCancelable(false)
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            Toast.makeText(MapActivity.this,
                                    "the app will not work because GPS is off", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setPositiveButton("go to setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // open setting to open GPS
                            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gpsIntent);
                            if(mMap!= null && startPoint != null)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint,16));
                        }
                    }).create().show();

        }
    }

    // when client connect to server
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // initialization location request to use on change location method
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100)
                .setFastestInterval(1100).
                setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // call on location change method
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest,
                new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // get mt new location point
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        // check if location changed
                        if (!latLng.equals(startPoint)) {
                            // make my new location point is start point
                            startPoint = latLng;
                            //call calculate distance and get direction methods
                            CalculationByDistance(startPoint, endPoint);
                            GetDirection(startPoint, endPoint);
                            Log.e("LocationChanged", latLng.toString());
                        }
                        // check if flag equal to true this mean it's first time to call get location method
                        if (flag) {
                            //zoom to my location
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 18));
                            // set the flag false
                            flag = false;
                        }
                    }
                });
    }

    // connect to api client server when activity start
    @Override
    protected void onStart() {
        if(apiClient != null)
        apiClient.connect();
        super.onStart();
    }

    // disconnect to api client server when activity stop
    @Override
    protected void onStop() {
        if(apiClient != null)
        apiClient.disconnect();
        super.onStop();
    }

    // this methods are implements methods
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
