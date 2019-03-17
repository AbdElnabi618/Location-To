package com.kh618.locationto;

import android.animation.TypeConverter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    Point point;
    ArrayList<Point> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        list = new ArrayList<>();
        GPSIsEnabled();
        SendData();
    }
    void SendData(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("points");

       /* myRef.setValue(new Point(0.1,0.1)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Home.this, "task done", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Home.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                  try {
                        point = d.getValue(Point.class);
                        list.add(point);
                        Log.e("Look", String.format("Value is: %s", point.toString()));

                    }catch (Exception ex){
                        Log.e("Error on Read", "onDataChange: ",ex );
                        Log.e("DataSnapShot", "data : "+ d);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Read Data Error", "Failed to read value.", error.toException());
            }
        });

    }
    public void OpenMap(View view){
        Intent intent = new Intent(Home.this,MapActivity.class);
        startActivity(intent);
    }
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

                            Toast.makeText(Home.this,
                                    "the app will not work because GPS is off", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setPositiveButton("go to setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // open setting to open GPS
                            Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gpsIntent);
                        }
                    }).create().show();

        }
    }

    public void OpenTrack(View view) {
        Intent i = new Intent(Home.this , Tracking.class);
        startActivity(i);
    }
}
