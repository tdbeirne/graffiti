package com.example.graffiti;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;


public class PostScreen extends AppCompatActivity {
    private ImageButton post_graffiti_button = findViewById(R.id.post_button_return);
    private EditText text_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        post_graffiti_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOriginalMenu();
            }
        });
    }

    private void openOriginalMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}

/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_screen);

        Intent task = getIntent();
        text_input = findViewById(R.id.text_input);

        post_graffiti_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic for pushing user submission to database


                openOriginalMenu();
            }
        });

    }
}*/


