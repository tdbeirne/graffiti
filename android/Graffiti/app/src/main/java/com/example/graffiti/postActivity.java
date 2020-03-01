package com.example.graffiti;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class postActivity extends AppCompatActivity {
    ImageButton post_button_return;
    EditText input_message;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_layout);

        post_button_return  = findViewById(R.id.post_button_return);
        input_message = findViewById(R.id.text_input);

        post_button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = input_message.getText().toString().trim();
                // send information to database HERE

                openMainMenu();

            }

        });
    }

    public void openMainMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}



