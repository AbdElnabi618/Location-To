package com.kh618.locationto;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        GPSIsEnabled();
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
}
