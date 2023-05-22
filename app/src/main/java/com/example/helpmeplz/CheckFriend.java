package com.example.helpmeplz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CheckFriend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_friend);

        Button button_Accept = findViewById(R.id.button_next2);

        button_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(CheckFriend.this, MyFriend.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
