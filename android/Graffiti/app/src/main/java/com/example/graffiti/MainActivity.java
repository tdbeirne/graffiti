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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.github.nkzawa.socketio.client.Socket;

public class MainActivity extends AppCompatActivity {
    String message;
    private Socket mSocket;

    private static final int REQUEST_LOCATION = 1;


    FrameLayout mainScreen = findViewById(R.id.mainScreen);
    ImageButton find_graffiti_button  = findViewById(R.id.find_graffiti_button);
    ImageButton post_button  = findViewById(R.id.post_button);
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        Graffiti app = (Graffiti)getApplication();
        mSocket = app.getmSocket();

        find_graffiti_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // Check if gps is enabled or not
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    enableGPS();
                }
                // change get Location to return a dict
                // Dict location = getLocation();

                // emit the dict to server
                /* mSocket.emit("location", location).on("show_posts", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject data = (JSONObject)args[0];
                        //here the data is in JSON Format
                        //Toast.makeText(MainActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                    }
                });*/

            }

        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPostActivity();
            }
        });


    }

    public void openPostActivity() {
        Intent intent = new Intent(this, postActivity.class);
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
