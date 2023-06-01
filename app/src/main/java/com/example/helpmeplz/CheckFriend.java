package com.example.helpmeplz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheckFriend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "ValueEventListener - onDataChange : " + 1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_friend);
        Log.d("MainActivity", "ValueEventListener - onDataChange : " + 2);

        Button button_Accept2 = findViewById(R.id.button_next2);
        Log.d("MainActivity", "ValueEventListener - onDataChange : " + 3);

        button_Accept2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("MainActivity", "ValueEventListener - onDataChange : " + 5);
                Intent myIntent5 = new Intent(CheckFriend.this, MyFriend.class);
                startActivity(myIntent5);
                finish();
            }
        });
        Log.d("MainActivity", "ValueEventListener - onDataChange : " + 4);
    }
}
