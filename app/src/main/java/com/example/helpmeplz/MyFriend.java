package com.example.helpmeplz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MyFriend extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);

        ImageButton button_bellButton = (ImageButton) findViewById(R.id.bellImageButton);
        ImageButton button_moreButton = (ImageButton) findViewById(R.id.moreImageButton);

        button_bellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MyFriend.this, AddFriend.class);
                startActivity(myIntent);
                finish();
            }
        });

        button_moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MyFriend.this, FindFriend.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
