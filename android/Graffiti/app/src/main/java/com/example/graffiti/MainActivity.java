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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://minimus.xyz:5000");
        } catch (URISyntaxException e) {}
    }
    private static final int REQUEST_LOCATION = 1;


    FrameLayout mainScreen;
    ImageButton find_graffiti_button;
    ImageButton post_button;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainScreen = findViewById(R.id.mainScreen);
        find_graffiti_button = findViewById(R.id.find_graffiti_button);
        post_button = findViewById(R.id.post_button);

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        mSocket.on("show_posts", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject post = new JSONObject((String)args[0]);
                    String posts_string = post.getString("posts");
                    System.out.println(posts_string);
                    System.out.println(posts_string.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mSocket.connect();

        attemptSend();

        find_graffiti_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
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


    private JSONObject getLocation() {
        // Check for permissions again
        double lat = 0;
        double longi = 0;

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
        JSONObject locationObj = new JSONObject();
        try {
            locationObj.put("latitude", lat);
            locationObj.put("longitude", longi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return locationObj;
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

    private EditText mInputMessageView;

    private void attemptSend() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if gps is enabled or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableGPS();
        }

        mSocket.emit("location", getLocation());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }
}
