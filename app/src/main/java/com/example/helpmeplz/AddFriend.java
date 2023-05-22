package com.example.helpmeplz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AddFriend extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        Button button_Accept = findViewById(R.id.button_accept);
        Button button_Refuse = findViewById(R.id.button_refuse);

        button_Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AddFriend.this, CheckFriend.class);
                startActivity(myIntent);
                finish();
            }
        });
        button_Refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(AddFriend.this, MyFriend.class);
                startActivity(myIntent);
                finish();
            }
        });

    }
}
