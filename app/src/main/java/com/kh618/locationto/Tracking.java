package com.kh618.locationto;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Tracking extends AppCompatActivity implements OnMapReadyCallback ,ValueEventListener{

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private final int ZOOM_DEGREE = 20;
    private Point currentUserPoint;
    private MarkerOptions markerOptions;
    private DatabaseReference currentPointRefrence;
    private FirebaseDatabase database;
    private Marker mark;
    private List<Point> points;
    private int position;

    DatabaseReference allPointsReference;
    private static final String TAG = "Tracking";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        points = new ArrayList<>();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.tracking_map);

        currentUserPoint = new Point(0.0, 0.0);
        markerOptions = new MarkerOptions()
                .position(currentUserPoint.toLatLng()).title("user location");

        database = FirebaseDatabase.getInstance();
        currentPointRefrence = database.getReference("Current point");
        allPointsReference = database.getReference("points");


        allPointsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot data:children) {
                    Point cure = data.getValue(Point.class);
                    points.add(cure);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (mapFragment != null) {
            mapFragment.getMapAsync(Tracking.this);
        } else {
            Toast.makeText(Tracking.this, "Error 15 : map async not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mark = mMap.addMarker(markerOptions);
        position =0;
        /*for(Point point : points) {
            ZoomAndMarkLocation(point);
        }*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ZoomAndMarkLocation(points.get(position));
                ++position;
                if(position >= points.size()){
                    Toast.makeText(Tracking.this, "User is stop here", Toast.LENGTH_SHORT).show();
                }else{
                    new Handler().postDelayed(this, 1000);
                }
            }
        }, 1500);
    }

    public void ZoomAndMarkLocation(Point currentUserPoint) {
        mark.setPosition(currentUserPoint.toLatLng());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentUserPoint.toLatLng(), ZOOM_DEGREE));
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        currentUserPoint = dataSnapshot.getValue(Point.class);

        if (mapFragment != null && currentUserPoint != null) {
           // ZoomAndMarkLocation(currentUserPoint);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e("Cancelled:", "event cancelled, Error:" + databaseError.getMessage());
    }
}
