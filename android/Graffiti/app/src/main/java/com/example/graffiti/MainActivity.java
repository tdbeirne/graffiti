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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    Button getLocationButton;
    TextView showLocationTxt;

    LocationManager locationManager;
    String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        showLocationTxt = findViewById(R.id.show_location);
        getLocationButton = findViewById(R.id.get_location);

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // Check if gps is enabled or not
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    enableGPS();
                } else {
                    getLocation();
                }

            }

        });
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

            if (LocationGPS != null) {
                double lat = LocationGPS.getLatitude();
                double longi = LocationGPS.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                showLocationTxt.setText("Your Location:"
                        +"\n"+"Latitude= "+latitude
                        +"\n"+"Longitude= "+longitude);
            } else if (LocationNetwork != null) {
                double lat = LocationNetwork.getLatitude();
                double longi = LocationNetwork.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                showLocationTxt.setText("Your Location:"
                        +"\n"+"Latitude= "+latitude
                        +"\n"+"Longitude= "+longitude);
            } else if (LocationPassive != null) {
                double lat = LocationPassive.getLatitude();
                double longi = LocationPassive.getLongitude();

                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                showLocationTxt.setText("Your Location:"
                        +"\n"+"Latitude= "+latitude
                        +"\n"+"Longitude= "+longitude);
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
