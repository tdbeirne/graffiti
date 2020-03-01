package com.example.graffiti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    ImageButton find_graffiti_button  = findViewById(R.id.find_graffiti_button);
    ImageButton post_button  = findViewById(R.id.post_button);

    LocationManager locationManager;
    String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        find_graffiti_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // Check if gps is enabled or not
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    enableGPS();
                } else {
                    getLocation();
                }

                // Get nearby items and display as a list in main screen



            }

        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPostScreen();
            }
        });
    }

    private void openPostScreen() {
        Intent intent  = new Intent(this, PostScreen.class);
        startActivity(intent);
    }

    private void getLocation() {
        // Check for permissions again
        if (ActivityCompat. checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location LocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive= locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            double lat = 0;
            double longi = 0;

            if (LocationGPS != null) {
                lat = LocationGPS.getLatitude();
                longi = LocationGPS.getLongitude();
            } else if (LocationNetwork != null) {
                lat = LocationNetwork.getLatitude();
                longi = LocationNetwork.getLongitude();

            } else if (LocationPassive != null) {
                lat = LocationPassive.getLatitude();
                longi = LocationPassive.getLongitude();
            } else {
                Toast.makeText(this, "Can't get your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
